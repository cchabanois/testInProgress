package org.jenkinsci.plugins.testinprogress.events.build;

import java.util.HashSet;
import java.util.Set;

import org.jenkinsci.plugins.testinprogress.ITestRunIds;

public class TestRunIds implements ITestRunIds {
	private final Set<String> runIds = new HashSet<String>();
	
	public synchronized Set<String> getRunIds() {
		return new HashSet<String>(runIds);
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
