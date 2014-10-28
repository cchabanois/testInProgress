package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;

public class CalcTest {
	
	@Test
	public void testAddWillSucceed() {
		assertEquals(5, 2+3);
	}
	
	@Test
	public void testAddWillFail() {
		assertEquals(6, 3+5);
	}	

	@Test
	@Ignore
	public void testIgnored() {
	}
	
	@Test
	public void testFail() {
		fail("this test failed");
	}
	
}
