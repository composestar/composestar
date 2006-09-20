package Composestar.Core.FIRE;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * 
 * $Id$
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
                ((SubstituteActionNode) rhs).substitute.equals(substitute));

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
