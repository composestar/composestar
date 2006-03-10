package Composestar.Core.INCRE;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.CompiledImplementation;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SimpleSelectorDef.PredicateSelector;
import Composestar.Core.CpsProgramRepository.PlatformRepresentation;
import Composestar.Core.CpsProgramRepository.PrimitiveConcern;
import Composestar.Core.CpsProgramRepository.Signature;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.Config.*;
import Composestar.Core.LAMA.*;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Module;
import Composestar.Core.Master.Config.Project;
import Composestar.Core.Master.Config.Source;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Master;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.RepositoryImplementation.DeclaredRepositoryEntity;
import Composestar.Core.CpsProgramRepository.MethodWrapper;
import Composestar.Core.TYM.TypeLocations;

import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;
import Composestar.Utils.StringConverter;

import java.io.File;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * The INCRE class is responsible for deciding which modules are incremental 
 * and which input has already been processed by a  module in a previous compilation run. 
 * This decision is based on the history which is being loaded and stored by INCRE.  
 * This class is the heart of the incremental compilation process.
 */
public class INCRE implements CTCommonModule 
{    
	private static INCRE Instance = null;
	private DataStore currentRepository;
	public DataStore history;
	private boolean enabled = false;
	public boolean searchingHistory = false;
	
	private String historyfile = "";
	private Date lastCompTime = null;
	private MyComparator comparator;
	private ConfigManager configmanager = null;
	private INCREReporter reporter;
	
	/* for optimalization purposes */
	public INCREConfigurations configurations;
	private HashMap filesCheckedOnTimeStamp;
	private HashMap filesCheckedOnProjectConfig;
	public HashMap externalSourcesBySource;
	private HashMap dsObjectsOrdered;
	private HashMap historyObjectsOrdered;
	private ArrayList currentConcernsWithFMO;
	private ArrayList historyConcernsWithFMO;
	private ArrayList currentConcernsWithModifiedSignatures;
	private ArrayList historyConcernsWithModifiedSignatures;
	private String projectSources;
	
	public INCRE()
	{
		reporter = new INCREReporter();
		reporter.open();
		filesCheckedOnTimeStamp = new HashMap();
		filesCheckedOnProjectConfig = new HashMap();
		dsObjectsOrdered = new HashMap();
		historyObjectsOrdered = new HashMap();
		externalSourcesBySource = new HashMap();
		configurations = new INCREConfigurations();
	}
	
	public static INCRE instance()
	{
		if (Instance == null) 
		{
			Instance = new INCRE();
		}
		return (Instance);
	} 

	public MyComparator getComparator(){
		return comparator;
	}
	
