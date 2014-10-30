package tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ParameterizedTest {
	// Fields
	private int datum;
	private int expectedResult;

	public ParameterizedTest(int datum, int expected) {
		this.datum = datum;
		this.expectedResult = expected;
	}

	@Parameters
	public static Collection<Object[]> generateData() {
		Random random = new Random(System.nanoTime());
		List<Object[]> data = new ArrayList<Object[]>();
		for (int i = 0; i < getNumTests(); i++) {
			data.add(new Object[]{random.nextInt(4), random.nextInt(4)});
		}
		return data;
	}

	private static int getNumTests() {
		String asStr = System.getProperty("numTests");
		if (asStr == null) {
			return 1000;
		}
		try {
			return Integer.parseInt(asStr);
		} catch (NumberFormatException e) {
			return 1000;
		}
	}
	
	@Test
	public void testWhatever() {
		try {
			Thread.sleep(this.expectedResult*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertEquals(this.expectedResult, this.datum);
	}
}
