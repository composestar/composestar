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

import Composestar.Core.RepositoryImplementation.TypedDeclaration;
import Composestar.Utils.CPSIterator;

/**
 * 
 * 
 * 
 */
public class InternalAST extends TypedDeclaration
{

	private static final long serialVersionUID = -7886002774701960062L;

	public Vector valueExpressions;

	public InternalAST()
	{
		super();
		valueExpressions = new Vector();
		type = null;
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
}
