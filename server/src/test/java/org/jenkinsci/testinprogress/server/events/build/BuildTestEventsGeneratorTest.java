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
		buildTestEventsGenerator.event(new RunStartEvent(0));
		buildTestEventsGenerator.event(new TestTreeEvent(0, "1", "suite", null,
				true));
		buildTestEventsGenerator.event(new TestTreeEvent(0, "2", "firstTest",
				"1", false));
		buildTestEventsGenerator.event(new TestTreeEvent(0, "3", "secondTest",
				"1", false));
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
				new BuildTestEvent("suite", new RunStartEvent(0)));
	}

	@Test
	public void testGenerateBuildTestEventsWhenRunIdIsKnown() {
		// Given
		IBuildTestEventListener listener = mock(IBuildTestEventListener.class);
		BuildTestEventsGenerator buildTestEventsGenerator = new BuildTestEventsGenerator(
				testRunIds, new IBuildTestEventListener[] { listener });
		String runId = "myRunId";

		// When
		buildTestEventsGenerator.event(new RunStartEvent(0, runId));
		buildTestEventsGenerator.event(new TestTreeEvent(0, "1", "suite", null,
				true));
		buildTestEventsGenerator.event(new TestTreeEvent(0, "2", "firstTest",
				"1", false));
		buildTestEventsGenerator.event(new TestTreeEvent(0, "3", "secondTest",
				"1", false));
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
		assertTrue(runIds.contains(runId));
		verify(listener).event(
				new BuildTestEvent(runId, new RunStartEvent(0, runId)));
	}

}
