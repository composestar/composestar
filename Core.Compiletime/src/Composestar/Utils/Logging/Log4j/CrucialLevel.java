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

import org.apache.log4j.Level;

/**
 * Adds support for the "useless" crucial level currently used by Compose*
 * 
 * @author Michiel Hendriks
 */
public class CrucialLevel extends Level
{
	public static final int CRUCIAL_INT = 55000;

	public static final Level CRUCIAL = new CrucialLevel(CRUCIAL_INT, "CRUCIAL", 0);

	protected CrucialLevel(int level, String levelStr, int syslogEquivalent)
	{
		super(level, levelStr, syslogEquivalent);
	}

	public static Level toLevel(String s)
	{
		return toLevel(s, Level.DEBUG);
	}

	public static Level toLevel(int i)
	{
		return toLevel(i, Level.DEBUG);
	}

	public static Level toLevel(int i, Level level)
	{
		if (i == CRUCIAL_INT)
		{
			return CRUCIAL;
		}
		return Level.toLevel(i, level);
	}

	public static Level toLevel(String s, Level level)
	{
		if ("CRUCIAL".equalsIgnoreCase(s))
		{
			return CRUCIAL;
		}
		return Level.toLevel(s, level);
	}
}
