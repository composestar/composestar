/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.DotNET.CONE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import Composestar.Core.CONE.CONE;
import Composestar.Core.Config.BuildConfig;
import Composestar.Core.Config.ModuleInfo;
import Composestar.Core.Config.ModuleInfoManager;
import Composestar.Core.Config.Source;
import Composestar.Core.Config.TypeMapping;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.PrimitiveConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.CompiledImplementation;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FILTH.InnerDispatcher;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.SANE.FilterModuleSuperImposition;
import Composestar.DotNET.BACO.DotNETBACO;
import Composestar.DotNET.COMP.DotNETCompiler;
import Composestar.Utils.FileUtils;
import Composestar.Utils.Logging.CPSLogger;

/**
 * This class generates the interception specification file for ILICIT based on
 * information in the repository.
 * 
 * @author Sverre Boschman
 */
public class DotNETWeaveFileGenerator implements CTCommonModule
{
	public final static String MODULE_NAME = "CONE-IS";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	private BuildConfig config;

	private PrintWriter out = null;

	protected File repositoryFile;

	protected String dummyAssembly;

	public DotNETWeaveFileGenerator()
	{}

	public void run(CommonResources resources) throws ModuleException
	{
		config = resources.configuration();
		File dummies = (File) resources.get(DotNETCompiler.DUMMY_ASSEMBLY);
		dummyAssembly = FileUtils.removeExtension(dummies.getName().toString());
		File destination = new File(config.getProject().getIntermediate(), "weavespec.xml");

		logger.debug("Writing weave specifications to file '" + destination.getName() + "'...");

		ModuleInfo mi = ModuleInfoManager.get(DotNETRepositorySerializer.MODULE_NAME);
		if (mi.getBooleanSetting("compressed"))
		{
			repositoryFile = new File(config.getProject().getIntermediate(), "repository.xml.gz");
		}
		else
		{
			repositoryFile = new File(config.getProject().getIntermediate(), "repository.xml");
		}
		resources.put(CONE.REPOSITORY_FILE_KEY, repositoryFile);

		try
		{
			out = new PrintWriter(new BufferedWriter(new FileWriter(destination)));

			out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			out.println("<weaveSpecification version=\"1.0\">");

			String entryAssembly = getEntryAssembly();

			writeAssemblyReferenceDefinitions(resources, entryAssembly);
			writeMethodDefinitions();
			writeApplicationInfo(entryAssembly);
			writeClassDefinitions(resources);

			out.println("</weaveSpecification>");
		}
		catch (IOException e)
		{
			throw new ModuleException("Unable to create weave specification file '" + destination + "'.", MODULE_NAME);
		}
		catch (Exception e)
		{
			throw new ModuleException("Unhandled exception: " + e.getClass() + ": " + e.getMessage(), MODULE_NAME);
		}
		finally
		{
			FileUtils.close(out);
		}
	}

	private String getEntryAssembly()
	{
		logger.debug("Resolving entry assembly...");

		String entryAssembly = null;
		if (config.getProject().getMainSource() != null)
		{
			entryAssembly = FileUtils.removeExtension(config.getProject().getMainSource().getAssembly().getName());
		}

		if (entryAssembly == null)
		{
			String applicationStart = config.getProject().getMainclass();
			int dot = applicationStart.lastIndexOf('.');
			entryAssembly = applicationStart.substring(0, dot);
			logger.warn("The entry assembly could not be reliably determined. Using '" + entryAssembly + "'.");
		}
		else
		{
			logger.debug("Resolved '" + entryAssembly + "' as entry assembly.");
		}

		return entryAssembly;
	}

	private void writeAssemblyReferenceDefinitions(CommonResources resources, String entryAssembly)
			throws ModuleException
	{
		logger.debug("Writing assembly reference block...");
		out.println("<assemblies>");

		writeAssemblyDefinitionRecord("ComposeStarRuntimeInterpreter", "0.0.0.0");
		writeAssemblyDefinitionRecord("ComposeStarDotNETRuntimeInterpreter", "0.0.0.0");
		writeAssemblyDefinitionRecord(dummyAssembly, "0.0.0.0", true);

		for (File file : config.getProject().getFilesDependencies())
		{
			String dep = file.toString();

			// Do not add the dependency when it is a .NET or Composestar
			// assembly
			if (DotNETBACO.isSystemAssembly(dep))
			{
				continue;
			}

			if (dep.indexOf("ComposeStarRuntimeInterpreter") >= 0)
			{
				continue;
			}

			if (dep.indexOf("ComposeStarDotNETRuntimeInterpreter") >= 0)
			{
				continue;
			}

			String dllname = FileUtils.removeExtension(FileUtils.getFilenamePart(dep));
			writeAssemblyDefinitionRecord(dllname, "0.0.0.0", entryAssembly);
		}

		for (Source src : config.getProject().getSources())
		{
			if (src.getAssembly() == null)
			{
				logger.error(String.format("Source without an assembly: %s", src.getFile()));
				continue;
			}
			String asm = FileUtils.removeExtension(src.getAssembly().getName());
			if (src == config.getProject().getMainSource())
			{
				writeAssemblyDefinitionRecord(asm, "0.0.0.0");
			}
			else
			{
				writeAssemblyDefinitionRecord(asm, "0.0.0.0", entryAssembly);
			}
		}

		out.println("</assemblies>");
		logger.debug("Assembly reference block has been written.");
	}

