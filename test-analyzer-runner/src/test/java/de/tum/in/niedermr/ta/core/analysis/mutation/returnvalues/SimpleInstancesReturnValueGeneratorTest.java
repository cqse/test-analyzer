package de.tum.in.niedermr.ta.core.analysis.mutation.returnvalues;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.objectweb.asm.Type;

import de.tum.in.niedermr.ta.core.code.identifier.MethodIdentifier;

/** Test {@link SimpleInstancesReturnValueGenerator}. */
public class SimpleInstancesReturnValueGeneratorTest
		extends AbstractReturnValueGeneratorTest<ClassWithMethodsForMutation> {

	/** Constructor. */
	public SimpleInstancesReturnValueGeneratorTest() {
		super(new SimpleInstancesReturnValueGenerator(), ClassWithMethodsForMutation.class);
	}

	/** {@inheritDoc} */
	@Override
	protected void verifyMutation(Class<?> mutatedClass, Object instanceOfMutatedClass,
			ClassWithMethodsForMutation instanceOfOriginalClass) throws ReflectiveOperationException {
		assertEquals("", mutatedClass.getMethod("getStringBuilder").invoke(instanceOfMutatedClass).toString());
	}

	/** Test. */
	@Test
	public void testCanHandleReturn() {
		IReturnValueGenerator retValGen = new SimpleInstancesReturnValueGenerator();

		assertTrue(retValGen.checkReturnValueSupported(MethodIdentifier.EMPTY,
				Type.getReturnType("()Ljava/lang/String;")));
		assertFalse(retValGen.checkReturnValueSupported(MethodIdentifier.EMPTY,
				Type.getReturnType("()[java/lang/Integer;")));
		assertFalse(retValGen.checkReturnValueSupported(MethodIdentifier.EMPTY,
				Type.getReturnType("()[java/lang/Collection;")));
	}
}