	/**
    * @roseuid 41E7E74501C2
    */
   public void run(CommonResources resources) throws ModuleException 
   {
	   Configuration config = Configuration.instance();
       //this.historyfile = resources.ProjectConfiguration.getProperty("TempFolder") + "history.dat";
       this.historyfile = config.pathSettings.getPath("Temp")+"history.dat";
	   
   	   // ability to turn INCRE on
   	   String incre_enabled = "";
   	   Module m = config.moduleSettings.getModule("INCRE");
   	   if(m!=null){
   		   incre_enabled = m.getProperty("enabled");
   	   }
       
       //if ("True".equalsIgnoreCase(resources.ProjectConfiguration.getProperty("INCRE_ENABLED"))) {
		//	this.enabled = true;
	   //}
   	   if ("True".equalsIgnoreCase(incre_enabled)) {
			this.enabled = true;
	   }
   	   else {
   			// non-incremental compilation so clean history
   			this.deleteHistory();
   	   }
   	
   	   // time this initialization process	
	   INCRETimer incretotal = this.getReporter().openProcess("INCRE","",INCRETimer.TYPE_ALL);
	   
	   // set configmanager to read xml configuration file */
	   configmanager = new ConfigManager(resources);
	   //String configfile = resources.ProjectConfiguration.getProperty("TempFolder") + "INCREconfig.xml";
	   String configfile = config.pathSettings.getPath("Temp") + "INCREconfig.xml";
	   try
	   {
		   File file = new File(configfile);
		   if(!file.exists() && !file.canRead())
			   configfile = config.pathSettings.getPath("Composestar") + "INCREconfig.xml";
			   //configfile = resources.ProjectConfiguration.getProperty("ComposestarPath") + "INCREconfig.xml";
	   }
	   catch(Exception ioe)
	   {
		   ioe.printStackTrace();
	   }
	   
	   /* parse the XML configuration file containing the modules 
	    	time the parsing process */
	   INCRETimer increparse = this.getReporter().openProcess("INCRE","Parsing configuration file",INCRETimer.TYPE_OVERHEAD);			
	   try 
	   {
		   	configmanager.parseXML(configfile);
		   	
		   	// load the project sources by iterating over the compilers
		   	/*String compilerStr = resources.ProjectConfiguration.getProperty( "Compilers", "ERROR" );
		   	if( "ERROR".equals(compilerStr) ) {
		   		throw new ModuleException( "Error in configuration file: No such property \"Compilers\"" );
		   	}
		   	Iterator compIt = StringConverter.stringToStringList( compilerStr );
		   	while( compIt.hasNext() ) {
		   		String compilerPrefix = (String)compIt.next();
	            String srcprefix = compilerPrefix + "Sources";
	            String execprefix = compilerPrefix + "ExecSource";
	            String sourceStr = resources.ProjectConfiguration.getProperty( srcprefix, "ERROR" );
	            
	            if( !"ERROR".equals( sourceStr ) ){
	                 this.projectSources += ",";
	                 this.projectSources += sourceStr;
	            }
	            
	            String execSourceStr = resources.ProjectConfiguration.getProperty( execprefix, "ERROR" );
	            if( !"ERROR".equals( execSourceStr ) ){
	                 this.projectSources += ",";
	                 this.projectSources += execSourceStr;
	            }
	        }
		   	
		   	int firstComma = this.projectSources.indexOf(",")+1;
		   	this.projectSources = this.projectSources.substring(firstComma);*/
		   	
		   	// get all sources by iterating over projects
		   	ArrayList sources = new ArrayList();
		   	ArrayList projects = config.projects.getProjects();
		   	Iterator prjIter = projects.iterator();
		   	while(prjIter.hasNext()){
		   		Project p = (Project)prjIter.next();
		   		ArrayList sourcesList = p.getSources();
		   		Iterator sourceItr = sourcesList.iterator();
		   		while(sourceItr.hasNext()){
		   			Source s = (Source)sourceItr.next();
		   			sources.add(s.getFileName());
		   		}
		   	}
		   	String[] sourcePaths = (String[]) sources.toArray(new String[sources.size()]);
		   	this.projectSources = StringConverter.stringListToString(sourcePaths);
		   	Debug.out(Debug.MODE_DEBUG,"INCRE","ProjectSources "+this.projectSources);
	   }
	   catch(Exception e)
	   {
		   increparse.stop();	
		   String error = e.getMessage();
		   if(error == null || "null".equals(error))
		   {
			   error = e.toString();
		   }
		   // parsing of configuration failed!
		   throw new ModuleException(error,"INCRE");
	   }

	   increparse.stop();  	
	   
	   // only load history once
	   //String phase = resources.ProjectConfiguration.getProperty("CompilePhase");
	   String phase = config.getProperty("compilePhase");
	   if(!phase.equals("two")){
	   		this.enabled = false; // no need to load history in MASTER phase one
	   }
	   
	   if(this.enabled){	
	   		/* load data of previous compilation run (history)
	   		 	time the loading process */
	   		INCRETimer loadhistory = this.getReporter().openProcess("INCRE","Loading history",INCRETimer.TYPE_OVERHEAD);	
	   		this.enabled = this.loadHistory(historyfile);
	   		loadhistory.stop(); // shut down INCRE in case loading fails!	
	   }
	   
	   if(this.enabled){
	   		// preprocess configurations for fast retrieval
	   		configurations.init();
	   }
	   
	   incretotal.stop(); // stop timing INCRE's initialization 
	   
	   // INCRE enabled or not? 
	   Debug.out(Debug.MODE_DEBUG, "INCRE","INCRE enabled? "+this.enabled);
   }

	/**
	 * Returns an instance of INCREReporter
	 */
	public INCREReporter getReporter() 
	{
		return this.reporter;
	}

