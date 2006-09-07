/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.COPPER;

import Composestar.Utils.*;
import Composestar.Core.Exception.ModuleException;
import antlr.*;
import antlr.debug.misc.*;

import java.io.*;


/**
 * Parses the cps file and builds the parse tree
 */
public class CPSFileParser {
	
  private CpsParser parser;

  public void parseCpsFileWithName(String file)  throws ModuleException
  {
  	try {
      this.readCpsFile(file);
      this.parseCpsFile(file);
      
      COPPER.setParser(parser);

      COPPER.setParseTree((CommonAST)parser.getAST());
 
    	Debug.out(Debug.MODE_DEBUG,"COPPER","Parse tree: "+parser.getAST().toStringTree());
        if(COPPER.isShowtree()) {
        	System.out.println(COPPER.getParseTree().toStringList() + "\n\n");
        	ASTFrame frame = new ASTFrame("The tree", COPPER.getParseTree());
        	frame.setSize(800, 600);
        	frame.setVisible(true);
        }
  	}
  	catch ( ModuleException e )
	{
  		throw e;
	}
  }

  public void readCpsFile(String file) throws ModuleException
  {
    BufferedReader d;
    try
	{
	    COPPER.setCpscontents("");
	    d = new BufferedReader(new InputStreamReader((new FileInputStream(file))));
        String line = d.readLine();
	    while (line != null){
            COPPER.setCpscontents(COPPER.getCpscontents() + (line + "\r\n"));
            line = d.readLine();
        }
	}
    catch(IOException ioe)
	{
    	Debug.out(Debug.MODE_ERROR,"COPPER","The specified file: "+file+" could not be found or read!");
    	throw new ModuleException("The specified file: "+file+" could not be found or read!","COPPER");
	}
  }

  public void parseCpsFile(String file) throws ModuleException
  {
  	StringReader sr;
    CpsPosLexer lexer;

		  try 
		  {
			  sr = new StringReader(COPPER.getCpscontents());
			  lexer = new CpsPosLexer(sr);
			  parser = new CpsParser(lexer);
			  parser.setASTNodeClass("Composestar.Core.COPPER.CpsAST");
			  parser.concern();
		  }
		  catch (NoViableAltException e)
		  {
			  Debug.out(Debug.MODE_ERROR, "COPPER", "Syntax Error: " + e.getMessage(),file,e.getLine());
			  throw new ModuleException("Syntax error in cps file: " + e, "COPPER");
		  }
		  catch (NoViableAltForCharException e)
		  {
			  Debug.out(Debug.MODE_ERROR, "COPPER", "Syntax Error: " + e.getMessage(),file,e.getLine());
			  throw new ModuleException("Syntax error in cps file: " + e, "COPPER");
		  }
		  catch(RecognitionException e)
		  {
			  Debug.out(Debug.MODE_ERROR, "COPPER", "Syntax Error: " + e.getMessage(),file,e.getLine());
			  throw new ModuleException("Syntax error in cps file: " + e, "COPPER");
		  }
		  catch (ANTLRException e)
		  {
			  Debug.out(Debug.MODE_ERROR, "COPPER", "Syntax Error: " + e.getMessage(),file,0);
			  throw new ModuleException("Syntax error in cps file: " + e, "COPPER");
		  }
  }
}
