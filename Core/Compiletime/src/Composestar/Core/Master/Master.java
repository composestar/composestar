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

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.XmlHandlers.BuildConfigHandler;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;


/**
 * Main entry point for the CompileTime. The Master class holds coreModules and
 * executes them in the order they are added.
 */
public abstract class Master
{
	public static final String MODULE_NAME = "Master";
	
	public static final String RESOURCES_KEY = "Composestar.Core.Master.CommonResources";
	
	/**
	 * Compile process failed (e.g. a ModuleException was thrown)
	 */
	public static final int ECOMPILE = 1;
	
	/**
	 * Return code when no config file was found
	 */
	public static final int ECONFIG = 2;
	
	/**
	 * General execution failure
	 */
	public static final int EFAIL = 3;
	
	protected String configfile;
	
	protected int debugOverride = -1;
	
	protected CommonResources resources;
	
	public Master(String[] args)
	{
		processCmdArgs(args);
	}
	
	public void processCmdArgs(String[] args)
	{
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].startsWith("-d"))
			{
				debugOverride = Integer.parseInt(args[i].substring(2));
				Debug.setMode(debugOverride);
				continue;
			}
			// assume it's the last argument
			else {
				configfile = args[i];
				break;
			}
		}
	}
	
	public void loadConfiguration() throws Exception
	{
		File cfgFile = new File(configfile);
		if (!cfgFile.canRead())
		{
			throw new Exception("Unable to open configuration file: "+configfile);
		}

		// create the repository
		DataStore ds = DataStore.instance();

		resources = new CommonResources();
		ds.addObject(RESOURCES_KEY, resources);

		// load the project configuration file
		try
		{
			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Reading build configuration from: " + configfile);

			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLReader parser = saxParser.getXMLReader();

			BuildConfigHandler handler = new BuildConfigHandler(parser);
			parser.setContentHandler(handler);
			parser.parse(new InputSource(configfile));
		}
		catch (Exception e)
		{
			throw new Exception("An error occured while reading the build configuration file: "
					+ configfile + ", reason: " + e.getMessage());
		}

		// Set debug level
		if (debugOverride == -1)
		{
			Debug.setMode(Configuration.instance().getBuildDebugLevel());
		}
	}

	public abstract void run();
}
