package Composestar.Core.CORE2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElement;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.True;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.model.FireModel;
import Composestar.Core.FIRE2.model.FlowModel;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.model.FlowTransition;
import Composestar.Core.FIRE2.util.iterator.ExecutionStateIterator;
import Composestar.Core.FIRE2.util.iterator.OrderedFlowNodeIterator;

public class CoreConflictDetector
{

	private FlowNode currentFilterNode;

	private int rejectingFilterElementCounter;

	private int filterElementCounter;

	private boolean unreachableFilter = false;

	private CoreConflict accRejFilterConflict;

	private FlowNode currentFilterElementNode;

	private FlowNode currentConditionOperatorNode;

	private FlowNode currentMatchingPatternNode;

	private ArrayList currentActionNodes = new ArrayList();

	private ArrayList unreachableActionNodes = new ArrayList();

	private CoreConflict acceptingFilterElementConflict;

	public CoreConflictDetector()
	{

	}

	public CoreConflict[] findConflicts(FireModel fireModel)
	{
		// Input filters:
		ExecutionModel execModel = fireModel.getExecutionModel(FireModel.INPUT_FILTERS);
		FlowModel flowModel = fireModel.getFlowModel(FireModel.INPUT_FILTERS);
		Set reachableParts = findReachableParts(execModel);
		List conflicts = checkForConflicts(flowModel, reachableParts);

		// Output filters:
		execModel = fireModel.getExecutionModel(FireModel.OUTPUT_FILTERS);
		flowModel = fireModel.getFlowModel(FireModel.OUTPUT_FILTERS);
		reachableParts = findReachableParts(execModel);
		conflicts.addAll(checkForConflicts(flowModel, reachableParts));

		return (CoreConflict[]) conflicts.toArray(new CoreConflict[conflicts.size()]);
	}

	private Set findReachableParts(ExecutionModel execModel)
	{
		// Create HashSet to store all reachable nodes and transitions
		HashSet reachableParts = new HashSet();

		// Iterate over all states
		Iterator stateIter = new ExecutionStateIterator(execModel);
		while (stateIter.hasNext())
		{
			ExecutionState state = (ExecutionState) stateIter.next();

			// Add corresponding FlowNode to reachable FlowNodes
			FlowNode flowNode = state.getFlowNode();
			reachableParts.add(flowNode);

			// Traverse outgoing transitions
			Iterator transitionIter = state.getOutTransitions();
			while (transitionIter.hasNext())
			{
				ExecutionTransition transition = (ExecutionTransition) transitionIter.next();

				// Add corresponding FlowTransition to reachable FlowTransitions
				FlowTransition flowTransition = transition.getFlowTransition();
				reachableParts.add(flowTransition);
			}
		}

		return reachableParts;
	}

	private List checkForConflicts(FlowModel flowModel, Set reachableParts)
	{
		List conflicts = new ArrayList();
		int transitionCount = 0;// FIXME Only for testing

		// Iterate over the flowModel
		OrderedFlowNodeIterator nodeIter = new OrderedFlowNodeIterator(flowModel);
		while (nodeIter.hasNext())
		{
			FlowNode node = (FlowNode) nodeIter.next();

			// Maintain certain nodes
			if (node.containsName(FlowNode.FILTER_NODE))
			{
				// Add filter conflicts
				if (currentFilterNode != null)
				{
					conflicts.addAll(identifyFilterConflicts());
				}
				unreachableFilter = false;
				currentFilterNode = node;
			}
			else if (node.containsName(FlowNode.FILTER_ELEMENT_NODE))
			{
				currentFilterElementNode = node;
				filterElementCounter++;
			}
			else if (node.containsName(FlowNode.CONDITION_OPERATOR_NODE))
			{
				currentConditionOperatorNode = node;
			}
			else if (node.containsName(FlowNode.MATCHING_PART_NODE))
			{
				// FIXME, should be matching pattern node
				currentMatchingPatternNode = node;
			}
			else if (node.containsName(FlowNode.ACTION_NODE))
			{
				currentActionNodes.add(node);
			}

			// Check whether node is unreachable
			if (!reachableParts.contains(node))
			{
				conflicts.addAll(identifyConflicts(node));
			}
			else
			{
				// Check whether outgoing transitions are unreachable
				Iterator transitionIter = node.getTransitions();
				while (transitionIter.hasNext())
				{
					transitionCount++;// FIXME Only for testing
					FlowTransition transition = (FlowTransition) transitionIter.next();
					if (!reachableParts.contains(transition))
					{
						conflicts.addAll(identifyConflicts(transition));
					}
				}

			}
		}

		System.out.println("Transitions:" + transitionCount);// FIXME Only
		// for testing

		return conflicts;
	}

