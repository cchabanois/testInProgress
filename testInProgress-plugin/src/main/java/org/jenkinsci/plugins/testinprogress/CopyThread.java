package org.jenkinsci.plugins.testinprogress;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Copies a stream and close them at EOF.
 * 
 * @author Kohsuke Kawaguchi
 */
public final class CopyThread extends Thread {
	private static final Logger LOGGER = Logger.getLogger(CopyThread.class
			.getName());
	private final InputStream in;
	private final OutputStream out;

	public CopyThread(String threadName, InputStream in, OutputStream out) {
		super(threadName);
		this.in = in;
		this.out = out;
	}

	public void run() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line;
		try {
			try {
			do {
				line = reader.readLine();
				out.write((line+"\n").getBytes());
			} while (line != null);
			} finally {
				reader.close();
				out.close();
			}
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Exception while copying in thread: "
					+ getName(), e);
		}
//		try {
//			try {
//				byte[] buf = new byte[8192];
//				int len;
//				while ((len = in.read(buf)) > 0)
//					out.write(buf, 0, len);
//			} finally {
//				in.close();
//				out.close();
//			}
//		} catch (IOException e) {
//			LOGGER.log(Level.WARNING, "Exception while copying in thread: "
//					+ getName(), e);
//		}
	}
}
