package Composestar.Utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;

import Composestar.Core.RepositoryImplementation.RepositoryEntity;

public final class Debug
{
	public static final int MODE_ERROR = 0;
	public static final int MODE_CRUCIAL = 1;
	public static final int MODE_WARNING = 2;
	public static final int MODE_INFORMATION = 3;
	public static final int MODE_DEBUG = 4;
	public static final int MODE_DEFAULTMODE = MODE_INFORMATION;

	private static int currentMode = MODE_DEFAULTMODE;
	private static int errThreshold = -1;
	private static int warnings;

	/**
	 * Public constructor needed because this class is serialized into the
	 * repository for some reason.
	 */
	public Debug()
	{
		//throw new UnsupportedOperationException();
	}

	public static void setMode(int mode)
	{
		currentMode = mode;
	}

	public static int getMode()
	{
		return currentMode;
	}
	
	public static void setErrThreshold(int et)
	{
		errThreshold = et;
	}
	
	/**
	 * Return true if this message will be logged with Debug.out(...) is called
	 */
	public static boolean willLog(int mode)
	{
		return currentMode >= mode;
	}

	/**
	 * Returns whether a message with the specified mode should be printed to System.out. 
	 */
	private static boolean printToOut(int mode)
	{
		return true;
	}
	
	/**
	 * Returns whether a message with the specified mode should be printed to System.err. 
	 */
	private static boolean printToErr(int mode)
	{
		if (mode == MODE_CRUCIAL)
			return false;
		
		return (mode <= errThreshold);
	}
	
	/**
	 * Returns the description for the specified mode.
	 */
	private static String getModeDescription(int mode)
	{
		switch (mode)
		{
			case MODE_ERROR:
				return "error";

			case MODE_CRUCIAL:
				return "crucial";

			case MODE_WARNING:
				return "warning";

			case MODE_INFORMATION:
				return "information";

			case MODE_DEBUG:
				return "debug";

			default:
				return "unknown";
		}
	}

	/**
	 * Returns the mode level that correspondends to the specified description.
	 */
	private static int getModeLevel(String description)
	{
		if ("error".equals(description))
		{
			return MODE_ERROR;
		}
		else if ("crucial".equals(description))
		{
			return MODE_CRUCIAL;
		}
		else if ("warning".equals(description))
		{
			return MODE_WARNING;
		}
		else if ("information".equals(description))
		{
			return MODE_INFORMATION;
		}
		else if ("debug".equals(description))
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
			String modeDescription = getModeDescription(mode);
			
			if (mode == MODE_WARNING)
			{
				warnings++;
			}
			
			if (filename == null)
			{
				filename = "";
			}
			
			if (printToOut(mode))
			{
				String full = module + '~' + modeDescription + '~' + filename + '~' + line + '~' + msg;
				System.out.println(full);
			}
			
			if (printToErr(mode))
			{
				String full = StringUtils.capitalize(modeDescription) + ": " + msg;
				System.err.println(full);
			}
		}
	}
	
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
		if (warnings > 0)
		{
			System.out.println("Warnings: " + warnings + '.');
		}
	}

	public static String stackTrace(Throwable t)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return sw.toString();
	}
}
