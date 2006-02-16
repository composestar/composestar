/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: CpsSrcPosLexer.java,v 1.1 2006/02/13 11:16:54 pascal Exp $
 */
package Composestar.Core.COPPER;

import java.io.InputStream;
import java.io.Reader;

import antlr.CharStreamException;
import antlr.InputBuffer;
import antlr.Token;

/**
 * Special versions of the CpsSrcLexer which allows you to determine the starting positions of tokens in the cps file
 */
public class CpsSrcPosLexer extends CpsSrcLexer {
  protected int col = 1;
  protected int tokenStartCol = 1;
  protected int tokenStartLine = 1;
  protected int tokenStartCharPos = 0; //byte position start at 0
  protected int charPos = 0;


  /**
   * Construction
   *
   * @param in Inputstream containing the text to be parsed
   */
  public CpsSrcPosLexer(InputStream in) {
    super(in);
  }


  /**
   * Construction
   *
   * @param in Reader containing the text to be parsed
   */
  public CpsSrcPosLexer(Reader in) {
    super(in);
  }


  /**
   * Construction
   *
   * @param in InputBuffer containing the text to be parsed
   */
  public CpsSrcPosLexer(InputBuffer ib) {
    super(ib);
  }


  /**
   * Consumes a character from the input
   */
  public void consume() throws CharStreamException {
    {
      super.consume();
      if (inputState.guessing == 0) {
        col++;
        charPos++;
      }
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
