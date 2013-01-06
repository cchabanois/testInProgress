package org.jenkinsci.plugins.testinprogress.events;

public interface ITestEvent {

	String getRunId();
	
	String getType();	
	
}
