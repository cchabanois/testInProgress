package org.jenkinsci.testinprogress.server.events.build;

import org.jenkinsci.testinprogress.server.events.run.IRunTestEvent;
import org.jenkinsci.testinprogress.server.events.run.IRunTestEventListener;
import org.jenkinsci.testinprogress.server.events.run.RunStartEvent;
import org.jenkinsci.testinprogress.server.events.run.TestTreeEvent;

/**
 * Generates {@link BuildTestEvent}s for a run.
 * 
 * A {@link BuildTestEventsGenerator} must be used for each run.
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public class BuildTestEventsGenerator implements IRunTestEventListener {
	private RunStartEvent runStartEvent;
	private boolean startEventFired = false;
	private String runId = null;
	private final IBuildTestEventListener[] listeners;
	private final TestRunIds testRunIds;

	public BuildTestEventsGenerator(TestRunIds testRunIds,
			IBuildTestEventListener[] listeners) {
		this.testRunIds = testRunIds;
		this.listeners = listeners;
	}

	public void event(IRunTestEvent testEvent) {
		if (testEvent instanceof RunStartEvent) {
			runId = getRunID((RunStartEvent)testEvent); 
			if (runId != null) {
				startEventFired = true;
				fireEvent(testEvent);
			} else {
				// wait until first TestTreeEvent so that we can guess a runId
				runStartEvent = (RunStartEvent) testEvent;
			}
		} else if (testEvent instanceof TestTreeEvent) {
			if(!startEventFired){
				startEventFired = true;
				runId = guessRunID((TestTreeEvent)testEvent);
				fireEvent(runStartEvent);
			}
			fireEvent(testEvent);
		} else {
			fireEvent(testEvent);
		}
	}

	private void fireEvent(IRunTestEvent testEvent) {
		for (IBuildTestEventListener listener : listeners) {
			listener.event(new BuildTestEvent(runId, testEvent));
		}
	}

	private String guessRunID(TestTreeEvent testEvent) {
		String proposedRunId = testEvent.getTestName();
		return testRunIds.addRunId(proposedRunId);
	}
	
	private String getRunID(RunStartEvent testEvent) {
		String testRunId = 	testEvent.getRunId();	
		if (testRunId == null) {
			return null;
		} else {
			return testRunIds.addRunId(testRunId);
		}
	}

}
