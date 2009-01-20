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

import org.apache.log4j.helpers.FormattingInfo;
import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.spi.LoggingEvent;

import Composestar.Utils.Logging.LocationProvider;

/**
 * Special converter to handle source location formatters
 * 
 * @author Michiel Hendriks
 */
public class SourcePatternConverter extends PatternConverter
{
	/**
	 * field for the file name
	 */
	public static final int FIELD_FILE = 0;

	/**
	 * Line number
	 */
	public static final int FIELD_LINE = 1;

	/**
	 * Line position number
	 */
	public static final int FIELD_LINEPOS = 2;

	protected int sourceField;

	public SourcePatternConverter(FormattingInfo fi, int inSourceField)
	{
		super(fi);
		sourceField = inSourceField;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.apache.log4j.helpers.PatternConverter#convert(org.apache.log4j.spi
	 * .LoggingEvent)
	 */
	@Override
	protected String convert(LoggingEvent event)
	{
		LocationProvider lp = null;
		if (event instanceof LocationProvider)
		{
			lp = (LocationProvider) event;
		}
		else if (event.getMessage() instanceof LocationProvider)
		{
			lp = (LocationProvider) event.getMessage();
		}
		if (lp == null)
		{
			return "";
		}
		switch (sourceField)
		{
			case FIELD_FILE:
				return lp.getFilename();
			case FIELD_LINE:
				return "" + lp.getLineNumber();
			case FIELD_LINEPOS:
				return "" + lp.getLinePosition();
		}
		return "";
	}

}