	/**
	 * Returns an instance of ConfigManager
	 */
	public ConfigManager getConfigManager() 
	{
		return this.configmanager;
	}
	
	public void addConfiguration(String key, String val)
	{
		configurations.addConfiguration(key,val);
	}
	
	public String getConfiguration(String key)
	{
		return configurations.getConfiguration(key);		
	}
	
	
	public DataStore getCurrentRepository()
	{
		return this.currentRepository;
	}
	
	public ArrayList getConcernsWithFMO()
	{
		if(searchingHistory){
			// if set before, return it
			if(historyConcernsWithFMO!=null)
				return historyConcernsWithFMO;
		}
		else {
			// if set before, return it
			if(currentConcernsWithFMO!=null)
				return currentConcernsWithFMO;
		}
		
		ArrayList concerns = new ArrayList();
		Iterator iterConcerns = currentRepository.getAllInstancesOf(Concern.class);
		while ( iterConcerns.hasNext() ){
			Concern c = (Concern)iterConcerns.next();
			if ( c.getDynObject("SingleOrder") != null){
				concerns.add(c);	
			}	
		}
		
		if(searchingHistory)
			historyConcernsWithFMO = concerns;
		else 
			currentConcernsWithFMO = concerns;
		
		// sort concerns before returning
		Collections.sort(concerns, new Comparator() { 
		   	public int compare(Object o1, Object o2) { 
		    	Concern c1 = (Concern) o1;
		   		Concern c2 = (Concern) o2;
		   		String s1 = c1.getQualifiedName();
		   		String s2 = c2.getQualifiedName();
		   		return s1.compareTo(s2); 
		   	}
		  });
		return concerns;
	}
	
	public ArrayList getConcernsWithModifiedSignature()
	{
		if(searchingHistory){
			// if set before, return it
			if(historyConcernsWithModifiedSignatures!=null)
				return historyConcernsWithModifiedSignatures;
		}
		else {
			// if set before, return it
			if(currentConcernsWithModifiedSignatures!=null)
				return currentConcernsWithModifiedSignatures;
		}
		
		ArrayList concerns = new ArrayList();
		Iterator iterConcerns = currentRepository.getAllInstancesOf(Concern.class);
		while ( iterConcerns.hasNext() ){
			Concern c = (Concern)iterConcerns.next();
			Signature signature = c.getSignature();
			List added = signature.getMethods(MethodWrapper.ADDED);
			List removed = signature.getMethods(MethodWrapper.REMOVED);
			if(!added.isEmpty() || !removed.isEmpty()){
				concerns.add(c);	
			}	
		}
		
		if(searchingHistory)
			historyConcernsWithModifiedSignatures = concerns;
		else 
			currentConcernsWithModifiedSignatures = concerns;
		
		// sort concerns before returning
		Collections.sort(concerns, new Comparator() { 
		   	public int compare(Object o1, Object o2) { 
		    	Concern c1 = (Concern) o1;
		   		Concern c2 = (Concern) o2;
		   		String s1 = c1.getQualifiedName();
		   		String s2 = c2.getQualifiedName();
		   		return s1.compareTo(s2); 
		   	}
		  });
		return concerns;
	}
	
	public Iterator getAllInstancesOfOrdered(Class c)
	{
		if(searchingHistory){
			// if set before, return it
			if(historyObjectsOrdered.containsKey(c.getName()))
				return ((ArrayList)historyObjectsOrdered.get(c.getName())).iterator();
		}
		else {
			// if set before, return it
			if(dsObjectsOrdered.containsKey(c.getName()))
				return ((ArrayList)dsObjectsOrdered.get(c.getName())).iterator();
		}
		
		ArrayList list = new ArrayList();
		if(searchingHistory)
			list = history.getListOfAllInstances(c);
		else
			list = currentRepository.getListOfAllInstances(c);
		
		// sort the list
		Collections.sort(list, new Comparator() { 
		   	public int compare(Object o1, Object o2) { 
		    	if(o1 instanceof CompiledImplementation){
		    		CompiledImplementation c1 = (CompiledImplementation)o1;
		    		CompiledImplementation c2 = (CompiledImplementation)o2;
		    		String s1 = c1.getClassName();
		    		String s2 = c2.getClassName();
		    		return s1.compareTo(s2); 
		    	}
		   		else {
		    		Concern c1 = (Concern) o1;
		    		Concern c2 = (Concern) o2;
		    		String s1 = c1.getQualifiedName();
		    		String s2 = c2.getQualifiedName();
		    		return s1.compareTo(s2); 
		    	}
		   	}
		  });
		
		// add the ordered list to hashmap
		if(searchingHistory)
			historyObjectsOrdered.put(c.getName(),list);
		else
			dsObjectsOrdered.put(c.getName(),list);
		
		return list.iterator();
	}
	
