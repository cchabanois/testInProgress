package org.jenkinsci.plugins.testinprogress;

import java.util.ArrayList;
import java.util.List;

import org.jenkinsci.plugins.testinprogress.events.build.BuildTestEvent;
import org.jenkinsci.plugins.testinprogress.events.build.IBuildTestEventListener;


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
