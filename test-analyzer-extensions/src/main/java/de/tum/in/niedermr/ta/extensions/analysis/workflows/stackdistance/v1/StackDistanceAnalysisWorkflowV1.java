package de.tum.in.niedermr.ta.extensions.analysis.workflows.stackdistance.v1;

import de.tum.in.niedermr.ta.extensions.analysis.workflows.ExtensionEnvironmentConstants;
import de.tum.in.niedermr.ta.extensions.analysis.workflows.stackdistance.common.AbstractStackDistanceAnalysisWorkflow;
import de.tum.in.niedermr.ta.extensions.analysis.workflows.stackdistance.common.steps.AnalysisInformationCollectorStep;
import de.tum.in.niedermr.ta.extensions.analysis.workflows.stackdistance.common.steps.AnalysisInstrumentationStep;
import de.tum.in.niedermr.ta.extensions.analysis.workflows.stackdistance.v1.logic.collection.AnalysisInformationCollectionLogicV1;
import de.tum.in.niedermr.ta.extensions.analysis.workflows.stackdistance.v1.logic.recording.StackLogRecorderV1;
import de.tum.in.niedermr.ta.extensions.analysis.workflows.stackdistance.v2.StackDistanceAnalysisWorkflowV2;

/**
 * Computes the minimum and maximum distance on the call stack between test case and method. <br/>
 * <b>Deprecated: {@link StackDistanceAnalysisWorkflowV2} should be used instead.</b> V2 produces valid results for
 * multi-threaded code. Note that V2 requires to replace the {@link Thread} class.
 */
public class StackDistanceAnalysisWorkflowV1 extends AbstractStackDistanceAnalysisWorkflow {

	/** {@inheritDoc} */
	@Override
	protected AnalysisInstrumentationStep createAnalysisInstrumentationStep() {
		AnalysisInstrumentationStep step = createAndInitializeExecutionStep(AnalysisInstrumentationStep.class);
		step.setStackLogRecorderClass(StackLogRecorderV1.class);
		return step;
	}

	/** {@inheritDoc} */
	@Override
	protected AnalysisInformationCollectorStep createAnalysisInformationCollectorStep() {
		AnalysisInformationCollectorStep step = createAndInitializeExecutionStep(
				AnalysisInformationCollectorStep.class);
		step.setResultOutputFile(ExtensionEnvironmentConstants.FILE_OUTPUT_STACK_DISTANCES_V1);
		step.setInformationCollectorLogicClass(AnalysisInformationCollectionLogicV1.class);
		return step;
	}
}