   /**
    * Searches the history repository for the specified object. 
	* Return null if object can't be found.
    * Uses getQualifiedName to compare objects
    * @return Object
    * @roseuid 421094DA000F
    */
   public Object findHistoryObject(Object obj) 
   {
   		try {
   			if(obj.getClass().equals(String.class)){
   				// special case, return string
   				return obj;
   			}
   	    
   			Iterator objIter = history.getAllInstancesOf(obj.getClass());
   			while( objIter.hasNext() ){
   				Object nextobject = (Object)objIter.next();
   				if(obj instanceof DeclaredRepositoryEntity){
   					DeclaredRepositoryEntity dre = (DeclaredRepositoryEntity)nextobject;
   					if(dre.getQualifiedName().equals(((DeclaredRepositoryEntity)obj).getQualifiedName()))
   						return dre;
   				}
   				else if(obj instanceof PredicateSelector){
   					PredicateSelector ps = (PredicateSelector)nextobject;
   					if(ps.getUniqueID().equals(((PredicateSelector)obj).getUniqueID()))
   						return ps;				
   				}
   			}
   	       			
   			return null;
   	    }
   		catch(Exception ex){
			// too bad, but not fatal
			Debug.out(Debug.MODE_DEBUG, "INCRE","Cannot find history object for object "+obj.getClass().getName()+" due to "+ex.toString());
			return null;
		}
   }
      
   public boolean isFileModified(String filename)
   {
   		if(filesCheckedOnTimeStamp.containsKey(filename))
			return ((Boolean)filesCheckedOnTimeStamp.get(filename)).booleanValue();	
		else { 
			File f = new File(filename);  			
			boolean modified = isFileModified(f);
			filesCheckedOnTimeStamp.put(filename, Boolean.valueOf(modified));
			return modified;
		}	
   }
   
   /**
    * Compares timestamp of file with last compilation time
    * @return boolean
    * @roseuid 42109419032C
    */
	public boolean isFileModified(File file) 
	{
		if(!file.exists()){
			Debug.out(Debug.MODE_WARNING, "INCRE","INCRE::isFileModified file "+file.getName()+" does not exist");
			return true;
		}
		boolean modified = true;
		try {
			modified= (file.lastModified() > lastCompTime.getTime());
		}
		catch(Exception e){return modified;}
		return modified;
	}	
   
