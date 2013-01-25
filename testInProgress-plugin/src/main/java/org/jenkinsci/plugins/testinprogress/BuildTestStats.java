package org.jenkinsci.plugins.testinprogress;

import java.util.concurrent.atomic.AtomicLong;

import org.jenkinsci.plugins.testinprogress.events.build.BuildTestEvent;
import org.jenkinsci.plugins.testinprogress.events.build.IBuildTestEventListener;
import org.jenkinsci.plugins.testinprogress.events.run.IRunTestEvent;
import org.jenkinsci.plugins.testinprogress.events.run.RunStartEvent;
import org.jenkinsci.plugins.testinprogress.events.run.TestEndEvent;
import org.jenkinsci.plugins.testinprogress.events.run.TestErrorEvent;
import org.jenkinsci.plugins.testinprogress.events.run.TestFailedEvent;

/**
 * Test statistics for a build
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 *
 */
public class BuildTestStats implements IBuildTestEventListener {
	private AtomicLong testsCount = new AtomicLong(0);
	private AtomicLong testsFailedCount = new AtomicLong(0);
	private AtomicLong testsErrorCount = new AtomicLong(0);
	private AtomicLong testsEndedCount = new AtomicLong(0);

	public void event(BuildTestEvent buildTestEvent) {
		IRunTestEvent runTestEvent = buildTestEvent.getRunTestEvent();
		if (runTestEvent instanceof RunStartEvent) {
			testsCount.addAndGet(((RunStartEvent) runTestEvent).getTestCount());
		} else if (runTestEvent instanceof TestFailedEvent) {
			testsFailedCount.incrementAndGet();
		} else if (runTestEvent instanceof TestErrorEvent) {
			testsErrorCount.incrementAndGet();
		} else if (runTestEvent instanceof TestEndEvent) {
			testsEndedCount.incrementAndGet();
		}

	}

	public long getTestsCount() {
		return testsCount.get();
	}
	
	public long getTestsFailedCount() {
		return testsFailedCount.get();
	}
	
	public long getTestsErrorCount() {
		return testsErrorCount.get();
	}
	
	public long getTestsEndedCount() {
		return testsEndedCount.get();
	}
	
}
