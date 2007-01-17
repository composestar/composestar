package Composestar.Utils.Logging;

import java.util.HashMap;
import java.util.Map;

import Composestar.Utils.Debug;


/**
 * More or less a placeholder class for log4j style logging
 */
public class CPSLogger
{
	private static final Map loggers = new HashMap(); 
	
	private final String name;
	
	protected CPSLogger(String name)
	{
		this.name = name;
	}
	
	public void stackTrace(Throwable t)
	{
		Debug.out(Debug.MODE_DEBUG, name, Debug.stackTrace(t));
	}
	
	public void error(String msg)
	{
		Debug.out(Debug.MODE_ERROR, name, msg);
	}

	public void error(String msg, LocationProvider lp)
	{
		Debug.out(Debug.MODE_ERROR, name, msg, lp);
	}

	public void warn(String msg)
	{
		Debug.out(Debug.MODE_WARNING, name, msg);
	}

	public void warn(String msg, LocationProvider lp)
	{
		Debug.out(Debug.MODE_WARNING, name, msg, lp);
	}

	public void info(String msg)
	{
		Debug.out(Debug.MODE_INFORMATION, name, msg);
	}

	public void debug(String msg)
	{
		Debug.out(Debug.MODE_DEBUG, name, msg);
	}

	public static CPSLogger getCPSLogger(String name)
	{
		if (loggers.containsKey(name))
		{
			return (CPSLogger) loggers.get(name);
		}
		else
		{
			CPSLogger logger = new CPSLogger(name);
			loggers.put(name, logger);
			return logger;
		}
	}
}
