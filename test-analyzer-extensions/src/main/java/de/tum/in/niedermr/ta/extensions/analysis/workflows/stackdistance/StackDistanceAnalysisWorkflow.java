package de.tum.in.niedermr.ta.extensions.analysis.workflows.stackdistance;

import de.tum.in.niedermr.ta.extensions.analysis.workflows.stackdistance.logic.collection.AnalysisInformationCollectionLogicV1;
import de.tum.in.niedermr.ta.extensions.analysis.workflows.stackdistance.steps.AnalysisInformationCollectorStep;
import de.tum.in.niedermr.ta.extensions.analysis.workflows.stackdistance.steps.AnalysisInstrumentationStep;
import de.tum.in.niedermr.ta.extensions.analysis.workflows.stackdistance2.StackDistanceAnalysisWorkflowV2;

/**
 * Computes the minimum and maximum distance on the call stack between test case and method. <br/>
 * <b>Deprecated: {@link StackDistanceAnalysisWorkflowV2} should be used instead.</b> V2 produces valid results for
 * multi-threaded code. Note that V2 requires to replace the {@link Thread} class.
 */
public class StackDistanceAnalysisWorkflow extends AbstractStackDistanceAnalysisWorkflow {

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
		step.setInformationCollectorLogicClass(AnalysisInformationCollectionLogicV1.class);
		return step;
	}
}
