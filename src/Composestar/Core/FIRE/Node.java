package Composestar.Core.FIRE;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * 
 * $Id$
 * 
**/

import java.util.LinkedList;
import java.util.Collections;

import java.util.HashSet;

public abstract class Node implements Comparable, Cloneable
{
	// Datamembers
	private LinkedList children = new LinkedList();
	protected Node parent = null;
	boolean perfectMatch = false;
	boolean conditionCheck = true;
	FilterReasoningEngine fireInfo = null;
	protected int filterNumber = -2;
	
	public Object clone() throws CloneNotSupportedException {
		try 
		{
			Node n = (Node) super.clone();
			n.children = (LinkedList) children.clone();
			return n;
		} 
		catch (CloneNotSupportedException e)
		{
			return null;
		}

	}

	public void addChild (Node childTree)
	{
		/* add the children sorted*/
		/*
		int i = 0;
		while (i < children.size() && compareTo(childTree) <= 0) i++;

		children.add(i, childTree);
		*/
		children.add(childTree);
		childTree.parent = this;
	}

	// Add a child at the end of the path.
	public void addChildAtPath (int index, Node newTree)
	{
		if (!hasChildren()) addChild (newTree);
		else getChild(index).addChildAtPath (0, newTree);
	}

	public void replaceChild (int index, Node newTree)
	{
		children.set(index, newTree);
		newTree.parent = this;
	}


	// Except the parent.
	public void removeVoidNodes ()
	{
		for (int i = 0; i < children.size(); i++)
		{
			Node subTree = getChild(i);
			if (subTree instanceof VoidNode)
			{
				if (subTree.hasChildren())
				{
					replaceChild(0, subTree.getChild(0));

					for (int k = 1; k < subTree.numberOfChildren(); k++)
					{
						addChild(subTree.getChild(k));
					}
				}
			}

			subTree.removeVoidNodes();
		}
	}
	
	public Node getChild (int index)
	{
		return (Node) children.get(index);
	}

	public void removeChild (int index)
	{
		children.remove(index);
	}

	public int numberOfChildren()
	{
		return children.size(); 
	}

	public boolean hasChildren ()
	{
		return (!children.isEmpty());
	}
	
	// Root node returns null
	public Node getParent()
	{
		return parent;
	}

	public boolean isRootNode()
	{
		return (parent == null);
	}
	
	public void setFIREInfo(FilterReasoningEngine _fireInfo)
	{
		fireInfo = _fireInfo;
	}
	
	public FilterReasoningEngine getFIREInfo()
	{
		return fireInfo;
	}
	
	public int countAllNodes()
	{
		int total = 0;

		if (hasChildren()) 
		{
			total = children.size(); 
			for (int i = 0; i < children.size(); i++)
			{
				Node subTree = (Node) children.get(i);
				total += subTree.countAllNodes();
			}
		}

		return total;
	}

