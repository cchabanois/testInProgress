package org.jenkinsci.testinprogress.httpserver;

import java.io.File;

import org.jenkinsci.testinprogress.TestInProgressServers;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.JUnitCore;

import tests.ProgressDynamicTestSuite;
import tests.ProgressParameterizedTestSuite;

public class TestInProgressHttpServerTest {
	private TestInProgressServers testInProgressServers;
	
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Before
	public void setUp() throws Exception {
		File testEventsDir = tempFolder.newFolder();
		testEventsDir.mkdirs();
		testInProgressServers = new TestInProgressServers(testEventsDir, 0, 0);
		testInProgressServers.start();
		System.setProperty("TEST_IN_PROGRESS_PORT",
				Integer.toString(testInProgressServers.getBuildTestEventsServerPort()));
	}

	@After
	public void tearDown() throws Exception {
		System.setProperty("TEST_IN_PROGRESS_PORT", "");
		testInProgressServers.stop();
	}

	@Test
	@Ignore
	public void testParameterizedTests() {
		runJunitTests(ProgressParameterizedTestSuite.class);
	}

	@Test
	@Ignore
	public void testDynamicTestSuite() {
		runJunitTests(ProgressDynamicTestSuite.class);
	}
	
	public void runJunitTests(Class<?> classes) {
		JUnitCore jUnitCore = new JUnitCore();
		jUnitCore.run(classes);

	}
	
}
