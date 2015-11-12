package de.tum.in.niedermr.ta.runner.analysis.workflow.steps.impl.s3;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.tum.in.niedermr.ta.core.code.tests.TestInformation;
import de.tum.in.niedermr.ta.core.common.constants.CommonConstants;
import de.tum.in.niedermr.ta.core.common.io.TextFileData;
import de.tum.in.niedermr.ta.core.common.util.ClasspathUtility;
import de.tum.in.niedermr.ta.runner.analysis.InformationCollector;
import de.tum.in.niedermr.ta.runner.analysis.workflow.steps.AbstractExecutionStep;
import de.tum.in.niedermr.ta.runner.execution.ExecutionInformation;
import de.tum.in.niedermr.ta.runner.execution.ProcessExecution;
import de.tum.in.niedermr.ta.runner.execution.environment.Environment;
import de.tum.in.niedermr.ta.runner.execution.infocollection.CollectedInformation;

public class InformationCollectorStep extends AbstractExecutionStep {
	private static final String EXEC_ID = "INFCOL";

	private final ConcurrentLinkedQueue<TestInformation> methodsToMutateAndTestsToRun;

	public InformationCollectorStep(ExecutionInformation information) {
		super(information);
		this.methodsToMutateAndTestsToRun = new ConcurrentLinkedQueue<>();
	}

	@Override
	public void runInternal() throws Exception {
		final String classPath = ClasspathUtility.getProgramClasspath() + getSourceInstrumentedJarFilesClasspath()
				+ CP_SEP + getTestInstrumentedJarFilesClasspath() + CP_SEP + m_configuration.getClasspath().getValue();

		List<String> arguments = new LinkedList<>();
		arguments.add(m_configuration.getCodePathToTest().getWithAlternativeSeparator(CommonConstants.SEPARATOR_DEFAULT));
		arguments.add(getFileInWorkingArea(FILE_OUTPUT_COLLECTED_INFORMATION));
		arguments.add(m_configuration.getTestRunner().getValue());
		arguments.add(m_configuration.getOperateFaultTolerant().getValueAsString());
		arguments.add(m_configuration.getTestClassesToSkip().getValue());

		m_processExecution.execute(getFullExecId(EXEC_ID), ProcessExecution.NO_TIMEOUT,
				getClassNameOfInformationCollector(), classPath, arguments);

		loadCollectedData();
	}

	protected void loadCollectedData() throws IOException {
		List<String> data = TextFileData.readFromFile(getFileInWorkingArea(FILE_OUTPUT_COLLECTED_INFORMATION));

		methodsToMutateAndTestsToRun.addAll(CollectedInformation.parseInformationCollectorData(data));
	}

	protected String getClassNameOfInformationCollector() {
		return InformationCollector.class.getName();
	}

	protected String getSourceInstrumentedJarFilesClasspath() {
		return Environment.getClasspathOfIndexedFiles(getFileInWorkingArea(FILE_TEMP_JAR_INSTRUMENTED_SOURCE_X), 0,
				m_configuration.getCodePathToMutate().countElements());
	}

	protected String getTestInstrumentedJarFilesClasspath() {
		return Environment.getClasspathOfIndexedFiles(getFileInWorkingArea(FILE_TEMP_JAR_INSTRUMENTED_TEST_X), 0,
				m_configuration.getCodePathToTest().countElements());
	}

	public ConcurrentLinkedQueue<TestInformation> getMethodsToMutateAndTestsToRun() {
		return methodsToMutateAndTestsToRun;
	}

	@Override
	protected String getDescription() {
		return "Loading information about the testcases and the methods they are invoking";
	}
}
