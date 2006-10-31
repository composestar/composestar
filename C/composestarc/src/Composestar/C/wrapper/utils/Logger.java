package Composestar.C.wrapper.utils;

import java.io.*;

public class Logger
{
	private Logger logger = null;
	private PrintStream ps = null;
	
	private Logger(String file)
	{
		try
		{
			ps = new PrintStream(new FileOutputStream(file));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static Logger init(String file)
	{
		return null;
	}
	
	public static Logger instance()
	{
		return null;
	}
}
