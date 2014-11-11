package org.jenkinsci.testinprogress.server.build;

import java.util.List;

import org.jenkinsci.testinprogress.server.events.build.BuildTestEvent;
import org.jenkinsci.testinprogress.server.events.build.IBuildTestEvents;

public class BuildTestEvents implements IBuildTestEvents {
	private final List<BuildTestEvent> testEvents;
	
	public BuildTestEvents(List<BuildTestEvent> testEvents) {
		this.testEvents = testEvents;
	}
	
	public List<BuildTestEvent> getEvents() {
		return testEvents;
	}

}
