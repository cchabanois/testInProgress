package org.jenkinsci.testinprogress.httpserver;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.jenkinsci.testinprogress.server.BuildTestEventsServer;
import org.jenkinsci.testinprogress.server.build.BuildTestResults;
import org.jenkinsci.testinprogress.server.events.build.IBuildTestEventListener;
import org.jenkinsci.testinprogress.server.events.build.IBuildTestEvents;
import org.jenkinsci.testinprogress.server.events.build.TestRunIds;
import org.jenkinsci.testinprogress.server.filters.StackTraceFilter;
import org.jenkinsci.testinprogress.server.listeners.BuildTestStats;
import org.jenkinsci.testinprogress.server.listeners.RunningBuildTestEvents;
import org.jenkinsci.testinprogress.server.listeners.SaveTestEventsListener;

public class TestInProgressHttpServer {
	private final String baseUri;
	private final IBuildTestEvents buildTestEvents;
	private HttpServer httpServer;

	public TestInProgressHttpServer(String baseUri,
			IBuildTestEvents buildTestEvents) {
		this.baseUri = baseUri;
		this.buildTestEvents = buildTestEvents;
	}

	public void start() throws IOException {
		ResourceConfig rc = new ResourceConfig().registerInstances(
				new BuildTestEventsResource(buildTestEvents)).registerClasses(
				ObjectMapperProvider.class, JacksonFeature.class);
		httpServer = GrizzlyHttpServerFactory.createHttpServer(
				URI.create(baseUri), rc, false);
		CLStaticHttpHandler httpHandler = new CLStaticHttpHandler(getClass()
				.getClassLoader(),
				"/org/jenkinsci/testinprogress/httpserver/resources/");
		httpServer.getServerConfiguration().addHttpHandler(httpHandler,
				"/testinprogress");
		httpServer.start();
	}

	public void stop() {
		httpServer.shutdownNow();
	}

	/**
	 * Main method.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File unitsEventsDir = new File("unitsEvents");
		unitsEventsDir.mkdir();
		RunningBuildTestEvents runningBuildTestEvents = new RunningBuildTestEvents();
		SaveTestEventsListener saveTestEventsListener = new SaveTestEventsListener(
				unitsEventsDir);
		BuildTestStats buildTestStats = new BuildTestStats();
		StackTraceFilter stackTraceFilter = new StackTraceFilter();
		BuildTestEventsServer buildTestEventsServer = new BuildTestEventsServer(6061,
				stackTraceFilter, new IBuildTestEventListener[] {
						runningBuildTestEvents, saveTestEventsListener,
						buildTestStats });
		buildTestEventsServer.start();
		
		TestRunIds testRunIds = new TestRunIds();
		BuildTestResults buildTestResults = new BuildTestResults(unitsEventsDir, testRunIds, runningBuildTestEvents, buildTestStats);
		
		TestInProgressHttpServer httpServer = new TestInProgressHttpServer(
				"http://localhost:8080/", buildTestResults);
		httpServer.start();
		System.in.read();
		httpServer.stop();
	}
}
