package org.jenkinsci.plugins.testinprogress;

import hudson.model.Action;
import hudson.model.AbstractBuild;
import hudson.model.Run;

import java.util.Collections;
import java.util.List;

import org.jenkinsci.testinprogress.server.build.BuildTestEventList;
import org.jenkinsci.testinprogress.server.build.BuildTestResults;
import org.jenkinsci.testinprogress.server.events.build.BuildTestEvent;
import org.jenkinsci.testinprogress.server.listeners.BuildTestStats;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * Action used to display the ivy report for the build
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public class TestInProgressRunAction implements Action {
	private static final String ICON_JUNIT_FILENAME = "/plugin/testInProgress/images/junit.gif";
	private static final String ICON_JUNIT_SUCCESS_FILENAME = "/plugin/testInProgress/images/junitsucc.gif";
	private static final String ICON_JUNIT_ERR_FILENAME = "/plugin/testInProgress/images/juniterr.gif";
	private final BuildTestResults buildTestResults;
	private final AbstractBuild build;

	public TestInProgressRunAction(AbstractBuild build,
			BuildTestResults buildTestResults) {
		this.build = build;
		this.buildTestResults = buildTestResults;
	}

	public AbstractBuild getBuild() {
		return build;
	}

	@JavaScriptMethod
	public BuildTestEventList getTestEvents(int fromIndex) {
		List<BuildTestEvent> events = buildTestResults.getEvents();
		return new BuildTestEventList(events.subList(fromIndex, events.size()),
				build.isBuilding());
	}

	@JavaScriptMethod
	public BuildTestEventList getPreviousTestEvents() {
		Run previousCompletedBuild = build.getPreviousCompletedBuild();
		List<BuildTestEvent> buildTestEvents = Collections.emptyList();
		if (previousCompletedBuild == null) {
			return new BuildTestEventList(buildTestEvents, false);
		}
		TestInProgressRunAction action = previousCompletedBuild
				.getAction(TestInProgressRunAction.class);
		if (action == null) {
			return new BuildTestEventList(buildTestEvents, false);
		}
		return action.getTestEvents(0);
	}

	public String getUrlName() {
		return "testinprogress";
	}

	public String getDisplayName() {
		return "Test progress report";
	}

	public String getIconFileName() {
		BuildTestStats buildTestStats = buildTestResults.getBuildTestStats();
		if (buildTestStats.getTestsErrorCount() > 0
				|| buildTestStats.getTestsFailedCount() > 0) {
			return ICON_JUNIT_ERR_FILENAME;
		} else if (buildTestResults.isBuildComplete()
				&& buildTestStats.getTestsCount() == buildTestStats
						.getTestsEndedCount()) {
			return ICON_JUNIT_SUCCESS_FILENAME;
		} else
			return ICON_JUNIT_FILENAME;
	}

}
