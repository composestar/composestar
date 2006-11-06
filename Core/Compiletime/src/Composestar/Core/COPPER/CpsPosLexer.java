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

import antlr.*;

import java.io.*;


/**
 * Special versions of the CpsLexer which allows you to determine the starting positions of tokens in the cps file
 */
public class CpsPosLexer extends CpsLexer {
  protected int col = 1;
  protected int tokenStartCol = 1;
  protected int tokenStartLine = 1;
  protected int tokenStartCharPos; //byte position start at 0
  protected int charPos;


  /**
   * Construction
   *
   * @param in Inputstream containing the text to be parsed
   */
  public CpsPosLexer(InputStream in) {
    super(in);
  }


  /**
   * Construction
   *
   * @param in Reader containing the text to be parsed
   */
  public CpsPosLexer(Reader in) {
    super(in);
  }


  /**
   * Construction
   *
   * @param in InputBuffer containing the text to be parsed
   * @param ib
   */
  public CpsPosLexer(InputBuffer ib) {
    super(ib);
  }


  /**
   * Consumes a character from the input
   */
  public void consume() throws CharStreamException {
    
      super.consume();
      if (inputState.guessing == 0) {
        col++;
        charPos++;
      }
    
  }


  /**
   * Going to a new line; so increase the column count
   */
  public void newline() {
    super.newline(); //increment inputState.line
    col = 1;
  }


  /**
   * Make a new 'special' token containing positioning information
   *
   * @param t type of the token
   */
  protected Token makeToken(int t) {
    PosToken tok = new PosToken(t);
    tok.setColumn(tokenStartCol);
    tok.setLine(tokenStartLine);
    tok.setBytePos(tokenStartCharPos);
    return tok;
  }


  /**
   * Needed for CharScanner
   */
  public void resetText() {
    super.resetText();
    tokenStartCol = col;
    tokenStartLine = getLine();
    tokenStartCharPos = charPos;
  }
}
