package tests;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class RuleErrorTest {

	@Rule
	public MyRule rule = new MyRule();
	
	
	@Test
	public void test() {
		
	}
	
	
	private static class MyRule implements TestRule {

		public Statement apply(Statement base, Description description) {
			throw new RuntimeException("buggy rule");
		}
		
	}
	
	
}
