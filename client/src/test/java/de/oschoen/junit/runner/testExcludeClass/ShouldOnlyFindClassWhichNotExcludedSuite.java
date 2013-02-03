package de.oschoen.junit.runner.testExcludeClass;

import de.oschoen.junit.runner.BatchTestRunner;
import org.junit.runner.RunWith;

@RunWith(BatchTestRunner.class)
@BatchTestRunner.BatchTestExclude("**.*ExcludeTest")
public class ShouldOnlyFindClassWhichNotExcludedSuite {
}
