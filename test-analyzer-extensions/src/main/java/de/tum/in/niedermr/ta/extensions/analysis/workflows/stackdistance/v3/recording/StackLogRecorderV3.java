package de.tum.in.niedermr.ta.extensions.analysis.workflows.stackdistance.v3.recording;

import java.util.HashMap;
import java.util.Map;

import de.tum.in.niedermr.ta.core.code.identifier.MethodIdentifier;
import de.tum.in.niedermr.ta.core.code.identifier.TestcaseIdentifier;
import de.tum.in.niedermr.ta.extensions.analysis.workflows.stackdistance.common.logic.collection.StackLogDataManager;
import de.tum.in.niedermr.ta.extensions.analysis.workflows.stackdistance.v2.logic.collection.ThreadStackManager;

/**
 * Stack log recorder V3.<br/>
 * Used by instrumented code. DO NOT MODIFY.
 * 
 * @see StackLogDataManager to retrieve the logged data
 */
public class StackLogRecorderV3 {

	/** Manages the stack heights of started threads. */
	private static ThreadStackManager s_threadStackManager;

	/** Current stack distance per thread name. */
	private static Map<String, Integer> s_currentStackDistanceByThreadName = new HashMap<>();

	/** Set the thread manager and verify that the modified thread class was loaded. */
	public static void setThreadStackManagerAndVerify(ThreadStackManager threadManager) {
		s_threadStackManager = threadManager;
		s_threadStackManager.verifyReplacedThreadClassInUse();
	}

	/**
	 * Start a new log for the specified test case. This resets all counters.<br/>
	 * Note that it is ok to log invocations from framing methods (<code>@Before</code>) too because they also invoke
	 * the mutated methods.
	 */
	public static synchronized void startLog(TestcaseIdentifier testCaseIdentifier) {
		StackLogDataManager.resetAndStart(testCaseIdentifier);
		s_currentStackDistanceByThreadName.clear();
		s_threadStackManager.setStopClassName(testCaseIdentifier.toMethodIdentifier().getOnlyClassName());
	}

	/**
	 * Push invocation: The invocation is started, the test case just invoked this method (directly or indirectly).<br/>
	 * (This method is invoked by instrumented code.)
	 */
	public static synchronized void pushInvocation(String methodIdentifierString) {
		if (s_threadStackManager == null) {
			// the thread stack manager has not been initialized yet because the test execution has not started yet
			// ignore the invocation
			// this can happen if an instrumented method is invoked from a static initializer of a class (since a class
			// may be loaded before the actual test execution)
			return;
		}

		MethodIdentifier methodIdentifier = MethodIdentifier.parse(methodIdentifierString);
		String currentThreadName = Thread.currentThread().getName();

		Integer currentStackDistance = s_currentStackDistanceByThreadName.get(currentThreadName);

		if (currentStackDistance == null) {
			currentStackDistance = s_threadStackManager.getStackHeightOfThread(currentThreadName);
		}

		currentStackDistance++;
		s_currentStackDistanceByThreadName.put(currentThreadName, currentStackDistance);
		StackLogDataManager.visitMethodInvocation(methodIdentifier, currentStackDistance);
	}

	/**
	 * Pop invocation: The invocation is completed, the test case is about to leave the method.
	 */
	public static synchronized void popInvocation() {
		String currentThreadName = Thread.currentThread().getName();

		Integer currentStackDistance = s_currentStackDistanceByThreadName.get(currentThreadName);

		if (currentStackDistance != null) {
			currentStackDistance--;

			if (currentStackDistance < 0) {
				currentStackDistance = 0;
			}

			s_currentStackDistanceByThreadName.put(currentThreadName, currentStackDistance);
		}
	}
}
