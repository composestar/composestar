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
 * extra klasse toegevoegd, niet in rose
 * 
 * @modelguid {1260B89D-250A-4C2C-8602-77D8B44B9E11}
 */
public class Internal extends TypedDeclaration
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5047706173440900206L;

	public Vector valueExpressions;

	public InternalAST internal_ast;

	/**
	 * @modelguid {5C63E872-AABD-4585-89FE-4B87E692DB03}
	 * @roseuid 401FAA6502C3
	 */
	public Internal()
	{
		super();
		valueExpressions = new Vector();
	}

	public Internal(InternalAST parentInternal)
	{
		super();
		valueExpressions = new Vector();
		internal_ast = parentInternal;
		name = internal_ast.getName();
		type = parentInternal.getType();
	}

	/**
	 * valueExpressions
	 * 
	 * @param valexp
	 * @return boolean
	 * @modelguid {AF18E2B5-C471-47DC-83E4-7F44C95BDFDE}
	 * @roseuid 401FAA6502C4
	 */
	public boolean addValueExpression(ValueExpression valexp)
	{
		return internal_ast.addValueExpression(valexp);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ValueExpressi
	 *         on
	 * @modelguid {DC7912C1-6175-4805-BCE8-19E0E251E6AD}
	 * @roseuid 401FAA6502CE
	 */
	public ValueExpression removeValueExpression(int index)
	{
		return internal_ast.removeValueExpression(index);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ValueExpressi
	 *         on
	 * @modelguid {93AE7522-8EC8-4920-B2DC-E8B70129EFB5}
	 * @roseuid 401FAA6502E2
	 */
	public ValueExpression getValueExpression(int index)
	{
		return internal_ast.getValueExpression(index);
	}

	/**
	 * @return java.util.Iterator
	 * @modelguid {5DEB1C94-050E-4527-A4CA-38E39BA43D5B}
	 * @roseuid 401FAA6502F5
	 */
	public Iterator getValueExpressionIterator()
	{
		return internal_ast.getValueExpressionIterator();
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

	public Object clone() throws CloneNotSupportedException
	{
		return internal_ast.clone();
	}

	public int getDescriptionLineNumber()
	{
		return internal_ast.getDescriptionLineNumber();
	}

	public void setDescriptionLineNumber(int newLineNumber)
	{
		internal_ast.setDescriptionLineNumber(newLineNumber);
	}

	public String getDescriptionFileName()
	{
		return internal_ast.getDescriptionFileName();
	}

	public void setDescriptionFileName(String newFileName)
	{
		internal_ast.setDescriptionFileName(newFileName);
	}

	public void addDynObject(String key, Object obj)
	{
		internal_ast.addDynObject(key, obj);
	}

	public Object getDynObject(String key)
	{
		return internal_ast.getDynObject(key);
	}

	public Iterator getDynIterator()
	{
		return internal_ast.getDynIterator();
	}

	public String getName()
	{
		return name;
	}

}
