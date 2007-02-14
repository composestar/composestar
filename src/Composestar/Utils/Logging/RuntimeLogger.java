/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Utils.Logging;

/**
 * A runtime safe logger instance, mimics a log4j logger without creating
 * dependencies to log4j.
 * 
 * @author Michiel Hendriks
 */
public class RuntimeLogger implements ILogger
{
	protected String name;

	public RuntimeLogger()
	{}

	public void setName(String newName)
	{
		name = newName;
	}

	public String getName()
	{
		return name;
	}

	public void debug(Object arg0, Throwable arg1)
	{
		System.out.println("[" + name + "] DEBUG: " + arg0);
		if (arg1 != null)
		{
			arg1.printStackTrace();
		}
	}

	public void debug(Object arg0)
	{
		System.out.println("[" + name + "] DEBUG: " + arg0);
	}

	public void error(Object arg0, Throwable arg1)
	{
		System.out.println("[" + name + "] ERROR: " + arg0);
		if (arg1 != null)
		{
			arg1.printStackTrace();
		}
	}

	public void error(Object arg0)
	{
		System.out.println("[" + name + "] ERROR: " + arg0);
	}

	public void fatal(Object arg0, Throwable arg1)
	{
		System.out.println("[" + name + "] FATAL: " + arg0);
		if (arg1 != null)
		{
			arg1.printStackTrace();
		}
	}

	public void fatal(Object arg0)
	{
		System.out.println("[" + name + "] FATAL: " + arg0);
	}

	public void info(Object arg0, Throwable arg1)
	{
		System.out.println("[" + name + "] INFO: " + arg0);
		if (arg1 != null)
		{
			arg1.printStackTrace();
		}
	}

	public void info(Object arg0)
	{
		System.out.println("[" + name + "] INFO: " + arg0);
	}

	public void warn(Object arg0, Throwable arg1)
	{
		System.out.println("[" + name + "] WARN: " + arg0);
		if (arg1 != null)
		{
			arg1.printStackTrace();
		}
	}

	public void warn(Object arg0)
	{
		System.out.println("[" + name + "] WARN: " + arg0);
	}
}
