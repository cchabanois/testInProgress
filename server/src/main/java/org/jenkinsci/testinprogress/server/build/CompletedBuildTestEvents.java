package org.jenkinsci.testinprogress.server.build;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jenkinsci.testinprogress.server.events.build.BuildTestEvent;
import org.jenkinsci.testinprogress.server.events.build.IBuildTestEvents;
import org.jenkinsci.testinprogress.server.events.run.IRunTestEvent;
import org.jenkinsci.testinprogress.server.events.run.IRunTestEventListener;
import org.jenkinsci.testinprogress.server.events.run.RunTestEventsGenerator;
import org.jenkinsci.testinprogress.server.messages.AllVersionsTestMessagesParser;
import org.jenkinsci.testinprogress.server.messages.ITestMessagesParser;
import org.jenkinsci.testinprogress.server.messages.ITestRunListener;

/**
 * Retrieve build test events for a completed build
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 *
 */
public class CompletedBuildTestEvents implements IBuildTestEvents {

	private final File testEventsDir;

	public CompletedBuildTestEvents(File testEventsDir) {
		this.testEventsDir = testEventsDir;
	}

	public List<BuildTestEvent> getEvents() {
		final ArrayList<BuildTestEvent> testEvents = new ArrayList<BuildTestEvent>();
		File[] testEventsFiles = testEventsDir.listFiles(new FilenameFilter() {

			public boolean accept(File dir, String name) {
				return name.endsWith(".events");
			}
		});
		for (File testEventsFile : testEventsFiles) {
			FileReader fileReader;
			try {
				fileReader = new FileReader(testEventsFile);
				final String runId = testEventsFile.getName().substring(0,
						testEventsFile.getName().length() - ".events".length());
				RunTestEventsGenerator eventsGenerator = new RunTestEventsGenerator(
						new IRunTestEventListener[] { new IRunTestEventListener() {
							public void event(IRunTestEvent testEvent) {
								testEvents.add(new BuildTestEvent(runId,
										testEvent));
							}
						} });
				ITestMessagesParser parser = new AllVersionsTestMessagesParser(
						new ITestRunListener[] { eventsGenerator });
				parser.processTestMessages(fileReader);
			} catch (FileNotFoundException e) {
				// should not happen
			}
		}
		Collections.sort(testEvents, new BuildTestEventComparator());
		return testEvents;
	}

	private static class BuildTestEventComparator implements Comparator<BuildTestEvent>{

		public int compare(BuildTestEvent buildTestEvent1, BuildTestEvent buildTestEvent2) {
			if (buildTestEvent1.getRunTestEvent().getTimestamp() < buildTestEvent2.getRunTestEvent().getTimestamp()) {
				return -1;
			}
			if (buildTestEvent1.getRunTestEvent().getTimestamp() > buildTestEvent2.getRunTestEvent().getTimestamp()) {
				return 1;
			}
			return 0;
		}
		
	}
	
}
