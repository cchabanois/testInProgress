package org.jenkinsci.testinprogress.runner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.JUnitCore;

public class AbstractProgressSuiteTest {
	private ExecutorService executorService;
	private int port;

	@Before
	public void setUp() {
		executorService = Executors.newSingleThreadExecutor();
		port = findFreePort();
		System.setProperty("TEST_IN_PROGRESS_PORT", Integer.toString(port));
	}

	@After
	public void tearDown() {
		System.setProperty("TEST_IN_PROGRESS_PORT", "");
		executorService.shutdown();
	}

	protected Future<String[]> runProgressSuite(Class<?> classes) throws IOException {
		Future<String[]> futures = readAllMessagesFromServerSocket();
		JUnitCore jUnitCore = new JUnitCore();
		jUnitCore.run(classes);
		return futures;
	}

	private Future<String[]> readAllMessagesFromServerSocket()
			throws IOException {
		return executorService.submit(new Callable<String[]>() {

			public String[] call() throws Exception {
				ServerSocket serverSocket = new ServerSocket(port);
				try {
					Socket s = serverSocket.accept();
					BufferedReader is = new BufferedReader(
							new InputStreamReader(s.getInputStream()));

					StringBuilder sb = new StringBuilder();
					String line = is.readLine();
					while (line != null) {
						sb.append(line + '\n');
						line = is.readLine();
					}
					return sb.toString().split("\n");
				} finally {
					serverSocket.close();
				}

			}
		});
	}

	private int findFreePort() {
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(0);
			return socket.getLocalPort();
		} catch (IOException e) {
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
		return -1;
	}

	protected void printTestMessages(String[] messages) {
		for (String message : messages) {
			System.out.println(message);
		}
	}	
	
}
