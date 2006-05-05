/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright   2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: ILICIT.java,v 1.19 2006/04/20 11:02:15 roy_ Exp $
 */


package Composestar.DotNET.ILICIT;
/*
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: ILICIT.java,v 1.19 2006/04/20 11:02:15 roy_ Exp $
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import Composestar.Core.WEAVER.*;
import Composestar.Utils.CommandLineExecutor;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.PrimitiveConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.CompiledImplementation;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Module;
import Composestar.Core.RepositoryImplementation.DataStore;

import java.util.ArrayList;
import java.util.Iterator;

public class ILICIT implements WEAVER {

	public static final String version = "$Revision: 1.19 $";
	
    public void run(CommonResources resources) throws ModuleException {
     Configuration config = Configuration.instance();
     PrintWriter out = null; 
     String binPath = config.getPathSettings().getPath("Composestar");
	 //String tempPath = resources.ProjectConfiguration.getProperty("TempFolder");
     String tempPath = config.getPathSettings().getPath("Base");
	 //String buildPath = resources.ProjectConfiguration.getProperty("BuildPath");
     //String buildPath = config.projects.getProperty("outputPath");
	 String weavePath = tempPath + "obj/Weaver";
	 String peweaver = binPath + "binaries/peweaver.exe";
	 String weavefile = "\"" + tempPath + "weavespec.xml" + '\"';
	      
     //ArrayList compiledSources = (ArrayList) resources.getResource("CompiledSources");
	 //ArrayList compiledSources = Configuration.instance().getLibraries().getLibraries();
	 ArrayList compiledSources = Configuration.instance().getProjects().getCompiledSources();
	 ArrayList builtAssemblies = new ArrayList();
	 ArrayList toBeWoven = new ArrayList();
	 
	 // make a copy of the built binaries 
	 Iterator binItr = compiledSources.iterator();
	 File weaveDir = new File(weavePath);
	 if(!weaveDir.exists()) weaveDir.mkdir(); /* create directory */
	 while(binItr.hasNext()){
		 String asm = (String)binItr.next();
		 File source = new File(asm);
		 String target = FileUtils.fixFilename(weavePath+File.separator+source.getName());
		 if(!INCRE.instance().isProcessedByModule(asm,"ILICIT")){
			 Debug.out(Debug.MODE_DEBUG,"ILICIT","Copying "+asm+" to Weaver directory...");
			 FileUtils.copyFile(target,source.getAbsolutePath());
			 String pdbFileName = FileUtils.removeExtension(source.getAbsolutePath()) + ".pdb";
			 if(FileUtils.fileExist(pdbFileName)){
				 Debug.out(Debug.MODE_DEBUG,"ILICIT","Copying "+pdbFileName+" to Weaver directory...");
				 File pdb = new File(pdbFileName);
				 FileUtils.copyFile(FileUtils.removeExtension(target) + ".pdb",pdb.getAbsolutePath());
			 }
			 builtAssemblies.add(target);
			 toBeWoven.add(target);
		 }
		 else {
			 // no need to weave the file
			 Debug.out(Debug.MODE_DEBUG,"INCRE","No need to re-weave "+asm);
			 builtAssemblies.add(target);
		 }
	 }
	 
	 DataStore.instance().addObject("BuiltLibs", builtAssemblies);
	 
	 //also copy dummies 
	 ArrayList dummies = Configuration.instance().getProjects().getCompiledDummies();
	 Iterator dumIt = dummies.iterator();
	 while(dumIt.hasNext()) 
	 {  
			String asm = FileUtils.prepareCommand((String)dumIt.next());
			File asmFile = new File(asm);
			Debug.out(Debug.MODE_DEBUG,"ILICIT","Copying "+asm+" to Weaver directory...");
			FileUtils.copyFile(weavePath+File.separator+asmFile.getName(),asm);
	 }
	 /*
	 String asm = Configuration.instance().getModuleSettings().getModule("ILICIT").getProperty("assemblies");
	 File asmFile = new File(asm);
	 Debug.out(Debug.MODE_DEBUG,"ILICIT","copying "+asm+" to Weaver directory...");
	 FileUtils.copyFile(weavePath+File.separator+asmFile.getName(),asm);
	 String pdbFileName = FileUtils.removeExtension(weavePath+File.separator+asmFile.getName()) + ".pdb";
	 */
	 
	 Debug.out(Debug.MODE_DEBUG,"ILICIT","To be woven file list: "+toBeWoven);
	 //ArrayList libraries = Configuration.instance().assemblies.getAssemblies();
	 String[] assemblyPaths = (String[]) toBeWoven.toArray(new String[toBeWoven.size()]);
	 String targets = "";
	 int assembliesSize = assemblyPaths.length;
	 if(assembliesSize > 0 )
	 {
	  if ( assembliesSize > 20 ) {
	 	try {
	 		out = new PrintWriter(new BufferedWriter(new FileWriter(tempPath + "filelist.peweaver")));
	  		
	 		for( int i = 0; i < assembliesSize; i++ )
	 		{
	 			//out.println(((String)libraries.get(i)).replaceAll("\"", ""));
	 			//out.println((String)builtAssemblies.get(i));
	 			out.println((String)assemblyPaths[i]);
	 		}
			
	 		out.flush();
	 		out.close();
	 	}
	 	catch (IOException e) {
	 		throw new ModuleException("Unable to create the build file for the weaver.","CONE_IS");
	 	}
	  } 
	  else {
		  for( int i = 0; i < assembliesSize; i++ )
	 	  {
	 			//targets += "\""+(String)builtAssemblies.get(i)+"\" ";
		  		targets += "\""+(String)assemblyPaths[i]+"\" ";
	 	  }
		  //targets = StringConverter.stringListToString(assemblyPaths, " ");
	  }
    
	  // verify libraries?
	  boolean verify = false;
      String verifystr = "";
	  Module m = config.getModuleSettings().getModule("ILICIT");
	  if(m!=null){
		  verifystr = m.getProperty("verifyAssemblies");
		  if ("True".equalsIgnoreCase(verifystr)) {
				verify = true;
		  }
	  }
	  
	  CommandLineExecutor cle = new CommandLineExecutor();

	  File f = new File(peweaver);
	  if (!f.exists()) {
	 	throw new ModuleException("Unable to locate the executable '" + peweaver + "'!");
	  }
	  else {
	 	String args = " /nologo";
        
	 	if (verify) {
	 		args = args.concat(" /verify");
	 	}
        
	 	// If debugging supply the /debug switch
	 	if ( Debug.getMode() == Debug.MODE_DEBUG ) args = args.concat(" /debug");
        
	 	args = args.concat(" /ws=" + weavefile);

	 	if ( targets.equals("") ) {
	 		args = args.concat(" /filelist=\"" + tempPath + "filelist.peweaver\"");
	 	}
	 	else {
	 		args = args.concat(" " + targets);
	 	}
        
	 	String cmd = "call \"" + peweaver + "\" " + args;
		
		// If debugging write output from PeWeaver to log file
		if ( Debug.getMode() == Debug.MODE_DEBUG ) 
			cmd += " > \"" + weavePath + "/peweaver.log\"";
		
			Debug.out(Debug.MODE_DEBUG, "ILICIT", "Starting execution of the 'PE Weaver' tool with arguments '" + args + "'...");
			int exitcode = cle.exec(cmd);

			if (exitcode != 0) {
				Debug.out(Debug.MODE_DEBUG, "ILICIT", cle.outputNormal());
			}
        
	  		String msg = "";
        
	  		switch(exitcode)
			{
			case 1: msg = "General PEWeaver failure"; break;
			case 2: msg = "Unable to find weave specification file"; break;
			case 3: msg = "An error occured in the weaving process"; break;
			case 4: msg = "Missing ildasm executable (ildasm.exe)"; break;
			case 5: msg = "Missing ilasm executable (SDK tool ilasm.exe)"; break;
			case 6: msg = "Missing PeVerify executable (SDK tool peverify.exe)"; break;
			case 9: msg = "Input file not found"; break;
			case 10: msg = "IL disassembler not found (ildasm.exe)"; break;
			case 11: msg = "IL disassembler execution failure"; break;
			case 12: msg = "Assembly verification error"; break;
			case 13: msg = "IL file not found"; break;
			case 14: msg = "Unsupported file format"; break;
			case 15: msg = "IL assemmbler not found (ilasm.exe)"; break;
			case 16: msg = "IL assembler execution failure"; break;
			case 17: msg = "Output file not found"; break;
			case 18: msg = "PEVerify tool not found (SDK tool peverify.exe)"; break;
			case 19: msg = "PEVerify execution failure"; break;
			case 25: msg = "ILWeaver not found (ilweaver.exe)"; break;
			case 26: msg = "ILWeaver execution failure"; break;
			case -532459699: msg = "PeWeaver or ILWeaver had a CLR crash. Most likely your compiler created rubish from your sourcefiles."; break; //Happens if your compiler creates rubbish
			default: msg = "PeWeaver execution failure (exitcode " + exitcode + ')'; break;
			}

	  		if (exitcode == 0)
	  			Debug.out(Debug.MODE_DEBUG, "ILICIT", "Successfully executed the 'PE Weaver' tool.");
	  		else
	  			throw new ModuleException(msg, "ILICIT");        
	   }
	  }//end assemblyPaths > 0
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
		// remove the \\ :(
		String srcFile = src.replaceAll("\\\\\\\\","/");
		ArrayList sources = (ArrayList)incre.externalSourcesBySource.get(FileUtils.removeExtension(srcFile));
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
     	// remove the \\ :(
		String srcFile = src.replaceAll("\\\\\\\\","/");
     	ArrayList sources = (ArrayList)incre.externalSourcesBySource.get(FileUtils.removeExtension(srcFile));
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
		// remove the \\ :(
		String srcFile = src.replaceAll("\\\\\\\\","/");
		ArrayList sources = (ArrayList)incre.externalSourcesBySource.get(FileUtils.removeExtension(srcFile));
		sources.add(0,src);
		
		Iterator it = incre.getAllInstancesOfOrdered(CompiledImplementation.class);
		while(it.hasNext())
		{
			CompiledImplementation ci = (CompiledImplementation)it.next();
			String className = ci.getClassName();
			if(className != null)
				result.add(className);
		}
		
		it = incre.getAllInstancesOfOrdered(CpsConcern.class);
		while (it.hasNext()) {
			CpsConcern c = (CpsConcern)it.next();
			Object o = c.getDynObject("IMPLEMENTATION");
			if ( o != null) {
				
				PrimitiveConcern pc = (PrimitiveConcern)o;
				result.add(pc.getQualifiedName());
			}
		}
		
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
		// remove the \\ :(
		String srcFile = src.replaceAll("\\\\\\\\","/");
		ArrayList sources = (ArrayList)incre.externalSourcesBySource.get(FileUtils.removeExtension(srcFile));
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
	
    public void main(String[] args) {
      CommonResources resources = new CommonResources();
      DataStore ds = DataStore.instance();
      

      Properties props = new Properties();
      props.setProperty("ILICIT_PEWEAVER", "C:\\Documents and Settings\\%username%\\My Documents\\Visual Studio Projects\\Composestar\\PeWeaver\\bin\\Debug\\peweaver.exe");
      props.setProperty("ILICIT_WEAVEFILE", "ws.xml");
      props.setProperty("ILICIT_VERIFY", "yes");
      props.setProperty("ILICIT_TARGETS", "\"C:\\Documents and Settings\\%username%\\My Documents\\Visual Studio Projects\\Composestar\\PeWeaver\\bin\\Debug\\bak\\TestProfilee.exe\"");

      ds.addObject("config", props);

      try {
        ILICIT ilicit = new ILICIT();
        ilicit.run(resources);
      }
      catch (ModuleException me) {
        System.out.println(me.getMessage());
      }
    }
}