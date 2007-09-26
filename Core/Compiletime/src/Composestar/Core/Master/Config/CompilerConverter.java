/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.Master.Config;

import java.io.Serializable;

@Deprecated
public class CompilerConverter implements Serializable
{
	private static final long serialVersionUID = -2071985122483867847L;

	private String name;

	private String expression;

	private String replaceBy;

	public CompilerConverter()
	{}

	public String getName()
	{
		return name;
	}

	public void setName(String inName)
	{
		name = inName;
	}

	public String getExpression()
	{
		return expression;
	}

	public void setExpression(String inExpression)
	{
		expression = inExpression;
	}

	public String getReplaceBy()
	{
		return replaceBy;
	}

	public void setReplaceBy(String inReplaceBy)
	{
		replaceBy = inReplaceBy;
	}
}
