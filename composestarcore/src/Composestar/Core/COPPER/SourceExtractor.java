/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: SourceExtractor.java,v 1.2 2006/02/13 11:53:08 pascal Exp $
 */
package Composestar.Core.COPPER;

import java.io.*;

import Composestar.Utils.*;
import antlr.ANTLRException;


/**
 * Extracts embedded source
 */
public class SourceExtractor {
  
	public void extractSource() {
	   if (COPPER.getParser().sourceIncluded) {
	      StringBuffer b = new StringBuffer();
	      b.append(COPPER.getCpscontents());
	      int endpos = b.length() - getEndPos(b);
	      COPPER.setEmbeddedSource(b.substring(COPPER.getParser().startPos, endpos));
	      Debug.out(Debug.MODE_DEBUG,"COPPER",COPPER.getEmbeddedSource());
	   }
  }

  /**
   * Gets the position of where the embedded source ends. This is done by reversing the cps file and
   * using a special parser to find 'the beginning of the end'. The starting position is already known
   * from the regular parsing.
   */
  private int getEndPos(StringBuffer b) {
    CpsSrcPosLexer srclexer;
    CpsSrcParser srcparser;
    
    try {
      // We are parsing backwards so reverse!
      b.reverse();
      // Create parser and lexer
      srclexer = new CpsSrcPosLexer(new StringReader(b.toString()));
      srcparser = new CpsSrcParser(srclexer);
      // Start parsing with the src method
      srcparser.src();
      // Reversed code is not so nice, so reverse it back :)
      b.reverse();
      return (srcparser.endPos);
    } 
    catch (ANTLRException e)
	{
      e.printStackTrace();
      return (-1);
    }
  }
}
