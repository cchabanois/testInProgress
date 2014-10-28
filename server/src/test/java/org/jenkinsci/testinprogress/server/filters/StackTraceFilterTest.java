package org.jenkinsci.testinprogress.server.filters;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StackTraceFilterTest {

	@Test
	public void testStackTraceFilter() {
		// Given
		StackTraceFilter stackTraceFilter = new StackTraceFilter();
		String originalTrace = "java.lang.AssertionError: expected:<6> but was:<5>\n"
				+ "at org.junit.Assert.fail(Assert.java:93)\n"
				+ "at org.junit.Assert.failNotEquals(Assert.java:647)\n"
				+ "at org.junit.Assert.assertEquals(Assert.java:128)\n"
				+ "at org.junit.Assert.assertEquals(Assert.java:472)\n"
				+ "at org.junit.Assert.assertEquals(Assert.java:456)\n"
				+ "at testproject.CalcTest.testAddWillFail(CalcTest.java:20)\n"
				+ "at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n"
				+ "at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)\n"
				+ "at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n"
				+ "at java.lang.reflect.Method.invoke(Method.java:601)\n"
				+ "at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:45)\n"
				+ "at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)\n"
				+ "at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:42)\n"
				+ "at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:20)\n"
				+ "at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:263)\n"
				+ "at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:68)\n"
				+ "at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:47)\n"
				+ "at org.junit.runners.ParentRunner$3.run(ParentRunner.java:231)\n"
				+ "at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:60)\n"
				+ "at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:229)\n"
				+ "at org.junit.runners.ParentRunner.access$000(ParentRunner.java:50)\n"
				+ "at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:222)\n"
				+ "at org.junit.runners.ParentRunner.run(ParentRunner.java:300)\n"
				+ "at org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:50)\n"
				+ "at org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)\n"
				+ "at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:467)\n"
				+ "at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:683)\n"
				+ "at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:390)\n"
				+ "at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:197)";

		// When
		String newTrace = stackTraceFilter.filter(originalTrace);

		// Then
		String lineSeparator = System.getProperty("line.separator");
		assertEquals("java.lang.AssertionError: expected:<6> but was:<5>"+lineSeparator
				+ "at testproject.CalcTest.testAddWillFail(CalcTest.java:20)"+lineSeparator,
				newTrace);
	}

}