	/**
	  * Checks whether a file has been added to the project since last compilation
	  * returns false when file has been found in previous project configurations
	  * @return boolean
	  */
	public boolean isFileAdded(String filename,FileDependency fdep) throws ModuleException
	{
		if(filesCheckedOnProjectConfig.containsKey(filename)) // checked before
  			return ((Boolean)filesCheckedOnProjectConfig.get(filename)).booleanValue();	
		
		boolean isAdded = true;
		String fixedFile = FileUtils.fixFilename(filename).toLowerCase();
		String searchStr = "";
		
		// project configuration of previous compilation run
		//Properties prop = (Properties)history.getObjectByID("config"); 
				 
		// As an optimalization: 
		// do not look in all configurations but only in the interesting part(s)
		// thus set searchstring dependent of type of file
		if(fixedFile.endsWith(".cs") || fixedFile.endsWith(".jsl") || fixedFile.endsWith(".vb") || fixedFile.endsWith(".java")){
			//TODO: use configurable SupportedLanguages (xml)
			searchStr = this.projectSources;// look in project sources
		}
		else if(fixedFile.endsWith(".cps")){
			//searchStr = prop.getProperty("ConcernSources");// look in concern sources
			ArrayList conList = Configuration.instance().projects.getConcernSources();
			String[] conPaths = (String[])conList.toArray(new String[conList.size()]);
		   	searchStr = StringConverter.stringListToString(conPaths);
		}
		else if(fixedFile.endsWith(".dll") || fixedFile.endsWith(".exe")){
			// TODO: use SupportedLanguages and move/replace .NET specific code
			// special case, never added to project configurations
			// TODO: add to project configurations
			if(fixedFile.indexOf("mscorlib.dll")>=0)
				return false;
			
			if(fixedFile.indexOf("/gac/")>0){// Global Assembly Cache
					fixedFile = fixedFile.substring(fixedFile.lastIndexOf("/")+1);
			}
			
			// TODO: make this configurable for filedependencies in config.xml
			CommonResources resources = (CommonResources)DataStore.instance().getObjectByID(Master.RESOURCES_KEY);
			try {
				// no need to check 'added to project' for these files
				if(filename.indexOf("/obj/Weaver/")>0){
					return false;
				}
				
				ArrayList compSources = (ArrayList)resources.getResource("CompiledSources");
				if(compSources.contains(filename)){
					return false;
				}
			}
			catch(Exception e){/*ignore*/}
			
			// look in configurations "Dependencies" and "Assemblies"
			// TODO: possible naming conflict when JAVA platform is there
			//searchStr = prop.getProperty("Dependencies");
			ArrayList depList = Configuration.instance().projects.getDependencies();
			String[] depPaths = (String[])depList.toArray(new String[depList.size()]);
		   	searchStr += StringConverter.stringListToString(depPaths);
		   	
			//searchStr += prop.getProperty("Assemblies");
			ArrayList dummies = Configuration.instance().projects.getCompiledDummies();
			String[] dummyPaths = (String[])dummies.toArray(new String[dummies.size()]);
		   	searchStr += StringConverter.stringListToString(dummyPaths);
			
		} 
		else {
			// file could be referenced by a ConfigNode of the FileDependency
			Path p = fdep.getPath();
			if(!p.isEmpty()){
				Node n = (Node)p.getFirstNode();
				if(n instanceof ConfigNode){
					//searchStr = (String)prop.getProperty(n.getReference());
					searchStr = (String)configurations.historyconfig.getProperty(n.getReference());
				}
			}
		}
		
		// file in old project configurations?
		searchStr = FileUtils.fixFilename(searchStr).toLowerCase();
		if(searchStr.indexOf(fixedFile)>=0)
			isAdded = false; // file not added to project
		
		if(isAdded){
			Debug.out(Debug.MODE_DEBUG,"INCRE","File "+fixedFile+" added to project since last compilation run");
		}
		
		this.filesCheckedOnProjectConfig.put(filename, Boolean.valueOf(isAdded));
		return isAdded;
	}
   /**
    * @return true when module is incremental
    * @roseuid 41EE335602EE
    */
	public boolean isModuleInc(String name)
	{
		if(enabled){
			if(configmanager.getModuleByID(name)!=null){
				Composestar.Core.INCRE.Module m = configmanager.getModuleByID(name);
				return m.isIncremental();
			}
		}
		
		return false;
	}
   
	/**
	 * Returns all modules extracted from a configuration file
	 * @return Iterator of modules
	 */
	public Iterator getModules()
	{
		return configmanager.getModules().values().iterator();
	}
   
