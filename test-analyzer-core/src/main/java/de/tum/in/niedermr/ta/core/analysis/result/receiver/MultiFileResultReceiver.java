package de.tum.in.niedermr.ta.core.analysis.result.receiver;

import java.util.List;

/** Rolling file receiver. */
public class MultiFileResultReceiver implements IResultReceiver {

	/** File name. */
	private final String m_baseFileName;

	/** Maximum number of lines per file. */
	private final int m_desiredLinesPerFile;

	/** Buffer size in lines. */
	private final int m_bufferSize;

	/** Current file result receiver. */
	private FileResultReceiver m_currentFileResultReceiver;

	/** Number of lines in the current file. */
	private int m_linesInCurrentFile;

	/** Number of created files. */
	private int m_fileCount;

	/** Constructor. */
	public MultiFileResultReceiver(String baseFileName, int desiredLinesPerFile) {
		this(baseFileName, desiredLinesPerFile, FileResultReceiver.DEFAULT_BUFFER_SIZE);
	}

	/** Constructor. */
	public MultiFileResultReceiver(String baseFileName, int desiredLinesPerFile, int bufferSize) {
		m_baseFileName = baseFileName;
		m_desiredLinesPerFile = desiredLinesPerFile;
		m_bufferSize = bufferSize;
		m_fileCount = 1;

		createNewFileResultReceiver();
	}

	private synchronized void createNewFileResultReceiver() {
		if (m_currentFileResultReceiver != null) {
			m_currentFileResultReceiver.markResultAsComplete();
			m_fileCount++;
		}

		m_currentFileResultReceiver = new FileResultReceiver(getFileName(m_fileCount), true, m_bufferSize);
		m_linesInCurrentFile = 0;
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void append(String line) {
		m_currentFileResultReceiver.append(line);
		m_linesInCurrentFile++;
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void append(List<String> lines) {
		m_currentFileResultReceiver.append(lines);
		m_linesInCurrentFile += lines.size();
	}

	/** {@inheritDoc} */
	@Override
	public void markResultAsComplete() {
		m_currentFileResultReceiver.markResultAsComplete();
	}

	/** {@inheritDoc} */
	@Override
	public void markResultAsPartiallyComplete() {
		createNewFileResultReceiverIfNeeded();
	}

	protected synchronized void createNewFileResultReceiverIfNeeded() {
		if (m_linesInCurrentFile > m_desiredLinesPerFile) {
			createNewFileResultReceiver();
		}
	}

	/** {@link #m_baseFileName} */
	public String getBaseFileName() {
		return m_baseFileName;
	}

	/**
	 * Get the file with the given index. The index is <b>not zero-based</b>.
	 */
	public String getFileName(int index) {
		if (index <= 0 || index > m_fileCount) {
			throw new IllegalArgumentException("Invalid index: " + index + " (File count is: " + m_fileCount + ")");
		}

		int lastIndexOfFileExtensionSeparator = m_baseFileName.lastIndexOf(".");

		if (lastIndexOfFileExtensionSeparator < 0) {
			return m_baseFileName + index;
		}

		return m_baseFileName.substring(0, lastIndexOfFileExtensionSeparator) + index
				+ m_baseFileName.substring(lastIndexOfFileExtensionSeparator);
	}

	/** {@link #m_fileCount} */
	public int getFileCount() {
		return m_fileCount;
	}
}
