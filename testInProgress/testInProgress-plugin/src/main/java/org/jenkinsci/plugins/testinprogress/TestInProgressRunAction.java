/*
 * The MIT License
 *
 * Copyright (c) 2012, Cedric Chabanois
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE. 
 */
package org.jenkinsci.plugins.testinprogress;

import hudson.model.Action;
import hudson.model.AbstractBuild;

import java.io.File;
import java.util.List;

import org.jenkinsci.plugins.testinprogress.events.ITestEvent;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * Action used to display the ivy report for the build
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public class TestInProgressRunAction implements Action {
    private static final String UNIT_EVENTS_FILENAME = "unit.events";
	private static final String ICON_FILENAME = "/plugin/ivy-report/ivyReport.png";
    private transient ITestEvents testEvents;
    private final AbstractBuild build;
    
    public TestInProgressRunAction(AbstractBuild build, RunningBuildTestEvents testEvents) {
        this.build = build;
    	this.testEvents = testEvents;
    }

    public AbstractBuild getBuild() {
		return build;
	}
    
    @JavaScriptMethod
    public List<ITestEvent> getTestEvents(int fromIndex) {
    	ITestEvents testEvents = getTestEvents();    	
    	List<ITestEvent> events = testEvents.getEvents();
		return events.subList(fromIndex, events.size());
	}
    
    private synchronized ITestEvents getTestEvents() {
    	if (testEvents != null) {
    		return testEvents;
    	}
    	File file = new File(build.getRootDir(), UNIT_EVENTS_FILENAME);
    	testEvents = new CompletedBuildTestEvents(file);
    	return testEvents;
    }
    
    public String getUrlName() {
        return "testinprogress";
    }

    public String getDisplayName() {
        return "Test progress report";
    }

    public String getIconFileName() {
        return ICON_FILENAME;
    }

	public synchronized void onBuildComplete() {
		File file = new File(build.getRootDir(), UNIT_EVENTS_FILENAME);
    	this.testEvents = new CompletedBuildTestEvents(file);
	}
    
}
