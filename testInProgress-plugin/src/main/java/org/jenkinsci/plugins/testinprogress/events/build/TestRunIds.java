package org.jenkinsci.plugins.testinprogress.events.build;

import java.util.ArrayList;
import java.util.List;

import org.jenkinsci.plugins.testinprogress.ITestRunIds;

public class TestRunIds implements ITestRunIds {
	private final List<String> runIds = new ArrayList<String>();
	
	public synchronized List<String> getRunIds() {
		return new ArrayList<String>(runIds);
	}

	public synchronized String addRunId(String proposedRunId) {
		String runId = proposedRunId;
		int num = 0;
		while (runIds.contains(proposedRunId)) {
			num++;
			runId = proposedRunId +"-"+Integer.toString(num);
		}
		runIds.add(runId);
		return runId;
	}
	
}
