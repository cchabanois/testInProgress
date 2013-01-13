package org.jenkinsci.plugins.testinprogress;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jenkinsci.plugins.testinprogress.events.build.BuildTestEvent;
import org.jenkinsci.plugins.testinprogress.events.build.TestRunIds;
import org.jenkinsci.plugins.testinprogress.events.run.RunEndEvent;
import org.jenkinsci.plugins.testinprogress.events.run.RunStartEvent;
import org.jenkinsci.plugins.testinprogress.utils.TestAreaUtils;
import org.junit.Before;
import org.junit.Test;

public class SaveTestEventsListenerTest {
	private SaveTestEventsListener saveTestEventsListener;
	private File directory;
	
	@Before
	public void setUp() throws IOException {
		directory = TestAreaUtils.getNonExistingFileInTestArea("unitTests");
	}
	
	@Test
	public void testEvent() throws Exception {
		// Given
		saveTestEventsListener = new SaveTestEventsListener(directory);
		saveTestEventsListener.init();
		
		// When
		saveTestEventsListener.event(new BuildTestEvent("run1", new RunStartEvent(4)));
		saveTestEventsListener.event(new BuildTestEvent("run2", new RunStartEvent(3)));
		saveTestEventsListener.event(new BuildTestEvent("run1", new RunEndEvent(4000)));
		saveTestEventsListener.event(new BuildTestEvent("run2", new RunEndEvent(2000)));
		
		// Then
		TestRunIds testRunIds = new TestRunIds();
		testRunIds.addRunId("run1");
		testRunIds.addRunId("run2");
		CompletedBuildTestEvents completedBuildTestEvents = new CompletedBuildTestEvents(testRunIds, directory);
		List<BuildTestEvent> events = completedBuildTestEvents.getEvents();
		assertTrue(events.contains(new BuildTestEvent("run1", new RunStartEvent(4))));
		assertTrue(events.contains(new BuildTestEvent("run2", new RunStartEvent(3))));
		assertTrue(events.contains(new BuildTestEvent("run1", new RunEndEvent(4000))));
		assertTrue(events.contains(new BuildTestEvent("run2", new RunEndEvent(2000))));
	}
	
	
	

}
