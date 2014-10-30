package org.jenkinsci.testinprogress.httpserver;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jenkinsci.testinprogress.server.events.build.BuildTestEvent;
import org.jenkinsci.testinprogress.server.events.build.IBuildTestEvents;

@Path("buildTestEvents")
public class BuildTestEventsResource {
	private final IBuildTestEvents buildTestEvents;
	
	public BuildTestEventsResource(IBuildTestEvents buildTestEvents) {
		this.buildTestEvents = buildTestEvents;
	}
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
	public List<BuildTestEvent> getBuildTestEvents(@DefaultValue("0") @QueryParam("fromIndex") int fromIndex) {
    	List<BuildTestEvent> list = buildTestEvents.getEvents();
    	return list.subList(fromIndex, list.size());
	}
}
