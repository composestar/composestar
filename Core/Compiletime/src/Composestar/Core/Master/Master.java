/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.Master;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.NullEnumeration;
import org.apache.log4j.net.SocketAppender;
import org.apache.log4j.varia.LevelRangeFilter;

import Composestar.Core.Config.BuildConfig;
import Composestar.Core.Config.ModuleInfoManager;
import Composestar.Core.Config.Xml.BuildConfigHandler;
import Composestar.Core.Config.Xml.PlatformConfigHandler;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.RepositoryImplementation.DataMap;
import Composestar.Core.RepositoryImplementation.DataMapImpl;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.Resources.PathResolver;
import Composestar.Utils.Version;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Logging.Log4j.CPSPatternLayout;
import Composestar.Utils.Logging.Log4j.CrucialLevel;
import Composestar.Utils.Logging.Log4j.MetricAppender;
import Composestar.Utils.Perf.CPSTimer;
import Composestar.Utils.Perf.Report.CPSTimerReport;
import Composestar.Utils.Perf.Report.CPSTimerTree;
import Composestar.Utils.Perf.Report.XMLTimerReport;

/**
 * Main entry point for the CompileTime. The Master class holds coreModules and
 * executes them in the order they are added.
 */
public abstract class Master
{
	public static final String MODULE_NAME = "Master";

	public static final String RESOURCE_CONFIGFILE = MODULE_NAME + ".Config";

	/**
	 * Compile process failed (e.g. a ModuleException was thrown)
	 */
	public static final int ECOMPILE = 1;

	/**
	 * Return code when no config file was found (or an exception was raised
	 * during parsing of the configuration)
	 */
	public static final int ECONFIG = 2;

	/**
	 * General execution failure
	 */
	public static final int EFAIL = 3;

	/**
	 * Errors were raised during compiling
	 */
	public static final int EERRORS = 4;

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	protected MetricAppender logMetrics = new MetricAppender();

	protected String configFilename;

	/**
	 * Platform configuration override. Set via the commandline arguments:
	 * -P=[file]
	 */
	protected File platformConfiguration;

	protected Map<String, String> settingsOverride;

	protected CommonResources resources;

	protected boolean debugOverride;

	public Master()
	{
		settingsOverride = new HashMap<String, String>();
		loggerSetup();
		initEvironment();
	}

	public Master(String[] args)
	{
		this();
		processCmdArgs(args);
	}

	/**
	 * Called to initialize certain evironment settings
	 */
	protected void initEvironment()
	{
		DataMap.setDataMapClass(DataMapImpl.class);
	}

	/**
	 * Initialize the logger environment. This is called from the constructor so
	 * the commandline arguments could still override the initial setup. This
	 * method can be used to override the default logging behavior in the
	 * platform masters.
	 */
	protected void loggerSetup()
	{
		// automatically done by log4j
		Logger root = Logger.getRootLogger();
		if (root.getAllAppenders() instanceof NullEnumeration)
		{
			URL propfile = Master.class.getResource("/log4j.properties");
			if (propfile != null)
			{
				PropertyConfigurator.configure(propfile);
			}
			else
			{
				// fallback in case log4j.properties could not be found
				// this produces the legacy output:
				// module~LEVEL~CPS File~CPS Line~message\n
				Layout layout = new CPSPatternLayout("%c~%p~%s~%S~%m%n");
				root.addAppender(new ConsoleAppender(layout, ConsoleAppender.SYSTEM_OUT));
			}
		}
		if (!Logger.getRootLogger().isAttached(logMetrics))
		{
			Logger.getRootLogger().addAppender(logMetrics);
		}

		String logLevel = System.getenv("LOG4J_LEVEL");
		if (logLevel != null)
		{
			setLogLevel(logLevel);
			logger.info("Log4j level override from environment");
			debugOverride = true;
		}
	}

	/**
	 * Convert a legacy debug mode to a Level
	 * 
	 * @param mode
	 * @return
	 */
	public static Level debugModeToLevel(int mode)
	{
		switch (mode)
		{
			case 0:
				return Level.ERROR;
			case 1:
				return CrucialLevel.CRUCIAL;
			case 2:
				return Level.WARN;
			case 3:
				return Level.INFO;
			case 4:
				return Level.DEBUG;
			case 5:
				return Level.TRACE;
			default:
				return Level.WARN;
		}
	}

	/**
	 * Set the current logging level
	 * 
	 * @param level
	 */
	public static void setLogLevel(String level)
	{
		if (level == null || level.trim().length() == 0)
		{
			return;
		}
		Level lvl = null;
		try
		{
			int nlevel = Integer.parseInt(level.trim());
			if (nlevel < 6)
			{
				lvl = debugModeToLevel(nlevel);
			}
			else
			{
				lvl = Level.toLevel(nlevel, null);
			}
		}
		catch (NumberFormatException e)
		{
			lvl = Level.toLevel(level.trim(), null);
		}
		if (lvl == null)
		{
			return;
		}
		Logger.getRootLogger().setLevel(lvl);
	}