	private void writeMethodDefinitions()
	{
		logger.debug("Writing method definitions block...");
		out.println("<methods>");

		int debugLevel = Integer.parseInt(config.getSetting("runDebugLevel"));

		logger.debug("Inserting method definition for 'handleApplicationStart'.");
		out
				.println("<method id=\"application_start\" assembly=\"ComposeStarDotNETRuntimeInterpreter\" class=\"Composestar.RuntimeDotNET.FLIRT.DotNETMessageHandlingFacility\" name=\"handleDotNETApplicationStart\">");
		out.println("<argument value=\"" + repositoryFile.getName() + "\" type=\"string\"/>");
		out.println("<argument value=\"" + debugLevel + "\" type=\"int\"/>");
		out.println("</method>");

		logger.debug("Inserting method definition for 'handleInstanceCreation'.");
		out
				.println("<method id=\"after_instantiation\" assembly=\"ComposeStarRuntimeInterpreter\" class=\"Composestar.RuntimeCore.FLIRT.MessageHandlingFacility\" name=\"handleInstanceCreation\">");
		out.println("<argument value=\"%senderobject\"/>");
		out.println("<argument value=\"%createdobject\"/>");
		out.println("<argument value=\"%originalparameters\"/>");
		out.println("</method>");

		logger.debug("Inserting method definition for 'handleVoidMethodCall'.");
		out
				.println("<method id=\"invocation_void\" assembly=\"ComposeStarRuntimeInterpreter\" class=\"Composestar.RuntimeCore.FLIRT.MessageHandlingFacility\" name=\"handleVoidMethodCall\">");
		out.println("<argument value=\"%senderobject\"/>");
		out.println("<argument value=\"%targetobject\"/>");
		out.println("<argument value=\"%targetmethod\"/>");
		out.println("<argument value=\"%originalparameters\"/>");
		out.println("</method>");

		logger.debug("Inserting method definition for 'handleReturnMethodCall'.");
		out
				.println("<method id=\"invocation_with_return\" assembly=\"ComposeStarRuntimeInterpreter\" class=\"Composestar.RuntimeCore.FLIRT.MessageHandlingFacility\" name=\"handleReturnMethodCall\" returnType=\"object\">");
		out.println("<argument value=\"%senderobject\"/>");
		out.println("<argument value=\"%targetobject\"/>");
		out.println("<argument value=\"%targetmethod\"/>");
		out.println("<argument value=\"%originalparameters\"/>");
		out.println("</method>");

		logger.debug("Inserting method definition for 'handleCast'.");
		out
				.println("<method id=\"cast\" assembly=\"ComposeStarRuntimeInterpreter\" class=\"Composestar.RuntimeCore.FLIRT.CastingFacility\" name=\"handleCast\" returnType=\"object\">");
		out.println("<argument value=\"%targetobject\"/>");
		out.println("<argument value=\"%casttarget\"/>");
		out.println("</method>");

		out.println("</methods>");
		logger.debug("Method definitions block has been written.");
	}

