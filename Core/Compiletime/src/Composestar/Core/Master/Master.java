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
import java.io.InputStream;
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
import org.apache.log4j.net.SocketAppender;
import org.apache.log4j.varia.LevelRangeFilter;

import Composestar.Core.Config.ModuleInfo;
import Composestar.Core.Config.ModuleInfoManager;
import Composestar.Core.Config.Xml.BuildConfigHandler;
import Composestar.Core.Config.Xml.PlatformConfigHandler;
import Composestar.Core.Exception.ConfigurationException;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.RepositoryImplementation.DataMap;
import Composestar.Core.RepositoryImplementation.DataMapImpl;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;
import Composestar.Utils.Version;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Logging.Log4j.CPSPatternLayout;
import Composestar.Utils.Logging.Log4j.MetricAppender;

/**
 * Main entry point for the CompileTime. The Master class holds coreModules and
 * executes them in the order they are added.
 */
public abstract class Master
{
	public static final String MODULE_NAME = "Master";

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

	protected int debugOverride = -1;

	protected Map<String, Map<String, String>> settingsOverride;

	protected CommonResources resources;

	public Master()
	{
		settingsOverride = new HashMap<String, Map<String, String>>();
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
		URL propfile = Master.class.getResource("/log4j.properties");
		if (propfile != null)
		{
			PropertyConfigurator.configure(propfile);
		}
		else
		{
			// fallback in case log4j.properties could not be found
			Logger root = Logger.getRootLogger();
			// this produces the legacy output:
			// module~LEVEL~CPS File~CPS Line~message\n
			Layout layout = new CPSPatternLayout("%c~%p~%s~%S~%m%n");
			root.addAppender(new ConsoleAppender(layout, ConsoleAppender.SYSTEM_OUT));
		}
		Logger.getRootLogger().addAppender(logMetrics);

		String logLevel = System.getenv("LOG4J_LEVEL");
		if (logLevel != null)
		{
			Logger.getRootLogger().setLevel(Level.toLevel(logLevel));
			logger.info("Log4j level override from environment");
		}
	}

	public void processCmdArgs(String[] args)
	{
		for (String arg : args)
		{
			if (arg.startsWith("-d"))
			{
				debugOverride = Integer.parseInt(arg.substring(2));
				Debug.setMode(debugOverride);
			}
			else if (arg.startsWith("-t"))
			{
				int et = Integer.parseInt(arg.substring(2));

				Logger root = Logger.getRootLogger();
				ConsoleAppender errAppender = new ConsoleAppender(new CPSPatternLayout("[%c] %p: %m%n"),
						ConsoleAppender.SYSTEM_ERR);
				LevelRangeFilter rangeFilter = new LevelRangeFilter();
				rangeFilter.setLevelMax(Level.FATAL);
				rangeFilter.setLevelMin(Debug.debugModeToLevel(et));
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
				Map<String, String> ms = settingsOverride.get(setting[0]);
				if (ms == null)
				{
					ms = new HashMap<String, String>();
					settingsOverride.put(setting[0], ms);
				}
				ms.put(setting[1], setting[2]);
			}
			else if (arg.startsWith("-L="))
			{
				// override Log4J configuration, must be before a -d setting
				// -L=<filename>
				arg = arg.substring(3);
				LogManager.resetConfiguration();
				PropertyConfigurator.configure(arg);
				Logger.getRootLogger().addAppender(logMetrics);
			}
			else if (arg.startsWith("-P="))
			{
				// override the platform configuration
				arg = arg.substring(3);
				platformConfiguration = new File(arg);
			}
			else if (arg.equals("-LN"))
			{
				// log to net
				Logger.getRootLogger().addAppender(new SocketAppender("127.0.0.1", 4445));
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
		resources.setConfiguration(BuildConfigHandler.loadBuildConfig(configFile));
		resources.setPathResolver(getPathResolver());
		ModuleInfoManager.getInstance().setBuildConfig(resources.configuration());

		/*
		 * // load the project configuration file try { logger.debug("Reading
		 * build configuration from: " + configFilename); SAXParserFactory
		 * saxParserFactory = SAXParserFactory.newInstance(); SAXParser
		 * saxParser = saxParserFactory.newSAXParser(); XMLReader parser =
		 * saxParser.getXMLReader(); BuildConfigHandler handler = new
		 * BuildConfigHandler(parser); parser.setContentHandler(handler);
		 * parser.parse(new InputSource(configFilename)); } catch (Exception e) {
		 * throw new Exception("An error occured while reading the build
		 * configuration file '" + configFilename + "': " + e.getMessage()); } //
		 * Set debug level if (debugOverride == -1) {
		 * Debug.setMode(Configuration.instance().getBuildDebugLevel()); }
		 */
	}

	/**
	 * Calls run on all modules added to the master.
	 */
	public int run()
	{
		try
		{
			long beginTime = System.currentTimeMillis();

			ModuleInfo mi = ModuleInfoManager.get(INCRE.class);
			Map<String, String> overrideSet = settingsOverride.get(INCRE.MODULE_NAME);
			if ((overrideSet != null) && (mi != null))
			{
				for (Entry<String, String> entry : overrideSet.entrySet())
				{
					try
					{
						mi.setSettingValue(entry.getKey(), entry.getValue());
					}
					catch (ConfigurationException ce)
					{
						CPSLogger log = CPSLogger.getCPSLogger(INCRE.MODULE_NAME);
						log.error("Configuration override error: " + ce.getMessage(), ce);
					}
				}
			}

			// initialize INCRE
			INCRE incre = INCRE.instance();
			incre.init(resources);

			// load override settings
			for (Entry<String, Map<String, String>> entry : settingsOverride.entrySet())
			{
				if (entry.getKey().equals(INCRE.MODULE_NAME))
				{
					continue;
				}
				mi = ModuleInfoManager.get(entry.getKey());
				if (mi != null)
				{
					for (Entry<String, String> se : entry.getValue().entrySet())
					{
						try
						{
							mi.setSettingValue(se.getKey(), se.getValue());
						}
						catch (ConfigurationException ce)
						{
							CPSLogger log = CPSLogger.getCPSLogger(entry.getKey());
							log.error("Configuration override error: " + ce.getMessage(), ce);
						}
					}
				}
				else
				{
					logger.error("Configuration override error: no such module: " + entry.getKey());
				}
			}

			// execute enabled modules one by one
			incre.runModules(resources);

			// close the incre reporter
			incre.getReporter().close();

			// display total time elapsed
			long total = System.currentTimeMillis() - beginTime;
			logger.debug("Total time: " + total + "ms");

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

			// TODO: shouldn't use stack trace
			Debug.out(Debug.MODE_DEBUG, e.getModule(), "StackTrace: " + Debug.stackTrace(e));
			return ECOMPILE;
		}
		catch (Exception e)
		{
			logger.error("Internal compiler error: " + e, e);

			// TODO: shouldn't use stack trace
			Debug.out(Debug.MODE_ERROR, MODULE_NAME, "StackTrace: " + Debug.stackTrace(e));
			return EFAIL;
		}
		return 0;
	}

	protected static void main(Class masterClass, String[] args)
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
			master = (Master) masterClass.newInstance();
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