	/**
	 * Processes the commandline argument
	 * 
	 * @param args
	 */
	public void processCmdArgs(String[] args)
	{
		for (String arg : args)
		{
			if (arg.startsWith("-d"))
			{
				setLogLevel(arg.substring(2));
				debugOverride = true;
			}
			else if (arg.startsWith("-t"))
			{
				int et = Integer.parseInt(arg.substring(2));

				Logger root = Logger.getRootLogger();
				ConsoleAppender errAppender = new ConsoleAppender(new CPSPatternLayout("[%c] %p: %m%n"),
						ConsoleAppender.SYSTEM_ERR);
				LevelRangeFilter rangeFilter = new LevelRangeFilter();
				rangeFilter.setLevelMax(Level.FATAL);
				rangeFilter.setLevelMin(debugModeToLevel(et));
				errAppender.addFilter(rangeFilter);
				root.addAppender(errAppender);
			}
			else if (arg.startsWith("-D"))
			{
				// -D<module>.<setting>=<value>
				String[] setting = arg.substring(2).split("\\.|=", 3);
				if (setting.length != 3)
				{
					System.err.println("Correct format is: -D<module>.<setting>=<value>");
					continue;
				}
				settingsOverride.put(setting[0] + "." + setting[1], setting[3]);
			}
			else if (arg.startsWith("-L="))
			{
				// override Log4J configuration, must be before a -d setting
				// -L=<filename>
				arg = arg.substring(3);
				LogManager.resetConfiguration();
				PropertyConfigurator.configure(arg);
				if (!Logger.getRootLogger().isAttached(logMetrics))
				{
					Logger.getRootLogger().addAppender(logMetrics);
				}
			}
			else if (arg.startsWith("-P="))
			{
				// override the platform configuration
				arg = arg.substring(3);
				platformConfiguration = new File(arg);
			}
			else if (arg.startsWith("-LN"))
			{
				// log to net
				String host = "127.0.0.1";
				int port = 4445;
				if (arg.startsWith("-LN="))
				{
					String[] hp = arg.substring(4).split(":");
					if (hp.length > 1)
					{
						host = hp[0];
					}
					if (hp.length > 2)
					{
						try
						{
							port = Integer.parseInt(hp[1]);
						}
						catch (NumberFormatException nfe)
						{
							System.err.println(String.format("%s is not a valid host:port", arg.substring(4)));
							port = 4445;
						}
					}
				}
				Logger.getRootLogger().addAppender(new SocketAppender(host, port));
			}
			else if (arg.startsWith("-"))
			{
				System.err.println("Unknown option " + arg);
			}
			else
			{
				// assume it's the config file
				configFilename = arg;
			}
		}
	}

	/**
	 * Loads the platform configuration. The platform configuration is loaded
	 * from three locations, in the following order:
	 * <ol>
	 * <li> file provided through via a commandline argument </li>
	 * <li> Platforms.xml in the compose* base directory </li>
	 * <li> Platforms.xml provided in the port's Jar file </li>
	 * </ol>
	 * When non of these files can be loaded a fatal exception will be raised.
	 */
	protected void loadPlatform() throws Exception
	{
		if (platformConfiguration != null)
		{
			if (platformConfiguration.exists())
			{
				logger.info("Loading custom platform config from: " + platformConfiguration.toString());
				try
				{
					PlatformConfigHandler.loadPlatformConfig(platformConfiguration);
					return;
				}
				catch (Exception e)
				{
					logger.error(e);
				}
			}
			else
			{
				logger.error("Platform configuration file does not exist: " + platformConfiguration.toString());
			}
		}

		// try <portdir>/Platforms.xml
		// jar file is in <portdir>/lib/
		File base = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
		platformConfiguration = new File(base.getParentFile().getParentFile(), "Platforms.xml");
		if (platformConfiguration.exists())
		{
			logger.info("Loading platform config from: " + platformConfiguration.toString());
			try
			{
				PlatformConfigHandler.loadPlatformConfig(platformConfiguration);
				return;
			}
			catch (Exception e)
			{
				logger.error(e);
			}
		}

		// use Platforms.xml in the port's jar file
		InputStream is = getClass().getResourceAsStream("/Platforms.xml");
		if (is != null)
		{
			logger.info("Loading internal platform config");
			PlatformConfigHandler.loadPlatformConfig(is);
			return;
		}
		throw new Exception("Unable to find the platform configuration.");
	}

	/**
	 * Will be called retrieve the path resolved.
	 * 
	 * @return
	 */
	protected PathResolver getPathResolver()
	{
		return new PathResolver(getClass());
	}

