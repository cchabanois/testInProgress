package org.jenkinsci.testinprogress.httpserver;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

import org.jenkinsci.testinprogress.server.BuildTestEventsServer;
import org.jenkinsci.testinprogress.server.build.BuildTestResults;
import org.jenkinsci.testinprogress.server.events.build.IBuildTestEventListener;
import org.jenkinsci.testinprogress.server.events.build.TestRunIds;
import org.jenkinsci.testinprogress.server.filters.StackTraceFilter;
import org.jenkinsci.testinprogress.server.listeners.BuildTestStats;
import org.jenkinsci.testinprogress.server.listeners.RunningBuildTestEvents;
import org.jenkinsci.testinprogress.server.listeners.SaveTestEventsListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.JUnitCore;

import tests.ProgressParameterizedTestSuite;

public class TestInProgressHttpServerTest {
	private TestRunIds testRunIds = new TestRunIds();
	private RunningBuildTestEvents runningBuildTestEvents = new RunningBuildTestEvents();
	private BuildTestStats buildTestStats = new BuildTestStats();
	private BuildTestEventsServer buildTestEventsServer; 
	private TestInProgressHttpServer testInProgressHttpServer;
	
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	
	@Before
	public void setUp() throws Exception {
		System.setProperty("TEST_IN_PROGRESS_PORT", Integer.toString(6061));
		File testEventsDir = tempFolder.newFolder();
		testEventsDir.mkdirs();
		buildTestEventsServer = createBuildTestEventsServer(testEventsDir);
		BuildTestResults buildTestResults = new BuildTestResults(testEventsDir, testRunIds, runningBuildTestEvents, buildTestStats);
		testInProgressHttpServer = createTestInProgressHttpServer(buildTestResults);
		buildTestEventsServer.start();
		testInProgressHttpServer.start();
	}
	
	@After
	public void tearDown() throws Exception {
		System.setProperty("TEST_IN_PROGRESS_PORT", "");
		buildTestEventsServer.stop();
		testInProgressHttpServer.stop();
	}

	@Test
	public void testParameterizedTests() {
		runTests(ProgressParameterizedTestSuite.class);		
	}
	
	public void runTests(Class<?> classes) {
		JUnitCore jUnitCore = new JUnitCore();
		jUnitCore.run(classes);
	
	}

	private BuildTestEventsServer createBuildTestEventsServer(File testEventsDir) {
		SaveTestEventsListener saveTestEventsListener = new SaveTestEventsListener(
				testEventsDir);
		StackTraceFilter stackTraceFilter = new StackTraceFilter();
		BuildTestEventsServer buildTestEventsServer = new BuildTestEventsServer(6061,
				stackTraceFilter, new IBuildTestEventListener[] {
						runningBuildTestEvents, saveTestEventsListener,
						buildTestStats });
		return buildTestEventsServer;
	}
	
	private TestInProgressHttpServer createTestInProgressHttpServer(BuildTestResults buildTestResults) {
		TestInProgressHttpServer httpServer = new TestInProgressHttpServer(
				"http://localhost:8080/", buildTestResults);
		return httpServer;
	}
	
	
	private int findFreePort() {
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(0);
			return socket.getLocalPort();
		} catch (IOException e) {
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
		return -1;
	}
	
}
