package org.jenkinsci.plugins.testinprogress;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.remoting.Callable;
import hudson.remoting.Channel;
import hudson.remoting.RemoteOutputStream;
import hudson.remoting.VirtualChannel;
import hudson.remoting.forward.Forwarder;
import hudson.remoting.forward.ListeningPort;
import hudson.remoting.forward.PortForwarder;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.Pipe;
import java.util.Map;

import org.jenkinsci.testinprogress.server.build.BuildTestResults;
import org.jenkinsci.testinprogress.server.events.TestEventsReceiver;
import org.jenkinsci.testinprogress.server.events.build.BuildTestEventsGenerator;
import org.jenkinsci.testinprogress.server.events.build.IBuildTestEventListener;
import org.jenkinsci.testinprogress.server.events.build.TestRunIds;
import org.jenkinsci.testinprogress.server.events.run.IRunTestEventListener;
import org.jenkinsci.testinprogress.server.filters.StackTraceFilter;
import org.jenkinsci.testinprogress.server.listeners.BuildTestStats;
import org.jenkinsci.testinprogress.server.listeners.RunningBuildTestEvents;
import org.jenkinsci.testinprogress.server.listeners.SaveTestEventsListener;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * This build wrapper communicates to the process being started the port to use
 * to report test progress (using a env var). It also forward test messages from
 * slave to the master and process them.
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public class TestInProgressBuildWrapper extends BuildWrapper {
	private static final String UNIT_EVENTS_DIR = "unitevents";

	@DataBoundConstructor
	public TestInProgressBuildWrapper() {
	}

	@Override
	public Environment setUp(AbstractBuild build, Launcher launcher,
			BuildListener listener) throws IOException, InterruptedException {
		TestRunIds testRunIds = new TestRunIds();
		RunningBuildTestEvents runningBuildTestEvents = new RunningBuildTestEvents();
		final SaveTestEventsListener saveTestEventsListener = new SaveTestEventsListener(
				new File(build.getRootDir(), UNIT_EVENTS_DIR));
		BuildTestStats buildTestStats = new BuildTestStats();
		final ListeningPort listeningPort = createPortForwarder(
				launcher.getChannel(), 0, new ForwarderImpl(testRunIds,
						saveTestEventsListener, runningBuildTestEvents,
						buildTestStats));
		final BuildTestResults testEvents = new BuildTestResults(new File(
				build.getRootDir(), UNIT_EVENTS_DIR), testRunIds,
				runningBuildTestEvents, buildTestStats);
		TestInProgressRunAction testInProgressRunAction = new TestInProgressRunAction(
				build, testEvents);
		build.addAction(testInProgressRunAction);
		saveTestEventsListener.init();
		return new Environment() {

			@Override
			public void buildEnvVars(Map<String, String> env) {
				env.put("TEST_IN_PROGRESS_PORT",
						String.valueOf(listeningPort.getPort()));
			}

			@Override
			public boolean tearDown(AbstractBuild build, BuildListener listener)
					throws IOException, InterruptedException {
				saveTestEventsListener.destroy();
				testEvents.onBuildComplete();
				listeningPort.close();
				return true;
			}

		};
	}

	/**
	 * Same than PortForwarder.create but also works when build is running on
	 * master
	 * 
	 * @see PortForwarder#create(VirtualChannel, int, Forwarder)
	 */
	public static ListeningPort createPortForwarder(VirtualChannel ch,
			final int acceptingPort, Forwarder forwarder) throws IOException,
			InterruptedException {
		// need a remotable reference
		final Forwarder proxy = ch.export(Forwarder.class, forwarder);

		return ch.call(new Callable<ListeningPort, IOException>() {
			public ListeningPort call() throws IOException {
				PortForwarder t = new PortForwarder(acceptingPort, proxy);
				t.start();
				if (Channel.current() != null) {
					// running on slave
					return Channel.current().export(ListeningPort.class, t);
				} else {
					// running on master
					return t;
				}
			}
		});
	}

	@Override
	public DescriptorImpl getDescriptor() {
		return DESCRIPTOR;
	}

	@Extension
	public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();
	
	public static final class DescriptorImpl extends BuildWrapperDescriptor {

		public DescriptorImpl() {
			super(TestInProgressBuildWrapper.class);
			load();
		}

		@Override
		public boolean isApplicable(AbstractProject<?, ?> item) {
			return true;
		}

		@Override
		public String getDisplayName() {
			return "Show tests in progress";
		}

	}

	/**
	 * Forward from slave to master
	 * 
	 */
	private static class ForwarderImpl implements Forwarder {
		private final IBuildTestEventListener[] listeners;
		private final TestRunIds testRunIds;

		public ForwarderImpl(TestRunIds testRunIds,
				IBuildTestEventListener... listeners) {
			this.testRunIds = testRunIds;
			this.listeners = listeners;
		}

		public OutputStream connect(OutputStream out) throws IOException {
			Pipe pipe = Pipe.open();
			Pipe.SinkChannel sinkChannel = pipe.sink();
			Pipe.SourceChannel sourceChannel = pipe.source();
			OutputStream pipedOutputStream = Channels.newOutputStream(sinkChannel);
			InputStream pipedInputStream = Channels.newInputStream(sourceChannel);
			
			BuildTestEventsGenerator buildTestEventsGenerator = new BuildTestEventsGenerator(
					testRunIds, listeners); 
			Runnable runnable = new TestEventsReceiver(
					pipedInputStream, new StackTraceFilter(),
					new IRunTestEventListener[] { buildTestEventsGenerator });
			Thread thread = new Thread(runnable);
			thread.setName("Test events receiver");
			thread.start();
			return new RemoteOutputStream(pipedOutputStream);
		}

		/**
		 * When sent to the remote node, send a proxy.
		 */
		private Object writeReplace() {
			return Channel.current().export(Forwarder.class, this);
		}
	}

}
