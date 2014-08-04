package org.jenkinsci.plugins.testinprogress.events.build;

import java.util.ArrayList;
import java.util.List;

import org.jenkinsci.plugins.testinprogress.events.run.IRunTestEvent;
import org.jenkinsci.plugins.testinprogress.events.run.IRunTestEventListener;
import org.jenkinsci.plugins.testinprogress.events.run.RunStartEvent;
import org.jenkinsci.plugins.testinprogress.events.run.TestTreeEvent;

/**
 * Generates {@link BuildTestEvent}s for a run.
 * 
 * A {@link BuildTestEventsGenerator} must be used for each run.
 * 
 * @author @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public class BuildTestEventsGenerator implements IRunTestEventListener {
	private RunStartEvent runStartEvent;
	private boolean startEventFired = false;
	private List<TestTreeEvent> testTreeEvents = new ArrayList<TestTreeEvent>();
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
			String eventRunId = getRunID(testEvent); 
			if( eventRunId != null) {
				startEventFired = true;
				fireEvent(testEvent);
			} else
				runStartEvent = (RunStartEvent) testEvent;
		} else if (testEvent instanceof TestTreeEvent) {
			if(!startEventFired){
				startEventFired = true;
				runId = guessRunID(testEvent);
				fireEvent(runStartEvent);
			}
			fireEvent(testEvent);
		} else {
			fireEvent(testEvent);
		}
	}

	private void fireEvent(IRunTestEvent testEvent) {
		for (IBuildTestEventListener listener : listeners) {
			listener.event(new BuildTestEvent(guessRunID(testEvent), testEvent));
		}
	}

	/*private String guessRunID(List<TestTreeEvent> testTreeEvents) {
		// TODO : test name "null"
		if (testTreeEvents.size() == 0) {
			return testRunIds.addRunId("empty");
		} else {
			return testRunIds.addRunId(testTreeEvents.get(0).getTestName());
		}
	}*/
	
	private String guessRunID(IRunTestEvent testEvent) {
		String rId = getRunID(testEvent);
		if(runId!=null)
			return runId;
		return testRunIds.addRunId(rId);		
	}
	
	private String getRunID(IRunTestEvent testEvent) {
		String testRunId = 	testEvent.getRunId();	
		if (testRunId == null || ("".equalsIgnoreCase(testRunId))) {
			if(testEvent instanceof TestTreeEvent)
				return ((TestTreeEvent)testEvent).getTestName();
			else 
				return runId;
		} else {
			return testRunId;
		}
	}

}
