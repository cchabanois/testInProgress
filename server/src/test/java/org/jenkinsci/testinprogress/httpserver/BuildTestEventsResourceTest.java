package org.jenkinsci.testinprogress.httpserver;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jenkinsci.testinprogress.server.events.build.BuildTestEvent;
import org.jenkinsci.testinprogress.server.events.build.IBuildTestEvents;
import org.jenkinsci.testinprogress.server.events.run.RunEndEvent;
import org.jenkinsci.testinprogress.server.events.run.RunStartEvent;
import org.jenkinsci.testinprogress.utils.FreePortsFinder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

public class BuildTestEventsResourceTest {
	private TestInProgressHttpServer server;
	private IBuildTestEvents buildTestEvents = mock(IBuildTestEvents.class);
	private WebTarget target;

	@Before
	public void setUp() throws IOException {
		int port = FreePortsFinder.findFreePort();
		server = new TestInProgressHttpServer(String.format("http://localhost:%d/", port),
				buildTestEvents);
		server.start();
		Client c = ClientBuilder.newClient();
		target = c.target(String.format("http://localhost:%d",port));
	}

	@After
	public void tearDown() {
		server.stop();
	}

	@Test
	public void testGetWithDefaultParam() throws UnsupportedEncodingException,
			IOException {
		// Given
		List<BuildTestEvent> events = Lists.newArrayList(new BuildTestEvent(
				"runId", new RunStartEvent(0)), new BuildTestEvent("runId",
				new RunEndEvent(10, 100)));
		when(buildTestEvents.getEvents()).thenReturn(events);

		// When
		Response response = target.path("buildTestEvents").request().get();
		InputStream is = (InputStream) response.getEntity();

		// Then
		ObjectMapper mapper = new ObjectMapper();
		@SuppressWarnings("unchecked")
		List<Object> result = mapper.readValue(is, List.class);
		assertEquals(
				"[{runId=runId, runTestEvent={timestamp=0, type=TESTC}}, {runId=runId, runTestEvent={timestamp=10, elapsedTime=100, type=RUNTIME}}]",
				result.toString());
	}

	@Test
	public void testGetFromIndex() throws JsonParseException, JsonMappingException, IOException {
		// Given
		List<BuildTestEvent> events = Lists.newArrayList(new BuildTestEvent(
				"runId", new RunStartEvent(0)), new BuildTestEvent("runId",
				new RunEndEvent(10, 100)));
		when(buildTestEvents.getEvents()).thenReturn(events);

		// When
		Response response = target.path("buildTestEvents").queryParam("fromIndex", "1").request().get();
		InputStream is = (InputStream) response.getEntity();

		// Then
		ObjectMapper mapper = new ObjectMapper();
		@SuppressWarnings("unchecked")
		List<Object> result = mapper.readValue(is, List.class);
		assertEquals(
				"[{runId=runId, runTestEvent={timestamp=10, elapsedTime=100, type=RUNTIME}}]",
				result.toString());		
	}
	
}
