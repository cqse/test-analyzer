package de.tum.in.niedermr.ta.extensions.analysis.workflows.stackdistance.logic.instrumentation;

import de.tum.in.niedermr.ta.core.code.tests.detector.ITestClassDetector;
import de.tum.in.niedermr.ta.core.code.tests.runner.ITestRunner;
import de.tum.in.niedermr.ta.core.execution.id.IFullExecutionId;
import de.tum.in.niedermr.ta.runner.analysis.instrumentation.AbstractInstrumentation;
import de.tum.in.niedermr.ta.runner.execution.exceptions.ExecutionException;

public class AnalysisInstrumentation extends AbstractInstrumentation {

	/** Class that handles the data gathered from the instrumentation. */
	private Class<?> m_instrumentationDataRetrieverClass;

	public AnalysisInstrumentation(IFullExecutionId executionId, Class<?> instrumentationLoggerClass,
			boolean operateFaultTolerant) {
		super(executionId, operateFaultTolerant);
		m_instrumentationDataRetrieverClass = instrumentationLoggerClass;
	}

	public void injectAnalysisStatements(String[] jarsToBeInstrumented, String genericJarOutputPath,
			ITestRunner testRunner, String[] testClassIncludes, String[] testClassExcludes) throws ExecutionException {
		// true as argument in order to include abstract test classes
		ITestClassDetector detector = testRunner.createTestClassDetector(true, testClassIncludes, testClassExcludes);
		AnalysisInstrumentationOperation operation = new AnalysisInstrumentationOperation(detector,
				m_instrumentationDataRetrieverClass);
		instrumentJars(jarsToBeInstrumented, genericJarOutputPath, operation);
	}
}
