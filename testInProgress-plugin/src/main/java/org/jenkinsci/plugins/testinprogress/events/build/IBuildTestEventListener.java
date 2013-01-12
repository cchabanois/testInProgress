package org.jenkinsci.plugins.testinprogress.events.build;


public interface IBuildTestEventListener {

	public void event(BuildTestEvent buildTestEvent);
	
}
