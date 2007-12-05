package Composestar.Core.CORE2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElement;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.True;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.model.FireModel;
import Composestar.Core.FIRE2.model.FlowModel;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.model.FlowTransition;
import Composestar.Core.FIRE2.model.FireModel.FilterDirection;
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

	private List<FlowNode> currentActionNodes = new ArrayList<FlowNode>();

	private List<FlowNode> unreachableActionNodes = new ArrayList<FlowNode>();

	private CoreConflict acceptingFilterElementConflict;

	public CoreConflictDetector()
	{}

	public CoreConflict[] findConflicts(FireModel fireModel)
	{
		// Input filters:
		ExecutionModel execModel = fireModel.getExecutionModel(FilterDirection.Input);
		FlowModel flowModel = fireModel.getFlowModel(FilterDirection.Input);
		// Set contains FlowNode and FlowTransition elements
		Set<Object> reachableParts = findReachableParts(execModel);
		List<CoreConflict> conflicts = checkForConflicts(flowModel, reachableParts);

		// Output filters:
		execModel = fireModel.getExecutionModel(FilterDirection.Output);
		flowModel = fireModel.getFlowModel(FilterDirection.Output);
		reachableParts = findReachableParts(execModel);
		conflicts.addAll(checkForConflicts(flowModel, reachableParts));

		return conflicts.toArray(new CoreConflict[conflicts.size()]);
	}

	private Set<Object> findReachableParts(ExecutionModel execModel)
	{
		// Create HashSet to store all reachable nodes and transitions
		Set<Object> reachableParts = new HashSet<Object>();

		// Iterate over all states
		Iterator<ExecutionState> stateIter = new ExecutionStateIterator(execModel);
		while (stateIter.hasNext())
		{
			ExecutionState state = stateIter.next();

			// Add corresponding FlowNode to reachable FlowNodes
			FlowNode flowNode = state.getFlowNode();
			reachableParts.add(flowNode);

			// Traverse outgoing transitions
			for (ExecutionTransition transition : state.getOutTransitionsEx())
			{
				// Add corresponding FlowTransition to reachable FlowTransitions
				FlowTransition flowTransition = transition.getFlowTransition();
				reachableParts.add(flowTransition);
			}
		}

		return reachableParts;
	}

	private List<CoreConflict> checkForConflicts(FlowModel flowModel, Set<Object> reachableParts)
	{
		List<CoreConflict> conflicts = new ArrayList<CoreConflict>();

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
				for (FlowTransition transition : node.getTransitionsEx())
				{
					if (!reachableParts.contains(transition))
					{
						conflicts.addAll(identifyConflicts(transition));
					}
				}

			}
		}

		return conflicts;
	}

	private List<CoreConflict> identifyFilterConflicts()
	{
		List<CoreConflict> conflicts = new ArrayList<CoreConflict>();

		if (currentFilterNode != null && rejectingFilterElementCounter == filterElementCounter)
		{
			CoreConflict conflict = new CoreConflict(CoreConflict.FILTER_ALWAYS_REJECTS, currentFilterNode
					.getRepositoryLink());
			conflicts.add(conflict);

		}

		if (!unreachableActionNodes.isEmpty())
		{
			boolean onlyContinue = true;
			for (FlowNode actionNode : currentActionNodes)
			{
				if (!unreachableActionNodes.contains(actionNode)
						&& !actionNode.containsName(FlowNode.CONTINUE_ACTION_NODE))
				{
					onlyContinue = false;
					break;
				}
			}

			if (onlyContinue)
			{
				CoreConflict conflict = new CoreConflict(CoreConflict.FILTER_REDUNDANT, currentFilterNode
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

	private List<CoreConflict> identifyConflicts(FlowNode unreachableNode)
	{
		List<CoreConflict> conflicts = new ArrayList<CoreConflict>();

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

	private List<CoreConflict> identifyConflicts(FlowTransition unreachableTransition)
	{
		List<CoreConflict> conflicts = new ArrayList<CoreConflict>();

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
}
