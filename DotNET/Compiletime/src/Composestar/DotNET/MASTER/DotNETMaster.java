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
import java.util.Properties;

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
import Composestar.Core.Master.Config.XmlHandlers.BuildXMLHandler;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;
import Composestar.Utils.Version;

/**
 * Main entry point for the CompileTime. The Master class holds coreModules
 * and executes them in the order they are added.
 */
public class DotNETMaster extends Master
{
	public static final String RESOURCES_KEY = "Composestar.Core.Master.CommonResources";

	private CommonResources resources;
	private String configfile;

	/**
	 * Default ctor.
	 */
	public DotNETMaster(String configurationFile) throws ModuleException
	{
		configfile = configurationFile;
		Debug.setMode(3);
		resources = new CommonResources();

		//  create the repository
		DataStore ds = DataStore.instance();

		ds.addObject(RESOURCES_KEY, resources);

		// init the project configuration file
		resources.CustomFilters = new Properties();

		try {
			Debug.out(Debug.MODE_DEBUG,"Master","Reading build configuration from: "+configurationFile);
			SAXParserFactory saxParserFactory =
				SAXParserFactory.newInstance();
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLReader  parser  = saxParser.getXMLReader();
			//BuildXMLHandler handler = new BuildXMLHandler( parser );
			BuildXMLHandler handler = new BuildXMLHandler(parser);
			parser.setContentHandler( handler );
			parser.parse( new InputSource( configurationFile ));

			//System.out.println("Done... "+config.pathSettings.getPath("Base"));
			//System.exit(-1);
		}
		catch (Exception e) { 
			throw new ModuleException("An error occured while reading the build configuration file: "+configurationFile+", reason: "+e.getMessage(),"Master");
		}

		ds.addObject(Master.RESOURCES_KEY,resources);

		// Set debug level
		Debug.setMode(Configuration.instance().getBuildDebugLevel());

		//just added this for testing
		//fixme:we need to iterate over all the cps files specified in the configuration
		//resources.addResource("CpsIterator",Configuration.instance().projects.getConcernSources().iterator());
	}

	/**
	 * Calls run on all modules added to the master.
	 */
	public void run() 
	{
		// This is the 'hardcoded' version
		try {
			Debug.out(Debug.MODE_DEBUG, "Master", "Composestar compile-time " + Version.getVersionString());			

			// Apache XML driver is moved to a different package in Java 5
			if (System.getProperty("java.version").substring(0, 3).equals("1.5")) {
				System.setProperty("org.xml.sax.driver","com.sun.org.apache.xerces.internal.parsers.SAXParser");
				Debug.out(Debug.MODE_DEBUG, "Master", "Selecting SAXParser XML SAX Driver");
			}
			else {
				System.setProperty("org.xml.sax.driver","org.apache.crimson.parser.XMLReaderImpl");
				Debug.out(Debug.MODE_DEBUG, "Master", "Selecting XMLReaderImpl XML SAX Driver");
			}    		 

			Debug.out(Debug.MODE_DEBUG, "Master", "Creating datastore...");
			DataStore.instance();

			// initialize INCRE
			INCRE incre = INCRE.instance();
			incre.run(resources);

			Iterator modulesIter = incre.getModules();
			while (modulesIter.hasNext())
			{
				// execute enabled modules one by one
				Module m = (Module)modulesIter.next();
				m.execute(resources);
			}

			incre.getReporter().close();
			if (Debug.getMode() >= Debug.MODE_WARNING) Debug.outWarnings();
		}
		catch (ModuleException e) { // MasterStopException
			String error = e.getMessage();
			if (error == null || "null".equals(error)) //great information
				error = e.toString();

			if ((e.getErrorLocationFilename() != null) && !e.getErrorLocationFilename().equals(""))
				Debug.out(Debug.MODE_ERROR, e.getModule(), error, e.getErrorLocationFilename(), e.getErrorLocationLineNumber());
			else
				Debug.out(Debug.MODE_ERROR, e.getModule(), error);

			Debug.out(Debug.MODE_DEBUG, e.getModule(), "StackTrace: " + Debug.stackTrace(e));
			System.exit(1);
		}
		catch (Exception e) {
			String error = e.getMessage();
			if (error == null || "null".equals(error)) //great information
				error = e.toString();

			Debug.out(Debug.MODE_ERROR, "Master", "Internal compiler error: " + error);
			Debug.out(Debug.MODE_ERROR, "Master", "StackTrace: " + Debug.stackTrace(e));
			System.exit(2);
		}
	}

	// not used
	public void saveModifiedConfigurationKeys(CommonResources resources)
	{
		List builtAssemblies = (List)resources.getResource("BuiltAssemblies");
		List configLines = new ArrayList();

		try {
			BufferedReader br = new BufferedReader(new FileReader(configfile));
			String line = br.readLine();
			while (line != null)
			{
				if (line.startsWith("BuiltAssemblies=")) 
				{
					line = "BuiltAssemblies=" + builtAssemblies.size();
					configLines.add(line);
					for (int i=0; i < builtAssemblies.size(); i++)
					{
						Object temp = builtAssemblies.get(i);
						if(temp != null)
							configLines.add("BuiltAssembly" + i + "=" + temp.toString());
					}
				}
				else
					configLines.add(line);

				line = br.readLine();
			}
			br.close(); 

			BufferedWriter bw = new BufferedWriter(new FileWriter(configfile));
			Iterator iterLines = configLines.iterator();
			while (iterLines.hasNext()) {
				line = (String)iterLines.next();
				bw.write(line + "\n");
			}
			bw.close();
		}
		catch (IOException e) {
			Debug.out(Debug.MODE_WARNING, "Master", "Unable to update configuration file '" + configfile + "'!");
		}
	}
	
	/**
	 * Compose* main function.
	 * Creates the Master object. Adds the desired modules and then calls run on each
	 * of them in the order that they where added.
	 */
	public static void main(String[] args) 
	{
		if (args.length == 0) {
			System.out.println("Usage: java " + Version.getProgramName() + " <config file>");
			return;
		}

		if (args[0].equalsIgnoreCase("-v")) {
			System.out.println(Version.getTitleString());
			System.out.println(Version.getAuthorString());
			System.exit(0);
		}

		try {
			Debug.out(Debug.MODE_DEBUG,"Master","Invoking Master " + Version.getVersionString() +" now...");
			Master master = new DotNETMaster(args[0]);
			Debug.out(Debug.MODE_DEBUG,"Master","Master initialized.");
			master.run();
		}
		catch (ModuleException e) {
			System.out.println("Could not open configuration file '" + args[0] + "': " + e.getMessage());
			System.exit(-1); // FIXME: are these errorlevels random? 
		}
	}
}
