/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Utils.Logging;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import Composestar.Core.RepositoryImplementation.RepositoryEntity;
import Composestar.Utils.Logging.Log4j.CPSLoggingEvent;

/**
 * The logger class you need to use for your module. To get a hold on a logger
 * for your module add the following code to the body the class where you want
 * to access the logger:
 * 
 * <pre>
 * protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);
 * </pre>
 * 
 * Then simply called the proper <em>error</em>, <em>warn</em>,
 * <em>info</em> and <em>debug</em> functions to add a log entry. To add
 * sourcefile information you will need to create an instance of LogMessage and
 * use that as the message argument for the former methods. Classes that are
 * also part of the runtime should create a ILogger through the SafeLogger
 * class:
 * 
 * <pre>
 * protected static final ILogger logger = SafeLogger.getILogger(MODULE_NAME);
 * </pre>
 * 
 * @author Michiel Hendriks
 */
public class CPSLogger extends Logger implements ILogger
{
	private static final Log4jLoggerFactory factory = new Log4jLoggerFactory();

	/**
	 * Get a CPSLogger with the given name
	 */
	public static CPSLogger getCPSLogger(String name)
	{
		return (CPSLogger) getLogger(name);
	}

	/**
	 * Get a plain Log4J Logger instance with the given name
	 */
	public static Logger getLogger(String name)
	{
		return Logger.getLogger(name, factory);
	}

	/**
	 * Trace log entry with a RepositoryEntity
	 */
	public void trace(Object message, RepositoryEntity re)
	{
		super.trace(new LogMessage(message, re));
	}

	/**
	 * Debug log entry with a RepositoryEntity
	 */
	public void debug(Object message, RepositoryEntity re)
	{
		super.debug(new LogMessage(message, re));
	}

	/**
	 * Error log entry with a RepositoryEntity
	 */
	public void error(Object message, RepositoryEntity re)
	{
		super.error(new LogMessage(message, re));
	}

	/**
	 * Fatal log entry with a RepositoryEntity
	 */
	public void fatal(Object message, RepositoryEntity re)
	{
		super.fatal(new LogMessage(message, re));
	}

	/**
	 * Information log entry with a RepositoryEntity
	 */
	public void info(Object message, RepositoryEntity re)
	{
		super.info(new LogMessage(message, re));
	}

	/**
	 * Warning log entry with a RepositoryEntity
	 */
	public void warn(Object message, RepositoryEntity re)
	{
		super.warn(new LogMessage(message, re));
	}

	/**
	 * A log entry with a RepositoryEntity
	 */
	public void log(Priority prio, Object message, RepositoryEntity re)
	{
		super.log(prio, new LogMessage(message, re));
	}

	protected CPSLogger(String name)
	{
		super(name);
	}

	@Override
	protected void forcedLog(String fqcn, Priority level, Object message, Throwable t)
	{
		callAppenders(new CPSLoggingEvent(fqcn, this, level, message, t));
	}

}
