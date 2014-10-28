package tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(org.kohsuke.junit4.ParallelSuite.class)
@SuiteClasses({ CalcTest.class, CalcTest2.class })
public class ParallelSuiteTest {

}
