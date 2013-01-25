package org.jenkinsci.plugins.testinprogress.events.build;

/**
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 *
 */
public interface IBuildTestEventListener {

	public void event(BuildTestEvent buildTestEvent);
	
}
