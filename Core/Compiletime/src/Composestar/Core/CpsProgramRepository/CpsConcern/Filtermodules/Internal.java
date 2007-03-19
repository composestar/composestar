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

import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference;
import Composestar.Core.RepositoryImplementation.TypedDeclaration;

/**
 * 
 */
public class Internal extends TypedDeclaration
{
	private static final long serialVersionUID = -5047706173440900206L;

	public Vector valueExpressions;

	public InternalAST internalAst;

	public Internal()
	{
		super();
		valueExpressions = new Vector();
	}

	public Internal(InternalAST parentInternal)
	{
		super();
		valueExpressions = new Vector();
		internalAst = parentInternal;
		descriptionFileName = internalAst.getDescriptionFileName();
		descriptionLineNumber = internalAst.getDescriptionLineNumber();
		name = internalAst.getName();
		type = parentInternal.getType();
	}

	/**
	 * valueExpressions
	 * 
	 * @param valexp
	 * @return boolean
	 */
	public boolean addValueExpression(ValueExpression valexp)
	{
		return internalAst.addValueExpression(valexp);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ValueExpressi
	 *         on
	 */
	public ValueExpression removeValueExpression(int index)
	{
		return internalAst.removeValueExpression(index);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ValueExpressi
	 *         on
	 */
	public ValueExpression getValueExpression(int index)
	{
		return internalAst.getValueExpression(index);
	}

	/**
	 * @return java.util.Iterator
	 */
	public Iterator getValueExpressionIterator()
	{
		return internalAst.getValueExpressionIterator();
	}

	public ConcernReference getType()
	{
		return type;
	}

	public void setType(ConcernReference aType)
	{
		type = aType;
	}

	/*
	 * public String getQualifiedName() { return
	 * internal_ast.getQualifiedName(); }
	 */

	public int getDescriptionLineNumber()
	{
		return internalAst.getDescriptionLineNumber();
	}

	public void setDescriptionLineNumber(int newLineNumber)
	{
		internalAst.setDescriptionLineNumber(newLineNumber);
	}

	public String getDescriptionFileName()
	{
		return internalAst.getDescriptionFileName();
	}

	public void setDescriptionFileName(String newFileName)
	{
		internalAst.setDescriptionFileName(newFileName);
	}

	public void addDynObject(String key, Object obj)
	{
		internalAst.addDynObject(key, obj);
	}

	public Object getDynObject(String key)
	{
		return internalAst.getDynObject(key);
	}

	public Iterator getDynIterator()
	{
		return internalAst.getDynIterator();
	}

	public String getName()
	{
		return name;
	}

	public String asSourceCode()
	{
		return internalAst.asSourceCode();
	}
}
