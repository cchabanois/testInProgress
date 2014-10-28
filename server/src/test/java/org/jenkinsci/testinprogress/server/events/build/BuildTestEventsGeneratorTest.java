package org.jenkinsci.testinprogress.server.events.build;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.jenkinsci.testinprogress.server.events.run.RunEndEvent;
import org.jenkinsci.testinprogress.server.events.run.RunStartEvent;
import org.jenkinsci.testinprogress.server.events.run.TestEndEvent;
import org.jenkinsci.testinprogress.server.events.run.TestStartEvent;
import org.jenkinsci.testinprogress.server.events.run.TestTreeEvent;
import org.junit.Test;

public class BuildTestEventsGeneratorTest {
	private TestRunIds testRunIds = new TestRunIds();

	@Test
	public void testGuessRunIdWhenGeneratingBuildTestEvents() {
		// Given
		IBuildTestEventListener listener = mock(IBuildTestEventListener.class);
		BuildTestEventsGenerator buildTestEventsGenerator = new BuildTestEventsGenerator(
				testRunIds, new IBuildTestEventListener[] { listener });

		// When
		buildTestEventsGenerator.event(new RunStartEvent(0, 2));
		buildTestEventsGenerator.event(new TestTreeEvent(0, "1", "suite", true,
				2));
		buildTestEventsGenerator.event(new TestTreeEvent(0, "2", "firstTest",
				false, 1));
		buildTestEventsGenerator.event(new TestTreeEvent(0, "3", "secondTest",
				false, 1));
		buildTestEventsGenerator.event(new TestStartEvent(0, "2", "firstTest",
				false));
		buildTestEventsGenerator.event(new TestEndEvent(0, "2", "firstTest",
				false, 0));
		buildTestEventsGenerator.event(new TestStartEvent(0, "3", "firstTest",
				false));
		buildTestEventsGenerator.event(new TestEndEvent(0, "3", "secondTest",
				false, 0));
		buildTestEventsGenerator.event(new RunEndEvent(0, 5000));

		// Then
		List<String> runIds = testRunIds.getRunIds();
		assertTrue(runIds.contains("suite"));
		verify(listener).event(
				new BuildTestEvent("suite", new RunStartEvent(0, 2)));
	}

	@Test
	public void testGenerateBuildTestEventsWhenRunIdIsKnown() {
		// Given
		IBuildTestEventListener listener = mock(IBuildTestEventListener.class);
		BuildTestEventsGenerator buildTestEventsGenerator = new BuildTestEventsGenerator(
				testRunIds, new IBuildTestEventListener[] { listener });
		String runId = "myRunId";
		
		// When
		buildTestEventsGenerator.event(new RunStartEvent(0, 2, runId));
		buildTestEventsGenerator.event(new TestTreeEvent(0, "1", "suite", true,
				2,runId));
		buildTestEventsGenerator.event(new TestTreeEvent(0, "2", "firstTest",
				false, 1,runId));
		buildTestEventsGenerator.event(new TestTreeEvent(0, "3", "secondTest",
				false, 1,runId));
		buildTestEventsGenerator.event(new TestStartEvent(0, "2", "firstTest",
				false,runId));
		buildTestEventsGenerator.event(new TestEndEvent(0, "2", "firstTest",
				false, 0,runId));
		buildTestEventsGenerator.event(new TestStartEvent(0, "3", "firstTest",
				false,runId));
		buildTestEventsGenerator.event(new TestEndEvent(0, "3", "secondTest",
				false, 0,runId));
		buildTestEventsGenerator.event(new RunEndEvent(0, 5000,runId));

		// Then
		List<String> runIds = testRunIds.getRunIds();
		assertTrue(runIds.contains(runId));
		verify(listener).event(
				new BuildTestEvent(runId, new RunStartEvent(0, 2, runId)));
	}
	
	
}
