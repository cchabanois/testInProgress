package org.jenkinsci.plugins.testinprogress;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jenkinsci.plugins.testinprogress.events.run.IRunTestEvent;
import org.jenkinsci.plugins.testinprogress.events.run.IRunTestEventListener;

public class SaveTestEventsListener implements
		IRunTestEventListener {
	private final static Logger LOG = Logger
			.getLogger(SaveTestEventsListener.class.getName());

	private final File eventsFile;
	private FileWriter fileWriter;

	public SaveTestEventsListener(File eventsFile) {
		this.eventsFile = eventsFile;
	}

	private synchronized void saveEvent(IRunTestEvent testEvent) {
		try {
			fileWriter.write(testEvent.toString() + '\n');
		} catch (IOException e) {
			LOG.log(Level.WARNING, "Cannot save junit event", e);
		}
	}

	public void init() throws IOException {
		fileWriter = new FileWriter(eventsFile);
	}

	public void destroy() {
		try {
			fileWriter.close();
		} catch (IOException e) {
			LOG.log(Level.WARNING, "Cannot close junit events file", e);
		}
	}

	public void event(IRunTestEvent testEvent) {
		saveEvent(testEvent);
	}

}