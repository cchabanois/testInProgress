package tests;

import org.junit.Test;
import static org.junit.Assume.*;

public class AssumptionNotVerifiedTest {

	@Test
	public void testAssumptionNotVerified() {
		assumeTrue(false);
	}
	
}
