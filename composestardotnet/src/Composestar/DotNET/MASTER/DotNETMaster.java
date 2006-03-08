/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: DotNETMaster.java,v 1.3 2006/02/21 16:42:41 whavinga Exp $
 */

package Composestar.DotNET.MASTER;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import Composestar.Utils.Debug;
import Composestar.Utils.Version;
import Composestar.Utils.StringConverter;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.Module;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Master;
import Composestar.Core.RepositoryImplementation.DataStore;

/**
 * Main entry point for the CompileTime. The Master class holds coreModules
 * and executes them in the order they are added.
 */
public class DotNETMaster extends Master  {
	public static final String RESOURCES_KEY = "Composestar.Core.Master.CommonResources";
	public static String phase = "";

    private Iterator concern_iterator;
    private CommonResources Resources;
    private String configfile;

    
    /**
     * Default ctor.
     * @param configurationFile
     * @throws Composestar.core.Exception.ModuleException
     * @roseuid 401B9C520251
     */
    public DotNETMaster(String configurationFile) throws ModuleException{
    	
		configfile = configurationFile;
    	Debug.setMode(3);
	    Resources = new CommonResources();

	    //  create the repository
	    DataStore ds = DataStore.instance();
	    Resources.addResource("TheRepository", ds);
	    ds.addObject(RESOURCES_KEY, Resources );

	    // init the project configuration file
	    Resources.ProjectConfiguration = new Properties();
	    Resources.CustomFilters = new Properties();

	    try {
	    	//FileInputStream fis = new FileInputStream();
	    	//StreamReader sr = File.OpenText(configurationFile);
	    	//File f = new File("e");

			String line;
	    	BufferedReader br = new BufferedReader(new FileReader(configurationFile));
	    	String section = "";
	    	while ( (line = br.readLine()) != null) {
	    		
	    		if( line.startsWith( "[" ) ) {
	    			section = line.substring( line.indexOf( "[" ) + 1, line.indexOf( "]" ) );
	    		} else 	if ( !line.startsWith("#") && !line.equalsIgnoreCase("") ) {
					if (line.indexOf("=") > 0) {
						String key = line.substring(0, line.indexOf("="));
						String value = "";
						if (line.indexOf("=")+1 != line.length()) {
							value = line.substring(line.indexOf("=") + 1, line.length());
						}
						
						

						if ( value.matches("^([0-5]?\\d?\\d?\\d?\\d|6[0-4]\\d\\d\\d|65[0-4]\\d\\d|655[0-2]\\d|6553[0-5])$") &&
								(key.indexOf("Sources")>=0 || key.indexOf("Dependencies")>=0 || key.indexOf("TypeSources")>=0) )
						{
								int count = Integer.parseInt(value);
								value = "";

								for (int i=0; i<count; i++) {
									line = br.readLine();
									if (line.indexOf("=")+1 != line.length()) {
										if (!value.equalsIgnoreCase("")) {
											value += ",";
										}
										value += line.substring(line.indexOf("=") + 1, line.length());
									}

								}
						}
						
						
						
						//System.out.println(key);
						if( section.equals( "CustomFilters" ) )
							Resources.CustomFilters.setProperty( key, value );
						else 
							Resources.ProjectConfiguration.put(key, value);
					}
	    		}
	    	}
	    	br.close();
			ds.addObject("config",Resources.ProjectConfiguration);
			ds.addObject(Master.RESOURCES_KEY,Resources);
			
	    } catch(IOException e) {
	    	throw new ModuleException();
	    }

	    // Set debug level
	    try {
			Debug.setMode(Integer.parseInt(Resources.ProjectConfiguration.getProperty("BuildDebugLevel")));
		}
	    catch (NumberFormatException e) {
			Debug.setMode(1);
	    }

		//just added this for testing
	    //fixme:we need to iterate over all the cps files specified in the configuration
	    String concerns = (String)Resources.ProjectConfiguration.get("ConcernSources");
		//System.out.println("ConcernSources: "+Resources.ProjectConfiguration);
		if(concerns != null)
		{
			concern_iterator = StringConverter.stringToStringList(concerns);
			Resources.addResource("CpsIterator",concern_iterator);
		}
	}

