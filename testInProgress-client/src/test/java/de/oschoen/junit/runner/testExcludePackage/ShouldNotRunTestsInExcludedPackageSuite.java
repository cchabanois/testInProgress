package de.oschoen.junit.runner.testExcludePackage;

import de.oschoen.junit.runner.BatchTestRunner;
import org.junit.runner.RunWith;

@RunWith(BatchTestRunner.class)
@BatchTestRunner.BatchTestExclude("**.shouldBeIgnored.**")
public class ShouldNotRunTestsInExcludedPackageSuite {
}
