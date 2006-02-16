package Composestar.RuntimeCore.CODER;

import Composestar.RuntimeCore.CODER.ConsoleDebugger.ConsolePrinterDebugger;
import Composestar.RuntimeCore.CODER.VisualDebugger.VisualDebugger;
import Composestar.RuntimeCore.Utils.Debug;
import java.io.*;

/**
 * Summary description for DebuggerFactory.
 */
public class DebuggerFactory {
	private final static String DEBUGGER_CONFIG_FILE = "debugger.xml";

	/**
	 * Returns the debugger that is specified
	 */
    public static Debugger getDebugger(DebuggerProvider provider) {
		if(Debug.DEBUGGER_INTERFACE)
		{
			return new VisualDebugger(provider);
		}
		else
		{
			return new ConsolePrinterDebugger(provider);
		}
    }

	/**
	 */
	public static boolean checkDebugInterfaceSetting()
	{
		boolean result = false;
		try
		{
			File file = new File(DEBUGGER_CONFIG_FILE);
			result = file.exists(); //not using settings now. Just pressent is ok
		}
		catch(Exception e)
		{
			//Ignore
		}
		return result;
	}
}
