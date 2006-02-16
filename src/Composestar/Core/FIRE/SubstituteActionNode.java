package Composestar.Core.FIRE;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * 
 * $Id: SubstituteActionNode.java,v 1.1 2006/02/13 11:16:56 pascal Exp $
 * 
**/


public class SubstituteActionNode extends ActionNode
{
	protected Symbol substitute = null;

	public SubstituteActionNode (Symbol subst)
	{
		substitute = subst;
	}

	public String toString ()
	{
		return "Substitute:" + ((substitute.getType()==1)?" target=":" selector=") + substitute;

	}

	protected boolean subsetOfSingle (Node rhs)
	{
		return (super.subsetOfSingle(rhs) && 
				rhs instanceof SubstituteActionNode && 
				((SubstituteActionNode)rhs).substitute == substitute);

		/*
		if (super.compareSingle(rhs)) return true;

		if (rhs instanceof SubstituteActionNode)
		{
			return (((SubstituteActionNode)rhs).substitute == substitute);
		}

		return false;

		*/
	}



}
