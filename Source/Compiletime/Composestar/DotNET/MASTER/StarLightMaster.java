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
import java.io.IOException;
import java.util.Iterator;

import org.apache.xmlbeans.XmlException;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.Module;
import Composestar.Core.INCRE.INCRETimer;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Master;
import Composestar.Core.Master.Config.ConcernSource;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.PathSettings;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;

import composestar.dotNET.tym.entities.ArrayOfConcernElement;
import composestar.dotNET.tym.entities.ConcernElement;
import composestar.dotNET.tym.entities.ConfigurationContainer;
import composestar.dotNET.tym.entities.ConfigurationContainerDocument;

/**
 * Main entry point for the CompileTime. The Master class holds coreModules
 * and executes them in the order they are added.
 */
public class StarLightMaster extends Master  {
	
	private static final String MODULENAME = "MASTER";
	
	private static String version = "0.1 beta";
	private static String author = "University of Twente";
	private static String title = "ComposeStar StarLight";
	
    private CommonResources resources;
    private String configfile;

    private static String configFileName;
    private static ConfigurationContainerDocument configDocument;
    private static ConfigurationContainer configContainer;
    
    /**
     * Default ctor.
     * @param configurationFile
     */
    public StarLightMaster(String configurationFile) {
        // Store the config filename 
    	configFileName = configurationFile;
        
    	// Init new resources
        resources = new CommonResources();
        
        // Set the default debug mode
        Debug.setMode(Debug.MODE_DEFAULTMODE);            
    }
    
    /**
     * @return The repository filename (Yap database).
     */
    public static String getConfigFileName(){
        return configFileName;        
    }
    
    

    /**
	 * @return the configContainer
	 */
	public static ConfigurationContainer getConfigContainer()
	{
		return configContainer;
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
    		e.printStackTrace();
    		Debug.out(Debug.MODE_ERROR,MODULENAME, "Could not open configuration file: " + args[0]);    		
    		System.exit(-1);
    	}
    	  	
    }
    
    /**
	 * Initialize the StarLight master.
     * @throws IOException 
     * @throws XmlException 
	 */
	private void initialize() throws XmlException, IOException {
		
		Debug.out(Debug.MODE_INFORMATION,MODULENAME,"Master initializing.");
		
		File f = new File(getConfigFileName());
    	if (!f.exists()) {
    		Debug.out(Debug.MODE_CRUCIAL,MODULENAME,"Configuration file '"+getConfigFileName()+" not found!");
    		System.exit(-1);
    	}
    	Debug.out(Debug.MODE_DEBUG,MODULENAME,"Using configuration file '"+getConfigFileName()+"'");
//    	configContainer = ConfigurationContainer.Factory.parse( new File(configFileName) );
    	configDocument = ConfigurationContainerDocument.Factory.parse(
    			new File(configFileName));
    	configContainer = configDocument.getConfigurationContainer();
    	
    	// Set the debugmode
    	Debug.setMode(configContainer.getCompiletimeDebugLevel());
    	//Debug.setMode(Debug.MODE_WARNING);
	    
		// Apache XML driver is moved to a different package in Java 5
		if (System.getProperty("java.version").substring(0, 3).equals("1.5")) {
			System.setProperty("org.xml.sax.driver","com.sun.org.apache.xerces.internal.parsers.SAXParser");			
		}
		else {
			System.setProperty("org.xml.sax.driver","org.apache.crimson.parser.XMLReaderImpl");			
		}  
    	
	    // Create the repository
	    DataStore ds = DataStore.instance();
	     
	    // Set the paths
	    if ( configContainer.getIntermediateOutputPath() != null ){
	    	Configuration.instance().getPathSettings().addPath("Base", configContainer.getIntermediateOutputPath());
	    }
		Configuration.instance().getPathSettings().addPath("Composestar", configContainer.getInstallFolder() + "\\" );
        
        //Set platform:
        Configuration.instance().addProperty( "Platform", "dotnet" );
        
        // Set FILTH input file
        Composestar.Core.Master.Config.ModuleSettings filthSettings = new Composestar.Core.Master.Config.ModuleSettings();
        filthSettings.setName("FILTH");
        filthSettings.addProperty("input", configContainer.getSpecificationFILTH());
        Configuration.instance().getModuleSettings().addModule("FILTH", filthSettings);
        
        Debug.out(Debug.MODE_INFORMATION,"Master","Master initialized.");
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
						
			ArrayOfConcernElement concerns = configContainer.getConcerns();
			for (int i=0; i<concerns.sizeOfConcernArray(); i++){
				ConcernElement ci = concerns.getConcernArray(i);
				ConcernSource concern = new ConcernSource();
				concern.setFileName(ci.getPathName() + '\\' + ci.getFileName());
				
				Configuration.instance().getProjects().addConcernSource(concern);				
			}			
			
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
				Debug.out(Debug.MODE_DEBUG, "INCRE", m.getName() + " executed in " + incre.getReporter().getTotalForModule(m.getName(), INCRETimer.TYPE_ALL) + " ms");
				
			}
			
			// Close INCRE
			incre.getReporter().close();

			// Shutdown
			shutdown();

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
    
    
    private void shutdown() throws IOException{
    	//write config file:
    	File file = new File(configFileName);
    	configDocument.save(file);
		
		// Successfull exit
		System.exit(0);
    }

}
