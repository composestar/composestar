/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: DotNETRealSourceManager.java,v 1.1 2006/02/16 23:11:01 pascal_durr Exp $
 */

package Composestar.DotNET.TYM.SrcCompiler;

import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.INCRETimer;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Master;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.TYM.TypeLocations;
import Composestar.Core.TYM.SrcCompiler.Compiler;
import Composestar.Core.TYM.SrcCompiler.CompilerException;
import Composestar.Core.TYM.SrcCompiler.CompilerFactory;
import Composestar.Core.TYM.SrcCompiler.RealSourceManager;

import Composestar.Utils.Debug;
import Composestar.Utils.StringConverter;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.PrimitiveConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.CompiledImplementation;
import Composestar.Core.Exception.ModuleException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 * Takes care of compiling the real user sources. Links with the dummies and takes 
 * care not to destroy them during compilation.
 */
public class DotNETRealSourceManager implements RealSourceManager  {
    private Vector deps = new Vector ();
    
    private TypeLocations typeLocations;
    
    private ArrayList builtAssemblies = new ArrayList ();
    
    protected List Assemblies = new ArrayList();
    
    protected Properties ProjectConfig;
    
    /**
     * Default ctor
     * @roseuid 401BCC5F03B9
     */
    public DotNETRealSourceManager() {
     	typeLocations = TypeLocations.instance();
    }
    
    /**
     * Compiles a real source and makes sure that the dummies are not changed.
     * @param sourceFile
     * @param buildPath
     * @param targetFile
     * @param compilerOptions
     * @param compilerPath
     * @param comp
     * @throws Composestar.core.Exception.ModuleException
     * @roseuid 401D4671022E
     */
    public void compileSource(String sourceFile, String buildPath, String targetFile, String compilerOptions, String compilerPath, Compiler comp) throws ModuleException {
              
		INCRE incre = INCRE.instance();
		
		String[] source = new String[1];
		source[0] = '\"' + sourceFile.replaceAll("/","\\\\\\\\")+ '\"';

		typeLocations.setSourceAssembly(sourceFile, targetFile);

		// String list of deps
		String[] depends = getDeps();

		// convert from source to dll output.
		try
		{
			String outputPath = ProjectConfig.getProperty( "OutputPath", "ERROR" );
			if( "ERROR".equals(outputPath) )
			{
				throw new ModuleException( "Error in configuration file: No such property \""
					+ "ouputPath\"" );
			}
			File finaltarget = new File(outputPath + targetFile);
			File buildtarget = new File(buildPath + targetFile);
						
			if(!incre.isProcessedByModule(sourceFile,"RECOMA") || !buildtarget.exists() || !finaltarget.exists() )
			{
				// INCRE has decided that we still need to recompile the source
				Debug.out(Debug.MODE_DEBUG, "RECOMA","compiling "+sourceFile);
				INCRETimer compsource = incre.getReporter().openProcess("RECOMA",sourceFile,INCRETimer.TYPE_NORMAL);
				Debug.out(Debug.MODE_DEBUG, "RECOMA","compiling " + source[0] +  "  " + buildPath + "\\" + targetFile + "  "+ targetFile.endsWith( "dll" ));
				Debug.out(Debug.MODE_DEBUG, "RECOMA","depends " + depends[0].toString()  + " co: " + compilerOptions + " compilerpath " + compilerPath);
				comp.compile( source, '\"' + buildPath + targetFile + '\"', targetFile.endsWith( "dll" ),
					depends, compilerOptions, compilerPath );
				builtAssemblies.add('\"' + buildPath + targetFile + '\"');
				compsource.stop();
			}
			else 
			{
				// INCRE has decided that we don't have to recompile this source
				Debug.out(Debug.MODE_DEBUG, "INCRE","No need to recompile "+sourceFile);
				INCRETimer skipsource = incre.getReporter().openProcess("RECOMA",sourceFile,INCRETimer.TYPE_INCREMENTAL);
				if(targetFile.endsWith(".exe"))
					DataStore.instance().addObject("Executable",'\"' + buildPath + targetFile + '\"');
				skipsource.stop();
			}
		} 
		catch(CompilerException e ) 
		{
			// output compiler error here, and throw ModuleException
			// System.out.println( comp.getOutput() );
			// System.err.println(e.getMessage());
			 
			//System.out.println(">> " + e.getStackTrace()[0].toString() );
			//System.out.println(">> " + e.getStackTrace()[1].toString() );
			throw new ModuleException( "Compilation error: " + e.getMessage() , "RECOMA");
		} 
    }
    