	private List<String> getAfterInstantiationClasses()
	{
		List<String> result = new ArrayList<String>();
		logger.debug("Searching for instantiation interceptions...");

		DataStore ds = DataStore.instance();
		Iterator it = ds.getAllInstancesOf(CompiledImplementation.class);
		while (it.hasNext())
		{
			CompiledImplementation ci = (CompiledImplementation) it.next();
			String className = ci.getClassName();
			if (className != null)
			{
				result.add(className);
				logger
						.debug("Adding interception for instantiation of class '" + className
								+ "' to our internal list.");
			}
		}

		it = ds.getAllInstancesOf(CpsConcern.class);
		while (it.hasNext())
		{
			CpsConcern c = (CpsConcern) it.next();
			logger.debug("Found CpsConcern with name[" + c.getName() + "]: " + c.getQualifiedName());

			Object impl = c.getDynObject("IMPLEMENTATION");
			if (impl != null)
			{
				PrimitiveConcern pc = (PrimitiveConcern) impl;

				logger.debug("Found primitive concern with name: " + pc.getQualifiedName());
				result.add(pc.getQualifiedName());
				logger.debug("Adding interception for instantiation of class '" + pc.getQualifiedName()
						+ "' to our internal list.");
			}
		}

		it = ds.getAllInstancesOf(Concern.class);
		while (it.hasNext())
		{
			Concern c = (Concern) it.next();
			// Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Found Concern with
			// name["+c.getName()+"]: " + c.getQualifiedName());
			if (c.getDynObject("superImpInfo") != null && !(c instanceof CpsConcern))
			{
				result.add(c.getQualifiedName());
				logger.debug("Adding interception for instantiation of class '" + c.getQualifiedName()
						+ "' to our internal list.");
			}
		}

		return result;
	}

	private void writeMethodInvocations(CommonResources resources) throws ModuleException
	{
		out.println("<methodInvocations>");

		Iterator iterConcerns = DataStore.instance().getAllInstancesOf(Concern.class);
		while (iterConcerns.hasNext())
		{
			Concern c = (Concern) iterConcerns.next();

			try
			{
				FilterModuleOrder fo = (FilterModuleOrder) c.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY);
				if (fo != null)
				{
					List list = fo.filterModuleSIList();
					writeMethodInvocationRecord(c.getQualifiedName());
				}
			}
			catch (Exception e)
			{
				logger.debug("Unable to get ordering for concern '" + c.getName() + "' from FILTH.");
			}
		}

