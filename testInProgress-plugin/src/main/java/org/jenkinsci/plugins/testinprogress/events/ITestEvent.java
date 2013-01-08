package org.jenkinsci.plugins.testinprogress.events;

/**
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 *
 */
public interface ITestEvent {

	String getRunId();
	
	String getType();	
	
}