	private List identifyFilterConflicts()
	{
		List conflicts = new ArrayList();

		if (currentFilterNode != null && rejectingFilterElementCounter == filterElementCounter)
		{
			CoreConflict conflict = new CoreConflict(CoreConflict.FILTER_ALWAYS_REJECTS, (Filter) currentFilterNode
					.getRepositoryLink());
			conflicts.add(conflict);

		}

		if (!unreachableActionNodes.isEmpty())
		{
			boolean onlyContinue = true;
			Iterator actionIter = currentActionNodes.iterator();
			for (Object currentActionNode : currentActionNodes)
			{
				FlowNode actionNode = (FlowNode) currentActionNode;
				if (!unreachableActionNodes.contains(actionNode)
						&& !actionNode.containsName(FlowNode.CONTINUE_ACTION_NODE))
				{
					onlyContinue = false;
					break;
				}
			}

			if (onlyContinue)
			{
				CoreConflict conflict = new CoreConflict(CoreConflict.FILTER_REDUNDANT, (Filter) currentFilterNode
						.getRepositoryLink(), accRejFilterConflict);
				conflicts.add(conflict);
			}
		}

		rejectingFilterElementCounter = 0;
		filterElementCounter = 0;

		currentActionNodes.clear();
		unreachableActionNodes.clear();

		return conflicts;
	}

	private List identifyConflicts(FlowNode unreachableNode)
	{
		List conflicts = new ArrayList();

		// If filter unreachable, just return instead of also notifying
		// unreachable filter elements etc.
		if (unreachableFilter)
		{
			return conflicts;
		}

		if (unreachableNode.containsName(FlowNode.FILTER_NODE))
		{
			unreachableFilter = true;

			CoreConflict conflict = new CoreConflict(CoreConflict.FILTER_UNREACHABLE, unreachableNode
					.getRepositoryLink(), accRejFilterConflict);
			conflicts.add(conflict);
		}
		else if (unreachableNode.containsName(FlowNode.FILTER_ELEMENT_NODE))
		{
			CoreConflict conflict = new CoreConflict(CoreConflict.FILTER_ELEMENT_UNREACHABLE, unreachableNode
					.getRepositoryLink(), acceptingFilterElementConflict);
			conflicts.add(conflict);
		}
		else if (unreachableNode.containsName(FlowNode.MATCHING_PATTERN_NODE))
		{
			CoreConflict conflict = new CoreConflict(CoreConflict.MATCHING_PATTERN_UNREACHABLE, unreachableNode
					.getRepositoryLink());
			conflicts.add(conflict);
		}
		else if (unreachableNode.containsName(FlowNode.ACTION_NODE))
		{
			// TODO implement unreachable action conflicts
		}

		return conflicts;
	}

