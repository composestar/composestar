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

import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.helpers.PatternParser;

/**
 * PatternParser subclass to add support for source location formatting rules:
 * <dl>
 * <dt>%s</dt>
 * <dd>source filename</dd>
 * <dt>%S</dt>
 * <dd>source line number</dd>
 * <dt>%$</dt>
 * <dd>source line position</dd>
 * </dl>
 * 
 * @author Michiel Hendriks
 */
public class CPSPatternParser extends PatternParser
{
	public CPSPatternParser(String pattern)
	{
		super(pattern);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.helpers.PatternParser#finalizeConverter(char)
	 */
	@Override
	protected void finalizeConverter(char c)
	{
		PatternConverter pc;
		switch (c)
		{
			// source location
			case 's':
				pc = new SourcePatternConverter(formattingInfo, SourcePatternConverter.FIELD_FILE);
				currentLiteral.setLength(0);
				addConverter(pc);
				break;
			// source line
			case 'S':
				pc = new SourcePatternConverter(formattingInfo, SourcePatternConverter.FIELD_LINE);
				currentLiteral.setLength(0);
				addConverter(pc);
				break;
			// source line
			case '$':
				pc = new SourcePatternConverter(formattingInfo, SourcePatternConverter.FIELD_LINEPOS);
				currentLiteral.setLength(0);
				addConverter(pc);
				break;
			default:
				super.finalizeConverter(c);
		}
	}
}
