package de.tum.in.niedermr.ta.core.analysis.mutation.returnvalues;

import static org.junit.Assert.assertEquals;

/** Test {@link VoidReturnValueGenerator}. */
public class VoidReturnValueGeneratorTest extends AbstractReturnValueGeneratorTest<ClassWithMethodsForMutation> {

	/** Constructor. */
	public VoidReturnValueGeneratorTest() {
		super(new VoidReturnValueGenerator(), ClassWithMethodsForMutation.class);
	}

	/** {@inheritDoc} */
	@Override
	protected void verifyMutation(Class<?> mutatedClass, Object instanceOfMutatedClass,
			ClassWithMethodsForMutation instanceOfOriginalClass) throws ReflectiveOperationException {
		assertEquals("no mutation expected", instanceOfOriginalClass.getStringValue(),
				mutatedClass.getMethod("getStringValue").invoke(instanceOfMutatedClass));
		assertEquals("no mutation expected", instanceOfOriginalClass.getIntValue(),
				mutatedClass.getMethod("getIntValue").invoke(instanceOfMutatedClass));

		// no exception expected
		mutatedClass.getMethod("voidMethod").invoke(instanceOfMutatedClass);
	}
}