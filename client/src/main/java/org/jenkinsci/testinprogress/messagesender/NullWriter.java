package org.jenkinsci.testinprogress.messagesender;

import java.io.IOException;
import java.io.Writer;

/**
 *  An Writer than eats its input.
 */
public class NullWriter extends Writer {

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		
	}

	@Override
	public void flush() throws IOException {
	}

	@Override
	public void close() throws IOException {
	}

}
