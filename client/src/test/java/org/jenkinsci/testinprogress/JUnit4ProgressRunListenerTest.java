package org.jenkinsci.testinprogress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.jenkinsci.testinprogress.messagesender.SimpleMessageSenderFactory;
import org.junit.Test;
import org.junit.runner.JUnitCore;

import tests.AssumptionNotVerifiedTest;
import tests.CalcTestsSuite;
import tests.EmptyTest;
import tests.IgnoredTest;
import tests.InitializationErrorTest;
import tests.ParallelSuiteTest;
import tests.RuleErrorTest;
import tests.SameTestsSuite;


public class JUnit4ProgressRunListenerTest {

	@Test
	public void testFirstMessageIsTestRunStart() {
		String[] messages = runTests(CalcTestsSuite.class);
		
		assertThat(messages[0], containsString("{\"testCount\":6,\"messageId\":\"%TESTC\",\"fVersion\":\"v2\"}"));
	}
	
	@Test
	public void testLatestMessageIsTestRunEnd() {
		String[] messages = runTests(CalcTestsSuite.class);
		assertThat(messages[messages.length-1], containsString("%RUNTIME"));
	}
	
	/**
	 * TODO : If the same test is run several times in the suite, we use the same id ...
	 */
	@Test
	public void testSameTestMultipleTimes() {
		String[] messages = runTests(SameTestsSuite.class);
		List<String> matchingMessages = getTestMessagesMatching(messages, "{\"parentId\":\"\",\"testName\":\"tests.CalcTestsSuite\",\"testId\":\"3\",\"testCount\":2,\"messageId\":\"%TSTTREE\",\"isSuite\":true,\"parentName\":\"\"}");
		assertEquals(2,matchingMessages.size());
	}
	
	@Test
	public void testInitializationErrorTest() {
		String[] messages = runTests(InitializationErrorTest.class);
		assertNotNull(getTestMessageMatching(messages, "{\"parentId\":\"\",\"testName\":\"initializationError(tests.InitializationErrorTest)\",\"testId\":\"3\",\"testCount\":1,\"messageId\":\"%TSTTREE\",\"isSuite\":false,\"parentName\":\"\"}"));
		assertNotNull(getTestMessageMatching(messages, "{\"errorTrace\":\"java.lang.Exception: Method testException should have no parameters\r\n\tat org.junit.runners.model.FrameworkMethod.validatePublicVoidNoArg(FrameworkMethod.java:69)\r\n\tat org.junit.runners.ParentRunner.validatePublicVoidNoArgMethods(ParentRunner.java:131)\r\n\tat org.junit.runners.BlockJUnit4ClassRunner.validateTestMethods(BlockJUnit4ClassRunner.java:178)\r\n\tat org.junit.runners.BlockJUnit4ClassRunner.validateInstanceMethods(BlockJUnit4ClassRunner.java:163)\r\n\tat org.junit.runners.BlockJUnit4ClassRunner.collectInitializationErrors(BlockJUnit4ClassRunner.java:102)\r\n\tat org.junit.runners.ParentRunner.validate(ParentRunner.java:344)\r\n\tat org.junit.runners.ParentRunner.<init>(ParentRunner.java:74)\r\n\tat org.junit.runners.BlockJUnit4ClassRunner.<init>(BlockJUnit4ClassRunner.java:55)\r\n\tat org.junit.internal.builders.JUnit4Builder.runnerForClass(JUnit4Builder.java:13)\r\n\tat org.junit.runners.model.RunnerBuilder.safeRunnerForClass(RunnerBuilder.java:57)\r\n\tat org.junit.internal.builders.AllDefaultPossibilitiesBuilder.runnerForClass(AllDefaultPossibilitiesBuilder.java:29)\r\n\tat org.junit.runner.Computer.getRunner(Computer.java:38)\r\n\tat org.junit.runner.Computer$1.runnerForClass(Computer.java:29)\r\n\tat org.junit.runners.model.RunnerBuilder.safeRunnerForClass(RunnerBuilder.java:57)\r\n\tat org.junit.runners.model.RunnerBuilder.runners(RunnerBuilder.java:98)\r\n\tat org.junit.runners.model.RunnerBuilder.runners(RunnerBuilder.java:84)\r\n\tat org.junit.runners.Suite.<init>(Suite.java:79)\r\n\tat org.junit.runner.Computer.getSuite(Computer.java:26)\r\n\tat org.junit.runner.Request.classes(Request.java:69)\r\n\tat org.junit.runner.JUnitCore.run(JUnitCore.java:117)\r\n\tat org.jenkinsci.testinprogress.JUnit4ProgressRunListenerTest.runTests(JUnit4ProgressRunListenerTest.java:127)\r\n\tat org.jenkinsci.testinprogress.JUnit4ProgressRunListenerTest.testInitializationErrorTest(JUnit4ProgressRunListenerTest.java:55)\r\n\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n\tat sun.reflect.NativeMethodAccessorImpl.invoke(Unknown Source)\r\n\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(Unknown Source)\r\n\tat java.lang.reflect.Method.invoke(Unknown Source)\r\n\tat org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:45)\r\n\tat org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)\r\n\tat org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:42)\r\n\tat org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:20)\r\n\tat org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:263)\r\n\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:68)\r\n\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:47)\r\n\tat org.junit.runners.ParentRunner$3.run(ParentRunner.java:231)\r\n\tat org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:60)\r\n\tat org.junit.runners.ParentRunner.runChildren(ParentRunner.java:229)\r\n\tat org.junit.runners.ParentRunner.access$000(ParentRunner.java:50)\r\n\tat org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:222)\r\n\tat org.junit.runners.ParentRunner.run(ParentRunner.java:300)\r\n\tat org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:50)\r\n\tat org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)\r\n\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:467)\r\n\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:683)\r\n\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:390)\r\n\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:197)\r\n\n\",\"testName\":\"initializationError(tests.InitializationErrorTest)\",\"testId\":\"3\",\"messageId\":\"%ERROR\"}"));
	}
	
