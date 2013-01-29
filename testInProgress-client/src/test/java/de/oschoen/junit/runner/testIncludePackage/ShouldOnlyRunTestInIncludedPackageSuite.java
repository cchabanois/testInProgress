package de.oschoen.junit.runner.testIncludePackage;

import de.oschoen.junit.runner.BatchTestRunner;
import org.junit.runner.RunWith;

@RunWith(BatchTestRunner.class)
@BatchTestRunner.BatchTestInclude("**.shouldBeFound.**")
public class ShouldOnlyRunTestInIncludedPackageSuite {
}