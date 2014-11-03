package org.jenkinsci.testinprogress.httpserver.utils;

import java.io.File;
import java.util.List;

import org.jenkinsci.testinprogress.httpserver.TestInProgressHttpServer;
import org.jenkinsci.testinprogress.server.BuildTestEventsServer;
import org.jenkinsci.testinprogress.server.build.BuildTestResults;
import org.jenkinsci.testinprogress.server.events.build.IBuildTestEventListener;
import org.jenkinsci.testinprogress.server.events.build.TestRunIds;
import org.jenkinsci.testinprogress.server.filters.StackTraceFilter;
import org.jenkinsci.testinprogress.server.listeners.BuildTestStats;
import org.jenkinsci.testinprogress.server.listeners.RunningBuildTestEvents;
import org.jenkinsci.testinprogress.server.listeners.SaveTestEventsListener;

public class TestInProgressServers {
	private final File testEventsDir;
	private final TestRunIds testRunIds = new TestRunIds();
	private final RunningBuildTestEvents runningBuildTestEvents = new RunningBuildTestEvents();
	private final BuildTestStats buildTestStats = new BuildTestStats();
	private BuildTestEventsServer buildTestEventsServer;
	private TestInProgressHttpServer testInProgressHttpServer;
	private int buildTestEventsServerPort;
	private int httpPort;

	public TestInProgressServers(File testEventsDir) {
		this.testEventsDir = testEventsDir;
	}
	
	public void start() throws Exception {
		List<Integer> ports = FreePortsFinder.findFreePorts(2);
		this.buildTestEventsServerPort = ports.get(0);
		this.httpPort = ports.get(1);
		System.out.println("Http server port : "+httpPort);
		buildTestEventsServer = createBuildTestEventsServer(testEventsDir);
		BuildTestResults buildTestResults = new BuildTestResults(testEventsDir,
				testRunIds, runningBuildTestEvents, buildTestStats);
		testInProgressHttpServer = createTestInProgressHttpServer(buildTestResults);
		buildTestEventsServer.start();
		testInProgressHttpServer.start();
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

	private BuildTestEventsServer createBuildTestEventsServer(File testEventsDir) {
		SaveTestEventsListener saveTestEventsListener = new SaveTestEventsListener(
				testEventsDir);
		StackTraceFilter stackTraceFilter = new StackTraceFilter();
		BuildTestEventsServer buildTestEventsServer = new BuildTestEventsServer(
				buildTestEventsServerPort, stackTraceFilter,
				new IBuildTestEventListener[] { runningBuildTestEvents,
						saveTestEventsListener, buildTestStats });
		return buildTestEventsServer;
	}

	private TestInProgressHttpServer createTestInProgressHttpServer(
			BuildTestResults buildTestResults) {
		String baseUri = "http://localhost:" + Integer.toString(httpPort) + '/';
		System.out.println("Test In progress page : "+baseUri+"testinprogress/index.html");
		TestInProgressHttpServer httpServer = new TestInProgressHttpServer(
				baseUri, buildTestResults);
		return httpServer;
	}	
	
	
}
