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

import java.util.StringTokenizer;
import java.util.Vector;

public class PathHelper
{
	private int curelement;

	private Vector pieces;

	PathHelper()
	{
		pieces = new Vector();
	}

	public Piece getNextPiece()
	{
		curelement++;
		return (Piece) (pieces.elementAt(curelement - 1));
	}

	public int getNumberOfPieces()
	{
		return pieces.size();
	}

	public boolean splitPath(String path)
	{
		StringTokenizer strTok;
		Piece piece;

		pieces.clear();
		strTok = new StringTokenizer(path, "\\");

		while (strTok.hasMoreTokens())
		{
			piece = splitPiece(strTok.nextToken());
			pieces.add(piece);
		}

		curelement = 0;
		return true;
	}

	private Piece splitPiece(String pathpart)
	{
		StringTokenizer strTok;
		Piece piece;
		String temp;

		piece = new Piece();

		// @(number | last)
		strTok = new StringTokenizer(pathpart, "@");
		if (pathpart.startsWith("@"))
		{
			handlePosition(strTok.nextToken(), piece);
		}

		// name@ (number | last) [new]
		if (strTok.hasMoreTokens())
		{ // name
			temp = strTok.nextToken();
			piece.setText(temp);

			if (strTok.hasMoreTokens())
			{ // @(number | last) [new]
				temp = strTok.nextToken();
				handlePosition(temp, piece);
			}
		}

		return piece;
	}

	private void handlePosition(String temp, Piece piece)
	{
		if (temp.endsWith("new"))
		{
			piece.setCreateNew(true);
			if (temp.length() > 3)
			{ // extra check
				// (number | last)
				temp = temp.substring(0, temp.length() - 3);
				piece.setPosition(temp);
			}
		}
		else
		{
			piece.setCreateNew(false);
			piece.setPosition(temp); // (number | last)
		}
	}
}
