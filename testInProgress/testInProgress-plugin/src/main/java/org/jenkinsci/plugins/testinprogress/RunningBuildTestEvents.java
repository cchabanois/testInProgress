package org.jenkinsci.plugins.testinprogress;

import java.util.ArrayList;
import java.util.List;

import org.jenkinsci.plugins.testinprogress.events.ITestEvent;
import org.jenkinsci.plugins.testinprogress.events.ITestEventListener;

public class RunningBuildTestEvents implements
		ITestEventListener, ITestEvents {
	private final List<ITestEvent> testEvents = new ArrayList<ITestEvent>();

	public void event(ITestEvent testEvent) {
		addEvent(testEvent);
	}

	private synchronized void addEvent(ITestEvent testEvent) {
		testEvents.add(testEvent);
	}

	public synchronized List<ITestEvent> getEvents() {
		return new ArrayList<ITestEvent>(testEvents);
	}

}