		out.println("</methodInvocations>");
	}

	private void writeCastingInterceptions()
	{
		Set<String> qns = new HashSet<String>();

		out.println("<casts>");

		Iterator iterConcerns = DataStore.instance().getAllInstancesOf(Concern.class);
		while (iterConcerns.hasNext())
		{
			Concern c = (Concern) iterConcerns.next();
			boolean castConcern = false;

			if (c.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY) != null)
			{
				FilterModuleOrder fmo = (FilterModuleOrder) c.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY);

				Iterator iterFilterModules = fmo.filterModuleSIList().iterator();
				while (iterFilterModules.hasNext())
				{
					FilterModuleSuperImposition fmn = (FilterModuleSuperImposition) iterFilterModules.next();
					FilterModule fm = fmn.getFilterModule().getRef();

					Iterator iterInternals = fm.getInternalIterator();
					while (iterInternals.hasNext())
					{
						Internal internal = (Internal) iterInternals.next();
						String internalQN = internal.getType().getQualifiedName();
						castConcern = true;

						if (!qns.contains(internalQN))
						{
							qns.add(internalQN);
							writeCastingInterceptionRecord(internalQN);
						}
					}
				}
			}

			if (castConcern && !qns.contains(c.getQualifiedName()))
			{
				qns.add(c.getQualifiedName());
				writeCastingInterceptionRecord(c.getQualifiedName());
			}
		}

		out.println("</casts>");
	}

	private void writeClassReplacements()
	{
		out.println("<classReplacements>");
		TypeMapping tm = config.getProject().getTypeMapping();
		for (Entry<String, Source> entry : tm.getMapping().entrySet())
		{
			String assembly = FileUtils.removeExtension(entry.getValue().getAssembly().getName());
			writeClassReplacementRecord(dummyAssembly, entry.getKey(), assembly, entry.getKey());
		}
		out.println("</classReplacements>");
	}

	private void writeClassDefinitions(CommonResources resources) throws ModuleException
	{
		// Write definitions for inputfilters and dummy unlinking
		logger.debug("Writing definition for class '*'...");

		out.println("<class name=\"*\">");

		writeMethodInvocations(resources);
		writeCastingInterceptions();
		writeClassReplacements();

		out.println("</class>");

		logger.debug("Class definition for class '*' has been written.");

		// Write definitions for outputfilters and instantiations
		List<String> instantiationClasses = getAfterInstantiationClasses();

		Iterator iterConcerns = DataStore.instance().getAllInstancesOf(Concern.class);
		while (iterConcerns.hasNext())
		{
			Concern c = (Concern) iterConcerns.next();

			if (c.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY) != null)
			{
				FilterModuleOrder fmo = (FilterModuleOrder) c.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY);

				Iterator iterFilterModules = fmo.filterModuleSIList().iterator();
				while (iterFilterModules.hasNext())
				{
					FilterModuleSuperImposition fmn = (FilterModuleSuperImposition) iterFilterModules.next();
					FilterModule fm = fmn.getFilterModule().getRef();

					if (InnerDispatcher.isDefaultDispatch(fm))
					{
						continue;
					}

					if (!fm.getOutputFilters().isEmpty())
					{
						// Outputfilters defined for this concern
						logger.debug("Writing definition for class '" + c.getQualifiedName() + "'...");

						out.println("<class name=\"" + c.getQualifiedName() + "\">");

						out.println("<methodInvocations>");
						writeMethodInvocationRecord();
						out.println("</methodInvocations>");

						if (instantiationClasses.contains(c.getQualifiedName()))
						{
							writeAfterInstantiationRecord();
							instantiationClasses.remove(c.getQualifiedName());
						}

						out.println("</class>");

						logger.debug("Class definition for class '" + c.getQualifiedName() + "' has been written.");

						break;
					}
				}
			}
		}

		// Write remaining class instantiations
		for (String className : instantiationClasses)
		{
			logger.debug("Writing definition for class '" + className + "'...");
			writeAfterInstantiationRecord(className);
			logger.debug("Class definition for class '" + className + "' has been written.");
		}
	}

	private void writeApplicationInfo(String applicationStart)
	{
		logger.debug("Writing application information...");
		out.println("<application name=\"" + applicationStart + ".exe\">");
		out.println("<notifyStart id=\"application_start\"/>");
		out.println("</application>");
		logger.debug("Application information has been written.");
	}

	private void writeAssemblyDefinitionRecord(String name, String version)
	{
		writeAssemblyDefinitionRecord(name, version, false, "");
	}

	private void writeAssemblyDefinitionRecord(String name, String version, boolean remove)
	{
		writeAssemblyDefinitionRecord(name, version, remove, "");
	}

	private void writeAssemblyDefinitionRecord(String name, String version, String forceReferenceIn)
	{
		writeAssemblyDefinitionRecord(name, version, false, forceReferenceIn);
	}

	private void writeAssemblyDefinitionRecord(String name, String version, boolean remove, String forceReferenceIn)
	{
		if (!remove)
		{
			logger.debug("Inserting assembly reference to '" + name + "'.");
			out.println("<assembly name=\"" + name + "\" version=\"" + version
					+ "\" publicKeytoken=\"\" forceReferenceIn=\"" + forceReferenceIn + "\"/>");
		}
		else
		{
			logger.debug("Inserting assembly reference to '" + name + "' for removal.");
			out.println("<assembly name=\"" + name + "\" version=\"" + version
					+ "\" publicKeytoken=\"\" remove=\"yes\"/>");
		}
	}

	private void writeMethodInvocationRecord()
	{
		logger.debug("Adding interception for all outgoing calls.");
		out.println("<callToMethod class=\"*\" name=\"*\">");
		out.println("<voidRedirectTo id=\"invocation_void\"/>");
		out.println("<returnvalueRedirectTo id=\"invocation_with_return\"/>");
		out.println("</callToMethod>");
	}

	private void writeMethodInvocationRecord(String target)
	{
		logger.debug("Adding interception for all methods of class '" + target + "'.");
		out.println("<callToMethod class=\"" + target + "\" name=\"*\">");
		out.println("<voidRedirectTo id=\"invocation_void\"/>");
		out.println("<returnvalueRedirectTo id=\"invocation_with_return\"/>");
		out.println("</callToMethod>");
	}

	private void writeClassReplacementRecord(String oldAssembly, String oldClass, String newAssembly, String newClass)
	{
		logger.debug("Replacing class '[" + oldAssembly + "]" + oldClass + "' with '[" + newAssembly + "]" + newClass
				+ "'.");
		out.println("<classReplacement assembly=\"" + oldAssembly + "\" class=\"" + oldClass + "\">");
		out.println("<replaceWith assembly=\"" + newAssembly + "\" class=\"" + newClass + "\"/>");
		out.println("</classReplacement>");
	}

	private void writeAfterInstantiationRecord()
	{
		logger.debug("Adding notification after instantiation.");
		out.println("<afterClassInstantiation>");
		out.println("<executeMethod id=\"after_instantiation\"/>");
		out.println("</afterClassInstantiation>");
	}

	private void writeAfterInstantiationRecord(String className)
	{
		out.println("<class name=\"" + className + "\">");
		writeAfterInstantiationRecord();
		out.println("</class>");
	}

	private void writeCastingInterceptionRecord(String className)
	{
		out.println("<castTo assembly=\"\" class=\"" + className + "\">");
		out.println("<executeMethodBefore id=\"cast\"/>");
		out.println("</castTo>");
	}
}
