package org.jenkinsci.testinprogress.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jenkinsci.testinprogress.server.events.TestEventsReceiver;
import org.jenkinsci.testinprogress.server.events.build.BuildTestEventsGenerator;
import org.jenkinsci.testinprogress.server.events.build.IBuildTestEventListener;
import org.jenkinsci.testinprogress.server.events.build.TestRunIds;
import org.jenkinsci.testinprogress.server.events.run.IRunTestEventListener;
import org.jenkinsci.testinprogress.server.filters.StackTraceFilter;
import org.jenkinsci.testinprogress.server.listeners.BuildTestStats;
import org.jenkinsci.testinprogress.server.listeners.RunningBuildTestEvents;
import org.jenkinsci.testinprogress.server.listeners.SaveTestEventsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuildTestEventsServer {
	private ExecutorService executorService;
	private final int port;
	private final StackTraceFilter stackTraceFilter;
	private final TestRunIds testRunIds;
	private final IBuildTestEventListener[] listeners;
	private ServerSocket serverSocket;
	private static Logger logger = LoggerFactory
			.getLogger(BuildTestEventsServer.class);

	public BuildTestEventsServer(int port, StackTraceFilter stackTraceFilter,
			IBuildTestEventListener[] listeners) {
		this.port = port;
		this.stackTraceFilter = stackTraceFilter;
		this.testRunIds = new TestRunIds();
		this.listeners = listeners;
	}

	public void start() throws IOException {
		executorService = Executors.newCachedThreadPool();
		serverSocket = new ServerSocket(port);
		executorService.submit(new Runnable() {

			public void run() {
				handleConnections();
			}
		});
	}

	public void stop() throws IOException {
		executorService.shutdown();
		serverSocket.close();
	}

	private void handleConnections() {
		try {
			while (true) {
				final Socket socket = serverSocket.accept();
				executorService.submit(new Runnable() {

					public void run() {
						handleTestRun(socket);
					}

				});
			}
		} catch (IOException e) {
			if (!executorService.isShutdown()) {
				logger.error("Error occured while listening for connections", e);
			}
		} finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
			}
		}
	}

	private void handleTestRun(Socket socket) {
		try {
			BuildTestEventsGenerator buildTestEventsGenerator = new BuildTestEventsGenerator(
					testRunIds, listeners);
			IRunTestEventListener[] runTestEventlisteners = new IRunTestEventListener[] { buildTestEventsGenerator };
			TestEventsReceiver testEventsReceiver = new TestEventsReceiver(
					socket.getInputStream(), stackTraceFilter,
					runTestEventlisteners);
			testEventsReceiver.run();
		} catch (IOException e) {
			logger.error("Error occured while handling test run", e);
		}
	}

	public static void main(String[] args) throws IOException {
		File unitsEventsDir = new File("unitsEvents");
		unitsEventsDir.mkdir();
		RunningBuildTestEvents runningBuildTestEvents = new RunningBuildTestEvents();
		SaveTestEventsListener saveTestEventsListener = new SaveTestEventsListener(
				unitsEventsDir);
		BuildTestStats buildTestStats = new BuildTestStats();
		StackTraceFilter stackTraceFilter = new StackTraceFilter();
		BuildTestEventsServer server = new BuildTestEventsServer(6061,
				stackTraceFilter, new IBuildTestEventListener[] {
						runningBuildTestEvents, saveTestEventsListener,
						buildTestStats });
		server.start();
	}

}
