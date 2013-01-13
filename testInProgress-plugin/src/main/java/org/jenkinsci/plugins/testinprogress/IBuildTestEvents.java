package org.jenkinsci.plugins.testinprogress;

import java.util.List;

import org.jenkinsci.plugins.testinprogress.events.build.BuildTestEvent;

public interface IBuildTestEvents {

	public abstract List<BuildTestEvent> getEvents();

}