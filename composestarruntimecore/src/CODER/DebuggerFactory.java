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
	private final static String DEFAULT_DEBUGGER = "Composestar.RuntimeCore.CODER.VisualDebugger.CodeDebugger.CodeDebugger";;
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

	public static String getDebuggerClass(String type)
	{
		if("VisualDebugger".equalsIgnoreCase(type))
		{
			return "Composestar.RuntimeCore.CODER.VisualDebugger.FilterVisualizer.FilterVisualizer";
		}
		else
		{
			return DEFAULT_DEBUGGER;
		}
	}

	public static String getDebugger()
	{
		try
		{
			FileInputStream fstream = new FileInputStream(DEBUGGER_CONFIG_FILE);
			DataInputStream in = new DataInputStream(fstream);
			String line = in.readLine();
			line = in.readLine();
			int index = line.toUpperCase().indexOf("<DEBUGGER>");
			if(index < 0)
			{
				return DEFAULT_DEBUGGER;
			}
			index += 10;
			int end = line.indexOf("/>");
			if(end <= index + 9)
			{
				return DEFAULT_DEBUGGER;
			}
			return getDebuggerClass(line.substring(index,end-9));
		}
		catch(Exception e)
		{

		}
		return DEFAULT_DEBUGGER;
	}
}
