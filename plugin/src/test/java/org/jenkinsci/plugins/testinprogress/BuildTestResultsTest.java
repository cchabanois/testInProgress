package org.jenkinsci.plugins.testinprogress;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hudson.model.PersistenceRoot;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jenkinsci.plugins.testinprogress.events.build.BuildTestEvent;
import org.jenkinsci.plugins.testinprogress.events.build.TestRunIds;
import org.jenkinsci.plugins.testinprogress.events.run.RunStartEvent;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class BuildTestResultsTest {
	private PersistenceRoot persistenceRoot = mock(PersistenceRoot.class);
	private TestRunIds testRunIds = new TestRunIds();
	private SaveTestEventsListener saveTestEventsListener;
	private RunningBuildTestEvents runningBuildTestEvents = new RunningBuildTestEvents();
	private BuildTestStats buildTestStats = new BuildTestStats();

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	
	@Before
	public void setUp() throws IOException {
		File persistenceRootFile = tempFolder.newFolder();
		when(persistenceRoot.getRootDir()).thenReturn(persistenceRootFile);
		saveTestEventsListener = new SaveTestEventsListener(new File(
				persistenceRootFile, "unitevents"));
		saveTestEventsListener.init();
	}

	@Test
	public void testGetEventsWhenBuildIsRunning() {
		// Given
		BuildTestResults testEvents = new BuildTestResults(persistenceRoot,
				testRunIds, runningBuildTestEvents, buildTestStats);
		event(new BuildTestEvent("run1", new RunStartEvent(1000,1)));
		event(new BuildTestEvent("run2", new RunStartEvent(2000,2)));

		// When
		List<BuildTestEvent> events = testEvents.getEvents();

		// Then
		assertTrue(events.contains(new BuildTestEvent("run1",
				new RunStartEvent(1000,1))));
		assertTrue(events.contains(new BuildTestEvent("run2",
				new RunStartEvent(2000,2))));
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
				new RunStartEvent(1000,1));
		BuildTestEvent secondEvent = new BuildTestEvent("run2",
				new RunStartEvent(2000,1));
		BuildTestResults testEvents = new BuildTestResults(persistenceRoot,
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
