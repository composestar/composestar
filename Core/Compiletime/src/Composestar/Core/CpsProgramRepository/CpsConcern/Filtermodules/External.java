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
 * 
 */
public class External extends TypedDeclaration
{

	private static final long serialVersionUID = 4850955866709616974L;

	/**
	 * initialization expression (e.g. pack.concern.fm.internal / external)
	 */
	public Vector valueExpressions;

	public ExternalConcernReference shortinit;

	public FilterModuleElementReference longinit;

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
	 */
	public ValueExpression getValueExpression(int index)
	{
		return (ValueExpression) valueExpressions.elementAt(index);
	}

	/**
	 * @return java.util.Iterator
	 */
	public Iterator getValueExpressionIterator()
	{
		return new CPSIterator(valueExpressions);
	}

	/**
	 * @param initValue
	 */
	public void setInit(FilterModuleElementReference initValue)
	{
		this.longinit = initValue;
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.FilterModuleElem
	 *         entReference
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

	public String asSourceCode()
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
