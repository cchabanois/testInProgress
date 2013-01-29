package de.oschoen.junit.runner.testExcludePackage.shouldBeIgnored;

import org.junit.Assert;
import org.junit.Test;

public class ShouldBeIgnoredTest {

    @Test
    public void shouldFail() {
        Assert.fail();
    }
}
