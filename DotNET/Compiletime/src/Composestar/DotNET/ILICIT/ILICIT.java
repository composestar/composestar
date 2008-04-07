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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Composestar.Core.BACO.BACO;
import Composestar.Core.Config.ModuleInfo;
import Composestar.Core.Config.ModuleInfoManager;
import Composestar.Core.Config.Source;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.WEAVER.WEAVER;
import Composestar.DotNET.COMP.DotNETCompiler;
import Composestar.Utils.CommandLineExecutor;
import Composestar.Utils.FileUtils;
import Composestar.Utils.StringUtils;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Applies the changes as specified by CONE-IS to the assemblies in order to
 * impose the behavior defined in the concern specifications.
 */
public class ILICIT implements WEAVER
{
	public static final String VERSION = "$Revision$";

	public static final String MODULE_NAME = "ILICIT";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	protected Composestar.Core.Config.BuildConfig config;

	protected CommonResources resources;

	public ILICIT()
	{}

	public void run(CommonResources inresources) throws ModuleException
	{
		resources = inresources;
		config = resources.configuration();
		File weaveDir = new File(config.getProject().getIntermediate(), "weaver");
		if (!weaveDir.exists())
		{
			weaveDir.mkdir();
		}

		// determine the assemblies to weave
		Set<File> toBeWoven = addBuiltAssemblies(weaveDir);

		// also copy dummies
		copyDummies(weaveDir);

		// start the weaver
		if (toBeWoven.size() > 0)
		{
			logger.debug("To be woven file list: " + toBeWoven);
			invokeWeaver(config.getProject().getIntermediate(), toBeWoven);
		}
		else
		{
			logger.warn("No files to weave");
		}
	}

	/**
	 * Returns a list of files to be woven
	 * 
	 * @param weaveDir
	 * @return
	 * @throws ModuleException
	 */
	private Set<File> addBuiltAssemblies(File weaveDir) throws ModuleException
	{
		Set<File> result = new HashSet<File>();
		List<File> builtAssemblies = new ArrayList<File>();
		resources.put(BACO.BUILDLIBS_KEY, builtAssemblies);

		for (Source src : config.getProject().getSources())
		{
			File asm = src.getAssembly();
			File target = new File(weaveDir, asm.getName());

			try
			{
				logger.debug("Copying '" + asm + "' to Weaver directory");
				FileUtils.copyFile(target, asm);
			}
			catch (IOException e)
			{
				throw new ModuleException("Unable to copy assembly: " + e.getMessage(), MODULE_NAME);
			}

			File pdbFile = new File(FileUtils.replaceExtension(src.toString(), "pdb"));
			if (pdbFile.exists())
			{
				logger.debug("Copying '" + pdbFile + "' to Weaver directory");
				File pdbTarget = new File(weaveDir, pdbFile.getName());
				try
				{
					FileUtils.copyFile(pdbTarget, pdbFile);
				}
				catch (IOException e)
				{
					throw new ModuleException("Unable to copy PDB: " + e.getMessage(), MODULE_NAME);
				}
			}

			builtAssemblies.add(target);
			result.add(target);
		}
		return result;
	}

	private void copyDummies(File weaveDir) throws ModuleException
	{
		File dummies = (File) resources.get(DotNETCompiler.DUMMY_ASSEMBLY);
		File destFile = new File(weaveDir, dummies.getName());
		try
		{
			logger.debug("Copying '" + dummies + "' to Weaver directory");
			FileUtils.copyFile(destFile, dummies);
		}
		catch (IOException e)
		{
			throw new ModuleException("Unable to copy dummy: " + e.getMessage(), MODULE_NAME);
		}
	}

	private void invokeWeaver(File basePath, Set<File> toBeWoven) throws ModuleException
	{
		// build command line
		List<String> cmdList = new ArrayList<String>();
		cmdList.add(getExecutable());
		cmdList.add("/nologo");

		// verify libraries?
		ModuleInfo mi = ModuleInfoManager.get(MODULE_NAME);
		if (mi.getBooleanSetting("verifyAssemblies", false))
		{
			cmdList.add("/verify");
		}

		// if debugging supply the /debug switch
		if (logger.isDebugEnabled())
		{
			cmdList.add("/debug");
		}

		// add weave specification
		File weaveFile = new File(basePath, "weavespec.xml");
		cmdList.add("/ws=" + weaveFile);

		// add build file
		File buildfile = new File(basePath, "filelist.peweaver");
		createBuildfile(buildfile, toBeWoven);
		cmdList.add("/filelist=" + buildfile);

		logger.debug("Command: " + StringUtils.join(cmdList));

		CommandLineExecutor cle = new CommandLineExecutor();
		int exitcode;
		try
		{
			exitcode = cle.exec(cmdList);
		}
		catch (IOException e)
		{
			throw new ModuleException(e.getMessage(), MODULE_NAME, e);
		}
		catch (InterruptedException e)
		{
			throw new ModuleException(e.getMessage(), MODULE_NAME, e);
		}

		File logFile = new File(basePath, "weavelog.txt");
		createLog(logFile, cle.outputNormal());

		if (exitcode != 0)
		{
			String msg = getExitMessage(exitcode) + ". See weavelog.txt for more information.";
			throw new ModuleException(msg, MODULE_NAME);
		}
	}

