package de.tum.in.niedermr.ta.core.code.tests.assertions;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import de.tum.in.niedermr.ta.core.code.identifier.MethodIdentifier;
import de.tum.in.niedermr.ta.core.code.util.BytecodeUtility;
import de.tum.in.niedermr.ta.core.code.util.JavaUtility;

public class AssertionInformation {
	private static final Class<?>[] CORE_ASSERTION_CLASSES = new Class<?>[] { org.junit.Assert.class,
			junit.framework.Assert.class };
	private final Result NO_ASSERTION = new Result(false, null);

	private final Set<Class<?>> m_assertionClassesInUse;
	private final Map<MethodIdentifier, int[]> m_assertions;

	public AssertionInformation(Class<?>... furtherAssertionClasses) {
		this.m_assertionClassesInUse = new HashSet<>();
		this.m_assertions = new HashMap<>();

		m_assertionClassesInUse.addAll(Arrays.asList(CORE_ASSERTION_CLASSES));
		m_assertionClassesInUse.addAll(Arrays.asList(furtherAssertionClasses));

		initAllAssertionClasses();
	}

	private void initAllAssertionClasses() {
		for (Class<?> assertionClass : m_assertionClassesInUse) {
			initAssertionClass(assertionClass);
		}
	}

	@SuppressWarnings("unchecked")
	private void initAssertionClass(Class<?> assertionClass) {
		try {
			final String className = assertionClass.getName();
			ClassNode cn = BytecodeUtility.getAcceptedClassNode(className);

			for (MethodNode methodNode : (List<MethodNode>) cn.methods) {
				if (BytecodeUtility.isPublicMethod(methodNode)) {
					MethodIdentifier methodIdentifier = MethodIdentifier.create(className, methodNode);
					int[] popOpcodes = calculatePopOpcodes(methodNode.desc);

					m_assertions.put(methodIdentifier, popOpcodes);
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private int[] calculatePopOpcodes(String methodDesc) {
		final Type[] argumentTypes = Type.getArgumentTypes(methodDesc);

		int[] popOpcodes = new int[argumentTypes.length];

		for (int i = 0; i < argumentTypes.length; i++) {
			final int typeSort = argumentTypes[i].getSort();

			if (typeSort == Type.LONG || typeSort == Type.DOUBLE) {
				popOpcodes[i] = Opcodes.POP2;
			} else {
				popOpcodes[i] = Opcodes.POP;
			}
		}

		return popOpcodes;
	}

	public Result isAssertionMethod(MethodIdentifier methodIdentifier) throws ClassNotFoundException {
		if (isKnownNativeAssertion(methodIdentifier)) {
			return new Result(true, methodIdentifier);
		} else {
			Class<?> cls = Class.forName(methodIdentifier.getOnlyClassName());

			for (Class<?> assertionClass : m_assertionClassesInUse) {
				if (JavaUtility.inheritsClass(cls, assertionClass)) {
					// create a new identifier in which the name of the class is replaced with the name of the assertion
					// super class
					// example: org.example.UtilTests.assertEquals(int,int) -> org.junit.Assert.assertEquals(int,int)
					MethodIdentifier newIdentifier = MethodIdentifier
							.parse(methodIdentifier.get().replace(cls.getName(), assertionClass.getName()));

					if (isKnownNativeAssertion(newIdentifier)) {
						return new Result(true, newIdentifier);
					} else {
						return NO_ASSERTION;
					}
				}
			}

			return NO_ASSERTION;
		}
	}

	private boolean isKnownNativeAssertion(MethodIdentifier identifier) {
		return m_assertions.containsKey(identifier);
	}

	public class Result {
		public final boolean m_isAssertion;
		public final int[] m_popInstructionOpcodes;

		public Result(boolean isAssertion, MethodIdentifier originalMethodIdentifier) {
			this.m_isAssertion = isAssertion;
			this.m_popInstructionOpcodes = getPopInstructionOpcodes(originalMethodIdentifier);
		}

		private int[] getPopInstructionOpcodes(MethodIdentifier methodIdentifier) {
			if (methodIdentifier == null) {
				return null;
			} else {
				return m_assertions.get(methodIdentifier);
			}
		}
	}
}
