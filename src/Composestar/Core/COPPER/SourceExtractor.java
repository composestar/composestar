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

import Composestar.Core.Exception.ModuleException;
import Composestar.Utils.Debug;

/**
 * Extracts embedded source
 */
public class SourceExtractor
{
	public static final String MODULE_NAME = "COPPER";

	public void extractSource() throws ModuleException
	{
		if (COPPER.getParser().sourceLang != null)
		{
			String b = COPPER.getCpscontents();

			// find second-last index of '}'
			int endpos = b.lastIndexOf('}'); // Closing tag of concern
			if (endpos > 0)
			{
				endpos = b.lastIndexOf('}', endpos - 1); // Closing tag of implementation by
			}

			if (endpos <= 0)
			{
				throw new ModuleException("Expecting closing '}' at end of cps file and after embedded source", MODULE_NAME);
			}

			String es = b.substring(COPPER.getParser().startPos, endpos);
			COPPER.setEmbeddedSource(es);
			
			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, es);
		}
	}
}
