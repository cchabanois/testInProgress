package org.jenkinsci.plugins.testinprogress;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.remoting.Channel;
import hudson.remoting.RemoteOutputStream;
import hudson.remoting.forward.Forwarder;
import hudson.remoting.forward.ListeningPort;
import hudson.remoting.forward.PortForwarder;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Serializable;
import java.util.Map;

import org.jenkinsci.plugins.testinprogress.events.ITestEventListener;
import org.jenkinsci.plugins.testinprogress.filters.StackTraceFilter;
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
	private static final String UNIT_EVENTS_FILENAME = "unit.events";

	@DataBoundConstructor
	public TestInProgressBuildWrapper() {
	}

	@Override
	public Environment setUp(AbstractBuild build, Launcher launcher,
			BuildListener listener) throws IOException, InterruptedException {
		RunningBuildTestEvents runningBuildTestEvents = new RunningBuildTestEvents();
		final SaveTestEventsListener saveTestEventsListener = new SaveTestEventsListener(
				new File(build.getRootDir(), UNIT_EVENTS_FILENAME));
		final ListeningPort listeningPort = PortForwarder.create(launcher
				.getChannel(), 0, new ForwarderImpl(saveTestEventsListener,
				runningBuildTestEvents));
		final TestInProgressRunAction testInProgressRunAction = new TestInProgressRunAction(
				build, runningBuildTestEvents);
		build.addAction(new TestInProgressRunAction(build,
				runningBuildTestEvents));
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
				testInProgressRunAction.onBuildComplete();
				listeningPort.close();
				return true;
			}

		};
	}

	@Override
	public Descriptor<BuildWrapper> getDescriptor() {
		return DESCRIPTOR;
	}

	@Extension
	public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

	public static final class DescriptorImpl extends BuildWrapperDescriptor
			implements Serializable {

		private static final long serialVersionUID = -9114594938706005233L;

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
		private final ITestEventListener[] listeners;

		public ForwarderImpl(ITestEventListener... listeners) {
			this.listeners = listeners;
		}

		public OutputStream connect(OutputStream out) throws IOException {
			PipedOutputStream pipedOutputStream = new PipedOutputStream();
			PipedInputStream pipedInputStream = new PipedInputStream();
			pipedOutputStream.connect(pipedInputStream);
			new TestEventsReceiverThread("Test events receiver",
					pipedInputStream, new StackTraceFilter(), listeners)
					.start();
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
