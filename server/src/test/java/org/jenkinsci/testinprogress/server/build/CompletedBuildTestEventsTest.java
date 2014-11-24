package org.jenkinsci.testinprogress.server.build;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.jenkinsci.testinprogress.server.events.build.BuildTestEvent;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.io.Files;

public class CompletedBuildTestEventsTest {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	
	
	@Test
	public void testEventsOrderedByTimestamp() throws Exception {
		// Given
		File testEventsDir = createTestEventsDir();
		CompletedBuildTestEvents completedBuildTestEvents = new CompletedBuildTestEvents(testEventsDir);
	
		// When
		List<BuildTestEvent> buildTestEvents = completedBuildTestEvents.getEvents();
		
		// Then
		assertEquals(1, buildTestEvents.get(0).getRunTestEvent().getTimestamp());
		assertEquals(2, buildTestEvents.get(1).getRunTestEvent().getTimestamp());
		assertEquals(3, buildTestEvents.get(2).getRunTestEvent().getTimestamp());
		assertEquals(4, buildTestEvents.get(3).getRunTestEvent().getTimestamp());
	}
	
	private File createTestEventsDir() throws IOException {
		File testEventsDir = temporaryFolder.newFolder();
		File file1 = new File(testEventsDir, "file1.events");
		Files.write(""
				+ "{'timeStamp':1,'messageId':'TESTC','fVersion':'v3'}\n"
				+ "{'testName':'tests.ProgressCalcTestsSuite','testId':'1','timeStamp':3,'messageId':'TSTTREE','isSuite':true}"
				, file1, Charset.forName("UTF-8"));
		
		File file2 = new File(testEventsDir, "file2.events");
		Files.write(""
				+ "{'timeStamp':2,'messageId':'TESTC','fVersion':'v3'}\n"
				+ "{'testName':'tests.ProgressCalcTestsSuite2','testId':'4','timeStamp':4,'messageId':'TSTTREE','isSuite':true}"
				, file2, Charset.forName("UTF-8"));
		return testEventsDir;
	}
	
}
