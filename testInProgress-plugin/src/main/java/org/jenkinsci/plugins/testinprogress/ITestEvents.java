package org.jenkinsci.plugins.testinprogress;

import java.util.List;

import org.jenkinsci.plugins.testinprogress.events.build.BuildTestEvent;

public interface ITestEvents {

	public abstract List<BuildTestEvent> getEvents();

}