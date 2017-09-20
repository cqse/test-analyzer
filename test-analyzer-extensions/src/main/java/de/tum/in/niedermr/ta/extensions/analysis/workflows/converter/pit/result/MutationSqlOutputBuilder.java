package de.tum.in.niedermr.ta.extensions.analysis.workflows.converter.pit.result;

import java.util.Optional;

import de.tum.in.niedermr.ta.core.code.identifier.MethodIdentifier;
import de.tum.in.niedermr.ta.core.code.identifier.TestcaseIdentifier;
import de.tum.in.niedermr.ta.core.common.util.StringUtility;
import de.tum.in.niedermr.ta.core.execution.id.IExecutionId;
import de.tum.in.niedermr.ta.extensions.analysis.workflows.converter.pit.PitOutputConverterWorkflow;

/** Output builder for {@link PitOutputConverterWorkflow}. */
public class MutationSqlOutputBuilder {

	/** HTML encoded name of the constructor. */
	private static final String CONSTRUCTOR_ENCODED_METHOD_NAME = "&#60;init&#62;";

	/** SQL insert statement. */
	private static final String SQL_INSERT_STATEMENT = "INSERT INTO Pit_Mutation_Result_Import "
			+ "(execution, mutatedMethod, mutationStatus, killingTestcase, mutatorName, mutationDescription) "
			+ "VALUES (%s);";

	/** Execution id. */
	private IExecutionId m_executionId;

	/** Mutation status. */
	private String m_mutationStatus;
	/** Mutated method. */
	private MethodIdentifier m_mutatedMethod;
	/** Name of the mutator. */
	private String m_mutatorName;
	/** Identifier of the test case that first killed the method. */
	private Optional<TestcaseIdentifier> m_killingTestcase;
	/** Description of the mutation. */
	private String m_mutationDescription;

	/** Constructor. */
	public MutationSqlOutputBuilder(IExecutionId executionId) {
		m_executionId = executionId;
	}

	/** {@link m_mutationStatus} */
	public void setMutationStatus(String mutationStatus) {
		m_mutationStatus = mutationStatus;
	}

	/** {@link m_mutatedMethod} */
	public void setMutatedMethod(String mutatedClassName, String mutatedMethodName, String mutatedMethodTypeSignature) {
		if (CONSTRUCTOR_ENCODED_METHOD_NAME.equals(mutatedMethodName)) {
			mutatedMethodName = MethodIdentifier.CONSTRUCTOR_NAME;
		}

		m_mutatedMethod = MethodIdentifier.create(mutatedClassName, mutatedMethodName, mutatedMethodTypeSignature);
	}

	/** {@link m_mutatorName} */
	public void setMutatorName(String mutatorName) {
		m_mutatorName = mutatorName;
	}

	/** {@link m_killingTestSignature} */
	public void setKillingTestSignature(String killingTestSignature) {
		if (StringUtility.isNullOrEmpty(killingTestSignature)) {
			m_killingTestcase = Optional.empty();
		} else {
			m_killingTestcase = Optional.of(TestcaseIdentifier.createFromJavaName(killingTestSignature));
		}
	}

	/** {@link m_mutatorDescription} */
	public void setMutationDescription(String mutationDescription) {
		m_mutationDescription = mutationDescription.replace("'", "");
	}

	/** Complete. */
	public String complete() {
		StringBuilder builder = new StringBuilder();
		builder.append(asSqlString(m_executionId.getShortId()));
		builder.append(", ");
		builder.append(asSqlString(m_mutatedMethod.get()));
		builder.append(", ");
		builder.append(asSqlString(m_mutationStatus));
		builder.append(", ");
		builder.append(
				m_killingTestcase.map(identifier -> asSqlString(identifier.toMethodIdentifier().get())).orElse("NULL"));
		builder.append(", ");
		builder.append(asSqlString(m_mutatorName));
		builder.append(", ");
		builder.append(asSqlString(m_mutationDescription));

		return String.format(SQL_INSERT_STATEMENT, builder.toString());
	}

	/** Wrap a string value in quotation marks. */
	private static String asSqlString(String value) {
		return "'" + value + "'";
	}
}