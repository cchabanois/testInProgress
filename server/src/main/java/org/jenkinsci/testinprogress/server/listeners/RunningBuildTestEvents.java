package org.jenkinsci.testinprogress.server.listeners;

import java.util.ArrayList;
import java.util.List;

import org.jenkinsci.testinprogress.server.events.build.BuildTestEvent;
import org.jenkinsci.testinprogress.server.events.build.IBuildTestEventListener;
import org.jenkinsci.testinprogress.server.events.build.IBuildTestEvents;


/**
 * Retrieve build test events for a running build
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public class RunningBuildTestEvents implements IBuildTestEventListener,
		IBuildTestEvents {
	private final List<BuildTestEvent> testEvents = new ArrayList<BuildTestEvent>();

	public synchronized void event(BuildTestEvent testEvent) {
		testEvents.add(testEvent);
	}

	public synchronized List<BuildTestEvent> getEvents() {
		return new ArrayList<BuildTestEvent>(testEvents);
	}

}
