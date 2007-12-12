/*
 * Created on 20-feb-2006
 *
 */
package Composestar.Core.FIRE2.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import Composestar.Core.RepositoryImplementation.RepositoryEntity;

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
	public static final String FLOW_ELEMENT_NODE = "FlowElement";

	public static final String FILTER_MODULE_NODE = "FilterModule";

	public static final String FILTER_NODE = "Filter";

	public static final String FILTER_ELEMENT_NODE = "FilterElement";

	public static final String MATCHING_PATTERN_NODE = "MatchingPattern";

	public static final String MATCHING_PART_NODE = "MatchingPart";

	public static final String SUBSTITUTION_PART_NODE = "SubstitutionPart";

	public static final String CONDITION_OPERATOR_NODE = "ConditionOperator";

	public static final String DISABLE_OPERATOR_NODE = "DisableOperator";

	public static final String ENABLE_OPERATOR_NODE = "EnableOperator";

	public static final String CONDITION_EXPRESSION_NODE = "ConditionExpression";

	public static final String TRUE_NODE = "True";

	public static final String FALSE_NODE = "False";

	public static final String ACTION_NODE = "FilterAction";

	public static final String FILTER_ACTION_NODE = "FilterAction";

	public static final String CONTINUE_ACTION_NODE = "ContinueFlowAction";

	public static final String EXIT_ACTION_NODE = "ExitFlowAction";

	public static final String RETURN_ACTION_NODE = "ReturnFlowAction";

	public static final String ORIGINAL_MESSAGE_ACTION_NODE = "OriginalMessageAction";

	public static final String SUBSTITUTED_MESSAGE_ACTION_NODE = "SubstitutedMessageAction";

	public static final String ANY_MESSAGE_ACTION_NODE = "AnyMessageAction";

	public static final String REJECT_CALL_ACTION_NODE = "RejectCallAction";

	public static final String ACCEPT_CALL_ACTION_NODE = "AcceptCallAction";

	public static final String REJECT_RETURN_ACTION_NODE = "RejectReturnAction";

	public static final String ACCEPT_RETURN_ACTION_NODE = "AcceptReturnAction";

	public static final String SIGNATURE_MATCHING_NODE = "SignatureMatchingPart";

	public static final String NAME_MATCHING_NODE = "NameMatchingPart";

	public static final String FILTER_COMP_OPER_NODE = "FilterCompOper";

	public static final String SEQ_FILTER_COMP_OPER_NODE = "SEQFilterCompOper";

	public static final String VOID_FILTER_COMP_OPER_NODE = "VoidFilterCompOper";

	public static final String FE_COMP_OPER_NODE = "CompOper";

	public static final String FE_COR_COMP_OPER_NODE = "CORCompOper";

	public static final String FE_VOID_COMP_OPER_NODE = "VoidCompOper";

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
	public static final String STOP_NODE = "Exit";

	/**
	 * The return node marks the return through the filterset. Examples of
	 * actions that return the flow are the Dispatch action and the Skip action.
	 */
	public static final String RETURN_NODE = "Return";

	/*
	 * Some labels that indicate a class of nodes
	 */
	public static final String CONTEXT_NODE = "ContextNode";

	public static final String PROCEDURE_NODE = "ProcedureNode";

	public static final String PREDICATE_NODE = "PredicateNode";

	/**
	 * @deprecated use getTransitionsEx();
	 * @return
	 */
	@Deprecated
	public Iterator<FlowTransition> getTransitions();

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

	public Set<String> getNamesEx();

	public boolean containsName(String name);

	/**
	 * @return Returns the repositoryLink.
	 */
	public RepositoryEntity getRepositoryLink();
}
