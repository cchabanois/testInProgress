package org.jenkinsci.plugins.testinprogress;

import java.util.List;

import org.jenkinsci.plugins.testinprogress.events.run.IRunTestEvent;

public interface ITestEvents {

	public abstract List<IRunTestEvent> getEvents();

}