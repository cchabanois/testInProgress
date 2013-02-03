package org.jenkinsci.testinprogress.runner;

import org.jenkinsci.testinprogress.JUnit4ProgressRunListener;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import de.oschoen.junit.runner.BatchTestRunner;

/**
 * Runner that combines {@link ProgressSuite} and {@link BatchTestRunner}
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public class ProgressBatchSuite extends BatchTestRunner {

	/**
	 * Called reflectively on classes annotated with
	 * <code>@RunWith(BatchTestRunner.class)</code>
	 * 
	 * @param klass
	 *            the root class
	 * @param builder
	 *            builds runners for classes in the suite
	 * @throws InitializationError
	 */
	public ProgressBatchSuite(Class<?> klass, RunnerBuilder builder)
			throws InitializationError {
		super(klass, builder);
	}

	/**
	 * Call this when there is no single root class (for example, multiple class
	 * names passed on the command line to {@link org.junit.runner.JUnitCore}
	 * 
	 * @param builder
	 *            builds runners for classes in the suite
	 * @param classes
	 *            the classes in the suite
	 * @throws InitializationError
	 */
	public ProgressBatchSuite(RunnerBuilder builder, Class<?>[] classes)
			throws InitializationError {
		super(builder, classes);
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
