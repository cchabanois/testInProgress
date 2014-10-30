package tests;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class ParameterizedTest2 extends ParameterizedTest {

	public ParameterizedTest2(int datum, int expected) {
		super(datum, expected);
	}

}
