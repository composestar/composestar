/*
 * This file is part of Composestar StarLight project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 */

package Composestar.DotNET.MASTER;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.Module;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Master;
import Composestar.Core.Master.Config.ConcernSource;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.PathSettings;
import Composestar.Core.Master.Config.Projects;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Repository.RepositoryAccess;
import Composestar.Repository.Configuration.CommonConfiguration;
import Composestar.Utils.Debug;
import Composestar.Utils.Version;

/**
 * Main entry point for the CompileTime. The Master class holds coreModules
 * and executes them in the order they are added.
 */
public class StarLightMaster extends Master  {
	public static final String RESOURCES_KEY = "Composestar.Core.Master.CommonResources";
	
    private CommonResources resources;
    private String configfile;

    private static String yapFileName;
    
    /**
     * Default ctor.
     * @param configurationFile
     * @throws Composestar.core.Exception.ModuleException
     */
    public StarLightMaster(String configurationFile) throws ModuleException{
        yapFileName = configurationFile;
        
    	File f = new File(configurationFile);
    	if (!f.exists()) {
    		Debug.out(Debug.MODE_CRUCIAL,"Master","Configuration file '"+configurationFile+"; not found!");
    		System.exit(-1);
    	}
    	Debug.out(Debug.MODE_DEBUG,"Master","Configuration file: "+configurationFile);
    	RepositoryAccess repository = new RepositoryAccess();
    	
    	// Set the debugmode
    	Debug.setMode(repository.GetCommonConfiguration().get_CompiletimeDebugLevel());  

	    resources = new CommonResources();
	    
	    // Create the repository
	    DataStore ds = DataStore.instance();
	    
	    // Set the paths
        CommonConfiguration configuration = repository.GetCommonConfiguration();
	    Configuration.instance().getPathSettings().addPath("Base", configuration.get_IntermediateOutputPath());
		Configuration.instance().getPathSettings().addPath("Composestar", configuration.get_InstallFolder() + "\\" );

    }
    
    public static String getYapFileName(){
        return yapFileName;
    }

    /**
     * Compose* StarLight main function.
     * Creates the Master object. Adds the desired modules and then calls run on each
     * of them in the order that they where added.
     * @param args
     */
    public static void main(String[] args) {
    	
		if(args.length == 0) {
    		System.out.println("Usage: java Composestar.Core.Master.StarLightMaster <config file>");
    		return;
    	}
		
    	Master master = null;

    	if(args[0].equalsIgnoreCase("-v")) {
    		System.out.println(Version.getTitleString());
    		System.out.println(Version.getAuthorString());
    		System.exit(0);
    	}
    	    
    	Debug.setMode(4);
    	
    	try {    		
    		Debug.out(Debug.MODE_DEBUG,"Master","Master initializing.");
    		master = new StarLightMaster(args[0]);
    		Debug.out(Debug.MODE_DEBUG,"Master","Master initialized.");
    	} catch(ModuleException e) {
    		Debug.out(Debug.MODE_ERROR,"Master", "Could not open configuration file: " + args[0]);    		
    		System.exit(-1);
    	}

    	if (master==null) {
    		Debug.out(Debug.MODE_ERROR,"Master", "Unable to initialize Master process.");
    		System.exit(-1);
    	}
    	
    	// Run the master process
    	master.run();
    	  	
    }
    
    /**
     * Calls run on all modules added to the master.
     */
    public void run() {
    	// This is the 'hardcoded' version

		try {
			String version = "1.0 alpha";
			Debug.out(Debug.MODE_DEBUG, "Master", "Composestar StarLight compiletime " + version);
			
			Debug.out(Debug.MODE_DEBUG, "Master", "Creating DataStore...");
			DataStore.instance();

			Debug.out(Debug.MODE_DEBUG, "Master", "Reading Configuration...");
			
			Projects projects = new Projects();
			Composestar.Repository.RepositoryAccess repository = new Composestar.Repository.RepositoryAccess();
			Iterator concernIterator =repository.GetConcernInformation().iterator();
			while (concernIterator.hasNext()) {
				Composestar.Repository.Configuration.ConcernInformation ci = (Composestar.Repository.Configuration.ConcernInformation)concernIterator.next();
				ConcernSource concern = new ConcernSource();
				concern.setFileName(ci.get_Path() + "\\" + ci.get_Filename());
				projects.addConcernSource(concern);
			}			
			Configuration.instance().setProjects(projects);
            PathSettings path = Configuration.instance().getPathSettings();
		
			
			// initialize INCRE
			INCRE incre = INCRE.instance();
			incre.run(resources);
					
			Iterator modulesIter = incre.getModules();
			while(modulesIter.hasNext())
			{
				// execute enabled modules one by one
				Module m = (Module)modulesIter.next();
				m.execute(resources);
			}
			
			incre.getReporter().close();

			System.exit(0);

		}	
		catch(ModuleException e)  { // MasterStopException
			String error = e.getMessage();
			if(error == null || "null".equals(error)) //great information
			{
				error = e.toString();
			}

			if ((e.getErrorLocationFilename() != null) && !e.getErrorLocationFilename().equals(""))
				Debug.out(Debug.MODE_ERROR, e.getModule(), error, e.getErrorLocationFilename(), e.getErrorLocationLineNumber());
			else
				Debug.out(Debug.MODE_ERROR, e.getModule(), error);
			//Debug.out(Debug.MODE_DEBUG, e.getModule(), "StackTrace: " + printStackTrace(e));
			System.exit(1);
		} 
		catch (Exception ex) {
			Debug.out(Debug.MODE_DEBUG, "Master", printStackTrace(ex));
		}
		//catch(Exception e) {
//			String error = e.getMessage();
//			if(error == null || "null".equals(error)) //great information
//			{
//				error = e.toString();
//			}
//			Debug.out(Debug.MODE_ERROR, "Master", "Internal compiler error: " + error);
//			Debug.out(Debug.MODE_ERROR, "Master", "StackTrace: " + printStackTrace(e));
//			//System.exit(1);
//		}
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
	
	public void SaveModifiedConfigurationKeys(CommonResources resources){}
	
}
