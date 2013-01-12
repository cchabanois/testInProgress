package org.jenkinsci.plugins.testinprogress.events.run;

public interface IRunTestEventListener {

	public void event(IRunTestEvent testEvent);

}