    /**
     * Perform one compilation run of the project. Normally called by Master.
     * 
     * @param resources Common resources
     * @throws Composestar.core.Exception.ModuleException Thrown on fatal errors 
     * in this module
     * @roseuid 401BCC860131
     */
    public void run(CommonResources resources) throws ModuleException {
        ProjectConfig = resources.ProjectConfiguration;
        // fetch compilers
        String compilerString = ProjectConfig.getProperty( "Compilers", "ERROR" );
        if( "ERROR".equals(compilerString) ) {
            // throw module exception.. can't continue..
            throw new ModuleException( "Error in configuration file: No such property \"Compilers\"" );
        }

        start( resources );
        // compile each of the sources
        Iterator compIt = StringConverter.stringToStringList( compilerString );
        while( compIt.hasNext() ) {
            String prefix = (String)compIt.next();
            prefix = prefix.trim();
            handleCompiler( prefix );
        }
        finish( resources );     
    }
    
    /**
     * Handles all the sources for one specific compiler.
     * 
     * @param compilerPrefix The prefix used in the configuration file for this 
     * compiler target.
     * @throws Composestar.core.Exception.ModuleException Thrown if
     * @roseuid 401BCC860124
     */
    private void handleCompiler(String compilerPrefix) throws ModuleException {
        // fetch the compiler to use
    	String sourceFile ="";
        String compilerID = ProjectConfig.getProperty( compilerPrefix + "Compiler", "ERROR" );

        if( "ERROR".equals(compilerID) ){
            throw new ModuleException( "Error in configuration file: No such property \""
                                       + compilerPrefix +"Compiler\"" );
        }
        // fetch path to compiler
        String compilerPath = ProjectConfig.getProperty( compilerPrefix + "CompilerPath", "" );

        // fetch optional switches, may be empty or none existent
        String compilerOptions = ProjectConfig.getProperty( compilerPrefix + "CompilerOptions", "" );
        // fetch sources to compile
        String sourcesString = ProjectConfig.getProperty( compilerPrefix + "Sources", "ERROR" );
        if( "ERROR".equals(sourcesString) ) {
            throw new ModuleException( "Error in configuration file: No such property \""
                                       + compilerPrefix +"Sources\"" );
        }
        Iterator sourceIt = StringConverter.stringToStringList( sourcesString );
        // fetch output path
        String buildPath = ProjectConfig.getProperty( "BuildPath", "ERROR" );
        if( "ERROR".equals(buildPath) ) {
            throw new ModuleException( "Error in configuration file: No such property BuildPath" );
        }

        CompilerFactory compFactory = CompilerFactory.instance();
        Compiler comp = compFactory.createCompiler( compilerID );
        if( comp == null ) {
            throw new ModuleException( "The requested compiler " + compilerID + " is not available." );
        }
		
		String exeSource = ProjectConfig.getProperty( compilerPrefix + "ExecSource", "" );
		char[] chars = exeSource.toCharArray();
		for( int i=0; i < chars.length; i++)
		{
			if(chars[i] == '/')
			{
				chars[i] = '\\';
			}
		}
		
        // fetch the compiler to use.
        while( sourceIt.hasNext() ) {
            sourceFile = (String)sourceIt.next();

			if( sourceFile.equals(exeSource))
				continue;
            // strip whitespace from sourceFile
            sourceFile = sourceFile.trim();
			//String targetFile = sourceFile.substring(sourceFile.lastIndexOf("/")+1);
			//targetFile = targetFile.replaceAll( "\\.\\w+", ".dll" );
            String targetFile = createTargetFile(sourceFile,false);
            compileSource( sourceFile, buildPath, targetFile, compilerOptions, compilerPath, comp );
        }

        // finally compile the exe if any

        if( !"".equals(exeSource) ) {
            exeSource = exeSource.trim();
			//String targetFile = exeSource.substring(exeSource.lastIndexOf("/")+1);
            //targetFile = targetFile.replaceAll( "\\.\\w+", ".exe" );
            String targetFile = createTargetFile(exeSource,true);
            compileSource( exeSource, buildPath, targetFile, compilerOptions, compilerPath, comp );
        }
    }
    
