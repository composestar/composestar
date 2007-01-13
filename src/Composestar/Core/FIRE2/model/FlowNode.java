/*
 * Created on 20-feb-2006
 *
 */
package Composestar.Core.FIRE2.model;

import java.util.Iterator;

import Composestar.Core.RepositoryImplementation.RepositoryEntity;

/**
 * This class is a node in a graph, which has some annotation.
 * 
 * @author Arjan de Roo
 */
public interface FlowNode
{
	/*
	 * Names (labels) a FlowNode can have
	 */
	public final static String FLOW_ELEMENT_NODE = "FlowElement";

	public final static String FILTER_MODULE_NODE = "FilterModule";

	public final static String FILTER_NODE = "Filter";

	public final static String FILTER_ELEMENT_NODE = "FilterElement";

	public final static String MATCHING_PATTERN_NODE = "MatchingPattern";

	public final static String MATCHING_PART_NODE = "MatchingPart";

	public final static String SUBSTITUTION_PART_NODE = "SubstitutionPart";

	public final static String CONDITION_OPERATOR_NODE = "ConditionOperator";

	public final static String DISABLE_OPERATOR_NODE = "DisableOperator";

	public final static String ENABLE_OPERATOR_NODE = "EnableOperator";

	public final static String CONDITION_EXPRESSION_NODE = "ConditionExpression";

	public final static String TRUE_NODE = "True";

	public final static String FALSE_NODE = "False";

	public final static String ACTION_NODE = "FilterAction";

	public final static String FILTER_ACTION_NODE = "FilterAction";

	public final static String CONTINUE_ACTION_NODE = "ContinueFlowAction";

	public final static String EXIT_ACTION_NODE = "ExitFlowAction";

	public final static String RETURN_ACTION_NODE = "ReturnFlowAction";

	public final static String ORIGINAL_MESSAGE_ACTION_NODE = "OriginalMessageAction";

	public final static String SUBSTITUTED_MESSAGE_ACTION_NODE = "SubstitutedMessageAction";

	public final static String ANY_MESSAGE_ACTION_NODE = "AnyMessageAction";

	public final static String REJECT_CALL_ACTION_NODE = "RejectCallAction";

	public final static String ACCEPT_CALL_ACTION_NODE = "AcceptCallAction";

	public final static String REJECT_RETURN_ACTION_NODE = "RejectReturnAction";

	public final static String ACCEPT_RETURN_ACTION_NODE = "AcceptReturnAction";

	public final static String SIGNATURE_MATCHING_NODE = "SignatureMatchingPart";

	public final static String NAME_MATCHING_NODE = "NameMatchingPart";

	public final static String FILTER_COMP_OPER_NODE = "FilterCompOper";

	public final static String SEQ_FILTER_COMP_OPER_NODE = "SEQFilterCompOper";

	public final static String VOID_FILTER_COMP_OPER_NODE = "VoidFilterCompOper";

	public final static String FE_COMP_OPER_NODE = "CompOper";

	public final static String FE_COR_COMP_OPER_NODE = "CORCompOper";

	public final static String FE_VOID_COMP_OPER_NODE = "VoidCompOper";

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
	public final static String END_NODE = "End";

	/**
	 * The stop node marks the exit of the filterset. The stop node is reached
	 * after a filter action that does not continue or return the flow, for
	 * example an Error action. Examples of actions that do continue the flow
	 * are the Substitution action, the Before action, the Wait action and the
	 * Continue action. Examples of actions that return the flow are the
	 * Dispatch action and the Skip action.
	 */
	public final static String STOP_NODE = "Stop";

	/**
	 * The return node marks the return through the filterset. Examples of
	 * actions that return the flow are the Dispatch action and the Skip action.
	 */
	public final static String RETURN_NODE = "Return";

	/*
	 * Some labels that indicate a class of nodes
	 */
	public final static String CONTEXT_NODE = "ContextNode";

	public final static String PROCEDURE_NODE = "ProcedureNode";

	public final static String PREDICATE_NODE = "PredicateNode";

	public Iterator getTransitions();

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
	 */
	public Iterator getNames();

	public boolean containsName(String name);

	/**
	 * @return Returns the repositoryLink.
	 */
	public RepositoryEntity getRepositoryLink();
}
