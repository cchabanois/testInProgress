package org.jenkinsci.plugins.testinprogress.events;

/**
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 *
 */
public interface IRunTestEvent {

	String getType();	
	
	@Override
	public boolean equals(Object obj);
	
	@Override
	public int hashCode();
	
}