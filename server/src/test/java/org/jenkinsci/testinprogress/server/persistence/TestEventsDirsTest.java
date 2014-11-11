package org.jenkinsci.testinprogress.server.persistence;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.jenkinsci.testinprogress.server.persistence.TestEventsDirs;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestEventsDirsTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void testNoDirs() {
		// Given
		TestEventsDirs testEventsDirs = new TestEventsDirs(tempFolder.getRoot());

		// When
		List<File> dirs = testEventsDirs.getBuildTestEventsDirs();

		// Then
		assertEquals(0, dirs.size());
	}

	@Test
	public void testDirsAreOrderedByCreationOrder() throws Exception {
		// Given
		TestEventsDirs testEventsDirs = new TestEventsDirs(tempFolder.getRoot());
		File file1 = testEventsDirs.createBuildTestEventsDir();
		Thread.sleep(1000);
		File file2 = testEventsDirs.createBuildTestEventsDir();
		Thread.sleep(1000);
		File file3 = testEventsDirs.createBuildTestEventsDir();

		// When
		List<File> dirs = testEventsDirs.getBuildTestEventsDirs();

		// Then
		assertEquals(3, dirs.size());
		assertEquals(file1, dirs.get(0));
		assertEquals(file2, dirs.get(1));
		assertEquals(file3, dirs.get(2));
	}

}
