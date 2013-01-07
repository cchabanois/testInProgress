package tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CalcTest2 {

	@Test
	public void testStringCompare() {
		assertEquals("5", "2"+"3");
	}
	
	@Test
	public void testDivideBy0() {
		assertEquals(0, 5/0);
	}
	
}
