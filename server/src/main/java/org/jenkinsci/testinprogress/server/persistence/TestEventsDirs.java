package org.jenkinsci.testinprogress.server.persistence;

import java.io.File;
import java.io.FileFilter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 *
 */
public class TestEventsDirs {
	private static final String PREFIX = "testEvents-";
	private final File rootDir;

	public TestEventsDirs(File rootDir) {
		this.rootDir = rootDir;
	}

	public List<File> getBuildTestEventsDirs() {
		List<File> eventsDirs = Arrays.asList(rootDir
				.listFiles(new FileFilter() {

					public boolean accept(File pathname) {
						return pathname.isDirectory()
								&& pathname.getName().startsWith(PREFIX);
					}
				}));
		Collections.sort(eventsDirs, new Comparator<File>() {

			public int compare(File file1, File file2) {
				return file1.getName().compareTo(file2.getName());
			}
		});
		return eventsDirs;
	}

	/**
	 * Get the dir for latest build
	 * @return the dir for latest build or null if none
	 */
	public File getLatestBuildTestEventsDir() {
		List<File> buildTestEventsDirs = getBuildTestEventsDirs();
		if (buildTestEventsDirs.isEmpty()) {
			return null;
		} else {
			return buildTestEventsDirs.get(buildTestEventsDirs.size()-1);
		}
	}
	
	/**
	 * Create a dir to save test events for current build
	 * @return
	 */
	public File createBuildTestEventsDir() {
		TimeZone tz = TimeZone.getDefault();
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		df.setTimeZone(tz);
		String now = df.format(new Date());
		String dirName = PREFIX + now;
		File dir = new File(rootDir, dirName);
		dir.mkdir();
		return dir;
	}

}
