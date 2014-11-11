package org.jenkinsci.testinprogress.httpserver;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jenkinsci.testinprogress.server.events.build.BuildTestEvent;
import org.jenkinsci.testinprogress.server.events.build.IBuildTestEvents;

@Path("previousBuildTestEvents")
public class PreviousBuildTestEventsResource {
	private final IBuildTestEvents buildTestEvents;
	
	public PreviousBuildTestEventsResource(IBuildTestEvents buildTestEvents) {
		this.buildTestEvents = buildTestEvents;
	}	
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<BuildTestEvent> getPreviousBuildTestEvents() {
    	return buildTestEvents.getEvents();
    }	
	
}
