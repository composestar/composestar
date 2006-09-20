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
import java.util.LinkedList;
import Composestar.Utils.Debug;

public class FilterReasoningEngine 
{
	public static final int FALSE = 0;
	public static final int UNKNOWN = 1;
	public static final int TRUE = 2;
	
	/**
 	 * Strategy pattern, treeBuilder creates the FIRE tree.	
	 */
	TreeBuilder treeBuilder = null;

	/** 
	 * The results of the calculations are stored in this stateTable.
	 */
	StateTable stateTable = null; 

	// Get the symbols. The symbol table can be reused.
	HashMap symbolHash =  null;

	/** 
	 * ctor, read the filtersets from the format specified by the ANTLR syntax.
	 */
	public FilterReasoningEngine(java.io.InputStream input)
	{
		treeBuilder = new AntlrStdIn(input);
	}

	/**
	 * ctor, read the filtersets from the repository.
	 */
	public FilterReasoningEngine(LinkedList list)
	{	
		treeBuilder = new RepositoryPtrList(list);
	}

	/**
	 * Build the tree, calculate the results
	 */
	public void run() 
	{
		Tand fc = (Tand) treeBuilder.getTree(this); 

		//Debug.out (Debug.MODE_INFORMATION, "FIRE", fc.toTreeString());

		SymbolTable st = SymbolTable.getInstance();
		//Debug.out (Debug.MODE_INFORMATION, "FIRE", "\n" + st.toString()); // print the symboltable.

		// Initialize status column
		Symbol [] symbols = st.getAllSymbols();
		Symbol [] conditions = st.getSymbols(0);

		stateTable = new StateTable(symbols, conditions, this);
		StatusColumn status = new StatusColumn(st.getColumnLength(), true);

		fc.calculateStatus(status, stateTable);

		symbolHash = st.getHashMap();

		//Debug.out (Debug.MODE_INFORMATION, "FIRE", "Status after calculation: " + status);
		//Debug.out (Debug.MODE_INFORMATION, "FIRE", stateTable.toString());
	}

	/**
	 * Return the result tree.
	 */
	public Node getTree ()
	{
		return stateTable.getNode(); 
	}
	
	/**
	 * Return all paths to a specific node
	 */
	public LinkedList getPaths (Node toNode)
	{
		Node tree = getTree();

		LinkedList ll = new LinkedList();
		DepthFirstIterator dfi = (DepthFirstIterator) tree.getIterator(DepthFirstIterator.class);

		do
		{	dfi.skipTo (toNode);

			if (!dfi.isDone() && !dfi.getNode().hasChildren())
			{
				ll.add (dfi.getPath());
			}
		} 
		while (!dfi.isDone());

		return ll;
	}

	/**
	 * Return all leaf nodes from the tree
	 */
	public LinkedList getLeafs ()
	{
		Node tree = getTree();
		LinkedList ll = new LinkedList();

		DepthFirstIterator dfi = (DepthFirstIterator) tree.getIterator(DepthFirstIterator.class);

		while (!dfi.isDone())
		{
			if (!dfi.getNode().hasChildren()) 
				ll.add(dfi.getNode());

			dfi.next();
		}

		return ll;
	}

	
	////////////////// FilterSetInfo /// FIREInfo
	
	protected HashMap concernSymbols = new HashMap();
	
	public void addConcernSymbol(String concern, Symbol s)
	{
		concernSymbols.put(concern, s);
	}
	
	public Symbol getConcernSymbol(String concern)
	{
		return (Symbol) concernSymbols.get(concern);
	}
	
	public String getConcern (String symbolName)
	{
		java.util.Iterator itr = concernSymbols.keySet().iterator();
		
		while (itr.hasNext())
		{
			String key = (String) itr.next();
			Symbol s = (Symbol) concernSymbols.get(key);
			
			if (s != null && s.getName().equals(symbolName)) return key;
		}
		
		return null;
	}
	
	//////////////////////////////////////////////////////////////////////

	// We suffer from the fact that the symbol table is a singleton.
	/*
	public boolean equals (FilterReasoningEngine fire)
	{
		return getTree().equals(fire.getTree());
	}
	*/
}