	public Iterator getIterator(Class iteratorKind)
	{
		try
		{
			Class [] parameterTypes = new Class[1];
			parameterTypes[0] = Node.class;

			Object [] parameterValues = new Object[1];
			parameterValues[0] = this;

			return (Iterator) iteratorKind.getConstructor(parameterTypes).newInstance(parameterValues);
		} catch (Exception e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		return null;
	}

	public int countAllNodes(Iterator itr)
	{
		return getSubTree(itr).countAllNodes();
	}

	// Iterator moves to the next element.
	public void remove(Iterator itr)
	{
		itr.parent.removeChild(itr.currentChild);
	}

	public void append(Iterator itr, Node subTree)
	{
		itr.parent.getChild(itr.currentChild).addChild(subTree);
	}

	public int numberOfChildren(Iterator itr)
	{
		return itr.parent.getChild(itr.currentChild).numberOfChildren();
	}

	public boolean hasChildren(Iterator itr)
	{
		return itr.parent.getChild(itr.currentChild).hasChildren();
	}

	// Deprecated
	public Node getSubTree (Iterator itr)
	{
		return itr.parent.getChild(itr.currentChild);
	}

	public boolean cut(Node compareWith)
	{
		boolean deleteNode = true;
		for (int i = 0; i < children.size(); i++)
		{
			Node subNode = (Node) children.get(i);

			if (subNode.cut(compareWith)) 
			{
				removeChild(i);
				// RemoveChild modifies the tree. The next branch becomes the current branch.
				// Therefore redo the current i. 
				i--; 
			}
			else deleteNode = false;
		}

        return !subsetOfExpression(compareWith) && deleteNode;

		}

	// Goes wrong by Signature match. But must be fixed in the future.
	public void minimizeLossy()
	{
		if (!hasChildren()) return;

		for (int i = 0; i < children.size(); i++)
		{
			for (int k = 0; k < children.size(); k++)
			{
				if (i != k)
				{
					if (getChild(i).subsetOfSingle(getChild(k)))
					{
						// Move all the children to k;
						getChild(k).moveAllChildren(getChild(i));
						removeChild(i);
					}
				}
			}
		}

		for (int i = 0; i < children.size(); i++)
		{ 
			getChild(i).minimizeLossy();
		}
	}


	public void minimize()
	{
		for (int i = 0; i < children.size(); i++)
			for (int k = 0; k < children.size(); k++)
				if (i != k && getChild(i).subsetOf(getChild(k)))
				{
					removeChild(i);
					if (k > 0) k--;
					if (i > 0) i--;
				}

		for (int i = 0; i < children.size(); i++)
			getChild(i).minimize();

	}

	private void moveAllChildren (Node parentNode)
	{
		while (parentNode.numberOfChildren() > 0)
		{
			addChild(parentNode.getChild(0));
			//children.add(parentNode.getChild(0));
			parentNode.removeChild(0);
		}
	}


	protected abstract boolean subsetOfSingle (Node singleNode);
	
	// Compare two trees. 
	// The children are not ordered yet.
	public boolean subsetOf (Node node)
	{
		if (!subsetOfSingle(node)) return false;
		if (numberOfChildren() != node.numberOfChildren()) return false;

		for (int i = 0; i < children.size(); i++)
		{
			if (!getChild(i).subsetOf(node.getChild(i))) return false;
		}

		return true;
	}

	// compare two trees. The right tree is an expression.
	// There is nothing more pleasant than a good recursive function.
	public final boolean subsetOfExpression (Node rhs)
	{
		// For the leafs:
		if (!rhs.hasChildren()) return subsetOfSingle(rhs);

		// The composites:
		boolean result = false;
		for (int i = 0; i < rhs.numberOfChildren(); i++)
		{
			// Parallel
			result |= subsetOfExpression (rhs.getChild(i));
		}

		// Serial
		return (result && subsetOfSingle(rhs));
	}

	// interface from comparable
	public int compareTo (Object rhs)
	{
		return toString().compareTo(rhs.toString());
	}

	public boolean equals (Object node)
	{
        return node instanceof Node && (subsetOf((Node) node) && ((Node) node).subsetOf(this));
        }

	public boolean equalsSingle (Node node)
	{
		return (subsetOfSingle(node) && node.subsetOfSingle(this));
	}

	public String toString()
	{
		return "Node";
	}

	public String toTreeString()
	{
		String total = "";
		DepthFirstIterator itr = (DepthFirstIterator) getIterator(DepthFirstIterator.class);

		for (;!itr.isDone(); itr.next())
		{
			total += itr.getDepth() + " " + itr.getNode().toString() + ' ' + itr.getNode().getFilterNumber() + '\n';
		}

		return total;
	}		

	// Signature match overrides this method.
	public void sort ()
	{
		Collections.sort(children);

		for (int i = 0; i < numberOfChildren(); i++)
		{
			getChild(i).sort();
		}
	}
	
		
	public int exists(HashSet dependencies, ActionNode startWith, Node match, String concernName)
	{	
		int endStatus = FilterReasoningEngine.FALSE;
		for (int i = 0; i < children.size(); i++)
		{
			Node currentChild = getChild(i);
			
			if (startWith.subsetOfSingle(currentChild))
			{		
				int status = currentChild.search(dependencies, match, startWith.getPreferedSelector(), concernName);

				// Status TRUE is good, and return directly.
				// Status UNKNOWN is better than false. Keep some faith.
				if (status == FilterReasoningEngine.TRUE)	return status;
				else if (status == FilterReasoningEngine.UNKNOWN) 
					endStatus = FilterReasoningEngine.UNKNOWN;					
			}
		}
		
		return endStatus;
	}
	
	// SignatureAction reimplements this stuff.
	public int search (HashSet dependencies, Node match, String selector, String concernName)
	{
		// If we match, tell directly the good news. 
		if (subsetOfExpression(match)) return FilterReasoningEngine.TRUE;
		
		// Think about the worst
		int status = FilterReasoningEngine.FALSE;
		for (int i = 0; i < numberOfChildren(); i++)
		{		
			// Maybe one of my children can say something
			int localStatus = getChild(i).search(dependencies, match, selector, concernName);

			// Yeah baby, don't search anymore, we got it. 
			if (localStatus == FilterReasoningEngine.TRUE) return localStatus;
			
			// status can be UNKNOWN or FALSE. 
			// Unknown is slightly better than FALSE. We are positive thinkers, keep up the faith.   
			if (localStatus > status) status = localStatus;					
		}
		
		// This time, the bad news travels not as fast as the good news.    
		return status;
	}
	
	public void setPerfectMatch (boolean set)
	{
		perfectMatch = set;
	}
	
	public boolean isPerfectMatch () 
	{
		return perfectMatch;
	}
	
	// BADBAD I won't win a nobel price with this code.
	public void setConditionCheck (boolean set)
	{
		conditionCheck = set;
	}
	
	public boolean checkConditions () 
	{
		return conditionCheck;
	}
	
	/*
	public boolean exists(Node startWith, Node match, boolean isFirst)
	{
	
		return true;
	}
	*/
	
	/*
	public boolean updateSignatures()
	{
		boolean result = false;
		for (int i = 0; i < numberOfChildren(); i++)
		{
			result |= getChild(i).updateSignatures();
		}
		return result;
	}
	*/
			
	public int parentUsesId()
	{
		int nrOfChildren = parent.numberOfChildren();
		for (int i = 0; i < nrOfChildren; i++)
		{
			if (parent.getChild(i).equals(this)) return i;
		}
		
		return -1;
	}
	
	public void setFilterNumber(int number)
	{
		filterNumber = number;
	}
	
	public int getFilterNumber()
	{
		return filterNumber;
	}


	
}


