/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.DotNET.ILICIT;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

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
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.WEAVER.WEAVER;
import Composestar.Utils.CommandLineExecutor;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;
import Composestar.Utils.StringUtils;

/**
 * Applies the changes as specified by CONE-IS to the assemblies
 * in order to impose the behavior defined in the concern specifications.
 */
public class ILICIT implements WEAVER
{
	public static final String version = "$Revision$";
	
	private Configuration config;
	
	public ILICIT()
	{
		config = Configuration.instance();
	}
	
	public void run(CommonResources resources) throws ModuleException
	{
		String cpsPath = config.getPathSettings().getPath("Composestar");
		String basePath = config.getPathSettings().getPath("Base");

		File weaveDir = new File(basePath, "obj/Weaver");
		if (!weaveDir.exists()) weaveDir.mkdir();

		// determine the assemblies to weave
		List toBeWoven = new ArrayList();
		List compiledSources = config.getProjects().getCompiledSources();
		addBuiltAssemblies(weaveDir, compiledSources, toBeWoven);
		Debug.out(Debug.MODE_DEBUG,"ILICIT","To be woven file list: " + toBeWoven);

		// also copy dummies 
		copyDummies(weaveDir);

		// start the weaver
		if (toBeWoven.size() > 0)
			invokeWeaver(cpsPath, basePath, toBeWoven);
	}

	private void addBuiltAssemblies(File weaveDir, List compiledSources, List toBeWoven) 
		throws ModuleException 
	{
		List builtAssemblies = new ArrayList();
		DataStore.instance().addObject("BuiltLibs", builtAssemblies);

		Iterator it = compiledSources.iterator();
		while (it.hasNext())
		{
			String asm = (String)it.next();
			
			File source = new File(asm);			
			File target = new File(weaveDir, source.getName());
			
			String sourceFilename = source.getAbsolutePath();
			String targetFilename = target.getAbsolutePath();				

			if (!INCRE.instance().isProcessedByModule(asm,"ILICIT"))
			{
				try {
					Debug.out(Debug.MODE_DEBUG,"ILICIT","Copying '" + sourceFilename + "' to Weaver directory");					
					FileUtils.copyFile(targetFilename, sourceFilename);
				}
				catch (IOException e) {
					throw new ModuleException("Unable to copy assembly: " + e.getMessage(), "ILICIT");
				}
				
				String pdbFile = FileUtils.replaceExtension(sourceFilename, "pdb");
				if (FileUtils.fileExist(pdbFile))
				{
					Debug.out(Debug.MODE_DEBUG,"ILICIT","Copying '" + pdbFile + "' to Weaver directory");
					
					String pdbSourceFilename = new File(pdbFile).getAbsolutePath(); 
					String pdbTargetFilename = FileUtils.replaceExtension(targetFilename, "pdb");
					
					try {
						FileUtils.copyFile(pdbTargetFilename, pdbSourceFilename);
					}
					catch (IOException e) {
						throw new ModuleException("Unable to copy PDB: " + e.getMessage(), "ILICIT");
					}
				}
				
				builtAssemblies.add(targetFilename);
				toBeWoven.add(targetFilename);
			}
			else
			{
				// no need to weave the file
				Debug.out(Debug.MODE_DEBUG,"ILICIT","No need to re-weave " + asm);
				builtAssemblies.add(targetFilename);
			}
		}
	}

	private void copyDummies(File weaveDir) throws ModuleException 
	{
		Configuration config = Configuration.instance();

		List dummies = config.getProjects().getCompiledDummies();
		Iterator dumIt = dummies.iterator();
		while (dumIt.hasNext()) 
		{
			String source = (String)dumIt.next();
			File destFile = new File(weaveDir, FileUtils.getFilenamePart(source));

			try {
				Debug.out(Debug.MODE_DEBUG,"ILICIT","Copying '" + source + "' to Weaver directory");
				FileUtils.copyFile(destFile.getAbsolutePath(), source);
			}
			catch (IOException e) {
				throw new ModuleException("Unable to copy dummy: " + e.getMessage(), "ILICIT");
			}
		}
	}

	private void invokeWeaver(String cpsPath, String projectPath, List toBeWoven) throws ModuleException
	{
		// build command line
		String peweaver = cpsPath + "binaries/peweaver.exe";
		if (!FileUtils.fileExist(peweaver))
			throw new ModuleException("Unable to locate the executable '" + peweaver + "'!");

		List cmdList = new ArrayList();
		cmdList.add(peweaver);
		cmdList.add("/nologo");

		// verify libraries?
		String va = config.getModuleProperty("ILICIT", "verifyAssemblies", "false");
		if ("true".equalsIgnoreCase(va))
			cmdList.add("/verify");

		// if debugging supply the /debug switch
		if (Debug.getMode() == Debug.MODE_DEBUG)
			cmdList.add("/debug");
		
		// add weave specification
		String weaveFile = projectPath + "weavespec.xml";
		cmdList.add("/ws=" + weaveFile);

		// add build file
		String buildfile = projectPath + "filelist.peweaver";
		createBuildfile(buildfile, toBeWoven);
		cmdList.add("/filelist=" + FileUtils.quote(buildfile));

		Debug.out(Debug.MODE_DEBUG, "ILICIT", "Command: " + StringUtils.join(cmdList));

		CommandLineExecutor cle = new CommandLineExecutor();
		int exitcode = cle.exec(cmdList);
		
		if (exitcode == 0)
			Debug.out(Debug.MODE_DEBUG, "ILICIT", "Successfully executed the 'PE Weaver' tool.");
		else
		{
			Debug.out(Debug.MODE_DEBUG, "ILICIT", cle.outputNormal());
			throw new ModuleException(getExitMessage(exitcode), "ILICIT");
		}
	}

