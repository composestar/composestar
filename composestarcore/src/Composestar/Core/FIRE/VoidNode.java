package Composestar.Core.FIRE;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * 
 * $Id: VoidNode.java,v 1.1 2006/02/13 11:16:56 pascal Exp $
 * 
**/

public class VoidNode extends Node
{

	public String toString ()
	{
		return "voidNode";
	}

	protected boolean subsetOfSingle (Node rhs)
	{
		return (rhs instanceof VoidNode);
	}
}
