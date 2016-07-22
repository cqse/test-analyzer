package de.tum.in.niedermr.ta.test.integration;

import java.io.File;
import java.io.IOException;

import org.conqat.lib.commons.filesystem.FileSystemUtils;

import de.tum.in.niedermr.ta.runner.configuration.exceptions.ConfigurationException;

/**
 * Jar: simple-project-lite.jar Multi-threaded: yes (4) Comments: no information collection, presentation DB
 */
public class IntegrationTest3 extends AbstractSystemTest {
	@Override
	public void testSystemInternal() throws ConfigurationException, IOException {
		assertFileExists(MSG_PATH_TO_TEST_JAR_IS_INCORRECT, new File(getCommonFolderTestData() + JAR_TEST_DATA));
		assertFileExists(MSG_TEST_DATA_MISSING, getFileExpectedCollectedInformation());
		assertFileExists(MSG_TEST_DATA_MISSING, getFileExpectedResult());

		FileSystemUtils.copyFile(getFileExpectedCollectedInformation(), getFileOutputCollectedInformation());

		executeTestAnalyzerWithConfiguration();

		assertFileExists(MSG_OUTPUT_MISSING, getFileOutputResult());

		assertFileContentEqual(MSG_NOT_EQUAL_COLLECTED_INFORMATION, false, getFileExpectedCollectedInformation(),
				getFileOutputCollectedInformation());
		assertFileContentEqual(MSG_NOT_EQUAL_RESULT, false, getFileExpectedResult(), getFileOutputResult());
	}
}
