package org.jenkinsci.plugins.testinprogress.events.build;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import org.jenkinsci.plugins.testinprogress.events.run.RunEndEvent;
import org.jenkinsci.plugins.testinprogress.events.run.RunStartEvent;
import org.jenkinsci.plugins.testinprogress.events.run.TestEndEvent;
import org.jenkinsci.plugins.testinprogress.events.run.TestStartEvent;
import org.jenkinsci.plugins.testinprogress.events.run.TestTreeEvent;
import org.junit.Test;

public class BuildTestEventsGeneratorTest {
	private TestRunIds testRunIds = new TestRunIds();

	@Test
	public void testBuildTestEventsGenerator() {
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
		assertTrue(testRunIds.getRunIds().contains("suite"));
		verify(listener).event(
				new BuildTestEvent("suite", new RunStartEvent(0, 2)));
	}

}
