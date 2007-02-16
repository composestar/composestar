package Composestar.Core.FIRE2.util.iterator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import Composestar.Core.FIRE2.model.FlowModel;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.model.FlowTransition;

public class OrderedFlowNodeIterator implements Iterator
{
	private HashMap unvisitedTransitionCount;

	private LinkedList nextNodes;

	public OrderedFlowNodeIterator(FlowModel model)
	{
		initialize(model);
	}

	private void initialize(FlowModel model)
	{
		unvisitedTransitionCount = new HashMap();
		Iterator transitionIter = model.getTransitions();
		while (transitionIter.hasNext())
		{
			FlowTransition transition = (FlowTransition) transitionIter.next();
			FlowNode endNode = transition.getEndNode();
			if (unvisitedTransitionCount.containsKey(endNode))
			{
				MutableInteger count = (MutableInteger) unvisitedTransitionCount.get(endNode);
				count.value++;
			}
			else
			{
				MutableInteger count = new MutableInteger();
				count.value = 1;
				unvisitedTransitionCount.put(endNode, count);
			}
		}

		nextNodes = new LinkedList();
		nextNodes.add(model.getStartNode());
	}

	public boolean hasNext()
	{
		return !nextNodes.isEmpty();
	}

	public Object next()
	{
		if (nextNodes.isEmpty())
		{
			throw new NoSuchElementException();
		}

		FlowNode node = (FlowNode) nextNodes.removeFirst();

		// Decrement counter of next nodes
		Iterator transitionIter = node.getTransitions();
		while (transitionIter.hasNext())
		{
			FlowTransition transition = (FlowTransition) transitionIter.next();
			FlowNode endNode = transition.getEndNode();
			MutableInteger count = (MutableInteger) unvisitedTransitionCount.get(endNode);
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

	private class MutableInteger
	{
		private int value = 0;
	}
}
