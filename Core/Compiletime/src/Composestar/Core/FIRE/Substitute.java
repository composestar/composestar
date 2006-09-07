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

public class Substitute extends FilterLeaf
{
	Symbol symbol = null;

	public Substitute(Symbol _symbol) {symbol = _symbol;}
    public Substitute(Symbol _symbol, int filterNumber)
    {	symbol = _symbol;
        setFilterNumber(filterNumber);
    }

    public String toString ()
    {
        return "replace(" + symbol + ')';
    }

	// TODO: implement
	public StatusColumn calculateStatus(StatusColumn status, StateTable stateTable)
	{
		if( symbol.getName().equals("*"))
			return status;
		
		//TODO Debug only.
		stateTable.snapshot(status, this);
		//stateTable.snapshot(status, new SubstituteActionNode(symbol));
		
		// replacing the symbol, instead of changing the status.
		Logic.or(symbol.column, status);


		Symbol [] symbols = (SymbolTable.getInstance()).getSymbols(symbol.type);

		Logic.not(status);
		for (int i = 0; i < symbols.length; i++)
		{
			if (!symbols[i].equals(symbol))
			{
				// Remove not matching stuff.
				Logic.and(symbols[i].column, status);
			}

		}
		Logic.not(status);

		return status;
	}

	public ActionNode createNode()
	{
		return new SubstituteActionNode(symbol);
	}

}
