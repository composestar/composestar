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

import java.util.HashMap;

import Composestar.Utils.Debug;

public class Symbol
{
	public String name;

	public int type;

	public Column column = null;

	public Column originalColumn = null;

	// Signature knowledge.
	// Don't know what this means for non target symbols.
	private HashMap signatures = new HashMap();

	public Symbol(String _name, int _type)
	{
		name = _name;
		type = _type;
	}

	public int getType()
	{
		return type;
	}

	public String getName()
	{
		return name;
	}

	public String toString()
	{
		String typeStr;

		switch (type)
		{
			case 0:
				typeStr = "condition";
				break;
			case 1:
				typeStr = "target";
				break;
			case 2:
				typeStr = "selector";
				break;
			default:
				typeStr = "unknown";
				break;
		}

		if (Debug.getMode() > Debug.MODE_WARNING)
		{
			return "Symbol: " + name + ' ' + typeStr + (column != null ? column.toString() : "");
		}
		else
		{
			return name;
		}
	}

	public int getColumnLength()
	{
		return column.length;
	}

	public int getOriginalLength()
	{
		return originalColumn.length;
	}

	public boolean isSignature(Symbol selector)
	{
		return ((Boolean) signatures.get(selector)).booleanValue();
	}

	public boolean isAvailable(Symbol selector)
	{
		return signatures.containsKey(selector);
	}

	public void addSignature(Symbol selector)
	{
		signatures.put(selector, Boolean.TRUE);
	}

	public void addNotSignature(Symbol selector)
	{
		signatures.put(selector, Boolean.FALSE);
	}

}
