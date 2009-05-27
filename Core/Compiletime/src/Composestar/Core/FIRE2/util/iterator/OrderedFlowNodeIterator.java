package Composestar.Core.FIRE2.util.iterator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import Composestar.Core.FIRE2.model.FlowModel;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.model.FlowTransition;

public class OrderedFlowNodeIterator implements Iterator<FlowNode>
{
	private HashMap<FlowNode, MutableInteger> unvisitedTransitionCount;

	private LinkedList<FlowNode> nextNodes;

	public OrderedFlowNodeIterator(FlowModel model)
	{
		initialize(model);
	}

	private void initialize(FlowModel model)
	{
		unvisitedTransitionCount = new HashMap<FlowNode, MutableInteger>();
		for (FlowTransition transition : model.getTransitionsEx())
		{
			FlowNode endNode = transition.getEndNode();
			if (unvisitedTransitionCount.containsKey(endNode))
			{
				MutableInteger count = unvisitedTransitionCount.get(endNode);
				count.value++;
			}
			else
			{
				MutableInteger count = new MutableInteger();
				count.value = 1;
				unvisitedTransitionCount.put(endNode, count);
			}
		}

		nextNodes = new LinkedList<FlowNode>();
		nextNodes.add(model.getStartNode());
	}

	public boolean hasNext()
	{
		return !nextNodes.isEmpty();
	}

	public FlowNode next()
	{
		if (nextNodes.isEmpty())
		{
			throw new NoSuchElementException();
		}

		FlowNode node = nextNodes.removeFirst();

		// Decrement counter of next nodes
		for (FlowTransition transition : node.getTransitionsEx())
		{
			FlowNode endNode = transition.getEndNode();
			MutableInteger count = unvisitedTransitionCount.get(endNode);
			count.value--;
			if (count.value == 0)
			{
				nextNodes.add(endNode);
			}
		}

		return node;
	}

	public void remove()
	{
		throw new UnsupportedOperationException();
	}

	private static class MutableInteger
	{
		private int value;
	}
}
