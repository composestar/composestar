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

public class Piece
{
	private String text;

	private String position;

	private boolean createNew;

	Piece()
	{
		text = null; // default values
		position = null;
		createNew = false;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String intext)
	{
		text = intext;
	}

	public String getPosition()
	{
		return position;
	}

	public void setPosition(String inposition)
	{
		position = inposition;
	}

	public boolean isCreateNew()
	{
		return createNew;
	}

	public void setCreateNew(boolean increateNew)
	{
		createNew = increateNew;
	}
}
