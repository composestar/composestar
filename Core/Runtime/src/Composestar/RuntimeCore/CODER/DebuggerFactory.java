package Composestar.RuntimeCore.CODER;

import Composestar.RuntimeCore.FLIRT.Debugger.Debugger;
import Composestar.RuntimeCore.CODER.ConsoleDebugger.ConsolePrinterDebugger;
import Composestar.RuntimeCore.CODER.VisualDebugger.VisualDebugger;
import Composestar.RuntimeCore.CODER.VisualDebugger.CodeDebugger.CodeDebugger;
import Composestar.RuntimeCore.CODER.VisualDebugger.FilterVisualizer.FilterVisualizer;
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
				String debuggerRepresentation = getDebuggerType();
				debugger = getDebuggerClass(debuggerRepresentation);
			}
		}
	}

	private static Debugger getDebuggerClass(String type)
	{
		if("VisualDebugger".equalsIgnoreCase(type))
		{
			return new VisualDebugger(new CodeDebugger());
		}
		else if ("CodeDebugger".equalsIgnoreCase(type))
		{
			return new VisualDebugger(new FilterVisualizer());
		}
		else
		{
			return new ConsolePrinterDebugger();
		}
	}
	private static String getDebuggerType()
	{
		try
		{
			FileInputStream fstream = new FileInputStream(DEBUGGER_CONFIG_FILE);
			DataInputStream in = new DataInputStream(fstream);
			in.readLine();
			String line = in.readLine();
			int index = line.toUpperCase().indexOf("<DEBUGGER>");
			if(index < 0)
			{
				return "";
			}
			index += 10;
			int end = line.indexOf("/>");
			if(end <= index + 9)
			{
				return "";
			}
			return line.substring(index,end-9);
		}
		catch(Exception e)
		{
           e.printStackTrace();
		}
		return "";
	}
}
