/*
 * Created on 20-feb-2006
 *
 */
package Composestar.Core.FIRE2.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import Composestar.Core.CpsRepository2.RepositoryEntity;

/**
 * This class is a node in a graph, which has some annotation.
 * 
 * @author Arjan de Roo
 */
public interface FlowNode extends Serializable
{
	/*
	 * Names (labels) a FlowNode can have
	 */

	/**
	 * Label that every node that participates in the flow contains
	 */
	public static final String FLOW_NODE = "FlowNode";

	@Deprecated
	public static final String FLOW_ELEMENT_NODE = "FlowElement";

	/**
	 * Filter modules
	 */
	public static final String FILTER_MODULE_NODE = "FilterModule";

	/**
	 * Used for all filter expression nodes
	 */
	public static final String FILTER_EXPRESSION_NODE = "FilterExpression";

	/**
	 * Used for the sequential filter operator (';' in the language)
	 */
	public static final String SEQUENTIAL_FILTER_OPER_NODE = "SequentialFilterOper";

	/**
	 * A filter node (this is not a filter type)
	 */
	public static final String FILTER_NODE = "Filter";

	/**
	 * All filter element expressions (cor-operator, filter element, ...)
	 */
	public static final String FILTER_ELEMENT_EXPRESSION_NODE = "FilterElementExpression";

	/**
	 * The conditional OR operator for filter elements
	 */
	public static final String COR_NODE = "COR";

	public static final String FILTER_ELEMENT_NODE = "FilterElement";

	public static final String MATCHING_EXPRESSION_NODE = "Expression";

	public static final String MATCH_AND_NODE = "And";

	public static final String MATCH_OR_NODE = "Or";

	public static final String MATCH_NOT_NODE = "Not";

	/**
	 * Used for all compare statements
	 */
	public static final String COMPARE_STATEMENT_NODE = "CompareStatement";

	public static final String INSTANCE_MATCHING = "CmpInstance";

	public static final String SIGNATURE_MATCHING = "CmpSignature";

	public static final String COMPATIBILITY_MATCHING = "CmpCompatible";

	public static final String ANNOTATION_MATCHING = "CmpAnnotation";

	/**
	 * Used for matching expression nodes that link to a condition
	 */
	public static final String CONDITION_NODE = "Condition";

	public static final String ASSIGNMENT_NODE = "Assignment";

	@Deprecated
	public static final String MATCHING_PATTERN_NODE = "MatchingPattern";

	@Deprecated
	public static final String MATCHING_PART_NODE = "MatchingPart";

	@Deprecated
	public static final String SUBSTITUTION_PART_NODE = "SubstitutionPart";

	@Deprecated
	public static final String CONDITION_OPERATOR_NODE = "ConditionOperator";

	@Deprecated
	public static final String DISABLE_OPERATOR_NODE = "DisableOperator";

	@Deprecated
	public static final String ENABLE_OPERATOR_NODE = "EnableOperator";

	@Deprecated
	public static final String CONDITION_EXPRESSION_NODE = "ConditionExpression";

	/**
	 * Used for a literal True in the matching expression
	 */
	public static final String TRUE_NODE = "True";

	/**
	 * Used for a literal False in the matching expression
	 */
	public static final String FALSE_NODE = "False";

	@Deprecated
	public static final String ACTION_NODE = "FilterAction";

	public static final String FILTER_ACTION_NODE = "FilterAction";

	public static final String CONTINUE_ACTION_NODE = "ContinueFlowAction";

	public static final String EXIT_ACTION_NODE = "ExitFlowAction";

	public static final String RETURN_ACTION_NODE = "ReturnFlowAction";

	@Deprecated
	public static final String ORIGINAL_MESSAGE_ACTION_NODE = "OriginalMessageAction";

	@Deprecated
	public static final String SUBSTITUTED_MESSAGE_ACTION_NODE = "SubstitutedMessageAction";

	@Deprecated
	public static final String ANY_MESSAGE_ACTION_NODE = "AnyMessageAction";

