package org.jenkinsci.plugins.testinprogress;

import java.util.List;

import org.jenkinsci.plugins.testinprogress.events.ITestEvent;

public interface ITestEvents {

	public abstract List<ITestEvent> getEvents();

}