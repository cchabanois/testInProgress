package tests;

import junit.framework.TestSuite;

import org.jenkinsci.testinprogress.runner.ProgressSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(ProgressSuite.class)
@SuiteClasses({ DynamicTestSuite.class })
public class ProgressDynamicTestSuite extends TestSuite {
	

	
}
