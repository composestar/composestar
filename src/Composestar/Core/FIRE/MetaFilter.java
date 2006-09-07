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

public class MetaFilter extends Filter
{
	public MetaFilter()
	{
		acceptContinues = true;
		rejectContinues = true;
		acceptAction = true;
		rejectAction = false;

		//doesMeta = true;
	}
	public String toString() { return "Meta";}

    private void metaAction(StatusColumn status, StateTable stateTable)
    {
        // All right, this is the situation:
        // We do not know what a meta filter does exactly. This can be anything, so all
        // options are open again.
        //
        // Therefore get the original permutations arrays from the symbol table.
        // Extend each symbol-column with these new permutations

        SymbolTable st = SymbolTable.getInstance();
        Symbol [] symbols = st.getAllSymbols();

        for (int i = 0; i < symbols.length; i++)
        {
            symbols[i].column.addElements(symbols[i].originalColumn);
        }

        // Extend the actionlist. Let the matching values point to the newtable.
        stateTable.addElements(symbols[0].getOriginalLength(), status);

        // Terminate this action.
//		stateTable.snapshot(status, new Terminated());
        status.finish(true);

        status.addElements(symbols[0].getOriginalLength(), true);
    }


	public StatusColumn calc (StatusColumn status, StateTable stateTable, Action component )
	{
		if (!Logic.allZero(status))
		{
			stateTable.snapshot(status, component);
			for( java.util.Iterator paramIt = this.parameters.iterator(); paramIt.hasNext(); )
			{
				Object next = paramIt.next();
				
				if( ((String)next).toLowerCase().equals("invasive") )
				{
					metaAction(status, stateTable);
					status.setAllValues(true); // TODO check this.
				}	
			}
			
			//stateTable.snapshot(status,component);
		}

		
		//Logic.not((StatusColumn) status);
		//(StateTable.getInstance()).snapshot(status,component);
		//Logic.not((StatusColumn) status);

		//status.finish(false);

		return status;
	}


	public ActionNode createNode()
	{
		return new FilterActionNode(toString());
	}


}