	public static final String REJECT_CALL_ACTION_NODE = "RejectCallAction";

	public static final String ACCEPT_CALL_ACTION_NODE = "AcceptCallAction";

	public static final String REJECT_RETURN_ACTION_NODE = "RejectReturnAction";

	public static final String ACCEPT_RETURN_ACTION_NODE = "AcceptReturnAction";

	@Deprecated
	public static final String SIGNATURE_MATCHING_NODE = "SignatureMatchingPart";

	@Deprecated
	public static final String NAME_MATCHING_NODE = "NameMatchingPart";

	@Deprecated
	public static final String FILTER_COMP_OPER_NODE = "FilterCompOper";

	@Deprecated
	public static final String SEQ_FILTER_COMP_OPER_NODE = "SEQFilterCompOper";

	@Deprecated
	public static final String VOID_FILTER_COMP_OPER_NODE = "VoidFilterCompOper";

	@Deprecated
	public static final String FE_COMP_OPER_NODE = "CompOper";

	@Deprecated
	public static final String FE_COR_COMP_OPER_NODE = "CORCompOper";

	@Deprecated
	public static final String FE_VOID_COMP_OPER_NODE = "VoidCompOper";

	@Deprecated
	public static final String FM_CONDITION_NODE = "FilterModuleCondition";

	/**
	 * In the flowmodel of a filter module, this node marks the end of the flow
	 * in the current module, proceeding to the next filter module. This node is
	 * reached after an action that continues the flow of the last filter in the
	 * filter module. In this case flow should continue to the next filter. But
	 * because the next filter is in the following filter module, this
	 * continuation if indicated by the End node. <br>
	 * <br>
	 * In the flowmodel of a complete filter set this node marks the end of the
	 * flow in that filter set, after the last filter did an action that
	 * continues the flow. Flow should have continued to a next filter, but
	 * because there is no next filter, flow continues to the end node. In the
	 * execution models of the selectors in the signature of a concern, this end
	 * node should not be reached. Because reaching this end state means no
	 * Dispatch has been done. This indicates a conflict, to be reported by
	 * SIGN.
	 */
	public static final String END_NODE = "End";

	/**
	 * The stop node marks the exit of the filterset. The stop node is reached
	 * after a filter action that does not continue or return the flow, for
	 * example an Error action. Examples of actions that do continue the flow
	 * are the Substitution action, the Before action, the Wait action and the
	 * Continue action. Examples of actions that return the flow are the
	 * Dispatch action and the Skip action.
	 */
	public static final String EXIT_NODE = "Exit";

	/**
	 * The return node marks the return through the filterset. Examples of
	 * actions that return the flow are the Dispatch action and the Skip action.
	 */
	public static final String RETURN_NODE = "Return";

	@Deprecated
	public static final String CONTEXT_NODE = "ContextNode";

	@Deprecated
	public static final String PROCEDURE_NODE = "ProcedureNode";

	@Deprecated
	public static final String PREDICATE_NODE = "PredicateNode";

	/**
	 * @deprecated use getTransitionsEx();
	 * @return
	 */
	@Deprecated
	public Iterator<FlowTransition> getTransitions();

	/**
	 * @return the outgoing transitions
	 */
	public List<FlowTransition> getTransitionsEx();

	/**
	 * Returns the (first) transition from this startnode to the given endnode,
	 * or null when no such transition exists.
	 * 
	 * @return
	 * @param endNode
	 */
	public FlowTransition getTransition(FlowNode endNode);

	/**
	 * @return Returns the names.
	 * @deprecated use getNamesEx();
	 */
	@Deprecated
	public Iterator<String> getNames();

	/**
	 * @return the names/labels of this node
	 */
	public Set<String> getNamesEx();

	/**
	 * @param name
	 * @return true if this node that the given name/label
	 */
	public boolean containsName(String name);

	/**
	 * @return Returns the repositoryLink.
	 */
	public RepositoryEntity getRepositoryLink();
}
