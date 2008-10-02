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

import Composestar.Core.CpsRepository2.RepositoryEntity;

/**
 * A runtime safe logger instance, mimics a log4j logger without creating
 * dependencies to log4j.
 * 
 * @author Michiel Hendriks
 */
public class RuntimeLogger implements ILogger
{
	/**
	 * The identifier of the logger instance
	 */
	protected String name;

	public RuntimeLogger()
	{}

	/**
	 * Set the name of the logger
	 * 
	 * @param newName
	 */
	public void setName(String newName)
	{
		name = newName;
	}

	/**
	 * @return the logger name
	 */
	public String getName()
	{
		return name;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Utils.Logging.ILogger#debug(java.lang.Object,
	 * java.lang.Throwable)
	 */
	public void debug(Object arg0, Throwable arg1)
	{
		System.out.println("[" + name + "] DEBUG: " + arg0);
		if (arg1 != null)
		{
			arg1.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Utils.Logging.ILogger#debug(java.lang.Object)
	 */
	public void debug(Object arg0)
	{
		System.out.println("[" + name + "] DEBUG: " + arg0);
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Utils.Logging.ILogger#error(java.lang.Object,
	 * java.lang.Throwable)
	 */
	public void error(Object arg0, Throwable arg1)
	{
		System.out.println("[" + name + "] ERROR: " + arg0);
		if (arg1 != null)
		{
			arg1.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Utils.Logging.ILogger#error(java.lang.Object)
	 */
	public void error(Object arg0)
	{
		System.out.println("[" + name + "] ERROR: " + arg0);
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Utils.Logging.ILogger#fatal(java.lang.Object,
	 * java.lang.Throwable)
	 */
	public void fatal(Object arg0, Throwable arg1)
	{
		System.out.println("[" + name + "] FATAL: " + arg0);
		if (arg1 != null)
		{
			arg1.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Utils.Logging.ILogger#fatal(java.lang.Object)
	 */
	public void fatal(Object arg0)
	{
		System.out.println("[" + name + "] FATAL: " + arg0);
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Utils.Logging.ILogger#info(java.lang.Object,
	 * java.lang.Throwable)
	 */
	public void info(Object arg0, Throwable arg1)
	{
		System.out.println("[" + name + "] INFO: " + arg0);
		if (arg1 != null)
		{
			arg1.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Utils.Logging.ILogger#info(java.lang.Object)
	 */
	public void info(Object arg0)
	{
		System.out.println("[" + name + "] INFO: " + arg0);
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Utils.Logging.ILogger#warn(java.lang.Object,
	 * java.lang.Throwable)
	 */
	public void warn(Object arg0, Throwable arg1)
	{
		System.out.println("[" + name + "] WARN: " + arg0);
		if (arg1 != null)
		{
			arg1.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Utils.Logging.ILogger#warn(java.lang.Object)
	 */
	public void warn(Object arg0)
	{
		System.out.println("[" + name + "] WARN: " + arg0);
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Utils.Logging.ILogger#debug(java.lang.Object,
	 * Composestar.Core.RepositoryImplementation.RepositoryEntity)
	 */
	public void debug(Object arg0, RepositoryEntity arg1)
	{
		debug(arg0);
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Utils.Logging.ILogger#error(java.lang.Object,
	 * Composestar.Core.RepositoryImplementation.RepositoryEntity)
	 */
	public void error(Object arg0, RepositoryEntity arg1)
	{
		error(arg0);
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Utils.Logging.ILogger#fatal(java.lang.Object,
	 * Composestar.Core.RepositoryImplementation.RepositoryEntity)
	 */
	public void fatal(Object arg0, RepositoryEntity arg1)
	{
		fatal(arg0);
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Utils.Logging.ILogger#info(java.lang.Object,
	 * Composestar.Core.RepositoryImplementation.RepositoryEntity)
	 */
	public void info(Object arg0, RepositoryEntity arg1)
	{
		info(arg0);
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Utils.Logging.ILogger#warn(java.lang.Object,
	 * Composestar.Core.RepositoryImplementation.RepositoryEntity)
	 */
	public void warn(Object arg0, RepositoryEntity arg1)
	{
		warn(arg0);
	}
}
