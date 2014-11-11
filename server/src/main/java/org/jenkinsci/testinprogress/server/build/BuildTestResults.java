package org.jenkinsci.testinprogress.server.build;

import java.io.File;
import java.util.List;

import org.jenkinsci.testinprogress.server.events.build.BuildTestEvent;
import org.jenkinsci.testinprogress.server.events.build.IBuildTestEvents;
import org.jenkinsci.testinprogress.server.events.build.TestRunIds;
import org.jenkinsci.testinprogress.server.listeners.BuildTestStats;
import org.jenkinsci.testinprogress.server.listeners.RunningBuildTestEvents;

/**
 * Test results (run ids used, test events and test stats) for a build
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 *
 */
public class BuildTestResults implements IBuildTestEvents {
	private transient IBuildTestEvents testEvents;
	private final File buildTestEventsDir;
	private final TestRunIds testRunIds;
	private final BuildTestStats buildTestStats;
	private boolean isBuildComplete = false;

	public BuildTestResults(File buildTestEventsDir, TestRunIds testRunIds,
			RunningBuildTestEvents testEvents, BuildTestStats buildTestStats) {
		this.buildTestEventsDir = buildTestEventsDir;
		this.testRunIds = testRunIds;
		this.testEvents = testEvents;
		this.buildTestStats = buildTestStats;
	}

	public synchronized void onBuildComplete() {
		this.testEvents = new CompletedBuildTestEvents(buildTestEventsDir);
		this.isBuildComplete = true;
	}

	public synchronized List<BuildTestEvent> getEvents() {
		if (testEvents != null) {
			return testEvents.getEvents();
		}
		testEvents = new CompletedBuildTestEvents(buildTestEventsDir);
		return testEvents.getEvents();

	}

	public BuildTestStats getBuildTestStats() {
		return buildTestStats;
	}

	public boolean isBuildComplete() {
		return isBuildComplete;
	}

	public TestRunIds getTestRunIds() {
		return testRunIds;
	}

}
