package de.oschoen.junit.runner.testDefault;

import org.junit.Test;

import static org.junit.Assert.fail;

public class ShouldBeIgnored {
    
    @Test
    public void shouldBeFail() {
        fail();
    }
        
}
