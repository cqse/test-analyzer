package de.tum.in.niedermr.ta.core.common.constants;

import java.io.File;

public interface FileSystemConstants {
	/**
	 * Classpath separator. It depends on the operating system.<br/>
	 * <ul>
	 * <li>{@link #CLASSPATH_SEPARATOR_WINDOWS}</li>
	 * <li>{@link #CLASSPATH_SEPARATOR_LINUX}</li>
	 * </ul>
	 */
	public static final String CP_SEP = File.pathSeparator;

	public static final String PATH_SEPARATOR = "/";
	public static final String PATH_SEPARATOR_ALTERNATIVE = "\\";

	public static final String CURRENT_FOLDER = ".";

	public static final String CLASSPATH_SEPARATOR_WINDOWS = ";";
	public static final String CLASSPATH_SEPARATOR_LINUX = ":";
	public static final String CLASSPATH_WILDCARD = "*";

	public static final String FILE_EXTENSION_CLASS = ".class";
	public static final String FILE_EXTENSION_JAR = ".jar";
	public static final String FILE_EXTENSION_CONFIG = ".config";
	public static final String FILE_EXTENSION_SQL = ".sql";
	public static final String FILE_EXTENSION_TXT = ".txt";
	public static final String FILE_EXTENSION_XML = ".xml";
	public static final String FILE_EXTENSION_SQL_TXT = FILE_EXTENSION_SQL + FILE_EXTENSION_TXT;
}
