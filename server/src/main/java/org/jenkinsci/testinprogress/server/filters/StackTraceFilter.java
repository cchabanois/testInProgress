package org.jenkinsci.testinprogress.server.filters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * copied from org.eclipse.jdt.internal.junit.ui.TextualTrace
 * 
 * 
 */
public class StackTraceFilter {
	private static final String FRAME_PREFIX = "at ";
	private final String[] filterPatterns;
	public final static String[] DEFAULT_FILTER_PATTERNS = new String[] {
			"org.junit.*", "sun.reflect.*", "java.lang.reflect.Method.invoke",
			"junit.framework.Assert", "junit.framework.TestCase",
			"junit.framework.TestResult", "junit.framework.TestResult$1",
			"junit.framework.TestSuite", "org.eclipse.jdt.internal.*",
			"org.apache.tools.ant.*", "org.jenkinsci.testinprogress.*" };

	public StackTraceFilter() {
		this(DEFAULT_FILTER_PATTERNS);
	}

	public StackTraceFilter(String[] filterPatterns) {
		this.filterPatterns = filterPatterns;
	}

	private boolean filterLine(String line) {
		String pattern;
		int len;
		for (int i = (filterPatterns.length - 1); i >= 0; --i) {
			pattern = filterPatterns[i];
			len = pattern.length() - 1;
			if (pattern.charAt(len) == '*') {
				// strip trailing * from a package filter
				pattern = pattern.substring(0, len);
			} else if (Character.isUpperCase(pattern.charAt(0))) {
				// class in the default package
				pattern = FRAME_PREFIX + pattern + '.';
			} else {
				// class names start w/ an uppercase letter after the .
				final int lastDotIndex = pattern.lastIndexOf('.');
				if ((lastDotIndex != -1)
						&& (lastDotIndex != len)
						&& Character.isUpperCase(pattern
								.charAt(lastDotIndex + 1)))
					pattern += '.'; // append . to a class filter
			}

			if (line.indexOf(pattern) > 0)
				return true;
		}
		return false;
	}

	public String filter(String stackTrace) {
		if (filterPatterns.length == 0 || stackTrace == null)
			return stackTrace;

		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		StringReader stringReader = new StringReader(stackTrace);
		BufferedReader bufferedReader = new BufferedReader(stringReader);

		String line;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				if (!filterLine(line))
					printWriter.println(line);
			}
		} catch (IOException e) {
			return stackTrace; // return the stack unfiltered
		}
		return stringWriter.toString();
	}

}