	private List identifyConflicts(FlowTransition unreachableTransition)
	{
		List conflicts = new ArrayList();

		// If filter unreachable, just return instead of also notifying
		// unreachable filter elements etc.
		if (unreachableFilter)
		{
			return conflicts;
		}

		if (unreachableTransition.getType() == FlowTransition.FLOW_TRUE_TRANSITION)
		{
			if (unreachableTransition.getStartNode().containsName(FlowNode.CONDITION_EXPRESSION_NODE))
			{
				// Condition expression
				CoreConflict conflict1 = new CoreConflict(CoreConflict.CONDITION_EXPRESSION_FALSE,
						unreachableTransition.getStartNode().getRepositoryLink());
				conflicts.add(conflict1);

				CoreConflict conflict2 = new CoreConflict(CoreConflict.FILTER_ELEMENT_ALWAYS_REJECTS,
						currentFilterElementNode.getRepositoryLink(), conflict1);
				conflicts.add(conflict2);
				rejectingFilterElementCounter++;

				CoreConflict conflict3 = new CoreConflict(CoreConflict.FILTER_ELEMENT_REDUNDANT,
						currentFilterElementNode.getRepositoryLink(), conflict2);
				conflicts.add(conflict3);
			}
			else
			{
				// Matching part
				CoreConflict conflict1 = new CoreConflict(CoreConflict.MATCHING_PART_NEVER_MATCHES,
						unreachableTransition.getStartNode().getRepositoryLink());
				conflicts.add(conflict1);

				if (currentConditionOperatorNode.containsName(FlowNode.ENABLE_OPERATOR_NODE))
				{
					CoreConflict conflict2 = new CoreConflict(CoreConflict.MATCHING_PATTERN_ALWAYS_REJECTS,
							currentMatchingPatternNode.getRepositoryLink(), conflict1);

					conflicts.add(conflict2);

					// TODO: add always rejecting filter element check
					CoreConflict conflict3 = new CoreConflict(CoreConflict.FILTER_ELEMENT_ALWAYS_REJECTS,
							currentFilterElementNode.getRepositoryLink(), conflict2);
					conflicts.add(conflict3);
					rejectingFilterElementCounter++;

					CoreConflict conflict4 = new CoreConflict(CoreConflict.FILTER_ELEMENT_REDUNDANT,
							currentFilterElementNode.getRepositoryLink(), conflict3);
					conflicts.add(conflict4);
				}
				else
				{
					CoreConflict conflict2 = new CoreConflict(CoreConflict.MATCHING_PATTERN_ALWAYS_ACCEPTS,
							currentMatchingPatternNode.getRepositoryLink(), conflict1);
					conflicts.add(conflict2);

					FilterElement filterElement = (FilterElement) currentFilterElementNode.getRepositoryLink();
					if (filterElement.getConditionPart() instanceof True)
					{
						CoreConflict conflict3 = new CoreConflict(CoreConflict.FILTER_ELEMENT_ALWAYS_ACCEPTS,
								currentFilterElementNode.getRepositoryLink(), conflict2);
						acceptingFilterElementConflict = conflict3;
						conflicts.add(conflict3);

						CoreConflict conflict4 = new CoreConflict(CoreConflict.FILTER_ALWAYS_ACCEPTS,
								currentFilterElementNode.getRepositoryLink(), conflict3);
						accRejFilterConflict = conflict4;
						conflicts.add(conflict4);
					}
				}
			}

		}
		else if (unreachableTransition.getType() == FlowTransition.FLOW_FALSE_TRANSITION)
		{
			if (unreachableTransition.getStartNode().containsName(FlowNode.CONDITION_EXPRESSION_NODE))
			{
				// Condition expression
				// No conflict
			}
			else
			{
				// Matching part
				CoreConflict conflict1 = new CoreConflict(CoreConflict.MATCHING_PART_ALWAYS_MATCHES,
						unreachableTransition.getStartNode().getRepositoryLink());
				conflicts.add(conflict1);

				if (currentConditionOperatorNode.containsName(FlowNode.DISABLE_OPERATOR_NODE))
				{
					CoreConflict conflict2 = new CoreConflict(CoreConflict.MATCHING_PATTERN_ALWAYS_REJECTS,
							currentMatchingPatternNode.getRepositoryLink(), conflict1);
					conflicts.add(conflict2);

					// TODO: add always rejecting filter element check
					CoreConflict conflict3 = new CoreConflict(CoreConflict.FILTER_ELEMENT_ALWAYS_REJECTS,
							currentFilterElementNode.getRepositoryLink(), conflict2);
					conflicts.add(conflict3);
					rejectingFilterElementCounter++;

					CoreConflict conflict4 = new CoreConflict(CoreConflict.FILTER_ELEMENT_REDUNDANT,
							currentFilterElementNode.getRepositoryLink(), conflict3);
					conflicts.add(conflict4);
				}
				else
				{
					CoreConflict conflict2 = new CoreConflict(CoreConflict.MATCHING_PATTERN_ALWAYS_ACCEPTS,
							currentMatchingPatternNode.getRepositoryLink(), conflict1);
					conflicts.add(conflict2);

					FilterElement filterElement = (FilterElement) currentFilterElementNode.getRepositoryLink();
					if (filterElement.getConditionPart() instanceof True)
					{
						CoreConflict conflict3 = new CoreConflict(CoreConflict.FILTER_ELEMENT_ALWAYS_ACCEPTS,
								currentFilterElementNode.getRepositoryLink(), conflict2);
						acceptingFilterElementConflict = conflict3;
						conflicts.add(conflict3);

						CoreConflict conflict4 = new CoreConflict(CoreConflict.FILTER_ALWAYS_ACCEPTS,
								currentFilterElementNode.getRepositoryLink(), conflict3);
						accRejFilterConflict = conflict4;
						conflicts.add(conflict4);
					}
				}
			}
		}

		return conflicts;
	}

