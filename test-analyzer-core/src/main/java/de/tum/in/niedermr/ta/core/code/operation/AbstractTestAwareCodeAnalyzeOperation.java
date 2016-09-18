package de.tum.in.niedermr.ta.core.code.operation;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import de.tum.in.niedermr.ta.core.code.tests.detector.ClassType;
import de.tum.in.niedermr.ta.core.code.tests.detector.ITestClassDetector;

/** Base class for a code analysis operation that is aware of test classes. */
public abstract class AbstractTestAwareCodeAnalyzeOperation extends AbstractTestAwareCodeOperation
		implements ICodeAnalyzeOperation {

	/** Constructor. */
	public AbstractTestAwareCodeAnalyzeOperation(ITestClassDetector testClassDetector) {
		super(testClassDetector);
	}

	/** {@inheritDoc} */
	@Override
	public final void analyze(ClassReader cr, String originalClassPath) throws CodeOperationException {
		ClassNode cn = new ClassNode();
		cr.accept(cn, 0);

		ClassType classType = analyzeClassType(cn);

		if (classType.isTestClass()) {
			analyzeTestClass(cn, originalClassPath, classType);
		} else {
			analyzeSourceClass(cn, originalClassPath);
		}
	}

	protected abstract void analyzeSourceClass(ClassNode cn, String originalClassPath);

	protected abstract void analyzeTestClass(ClassNode cn, String originalClassPath, ClassType testClassType);
}
