/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: CpsParseTreeWalker.java,v 1.1 2006/02/16 23:03:49 pascal_durr Exp $
 */
package Composestar.Core.COPPER;

import Composestar.Core.Exception.ModuleException;
import antlr.RecognitionException;


/**
 * Build the first version of the objects for the repository by walking the parse tree
 */
public class CpsParseTreeWalker {
  private CpsASTFactory factory = null;

	public void setCpsASTFactory(CpsASTFactory factory)
	{
		this.factory = factory;
	}

  public void walkTree() throws ModuleException {
    CpsTreeWalker walker;

    try {
      walker = new CpsTreeWalker();
	  walker.setASTFactory(factory);
      walker.concern(COPPER.getParseTree());
    } catch (RecognitionException r)
    {
		throw new ModuleException("AST Error: " + r.getMessage(), "COPPER", r.getFilename(), r.getLine());
    }
  }
}
