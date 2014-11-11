package org.jenkinsci.testinprogress.httpserver;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.jenkinsci.testinprogress.server.BuildTestEventsServer;
import org.jenkinsci.testinprogress.server.build.BuildTestEvents;
import org.jenkinsci.testinprogress.server.build.BuildTestResults;
import org.jenkinsci.testinprogress.server.build.CompletedBuildTestEvents;
import org.jenkinsci.testinprogress.server.events.build.BuildTestEvent;
import org.jenkinsci.testinprogress.server.events.build.IBuildTestEventListener;
import org.jenkinsci.testinprogress.server.events.build.IBuildTestEvents;
import org.jenkinsci.testinprogress.server.events.build.TestRunIds;
import org.jenkinsci.testinprogress.server.filters.StackTraceFilter;
import org.jenkinsci.testinprogress.server.listeners.BuildTestStats;
import org.jenkinsci.testinprogress.server.listeners.RunningBuildTestEvents;
import org.jenkinsci.testinprogress.server.listeners.SaveTestEventsListener;
import org.jenkinsci.testinprogress.server.persistence.TestEventsDirs;

public class TestInProgressHttpServer {
	private final String baseUri;
	private final IBuildTestEvents previousBuildTestEvents;
	private final IBuildTestEvents buildTestEvents;
	private HttpServer httpServer;

	public TestInProgressHttpServer(String baseUri, 
			IBuildTestEvents buildTestEvents) {
		this(baseUri, null, buildTestEvents);
	}
	
	public TestInProgressHttpServer(String baseUri, IBuildTestEvents previousBuildTestEvents,
			IBuildTestEvents buildTestEvents) {
		this.baseUri = baseUri;
		if (previousBuildTestEvents == null) {
			this.previousBuildTestEvents = new BuildTestEvents(Collections.<BuildTestEvent>emptyList());
		} else {
			this.previousBuildTestEvents = previousBuildTestEvents;
		}
		this.buildTestEvents = buildTestEvents;
	}

	public void start() throws IOException {
		ResourceConfig rc = new ResourceConfig().registerInstances(
				new PreviousBuildTestEventsResource(previousBuildTestEvents),
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
		File testEventsRootDir = new File("testEvents");
		testEventsRootDir.mkdir();
		TestEventsDirs testEventsDirs = new TestEventsDirs(testEventsRootDir);
		File previousBuildTestEventsDir = testEventsDirs.getLatestBuildTestEventsDir();
		File buildTestEventsDir = testEventsDirs.createBuildTestEventsDir();
		RunningBuildTestEvents runningBuildTestEvents = new RunningBuildTestEvents();
		SaveTestEventsListener saveTestEventsListener = new SaveTestEventsListener(
				buildTestEventsDir);
		BuildTestStats buildTestStats = new BuildTestStats();
		StackTraceFilter stackTraceFilter = new StackTraceFilter();
		BuildTestEventsServer buildTestEventsServer = new BuildTestEventsServer(6061,
				stackTraceFilter, new IBuildTestEventListener[] {
						runningBuildTestEvents, saveTestEventsListener,
						buildTestStats });
		buildTestEventsServer.start();
		
		TestRunIds testRunIds = new TestRunIds();
		CompletedBuildTestEvents completedBuildTestEvents = null;
		if (previousBuildTestEventsDir != null) {
			completedBuildTestEvents = new CompletedBuildTestEvents(previousBuildTestEventsDir);
		}
		BuildTestResults buildTestResults = new BuildTestResults(buildTestEventsDir, testRunIds, runningBuildTestEvents, buildTestStats);
		
		TestInProgressHttpServer httpServer = new TestInProgressHttpServer(
				"http://localhost:8080/", completedBuildTestEvents ,buildTestResults);
		httpServer.start();
		System.in.read();
		httpServer.stop();
	}
}
