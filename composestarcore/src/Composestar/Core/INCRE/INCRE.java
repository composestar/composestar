package Composestar.Core.INCRE;
 
import Composestar.Core.INCRE.Config.*;
import Composestar.Core.LAMA.*;
//import Composestar.dotnet.LAMA.*;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Master;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.RepositoryImplementation.DeclaredRepositoryEntity;
import Composestar.Core.TYM.TypeLocations;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.PlatformRepresentation;
import Composestar.Core.CpsProgramRepository.PrimitiveConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SimpleSelectorDef.PredicateSelector;
import Composestar.Utils.*;
import Composestar.Core.Exception.ModuleException;

import java.io.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;


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
	private boolean searchingHistory = false;
	
	private String historyfile = "";
	private Date lastCompTime = null;
	private MyComparator comparator;
	private ConfigManager configmanager = null;
	private INCREReporter reporter;
	
	/* for optimalization purposes */
	private HashMap filesChecked;   
	public HashMap externalSourcesBySource;
	private ArrayList currentConcernsWithFMO;
	private ArrayList historyConcernsWithFMO;
	
	public INCRE()
	{
		reporter = new INCREReporter();
		reporter.open();
		filesChecked = new HashMap();
		externalSourcesBySource = new HashMap();
	}
	
	public MyComparator getComparator(){
		return comparator;
	}
	
	public static INCRE instance()
	{
		if (Instance == null) 
		{
			Instance = new INCRE();
		}
		return (Instance);
	} 

	/**
    * @roseuid 41E7E74501C2
    */
   public void run(CommonResources resources) throws ModuleException 
   {
   	   // time this initialization process	
	   INCRETimer incretotal = this.getReporter().openProcess("INCRE","",INCRETimer.TYPE_ALL);
	   
	   // set configmanager to read xml configuration file */
	   configmanager = new ConfigManager(resources);
	   String configfile = resources.ProjectConfiguration.getProperty("TempFolder") + "INCREconfig.xml";
	   try
	   {
		   File file = new File(configfile);
		   if(!file.exists() && !file.canRead())
			   configfile = resources.ProjectConfiguration.getProperty("ComposestarPath") + "INCREconfig.xml";
	   }
	   catch(Exception ioe)
	   {
		   ioe.printStackTrace();
	   }
	   
	   this.historyfile = resources.ProjectConfiguration.getProperty("TempFolder") + "history.dat";
	 
	   /* parse the XML configuration file containing the modules 
	    	time the parsing process */
	   INCRETimer increparse = this.getReporter().openProcess("INCRE","Parsing configuration file",INCRETimer.TYPE_OVERHEAD);			
	   
	   try
	   {
		   configmanager.parseXML(configfile);
	   }
	   catch(Exception e)
	   {
		   increparse.stop();	
		   String error = e.getMessage();
		   if(error == null || "null".equals(error))
		   {
			   error = e.toString();
		   }
		   throw new ModuleException(error,"INCRE");
		}

	   /* shut down INCRE in case parsing fails! */
	   increparse.stop();	
	   
	   if(!Master.phase.equals("two")){
	   		// no need to load history in MASTER phase one
	   		this.enabled = false;
	   }
	   
	   //Need for development, ability to turn INCRE off
	   if("OFF".equalsIgnoreCase(resources.ProjectConfiguration.getProperty("INCRE_ENABLED")))
	   {
		   this.enabled = false;
	   }

	   if(this.enabled){	
	   		/* load data of previous compilation run (history)
	   		 	time the loading process */
	   		INCRETimer loadhistory = this.getReporter().openProcess("INCRE","Loading history",INCRETimer.TYPE_OVERHEAD);	
	   		this.enabled = this.loadHistory(historyfile);
	   		// shut down INCRE in case loading fails!
			loadhistory.stop();	
	   }
	   
	   // stop timing INCRE's initialization 
	   incretotal.stop();
	   
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
	
	public DataStore getCurrentRepository(){
		return this.currentRepository;
	}
	
	public ArrayList getConcernsWithFMO(){
		
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
		while ( iterConcerns.hasNext() )
		{
			Concern c = (Concern)iterConcerns.next();
			if ( c.getDynObject("SingleOrder") != null) 
			{
				concerns.add(c);	
			}	
		}
		
		if(searchingHistory)
			historyConcernsWithFMO = concerns;
		else 
			currentConcernsWithFMO = concerns;
		
		return concerns;
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
   		
			if(obj.getClass().equals(String.class))
			{
				// special case, return string
				return obj;
			}
   	    
			String type = obj.getClass().getName();
			
   			Iterator objIter = history.getAllInstancesOf(obj.getClass());
   			while( objIter.hasNext() )
			{
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
      
   public boolean isFileModified(String filename){
   		
   		if(filesChecked.containsKey(filename))
   			return ((Boolean)filesChecked.get(filename)).booleanValue();	
   		else { 
   			File f = new File(filename);  			
   			boolean modified = isFileModified(f);
   			filesChecked.put(filename,new Boolean(modified));
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
		if(!file.exists())
			return true;
		
		boolean modified = true;
		try {
			modified= (file.lastModified() > lastCompTime.getTime());
		}
		catch(Exception e){return modified;}
		return modified;
	}	
   
   /**
    * @return boolean
    * @roseuid 41EE335602EE
    */
	public boolean isModuleInc(String name)
	{
		if(enabled){
			if(configmanager.getModuleByID(name)!=null)
			{
				Module m = configmanager.getModuleByID(name);
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
	 * Returns all primitive concerns from unmodified assemblies
	 * @param ds Datastore to search
	 * @return
	 */
	public ArrayList getAllModifiedPrimitiveConcerns(DataStore ds) {
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
	        	 if(sourceFile!=null && !incre.isFileModified(sourceFile)){
	        	 	/*skip because sourcefile unmodified*/
	        	 }
	        	 // TODO: Check this
	        	 //else if(!incre.isFileModified(dtype.Module.FullyQualifiedName)){
	        	 	/*skip because assembly unmodified*/
	        	 //}
	        	 else list.add(pc); /* safety first */
	        }
	        else 
	           	list.add(pc);
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
   		Object newobject = input;
		Object historyobject = null;
		Object depofnewobject = null;
		Object depofhistoryobject = null; 
		boolean processed = false;
		INCRETimer overhead = getReporter().openProcess(modulename,"INCRE::isProcessedBy("+input+")",INCRETimer.TYPE_OVERHEAD);
	
   		if(!isModuleInc(modulename))
   			return false;
   		 
   		Module mod = configmanager.getModuleByID(modulename);
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

			Iterator deps = mod.getDeps();
			while(deps.hasNext())
			{
				currentRepository = DataStore.instance();
				searchingHistory = false;
				Dependency dep = (Dependency)deps.next();
				//System.out.println("Checking dependency "+dep.getName());
				depofnewobject = (Object)dep.getDepObject(newobject);
				if(dep instanceof FileDependency)
				{
					// check if file(s) have been modified
					// stop process when file has been modified
					ArrayList files = (ArrayList)depofnewobject;
					Iterator fileItr = files.iterator();
					while(fileItr.hasNext())
					{
						if(isFileModified((String)fileItr.next()))
						{
							overhead.stop();
							return false;	
						}
					}
				}
				else 
				{
					if(dep.lookup && depofnewobject!=null && comparator.comparisonMade(dep.getName()+depofnewobject.hashCode()))
					{
						// the dependency has been checked before
						// return the result of the previous comparison if false
						//System.out.println("DEP already made: "+dep.name);
						boolean modified = !comparator.getComparison(dep.getName()+depofnewobject.hashCode());
						if(modified){
							overhead.stop();
							return false;
						}
					}
					else 
					{
						// find object in history
						if(historyobject==null)
							historyobject = findHistoryObject(newobject);
											
						if(historyobject!=null)
						{
							// get dependent object of the 'history' object
							currentRepository = history;
							searchingHistory = true;
							depofhistoryobject = (Object)dep.getDepObject(historyobject);
							
							// compare both dependent objects for modification
							boolean modified = !comparator.compare(depofnewobject,depofhistoryobject);
							
							// add the result to comparator's map
							if(dep.store && depofnewobject!=null)
								comparator.addComparison(dep.getName()+depofnewobject.hashCode(),!modified);
				
							// stop calculation when object has been modified
							if(modified){
								overhead.stop();
								return false;
							}
						}
						else 
						{
							// history of input object cannot be found => input has not been processed
							overhead.stop();
							return false;
						}
					}
				}
			} // next dependency	
		}
		else 
		{
			throw (new ModuleException("INCRE cannot find module "+modulename+"!"));
		}

		// all dependencies have not been modified => input has already been processed
		overhead.stop();
		return true;
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
		   history.addObject("config",ois.readObject());
		   
		   int numberofobjects = ois.readInt();	
		   for(int i=0;i<numberofobjects;i++)
		   {
			   try 
			   {
				    history.addObject(ois.readObject());
			   }
			   catch(EOFException ex){
					Debug.out(Debug.MODE_WARNING, "INCRE","End of file exception occurred");
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
	   if(!enabled) return;
	   Debug.out(Debug.MODE_DEBUG, "INCRE","Comparator made "+comparator.getCompare()+" comparisons");
	   	   
	   try
	   {
		   FileOutputStream fos = new FileOutputStream(this.historyfile);
		   BufferedOutputStream bos = new BufferedOutputStream(fos);
		   ObjectOutputStream oos = new ObjectOutputStream(bos);
		   DataStore ds = DataStore.instance();
		   
		   // write current date = last compilation date
		   oos.writeObject(new Date());
		   
		   // write project configurations
		   oos.writeObject(ds.getObjectByID("config"));
		   		   
		   // collect the objects
		   Object[] objects = ds.getAllObjects();
		   oos.writeInt(objects.length-1);
		   Debug.out(Debug.MODE_DEBUG, "INCRE","Objects to store: "+objects.length);
		   int stored = 0;
		   for(int k=1;k<objects.length;k++)
		   {
			    // write objects
			   //try 
			   //{
				   //System.out.println("Writing object "+k);
				   if(objects[k]!=null)
				   {
						oos.writeObject(objects[k]);
						oos.flush();
						stored++;
				   }
			   //}
			   /*catch(NullPointerException npe)
			   {
			   		
			   	   Debug.out(Debug.MODE_WARNING, "INCRE","NULL value for "+objects[k].getClass().getName()); 
				   if(objects[k]!=null)
				   	System.out.println(((PrimitiveConcern)objects[k]).name);
			   	   continue;
			   }*/
		   }
		   Debug.out(Debug.MODE_DEBUG, "INCRE","Objects stored: "+stored);

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
