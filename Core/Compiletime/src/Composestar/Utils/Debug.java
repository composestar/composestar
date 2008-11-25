/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Utils;

import java.util.StringTokenizer;

import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Logging.LocationProvider;
import Composestar.Utils.Logging.LogMessage;
import Composestar.Utils.Logging.Log4j.CrucialLevel;

/**
 * @deprecated use Composestar.Utils.Logging.CPSLogger or
 *             Composestar.Utils.Logging.SafeLogger
 */
@Deprecated
public final class Debug
{
	public static final int MODE_ERROR = 0;

	public static final int MODE_CRUCIAL = 1;

	public static final int MODE_WARNING = 2;

	public static final int MODE_INFORMATION = 3;

	public static final int MODE_DEBUG = 4;

	public static final int MODE_DEFAULTMODE = MODE_INFORMATION;

	private static int currentMode = MODE_DEFAULTMODE;

	private static int warnings;

	private static int errors;

	/**
	 * Returns the mode level that correspondends to the specified description.
	 */
	private static int getModeLevel(String description)
	{
		if ("error".equalsIgnoreCase(description))
		{
			return MODE_ERROR;
		}
		else if ("crucial".equalsIgnoreCase(description))
		{
			return MODE_CRUCIAL;
		}
		else if ("warning".equals(description) || "WARN".equals(description))
		{
			return MODE_WARNING;
		}
		else if ("information".equals(description) || "INFO".equals(description))
		{
			return MODE_INFORMATION;
		}
		else if ("debug".equalsIgnoreCase(description))
		{
			return MODE_DEBUG;
		}
		else
		{
			return MODE_CRUCIAL;
		}
	}

	public static void out(int mode, String module, String msg, String filename, int line)
	{
		if (currentMode >= mode)
		{
			if (mode == MODE_WARNING)
			{
				warnings++;
			}
			else if (mode == MODE_ERROR)
			{
				errors++;
			}

			if (filename == null)
			{
				filename = "";
			}

			CPSLogger logger = CPSLogger.getCPSLogger(module);
			switch (mode)
			{
				case MODE_CRUCIAL:
					logger.log(CrucialLevel.CRUCIAL, new LogMessage(msg, filename, line));
					break;
				case MODE_DEBUG:
					logger.debug(new LogMessage(msg, filename, line));
					break;
				case MODE_ERROR:
					logger.error(new LogMessage(msg, filename, line));
					break;
				case MODE_WARNING:
					logger.warn(new LogMessage(msg, filename, line));
					break;
				case MODE_INFORMATION:
					logger.info(new LogMessage(msg, filename, line));
					break;
			}
		}
	}

	public static void out(int mode, String module, String msg, String filename)
	{
		out(mode, module, msg, filename, 0);
	}

	public static void out(int mode, String module, String msg, RepositoryEntity re)
	{
		String filename = re.getSourceInformation().getFilename().toString();
		int linenumber = re.getSourceInformation().getLine();
		out(mode, module, msg, filename, linenumber);
	}

	public static void out(int mode, String module, String msg, LocationProvider loc)
	{
		String filename = loc.getFilename();
		int lineNumber = loc.getLineNumber();
		out(mode, module, msg, filename, lineNumber);
	}

	public static void out(int mode, String module, String msg)
	{
		out(mode, module, msg, "", 0);
	}

	public static void parseLog(String log)
	{
		StringTokenizer lineTok = new StringTokenizer(log, "\n");
		while (lineTok.hasMoreTokens())
		{
			String line = lineTok.nextToken();
			String[] parts = StringUtils.split(line, '~');

			if (parts.length == 5)
			{
				String module = parts[0];
				String mode = parts[1];
				String filename = parts[2];
				String linenum = parts[3];
				String msg = parts[4];

				out(getModeLevel(mode), module, msg, filename, Integer.parseInt(linenum));
			}
			else
			{
				Debug.out(Debug.MODE_ERROR, "DEBUG", "Wrong log line: '" + line + "'");
			}
		}
	}

}
