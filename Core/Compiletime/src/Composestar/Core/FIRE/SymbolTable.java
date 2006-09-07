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

import java.util.HashMap;

/**
 * Store symbols with a type to match in a hashmap.
 */
public class SymbolTable
{
	/** Currently there are 3 types available. 
	 *  0: Condition
	 *  1: Target
	 *  2: Selector
	 */
	public static final int CONDITION = 0;
	public static final int TARGET = 1;
	public static final int SELECTOR = 2;
	
	
	private final int totalTypes = 3;

	/**
	 * Pointer to it self. Because this is a singleton class.
	 */
	private static SymbolTable object = null;

	/**
	 * Store all types in a hashmap. 
	 * Symbol name is the key and the type the value.
	 */
	private HashMap symbolMap = null;

	/**
	 * Count the symbols of a type.
	 */
	private int symbolCounter[] = new int[totalTypes];

	/**
	 */
	private Symbol [] allSymbols = null;
	private Symbol [][] typeSymbols = null;
	private int columnLength = 0;

	/**
	 * Private constructor (singleton).
	 */
	private SymbolTable()
	{
		empty();
	}

	/**
	 * Return the instance of the SymbolTable object.
	 * Those who want to access the SymbolTable object, should call this method.
	 */
	public static SymbolTable getInstance()
	{
		if (object == null) object = new SymbolTable();
		return object;
	}

	/**
	 * Empty the symboltable.
	 */
	public void empty()
	{
		symbolMap = new HashMap();

		// reset the symbolCounter;
		for (int i = 0; i < symbolCounter.length; i++) 
			symbolCounter[i] = 0;

		allSymbols = null;
		typeSymbols = null;
	}
	
	public Symbol addSymbol (String name, int type)
	{
		// No way. We are already done().
		if (allSymbols != null) return null;

		String key = getHashKey(name, type);
		if (symbolMap.containsKey(key)) 
		{
			return (Symbol) symbolMap.get(key);
		}
		else
		{
			symbolCounter[type]++;

			Symbol s = new Symbol(name, type); 
			symbolMap.put(key, s);
			return s;
		}
	}

	public HashMap getHashMap ()
	{
		return (HashMap) symbolMap.clone();
	}

	public Symbol getSymbol (String name, int type)
	{
		return (Symbol) symbolMap.get(getHashKey(name, type));
	}

	public Symbol[] getSymbols (int type)
	{
		return typeSymbols[type];
	}

	public Symbol[] getAllSymbols()
	{
		return allSymbols;
	}

	public void done()
	{
		// Calculate the typeSymbols arrays.
		int totalLength = 0; 
		typeSymbols = new Symbol[3][];
		for (int i = 0; i < totalTypes; i++)
		{
			typeSymbols[i] = getSymbolArray(i);

			if (typeSymbols[i] != null)
				totalLength += typeSymbols[i].length;
		}

		allSymbols = getAllSymbolsArray();
		calculateColumnBits();
	}

	/**
	 * @Return the number of symbols that are available of a given type.
	 */
	public int totalSymbols (int type) { return symbolCounter[type]; }

	/**
	 * @Return the hard coded number of types that are available
	 */
	public int totalTypes () {return totalTypes;}

    /**
     * @Return a string representation of the SymbolTypes.
     */
    public String toString()
    {
        if (allSymbols == null) return "Symbol table not done";

        String out = "Symbol table\n-----------\n";

        for (int i = 0; i < allSymbols.length; i++)
        {
            out += i + " --> " + (allSymbols[i]).toString() + '\n';
        }

        return out;
    }

	public int getColumnLength()
	{
		return columnLength;
	}
	
	/**
	 * Get all symbols of a given type.
	 */
	private Symbol [] getSymbolArray (int type)
	{
		if (symbolCounter[type] == 0) return null;

		Symbol symbols[] = new Symbol[symbolCounter[type]];
		int k = 0;

		Symbol [] allSymbols = getAllSymbolsArray();
		for (int i = 0; i < allSymbols.length; i++)
		{
			if (allSymbols[i].getType() == type) symbols[k++] = allSymbols[i];
		}

		return symbols;
	}
	
	// TODO: arraycopy.
	private Symbol[] getAllSymbolsArray()
	{
		Object symbolObjects[] = symbolMap.keySet().toArray();
		Symbol symbols[] = new Symbol[symbolObjects.length];
		for (int i = 0; i < symbolObjects.length; i++)
		{
			symbols[i] = (Symbol) symbolMap.get(symbolObjects[i]);
		}

		return symbols;
	}


/////////////////////////////////// CALCULATE COLUMNS ////////////////////////////////

	/**
	 * Calculate 2^x
	 */
	private int power2(int power)
	{
		int val = 1;	
		val <<= power;
		return val;
	}

	/**
	 * @return the length of a column. 
	 */
	private void calculateColumnLength()
	{
		// If calculated once, return internal stored value.
		//if (columnLength != 0) return columnLength;

		int total = power2(totalSymbols(0));
		for (int i = 1; i < totalTypes(); i++)
		{
			if (totalSymbols(i) != 0) 
				total *= totalSymbols(i);
		}

		columnLength = total;
	}


	/**
	 * Add to each symbol all the possible permutations.
	 */
	private void calculateColumnBits()
	{
		calculateColumnLength(); 

		int trueBits = 1;

		// All symbols of type 0.
		Symbol symbols[] = getSymbols(0);
		for (int i = 0; i < totalSymbols(0); i++)
		{
			symbols[i].column =  new Column (columnLength, trueBits, 1, 0);
			symbols[i].originalColumn =  new Column (columnLength, trueBits, 1, 0);
			trueBits <<= 1; 
		}
		
		// And the rest of the symbol types.
		trueBits = power2(totalSymbols(0));

		for (int type = 1; type < totalTypes(); type++)
		{
			symbols = getSymbols(type);
			for (int i = 0; i < totalSymbols(type); i++)
			{
				symbols[i].column = new Column (columnLength, trueBits, (symbols.length - 1), i);
				symbols[i].originalColumn =  new Column (columnLength, trueBits, (symbols.length - 1), i);
			}

			if (totalSymbols(type) > 0) 
				trueBits *= symbols.length;
		}
	}


	private String getHashKey(String name, int type)
	{
			return name + type;
	}
				

}


