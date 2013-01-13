package org.jenkinsci.plugins.testinprogress;

import java.util.ArrayList;
import java.util.List;

import org.jenkinsci.plugins.testinprogress.events.build.BuildTestEvent;
import org.jenkinsci.plugins.testinprogress.events.build.IBuildTestEventListener;

public class RunningBuildTestEvents implements
		IBuildTestEventListener, ITestEvents {
	private final List<BuildTestEvent> testEvents = new ArrayList<BuildTestEvent>();

	public void event(BuildTestEvent testEvent) {
		addEvent(testEvent);
	}

	private synchronized void addEvent(BuildTestEvent testEvent) {
		testEvents.add(testEvent);
	}

	public synchronized List<BuildTestEvent> getEvents() {
		return new ArrayList<BuildTestEvent>(testEvents);
	}

}
