package tests;

import java.util.Random;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DynamicTestSuite extends TestSuite {
	private final DynamicSuiteConfig config;
	private final int level;
	
	public DynamicTestSuite(DynamicSuiteConfig config) {
		this(config, 0, config.remainingTests);
	}
	
	public DynamicTestSuite(DynamicSuiteConfig config, int level, int testCount) {
		this.config = config;
		this.level = level;
		setName("suite-"+config.nextId());
		addTests(testCount);
	}
	
	
	private void addTests(int testCount) {
		if (testCount == 0) {
			return;
		}
		for (int i = 0; i < testCount; i++) {
			if (config.remainingTests == 0) {
				return;
			}
			if (level < config.maxLevel && config.randomInt(100) < config.percentSuites) {
				addTest(new DynamicTestSuite(config, level+1, config.randomInt(config.maxChildren)+1));
			} else {
				addTest(new DynamicTestCase(config));
				config.testAdded();
			}
		}
	}
	
	public static Test suite() {
		return new DynamicTestSuite(new DynamicSuiteConfig(10000, 20, 10, 4,50));
	}
	
	public static class DynamicTestCase extends TestCase {
		private final DynamicSuiteConfig config;
		
		public DynamicTestCase(DynamicSuiteConfig config) {
			super();
			this.config = config;
			setName("test-"+config.nextId());
		}
		
		@Override
		protected void runTest() throws Throwable {
			Thread.sleep(config.sleepTime);
			assertEquals(1,1);
		}
		
	}
	
	static class DynamicSuiteConfig {
		private final Random random = new Random();
		private int nextId;
		int remainingTests;
		final int percentSuites;
		final int maxChildren;
		private final long sleepTime;
		final int maxLevel;
		
		public DynamicSuiteConfig(int numTests, int percentSuites, int maxChildren, int maxLevel, long sleepTime) {
			this.remainingTests = numTests;
			this.percentSuites = percentSuites;
			this.maxChildren = maxChildren;
			this.maxLevel = maxLevel;
			this.sleepTime = sleepTime;
		}
		
		public int nextId() {
			return nextId++;
		}
		
		public void testAdded() {
			remainingTests--;
		}
		
		public int randomInt(int n) {
			return random.nextInt(n);
		}
		
	}	
	
}