package Composestar.Core.FIRE;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * 
 * $Id: Match.java,v 1.1 2006/02/16 23:03:56 pascal_durr Exp $
 * 
**/
import Composestar.Utils.Debug;

public class Match extends FilterLeaf
{
	Symbol symbol = null;

	public Match(Symbol _symbol) {symbol = _symbol;}
	public Match(Symbol _symbol, int filterNumber) 
	{	symbol = _symbol;
	 	setFilterNumber(filterNumber);
	}
	
	public String toString () 
	{
		return "match(" + symbol + ")";
	} 

	// TODO: implement
	public StatusColumn calculateStatus(StatusColumn status, StateTable stateTable)
	{
		//Debug.out(Debug.MODE_INFORMATION, "FIRE" ,"Match start: " + symbol.name);
		if (symbol.name.equals("*")) 
		{
			return status;
		}
		

		Logic.and((StatusColumn) status, symbol.column); 

		//Debug.out(Debug.MODE_INFORMATION, "FIRE" ,"Match stop: " + symbol.name);
		return status;
	}

	public ActionNode createNode()
	{	
		return new FilterActionNode("Match");
	}

}
