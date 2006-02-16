package Composestar.Core.FIRE;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * 
 * $Id: AntlrStdIn.java,v 1.1 2006/02/13 11:16:55 pascal Exp $
 * 
**/

import java.io.*;
import antlr.CommonAST;

public class AntlrStdIn extends TreeBuilder 
{
	InputStream inputStream = null;

	public AntlrStdIn (InputStream input)
	{
		inputStream = input;
	}

	public FilterComponent getTree()
	{
		return getTree(new  FilterReasoningEngine(inputStream));
	}

	public FilterComponent getTree (FilterReasoningEngine fireInfo)
	{

		//	Uncomment the following lines, if you want to use the internal FIRE parser.		
		/*			

		 try 
		 {
		 // We start to parse, make sure we empty the symboltable.
		 (SymbolTable.getInstance()).empty();
			
		 Debug.out(3, "Excellent choice! Using internal FIRE parser\n");
		 Debug.out(2, "=-= Parsing Filter Input =-=\n");
			
		 TT_Lexer lexer = new TT_Lexer(new DataInputStream(inputStream));
		 TT_BuildTree parser = new TT_BuildTree(lexer);
		 parser.program();
		 Debug.out(3, parser.getAST().toStringList() + "\n");

		 Debug.out(2, "=-= Building FilterComponent Tree =-=\n");
		 TT_BuildFireTree checker = new TT_BuildFireTree();

		 FilterComponent fc = checker.program((CommonAST)parser.getAST());

		 // No more symbols.
		 (SymbolTable.getInstance()).done();
		 return fc;
		 } 
		 catch(Exception e) 
		 {
		 System.err.println("Exception raised while building the FilterComponent Tree: " + e);
		 }
		 */
		return null;
	}
}
