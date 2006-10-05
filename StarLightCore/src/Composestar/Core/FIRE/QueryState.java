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

import java.util.*;

public class QueryState
{
        // protected data members
        protected Symbol target = null;
        protected Symbol selector = null;
	protected Symbol replaceTarget = null;
	protected Symbol replaceSelector = null;

        protected LinkedList conditions = new LinkedList();

        protected String filter = null;
       	protected Symbol inSignature = null;


        // public methods
        public void setTarget (Symbol targetSymbol) {target = targetSymbol;}
        public void setTarget (String targetString) {setTarget (stringToSymbol(targetString, 1));}
        public Symbol getTarget () {return target;}

        public void setSelector (Symbol selectorSymbol) {selector = selectorSymbol;}
        public void setSelector (String selectorString) {setSelector (stringToSymbol(selectorString, 2));}
        public Symbol getSelector () {return selector;}

        public void addCondition (Symbol s)
        {
                if (!conditions.contains(s)) conditions.add(s);
        }

        public void addCondition (String str)
        {
		addCondition(stringToSymbol(str,0));
        }

	public void setConditions (Symbol [] conditionArray)
	{
		for (int i = 0; i < conditionArray.length; i++) 
		{
			addCondition(conditionArray[i]);
		}
	}

        public boolean containsCondition (Symbol s)
        {
                return conditions.contains(s);
        }

	public Symbol[] getConditions ()
        {
                return (Symbol []) conditions.toArray();
        }

        public void setFilter (String filterName)
        {
                filter = filterName;
        }

        public String getFilter ()
        {
                return filter;
        }

        public void setInSignature (Symbol signature)
        {
                inSignature = signature;
        }

        public Symbol getInSignature () 
	{
		return inSignature;
	}

        public void setReplace (Symbol _replaceTarget, Symbol _replaceSelector)
        {
                replaceTarget = _replaceTarget;
                replaceSelector = _replaceSelector;
        }


        // protected methods
        protected Symbol stringToSymbol (String symbolString, int symbolType)
        {
                SymbolTable st = SymbolTable.getInstance();
                Symbol symbol = st.getSymbol(symbolString, symbolType);

		if (symbol == null) 
		{
			Exception e = new Exception ("stringToSymbol cannot find the given String in the symbolTable");
			e.printStackTrace();
		}

		return symbol;
        }

        public String toString()
        {
                String str = "(" +conditions+ ')' + target + ' ' + selector + " [" + filter + ']';
                return str;
        }
}

