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

import java.io.Serializable;

public class Conflict implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7017897791526259274L;

	private String resource = "";

	private String sequence = "";

	private String msg = "";

	private String expr = "";

	public void setResource(String resource)
	{
		this.resource = resource;
	}

	public String getResource()
	{
		return resource;
	}

	public void setSequence(String insequence)
	{
		sequence = insequence;
	}

	public String getSequence()
	{
		return sequence;
	}

	public void setMsg(String inmsg)
	{
		msg = inmsg;
	}

	public String getMsg()
	{
		return msg;
	}

	public void setExpr(String inexpr)
	{
		expr = inexpr;
	}

	public String getExpr()
	{
		return expr;
	}
}
