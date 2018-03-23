package de.tum.in.niedermr.ta.extensions.analysis.workflows.stackdistance.maven;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import de.tum.in.niedermr.ta.core.analysis.result.receiver.InMemoryResultReceiver;
import de.tum.in.niedermr.ta.core.code.identifier.MethodIdentifier;
import de.tum.in.niedermr.ta.core.code.identifier.TestcaseIdentifier;
import de.tum.in.niedermr.ta.core.common.TestUtility;

/** Test {@link TeamscaleSurefireTestListener}. */
public class TeamscaleSurefireTestListenerTest {

	/** Test. */
	@Test
	public void testOutputGeneration() throws IOException {
		InMemoryResultReceiver resultReceiver = new InMemoryResultReceiver();

		TeamscaleSurefireTestListener listener = new TeamscaleSurefireTestListener();

		TestcaseIdentifier testcaseIdentifier1 = TestcaseIdentifier.create("SampleTest", "test1");
		TestcaseIdentifier testcaseIdentifier2 = TestcaseIdentifier.create("SampleTest", "test2");
		MethodIdentifier methodIdentifier = MethodIdentifier.create("java.lang.String", "toString",
				"()Ljava/lang/String;");

		listener.execAfterOutputWriterInitialized(resultReceiver);
		listener.writeCommentToResultFile(resultReceiver, "abc");
		listener.execBeforeAllTests(resultReceiver);
		listener.appendToResult(resultReceiver, testcaseIdentifier1, methodIdentifier, 3, 4);
		listener.appendToResult(resultReceiver, testcaseIdentifier2, methodIdentifier, 1, 6);
		listener.execAfterAllTests(resultReceiver);

		String expectedResult = TestUtility.loadTestContent(getClass(), "expected-output.txt");
		String actualResult = TestUtility.loadTestContent(resultReceiver);
		assertEquals(expectedResult, actualResult);
	}
}
