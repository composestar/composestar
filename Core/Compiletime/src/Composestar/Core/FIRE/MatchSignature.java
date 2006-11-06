package Composestar.Core.FIRE;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * MatchSignature.java 570 2006-09-07 16:16:48Z reddog33hummer $
 */

public class MatchSignature extends FilterLeaf
{
	Symbol symbol = null;

	public MatchSignature(Symbol _symbol)
	{
		symbol = _symbol;
	}

	public MatchSignature(Symbol _symbol, int filterNumber)
	{
		symbol = _symbol;
		setFilterNumber(filterNumber);
	}

	public String toString()
	{
		return "matchsignature(" + symbol + ')';
	}

	// If known:
	//
	// A -> p, r
	// B -> q
	//
	// Symbol is A.
	//
	// A B | p q r in out
	// 1 0 | 1 0 0 [1] [1]
	// 1 0 | 0 1 0 [1] [0]
	// 1 0 | 0 0 1 [0] [0]
	// 0 1 | 1 0 0 [1] [1]
	// 0 1 | 0 1 0 [1] [0]
	// 0 1 | 0 0 1 [1] [1]
	//
	//
	// All unknown:
	//
	// A B | p q r in out
	// 1 0 | 1 0 0 [1] [1]
	// 1 0 | 0 1 0 [1] [1]
	// 1 0 | 0 0 1 [0] [0]
	// 0 1 | 1 0 0 [1] [1]
	// 0 1 | 0 1 0 [1] [1]
	// 0 1 | 0 0 1 [1] [1]
	//
	// 1 0 | 1 0 0 [1] [0]
	// 1 0 | 0 1 0 [1] [0]
	// 0 1 | 1 0 0 [1] [0]
	// 0 1 | 0 1 0 [1] [0]
	// 0 1 | 0 0 1 [1] [0]
	private void updateSymbols(StatusColumn status, int row)
	{
		Symbol[] symbols = (SymbolTable.getInstance()).getAllSymbols();
		for (int i = 0; i < symbols.length; i++)
		{
			// Update each symbol stuff.
			symbols[i].column.addElements(1, symbols[i].column.getValue(row));
		}
	}

	// "All unknown" implementation.
	public StatusColumn calculateStatus(StatusColumn status, StateTable stateTable)
	{
		// The bits that are true, counter the problem of a signature match.
		stateTable.snapshot(status, this);

		Symbol[] symbols = (SymbolTable.getInstance()).getAllSymbols();

		// We are going to modify the status table, therefore the length will
		// change.
		// The extra rows, should not be evaluated.
		int statusLength = status.length;
		for (int row = 0; row < statusLength; row++)
		{
			// Handle each occurrence of the signatureMatch independent. That
			// means
			// each occurrence of a signatureMatch points to a separate table
			// entry.
			if (status.isActive(row))
			{
				// we should extent the stateTable, statusColumn, and each
				// symbol-column.
				// Since we do not know if the signature matches, we set both
				// options in the
				// new table.
				stateTable.addElements(2, row);
				boolean[] a = { true, false };
				status.addElements(a);

				for (int k = 0; k < symbols.length; k++)
				{
					// Copy two times the symbol values.
					symbols[k].column.addElements(1, symbols[k].column.getValue(row));
					symbols[k].column.addElements(1, symbols[k].column.getValue(row));
				}

				// Terminate our own flow.
				status.finish(row);
			}
		}

		return status;
	}

	public ActionNode createNode()
	{
		return new SignatureActionNode(symbol);
	}

}