    /**
     * Converts sourcefile to a compilation targetfile
     * @param sourcefile
     * @param isExec whether sourcefile contains executable
     * @return
     */
    public static String createTargetFile(String sourcefile,boolean isExec){
    	
    	String targetFile = "";
    	// convert / to \ because of build.ini format
    	String source = sourcefile.replace('/','\\'); 
    	
    	/* last part of sourcefile's path, without extension
    	 * e.g C:/pacman/Main.jsl => Main
    	 */
    	String srcType = source.substring(source.lastIndexOf("\\")+1);
    	srcType = srcType.replaceAll("\\.\\w+", "");
    	
    	TypeLocations locations = TypeLocations.instance();
    	ArrayList types = locations.getTypesBySource(source);
    	
    	// iterates over typesources to find type with full namespace
    	Iterator typesItr = types.iterator();
    	while(typesItr.hasNext()){
    		  String type = (String)typesItr.next();
    		  String[] elems = type.split("\\.");
    		  ArrayList list = new ArrayList(Arrays.asList(elems));
    		  if(list.contains(srcType)){
    		  	targetFile = type;
    		  	break; // found full namespace
    		  }
    	}
    	
    	if(targetFile.equals("")) { // full namespace not found  
    		if(!types.isEmpty()){
    			targetFile = (String)types.get(0); // first type declared in sourcefile
    		}
    		else {
    			Debug.out(Debug.MODE_WARNING, "RECOMA",srcType+" is not a fully qualified target of source "+sourcefile);
    			targetFile = srcType; // last part of sourcefile's path
    		}
    	}   
    	
    	// finish by adding .dll or .exe 
    	if(isExec)
    		targetFile += ".exe";
    	else
    		targetFile += ".dll";
    	
    	Debug.out(Debug.MODE_DEBUG, "RECOMA","Target of "+sourcefile+" set to "+targetFile); 
    	return targetFile; 
    }
    
    /**
     * @param resources
     * @roseuid 404F328B039A
     */
    public void start(CommonResources resources) {
      	 String assemblies = resources.ProjectConfiguration.getProperty("Assemblies");
      	 String[] assArray = assemblies.split(",");
      	 for( int i = 0; i < assArray.length; i++ )
      	 {
      	   if (!"".equals(assArray[i]))
      	 	deps.add('\"' + assArray[i] + '\"');
      	 }
      	 
      	 String dependencies = resources.ProjectConfiguration.getProperty("Dependencies");
      	 String[] depArray = dependencies.split(",");
      	 for( int i = 0; i < depArray.length; i++ )
      	 {
      	 	//if( depArray[i].indexOf("Microsoft.NET") == -1 )
      	   if (!"".equals(depArray[i]))
      	 		deps.add(depArray[i]);
      	 }
    }
    
    /**
     * @param resources
     * @roseuid 40FD28650122
     */
    public void finish(CommonResources resources) {
    	resources.addResource("BuiltAssemblies", builtAssemblies);
    }
    
    /**
     * @return java.lang.String[]
     * @roseuid 40FD2865018F
     */
    public String[] getDeps() {
    	Object[] depends = deps.toArray();
    	String[] theDeps = new String[depends.length];
    	int n = depends.length-1;
    	for( int i = 0; i < depends.length; i++ )
    	{
    		theDeps[n-i] = (String) depends[i];
    	}
    	return theDeps;     
    }

    /**
	 * @param src Absolute path of sourcefile
	 * @return Arraylist containing the filenames of all external linked sources
	 */
	public ArrayList externalSources(String src) throws ModuleException
	{ 
		INCRE incre = INCRE.instance();
		ArrayList extSources = new ArrayList();
		ArrayList asmReferences = new ArrayList();
		String line = "";
		
		DataStore ds = DataStore.instance();
		CommonResources resources = (CommonResources)ds.getObjectByID(Master.RESOURCES_KEY);
		String buildPath = resources.ProjectConfiguration.getProperty( "BuildPath", "ERROR" );
		if(buildPath.equals("ERROR"))
		{
			return extSources;
		}

		//	step 1: open il code of source
		String ilFile = createTargetFile(src,false);
		ilFile = ilFile.replaceAll( ".dll", ".il" );
		ilFile = buildPath + ilFile;
		
		// step 2: extract all external assemblies
		BufferedReader in = null;
		try
		{
			in = new BufferedReader( new InputStreamReader( new FileInputStream( ilFile ) ) );
		}
		catch( FileNotFoundException e ) 
		{
			throw new ModuleException( "Cannot read " + ilFile, "RECOMA" );
		}

		try
		{
			while( (line=in.readLine()) != null )
			{
				// read the lines
				if( line.trim().startsWith( ".assembly extern" ) ) 
				{
					// get name of external assembly
					String[] elems = line.split( " " );
					String asmref = elems[elems.length-1];
					asmReferences.add(asmref);
				}
			}
			in.close();
		}
		catch(IOException ioexc){throw new ModuleException("Error occured while reading "+ilFile);}
		
		// step 3: convert external assemblies to user sources on disk
		TypeLocations locations = TypeLocations.instance();
		Iterator refs = asmReferences.iterator();
		while(refs.hasNext())
		{
			String ref = (String)refs.next();
			String source = locations.getSourceByType(ref);
			if(source!=null)
				extSources.add(source);
		}
			
		incre.externalSourcesBySource.put(src,extSources);
		return extSources;
	}

