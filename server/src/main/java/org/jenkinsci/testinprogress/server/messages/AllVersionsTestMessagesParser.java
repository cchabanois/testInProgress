package org.jenkinsci.testinprogress.server.messages;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jenkinsci.testinprogress.server.messages.jdt.JdtTestMessagesParser;
import org.jenkinsci.testinprogress.server.messages.json.v2.Jsonv2TestMessagesParser;
import org.json.JSONException;
import org.json.JSONObject;

public class AllVersionsTestMessagesParser implements ITestMessagesParser {
	private final static Logger LOG = Logger
			.getLogger(AllVersionsTestMessagesParser.class.getName());
	private final ITestRunListener[] listeners;

	public AllVersionsTestMessagesParser(ITestRunListener[] listeners) {
		this.listeners = listeners;
	}

	public void processTestMessages(Reader reader) {
		PushbackReader pushbackReader = new PushbackReader(reader, 1024);
		try {
			String firstMessage = peekMessage(pushbackReader);
			ITestMessagesParser testMessagesParser = null;
			if (firstMessage.contains("%TESTC")) {
				testMessagesParser = new JdtTestMessagesParser(listeners);
			} else {
				try {
					JSONObject message = new JSONObject(firstMessage);
					String version = message.getString("fVersion");
					if ("v2".equals(version)) {
						testMessagesParser = new Jsonv2TestMessagesParser(listeners);
					} else
					if ("v3".equals(version)) {
						testMessagesParser = new TestMessagesParser(listeners);
					}
				} catch (JSONException e) {
					// not json or fVersion not found
				}
			}
			if (testMessagesParser == null) {
				throw new IOException("Unknown message format : "
						+ firstMessage);
			}
			testMessagesParser.processTestMessages(pushbackReader);
		} catch (IOException e) {
			LOG.log(Level.WARNING, "Could not read message", e);
			notifyTestRunTerminated();
		} finally {
			try {
				pushbackReader.close();
			} catch (IOException e) {
			}
		}
	}

	private String peekMessage(PushbackReader in) throws IOException {
		StringBuffer buf = new StringBuffer(128);
		int ch;
		while ((ch = in.read()) != -1) {
			if (ch == '\n') {
				in.unread(getChars(buf.toString() + '\n'));
				return buf.toString();
			} else if (ch == '\r') {
				in.unread(getChars(buf.toString() + '\r'));
				return buf.toString();
			} else {
				buf.append((char) ch);
			}
		}
		if (buf.length() > 0) {
			in.unread(getChars(buf.toString()));
		}
		return buf.toString();
	}

	private char[] getChars(String str) {
		char chars[] = new char[str.length()];
		str.getChars(0, str.length(), chars, 0);
		return chars;
	}

	private void notifyTestRunTerminated() {
		for (int i = 0; i < listeners.length; i++) {
			ITestRunListener listener = listeners[i];
			listener.testRunTerminated();
		}
	}

}
