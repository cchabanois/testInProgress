package org.jenkinsci.testinprogress.server.build;

import java.util.List;

import org.jenkinsci.testinprogress.server.events.build.BuildTestEvent;

/**
 * List of build test events.
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 *
 */
public class BuildTestEventList {
	private final boolean isBuilding;
	private final List<BuildTestEvent> buildTestEvents;
	
	public BuildTestEventList(List<BuildTestEvent> buildTestEvents, boolean isBuilding) {
		this.buildTestEvents = buildTestEvents;
		this.isBuilding = isBuilding;
	}
	
	public boolean isBuilding() {
		return isBuilding;
	}
	
	public List<BuildTestEvent> getBuildTestEvents() {
		return buildTestEvents;
	}
	
}
