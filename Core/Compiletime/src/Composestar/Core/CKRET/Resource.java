/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2004-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CKRET;

import java.util.ArrayList;

/**
 * 
 */
public class Resource
{

	private String name;

	private ArrayList alphabet = new ArrayList();

	// private StringBuffer history;
	// private StringBuffer fancyhistory;

	public Resource(String inname)
	{
		name = inname;
		// this.history = new StringBuffer();
		// this.fancyhistory = new StringBuffer();
	}

	// public void add(String operation)
	// {
	// //this.history.append(operation.charAt(0));
	// this.history.append(operation);
	// this.fancyhistory.append(' ');
	// this.fancyhistory.append(operation);
	// }

	// public String toString()
	// {
	// return this.name + " > " + history;
	// }

	public String getName()
	{
		return name;
	}

	// public String sequence()
	// {
	// return history.toString();
	// }
	//	
	// public String fancySequence()
	// {
	// return fancyhistory.toString();
	// }
	//	
	public boolean operationIsInAlphabet(String operation)
	{
		for (Object anAlphabet : alphabet)
		{
			String str = (String) anAlphabet;
			if (str.equals(operation))
			{
				return true;
			}
		}
		return false;
	}

	public void addToAlphabet(String operation)
	{
		alphabet.add(operation);
	}
}
