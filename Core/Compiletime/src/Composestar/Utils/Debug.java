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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import Composestar.Core.RepositoryImplementation.RepositoryEntity;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Logging.LocationProvider;
import Composestar.Utils.Logging.LogMessage;
import Composestar.Utils.Logging.Log4j.CrucialLevel;

/**
 * @deprecated use Composestar.Utils.Logging.CPSLogger or
 *             Composestar.Utils.Logging.SafeLogger
 */
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

	public static void setMode(int mode)
	{
		currentMode = mode;
		Logger root = Logger.getRootLogger();
		root.setLevel(debugModeToLevel(mode));
	}

	/**
	 * Return the level for the given legacy debug mode
	 * 
	 * @param mode
	 * @return
	 */
	public static Level debugModeToLevel(int mode)
	{
		switch (mode)
		{
			case MODE_ERROR:
				return Level.ERROR;
			case MODE_CRUCIAL:
				return CrucialLevel.CRUCIAL;
			case MODE_WARNING:
				return Level.WARN;
			case MODE_INFORMATION:
				return Level.INFO;
			case MODE_DEBUG:
				return Level.DEBUG;
			case 5:
				return Level.TRACE;
			default:
				return Level.WARN;
		}
	}

	public static int getMode()
	{
		return currentMode;
	}

	/**
	 * Return true if this message will be logged with Debug.out(...) is called
	 */
	public static boolean willLog(int mode)
	{
		return currentMode >= mode;
	}

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

	/**
	 * @deprecated
	 */
	public static void out(int mode, String module, String msg, String filename)
	{
		out(mode, module, msg, filename, 0);
	}

	public static void out(int mode, String module, String msg, RepositoryEntity re)
	{
		String filename = re.getDescriptionFileName();
		int linenumber = re.getDescriptionLineNumber();
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

	public static void outWarnings()
	{
		if ((warnings > 0) || (errors > 0))
		{
			System.out.println("Warnings: " + warnings + "; Errors: " + errors);
		}
	}

	public static int numErrors()
	{
		return errors;
	}

	public static int numWarnings()
	{
		return warnings;
	}

	public static String stackTrace(Throwable t)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return sw.toString();
	}
}
