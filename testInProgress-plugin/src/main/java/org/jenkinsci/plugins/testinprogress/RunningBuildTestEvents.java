package org.jenkinsci.plugins.testinprogress;

import java.util.ArrayList;
import java.util.List;

import org.jenkinsci.plugins.testinprogress.events.IRunTestEvent;
import org.jenkinsci.plugins.testinprogress.events.ITestEventListener;

public class RunningBuildTestEvents implements
		ITestEventListener, ITestEvents {
	private final List<IRunTestEvent> testEvents = new ArrayList<IRunTestEvent>();

	public void event(IRunTestEvent testEvent) {
		addEvent(testEvent);
	}

	private synchronized void addEvent(IRunTestEvent testEvent) {
		testEvents.add(testEvent);
	}

	public synchronized List<IRunTestEvent> getEvents() {
		return new ArrayList<IRunTestEvent>(testEvents);
	}

}
