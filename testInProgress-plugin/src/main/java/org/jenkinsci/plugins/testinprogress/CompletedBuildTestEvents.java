package org.jenkinsci.plugins.testinprogress;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jenkinsci.plugins.testinprogress.events.run.RunTestEventsGenerator;
import org.jenkinsci.plugins.testinprogress.events.run.IRunTestEvent;
import org.jenkinsci.plugins.testinprogress.events.run.IRunTestEventListener;
import org.jenkinsci.plugins.testinprogress.messages.ITestRunListener;
import org.jenkinsci.plugins.testinprogress.messages.TestMessagesParser;

public class CompletedBuildTestEvents implements ITestEvents {

	public final File eventsFile;

	public CompletedBuildTestEvents(File eventsFile) {
		this.eventsFile = eventsFile;
	}

	public List<IRunTestEvent> getEvents() {
		if (!eventsFile.exists()) {
			return Collections.emptyList();
		}
		final ArrayList<IRunTestEvent> testEvents = new ArrayList<IRunTestEvent>();
		FileReader fileReader;
		try {
			fileReader = new FileReader(eventsFile);
			RunTestEventsGenerator eventsGenerator = new RunTestEventsGenerator(
					new IRunTestEventListener[] { new IRunTestEventListener() {
						public void event(IRunTestEvent testEvent) {
							testEvents.add(testEvent);
						}
					} });
			TestMessagesParser parser = new TestMessagesParser(
					new ITestRunListener[] { eventsGenerator });
			parser.processTestMessages(fileReader);

			return testEvents;
		} catch (FileNotFoundException e) {
			return Collections.emptyList();
		}
	}

}
