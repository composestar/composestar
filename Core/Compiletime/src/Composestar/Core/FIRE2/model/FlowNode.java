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
	final String FLOW_NODE = "FlowNode";

	/**
	 * Filter modules
	 */
	final String FILTER_MODULE_NODE = "FilterModule";

	/**
	 * Used for all filter expression nodes
	 */
	final String FILTER_EXPRESSION_NODE = "FilterExpression";

	/**
	 * Used for the sequential filter operator (';' in the language)
	 */
	final String SEQUENTIAL_FILTER_OPER_NODE = "SequentialFilterOper";

	/**
	 * A filter node (this is not a filter type)
	 */
	final String FILTER_NODE = "Filter";

	/**
	 * All filter element expressions (cor-operator, filter element, ...)
	 */
	final String FILTER_ELEMENT_EXPRESSION_NODE = "FilterElementExpression";

	/**
	 * The conditional OR operator for filter elements
	 */
	final String COR_NODE = "COR";

	final String FILTER_ELEMENT_NODE = "FilterElement";

	final String MATCHING_EXPRESSION_NODE = "Expression";

	final String MATCH_AND_NODE = "And";

	final String MATCH_OR_NODE = "Or";

	final String MATCH_NOT_NODE = "Not";

	/**
	 * Used for all compare statements
	 */
	final String COMPARE_STATEMENT_NODE = "CompareStatement";

	final String INSTANCE_MATCHING = "CmpInstance";

	final String SIGNATURE_MATCHING = "CmpSignature";

	final String COMPATIBILITY_MATCHING = "CmpCompatible";

	final String ANNOTATION_MATCHING = "CmpAnnotation";

	/**
	 * Used for matching expression nodes that link to a condition
	 */
	final String CONDITION_NODE = "Condition";

	final String ASSIGNMENT_NODE = "Assignment";

	/**
	 * Used for a literal True in the matching expression
	 */
	final String TRUE_NODE = "True";

	/**
	 * Used for a literal False in the matching expression
	 */
	final String FALSE_NODE = "False";

	final String FILTER_ACTION_NODE = "FilterAction";

	final String CONTINUE_ACTION_NODE = "ContinueFlowAction";

	final String EXIT_ACTION_NODE = "ExitFlowAction";

	final String RETURN_ACTION_NODE = "ReturnFlowAction";

	final String REJECT_CALL_ACTION_NODE = "RejectCallAction";

	final String ACCEPT_CALL_ACTION_NODE = "AcceptCallAction";

	final String REJECT_RETURN_ACTION_NODE = "RejectReturnAction";

	final String ACCEPT_RETURN_ACTION_NODE = "AcceptReturnAction";

	/**
	 * Created by FlowModel for the filter module binding conditions
	 */
	final String FM_CONDITION_NODE = "FilterModuleCondition";

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
	final String END_NODE = "End";

	/**
	 * The stop node marks the exit of the filterset. The stop node is reached
	 * after a filter action that does not continue or return the flow, for
	 * example an Error action. Examples of actions that do continue the flow
	 * are the Substitution action, the Before action, the Wait action and the
	 * Continue action. Examples of actions that return the flow are the
	 * Dispatch action and the Skip action.
	 */
	final String EXIT_NODE = "Exit";

	/**
	 * The return node marks the return through the filterset. Examples of
	 * actions that return the flow are the Dispatch action and the Skip action.
	 */
	final String RETURN_NODE = "Return";

	/**
	 * @deprecated use getTransitionsEx();
	 * @return
	 */
	@Deprecated
	Iterator<FlowTransition> getTransitions();

	/**
	 * @return the outgoing transitions
	 */
	List<FlowTransition> getTransitionsEx();

	/**
	 * Returns the (first) transition from this startnode to the given endnode,
	 * or null when no such transition exists.
	 * 
	 * @return
	 * @param endNode
	 */
	FlowTransition getTransition(FlowNode endNode);

	/**
	 * @return Returns the names.
	 * @deprecated use getNamesEx();
	 */
	@Deprecated
	Iterator<String> getNames();

	/**
	 * @return the names/labels of this node
	 */
	Set<String> getNamesEx();

	/**
	 * @param name
	 * @return true if this node that the given name/label
	 */
	boolean containsName(String name);

	/**
	 * @return Returns the repositoryLink.
	 */
	RepositoryEntity getRepositoryLink();
}