	private String getExecutable() throws ModuleException
	{
		File exe = resources.getPathResolver().getResource("bin/peweaver.exe");
		if (exe == null)
		{
			throw new ModuleException("Unable to locate the executable bin/peweaver.exe", MODULE_NAME);
		}

		return exe.getAbsolutePath();
	}

	private void createBuildfile(File buildfile, Set<File> toBeWoven) throws ModuleException
	{
		PrintWriter out = null;
		try
		{
			out = new PrintWriter(new BufferedWriter(new FileWriter(buildfile)));
			for (File file : toBeWoven)
			{
				out.println(file.toString());
			}
		}
		catch (IOException e)
		{
			throw new ModuleException("Unable to create build file for the weaver: " + e.getMessage(), MODULE_NAME);
		}
		finally
		{
			FileUtils.close(out);
		}
	}

	private void createLog(File logFile, String log) throws ModuleException
	{
		PrintWriter out = null;
		try
		{
			out = new PrintWriter(new BufferedWriter(new FileWriter(logFile)));
			out.println(log);
		}
		catch (IOException e)
		{
			throw new ModuleException("Unable to create PEWeaver logfile: " + e.getMessage(), MODULE_NAME);
		}
		finally
		{
			FileUtils.close(out);
		}
	}

	private String getExitMessage(int code)
	{
		switch (code)
		{
			case 1:
				return "General PEWeaver failure";
			case 2:
				return "Unable to find weave specification file";
			case 3:
				return "An error occured in the weaving process";
			case 4:
				return "Missing ildasm executable (ildasm.exe)";
			case 5:
				return "Missing ilasm executable (SDK tool ilasm.exe)";
			case 6:
				return "Missing PeVerify executable (SDK tool peverify.exe)";
			case 9:
				return "Input file not found";
			case 10:
				return "IL disassembler not found (ildasm.exe)";
			case 11:
				return "IL disassembler execution failure";
			case 12:
				return "Assembly verification error";
			case 13:
				return "IL file not found";
			case 14:
				return "Unsupported file format";
			case 15:
				return "IL assemmbler not found (ilasm.exe)";
			case 16:
				return "IL assembler execution failure";
			case 17:
				return "Output file not found";
			case 18:
				return "PEVerify tool not found (SDK tool peverify.exe)";
			case 19:
				return "PEVerify execution failure";
			case 25:
				return "ILWeaver not found (ilweaver.exe)";
			case 26:
				return "ILWeaver execution failure";
			case -532459699:
				return "PeWeaver or ILWeaver had a CLR crash";
			default:
				return "PeWeaver execution failure (exitcode " + code + ')';
		}
	}

