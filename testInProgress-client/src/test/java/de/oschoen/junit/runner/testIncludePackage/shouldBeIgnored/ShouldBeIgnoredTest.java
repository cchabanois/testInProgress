package de.oschoen.junit.runner.testIncludePackage.shouldBeIgnored;

import org.junit.Assert;
import org.junit.Test;

public class ShouldBeIgnoredTest {

    @Test
    public void shouldFail() {
        Assert.fail();
    }
}