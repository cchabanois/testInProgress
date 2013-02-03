package de.oschoen.junit.runner.testExcludeClass;

import org.junit.Test;

import static org.junit.Assert.fail;

public class ShouldBeIgnoredBecauseNameEndsOfExcludeTest {

    @Test
    public void shouldBeFail() {
        fail();
    }

}