    /**
     * Compose* main function.
     * Creates the Master object. Adds the desired modules and then calls run on each
     * of them in the order that they where added.
     * @param args
     * @roseuid 401B89E70233
     */
    public static void main(String[] args) {
    	
		if(args.length == 0) {
    		System.out.println("Usage: java " + Version.getProgramName() + " <config file>");
    		return;
    	}
    	Master master = null;

    	if(args[0].equalsIgnoreCase("-v")) {
    		System.out.println(Version.getTitleString());
    		System.out.println(Version.getAuthorString());
    		System.exit(0);
    	}
    	
    	try {
    		Debug.out(Debug.MODE_DEBUG,"Master","Invoking Master " + Version.getVersionString() +" now...");
    		master = new DotNETMaster(args[0]);
    		Debug.out(Debug.MODE_DEBUG,"Master","Master initialized.");
    	} catch(ModuleException e) {
    		System.out.println("Could not open configuration file: " + args[0]);
    		System.out.println("Exiting...");
    		System.exit(-1);
    	}

    	master.run();
    }
    
    /**
     * Calls run on all modules added to the master.
     * @roseuid 401B92150325
     */
    public void run() {
    	// This is the 'hardcoded' version

		try{

			Debug.out(Debug.MODE_DEBUG, "Master", "Composestar compile-time " + Version.getVersionString());
			
			
			// Apache XML driver is moved to a different package in Java 5
    		if( System.getProperty( "java.version" ).substring( 0, 3 ).equals( "1.5" ) ) {
    			System.setProperty("org.xml.sax.driver","com.sun.org.apache.xerces.internal.parsers.SAXParser");
    			Debug.out(Debug.MODE_DEBUG, "Master", "Selecting SAXParser XML SAX Driver" );
    		} else {
    			System.setProperty("org.xml.sax.driver","org.apache.crimson.parser.XMLReaderImpl");
    			Debug.out(Debug.MODE_DEBUG, "Master", "Selecting XMLReaderImpl XML SAX Driver" );
    		}
    		 
    		
			Debug.out(Debug.MODE_DEBUG, "Master", "Creating datastore...");
			DataStore.instance();

			// read phase configuration key
			phase = Resources.ProjectConfiguration.getProperty( "CompilePhase", "ERROR" );
			
			// initialize INCRE
			INCRE incre = INCRE.instance();
			incre.run(Resources);
									
			Iterator modulesIter = incre.getModules();
			while(modulesIter.hasNext())
			{
				// execute enabled modules one by one
				Module m = (Module)modulesIter.next();
				m.execute(phase,Resources);
			}
			
			if("two".equalsIgnoreCase(phase))
			{	
				Debug.out(Debug.MODE_DEBUG, "Master", "Updating configuration file...");
				this.SaveModifiedConfigurationKeys(Resources);

				incre.getReporter().close();
		        
				if (Debug.getMode() >= Debug.MODE_WARNING ) Debug.outWarnings();
			}

		} catch(ModuleException e)  { // MasterStopException
			String error = e.getMessage();
			if(error == null || "null".equals(error)) //great information
			{
				error = e.toString();
			}

			if (!e.getErrorLocationFilename().equals(""))
				Debug.out(Debug.MODE_ERROR, e.getModule(), error, e.getErrorLocationFilename(), e.getErrorLocationLineNumber());
			else
				Debug.out(Debug.MODE_ERROR, e.getModule(), error);
			Debug.out(Debug.MODE_DEBUG, e.getModule(), "StackTrace: " + printStackTrace(e));
			System.exit(1);
		} catch(Exception e) {
			String error = e.getMessage();
			if(error == null || "null".equals(error)) //great information
			{
				error = e.toString();
			}
			Debug.out(Debug.MODE_ERROR, "Master", "Internal compiler error: " + error);
			Debug.out(Debug.MODE_ERROR, "Master", "StackTrace: " + printStackTrace(e));
			//System.exit(1);
		}
	}

	public String printStackTrace(Exception e) 
	{
		try 
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return sw.toString();
		}
		catch(Exception e2) 
		{
			return "Stack Trace Failed";
		}
	} 

	public void SaveModifiedConfigurationKeys(CommonResources resources) {
  	  ArrayList builtAssemblies = (ArrayList) resources.getResource("BuiltAssemblies");
	  java.util.ArrayList configlines = new java.util.ArrayList();
  	  
	  try { 
		BufferedReader br = new BufferedReader(new FileReader(configfile));
		String line = br.readLine();
    	while ( line != null) {
    		if (line.startsWith("BuiltAssemblies=")) {
    			line = "BuiltAssemblies=" + builtAssemblies.size();
    			configlines.add(line);
    			for (int i=0; i<builtAssemblies.size(); i++)
    			{
					Object temp = builtAssemblies.get(i);
					if(temp != null)
    					configlines.add("BuiltAssembly" + i + "=" + temp.toString());
    			}
    		}
    		else {
    			configlines.add(line);
    		}
			line = br.readLine();
    	}
    	br.close(); 
 
	  	BufferedWriter bw = new BufferedWriter(new FileWriter(configfile));
	  	Iterator iterLines = configlines.iterator();
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
}
