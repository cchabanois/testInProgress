package org.jenkinsci.testinprogress.server.events.build;

import java.util.List;

/**
 * Test events for a build
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 *
 */
public interface IBuildTestEvents {

	public abstract List<BuildTestEvent> getEvents();

}