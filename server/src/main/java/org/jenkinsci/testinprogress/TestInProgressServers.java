package org.jenkinsci.testinprogress;

import java.io.File;

import org.jenkinsci.testinprogress.httpserver.TestInProgressHttpServer;
import org.jenkinsci.testinprogress.server.BuildTestEventsServer;
import org.jenkinsci.testinprogress.server.build.BuildTestResults;
import org.jenkinsci.testinprogress.server.build.CompletedBuildTestEvents;
import org.jenkinsci.testinprogress.server.events.build.IBuildTestEventListener;
import org.jenkinsci.testinprogress.server.events.build.TestRunIds;
import org.jenkinsci.testinprogress.server.filters.StackTraceFilter;
import org.jenkinsci.testinprogress.server.listeners.BuildTestStats;
import org.jenkinsci.testinprogress.server.listeners.RunningBuildTestEvents;
import org.jenkinsci.testinprogress.server.listeners.SaveTestEventsListener;
import org.jenkinsci.testinprogress.server.persistence.TestEventsDirs;
import org.jenkinsci.testinprogress.utils.FreePortsFinder;

/**
 * Create both the build test events server and the test in progress http server
 * 
 * @author cchabanois
 *
 */
public class TestInProgressServers {
	private final TestEventsDirs testEventsDirs;
	private final TestRunIds testRunIds = new TestRunIds();
	private final RunningBuildTestEvents runningBuildTestEvents = new RunningBuildTestEvents();
	private final BuildTestStats buildTestStats = new BuildTestStats();
	private BuildTestEventsServer buildTestEventsServer;
	private TestInProgressHttpServer testInProgressHttpServer;
	private int buildTestEventsServerPort;
	private int httpPort;

	/**
	 * 
	 * @param testEventsDir
	 * @param buildTestEventsServerPort
	 *            the port for the build test events server or 0 for any free
	 *            port
	 * @param httpPort
	 *            the port for the http server or 0 for any free port
	 */
	public TestInProgressServers(File testEventsRootDir,
			int buildTestEventsServerPort, int httpPort) {
		this.testEventsDirs = new TestEventsDirs(testEventsRootDir);
		this.buildTestEventsServerPort = buildTestEventsServerPort;
		this.httpPort = httpPort;
	}

	public void start() throws Exception {
		if (httpPort == 0) {
			this.httpPort = FreePortsFinder.findFreePort();
		}
		File previousBuildTestEventsDir = testEventsDirs.getLatestBuildTestEventsDir();
		File buildTestEventsDir = testEventsDirs.createBuildTestEventsDir();
		BuildTestResults buildTestResults = new BuildTestResults(buildTestEventsDir,
				testRunIds, runningBuildTestEvents, buildTestStats);
		CompletedBuildTestEvents completedBuildTestEvents = null;
		if (previousBuildTestEventsDir != null) {
			completedBuildTestEvents = new CompletedBuildTestEvents(previousBuildTestEventsDir);
		}
		testInProgressHttpServer = createTestInProgressHttpServer(httpPort, completedBuildTestEvents, buildTestResults);
		testInProgressHttpServer.start();
		if (buildTestEventsServerPort == 0) {
			this.buildTestEventsServerPort = FreePortsFinder.findFreePort();
		}
		buildTestEventsServer = createBuildTestEventsServer(buildTestEventsServerPort, buildTestEventsDir);
		buildTestEventsServer.start();
	}

	public int getHttpPort() {
		return httpPort;
	}

	public int getBuildTestEventsServerPort() {
		return buildTestEventsServerPort;
	}

	public void stop() throws Exception {
		buildTestEventsServer.stop();
		testInProgressHttpServer.stop();
	}

	private BuildTestEventsServer createBuildTestEventsServer(int buildTestEventsServerPort, File testEventsDir) {
		SaveTestEventsListener saveTestEventsListener = new SaveTestEventsListener(
				testEventsDir);
		StackTraceFilter stackTraceFilter = new StackTraceFilter();
		BuildTestEventsServer buildTestEventsServer = new BuildTestEventsServer(
				buildTestEventsServerPort, stackTraceFilter,
				new IBuildTestEventListener[] { runningBuildTestEvents,
						saveTestEventsListener, buildTestStats });
		return buildTestEventsServer;
	}

	private TestInProgressHttpServer createTestInProgressHttpServer(int httpPort, CompletedBuildTestEvents previousBuildTestEvents,
			BuildTestResults buildTestResults) {
		String baseUri = "http://localhost:" + Integer.toString(httpPort) + '/';
		System.out.println("Test In progress page : " + baseUri
				+ "testinprogress/index.html");
		TestInProgressHttpServer httpServer = new TestInProgressHttpServer(
				baseUri, previousBuildTestEvents, buildTestResults);
		return httpServer;
	}

}
