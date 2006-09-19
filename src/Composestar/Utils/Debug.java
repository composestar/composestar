package Composestar.Utils;

import Composestar.Core.RepositoryImplementation.RepositoryEntity;

/**
 * Summary description for Debug.
 */
public class Debug 
{
	private static int currentMode = 0;
	private static int warnings = 0;
    
	public static final int MODE_ERROR = 0;
	public static final int MODE_CRUCIAL = 1;
	public static final int MODE_WARNING = 2;
	public static final int MODE_INFORMATION = 3;
	public static final int MODE_DEBUG = 4;
    
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
			String modeDescription = "";
    	
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
					modeDescription = "error";
					break;
			}

			System.out.println(module+ '~' +modeDescription+ '~' +filename+ '~' +line+ '~' +msg);
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
    
	public static void outWarnings() 
	{
		if (warnings > 0)
			System.out.println("Warnings: " + warnings + '.');
	}
}
