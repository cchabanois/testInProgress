/*
 * The MIT License
 *
 * Copyright (c) 2012, Cedric Chabanois
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE. 
 */
package org.jenkinsci.plugins.testinprogress;

import hudson.model.Action;
import hudson.model.AbstractBuild;
import hudson.model.Run;

import java.util.Collections;
import java.util.List;

import org.jenkinsci.plugins.testinprogress.events.build.BuildTestEvent;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * Action used to display the ivy report for the build
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public class TestInProgressRunAction implements Action {
	private static final String ICON_JUNIT_FILENAME = "/plugin/testInProgress-plugin/images/junit.gif";
	private static final String ICON_JUNIT_SUCCESS_FILENAME = "/plugin/testInProgress-plugin/images/junitsucc.gif";
	private static final String ICON_JUNIT_ERR_FILENAME = "/plugin/testInProgress-plugin/images/juniterr.gif";
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
	public List<BuildTestEvent> getTestEvents(int fromIndex) {
		List<BuildTestEvent> events = buildTestResults.getEvents();
		return events.subList(fromIndex, events.size());
	}

	@JavaScriptMethod
	public List<BuildTestEvent> getPreviousTestEvents() {
		Run previousCompletedBuild = build.getPreviousCompletedBuild();
		if (previousCompletedBuild == null) {
			return Collections.emptyList();
		}
		TestInProgressRunAction action = previousCompletedBuild.getAction(TestInProgressRunAction.class);
		if (action == null) {
			return Collections.emptyList();
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
