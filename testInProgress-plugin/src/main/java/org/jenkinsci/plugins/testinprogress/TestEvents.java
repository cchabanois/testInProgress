package org.jenkinsci.plugins.testinprogress;

import hudson.model.PersistenceRoot;

import java.io.File;
import java.util.List;

import org.jenkinsci.plugins.testinprogress.events.build.BuildTestEvent;
import org.jenkinsci.plugins.testinprogress.events.build.TestRunIds;

public class TestEvents implements ITestEvents {
	private static final String UNIT_EVENTS_DIR = "unitevents";
    private transient ITestEvents testEvents;
    private final PersistenceRoot persitenceRoot;
    private final TestRunIds testRunIds;
    
    public TestEvents(PersistenceRoot persitenceRoot, TestRunIds testRunIds, RunningBuildTestEvents testEvents) {
        this.persitenceRoot = persitenceRoot;
        this.testRunIds = testRunIds;
    	this.testEvents = testEvents;
    }

	public synchronized void onBuildComplete() {
		File dir = new File(persitenceRoot.getRootDir(), UNIT_EVENTS_DIR);
    	this.testEvents = new CompletedBuildTestEvents(testRunIds, dir);
	}

	public synchronized List<BuildTestEvent> getEvents() {
    	if (testEvents != null) {
    		return testEvents.getEvents();
    	}
    	File dir = new File(persitenceRoot.getRootDir(), UNIT_EVENTS_DIR);
    	testEvents = new CompletedBuildTestEvents(testRunIds,dir);
    	return testEvents.getEvents();

	}

}
