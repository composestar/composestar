/*
 * Created on 22-feb-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.Core.FIRE2.model;

import java.io.Serializable;

/**
 * A transition from one execution state to an other.
 * 
 * @author Arjan de Roo
 */
public abstract class ExecutionTransition implements Serializable
{
	private static final long serialVersionUID = -4306096984983387723L;

	/*
	 * Transition labels
	 */
	@Deprecated
	public static final String CONDITION_EXPRESSION_FALSE = "<ConditionExpression-false>";

	@Deprecated
	public static final String CONDITION_EXPRESSION_TRUE = "<ConditionExpression-true>";

	@Deprecated
	public static final String CONDITION_EXPRESSION_GENERALIZATION = "ConditionExpression";

	@Deprecated
	public static final String CONDITION_OPERATOR = "<ConditionOperator>";

	@Deprecated
	public static final String CONTINUE_ACTION_ACCEPT = "<ContinueAction-accept>";

	@Deprecated
	public static final String CONTINUE_ACTION_REJECT = "<ContinueAction-reject>";

	@Deprecated
	public static final String FILTER = "<Filter>";

	@Deprecated
	public static final String FILTER_MODULE = "<FilterModule>";

	@Deprecated
	public static final String INIT_STAR_STAR = "<Init-star-star>";

	@Deprecated
	public static final String INIT_STAR_VALUE = "<Init-star-value>";

	@Deprecated
	public static final String INIT_VALUE_STAR = "<Init-value-star>";

	@Deprecated
	public static final String INIT_VALUE_VALUE = "<Init-value-value>";

	@Deprecated
	public static final String INIT_GENERALIZATION = "Init";

	@Deprecated
	public static final String NAME_MATCHING_PART_ANY_FALSE = "<NameMatchingPart-any-false>";

	@Deprecated
	public static final String NAME_MATCHING_PART_FALSE_STAR = "<NameMatchingPart-false-star>";

	@Deprecated
	public static final String NAME_MATCHING_PART_FALSE_TRUE = "<NameMatchingPart-false-true>";

	@Deprecated
	public static final String NAME_MATCHING_PART_STAR_STAR = "<NameMatchingPart-star-star>";

	@Deprecated
	public static final String NAME_MATCHING_PART_STAR_TRUE = "<NameMatchingPart-star-true>";

	@Deprecated
	public static final String NAME_MATCHING_PART_TRUE_STAR = "<NameMatchingPart-true-star>";

	@Deprecated
	public static final String NAME_MATCHING_PART_TRUE_TRUE = "<NameMatchingPart-true-true>";

	@Deprecated
	public static final String NAME_MATCHING_PART_GENERALIZATION = "NameMatchingPart";

	@Deprecated
	public static final String SIGNATURE_MATCHING_PART_FALSE = "<SignatureMatchingPart-false>";

	@Deprecated
	public static final String SIGNATURE_MATCHING_PART_TRUE_STAR = "<SignatureMatchingPart-true-star>";

	@Deprecated
	public static final String SIGNATURE_MATCHING_PART_TRUE_TRUE = "<SignatureMatchingPart-true-true>";

	@Deprecated
	public static final String SIGNATURE_MATCHING_PART_GENERALIZATION = "SignatureMatchingPart";

	@Deprecated
	public static final String SUBSTITUTION_ACTION = "<SubstitutionAction>";

	@Deprecated
	public static final String SUBSTITUTION_PART_STAR_STAR = "<SubstitutionPart-star-star>";

	@Deprecated
	public static final String SUBSTITUTION_PART_STAR_VALUE = "<SubstitutionPart-star-value>";

	@Deprecated
	public static final String SUBSTITUTION_PART_VALUE_STAR = "<SubstitutionPart-value-star>";

	@Deprecated
	public static final String SUBSTITUTION_PART_VALUE_VALUE = "<SubstitutionPart-value-value>";

	@Deprecated
	public static final String SUBSTITUTION_PART_GENERALIZATION = "SubstitutionPart";

	/**
	 * The label of this transition.
	 */
	private String label;

	/**
	 * The corresponding flowTransition;
	 */
	private FlowTransition flowTransition;

	public ExecutionTransition(String inLabel, FlowTransition transition)
	{
		super();

		label = inLabel;
		flowTransition = transition;
	}

	/**
	 * @return Returns the label.
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * @return Returns the flowTransition.
	 */
	public FlowTransition getFlowTransition()
	{
		return flowTransition;
	}

	/**
	 * @return Returns the startState.
	 */
	public abstract ExecutionState getStartState();

	/**
	 * @return Returns the endState.
	 */
	public abstract ExecutionState getEndState();

}
