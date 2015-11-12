package de.tum.in.niedermr.ta.extensions.analysis.workflows.statistics.operation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import de.tum.in.niedermr.ta.core.analysis.jars.operation.AbstractCodeAnalyzeOperation;
import de.tum.in.niedermr.ta.core.code.identifier.MethodIdentifier;
import de.tum.in.niedermr.ta.core.code.tests.assertions.AssertionInformation;
import de.tum.in.niedermr.ta.core.code.tests.detector.ClassType;
import de.tum.in.niedermr.ta.core.code.tests.detector.ITestClassDetector;
import de.tum.in.niedermr.ta.extensions.analysis.workflows.statistics.bytecode.AssertionCounterMethodVisitor;

public class AssertionCounterOperation extends AbstractCodeAnalyzeOperation {
	private final Map<MethodIdentifier, Integer> assertionsPerTestcase;
	private final AssertionCounterMethodVisitor methodVisitor;

	public AssertionCounterOperation(ITestClassDetector testClassDetector, AssertionInformation assertionInformation) {
		super(testClassDetector);
		this.assertionsPerTestcase = new HashMap<>();
		this.methodVisitor = new AssertionCounterMethodVisitor(assertionInformation);
	}

	@Override
	protected void analyzeSourceClass(ClassNode cn, String originalClassPath) {
		// NOP
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void analyzeTestClass(ClassNode cn, String originalClassPath, ClassType testClassType) {
		for (MethodNode methodNode : (List<MethodNode>) cn.methods) {
			if (m_testClassDetector.analyzeIsTestcase(methodNode, testClassType)) {
				methodVisitor.reset();

				methodNode.accept(methodVisitor);

				MethodIdentifier identifier = MethodIdentifier.create(cn.name, methodNode);
				assertionsPerTestcase.put(identifier, methodVisitor.getCountAssertions());
			}
		}
	}

	public Map<MethodIdentifier, Integer> getAssertionsPerTestcase() {
		return assertionsPerTestcase;
	}
}
