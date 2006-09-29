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

/**
 * Main entry point for the CompileTime. The Master class holds coreModules
 * and executes them in the order they are added.
 */
public class StarLightMaster extends Master  {
	
	private static final String MODULENAME = "MASTER";
	
	private static String version = "1.0 alpha";
	private static String author = "University of Twente";
	private static String title = "ComposeStar StarLight";
	
    private CommonResources resources;
    private String configfile;

    private static String yapFileName;
    
    /**
     * Default ctor.
     * @param configurationFile
     * @throws Composestar.core.Exception.ModuleException
     */
    public StarLightMaster(String configurationFile) {
        // Store the config filename 
    	yapFileName = configurationFile;
        
    	// Init new resources
        resources = new CommonResources();
        
        // Set the default debug mode
        Debug.setMode(Debug.MODE_DEFAULTMODE);            
    }
    
    /**
     * @return The repository filename (Yap database).
     */
    public static String getYapFileName(){
        return yapFileName;        
    }

    /**
     * Compose* StarLight main function.
     * @param args
     */
    public static void main(String[] args) {
    	
		if(args.length == 0) {
    		System.out.println("Usage: java Composestar.DotNET.MASTER.StarLightMaster <config file>");
    		System.exit(0);
    	}
		
		StarLightMaster master = null;

    	if(args[0].equalsIgnoreCase("-v")) {
    		System.out.println(title);
    		System.out.println(author);
    		System.exit(0);
    	}
    	
		// Create new master
		master = new StarLightMaster(args[0]);
    	
    	try { 
    		// Initialize
    		master.initialize();    
    	
    		//	Run the master process
        	master.run();
    	} catch(Exception e) {
    		Debug.out(Debug.MODE_ERROR,MODULENAME, "Could not open configuration file: " + args[0]);    		
    		System.exit(-1);
    	}
    	  	
    }
    
    /**
	 * Initialize the StarLight master.
	 */
	public void initialize() {
		
		Debug.out(Debug.MODE_INFORMATION,MODULENAME,"Master initializing.");
		
		File f = new File(getYapFileName());
    	if (!f.exists()) {
    		Debug.out(Debug.MODE_CRUCIAL,MODULENAME,"Configuration file '"+getYapFileName()+" not found!");
    		System.exit(-1);
    	}
    	Debug.out(Debug.MODE_DEBUG,MODULENAME,"Using configuration file '"+getYapFileName()+"'");
    	RepositoryAccess repository = new RepositoryAccess();
    	
    	// Set the debugmode
    	Debug.setMode(repository.GetCommonConfiguration().get_CompiletimeDebugLevel());  	
	    
	    // Create the repository
	    DataStore ds = DataStore.instance();
	    
	    // Set the paths
        CommonConfiguration configuration = repository.GetCommonConfiguration();
	    Configuration.instance().getPathSettings().addPath("Base", configuration.get_IntermediateOutputPath());
		Configuration.instance().getPathSettings().addPath("Composestar", configuration.get_InstallFolder() + "\\" );
        
        //Set platform:
        Configuration.instance().addProperty( "Platform", "dotnet" );
        
        Debug.out(Debug.MODE_INFORMATION,MODULENAME,"Master initialized.");
	}

	/**
     * Calls run on all modules added to the master.
     */
    public void run() {
    	
		try {		
			Debug.out(Debug.MODE_INFORMATION, MODULENAME, StarLightMaster.title + " " + StarLightMaster.version);
			
			Debug.out(Debug.MODE_DEBUG, MODULENAME, "Creating DataStore");
			DataStore.instance();

			Debug.out(Debug.MODE_DEBUG, MODULENAME, "Reading configuration");
			
			Projects projects = new Projects();
			Composestar.Repository.RepositoryAccess repository = new Composestar.Repository.RepositoryAccess();
			Iterator concernIterator =repository.GetConcernInformation().iterator();
			while (concernIterator.hasNext()) {
				Composestar.Repository.Configuration.ConcernInformation ci = (Composestar.Repository.Configuration.ConcernInformation)concernIterator.next();
				ConcernSource concern = new ConcernSource();
				concern.setFileName(ci.getFullFilename());
				projects.addConcernSource(concern);
			}			
			Configuration.instance().setProjects(projects);
            PathSettings path = Configuration.instance().getPathSettings();		
			
			// Initialize INCRE
            Debug.out(Debug.MODE_DEBUG, MODULENAME, "Initializing INCRE");
			INCRE incre = INCRE.instance();
			incre.run(resources);
				
			Debug.out(Debug.MODE_DEBUG, MODULENAME, "Starting INCRE to process modules");
			Iterator modulesIter = incre.getModules();
			while(modulesIter.hasNext())
			{
				// Execute enabled modules one by one
				Module m = (Module)modulesIter.next();
				m.execute(resources);
			}
			
			// Close INCRE
			incre.getReporter().close();

			// Successfull exit
			System.exit(0);

		}	
		catch(ModuleException e)  { // MasterStopException
			String error = e.getMessage();
			if(error == null || error.equals("null")) //great information
			{
				error = e.toString();
			}

			if ((e.getErrorLocationFilename() != null) && e.getErrorLocationFilename().length() > 0)
				Debug.out(Debug.MODE_ERROR, e.getModule(), error, e.getErrorLocationFilename(), e.getErrorLocationLineNumber());
			else
				Debug.out(Debug.MODE_ERROR, e.getModule(), error);
			//Debug.out(Debug.MODE_DEBUG, e.getModule(), "StackTrace: " + printStackTrace(e));
			System.exit(1);
		} 
		catch (Exception ex) {
			Debug.out(Debug.MODE_DEBUG, MODULENAME, Debug.stackTrace(ex));
			System.exit(1);
		}
	}

}
