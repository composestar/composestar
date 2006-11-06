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
 * Applies the changes as specified by CONE-IS to the assemblies in order to
 * impose the behavior defined in the concern specifications.
 */
public class ILICIT implements WEAVER
{
	public static final String VERSION = "$Revision$";

	public static final String MODULE_NAME = "ILICIT";

	private Configuration config;

	public ILICIT()
	{
		config = Configuration.instance();
	}

	public void run(CommonResources resources) throws ModuleException
	{
		String basePath = config.getPathSettings().getPath("Base");

		File weaveDir = new File(basePath, "obj/weaver");
		if (!weaveDir.exists())
		{
			weaveDir.mkdir();
		}

		// determine the assemblies to weave
		List toBeWoven = new ArrayList();
		List compiledSources = config.getProjects().getCompiledSources();
		addBuiltAssemblies(weaveDir, compiledSources, toBeWoven);

		// also copy dummies
		copyDummies(weaveDir);

		// start the weaver
		if (toBeWoven.size() > 0)
		{
			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "To be woven file list: " + toBeWoven);
			invokeWeaver(basePath, toBeWoven);
		}
		else
		{
			Debug.out(Debug.MODE_WARNING, MODULE_NAME, "No files to weave");
		}
	}

	private void addBuiltAssemblies(File weaveDir, List compiledSources, List toBeWoven) throws ModuleException
	{
		List builtAssemblies = new ArrayList();
		DataStore.instance().addObject("BuiltLibs", builtAssemblies);

		Iterator it = compiledSources.iterator();
		while (it.hasNext())
		{
			String asm = (String) it.next();

			File source = new File(asm);
			File target = new File(weaveDir, source.getName());

			String sourceFilename = source.getAbsolutePath();
			String targetFilename = target.getAbsolutePath();

			if (!INCRE.instance().isProcessedByModule(asm, MODULE_NAME))
			{
				try
				{
					Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Copying '" + sourceFilename + "' to Weaver directory");
					FileUtils.copyFile(targetFilename, sourceFilename);
				}
				catch (IOException e)
				{
					throw new ModuleException("Unable to copy assembly: " + e.getMessage(), MODULE_NAME);
				}

				String pdbFile = FileUtils.replaceExtension(sourceFilename, "pdb");
				if (FileUtils.fileExist(pdbFile))
				{
					Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Copying '" + pdbFile + "' to Weaver directory");

					String pdbSourceFilename = new File(pdbFile).getAbsolutePath();
					String pdbTargetFilename = FileUtils.replaceExtension(targetFilename, "pdb");

					try
					{
						FileUtils.copyFile(pdbTargetFilename, pdbSourceFilename);
					}
					catch (IOException e)
					{
						throw new ModuleException("Unable to copy PDB: " + e.getMessage(), MODULE_NAME);
					}
				}

				builtAssemblies.add(targetFilename);
				toBeWoven.add(targetFilename);
			}
			else
			{
				// no need to weave the file
				Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "No need to re-weave " + asm);
				builtAssemblies.add(targetFilename);
			}
		}
	}

	private void copyDummies(File weaveDir) throws ModuleException
	{
		List dummies = config.getProjects().getCompiledDummies();
		Iterator dumIt = dummies.iterator();
		while (dumIt.hasNext())
		{
			String source = (String) dumIt.next();
			File destFile = new File(weaveDir, FileUtils.getFilenamePart(source));

			try
			{
				Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Copying '" + source + "' to Weaver directory");
				FileUtils.copyFile(destFile.getAbsolutePath(), source);
			}
			catch (IOException e)
			{
				throw new ModuleException("Unable to copy dummy: " + e.getMessage(), MODULE_NAME);
			}
		}
	}

	private void invokeWeaver(String basePath, List toBeWoven) throws ModuleException
	{
		// build command line
		List cmdList = new ArrayList();
		cmdList.add(getExecutable());
		cmdList.add("/nologo");

		// verify libraries?
		String va = config.getModuleProperty(MODULE_NAME, "verifyAssemblies", "false");
		if ("true".equalsIgnoreCase(va))
		{
			cmdList.add("/verify");
		}

		// if debugging supply the /debug switch
		if (Debug.getMode() == Debug.MODE_DEBUG)
		{
			cmdList.add("/debug");
		}

		// add weave specification
		String weaveFile = basePath + "weavespec.xml";
		cmdList.add("/ws=" + weaveFile);

		// add build file
		String buildfile = basePath + "filelist.peweaver";
		createBuildfile(buildfile, toBeWoven);
		cmdList.add("/filelist=" + buildfile);

		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Command: " + StringUtils.join(cmdList));

		CommandLineExecutor cle = new CommandLineExecutor();
		int exitcode = cle.exec(cmdList);

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
		String cpsPath = config.getPathSettings().getPath("Composestar");
		File exe = new File(cpsPath, "binaries/peweaver.exe");

		if (!exe.exists())
		{
			throw new ModuleException("Unable to locate the executable '" + exe + "'!", MODULE_NAME);
		}

		return exe.getAbsolutePath();
	}

	private void createBuildfile(String buildfile, List toBeWoven) throws ModuleException
	{
		PrintWriter out = null;
		try
		{
			out = new PrintWriter(new BufferedWriter(new FileWriter(buildfile)));

			Iterator it = toBeWoven.iterator();
			while (it.hasNext())
			{
				out.println((String) it.next());
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

	/**
	 * @param src Absolute path of a sourcefile
	 * @return ArrayList containing all concerns with FMO and extracted from the
	 *         source and its external linked sources Used by INCRE
	 */
	public List getConcernsWithFMO(String src)
	{
		INCRE incre = INCRE.instance();
		List concerns = new ArrayList();
		List concernsWithFMO = incre.getConcernsWithFMO();
		// remove the \\ :(
		String srcFile = src.replaceAll("\\\\\\\\", "/"); // FIXME: why does
		// it have the
		// double slashes in
		// the first place?
		ArrayList sources = (ArrayList) incre.externalSourcesBySource.get(FileUtils.removeExtension(srcFile));
		sources.add(0, src);

		if (!concernsWithFMO.isEmpty())
		{
			Iterator iterConcerns = concernsWithFMO.iterator();

			while (iterConcerns.hasNext())
			{
				Concern c = (Concern) iterConcerns.next();
				if (incre.declaredInSources(c, sources))
				{
					concerns.add(c.getQualifiedName());
				}
			}
		}

		return concerns;
	}

	/**
	 * @param src Absolute path of a sourcefile
	 * @return ArrayList containing all concerns recognized as a casting
	 *         interception Only concerns extracted from the source and its
	 *         external linked sources are returned Used by INCRE
	 */
	public List castingInterceptions(String src) throws ModuleException
	{
		ArrayList list = new ArrayList();
		INCRE incre = INCRE.instance();
		DataStore ds = incre.getCurrentRepository();
		ArrayList concernsWithFMO = incre.getConcernsWithFMO();
		// remove the \\ :(
		String srcFile = src.replaceAll("\\\\\\\\", "/"); // FIXME: why does
		// it have the
		// double slashes in
		// the first place?
		ArrayList sources = (ArrayList) incre.externalSourcesBySource.get(FileUtils.removeExtension(srcFile));
		sources.add(0, src);

		if (!concernsWithFMO.isEmpty())
		{
			Iterator iterConcerns = concernsWithFMO.iterator();
			while (iterConcerns.hasNext())
			{
				Concern c = (Concern) iterConcerns.next();
				boolean castConcern = false;

				if (incre.declaredInSources(c, sources))
				{
					FilterModuleOrder fmo = (FilterModuleOrder) c.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY);

					Iterator iterFilterModules = fmo.orderAsList().iterator();
					while (iterFilterModules.hasNext())
					{
						String fmref = (String) iterFilterModules.next();
						FilterModule fm = (FilterModule) ds.getObjectByID(fmref);

						Iterator iterInternals = fm.getInternalIterator();
						while (iterInternals.hasNext())
						{
							Internal internal = (Internal) iterInternals.next();
							if (!list.contains(internal.type.getQualifiedName()))
							{
								castConcern = true;
								list.add(internal.type.getQualifiedName());
							}
						}
					}

					if (castConcern)
					{
						if (!list.contains(c.getQualifiedName()))
						{
							list.add(c.getQualifiedName());
						}
					}
				}
			}
		}

		return list;
	}

	/**
	 * @param src Absolute path of a sourcefile
	 * @return ArrayList containing all concerns which instantiation should be
	 *         intercepted Only concerns extracted from the source and its
	 *         external linked sources are returned Used by INCRE
	 */
	public List getAfterInstantiationClasses(String src) throws ModuleException
	{
		INCRE incre = INCRE.instance();
		ArrayList result = new ArrayList();
		// remove the \\ :(
		String srcFile = src.replaceAll("\\\\\\\\", "/"); // FIXME: why does
		// it have the
		// double slashes in
		// the first place?
		ArrayList sources = (ArrayList) incre.externalSourcesBySource.get(FileUtils.removeExtension(srcFile));
		sources.add(0, src);

		Iterator it = incre.getAllInstancesOfOrdered(CompiledImplementation.class);
		while (it.hasNext())
		{
			CompiledImplementation ci = (CompiledImplementation) it.next();
			String className = ci.getClassName();
			if (className != null)
			{
				result.add(className);
			}
		}

		it = incre.getAllInstancesOfOrdered(CpsConcern.class);
		while (it.hasNext())
		{
			CpsConcern c = (CpsConcern) it.next();
			Object o = c.getDynObject("IMPLEMENTATION");
			if (o != null)
			{

				PrimitiveConcern pc = (PrimitiveConcern) o;
				result.add(pc.getQualifiedName());
			}
		}

		it = incre.getAllInstancesOfOrdered(Concern.class);
		while (it.hasNext())
		{
			Concern c = (Concern) it.next();
			if (incre.declaredInSources(c, sources))
			{
				if (c.getDynObject("superImpInfo") != null && !(c instanceof CpsConcern))
				{
					result.add(c.getQualifiedName());
				}
			}
		}

		return result;
	}

	/**
	 * @param src Absolute path of a sourcefile
	 * @return ArrayList containing all concerns with outputfilter(s) Only
	 *         concerns extracted from the source and its external linked
	 *         sources are returned Used by INCRE
	 */
	public List getConcernsWithOutputFilters(String src) throws ModuleException
	{
		ArrayList concerns = new ArrayList();
		INCRE incre = INCRE.instance();
		DataStore ds = incre.getCurrentRepository();
		ArrayList concernsWithFMO = incre.getConcernsWithFMO();
		// remove the \\ :(
		String srcFile = src.replaceAll("\\\\\\\\", "/"); // FIXME: why does
		// it have the
		// double slashes in
		// the first place?
		ArrayList sources = (ArrayList) incre.externalSourcesBySource.get(FileUtils.removeExtension(srcFile));
		sources.add(0, src);

		if (!concernsWithFMO.isEmpty())
		{
			Iterator iterConcerns = concernsWithFMO.iterator();
			while (iterConcerns.hasNext())
			{
				Concern c = (Concern) iterConcerns.next();
				if (incre.declaredInSources(c, sources))
				{
					FilterModuleOrder fmo = (FilterModuleOrder) c.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY);

					Iterator iterFilterModules = fmo.orderAsList().iterator();
					while (iterFilterModules.hasNext())
					{
						FilterModule fm = (FilterModule) ds.getObjectByID((String) iterFilterModules.next());

						if (!fm.getOutputFilters().isEmpty())
						{
							concerns.add(c.getQualifiedName());
						}
					}
				}
			}
		}

		return concerns;
	}
}