	@Test
	public void testRuleErrorTest() {
		String[] messages = runTests(RuleErrorTest.class);
		// error is associated with a suite, not a test
		assertNotNull(getTestMessageMatching(messages, "{\"parentId\":\"\",\"testName\":\"tests.RuleErrorTest\",\"testId\":\"2\",\"testCount\":1,\"messageId\":\"%TSTTREE\",\"isSuite\":true,\"parentName\":\"\"}"));
		assertNotNull(getTestMessageMatching(messages, "{\"errorTrace\":\"java.lang.RuntimeException: buggy rule\r\n\tat tests.RuleErrorTest$MyRule.apply(RuleErrorTest.java:24)\r\n\tat org.junit.rules.RunRules.applyAll(RunRules.java:24)\r\n\tat org.junit.rules.RunRules.<init>(RunRules.java:13)\r\n\tat org.junit.runners.BlockJUnit4ClassRunner.withTestRules(BlockJUnit4ClassRunner.java:376)\r\n\tat org.junit.runners.BlockJUnit4ClassRunner.withRules(BlockJUnit4ClassRunner.java:331)\r\n\tat org.junit.runners.BlockJUnit4ClassRunner.methodBlock(BlockJUnit4ClassRunner.java:248)\r\n\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:68)\r\n\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:47)\r\n\tat org.junit.runners.ParentRunner$3.run(ParentRunner.java:231)\r\n\tat org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:60)\r\n\tat org.junit.runners.ParentRunner.runChildren(ParentRunner.java:229)\r\n\tat org.junit.runners.ParentRunner.access$000(ParentRunner.java:50)\r\n\tat org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:222)\r\n\tat org.junit.runners.ParentRunner.run(ParentRunner.java:300)\r\n\tat org.junit.runners.Suite.runChild(Suite.java:128)\r\n\tat org.junit.runners.Suite.runChild(Suite.java:24)\r\n\tat org.junit.runners.ParentRunner$3.run(ParentRunner.java:231)\r\n\tat org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:60)\r\n\tat org.junit.runners.ParentRunner.runChildren(ParentRunner.java:229)\r\n\tat org.junit.runners.ParentRunner.access$000(ParentRunner.java:50)\r\n\tat org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:222)\r\n\tat org.junit.runners.ParentRunner.run(ParentRunner.java:300)\r\n\tat org.junit.runner.JUnitCore.run(JUnitCore.java:157)\r\n\tat org.junit.runner.JUnitCore.run(JUnitCore.java:136)\r\n\tat org.junit.runner.JUnitCore.run(JUnitCore.java:117)\r\n\tat org.jenkinsci.testinprogress.JUnit4ProgressRunListenerTest.runTests(JUnit4ProgressRunListenerTest.java:127)\r\n\tat org.jenkinsci.testinprogress.JUnit4ProgressRunListenerTest.testRuleErrorTest(JUnit4ProgressRunListenerTest.java:62)\r\n\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n\tat sun.reflect.NativeMethodAccessorImpl.invoke(Unknown Source)\r\n\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(Unknown Source)\r\n\tat java.lang.reflect.Method.invoke(Unknown Source)\r\n\tat org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:45)\r\n\tat org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)\r\n\tat org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:42)\r\n\tat org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:20)\r\n\tat org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:263)\r\n\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:68)\r\n\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:47)\r\n\tat org.junit.runners.ParentRunner$3.run(ParentRunner.java:231)\r\n\tat org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:60)\r\n\tat org.junit.runners.ParentRunner.runChildren(ParentRunner.java:229)\r\n\tat org.junit.runners.ParentRunner.access$000(ParentRunner.java:50)\r\n\tat org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:222)\r\n\tat org.junit.runners.ParentRunner.run(ParentRunner.java:300)\r\n\tat org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:50)\r\n\tat org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)\r\n\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:467)\r\n\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:683)\r\n\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:390)\r\n\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:197)\r\n\n\",\"testName\":\"tests.RuleErrorTest\",\"testId\":\"2\",\"messageId\":\"%ERROR\"}"));
	}
	
