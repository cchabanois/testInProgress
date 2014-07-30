package org.jenkinsci.plugins.testinprogress.events.run;

/**
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 *
 */
public interface IRunTestEvent {

	long getTimestamp();
	
	String getRunId();
	
	String getType();	
	
	@Override
	public boolean equals(Object obj);
	
	@Override
	public int hashCode();

	public String toString(boolean includeTimeStamp);
}