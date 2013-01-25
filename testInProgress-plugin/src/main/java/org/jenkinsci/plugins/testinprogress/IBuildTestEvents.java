package org.jenkinsci.plugins.testinprogress;

import java.util.List;

import org.jenkinsci.plugins.testinprogress.events.build.BuildTestEvent;

/**
 * Test events for a build
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 *
 */
public interface IBuildTestEvents {

	public abstract List<BuildTestEvent> getEvents();

}