	@Test
	public void testEmptyTest() {
		String[] messages = runTests(EmptyTest.class);
		assertNotNull(getTestMessageMatching(messages, "{\"parentId\":\"\",\"testName\":\"initializationError(tests.EmptyTest)\",\"testId\":\"3\",\"testCount\":1,\"messageId\":\"%TSTTREE\",\"isSuite\":false,\"parentName\":\"\"}"));
		assertNotNull(getTestMessageMatching(messages, "{\"errorTrace\":\"java.lang.Exception: No runnable methods\r\n\tat org.junit.runners.BlockJUnit4ClassRunner.validateInstanceMethods(BlockJUnit4ClassRunner.java:166)\r\n\tat org.junit.runners.BlockJUnit4ClassRunner.collectInitializationErrors(BlockJUnit4ClassRunner.java:102)\r\n\tat org.junit.runners.ParentRunner.validate(ParentRunner.java:344)\r\n\tat org.junit.runners.ParentRunner.<init>(ParentRunner.java:74)\r\n\tat org.junit.runners.BlockJUnit4ClassRunner.<init>(BlockJUnit4ClassRunner.java:55)\r\n\tat org.junit.internal.builders.JUnit4Builder.runnerForClass(JUnit4Builder.java:13)\r\n\tat org.junit.runners.model.RunnerBuilder.safeRunnerForClass(RunnerBuilder.java:57)\r\n\tat org.junit.internal.builders.AllDefaultPossibilitiesBuilder.runnerForClass(AllDefaultPossibilitiesBuilder.java:29)\r\n\tat org.junit.runner.Computer.getRunner(Computer.java:38)\r\n\tat org.junit.runner.Computer$1.runnerForClass(Computer.java:29)\r\n\tat org.junit.runners.model.RunnerBuilder.safeRunnerForClass(RunnerBuilder.java:57)\r\n\tat org.junit.runners.model.RunnerBuilder.runners(RunnerBuilder.java:98)\r\n\tat org.junit.runners.model.RunnerBuilder.runners(RunnerBuilder.java:84)\r\n\tat org.junit.runners.Suite.<init>(Suite.java:79)\r\n\tat org.junit.runner.Computer.getSuite(Computer.java:26)\r\n\tat org.junit.runner.Request.classes(Request.java:69)\r\n\tat org.junit.runner.JUnitCore.run(JUnitCore.java:117)\r\n\tat org.jenkinsci.testinprogress.JUnit4ProgressRunListenerTest.runTests(JUnit4ProgressRunListenerTest.java:127)\r\n\tat org.jenkinsci.testinprogress.JUnit4ProgressRunListenerTest.testEmptyTest(JUnit4ProgressRunListenerTest.java:70)\r\n\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n\tat sun.reflect.NativeMethodAccessorImpl.invoke(Unknown Source)\r\n\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(Unknown Source)\r\n\tat java.lang.reflect.Method.invoke(Unknown Source)\r\n\tat org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:45)\r\n\tat org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)\r\n\tat org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:42)\r\n\tat org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:20)\r\n\tat org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:263)\r\n\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:68)\r\n\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:47)\r\n\tat org.junit.runners.ParentRunner$3.run(ParentRunner.java:231)\r\n\tat org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:60)\r\n\tat org.junit.runners.ParentRunner.runChildren(ParentRunner.java:229)\r\n\tat org.junit.runners.ParentRunner.access$000(ParentRunner.java:50)\r\n\tat org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:222)\r\n\tat org.junit.runners.ParentRunner.run(ParentRunner.java:300)\r\n\tat org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:50)\r\n\tat org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)\r\n\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:467)\r\n\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:683)\r\n\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:390)\r\n\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:197)\r\n\n\",\"testName\":\"initializationError(tests.EmptyTest)\",\"testId\":\"3\",\"messageId\":\"%ERROR\"}"));
	}
	
