package Composestar.Core.FIRE.Reporter;

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

public class Reporter
{
	protected LinkedList tables = null;

	protected LinkedList tableNames = new LinkedList();

	// Work in progress
	protected LinkedList currentTable = null;

	protected LinkedList currentRow = null;

	protected LinkedList currentState = null;

	protected String buffer = "";

	protected LinkedList stateStringList = new LinkedList(); // Remember the

	// previous two
	// strings.

	protected String currentStateString = "";

	public Reporter()
	{
		empty();
	}

	public void empty()
	{
		tables = new LinkedList();
		tableNames = new LinkedList();
	}

	public void addTable(String name)
	{
		testEqual();
		// stateStringList.clear();

		tableNames.add(name);
		currentTable = new LinkedList();
		tables.add(currentTable);
	}

	public void addRow()
	{
		testEqual();
		// stateStringList.clear();

		currentRow = new LinkedList();
		currentTable.add(currentRow);
	}

	protected void testEqual()
	{
		// Do state checking.
		if (stateStringList.size() == 3)
		{
			String prevString = (String) stateStringList.getFirst();
			// System.out.println ("prev: " + prevString);
			// System.out.println ("cur: " + currentStateString);
			if (currentStateString.length() > 0 && prevString.equals(currentStateString))
			{
				int stateIndex = currentRow.size();
				currentRow.remove(stateIndex - 4);
				currentRow.remove(stateIndex - 4);
			}

			stateStringList.removeFirst();
		}

		// Add the current state.
		stateStringList.add(currentStateString);
		currentStateString = "";
	}

	public void addState()
	{
		// testEqual();

		// Do your thing.
		currentState = new LinkedList();
		currentRow.add(currentState);

	}

	public void addElement(String element)
	{
		currentState.add(element);

		// currentStateString += element;
	}

	public void addStart()
	{
		addState();
		currentState.add("[ start ]"); // Don't try to match.
	}

	public void addFilter(String filter)
	{
		addState();
		currentState.add("[ " + filter + " ]");
	}

	public void addCondition(String condition)
	{
		addElement(condition);
	}

	public void addMessage(String target, String selector)
	{
		addElement(target + '.' + selector);
	}

	public void addSeparator()
	{
		addState();
		currentState.add(" --> ");
	}

	public void addLink(int number)
	{
		addState();
		addElement("[ Table:  " + number + "] ");
	}

	/*
	 * protected int getMaxRowLength() { int max = 0; for (int r = 0; r <
	 * rows.size(); r++) { LinkedList cr = (LinkedList) rows.get(r); if
	 * (cr.size() > max) max = cr.size(); } return max; }
	 */

	/*
	 * // Not used. protected int [] calculateColumnLengths() { int [] colSize =
	 * new int [getMaxRowLength()]; for (int i = 0; i < colSize.length; i++)
	 * colSize[i] = 0; for (int r = 0; r < rows.size(); r++) { LinkedList cr =
	 * (LinkedList) rows.get(r); for (int i = 0; i < cr.size(); i++) { if
	 * (((String) cr.get(i)).length() > colSize[i]) { colSize[i] = ((String)
	 * cr.get(i)).length(); } } } return colSize; } protected void
	 * printItem(String item, int size) { System.out.print (item); for (int i =
	 * 0; i < size - item.length(); i++) System.out.print (" "); }
	 */

	protected void create()
	{
		buffer = "";

		if (tables == null)
		{
			return;
		}

		for (int t = 0; t < tables.size(); t++)
		{
			buffer += "[ " + tableNames.get(t) + "]\n";
			LinkedList rows = (LinkedList) tables.get(t);

			for (int r = 0; r < rows.size(); r++)
			{
				LinkedList states = (LinkedList) rows.get(r);
				for (int s = 0; s < states.size(); s++)
				{
					LinkedList item = (LinkedList) states.get(s);
					for (int i = 0; i < item.size(); i++)
					{
						buffer += item.get(i);
					}
				}
				buffer += ("\n");
			}
			buffer += ("\n\n");
		}
	}

	public void print()
	{
		// testEqual();
		create();
		System.out.print(buffer);
	}

	/*
	 * public void sortRows(int columnNr) { for (int r = 0; r < rows.size();
	 * r++) { LinkedList cr = (LinkedList) rows.get(r); cr.get(columnNr); } }
	 */

	public String getBuffer()
	{
		create();
		return buffer;
	}
}
