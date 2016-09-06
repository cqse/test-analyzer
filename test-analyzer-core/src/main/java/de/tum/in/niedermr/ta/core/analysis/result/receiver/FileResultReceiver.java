package de.tum.in.niedermr.ta.core.analysis.result.receiver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.tum.in.niedermr.ta.core.common.io.TextFileData;

/** Receiver that appends the result to a file. */
public class FileResultReceiver implements IResultReceiver {

	/** Buffer size in number of lines. */
	private static final int DEFAULT_BUFFER_SIZE = 100;

	/** File name. */
	private String m_fileName;

	/** Buffer size in lines. */
	private int m_bufferSize;

	private final List<String> m_resultBuffer;

	/**
	 * Constructor.
	 * 
	 * @param overwriteExisting
	 *            if true, the file will be reset, otherwise the new content will be appended
	 */
	public FileResultReceiver(String fileName, boolean overwriteExisting) {
		this(fileName, overwriteExisting, DEFAULT_BUFFER_SIZE);
	}

	/** Constructor. */
	public FileResultReceiver(String fileName, boolean overwriteExisting, int bufferSize) {
		m_fileName = fileName;
		m_bufferSize = bufferSize;
		m_resultBuffer = new ArrayList<>(bufferSize);

		if (bufferSize <= 0) {
			throw new IllegalArgumentException("bufferSize <= 0");
		}

		if (overwriteExisting) {
			resetFile();
		}
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void append(String line) {
		m_resultBuffer.add(line);
		flushIfNeeded();
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void append(List<String> lines) {
		m_resultBuffer.addAll(lines);
		flushIfNeeded();
	}

	/** {@inheritDoc} */
	@Override
	public void markResultAsComplete() {
		flush();
	}

	/** Check if a flush is needed and perform it if necessary. */
	protected synchronized void flushIfNeeded() {
		if (m_resultBuffer.size() > m_bufferSize) {
			flush();
		}
	}

	/** Return true if the result buffer is empty. */
	protected boolean isResultBufferEmpty() {
		return m_resultBuffer.isEmpty();
	}

	/** Perform a flush. */
	protected synchronized void flush() {
		if (isResultBufferEmpty()) {
			return;
		}

		try {
			TextFileData.appendToFile(m_fileName, m_resultBuffer);
		} catch (IOException e) {
			throw new IllegalStateException("Result flushing failed");
		}

		m_resultBuffer.clear();
	}

	protected void resetFile() {
		try {
			TextFileData.writeToFile(m_fileName, new ArrayList<>());
		} catch (IOException e) {
			throw new IllegalStateException("File reset failed");
		}
	}
}