	/**
	 * Returns all primitive concerns potentially modified
	 * primitive concerns from unmodified assemblies/sources are excluded
	 * If this information is not available then the primitive concern is included
	 * @param ds Datastore to search
	 * @return
	 */
	public ArrayList getAllModifiedPrimitiveConcerns(DataStore ds) throws ModuleException{
	  INCRE incre = INCRE.instance();
	  TypeLocations locations = TypeLocations.instance();
	  ArrayList list = new ArrayList();
	  
	  Iterator concerns = ds.getAllInstancesOf(PrimitiveConcern.class);
	  while(concerns.hasNext()){
	      PrimitiveConcern pc = (PrimitiveConcern)concerns.next();
	      ProgramElement unit = (ProgramElement)pc.platformRepr;
	       
	        // Only add primitive concerns in case:
	        // 1. concern extracted from modified source file 
	      	// 2. concern extracted from modified assembly
	        if(unit instanceof Type){
	        	 Type dtype = (Type)unit;
	        	 String sourceFile = locations.getSourceByType(dtype.FullName);
	        	 if(sourceFile!=null && !incre.isFileAdded(sourceFile,null) && !incre.isFileModified(sourceFile)){
	        	 	/*skip because sourcefile unmodified*/
	        	 }
	        	 // TODO: check this
	        	 //else if(!incre.isFileAdded(dtype.Module.FullyQualifiedName,null) && !incre.isFileModified(dtype.Module.FullyQualifiedName)){
	        	 	/*skip because assembly unmodified*/
	        	 //}
	        	 else {
	        	 	list.add(pc); /* safety first */
	        	 }
	        }
	        else {
	        	list.add(pc);
	        }
	  }
	 
	  /* sort primitive concerns */
	  Collections.sort(list, new Comparator() { 
	   	public int compare(Object o1, Object o2) { 
	    	PrimitiveConcern pc1 = (PrimitiveConcern) o1;
	   		PrimitiveConcern pc2 = (PrimitiveConcern) o2;
	   		String s1 = pc1.getUniqueID();
	   		String s2 = pc2.getUniqueID();
	   		return s1.compareTo(s2); 
	   	}
	  });	
	 
	  return list;
	}
	
