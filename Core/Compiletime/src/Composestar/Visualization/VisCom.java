/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Visualization;

import java.io.File;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import Composestar.Core.Master.CompileHistory;
import Composestar.Utils.Logging.CPSLogger;

/**
 * VISualization of COMposestar Programs.
 * 
 * @author Michiel Hendriks
 */
public class VisCom
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger("VisCom");

	protected CompileHistory compileHistory;

	public VisCom()
	{
		initialize();
	}

	/**
	 * Initialize the environement. e.g. set up logging
	 */
	protected void initialize()
	{
		LogManager.resetConfiguration();
		Logger root = Logger.getRootLogger();
		root.addAppender(new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN),
				ConsoleAppender.SYSTEM_OUT));
	}

	/**
	 * Process the commandline arguments
	 * 
	 * @param args
	 */
	protected void processCmdArguments(String[] args)
	{
		int i = 0;
		while (i < args.length - 1)
		{
			// process ...
			i++;
		}

		if (i == args.length - 1)
		{
			// one more argument left, assume it's the compile history file
			openCompileHistory(new File(args[i]));
		}
	}

	protected boolean openCompileHistory(File histFile)
	{
		try
		{
			long startTime = System.currentTimeMillis();
			compileHistory = CompileHistory.load(histFile);
			logger.debug("loaded compile history from '" + histFile + "' in "
					+ (System.currentTimeMillis() - startTime) + "ms");
		}
		catch (Exception e)
		{
			logger.fatal("Unable to load history file from " + histFile, e);
			return false;
		}
		return true;
	}

	/**
	 * Start the stuff
	 */
	protected void run()
	{
		new Viewport(this);
	}

	public static void main(String[] args)
	{
		VisCom vs = new VisCom();
		vs.processCmdArguments(args);
		vs.run();
	}
}
