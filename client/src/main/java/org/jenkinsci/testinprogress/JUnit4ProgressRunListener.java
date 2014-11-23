package org.jenkinsci.testinprogress;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.jenkinsci.testinprogress.messagesender.IMessageSenderFactory;
import org.jenkinsci.testinprogress.messagesender.MessageSender;
import org.jenkinsci.testinprogress.messagesender.SocketMessageSenderFactory;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * Using <code>JUnit4ProgressRunListener</code> as a {@link RunListener} allows
 * you to send test unit messages to the jenkins plugin that will display
 * progress as the build is running.
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public class JUnit4ProgressRunListener extends RunListener {
	private MessageSender messageSender;
	private IMessageSenderFactory messageSenderFactory;
	private Map<Description, String> testIds = new HashMap<Description, String>();
	private AtomicLong atomicLong = new AtomicLong(0);
	private long startTime;

	public JUnit4ProgressRunListener() {
		this.messageSenderFactory = new SocketMessageSenderFactory();
	}

	public JUnit4ProgressRunListener(IMessageSenderFactory messageSenderFactory) {
		this.messageSenderFactory = messageSenderFactory;
	}

	@Override
	public void testRunStarted(Description description) throws Exception {
		messageSender = messageSenderFactory.getMessageSender();
		messageSender.init();
		messageSender.testRunStarted();
		sendTestTree(null, description);
		startTime = System.currentTimeMillis();
	}

	private void sendTestTree(String parentId, Description description) throws IOException {
		String id = getTestId(description);
		messageSender.testTree(id, description.getDisplayName(), parentId,description
				.isSuite());
		for (Description childDescription : description.getChildren()) {
			sendTestTree(id, childDescription);
		}
	}

	private synchronized String getTestId(Description description) {
		String test = testIds.get(description);
		if (test == null) {
			test = Long.toString(atomicLong.incrementAndGet());
			testIds.put(description, test);
		}
		return test;
	}

	@Override
	public void testRunFinished(Result result) throws Exception {
		long stopTime = System.currentTimeMillis();
		messageSender.testRunEnded(stopTime - startTime);
		messageSender.shutdown();
	}

	@Override
	public void testStarted(Description description) throws Exception {
		String id = getTestId(description);
		messageSender.testStarted(id, description.getDisplayName(), false);
	}

	@Override
	public void testFailure(Failure failure) throws Exception {
		String id = getTestId(failure.getDescription());
		Throwable exception = failure.getException();
		if (exception instanceof AssertionError) {
			String expected = null;
			String actual = null;
			if (exception instanceof junit.framework.ComparisonFailure) {
				junit.framework.ComparisonFailure comparisonFailure = (junit.framework.ComparisonFailure) exception;
				expected = comparisonFailure.getExpected();
				actual = comparisonFailure.getActual();
			} else if (exception instanceof org.junit.ComparisonFailure) {
				org.junit.ComparisonFailure comparisonFailure = (org.junit.ComparisonFailure) exception;
				expected = comparisonFailure.getExpected();
				actual = comparisonFailure.getActual();
			}
			messageSender.testFailed(id, failure.getDescription()
					.getDisplayName(), expected, actual, failure.getTrace());
		} else {
			messageSender.testError(id, failure.getDescription()
					.getDisplayName(), failure.getTrace());
		}

	}

	@Override
	public void testFinished(Description description) throws Exception {
		String id = getTestId(description);
		messageSender.testEnded(id, description.getDisplayName(), false);
	}

	@Override
	public void testIgnored(Description description) throws Exception {
		String id = getTestId(description);
		messageSender.testStarted(id, description.getDisplayName(), true);
		messageSender.testEnded(id, description.getDisplayName(), true);
	}
	
	@Override
	public void testAssumptionFailure(Failure failure) {
		Description description = failure.getDescription();
		String id = getTestId(description);
		try {
			messageSender.testAssumptionFailed(id, description.getDisplayName(), failure.getTrace());
		} catch (IOException e) {
			// don't know why testAssumptionFailure does not throw Exception like other methods ... 
			throw new RuntimeException(e);
		}
	}
	
}
