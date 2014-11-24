package org.jenkinsci.testinprogress.server.build;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jenkinsci.testinprogress.server.build.BuildTestResults;
import org.jenkinsci.testinprogress.server.events.build.BuildTestEvent;
import org.jenkinsci.testinprogress.server.events.build.TestRunIds;
import org.jenkinsci.testinprogress.server.events.run.RunStartEvent;
import org.jenkinsci.testinprogress.server.listeners.BuildTestStats;
import org.jenkinsci.testinprogress.server.listeners.RunningBuildTestEvents;
import org.jenkinsci.testinprogress.server.listeners.SaveTestEventsListener;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class BuildTestResultsTest {
	private File persistenceRootFile;
	private TestRunIds testRunIds = new TestRunIds();
	private SaveTestEventsListener saveTestEventsListener;
	private RunningBuildTestEvents runningBuildTestEvents = new RunningBuildTestEvents();
	private BuildTestStats buildTestStats = new BuildTestStats();

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	
	@Before
	public void setUp() throws IOException {
		persistenceRootFile = tempFolder.newFolder();
		saveTestEventsListener = new SaveTestEventsListener(persistenceRootFile);
		saveTestEventsListener.init();
	}

	@Test
	public void testGetEventsWhenBuildIsRunning() {
		// Given
		BuildTestResults testEvents = new BuildTestResults(persistenceRootFile,
				testRunIds, runningBuildTestEvents, buildTestStats);
		event(new BuildTestEvent("run1", new RunStartEvent(1000)));
		event(new BuildTestEvent("run2", new RunStartEvent(2000)));

		// When
		List<BuildTestEvent> events = testEvents.getEvents();

		// Then
		assertTrue(events.contains(new BuildTestEvent("run1",
				new RunStartEvent(1000))));
		assertTrue(events.contains(new BuildTestEvent("run2",
				new RunStartEvent(2000))));
	}

	private void event(BuildTestEvent buildTestEvent) {
		if (!testRunIds.getRunIds().contains(buildTestEvent.getRunId())) {
			testRunIds.addRunId(buildTestEvent.getRunId());
		}
		runningBuildTestEvents.event(buildTestEvent);
		saveTestEventsListener.event(buildTestEvent);
	}

	@Test
	public void testGetEventsAfterBuildHasRun() {
		// Given
		BuildTestEvent firstEvent = new BuildTestEvent("run1",
				new RunStartEvent(1000));
		BuildTestEvent secondEvent = new BuildTestEvent("run2",
				new RunStartEvent(2000));
		BuildTestResults testEvents = new BuildTestResults(persistenceRootFile,
				testRunIds, runningBuildTestEvents, buildTestStats);
		event(firstEvent);
		event(secondEvent);
		saveTestEventsListener.destroy();
		testEvents.onBuildComplete();

		// When
		List<BuildTestEvent> events = testEvents.getEvents();

		// Then
		assertTrue(events.contains(firstEvent));
		assertTrue(events.contains(secondEvent));
	}

}
