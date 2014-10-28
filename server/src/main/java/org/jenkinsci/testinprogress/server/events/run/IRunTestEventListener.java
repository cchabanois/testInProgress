package org.jenkinsci.testinprogress.server.events.run;

public interface IRunTestEventListener {

	public void event(IRunTestEvent testEvent);

}