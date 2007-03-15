/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

import java.util.Iterator;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.CpsConcern.References.ExternalConcernReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.FilterModuleElementReference;
import Composestar.Core.RepositoryImplementation.TypedDeclaration;
import Composestar.Utils.CPSIterator;

/**
 * @modelguid {891E164C-FC74-4E41-B203-ECA4B865B45B}
 */
public class External extends TypedDeclaration
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4850955866709616974L;

	/**
	 * initialization expression (e.g. pack.concern.fm.internal / external)
	 */
	public Vector valueExpressions;

	public ExternalConcernReference shortinit;

	public FilterModuleElementReference longinit;

	/**
	 * @modelguid {88606DD2-74A3-4395-8CBC-3C1D298F4242}
	 * @roseuid 401FAA6203A6
	 */
	public External()
	{
		super();
		valueExpressions = new Vector();
	}

	/**
	 * valueExpressions
	 * 
	 * @param valexp
	 * @return boolean
	 * @modelguid {D6976E0D-4CF3-4D1D-A65D-D2967F5C7B5E}
	 * @roseuid 401FAA6203AF
	 */
	public boolean addValueExpression(ValueExpression valexp)
	{
		valueExpressions.addElement(valexp);
		return true;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ValueExpressi
	 *         on
	 * @modelguid {C47B2704-8D36-45F3-ABD6-490E0F343B4E}
	 * @roseuid 401FAA6203CD
	 */
	public ValueExpression removeValueExpression(int index)
	{
		Object o = valueExpressions.elementAt(index);
		valueExpressions.removeElementAt(index);
		return (ValueExpression) o;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ValueExpressi
	 *         on
	 * @modelguid {F40BC03B-67D4-4AA4-8AD8-1BBB10C98A1F}
	 * @roseuid 401FAA6203D8
	 */
	public ValueExpression getValueExpression(int index)
	{
		return (ValueExpression) valueExpressions.elementAt(index);
	}

	/**
	 * @return java.util.Iterator
	 * @modelguid {C860EB87-CF67-41C4-8EB6-459A190D371C}
	 * @roseuid 401FAA630004
	 */
	public Iterator getValueExpressionIterator()
	{
		return new CPSIterator(valueExpressions);
	}

	/**
	 * @param initValue
	 * @roseuid 40ADD58E02DA
	 */
	public void setInit(FilterModuleElementReference initValue)
	{
		this.longinit = initValue;
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.FilterModuleElem
	 *         entReference
	 * @roseuid 40ADD59100CC
	 */
	public FilterModuleElementReference getInit()
	{
		return longinit;
	}

	public FilterModuleElementReference getLonginit()
	{
		return longinit;
	}

	public void setLonginit(FilterModuleElementReference inlonginit)
	{
		longinit = inlonginit;
	}

	public ExternalConcernReference getShortinit()
	{
		return shortinit;
	}

	public void setShortinit(ExternalConcernReference cref)
	{
		this.shortinit = cref;
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer(super.toString());
		sb.append(" = ");
		if (shortinit != null)
		{
			sb.append(shortinit.getInitTarget());
			sb.append(".");
			sb.append(shortinit.getInitSelector());
			sb.append("()");
		}
		else if (longinit != null)
		{
			sb.append("#LONGREF#"); // TODO:
		}
		return sb.toString();
	}
}
