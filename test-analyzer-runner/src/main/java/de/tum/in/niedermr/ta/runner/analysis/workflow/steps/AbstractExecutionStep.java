package de.tum.in.niedermr.ta.runner.analysis.workflow.steps;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tum.in.niedermr.ta.runner.configuration.Configuration;
import de.tum.in.niedermr.ta.runner.execution.ExecutionContext;
import de.tum.in.niedermr.ta.runner.execution.ProcessExecution;
import de.tum.in.niedermr.ta.runner.execution.environment.Environment;
import de.tum.in.niedermr.ta.runner.execution.environment.EnvironmentConstants;
import de.tum.in.niedermr.ta.runner.execution.exceptions.FailedExecution;

public abstract class AbstractExecutionStep implements IExecutionStep, EnvironmentConstants {
	private static final Logger LOG = LogManager.getLogger(AbstractExecutionStep.class);

	private final ExecutionContext m_context;
	protected final Configuration m_configuration;
	protected final ProcessExecution m_processExecution;

	public AbstractExecutionStep(ExecutionContext information) {
		this.m_context = information;
		this.m_configuration = information.getConfiguration();
		this.m_processExecution = new ProcessExecution(information.getWorkingFolder(), information.getProgramPath(),
				information.getWorkingFolder());
	}

	public ExecutionContext getContext() {
		return m_context;
	}

	@Override
	public final void run() throws FailedExecution {
		try {
			LOG.info("START: " + getDescription());

			long startTime = System.currentTimeMillis();
			runInternal();
			long duration = getDuration(startTime);

			LOG.info("COMPLETED: " + getDescription());
			LOG.info("DURATION: " + duration + " seconds.");
		} catch (FailedExecution ex) {
			throw ex;
		} catch (Throwable t) {
			throw new FailedExecution(m_context.getExecutionId(), t);
		}
	}

	protected abstract void runInternal() throws Throwable;

	protected abstract String getDescription();

	protected final String getFullExecId(String processId) {
		return m_context.getExecutionId() + "_" + processId;
	}

	protected final String getWithIndex(String fileName, int index) {
		return Environment.getWithIndex(fileName, index);
	}

	protected final String getFileInWorkingArea(String fileName) {
		return Environment.replaceWorkingFolder(fileName, m_context.getWorkingFolder());
	}

	private long getDuration(long startTime) {
		return (System.currentTimeMillis() - startTime) / 1000;
	}

	@Override
	public String toString() {
		return "ExecutionStep [" + getDescription() + "]";
	}
}
