/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Utils.Logging.Log4j;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.PatternParser;

/**
 * Custom PatternLayout instance to add support for input source locations
 * 
 * @author Michiel Hendriks
 */
public class CPSPatternLayout extends PatternLayout
{
	public CPSPatternLayout()
	{}

	public CPSPatternLayout(String format)
	{
		super(format);
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.log4j.PatternLayout#createPatternParser(java.lang.String)
	 */
	@Override
	protected PatternParser createPatternParser(String pattern)
	{
		return new CPSPatternParser(pattern);
	}

}
