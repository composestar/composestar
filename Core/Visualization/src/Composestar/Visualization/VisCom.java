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
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import Composestar.Core.Master.CompileHistory;
import Composestar.Core.RepositoryImplementation.DataMap;
import Composestar.Core.RepositoryImplementation.DataMapImpl;
import Composestar.Utils.FileUtils;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Visualization.Model.ViewManager;
import Composestar.Visualization.UI.Viewport;

/**
 * VISualization of COMposestar Programs.
 * 
 * @author Michiel Hendriks
 */
public class VisCom
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger("VisCom");

	protected CompileHistory compileHistory;

	protected ViewManager viewManager;

	/**
	 * FilterViews to open
	 */
	public List<String> openFilterViews;

	/**
	 * FilterActionViews to open
	 */
	public List<String> openFilterActionViews;

	public VisCom()
	{
		openFilterViews = new ArrayList<String>();
		openFilterActionViews = new ArrayList<String>();
		DataMap.setDataMapClass(DataMapImpl.class);
		initialize();
		logger.info("Starting Compose* Visualizer");
	}

	/**
	 * Dispose the current compile history and load a new one.
	 * 
	 * @param histFile
	 * @return
	 */
	public boolean openCompileHistory(File histFile)
	{
		try
		{
			viewManager = null;
			long startTime = System.currentTimeMillis();

			InputStream is = new FileInputStream(histFile);
			if (FileUtils.getExtension(histFile.getName()).equalsIgnoreCase(CompileHistory.EXT_COMPRESSED))
			{
				is = new GZIPInputStream(is);
			}
			compileHistory = CompileHistory.load(new VisComObjectInputStream(is));
			logger.debug("loaded compile history from '" + histFile + "' in "
					+ (System.currentTimeMillis() - startTime) + "ms");
			viewManager = new ViewManager(compileHistory);
		}
		catch (Exception e)
		{
			logger.fatal("Unable to load history file from " + histFile, e);
			return false;
		}
		return true;
	}

	/**
	 * Get the current view manager
	 * 
	 * @return
	 */
	public ViewManager getViewManager()
	{
		return viewManager;
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
	 * Process the commandline arguments. Returns the CompileHistory file to
	 * open, if any.
	 * 
	 * @param args
	 */
	protected File processCmdArguments(String[] args)
	{
		int i = 0;
		while (i < args.length - 1)
		{
			if (args[i].equals("-fv"))
			{
				i++;
				openFilterViews.add(args[i]);
			}
			else if (args[i].equals("-fav"))
			{
				i++;
				openFilterActionViews.add(args[i]);
			}
			i++;
		}

		if (i == args.length - 1)
		{
			// one more argument left, assume it's the compile history file
			return new File(args[i]);
		}
		return null;
	}

	/**
	 * Start the stuff
	 */
	protected void run(File historyFile)
	{
		new Viewport(this, historyFile);
	}

	public static void main(String[] args)
	{
		VisCom vs = new VisCom();
		vs.run(vs.processCmdArguments(args));
	}
}
