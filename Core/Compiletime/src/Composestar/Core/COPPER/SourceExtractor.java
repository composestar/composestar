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

import Composestar.Utils.Debug;

/**
 * Extracts embedded source
 */
public class SourceExtractor
{

	public void extractSource()
	{
		if (COPPER.getParser().sourceIncluded)
		{
			String b = COPPER.getCpscontents();
			int endpos = b.lastIndexOf('}'); // Closing tag cps
			if (endpos > 0)
			{
				endpos = b.lastIndexOf('}', endpos - 1); // Closing tag
				// implementation by
			}
			if (endpos <= 0)
			{
				Debug.out(Debug.MODE_WARNING, "COPPER",
						"Expecting closing '}' at end of cps file and after embedded source");
				Debug.out(Debug.MODE_WARNING, "COPPER", "Ignoring embedded source");
				return;
			}
			COPPER.setEmbeddedSource(b.substring(COPPER.getParser().startPos, endpos));
			Debug.out(Debug.MODE_DEBUG, "COPPER", COPPER.getEmbeddedSource());
		}
	}

	/**
	 * Gets the position of where the embedded source ends. This is done by
	 * reversing the cps file and using a special parser to find 'the beginning
	 * of the end'. The starting position is already known from the regular
	 * parsing.
	 */
	/*
	 * private int getEndPos(String b) { CpsSrcPosLexer srclexer; CpsSrcParser
	 * srcparser; try { // We are parsing backwards so reverse! b.reverse(); //
	 * Create parser and lexer srclexer = new CpsSrcPosLexer(new
	 * StringReader(b.toString())); srcparser = new CpsSrcParser(srclexer); //
	 * Start parsing with the src method srcparser.src(); // Reversed code is
	 * not so nice, so reverse it back :) b.reverse(); return
	 * (srcparser.endPos); } catch (ANTLRException e) { e.printStackTrace();
	 * return (-1); } return b.lastIndexOf("}"); }
	 */
}
