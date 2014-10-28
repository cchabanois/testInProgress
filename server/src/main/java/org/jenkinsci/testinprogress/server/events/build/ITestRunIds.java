package org.jenkinsci.testinprogress.server.events.build;

import java.util.List;

/**
 * Run ids used for a build
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 *
 */
public interface ITestRunIds {

	public abstract List<String> getRunIds();

}