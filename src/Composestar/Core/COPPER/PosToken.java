/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
/**
 * class to override the common antlr tokens so we can keep track of the starting
 * position of each token
 */
package Composestar.Core.COPPER;

import antlr.CommonToken;

/**
 * Special token class to keep track of the position of tokens in the cps file
 */
public class PosToken extends CommonToken
{
	int col = 0;

	int bytePos = 0;

	/**
	 * Constructor
	 */
	public PosToken()
	{
		super();
	}

	/**
	 * Constructor, makes a token of type t
	 * 
	 * @param t type of the token
	 */
	public PosToken(int t)
	{
		setType(t);
	}

	/**
	 * Constructor, makes a token of type t with text txt
	 * 
	 * @param t type of the token
	 * @param txt token text
	 */
	public PosToken(int t, String txt)
	{
		super(t, txt);
	}

	/**
	 * Sets the line this token is found at
	 * 
	 * @param c line number
	 */
	public void setColumn(int c)
	{
		col = c;
	}

	/**
	 * Gets line this token is found at
	 * 
	 * @return line number
	 */
	public int getColumn()
	{
		return col;
	}

	/**
	 * Sets the byte position in the file this token is found at
	 * 
	 * @param c position in the file
	 */
	public void setBytePos(int c)
	{
		bytePos = c;
	}

	/**
	 * Gets the byte position in the file that this token is found at
	 * 
	 * @return position in the file
	 */
	public int getBytePos()
	{
		return bytePos;
	}
}