	/**
	 * @param src Absolute path of a sourcefile
	 * @return ArrayList containing modified signatures (signatures with ADDED/REMOVED methodwrappers)
	 * of concerns extracted from external linked source files
	 */
	public ArrayList fullSignatures(String src) throws ModuleException
	{ 
		INCRE incre = INCRE.instance();
     	DataStore ds = incre.getCurrentRepository();
		ArrayList extSources = new ArrayList();
		ArrayList signatures = new ArrayList();
		ArrayList concernsToCheck = new ArrayList();
		HashSet concernsCheckedByKey = new HashSet();
				
		/* first gather all concerns with SingleOrder */
		concernsToCheck = incre.getConcernsWithFMO();
		
		/* add full signatures of src */
		/* When compiling a source the compiler does not use 
		 * the modified signature from its dummy source
		Iterator concerns = concernsToCheck.iterator();
		while ( concerns.hasNext() )
		{
			Concern c = (Concern)concerns.next();
			if(incre.declaredInSource(c,src))
			{
				signatures.add(c.getSignature());
				concernsCheckedByKey.add(c.getQualifiedName());
			}
		}*/
		
		if(!concernsToCheck.isEmpty())
		{	
			/* add full signatures of external linked sources */
			extSources = (ArrayList)incre.externalSourcesBySource.get(src);
			if(extSources == null)		
				extSources = this.externalSources(src);
			
			Iterator externals = extSources.iterator();
			while(externals.hasNext()){
				String external = (String)externals.next();
				Iterator conIter = concernsToCheck.iterator();
				while ( conIter.hasNext() )
				{
					Concern c = (Concern)conIter.next();
					
					if(incre.declaredInSource(c,external))
					{
						if(!concernsCheckedByKey.contains(c.getQualifiedName()))
						{
							signatures.add(c.getSignature());
							concernsCheckedByKey.add(c.getQualifiedName());
						}
					}
				}
			}
		}
		
		return signatures;
	}
	
	/**
	 * @param src Absolute path of a sourcefile 
	 * @return ArrayList containing all concerns with FMO and extracted from 
	 * the source and its external linked sources
	 */
	public ArrayList getConcernsWithFMO(String src)
	{
		INCRE incre = INCRE.instance();
		ArrayList concerns = new ArrayList();
		ArrayList concernsWithFMO = incre.getConcernsWithFMO();
		ArrayList sources = (ArrayList)incre.externalSourcesBySource.get(src);
		sources.add(0,src);
		
		if(!concernsWithFMO.isEmpty()){
			Iterator iterConcerns = concernsWithFMO.iterator();

			while( iterConcerns.hasNext() )
			{
				Concern c = (Concern)iterConcerns.next();
				if(incre.declaredInSources(c,sources)){
					concerns.add(c.getQualifiedName());
				}
			}
		}
		
		return concerns;
	}
	
	/**
	 * @param src Absolute path of a sourcefile 
	 * @return ArrayList containing all concerns recognized as a casting interception 
	 * Only concerns extracted from the source and its external linked sources are returned
	 */
	public ArrayList castingInterceptions(String src) throws ModuleException
	{
		ArrayList list = new ArrayList();
     	INCRE incre = INCRE.instance();
     	DataStore ds = incre.getCurrentRepository();
     	ArrayList concernsWithFMO = incre.getConcernsWithFMO();
		ArrayList sources = (ArrayList)incre.externalSourcesBySource.get(src);
		sources.add(0,src);
     	
		if(!concernsWithFMO.isEmpty())
		{
     		Iterator iterConcerns = concernsWithFMO.iterator();
			while ( iterConcerns.hasNext() )
			{
				Concern c = (Concern)iterConcerns.next();
				boolean castConcern = false;
				
				if(incre.declaredInSources(c,sources))
				{
					FilterModuleOrder fmo = (FilterModuleOrder)c.getDynObject("SingleOrder");
			
					Iterator iterFilterModules = fmo.orderAsList().iterator();
					while ( iterFilterModules.hasNext() )
					{
						String fmref = (String)iterFilterModules.next();
						FilterModule fm = (FilterModule) ds.getObjectByID(fmref);
				  	  			
						Iterator iterInternals = fm.getInternalIterator() ;
						while ( iterInternals.hasNext() )
						{
							Internal internal = (Internal)iterInternals.next();
							if ( !list.contains(internal.type.getQualifiedName()) )
							{
								castConcern = true;
								list.add( internal.type.getQualifiedName() );
							}
						}
					}
								  	  		
					if ( castConcern ) 
					{
						if(!list.contains( c.getQualifiedName() ))
							list.add( c.getQualifiedName() );
					}
				}
			}
		}
		
		return list;
	}

