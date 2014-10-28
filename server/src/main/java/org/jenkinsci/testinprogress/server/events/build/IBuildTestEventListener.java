package org.jenkinsci.testinprogress.server.events.build;

/**
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 *
 */
public interface IBuildTestEventListener {

	public void event(BuildTestEvent buildTestEvent);
	
}
