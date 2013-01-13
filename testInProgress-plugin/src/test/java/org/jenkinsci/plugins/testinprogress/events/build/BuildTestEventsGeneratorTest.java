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
		BuildTestEventsGenerator buildTestEventsGenerator = new BuildTestEventsGenerator(testRunIds, new IBuildTestEventListener[] { listener });
		
		// When
		buildTestEventsGenerator.event(new RunStartEvent(2));
		buildTestEventsGenerator.event(new TestTreeEvent("1", "suite", true, 2));
		buildTestEventsGenerator.event(new TestTreeEvent("2", "firstTest", false, 1));
		buildTestEventsGenerator.event(new TestTreeEvent("3", "secondTest", false, 1));
		buildTestEventsGenerator.event(new TestStartEvent("2", "firstTest", false));
		buildTestEventsGenerator.event(new TestEndEvent("2", "firstTest", false));
		buildTestEventsGenerator.event(new TestStartEvent("3", "firstTest", false));
		buildTestEventsGenerator.event(new TestEndEvent("3", "secondTest", false));
		buildTestEventsGenerator.event(new RunEndEvent(5000));
		
		// Then
		assertTrue(testRunIds.getRunIds().contains("suite"));
		verify(listener).event(new BuildTestEvent("suite", new RunStartEvent(2)));
	}
	
}
