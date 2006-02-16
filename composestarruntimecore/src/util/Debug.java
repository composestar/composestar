package Composestar.RuntimeCore.Utils;

/**
 * Summary description for Debug.
 */
public class Debug 
{
	public static boolean SHOULD_DEBUG = true;
	public static boolean DEBUGGER_INTERFACE = false;

	private static int currentMode = 0;
    
	public static final int MODE_ERROR = Composestar.Utils.Debug.MODE_ERROR;             //0;
	public static final int MODE_CRUCIAL = Composestar.Utils.Debug.MODE_CRUCIAL;         //1;
	public static final int MODE_WARNING = Composestar.Utils.Debug.MODE_WARNING;         //2;
	public static final int MODE_INFORMATION = Composestar.Utils.Debug.MODE_INFORMATION; //3;
	public static final int MODE_DEBUG = Composestar.Utils.Debug.MODE_DEBUG;             //4;
    
	/**
	 * @param mode
	 * @roseuid 41161EA901B7
	 */
	public static void setMode(int mode) 
	{
		currentMode = mode;
		if (mode > 1) 
			Debug.SHOULD_DEBUG = true;
	}

	public static void setDebuggerInterface(boolean on)
	{
		Debug.DEBUGGER_INTERFACE = on;
	}
    
	/**
	 * @return int
	 * @roseuid 41161EA901B9
	 */
	public static int getMode() 
	{
		return currentMode;     
	}

	/**
	 * Print debug information to standard error output.
	 * 
	 * @param mode    Debug mode in which the information should be visible. 
	 * @param module  Module name where the message occurred.
	 * @param message Actual message to display.
	 */
	public static final void out(int mode, String module, String message) 
	{
		if (currentMode >= mode) 
		{
			String modeDescription = "";
    	
			switch(mode)
			{
				case MODE_ERROR:
					modeDescription = "error";
					break;

				case MODE_INFORMATION:
					modeDescription = "info";
					break;

				case MODE_DEBUG:
					modeDescription = "debug";
					break;

				case MODE_CRUCIAL:
					modeDescription = "crucial";
					break;
				
				case MODE_WARNING:
					modeDescription = "warning";
					break;

				default:
					modeDescription = "unknown";
					break;
			}
			System.err.println (module + " (" + modeDescription + "): " + message);
		}
	}

	//You can use this instead of System.out.printline while debugging and still be able to find it in debug mode 5
	public static final void programmerOut(String module, String message) 
	{
		System.err.println (module + ": " + message);
		try
		{
			System.in.read();
		}
		catch(Exception e)
		{
		}
	}
}
