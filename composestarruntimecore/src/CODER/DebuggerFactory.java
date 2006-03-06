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
			String debugger = getDebugger();
			return new VisualDebugger(provider,debugger);
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

	public static String getDebugger()
	{
		final String fail = "Composestar.RuntimeCore.CODER.VisualDebugger.FilterVisualizer.FilterVisualizer";
		try
		{
			FileInputStream fstream = new FileInputStream(DEBUGGER_CONFIG_FILE);
			DataInputStream in = new DataInputStream(fstream);
			String line = in.readLine();
			line = in.readLine();
			int index = line.toUpperCase().indexOf("<DEBUGGER>");
			if(index < 0)
			{
				return fail;
			}
			index += 10;
			int end = line.indexOf("/>");
			if(end <= index + 9)
			{
				return fail;
			}
			return line.substring(index,end-9);
		}
		catch(Exception e)
		{

		}
		return fail;
	}
}
