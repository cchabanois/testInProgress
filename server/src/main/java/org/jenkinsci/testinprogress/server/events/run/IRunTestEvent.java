package org.jenkinsci.testinprogress.server.events.run;

/**
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 *
 */
public interface IRunTestEvent {

	long getTimestamp();
	
	String getType();	
	
	@Override
	public boolean equals(Object obj);
	
	@Override
	public int hashCode();

	@Override
	public String toString();
}