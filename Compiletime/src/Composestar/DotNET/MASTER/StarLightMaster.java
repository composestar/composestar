/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: DotNETMaster.java 235 2006-03-14 07:42:35Z dspenkel $
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
     * @roseuid 401B9C520251
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
        
    	Debug.setMode(repository.GetCommonConfiguration().get_CompiletimeDebugLevel());  
		
//    	Debug.setMode(4);
	    resources = new CommonResources();
	    
	    //  create the repository
	    DataStore ds = DataStore.instance();
	    
        CommonConfiguration configuration = repository.GetCommonConfiguration();
	    Configuration.instance().getPathSettings().addPath("Base", configuration.get_IntermediateOutputPath());
		Configuration.instance().getPathSettings().addPath("Composestar", configuration.get_InstallFolder() + "\\" );

		
	    //getPathSettings().getPath("Base")
	    
	    
	   // ModuleSettings ms = new ModuleSettings();
	    //Composestar.Core.Master.Config.Module m = new Composestar.Core.Master.Config.Module();
	    //m.addProperty("enabled", "false");
	   // ms.addModule("INCRE", m);
	    //config.setModuleSettings(ms);


	    //ds.addObject(RESOURCES_KEY, resources );

	    // init the project configuration file
	    //resources.CustomFilters = new Properties();


		
	    //ds.addObject(Master.RESOURCES_KEY,resources);

	    // Set debug level
	    //try {
		//	Debug.setMode(Integer.parseInt(Configuration.instance().getProperty("buildDebugLevel")));
		//}
	    //catch (NumberFormatException e) {
		//	Debug.setMode(1);
	    //}

		//just added this for testing
	    //fixme:we need to iterate over all the cps files specified in the configuration
	    //resources.addResource("CpsIterator",Configuration.instance().projects.getConcernSources().iterator());
	}
    
    public static String getYapFileName(){
        return yapFileName;
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
     * @roseuid 401B92150325
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