	/**
	 * Load the current project configuration and initialize common resources
	 * 
	 * @throws Exception
	 */
	protected void loadConfiguration() throws Exception
	{
		File configFile = new File(configFilename);
		if (!configFile.canRead())
		{
			throw new Exception("Unable to open configuration file: '" + configFile.toString() + "'");
		}

		// create the repository and common resources
		DataStore.instance();
		resources = new CommonResources();
		resources.put(RESOURCE_CONFIGFILE, configFilename);
		BuildConfig config = BuildConfigHandler.loadBuildConfig(configFile);
		resources.setConfiguration(config);
		resources.setPathResolver(getPathResolver());
		if (!debugOverride)
		{
			setLogLevel(config.getSetting("buildDebugLevel"));
		}
		ModuleInfoManager.getInstance().setBuildConfig(config);

		for (Entry<String, String> override : settingsOverride.entrySet())
		{
			config.addSetting(override.getKey(), override.getValue());
		}
	}

	/**
	 * Called just before building and after initialization
	 */
	protected void preBuild() throws Exception
	{}

	/**
	 * Called after building is complete
	 */
	protected void postBuild() throws Exception
	{}

	/**
	 * Calls run on all modules added to the master.
	 */
	public int run()
	{
		try
		{
			CPSTimer timer = CPSTimer.getTimer(MODULE_NAME, "Main process");

			preBuild();

			// initialize INCRE
			INCRE incre = new INCRE();
			incre.init(resources);

			// execute enabled modules one by one
			incre.runModules(resources);

			postBuild();

			// display total time elapsed
			timer.stop();
			logger.debug("Total time: " + (timer.getLastEvent().getDuration() / 1000000) + " ms");

			createTimerReport();

			// display number of warnings
			if ((logMetrics.numWarnings() > 0) || (logMetrics.numErrors() > 0) || (logMetrics.numFatals() > 0))
			{
				// TODO: shouldn't print to stdout or anywhere else for that
				// matter
				System.out.println("Warnings: " + logMetrics.numWarnings() + "; Errors: " + logMetrics.numErrors());
			}

			if (logMetrics.numErrors() > 0)
			{
				// Debug.outWarnings();
				return EERRORS;
			}
		}
		catch (ModuleException e)
		{
			CPSLogger mLogger = CPSLogger.getCPSLogger(e.getModule());
			mLogger.error(e, e);
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			mLogger.debug(sw.toString());
			return ECOMPILE;
		}
		catch (Exception e)
		{
			logger.error("Internal compiler error: " + e, e);
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			logger.debug(sw.toString());
			return EFAIL;
		}
		return 0;
	}

	/**
	 * 
	 */
	protected void createTimerReport()
	{
		File timerResult = new File(resources.configuration().getProject().getIntermediate(), "Analyses");
		if (!timerResult.exists())
		{
			if (!timerResult.mkdirs())
			{
				logger.warn(String.format("Unable to create report directory: %s", timerResult));
				return;
			}
		}
		if (!timerResult.isDirectory())
		{
			logger.warn(String.format("Report output location is not a directory: %s", timerResult));
			return;
		}
		if (timerResult.canWrite())
		{
			timerResult = new File(timerResult, "TimerResults.xml");
			FileOutputStream trOs;
			try
			{
				trOs = new FileOutputStream(timerResult);
			}
			catch (FileNotFoundException e)
			{
				logger.warn(String.format("Error writing timer report: %s", e));
				return;
			}
			String styleSheet = "TimerResults.xslt";
			FileOutputStream stOs;
			try
			{
				stOs = new FileOutputStream(new File(timerResult.getParent(), styleSheet));
			}
			catch (FileNotFoundException e)
			{
				logger.warn(String.format("Error writing timer report: %s", e));
				return;
			}
			CPSTimerReport report = new XMLTimerReport(trOs, stOs, styleSheet);
			report.generateReport(CPSTimerTree.constructTree(CPSTimer.getTimers()));
		}
	}

	protected static void main(Class<? extends Master> masterClass, String[] args)
	{
		if (args.length == 0)
		{
			System.err.println("No build configuration provided");
			System.exit(EFAIL);
			return;
		}

		String arg = args[0].toLowerCase();
		if (arg.equals("-v") || arg.equals("--version"))
		{
			Version.reportVersion(System.out);
			return;
		}

		Master master;
		try
		{
			master = masterClass.newInstance();
		}
		catch (Exception e)
		{
			System.err.println("Failed to initialize master: " + e.getMessage());
			e.printStackTrace();
			System.exit(EFAIL);
			return;
		}
		master.processCmdArgs(args);
		try
		{
			master.loadPlatform();
			master.loadConfiguration();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(ECONFIG);
			return;
		}

		logger.info(Version.getTitle() + " " + Version.getVersionString());
		logger.debug("Compiled on " + Version.getCompileDate().toString());

		int ret = master.run();
		if (ret != 0)
		{
			System.exit(ret);
		}
	}
}
