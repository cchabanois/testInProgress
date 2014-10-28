package org.jenkinsci.testinprogress.server.build;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.jenkinsci.testinprogress.server.events.build.BuildTestEvent;
import org.jenkinsci.testinprogress.server.events.build.IBuildTestEvents;
import org.jenkinsci.testinprogress.server.events.build.TestRunIds;
import org.jenkinsci.testinprogress.server.events.run.IRunTestEvent;
import org.jenkinsci.testinprogress.server.events.run.IRunTestEventListener;
import org.jenkinsci.testinprogress.server.events.run.RunTestEventsGenerator;
import org.jenkinsci.testinprogress.server.messages.ITestRunListener;
import org.jenkinsci.testinprogress.server.messages.TestMessagesParser;

/**
 * Retrieve build test events for a completed build
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 *
 */
public class CompletedBuildTestEvents implements IBuildTestEvents {

	public final File directory;
	private final TestRunIds runIds;

	public CompletedBuildTestEvents(TestRunIds runIds, File directory) {
		this.directory = directory;
		this.runIds = runIds;
	}

	public List<BuildTestEvent> getEvents() {
		final ArrayList<BuildTestEvent> testEvents = new ArrayList<BuildTestEvent>();
		for (final String runId : runIds.getRunIds()) {
			FileReader fileReader;
			try {
				fileReader = new FileReader(new File(directory, runId
						+ ".events"));
				RunTestEventsGenerator eventsGenerator = new RunTestEventsGenerator(
						new IRunTestEventListener[] { new IRunTestEventListener() {
							public void event(IRunTestEvent testEvent) {
								testEvents.add(new BuildTestEvent(runId,
										testEvent));
							}
						} });
				TestMessagesParser parser = new TestMessagesParser(
						new ITestRunListener[] { eventsGenerator });
				parser.processTestMessages(fileReader);
			} catch (FileNotFoundException e) {
				// no file for this run id ...
			}
		}
		return testEvents;
	}

}
