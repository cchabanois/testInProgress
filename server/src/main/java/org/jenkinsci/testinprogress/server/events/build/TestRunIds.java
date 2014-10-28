package org.jenkinsci.testinprogress.server.events.build;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Generate and Keep all run ids for a build.
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public class TestRunIds implements ITestRunIds {
	private final Set<String> runIds = new HashSet<String>();

	public synchronized List<String> getRunIds() {
		return new ArrayList<String>(runIds);
	}

	/**
	 * Create a run id and add it to the sets of run ids for the build.
	 * 
	 * @param proposedRunId
	 *            the proposed run id (generally the main suite name)
	 * @return an unique run id for the build
	 */
	public synchronized String addRunId(String proposedRunId) {
		String runId = proposedRunId;
		int num = 0;
		while (runIds.contains(runId)) {
			num++;
			runId = proposedRunId + "-" + Integer.toString(num);
		}
		runIds.add(runId);
		return runId;
	}

}