	@Test
	public void testParallelTests() {
		String[] messages = runTests(ParallelSuiteTest.class);
		printTestMessages(messages);
	}
	
	@Test
	public void testIgnoredTest() {
		String[] messages = runTests(IgnoredTest.class);
		assertNotNull(getTestMessageMatching(messages, "{\"ignored\":true,\"testName\":\"testIgnore(tests.IgnoredTest)\",\"testId\":\"3\",\"messageId\":\"%TESTS\"}"));
		assertNotNull(getTestMessageMatching(messages, "{\"ignored\":true,\"testName\":\"testIgnore(tests.IgnoredTest)\",\"testId\":\"3\",\"messageId\":\"%TESTE\"}"));		
	}
	
	@Test
	public void testAssumptionTest() {
		String[] messages = runTests(AssumptionNotVerifiedTest.class);
		assertNotNull(getTestMessageMatching(messages, "{\"errorTrace\":\"org.junit.internal.AssumptionViolatedException: got: <false>, expected: is <true>\r\n\tat org.junit.Assume.assumeThat(Assume.java:70)\r\n\tat org.junit.Assume.assumeTrue(Assume.java:39)\r\n\tat tests.AssumptionNotVerifiedTest.testAssumptionNotVerified(AssumptionNotVerifiedTest.java:10)\r\n\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n\tat sun.reflect.NativeMethodAccessorImpl.invoke(Unknown Source)\r\n\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(Unknown Source)\r\n\tat java.lang.reflect.Method.invoke(Unknown Source)\r\n\tat org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:45)\r\n\tat org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)\r\n\tat org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:42)\r\n\tat org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:20)\r\n\tat org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:263)\r\n\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:68)\r\n\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:47)\r\n\tat org.junit.runners.ParentRunner$3.run(ParentRunner.java:231)\r\n\tat org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:60)\r\n\tat org.junit.runners.ParentRunner.runChildren(ParentRunner.java:229)\r\n\tat org.junit.runners.ParentRunner.access$000(ParentRunner.java:50)\r\n\tat org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:222)\r\n\tat org.junit.runners.ParentRunner.run(ParentRunner.java:300)\r\n\tat org.junit.runners.Suite.runChild(Suite.java:128)\r\n\tat org.junit.runners.Suite.runChild(Suite.java:24)\r\n\tat org.junit.runners.ParentRunner$3.run(ParentRunner.java:231)\r\n\tat org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:60)\r\n\tat org.junit.runners.ParentRunner.runChildren(ParentRunner.java:229)\r\n\tat org.junit.runners.ParentRunner.access$000(ParentRunner.java:50)\r\n\tat org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:222)\r\n\tat org.junit.runners.ParentRunner.run(ParentRunner.java:300)\r\n\tat org.junit.runner.JUnitCore.run(JUnitCore.java:157)\r\n\tat org.junit.runner.JUnitCore.run(JUnitCore.java:136)\r\n\tat org.junit.runner.JUnitCore.run(JUnitCore.java:117)\r\n\tat org.jenkinsci.testinprogress.JUnit4ProgressRunListenerTest.runTests(JUnit4ProgressRunListenerTest.java:127)\r\n\tat org.jenkinsci.testinprogress.JUnit4ProgressRunListenerTest.testAssumptionTest(JUnit4ProgressRunListenerTest.java:90)\r\n\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n\tat sun.reflect.NativeMethodAccessorImpl.invoke(Unknown Source)\r\n\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(Unknown Source)\r\n\tat java.lang.reflect.Method.invoke(Unknown Source)\r\n\tat org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:45)\r\n\tat org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)\r\n\tat org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:42)\r\n\tat org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:20)\r\n\tat org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:263)\r\n\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:68)\r\n\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:47)\r\n\tat org.junit.runners.ParentRunner$3.run(ParentRunner.java:231)\r\n\tat org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:60)\r\n\tat org.junit.runners.ParentRunner.runChildren(ParentRunner.java:229)\r\n\tat org.junit.runners.ParentRunner.access$000(ParentRunner.java:50)\r\n\tat org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:222)\r\n\tat org.junit.runners.ParentRunner.run(ParentRunner.java:300)\r\n\tat org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:50)\r\n\tat org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)\r\n\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:467)\r\n\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:683)\r\n\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:390)\r\n\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:197)\r\n\n\",\"testName\":\"testAssumptionNotVerified(tests.AssumptionNotVerifiedTest)\",\"testId\":\"3\",\"assumptionFailed\":true,\"messageId\":\"%FAILED\"}"));	
	}
	
	private String getTestMessageMatching(String[] messages, String regex) {
		List<String> testMatchings = getTestMessagesMatching(messages, regex);
		if (testMatchings.size() == 0) {
			return null;
		} else if (testMatchings.size() == 1) {
			return testMatchings.get(0);
		} else {
			fail("More than one message matching "+regex);
			return null;
		}
	}
	
	private List<String> getTestMessagesMatching(String[] messages, String expectedMessage)  {
		List<String> result = new ArrayList<String>();
		for (String message : messages) {
			if (message.equals(expectedMessage)) {
				result.add(message);
			}
		}
		return result;
	}
	
	private void printTestMessages(String[] messages) {
		for (String message : messages) {
			System.out.println(message);
		}
	}
	
	private String[] runTests(Class<?>... classes) {
		JUnitCore core= new JUnitCore();
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		core.addListener(new JUnit4ProgressRunListener(new SimpleMessageSenderFactory(pw)));
		core.run(classes);
		return sw.toString().split("\n");
	}
	
	
}
