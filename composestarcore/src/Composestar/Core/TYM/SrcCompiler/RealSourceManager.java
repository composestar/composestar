/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: RealSourceManager.java,v 1.1 2006/02/16 23:04:07 pascal_durr Exp $
 */

package Composestar.Core.TYM.SrcCompiler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.*;
import Composestar.Core.Master.Master;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.TYM.TypeLocations;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;
//import Composestar.Core.Exception.*;

import Composestar.Core.COMP.*;

/**
 * Takes care of compiling the real user sources. Links with the dummies and takes 
 * care not to destroy them during compilation.
 */

public class RealSourceManager implements CTCommonModule {
	
	ArrayList compiledSources;
	
	public RealSourceManager() {
		compiledSources = new ArrayList();
	}
	
	public void run(CommonResources resources) throws ModuleException {
		//compile sources of project
		Configuration config = Configuration.instance();
		ArrayList projects = config.projects.getProjects();
		Iterator projIt = projects.iterator();
		while(projIt.hasNext()) {
			Project p = (Project)projIt.next();
			Language lang = p.getLanguage();
			CompilerSettings compsettings = lang.compilerSettings;
			LangCompiler comp = (LangCompiler)compsettings.getCompiler();
			
			//set target of sources
			Iterator sourceIt = p.getSources().iterator();
			while( sourceIt.hasNext() ) {
				Source source = (Source)sourceIt.next();
				String target = this.createTargetFile(source.getFileName(),source.isExecutable());
				source.setTarget(target);
			}
			
			try {
				comp.compileSources(p);
			}
			catch(Composestar.Core.COMP.CompilerException e) {
				throw new ModuleException( "Compilation error: " + e.getMessage() , "RECOMA");
			}
		}
		
		finish(resources);
	}
	
	/**
     * @param resources
     * @roseuid 40FD28650122
     */
    public void finish(CommonResources resources) {
       	resources.addResource("CompiledSources", compiledSources);
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
    	
    	return targetFile; 
    }
    
	/**
	 * @param src Absolute path of sourcefile
	 * @return Arraylist containing the filenames of all external linked sources
	 */
	public ArrayList externalSources(String src) throws ModuleException
	{ 
		/*
		INCRE incre = INCRE.instance();
		ArrayList extSources = new ArrayList();
		ArrayList asmReferences = new ArrayList();
		String line = "";
		
		DataStore ds = DataStore.instance();
		CommonResources resources = (CommonResources)ds.getObjectByID(Master.RESOURCES_KEY);
		String buildPath = resources.ProjectConfiguration.getProperty( "BuildPath", "ERROR" );
		
		//	step 1: open il code of source
		String targetFile = createTargetFile(src,false);
		String ilFile = targetFile.replaceAll( ".dll", ".il" );
		ilFile = buildPath + "Weaver\\" + ilFile;
		
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
			
		incre.externalSourcesBySource.put(FileUtils.removeExtension(buildPath+targetFile),extSources);
		return extSources;
		*/
		
		return null;
	}

	/**
	 * @param src Absolute path of a sourcefile
	 * @return ArrayList containing modified signatures (signatures with ADDED/REMOVED methodwrappers)
	 * of concerns extracted from external linked source files
	 */
	public ArrayList fullSignatures(String src) throws ModuleException
	{ 
		/*
		INCRE incre = INCRE.instance();
     	ArrayList extSources = new ArrayList();
		ArrayList signatures = new ArrayList();
		ArrayList concernsToCheck = new ArrayList();
		HashSet concernsCheckedByKey = new HashSet();
		
		DataStore ds = DataStore.instance();
		CommonResources resources = (CommonResources)ds.getObjectByID(Master.RESOURCES_KEY);
		String buildPath = resources.ProjectConfiguration.getProperty( "BuildPath", "ERROR" );
		
		//concernsToCheck = incre.getConcernsWithFMO();
		concernsToCheck = incre.getConcernsWithModifiedSignature();
		
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
		/*
		if(!concernsToCheck.isEmpty())
		{*/	
			/* add full signatures of external linked sources */
			/*String target = buildPath+createTargetFile(src,false);
			extSources = (ArrayList)incre.externalSourcesBySource.get(FileUtils.removeExtension(target));
			if(extSources == null){		
				extSources = this.externalSources(src);
			}
					
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
		
		return signatures;*/
		
		return null;
		
	}
}

/*public interface RealSourceManager extends CTCommonModule {
    public abstract void compileSource(String sourceFile, String buildPath, String targetFile, String compilerOptions, String compilerPath, Compiler comp) throws ModuleException;
     
}*/