	/**
	 * Returns true if concern is possible declared in a sourcefile
	 * @param c - The concern possible declared in sourcefile
	 * @param src - Fullpath of sourcefile
	 */
	public boolean declaredInSource(Concern c,String src){
		
		/* Sourcefile format: C:\Program Files\ComposeStar\... */
		String source = src.replace('/','\\');
		TypeLocations locations = TypeLocations.instance();
		PlatformRepresentation repr = c.getPlatformRepresentation();
		
		if(repr instanceof Type){
			Type type = (Type)repr;
			if(type.IsNestedPrivate || type.IsNestedPublic){
				/* undecided yet, safety first */
				return true;
			}
			
			String location = locations.getSourceByType(type.FullName);
			if(location!=null){
				if(location.equals(source))
					return true;
			}
		}
		else {
			/* undecided yet, safety first */
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns true if concern is possible declared in one of the source files
	 * @param c - The concern possible declared in sourcefile
	 * @param src - Fullpath of sourcefile
	 */
	public boolean declaredInSources(Concern c,ArrayList sources){
		
		Iterator sourceItr = sources.iterator();
		while(sourceItr.hasNext()){
			String src = (String)sourceItr.next();
			if(declaredInSource(c,src))
				return true;
		}
		
		return false;
	}
	
	/**
	 * step 1. Get module
	 * step 2. Get dependencies of modules
	 * step 3. iterate over dependencies
	 * step 4. get dependent object
	 * step 5. search history for same object
	 * step 6. compare two objects (only in case not a file)
	 * step 7. stop if modification found
	 * 
	 * return true in case all dependencies have not been modified
	 * return false otherwise
	 * @roseuid 41F4E50900CB
	 */
	public boolean isProcessedByModule(Object input, String modulename) throws ModuleException  
	{
		comparator = new MyComparator(modulename);
	   	currentRepository = DataStore.instance();
	   	searchingHistory = false;
	   	Object inputobject = input;
		Object historyobject = null;
		Object depofinputobject = null;
		Object depofhistoryobject = null; 
		INCRETimer overhead = getReporter().openProcess(modulename,"INCRE::isProcessedBy("+input+")",INCRETimer.TYPE_OVERHEAD);
		
	   	if(!isModuleInc(modulename))
	   		return false;
	   		 
	   	Composestar.Core.INCRE.Module mod = configmanager.getModuleByID(modulename);
		if(mod!=null)
		{
			// *** Little verification of input object ***
			try 
			{
				if ( !Class.forName ( mod.getInput() ).isInstance ( input ) )
					throw new ModuleException("Wrong input for module "+mod.getName()+". "+input.getClass()+" is not an instance of "+mod.getInput(),"INCRE");
				}
			catch(ClassNotFoundException cnfe)
			{
				throw new ModuleException("Could not find class "+mod.getInput(),"INCRE::isProcessedByModule");
			}

			Iterator dependencies = mod.getDeps();
			while(dependencies.hasNext())
			{
				currentRepository = DataStore.instance();
				searchingHistory = false;
				Dependency dep = (Dependency)dependencies.next();
				
				try {
					depofinputobject = (Object)dep.getDepObject(inputobject);
				}
				catch(Exception e){
					Debug.out(Debug.MODE_DEBUG,"INCRE","Could not capture dependency "+dep.getName()+ " for "+inputobject);
					Debug.out(Debug.MODE_DEBUG, "INCRE","Found modified dependency [module="+modulename+",dep="+dep.getName()+",input="+input+"]");
					return false;
				}
					
				if(dep instanceof FileDependency)
				{
					/* check if file(s) have been modified
					 stop process when a file has been modified */
					ArrayList files = (ArrayList)depofinputobject;
					if(files.size()>0 && files.get(0).equals("EMPTY_CONFIG")){
						// special case, file has not been configured
						currentRepository = history;
						ArrayList hfiles = (ArrayList)dep.getDepObject(inputobject);
						if(!hfiles.get(0).equals("EMPTY_CONFIG")){
							// configuration has been removed since last compilation run
							Debug.out(Debug.MODE_DEBUG, "INCRE","Found modified dependency [module="+modulename+",dep="+dep.getName()+",input="+input+"]");
							return false;
						}
						currentRepository = DataStore.instance();
					}
					else {
						// iterate over all files	
						Iterator fileItr = files.iterator();
						while(fileItr.hasNext())
						{
							String currentFile = (String)fileItr.next();
							if(isFileAdded(currentFile,(FileDependency)dep)){
								Debug.out(Debug.MODE_DEBUG, "INCRE","Found modified dependency [module="+modulename+",dep="+dep.getName()+",input="+input+"]");
								return false; // file added to project thus modified!
							}
							if(isFileModified(currentFile))
							{							
								overhead.stop();
								Debug.out(Debug.MODE_DEBUG, "INCRE","Found modified dependency [module="+modulename+",dep="+dep.getName()+",input="+input+"]");
								return false;	
							}
						}
					}
				}
				else 
				{
					if(dep.lookup && depofinputobject!=null && comparator.comparisonMade(dep.getName()+depofinputobject.hashCode()))
					{
						// the dependency has been checked before
						boolean modified = !comparator.getComparison(dep.getName()+depofinputobject.hashCode());
						if(modified){
							overhead.stop();
							Debug.out(Debug.MODE_DEBUG, "INCRE","Found modified dependency [module="+modulename+",dep="+dep.getName()+",input="+input+"]");
							return false;
						}
					}
					else 
					{
						// find object in history
						if(historyobject==null)
							historyobject = findHistoryObject(inputobject);
												
						if(historyobject!=null)
						{
							// get dependent object of the 'history' object
							currentRepository = history;
							searchingHistory = true;
							depofhistoryobject = (Object)dep.getDepObject(historyobject);
								
							// compare both dependent objects for modification
							boolean modified = !comparator.compare(depofinputobject,depofhistoryobject);
								
							// add the result to comparator's map
							if(dep.store && depofinputobject!=null)
								comparator.addComparison(dep.getName()+depofinputobject.hashCode(),!modified);
					
							// stop calculation when object has been modified
							if(modified){
								overhead.stop();
								Debug.out(Debug.MODE_DEBUG, "INCRE","Found modified dependency [module="+modulename+",dep="+dep.getName()+",input="+input+"]");
								return false;
							}
						}
						else 
						{
							/* history of input object cannot be found 
								thus input has not been processed */
							overhead.stop();
							Debug.out(Debug.MODE_DEBUG, "INCRE","Found modified dependency [module="+modulename+",dep="+dep.getName()+",input="+input+"]");
							return false;
						}
					}
				}
			} // next dependency plz	
		}
		else 
		{
			throw (new ModuleException("INCRE cannot find module "+modulename+"!"));
		}

		/* all dependencies have not been modified 
			thus input has already been processed */
		overhead.stop();
		
		return true;
   }
   
   public void deleteHistory()
   {
   	    File f = new File(this.historyfile);
   	    if(f.exists()){
   	   		f.delete();
   	    }
   }
	
	
   /**
    * Loads the history repository specified by the filename. Uses objectinputstream.
    * @param filename
    * @roseuid 4209F748036B
    */
   public boolean loadHistory(String filename) throws ModuleException
   {
	   try 
	   {
		   FileInputStream fis = new FileInputStream(filename);
		   BufferedInputStream bis = new BufferedInputStream(fis);
		   ObjectInputStream ois = new ObjectInputStream(bis);
		   history = new DataStore();
		   
		   // read last compilation date
		   this.lastCompTime = (Date)ois.readObject();
		   Debug.out(Debug.MODE_INFORMATION, "INCRE","Loading history ("+lastCompTime.toString()+") ...");	

		   // read project configurations
		   //history.addObject("config",ois.readObject());
		   configurations.historyconfig = (Configuration)ois.readObject();
		   
		   int numberofobjects = ois.readInt();	
		   for(int i=0;i<numberofobjects;i++)
		   {
			   try 
			   {
				    history.addObject(ois.readObject());
			   }
			   catch(EOFException ex){
					Debug.out(Debug.MODE_WARNING, "INCRE",ex.toString());
					return false;
			   }	
		   }

		   ois.close();
		   return true; /* successfully loaded history */
			
	   }
	   catch(StackOverflowError ex)
	   {
		   throw new ModuleException("Need more stack size to load history: "+ex.toString(),"INCRE");
	   }
	   catch(FileNotFoundException fne){
	   		Debug.out(Debug.MODE_DEBUG, "INCRE","Cannot find history thus INCRE is not ");
	   		return false;
	   }
	   catch(Exception ex){
		   Debug.out(Debug.MODE_WARNING, "INCRE","Failed to load history: "+ex.toString());
		  return false;
	   }
   }
   
   /**
    * Stores the current repository to a specified file. Uses objectoutputstream.
    * @roseuid 4209F75B0186
    */
   public void storeHistory() throws ModuleException
   {
	   Debug.out(Debug.MODE_DEBUG, "INCRE","Comparator made "+comparator.getCompare()+" comparisons");
	   
	   DataStore ds = DataStore.instance();
	   //CommonResources resources = (CommonResources)ds.getObjectByID(Master.RESOURCES_KEY);
	   
	   Configuration config = Configuration.instance();
	   String incre_enabled = "";
   	   Module m = config.moduleSettings.getModule("INCRE");
   	   if(m!=null){
   		   incre_enabled = m.getProperty("enabled");
   	   }
	   //if (!"True".equalsIgnoreCase(resources.ProjectConfiguration.getProperty("INCRE_ENABLED"))) {
	   //		return;
	   //}
   	   if (!"True".equalsIgnoreCase(incre_enabled)) {
	   		return;
	   }
		
	   try
	   {
		   FileOutputStream fos = new FileOutputStream(this.historyfile);
		   BufferedOutputStream bos = new BufferedOutputStream(fos);
		   ObjectOutputStream oos = new ObjectOutputStream(bos);
		   		   
		   // write current date = last compilation date
		   oos.writeObject(new Date());
		  		   
		   // write project configurations
		   //oos.writeObject(ds.getObjectByID("config"));
		   oos.writeObject(Configuration.instance());
		   
		   // collect the objects
		   Object[] objects = ds.getAllObjects();
		   oos.writeInt(objects.length-1);
		   Debug.out(Debug.MODE_DEBUG, "INCRE",objects.length-1+" objects to store");
		   int stored = 0;
		   for(int k=1;k<objects.length;k++)
		   {
				// write objects
				if(objects[k]!=null)
				{
					oos.writeObject(objects[k]);
					oos.flush();
					stored++;
				}
		   }

		   Debug.out(Debug.MODE_DEBUG, "INCRE",stored+" objects have been stored");
		   oos.close();
       }
	   catch(StackOverflowError ex)
	   {
		   Debug.out(Debug.MODE_ERROR, "INCRE", "StackOverflow while creating incre history");
		   ex.printStackTrace(System.out);
		   // throw new ModuleException("Failure in writing: "+e.toString(),"INCRE");
	   }
	   catch(Exception e)
	   {
		   Debug.out(Debug.MODE_ERROR, "INCRE", "Exception occured during creation of incre history: "+e.getMessage());
		   e.printStackTrace(System.out);
	   }
	   catch(Throwable ee) 
	   {
		   ee.printStackTrace(System.out);
	   }
   }
}
