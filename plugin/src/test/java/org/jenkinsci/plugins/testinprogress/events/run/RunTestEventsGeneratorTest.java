package org.jenkinsci.plugins.testinprogress.events.run;

import org.jenkinsci.plugins.testinprogress.events.run.RunTestEventsGenerator;
import org.jenkinsci.plugins.testinprogress.events.run.IRunTestEventListener;
import org.jenkinsci.plugins.testinprogress.events.run.TestStartEvent;
import org.jenkinsci.plugins.testinprogress.events.run.TestTreeEvent;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class RunTestEventsGeneratorTest {
	private RunTestEventsGenerator eventsGenerator;
	private IRunTestEventListener listener = mock(IRunTestEventListener.class);

	@Before
	public void setUp() {
		this.eventsGenerator = new RunTestEventsGenerator(
				new IRunTestEventListener[] { listener });
	}

	@Test
	public void testIgnoredTest() {
		// Given

		// When
		eventsGenerator.testStarted(200, "4",
				"testIgnored(testproject.CalcTest)",true,"");

		// Then
		verify(listener).event(
				new TestStartEvent(200, "4",
						"testIgnored(testproject.CalcTest)", true));
	}

	@Test
	public void testTreeEventTest() {
		// Given

		// When
		eventsGenerator.testTreeEntry(200, "1","testproject.AllTests","","",true,2,"");

		// Then
		verify(listener).event(
				new TestTreeEvent(200, "1", "testproject.AllTests", true, 2));
	}

	@Test
	public void testTimeElapsedForTestEndEvent() {
		// Given
		
		// When
		eventsGenerator.testStarted(200, "4", 
				"test1(testproject.CalcTest)",false,"");
		eventsGenerator.testStarted(500, "5",
				"test2(testproject.CalcTest)",false,"");
		eventsGenerator.testEnded(1000, "4", "test1(testproject.CalcTest)",false,"");
		eventsGenerator.testEnded(1100, "5", "test2(testproject.CalcTest)",false,"");
		
		// Then 
		verify(listener).event(new TestEndEvent(1000, "4", "test1(testproject.CalcTest)", false, 800));
		verify(listener).event(new TestEndEvent(1100, "5", "test2(testproject.CalcTest)", false, 600));
	}
	
}
