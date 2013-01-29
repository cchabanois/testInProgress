package de.oschoen.junit.runner.testIncludeClass;

import org.junit.Test;

import static org.junit.Assert.fail;

public class ShouldNotFoundBecauseEndsOfTest {

    @Test
    public void shouldFail() {
        fail();
    }
}
