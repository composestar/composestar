/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: RealSourceManager.java,v 1.5 2006/03/14 10:07:18 pascal_durr Exp $
 */

package Composestar.Core.TYM.SrcCompiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.*;
import Composestar.Core.TYM.TypeLocations;
import Composestar.Utils.Debug;

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
		ArrayList projects = config.getProjects().getProjects();
		Iterator projIt = projects.iterator();
		while(projIt.hasNext()) {
			Project p = (Project)projIt.next();
			Language lang = p.getLanguage();
			CompilerSettings compsettings = lang.compilerSettings;
			LangCompiler comp = (LangCompiler)compsettings.getCompiler();
			
			String exec = Configuration.instance().getProjects().getProperty("Executable");
	        String exefile = "";
	        Iterator typesourcesit = p.getTypeSources().iterator();
	        while(typesourcesit.hasNext())
	        {
	        	TypeSource source = (TypeSource)typesourcesit.next();
	        	if(source.getName().equals(exec))
	        		exefile = source.getFileName();
	        }
			
			//set target of sources
			Iterator sourceIt = p.getSources().iterator();
			while( sourceIt.hasNext() ) {
				Source source = (Source)sourceIt.next();
				if(source.getFileName().equals(exefile))
					source.setIsExecutable(true);
				else
					source.setIsExecutable(false);
				String target = createTargetFile(source.getFileName(),source.isExecutable());
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
    
}

/*public interface RealSourceManager extends CTCommonModule {
    public abstract void compileSource(String sourceFile, String buildPath, String targetFile, String compilerOptions, String compilerPath, Compiler comp) throws ModuleException;
     
}*/
