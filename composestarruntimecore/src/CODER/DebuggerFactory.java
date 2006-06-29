package Composestar.RuntimeCore.CODER;

import Composestar.RuntimeCore.FLIRT.Debugger.Debugger;
import Composestar.RuntimeCore.CODER.ConsoleDebugger.ConsolePrinterDebugger;
import Composestar.RuntimeCore.CODER.VisualDebugger.VisualDebugger;
import Composestar.RuntimeCore.Utils.Debug;
import java.io.*;

/**
 * Summary description for DebuggerFactory.
 */
public class DebuggerFactory {
	private static String DEBUGGER_CONFIG_FILE = "debugger.xml";
	private static String DEFAULT_DEBUGGER = "Composestar.RuntimeCore.CODER.VisualDebugger.CodeDebugger.CodeDebugger";;
	/**
	 * Returns the debugger that is specified
	 */
	private static Debugger debugger = null;

    public final static Debugger getDebugger() {
		if(debugger == null) //First selection
		{
			instantiateDebugger();
		}
		return debugger;
    }

	private final static void instantiateDebugger()
	{
		synchronized(DEBUGGER_CONFIG_FILE)
		{
			if(debugger == null) //Because of race condition with singleton
			{
				if(Debug.DEBUGGER_INTERFACE)
				{
					String debuggerRepresentation = getDebuggerType();
					debugger = new VisualDebugger(debuggerRepresentation);
				}
				else
				{
					debugger = new ConsolePrinterDebugger();
				}
			}
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

	private static String getDebuggerClass(String type)
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
	private static String getDebuggerType()
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
