/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.DotNET.MASTER;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.Module;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Master;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.XmlHandlers.BuildConfigHandler;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;
import Composestar.Utils.Version;

/**
 * Main entry point for the CompileTime. The Master class holds coreModules and
 * executes them in the order they are added.
 */
public class DotNETMaster extends Master
{
	private CommonResources resources;

	private String configfile;

	/**
	 * Default ctor.
	 */
	public DotNETMaster(String configurationFile) throws ModuleException
	{
		configfile = configurationFile;

		// create the repository
		DataStore ds = DataStore.instance();

		resources = new CommonResources();
		ds.addObject(RESOURCES_KEY, resources);

		// load the project configuration file
		try
		{
			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Reading build configuration from: " + configurationFile);

			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLReader parser = saxParser.getXMLReader();

			BuildConfigHandler handler = new BuildConfigHandler(parser);
			parser.setContentHandler(handler);
			parser.parse(new InputSource(configurationFile));
		}
		catch (Exception e)
		{
			throw new ModuleException("An error occured while reading the build configuration file: "
					+ configurationFile + ", reason: " + e.getMessage(), MODULE_NAME);
		}

		// Set debug level
		Debug.setMode(Configuration.instance().getBuildDebugLevel());
	}

	/**
	 * Calls run on all modules added to the master.
	 */
	public void run()
	{
		// This is the 'hardcoded' version
		try
		{
			//Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Composestar compile-time " + Version.getVersionString());

			// Apache XML driver is moved to a different package in Java 5
			if (System.getProperty("java.version").substring(0, 3).equals("1.5"))
			{
				System.setProperty("org.xml.sax.driver", "com.sun.org.apache.xerces.internal.parsers.SAXParser");
				Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Selecting SAXParser XML SAX Driver");
			}
			else
			{
				System.setProperty("org.xml.sax.driver", "org.apache.crimson.parser.XMLReaderImpl");
				Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Selecting XMLReaderImpl XML SAX Driver");
			}

			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Creating datastore...");
			DataStore.instance();

			// initialize INCRE
			INCRE incre = INCRE.instance();
			incre.run(resources);

			Iterator modulesIter = incre.getModules();
			while (modulesIter.hasNext())
			{
				// execute enabled modules one by one
				Module m = (Module) modulesIter.next();
				m.execute(resources);
			}

			incre.getReporter().close();
			if (Debug.getMode() >= Debug.MODE_WARNING)
			{
				Debug.outWarnings();
			}
		}
		catch (ModuleException e)
		{ // MasterStopException
			String error = e.getMessage();
			if (error == null || "null".equals(error))
			{
				error = e.toString();
			}

			if ((e.getErrorLocationFilename() != null) && !e.getErrorLocationFilename().equals(""))
			{
				Debug.out(Debug.MODE_ERROR, e.getModule(), error, e.getErrorLocationFilename(), e
						.getErrorLocationLineNumber());
			}
			else
			{
				Debug.out(Debug.MODE_ERROR, e.getModule(), error);
			}

			Debug.out(Debug.MODE_DEBUG, e.getModule(), "StackTrace: " + Debug.stackTrace(e));
			System.exit(1);
		}
		catch (Exception e)
		{
			String error = e.getMessage();
			if (error == null || "null".equals(error))
			{
				error = e.toString();
			}

			Debug.out(Debug.MODE_ERROR, MODULE_NAME, "Internal compiler error: " + error);
			Debug.out(Debug.MODE_ERROR, MODULE_NAME, "StackTrace: " + Debug.stackTrace(e));
			System.exit(2);
		}
	}

	// not used
	public void saveModifiedConfigurationKeys(CommonResources resources)
	{
		List builtAssemblies = (List) resources.getResource("BuiltAssemblies");
		List configLines = new ArrayList();

		try
		{
			BufferedReader br = new BufferedReader(new FileReader(configfile));
			String line = br.readLine();
			while (line != null)
			{
				if (line.startsWith("BuiltAssemblies="))
				{
					line = "BuiltAssemblies=" + builtAssemblies.size();
					configLines.add(line);
					for (int i = 0; i < builtAssemblies.size(); i++)
					{
						Object temp = builtAssemblies.get(i);
						if (temp != null)
						{
							configLines.add("BuiltAssembly" + i + "=" + temp.toString());
						}
					}
				}
				else
				{
					configLines.add(line);
				}

				line = br.readLine();
			}
			br.close();

			BufferedWriter bw = new BufferedWriter(new FileWriter(configfile));
			Iterator iterLines = configLines.iterator();
			while (iterLines.hasNext())
			{
				line = (String) iterLines.next();
				bw.write(line + "\n");
			}
			bw.close();
		}
		catch (IOException e)
		{
			Debug.out(Debug.MODE_WARNING, MODULE_NAME, "Unable to update configuration file '" + configfile + "'!");
		}
	}

	/**
	 * Compose* main function. Creates the Master object. Adds the desired
	 * modules and then calls run on each of them in the order that they where
	 * added.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		if (args.length == 0)
		{
			System.out.println("Usage: java -jar ComposestarDotNET.jar <config file>");
			return;
		}

		if (args[0].equalsIgnoreCase("-V") || args[0].equalsIgnoreCase("--version"))
		{
			Version.reportVersion(System.out);
			return;
		}

		try
		{
			Master master = new DotNETMaster(args[0]);
			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, Version.getTitle() + " " + Version.getVersionString());
			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Compiled on "+Version.getCompileDate().toString());
			master.run();
		}
		catch (ModuleException e)
		{
			System.out.println("Could not open configuration file '" + args[0] + "': " + e.getMessage());
			System.exit(-1); // FIXME: are these errorlevels random?
		}
	}
}