	private void createBuildfile(String buildfile, List toBeWoven) throws ModuleException
	{
		PrintWriter out = null; 
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(buildfile)));

			Iterator it = toBeWoven.iterator();
			while (it.hasNext())
				out.println((String)it.next());

			out.flush();
		}
		catch (IOException e) {
			throw new ModuleException("Unable to create the build file for the weaver.","CONE_IS");
		}
		finally {
			FileUtils.close(out);
		}
	}

	private String getExitMessage(int code)
	{
		switch (code)
		{
		case  1: return "General PEWeaver failure";
		case  2: return "Unable to find weave specification file";
		case  3: return "An error occured in the weaving process";
		case  4: return "Missing ildasm executable (ildasm.exe)";
		case  5: return "Missing ilasm executable (SDK tool ilasm.exe)";
		case  6: return "Missing PeVerify executable (SDK tool peverify.exe)";
		case  9: return "Input file not found";
		case 10: return "IL disassembler not found (ildasm.exe)";
		case 11: return "IL disassembler execution failure";
		case 12: return "Assembly verification error";
		case 13: return "IL file not found";
		case 14: return "Unsupported file format";
		case 15: return "IL assemmbler not found (ilasm.exe)";
		case 16: return "IL assembler execution failure";
		case 17: return "Output file not found";
		case 18: return "PEVerify tool not found (SDK tool peverify.exe)";
		case 19: return "PEVerify execution failure";
		case 25: return "ILWeaver not found (ilweaver.exe)";
		case 26: return "ILWeaver execution failure";
		case -532459699: return "PeWeaver or ILWeaver had a CLR crash. Most likely your compiler created rubish from your sourcefiles."; //Happens if your compiler creates rubbish
		default: return "PeWeaver execution failure (exitcode " + code + ')';
		}		
	}

	/**
	 * @param src Absolute path of a sourcefile 
	 * @return ArrayList containing all concerns with FMO and extracted from 
	 * the source and its external linked sources
	 * 
	 * Used by INCRE
	 */
	public List getConcernsWithFMO(String src)
	{
		INCRE incre = INCRE.instance();
		List concerns = new ArrayList();
		List concernsWithFMO = incre.getConcernsWithFMO();
		// remove the \\ :(
		String srcFile = src.replaceAll("\\\\\\\\","/"); // FIXME: why does it have the double slashes in the first place?
		ArrayList sources = (ArrayList)incre.externalSourcesBySource.get(FileUtils.removeExtension(srcFile));
		sources.add(0,src);

		if (!concernsWithFMO.isEmpty()){
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
	 * 
	 * Used by INCRE
	 */
	public List castingInterceptions(String src) throws ModuleException
	{
		ArrayList list = new ArrayList();
		INCRE incre = INCRE.instance();
		DataStore ds = incre.getCurrentRepository();
		ArrayList concernsWithFMO = incre.getConcernsWithFMO();
		// remove the \\ :(
		String srcFile = src.replaceAll("\\\\\\\\","/"); // FIXME: why does it have the double slashes in the first place?
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
	 * 
	 * Used by INCRE
	 */
	public List getAfterInstantiationClasses(String src) throws ModuleException 
	{
		INCRE incre = INCRE.instance();
		ArrayList result = new ArrayList();
		// remove the \\ :(
		String srcFile = src.replaceAll("\\\\\\\\","/"); // FIXME: why does it have the double slashes in the first place?
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
	 * 
	 * Used by INCRE
	 */
	public List getConcernsWithOutputFilters(String src) throws ModuleException
	{
		ArrayList concerns = new ArrayList();
		INCRE incre = INCRE.instance();
		DataStore ds = incre.getCurrentRepository();
		ArrayList concernsWithFMO = incre.getConcernsWithFMO();
		// remove the \\ :(
		String srcFile = src.replaceAll("\\\\\\\\","/"); // FIXME: why does it have the double slashes in the first place?
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

						if ( !fm.getOutputFilters().isEmpty() )
							concerns.add( c.getQualifiedName() );
					}
				}
			} 
		}

		return concerns;
	}

	/**
	 * For testing purposes.
	 */
	public void main(String[] args)
	{
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
