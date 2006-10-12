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

public class StateTable
{
	private int length = 0;

	private LinkedList statesList = new LinkedList();  // Tables.

	private Symbol [] allSymbols = null;

    private FilterReasoningEngine fireInfo = null;

	public StateTable(Symbol [] _allSymbols, Symbol [] _conditionSymbols, FilterReasoningEngine _fireInfo)
	{
		empty();
		fireInfo = _fireInfo;

		allSymbols = _allSymbols;
        Symbol[] conditionSymbols = _conditionSymbols;
        int columnLength = allSymbols[0].getColumnLength();
        addElements(columnLength);
	}

	public void empty()
	{
		length = 0;
		statesList = new LinkedList(); 
	}

	public int size() {return length;}

	
	public void addElements(int size)
	{
		length += size;

		Node root = new VoidNode();
		root.setFIREInfo(fireInfo);
		for (int i = 0; i < size; i++) 
		{
			VoidNode vn = new VoidNode();
			vn.setFIREInfo(fireInfo);
			root.addChild(vn);	
		}
		
		statesList.add(root);
	}

	/** Add also pointers to the new table.
     * @param size
     * @param status*/
	public void addElements(int size, StatusColumn status)
	{
		addElements(size);
		Node lastTableReference = (Node) statesList.getLast();

		for (int i = 0; i < status.length; i++)
		{
			if (!status.isFinished(i) && status.getValue(i))
			{
				int table = getTable(i);
				int index = getTableIndex (i);

				Node tree = (Node) statesList.get(table);
				tree.addChildAtPath(index, lastTableReference);
			}
		}
	}

	public void addElements(int size, int row)
	{
		addElements(size);
		Node lastTableReference = (Node) statesList.getLast();
		
		int table = getTable(row);
		int index = getTableIndex (row);

		Node tree = (Node) statesList.get(table);
		tree.addChildAtPath(index, lastTableReference);
	}
	
	// TODO Throw exception when status.length != symbol.column.length
	public void snapshot(StatusColumn status, FilterLeaf fc)
	{
		if (status.length > length) 
		{
			System.out.println ("SHOULD BE EXCEPTION: Incorrect size");
			System.out.println ("statetable length" + length);
			System.out.println ("status length" + status.length);
		}
		
		int k = 0;
		for (int t = 0; t < statesList.size(); t++) // tables
		{
			Node tree = (Node)statesList.get(t);
			for (int i = 0; i < tree.numberOfChildren(); i++)
			{
				if (status.isActive(k))
				{
					ActionNode node = fc.createNode();
					node.setFIREInfo(fireInfo);
					node.setFilterNumber(fc.getFilterNumber());
					
					// TODO: Inefficient
					for (int s = 0; s < allSymbols.length; s++)
					{
						if (allSymbols[s].column.getValue(k))
							node.addSymbol(allSymbols[s]); 
					}

					if (tree.getChild(i) instanceof VoidNode) tree.replaceChild(i, node);
					else tree.addChildAtPath(i, node);
				}
				k++;
			}
		}
	}

	public LinkedList getList () { return statesList; }


	public Node getNode ()
	{
		Node tree = ((Node) statesList.get(0));
		tree.removeVoidNodes();

		return tree;

	}
	public String toString()
	{
		return getNode().toTreeString();
	}

///////////////////////// PRIVATE STUFF ///////////////////////////////

	private int getTable(int row)
	{
		int totalLength = 0;
		for (int i = 0; i < statesList.size(); i++)
		{
			totalLength += ((Node) statesList.get(i)).numberOfChildren();
			if (totalLength > row) return i;
		}

		return -1;
	}

	private int getTableIndex (int row)
	{
		int totalLength = 0;
		for (int i = 0; i < getTable(row); i++)
		{
			totalLength += ((Node) statesList.get(i)).numberOfChildren();
		}
		return row - totalLength;
	}


}
