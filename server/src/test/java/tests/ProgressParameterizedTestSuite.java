package tests;

import org.jenkinsci.testinprogress.runner.ProgressSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(ProgressSuite.class)
@SuiteClasses({ ParameterizedTest.class })
public class ProgressParameterizedTestSuite {

}
