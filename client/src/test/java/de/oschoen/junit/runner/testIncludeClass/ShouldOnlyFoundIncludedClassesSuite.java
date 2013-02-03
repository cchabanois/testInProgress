package de.oschoen.junit.runner.testIncludeClass;

import de.oschoen.junit.runner.BatchTestRunner;
import org.junit.runner.RunWith;


@RunWith(BatchTestRunner.class)
@BatchTestRunner.BatchTestInclude("**.*Include")
public class ShouldOnlyFoundIncludedClassesSuite {
}
