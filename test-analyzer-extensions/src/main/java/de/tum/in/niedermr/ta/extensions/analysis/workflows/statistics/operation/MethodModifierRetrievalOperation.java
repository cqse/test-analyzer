package de.tum.in.niedermr.ta.extensions.analysis.workflows.statistics.operation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import de.tum.in.niedermr.ta.core.analysis.jars.operation.AbstractCodeAnalyzeOperation;
import de.tum.in.niedermr.ta.core.code.identifier.MethodIdentifier;
import de.tum.in.niedermr.ta.core.code.tests.detector.ClassType;
import de.tum.in.niedermr.ta.core.code.tests.detector.ITestClassDetector;
import de.tum.in.niedermr.ta.core.code.util.BytecodeUtility;
import de.tum.in.niedermr.ta.core.code.util.OpcodesUtility;

public class MethodModifierRetrievalOperation extends AbstractCodeAnalyzeOperation {
	private final Map<MethodIdentifier, String> m_result = new HashMap<>();

	public MethodModifierRetrievalOperation(ITestClassDetector testClassDetector) {
		super(testClassDetector);
	}

	public Map<MethodIdentifier, String> getResult() {
		return m_result;
	}

	@Override
	protected void analyzeSourceClass(ClassNode cn, String originalClassPath) {
		analyzeMethods(cn);
	}

	@Override
	protected void analyzeTestClass(ClassNode cn, String originalClassPath, ClassType testClassType) {
		// NOP
	}

	@SuppressWarnings("unchecked")
	private void analyzeMethods(ClassNode cn) {
		for (MethodNode methodNode : (List<MethodNode>) cn.methods) {
			if (!(BytecodeUtility.isConstructor(methodNode) || BytecodeUtility.isAbstractMethod(methodNode))) {
				m_result.put(MethodIdentifier.create(cn.name, methodNode), getAccessModifier(methodNode));
			}
		}
	}

	private String getAccessModifier(MethodNode methodNode) {
		if (OpcodesUtility.hasFlag(methodNode.access, Opcodes.ACC_PUBLIC)) {
			return "public";
		} else if (OpcodesUtility.hasFlag(methodNode.access, Opcodes.ACC_PROTECTED)) {
			return "protected";
		} else if (OpcodesUtility.hasFlag(methodNode.access, Opcodes.ACC_PRIVATE)) {
			return "private";
		} else {
			return "default";
		}
	}
}