	// public void check(Concern concern, FilterModuleOrder modules)
	// {
	// ExecutionState state;
	// ExecutionTransition transition;
	// FlowNode flowNode;
	// FlowTransition flowTransition;
	//
	// HashSet visitedNodes = new HashSet();
	// HashSet visitedTransitions = new HashSet();
	// Hashtable filterContinueTable = new Hashtable();
	//
	// FireModel fireModel = new FireModel(concern, modules, true);
	// ExecutionModel execModel = fireModel.getExecutionModel();
	// ExecutionStateIterator iterator = new ExecutionStateIterator(execModel);
	//
	// Iterator it;
	//
	// while (iterator.hasNext())
	// {
	// state = (ExecutionState) iterator.next();
	// flowNode = state.getFlowNode();
	// visitedNodes.add(flowNode);
	//
	// if (flowNode.containsName(FlowNode.ACTION_NODE))
	// {
	// Filter filter = (Filter) flowNode.getRepositoryLink();
	// if (flowNode.containsName("ContinueAction"))
	// {
	// if (!filterContinueTable.containsKey(filter))
	// {
	// // put boolean in the table indicating that for the
	// // filter until now only continueactions are found:
	// filterContinueTable.put(filter, Boolean.TRUE);
	// }
	// }
	// else
	// {
	// // put boolean in the table indicating that for the
	// // filter not only continueactions are found:
	// filterContinueTable.put(filter, Boolean.FALSE);
	// }
	// }
	//
	// it = state.getOutTransitions();
	// while (it.hasNext())
	// {
	// transition = (ExecutionTransition) it.next();
	// visitedTransitions.add(transition.getFlowTransition());
	// }
	// }
	//
	// // check useless filters (filters that only continue):
	// it = filterContinueTable.entrySet().iterator();
	// while (it.hasNext())
	// {
	// Entry entry = (Entry) it.next();
	// Filter filter = (Filter) entry.getKey();
	// Boolean b = (Boolean) entry.getValue();
	// if (b.booleanValue())
	// {
	// Debug.out(Debug.MODE_ERROR, Core.MODULE_NAME, "Redundant filter found!",
	// filter);
	// }
	// }
	//
	// FlowModel[] flowModels = fireModel.getFlowModels();
	// for (int i = 0; i < flowModels.length; i++)
	// {
	//
	// // unreachable matchingparts:
	// it = flowModels[i].getNodes();
	// while (it.hasNext())
	// {
	// flowNode = (FlowNode) it.next();
	//
	// if (!visitedNodes.contains(flowNode) &&
	// flowNode.containsName("MatchingPart"))
	// {
	// Debug.out(Debug.MODE_ERROR, Core.MODULE_NAME, "Unreachable matchingpart
	// found!", flowNode
	// .getRepositoryLink());
	// }
	// }
	//
	// // matchingparts that always accept or reject:
	// it = flowModels[i].getTransitions();
	// while (it.hasNext())
	// {
	// flowTransition = (FlowTransition) it.next();
	// if (!visitedTransitions.contains(flowTransition))
	// {
	// FlowNode startNode = flowTransition.getStartNode();
	// if (visitedNodes.contains(startNode) &&
	// startNode.containsName("MatchingPart"))
	// {
	// MatchingPart part = (MatchingPart) startNode.getRepositoryLink();
	// if (flowTransition.getType() == FlowTransition.FLOW_TRUE_TRANSITION)
	// {
	// Debug.out(Debug.MODE_WARNING, Core.MODULE_NAME, "Matchingpart never
	// accepts!", startNode
	// .getRepositoryLink());
	// }
	// else if (!part.getSelector().getName().equals("*") ||
	// !part.getTarget().getName().equals("*"))
	// {
	// Debug.out(Debug.MODE_WARNING, Core.MODULE_NAME, "Matchingpart always
	// accepts!", startNode
	// .getRepositoryLink());
	// }
	//
	// }
	// }
	// }
	// }
	// }
}
