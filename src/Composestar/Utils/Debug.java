package Composestar.Utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;

import Composestar.Core.RepositoryImplementation.RepositoryEntity;

/**
 * Summary description for Debug.
 */
public class Debug 
{
	public static final int MODE_ERROR = 0;
	public static final int MODE_CRUCIAL = 1;
	public static final int MODE_WARNING = 2;
	public static final int MODE_INFORMATION = 3;
	public static final int MODE_DEBUG = 4;
	
	public static final int MODE_DEFAULTMODE = MODE_INFORMATION;
	
	private static int currentMode = MODE_DEFAULTMODE;
	private static int warnings = 0;
    
	/**
	 * Public constructor needed because this class is
	 * serialized into the repository for some reason.
	 */
	public Debug() {}
    
	public static void setMode(int mode) 
	{
		currentMode = mode;     
	}
    
	public static int getMode() 
	{
		return currentMode;     
	}
	
	public static void out(int mode, String module, String msg, String filename, int line)
	{
		if (currentMode >= mode) 
		{
			String modeDescription;
    	
			switch (mode)
			{
				case MODE_ERROR:
					modeDescription = "error";
					break;
					
				case MODE_CRUCIAL:
					modeDescription = "crucial";
					break;
					
				case MODE_WARNING:
					warnings++;
					modeDescription = "warning";
					break;

				case MODE_INFORMATION:
					modeDescription = "information";
					break;
					
				case MODE_DEBUG:
					modeDescription = "debug";
					break;

				default:
					modeDescription = "unknown";
					break;
			}

			if (filename == null) filename = "";
			System.out.println(module + '~' + modeDescription + '~' + filename + '~' + line + '~' + msg);
		}
	}
	
	public static void out(int mode, String module, String msg, String filename)
	{
		out(mode,module,msg,filename,0);
	}
	
	public static void out(int mode, String module, String msg, RepositoryEntity re)
	{
		String filename = re.getDescriptionFileName();
		int linenumber = re.getDescriptionLineNumber();
		out(mode,module,msg,filename,linenumber);
	}
    
	public static void out(int mode, String module, String msg) 
	{
		out(mode,module,msg,"",0);
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
				Debug.out(Debug.MODE_ERROR, "DEBUG", "Wrong log line: '" + line + "'");
		}
	}
	
	private static int getModeLevel(String mode)
	{
		if ("error".equals(mode))
			return MODE_ERROR;
		else if ("crucial".equals(mode))
			return MODE_CRUCIAL;
		else if ("warning".equals(mode))
			return MODE_WARNING;
		else if ("information".equals(mode))
			return MODE_INFORMATION;
		else if ("debug".equals(mode))
			return MODE_DEBUG;
		else
			return MODE_CRUCIAL;
	}
    
	public static void outWarnings() 
	{
		if (warnings > 0)
			System.out.println("Warnings: " + warnings + '.');
	}
	
	public static String stackTrace(Throwable t)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);			
		t.printStackTrace(pw);
		return sw.toString();
	}
}
