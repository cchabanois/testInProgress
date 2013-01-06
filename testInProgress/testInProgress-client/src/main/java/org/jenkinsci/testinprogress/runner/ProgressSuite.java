package org.jenkinsci.testinprogress.runner;

import org.jenkinsci.testinprogress.JUnit4ProgressRunListener;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

/**
 * Using <code>ProgressSuite</code> as a runner allows you to send test unit
 * messages to the jenkins plugin that will display progress as the build is
 * running.
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public class ProgressSuite extends Suite {

	/**
	 * Called reflectively on classes annotated with
	 * <code>@RunWith(ProgressSuite.class)</code>
	 * 
	 * @param klass
	 *            the root class
	 * @param builder
	 *            builds runners for classes in the suite
	 * @throws InitializationError
	 */
	public ProgressSuite(Class<?> klass, RunnerBuilder builder)
			throws InitializationError {
		super(klass, builder);
	}

	@Override
	public void run(RunNotifier notifier) {
		Description description = getDescription();
		Result result = new Result();
		RunListener resultRunListener = result.createListener();
		JUnit4ProgressRunListener listener = new JUnit4ProgressRunListener();
		// notifier.fireTestRunStarted was called just before ProgressSuite.run
		fireTestRunStarted(new RunListener[] { resultRunListener, listener },
				description);

		notifier.addFirstListener(resultRunListener);
		notifier.addListener(listener);
		try {
			super.run(notifier);
		} finally {
			fireTestRunFinished(
					new RunListener[] { resultRunListener, listener }, result);
			notifier.removeListener(resultRunListener);
			notifier.removeListener(listener);
		}
	}

	private void fireTestRunStarted(RunListener[] runListeners,
			Description description) {
		for (RunListener runListener : runListeners) {
			try {
				runListener.testRunStarted(description);
			} catch (Exception e) {
			}
		}
	}

	private void fireTestRunFinished(RunListener[] runListeners, Result result) {
		for (RunListener runListener : runListeners) {
			try {
				runListener.testRunFinished(result);
			} catch (Exception e) {
			}
		}
	}

}
