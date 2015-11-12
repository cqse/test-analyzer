package de.tum.in.niedermr.ta.runner.analysis;

import java.io.IOException;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tum.in.niedermr.ta.core.common.constants.CommonConstants;
import de.tum.in.niedermr.ta.core.common.constants.FileSystemConstants;
import de.tum.in.niedermr.ta.core.common.util.ClasspathUtility;
import de.tum.in.niedermr.ta.core.common.util.CommonUtility;
import de.tum.in.niedermr.ta.runner.analysis.workflow.IWorkflow;
import de.tum.in.niedermr.ta.runner.configuration.Configuration;
import de.tum.in.niedermr.ta.runner.configuration.ConfigurationLoader;
import de.tum.in.niedermr.ta.runner.configuration.exceptions.ConfigurationException;
import de.tum.in.niedermr.ta.runner.execution.environment.Environment;
import de.tum.in.niedermr.ta.runner.execution.exceptions.FailedExecution;
import de.tum.in.niedermr.ta.runner.logging.LoggingUtil;
import de.tum.in.niedermr.ta.runner.start.AnalyzerRunnerStart;

/**
 * <b>Starts the whole workflow</b> which consists of the core steps INSTRU (instrumentation), INFCOL (information
 * collection), METKIL (mutation) and TSTRUN (test run).<br/>
 * This project <b>contains</b> the steps <b>INSTRU and METKIL</b> and invokes INFCOL and TSTRUN in own processes.<br/>
 * <br/>
 * Contains the jar files of: the testing projects<br/>
 * Dependencies: ASM, log4j, ccsm-commons, core.<br/>
 * Further dependencies: jars to be processed and dependencies
 * 
 */
public class AnalyzerRunnerInternal {
	public static final Logger LOG = LogManager.getLogger(AnalyzerRunnerInternal.class);

	public static final String EXECUTION_ID_FOR_TESTS = "TEST";
	private static final String RELATIVE_WORKING_FOLDER = FileSystemConstants.CURRENT_FOLDER;

	/**
	 * args[0]: arbitrary (an ID will be generated) or {@link EXECUTION_ID_FOR_TESTS} args[1]: absolute path to the
	 * TestAnalyzer project (which is referenced by program libraries) args[2]: relative path to the configuration file
	 * in the working area
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			LoggingUtil.printDontStartThisClass(AnalyzerRunnerInternal.class, AnalyzerRunnerStart.class);
			return;
		}

		final String executionId = getExecutionId(args);
		final String programPath = CommonUtility.getArgument(args, 1);
		final String configurationFileToUse = Environment.replaceWorkingFolder(CommonUtility.getArgument(args, 2),
				RELATIVE_WORKING_FOLDER);

		try {
			LOG.info("TEST ANALYZER START");
			LOG.info("Classpath: " + ClasspathUtility.getCurrentClasspath());

			Configuration configuration = loadAndValidateTheConfiguration(configurationFileToUse);

			LOG.info("Configuration is valid.");
			LOG.info("Configuration is:" + CommonConstants.NEW_LINE + configuration.toMultiLineString());

			IWorkflow[] testWorkflows = createTestWorkflows(executionId, configuration);

			for (IWorkflow workFlow : testWorkflows) {
				LOG.info("WORKFLOW " + workFlow.getName() + " START (" + new Date() + ")");
				long startTime = System.currentTimeMillis();

				IWorkflow workflow = initializeTestWorkflow(executionId, workFlow, configuration, programPath);
				workflow.start();

				LOG.info("Workflow execution id was: '" + executionId + "'");
				LOG.info("Workflow duration was: " + getDuration(startTime));
				LOG.info("WORKFLOW " + workFlow.getName() + " END (" + new Date() + ")");
			}

			LOG.info("TEST ANALYZER END");
		} catch (Throwable t) {
			t.printStackTrace();
			LOG.fatal("Execution failed", t);
			throw new FailedExecution(executionId, AnalyzerRunnerInternal.class.getName() + " was not successful.");
		}
	}

	private static Configuration loadAndValidateTheConfiguration(String configurationFileToUse)
			throws ConfigurationException, IOException {
		Configuration configuration = ConfigurationLoader.getConfigurationFromFile(configurationFileToUse);

		final String classpathBefore = configuration.getClasspath().getValue();

		configuration.validateAndAdjust();

		if (!configuration.getClasspath().getValue().equals(classpathBefore)) {
			LOG.warn("Fixed the classpath of the configuration: removed elements of "
					+ configuration.getCodePathToMutate().getName() + " from " + configuration.getClasspath().getName()
					+ "!");
		}

		return configuration;
	}

	private static IWorkflow[] createTestWorkflows(String executionId, Configuration configuration) {
		try {
			return configuration.getTestWorkflows().createInstances();
		} catch (Throwable t) {
			throw new FailedExecution(executionId, "Error when creating the test workflows");
		}
	}

	private static IWorkflow initializeTestWorkflow(String executionId, IWorkflow workflow, Configuration configuration,
			String programPath) {
		try {
			workflow.init(executionId, configuration, programPath, RELATIVE_WORKING_FOLDER);

			return workflow;
		} catch (Throwable t) {
			throw new FailedExecution(executionId, "Error when initializing the test workflow");
		}
	}

	private static String getExecutionId(String[] args) {
		if (CommonUtility.getArgument(args, 0).equals(EXECUTION_ID_FOR_TESTS)) {
			return EXECUTION_ID_FOR_TESTS;
		} else {
			return CommonUtility.createRandomId();
		}
	}

	private static String getDuration(final long startTime) {
		final long endTime = System.currentTimeMillis();
		final long duration = endTime - startTime;

		return (duration / 1000) + " seconds";
	}
}
