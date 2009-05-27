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
	String FLOW_NODE = "FlowNode";

	/**
	 * Filter modules
	 */
	String FILTER_MODULE_NODE = "FilterModule";

	/**
	 * Used for all filter expression nodes
	 */
	String FILTER_EXPRESSION_NODE = "FilterExpression";

	/**
	 * Used for the sequential filter operator (';' in the language)
	 */
	String SEQUENTIAL_FILTER_OPER_NODE = "SequentialFilterOper";

	/**
	 * A filter node (this is not a filter type)
	 */
	String FILTER_NODE = "Filter";

	/**
	 * All filter element expressions (cor-operator, filter element, ...)
	 */
	String FILTER_ELEMENT_EXPRESSION_NODE = "FilterElementExpression";

	/**
	 * The conditional OR operator for filter elements
	 */
	String COR_NODE = "COR";

	String FILTER_ELEMENT_NODE = "FilterElement";

	String MATCHING_EXPRESSION_NODE = "Expression";

	String MATCH_AND_NODE = "And";

	String MATCH_OR_NODE = "Or";

	String MATCH_NOT_NODE = "Not";

	/**
	 * Used for all compare statements
	 */
	String COMPARE_STATEMENT_NODE = "CompareStatement";

	String INSTANCE_MATCHING = "CmpInstance";

	String SIGNATURE_MATCHING = "CmpSignature";

	String COMPATIBILITY_MATCHING = "CmpCompatible";

	String ANNOTATION_MATCHING = "CmpAnnotation";

	/**
	 * Used for matching expression nodes that link to a condition
	 */
	String CONDITION_NODE = "Condition";

	String ASSIGNMENT_NODE = "Assignment";

	/**
	 * Used for a literal True in the matching expression
	 */
	String TRUE_NODE = "True";

	/**
	 * Used for a literal False in the matching expression
	 */
	String FALSE_NODE = "False";

	String FILTER_ACTION_NODE = "FilterAction";

	String CONTINUE_ACTION_NODE = "ContinueFlowAction";

	String EXIT_ACTION_NODE = "ExitFlowAction";

	String RETURN_ACTION_NODE = "ReturnFlowAction";

	String REJECT_CALL_ACTION_NODE = "RejectCallAction";

	String ACCEPT_CALL_ACTION_NODE = "AcceptCallAction";

	String REJECT_RETURN_ACTION_NODE = "RejectReturnAction";

	String ACCEPT_RETURN_ACTION_NODE = "AcceptReturnAction";

	/**
	 * Created by FlowModel for the filter module binding conditions
	 */
	String FM_CONDITION_NODE = "FilterModuleCondition";

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
	String END_NODE = "End";

	/**
	 * The stop node marks the exit of the filterset. The stop node is reached
	 * after a filter action that does not continue or return the flow, for
	 * example an Error action. Examples of actions that do continue the flow
	 * are the Substitution action, the Before action, the Wait action and the
	 * Continue action. Examples of actions that return the flow are the
	 * Dispatch action and the Skip action.
	 */
	String EXIT_NODE = "Exit";

	/**
	 * The return node marks the return through the filterset. Examples of
	 * actions that return the flow are the Dispatch action and the Skip action.
	 */
	String RETURN_NODE = "Return";

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