	// TODO michieh: this isn't used?
	// /**
	// * @param src Absolute path of a sourcefile
	// * @return ArrayList containing all concerns with FMO and extracted from
	// the
	// * source and its external linked sources Used by INCRE
	// */
	// public List getConcernsWithFMO(String src)
	// {
	// INCRE incre = INCRE.instance();
	// List concerns = new ArrayList();
	// List concernsWithFMO = incre.getConcernsWithFMO();
	//
	// // Source orig =
	// // Configuration.instance().getProjects().getSourceOfBinary(src);
	// String target = FileUtils.removeExtension(orig.getTarget());
	// ArrayList sources = new ArrayList((Collection)
	// incre.externalSourcesBySource.get(orig));
	// sources.add(0, orig.getFileName());
	//
	// if (!concernsWithFMO.isEmpty())
	// {
	// Iterator iterConcerns = concernsWithFMO.iterator();
	//
	// while (iterConcerns.hasNext())
	// {
	// Concern c = (Concern) iterConcerns.next();
	// if (incre.declaredInSources(c, sources))
	// {
	// concerns.add(c.getQualifiedName());
	// }
	// }
	// }
	//
	// return concerns;
	// }
	//
	// /**
	// * @param src Absolute path of a sourcefile
	// * @return ArrayList containing all concerns recognized as a casting
	// * interception Only concerns extracted from the source and its
	// * external linked sources are returned Used by INCRE
	// */
	// public List castingInterceptions(String src) throws ModuleException
	// {
	// ArrayList list = new ArrayList();
	// INCRE incre = INCRE.instance();
	// DataStore ds = incre.getCurrentRepository();
	// List concernsWithFMO = incre.getConcernsWithFMO();
	//
	// // Source orig =
	// // Configuration.instance().getProjects().getSourceOfBinary(src);
	// String target = FileUtils.removeExtension(orig.getTarget());
	// ArrayList sources = new ArrayList((Collection)
	// incre.externalSourcesBySource.get(orig));
	// sources.add(0, orig.getFileName());
	//
	// if (!concernsWithFMO.isEmpty())
	// {
	// Iterator iterConcerns = concernsWithFMO.iterator();
	// while (iterConcerns.hasNext())
	// {
	// Concern c = (Concern) iterConcerns.next();
	// boolean castConcern = false;
	//
	// if (incre.declaredInSources(c, sources))
	// {
	// FilterModuleOrder fmo = (FilterModuleOrder)
	// c.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY);
	//
	// Iterator iterFilterModules = fmo.orderAsList().iterator();
	// while (iterFilterModules.hasNext())
	// {
	// String fmref = (String) iterFilterModules.next();
	// FilterModule fm = (FilterModule) ds.getObjectByID(fmref);
	//
	// Iterator iterInternals = fm.getInternalIterator();
	// while (iterInternals.hasNext())
	// {
	// Internal internal = (Internal) iterInternals.next();
	// if (!list.contains(internal.type.getQualifiedName()))
	// {
	// castConcern = true;
	// list.add(internal.type.getQualifiedName());
	// }
	// }
	// }
	//
	// if (castConcern)
	// {
	// if (!list.contains(c.getQualifiedName()))
	// {
	// list.add(c.getQualifiedName());
	// }
	// }
	// }
	// }
	// }
	//
	// return list;
	// }
	//
	// /**
	// * @param src Absolute path of a sourcefile
	// * @return ArrayList containing all concerns which instantiation should be
	// * intercepted Only concerns extracted from the source and its
	// * external linked sources are returned Used by INCRE
	// */
	// public List getAfterInstantiationClasses(String src) throws
	// ModuleException
	// {
	// INCRE incre = INCRE.instance();
	// ArrayList result = new ArrayList();
	//
	// // Source orig =
	// // Configuration.instance().getProjects().getSourceOfBinary(src);
	// String target = FileUtils.removeExtension(orig.getTarget());
	// ArrayList sources = new ArrayList((Collection)
	// incre.externalSourcesBySource.get(orig));
	// sources.add(0, orig.getFileName());
	//
	// Iterator it =
	// incre.getAllInstancesOfOrdered(CompiledImplementation.class);
	// while (it.hasNext())
	// {
	// CompiledImplementation ci = (CompiledImplementation) it.next();
	// String className = ci.getClassName();
	// if (className != null)
	// {
	// result.add(className);
	// }
	// }
	//
	// it = incre.getAllInstancesOfOrdered(CpsConcern.class);
	// while (it.hasNext())
	// {
	// CpsConcern c = (CpsConcern) it.next();
	// Object o = c.getDynObject("IMPLEMENTATION");
	// if (o != null)
	// {
	//
	// PrimitiveConcern pc = (PrimitiveConcern) o;
	// result.add(pc.getQualifiedName());
	// }
	// }
	//
	// it = incre.getAllInstancesOfOrdered(Concern.class);
	// while (it.hasNext())
	// {
	// Concern c = (Concern) it.next();
	// if (incre.declaredInSources(c, sources))
	// {
	// if (c.getDynObject("superImpInfo") != null && !(c instanceof CpsConcern))
	// {
	// result.add(c.getQualifiedName());
	// }
	// }
	// }
	//
	// return result;
	// }
	//
	// /**
	// * @param src Absolute path of a sourcefile
	// * @return ArrayList containing all concerns with outputfilter(s) Only
	// * concerns extracted from the source and its external linked
	// * sources are returned Used by INCRE
	// */
	// public List getConcernsWithOutputFilters(String src) throws
	// ModuleException
	// {
	// ArrayList concerns = new ArrayList();
	// INCRE incre = INCRE.instance();
	// DataStore ds = incre.getCurrentRepository();
	// List concernsWithFMO = incre.getConcernsWithFMO();
	//
	// // Source orig =
	// // Configuration.instance().getProjects().getSourceOfBinary(src);
	// String target = FileUtils.removeExtension(orig.getTarget());
	// ArrayList sources = new ArrayList((Collection)
	// incre.externalSourcesBySource.get(orig));
	// sources.add(0, orig.getFileName());
	//
	// if (!concernsWithFMO.isEmpty())
	// {
	// Iterator iterConcerns = concernsWithFMO.iterator();
	// while (iterConcerns.hasNext())
	// {
	// Concern c = (Concern) iterConcerns.next();
	// if (incre.declaredInSources(c, sources))
	// {
	// FilterModuleOrder fmo = (FilterModuleOrder)
	// c.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY);
	//
	// Iterator iterFilterModules = fmo.orderAsList().iterator();
	// while (iterFilterModules.hasNext())
	// {
	// FilterModule fm = (FilterModule) ds.getObjectByID((String)
	// iterFilterModules.next());
	//
	// if (!fm.getOutputFilters().isEmpty())
	// {
	// concerns.add(c.getQualifiedName());
	// }
	// }
	// }
	// }
	// }
	//
	// return concerns;
	// }
}