	/**
	 * @param src Absolute path of a sourcefile 
	 * @return ArrayList containing all concerns which instantiation should be intercepted 
	 * Only concerns extracted from the source and its external linked sources are returned
	 */
	public ArrayList getAfterInstantiationClasses(String src) throws ModuleException 
	{
		INCRE incre = INCRE.instance();
		ArrayList result = new ArrayList();
		ArrayList sources = (ArrayList)incre.externalSourcesBySource.get(src);
		sources.add(0,src);
		
		DataStore ds = incre.getCurrentRepository();
    	//Iterator it = ds.getAllInstancesOf(CompiledImplementation.class);
		Iterator it = incre.getAllInstancesOfOrdered(CompiledImplementation.class);
		while(it.hasNext())
		{
			CompiledImplementation ci = (CompiledImplementation)it.next();
			String className = ci.getClassName();
			if(className != null)
				result.add(className);
		}
		
		//it = ds.getAllInstancesOf(CpsConcern.class);
		it = incre.getAllInstancesOfOrdered(CpsConcern.class);
		while (it.hasNext()) {
			CpsConcern c = (CpsConcern)it.next();
			Object o = c.getDynObject("IMPLEMENTATION");
			if ( o != null) {
				
				PrimitiveConcern pc = (PrimitiveConcern)o;
				result.add(pc.getQualifiedName());
			}
		}
		
		//it = ds.getAllInstancesOf(Concern.class);
		it = incre.getAllInstancesOfOrdered(Concern.class);
		while (it.hasNext()) {
			Concern c = (Concern)it.next();
			if(incre.declaredInSources(c,sources)){
				if(c.getDynObject("superImpInfo") != null && !(c instanceof CpsConcern))
				{
						result.add(c.getQualifiedName());
				}
			}
		}
			
		return result;
	}

	/**
	 * @param src Absolute path of a sourcefile 
	 * @return ArrayList containing all concerns with outputfilter(s) 
	 * Only concerns extracted from the source and its external linked sources are returned
	 */
	public ArrayList getConcernsWithOutputFilters(String src) throws ModuleException
	{
		ArrayList concerns = new ArrayList();
		INCRE incre = INCRE.instance();
		DataStore ds = incre.getCurrentRepository();
		ArrayList concernsWithFMO = incre.getConcernsWithFMO();
		ArrayList sources = (ArrayList)incre.externalSourcesBySource.get(src);
		sources.add(0,src);
		
		if(!concernsWithFMO.isEmpty()){
     	Iterator iterConcerns = concernsWithFMO.iterator();
			while ( iterConcerns.hasNext() )
			{
				Concern c = (Concern)iterConcerns.next();
				if(incre.declaredInSources(c,sources)){
					FilterModuleOrder fmo = (FilterModuleOrder)c.getDynObject("SingleOrder");
			
					Iterator iterFilterModules = fmo.orderAsList().iterator();
					while ( iterFilterModules.hasNext() )
					{
						FilterModule fm = (FilterModule) ds.getObjectByID((String)iterFilterModules.next());
			
						if ( !fm.outputFilters.isEmpty() )
							concerns.add( c.getQualifiedName() );
					}
				}
			} 
		}

		return concerns;
	}
}
/**
 * void RealSourceManager.start(Composestar.core.Master.CommonResources){
 * 
 * }
 * void RealSourceManager.main(java.lang.String[]){
 * DummyManager dummy = new DummyManager( 1 );
 * DummyManager dummy2 = new DummyManager( 2 );
 * RealSourceManager real = new RealSourceManager();
 * // fetch project configuration file
 * Properties projectConfig = new Properties();
 * CommonResources resources = new CommonResources();
 * try {
 * projectConfig.load( new FileInputStream( "projectTest/project.ini" ) );
 * resources.ProjectConfiguration = projectConfig;
 * } catch( IOException e ) {
 * System.out.println( "Got IO exception: " + e.getMessage() );
 * }
 * try{
 * System.out.println( "##### FIRST #######" );
 * dummy.run( resources );
 * System.out.println( "##### SECOND #######" );
 * dummy2.run( resources );
 * System.out.println( "##### FINAL #######" );
 * real.run( resources );
 * } catch( ModuleException e ) {
 * System.out.println( "Got ModuleException: " + e.getMessage() );
 * }
 * }
 */
