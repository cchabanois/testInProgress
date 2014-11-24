package org.jenkinsci.testinprogress.server.listeners;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jenkinsci.testinprogress.server.build.CompletedBuildTestEvents;
import org.jenkinsci.testinprogress.server.events.build.BuildTestEvent;
import org.jenkinsci.testinprogress.server.events.run.RunEndEvent;
import org.jenkinsci.testinprogress.server.events.run.RunStartEvent;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class SaveTestEventsListenerTest {
	private SaveTestEventsListener saveTestEventsListener;
	private File directory;
	
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	
	@Before
	public void setUp() throws IOException {
		directory = tempFolder.newFolder();
	}
	
	@Test
	public void testEvent() throws Exception {
		// Given
		saveTestEventsListener = new SaveTestEventsListener(directory);
		saveTestEventsListener.init();
		
		// When
		saveTestEventsListener.event(new BuildTestEvent("run1", new RunStartEvent(1000)));
		saveTestEventsListener.event(new BuildTestEvent("run2", new RunStartEvent(2000)));
		saveTestEventsListener.event(new BuildTestEvent("run1", new RunEndEvent(5000,4000)));
		saveTestEventsListener.event(new BuildTestEvent("run2", new RunEndEvent(3000,2000)));
		
		// Then
		CompletedBuildTestEvents completedBuildTestEvents = new CompletedBuildTestEvents(directory);
		List<BuildTestEvent> events = completedBuildTestEvents.getEvents();
		assertTrue(events.contains(new BuildTestEvent("run1", new RunStartEvent(1000))));
		assertTrue(events.contains(new BuildTestEvent("run2", new RunStartEvent(2000))));
		assertTrue(events.contains(new BuildTestEvent("run1", new RunEndEvent(5000,4000))));
		assertTrue(events.contains(new BuildTestEvent("run2", new RunEndEvent(3000,2000))));
	}
	
	
	

}
