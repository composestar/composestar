/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SimpleSelectorDef.SimpleSelExpression;
import Composestar.Core.RepositoryImplementation.DeclaredRepositoryEntity;
import Composestar.Utils.CPSIterator;

/**
 * Definition of the Selector that defines the crosscut
 */
public class SelectorDefinition extends DeclaredRepositoryEntity
{
	public static final String INTREP_KEY = "SelDefIntrep";

	private static final long serialVersionUID = -8636845317655529305L;

	public Vector selExpressionList;

	/**
	 * @modelguid {78DDA528-5560-41A8-BB73-FB6A5D5C4C27}
	 * @roseuid 401FAA6800B6
	 */
	public SelectorDefinition()
	{
		super();
		selExpressionList = new Vector();
	}

	/**
	 * selExpressionList
	 * 
	 * @param selExpression
	 * @return boolean
	 * @modelguid {573E5F06-0190-48FD-8C84-767F4BACC692}
	 * @roseuid 401FAA6800CA
	 */
	public boolean addSelExpression(SimpleSelExpression selExpression)
	{
		selExpressionList.addElement(selExpression);
		return true;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SimpleSelec
	 *         torDef.SimpleSelExpression
	 * @modelguid {E6470D13-A23E-45BA-9CD7-60546539E7D2}
	 * @roseuid 401FAA6800D4
	 */
	public SimpleSelExpression removeSelExpression(int index)
	{
		Object o = selExpressionList.elementAt(index);
		selExpressionList.removeElementAt(index);
		return (SimpleSelExpression) o;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SimpleSelec
	 *         torDef.SimpleSelExpression
	 * @modelguid {8C40809B-3EF0-4FC7-9925-73B341395845}
	 * @roseuid 401FAA6800E8
	 */
	public SimpleSelExpression getSelExpression(int index)
	{
		return (SimpleSelExpression) selExpressionList.elementAt(index);
	}

	/**
	 * @return java.util.Iterator
	 * @modelguid {B3803077-2581-4A13-9C86-8D7F44FD263E}
	 * @roseuid 401FAA6800FB
	 */
	public Iterator getSelExpressionIterator()
	{
		return new CPSIterator(selExpressionList);
	}

	/**
	 * @return Vector obsolete??
	 * @roseuid 404929FD0119
	 */
	public Vector getConcerns()
	{
		return null;
	}

	/**
	 * interprets the selector definition and attaches a Vector of
	 * ConcernReferences in the dynamicObjects with ID "interpretation"
	 * 
	 * @return java.util.Vector
	 * @roseuid 4053B2C000C7
	 */
	public Vector interpret()
	{
		Vector result = new Vector();
		Iterator selExpIter = this.getSelExpressionIterator();
		while (selExpIter.hasNext())
		{
			// GG
			for (Enumeration e = ((SimpleSelExpression) selExpIter.next()).interpret().elements(); e.hasMoreElements();)
			{
				result.addElement(e.nextElement());
			}
			// result.addAll(((SimpleSelExpression)
			// selExpIter.next()).interpret().elements()); GG
		}
		this.addDynObject(INTREP_KEY, result);
		return result;
	}
}
