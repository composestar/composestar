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

import Composestar.Core.CONE.RepositorySerializer;
import Composestar.Core.CONE.WeaveFileGenerator;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.PrimitiveConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.CompiledImplementation;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FILTH.FILTHService;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Dependency;
import Composestar.Core.Master.Config.ModuleInfo;
import Composestar.Core.Master.Config.ModuleInfoManager;
import Composestar.Core.Master.Config.Project;
import Composestar.Core.Master.Config.Projects;
import Composestar.Core.Master.Config.Source;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.TYM.TypeLocations;
import Composestar.DotNET.BACO.DotNETBACO;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;

/**
 * This class generates the interception specification file for ILICIT based on
 * information in the repository.
 * 
 * @author Sverre Boschman
 */
public class DotNETWeaveFileGenerator implements WeaveFileGenerator
{
	private final static String MODULE_NAME = "CONE-IS";

	private Configuration config;

	private PrintWriter out = null;
	
	protected File repositoryFile;
	
	public DotNETWeaveFileGenerator()
	{
		config = Configuration.instance();
	}

	public void run(CommonResources resources) throws ModuleException
	{
		String basePath = config.getPathSettings().getPath("Base");
		File destination = new File(basePath, "weavespec.xml");

		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, 
				"Writing weave specifications to file '" + destination.getName() + "'...");

		ModuleInfo mi = ModuleInfoManager.get(DotNETRepositorySerializer.MODULE_NAME);
		if (mi.getBooleanSetting("compressed"))
		{
			repositoryFile = new File(basePath, "repository.xml.gz");
		}
		else 
		{
			repositoryFile = new File(basePath, "repository.xml");
		}
		resources.add(RepositorySerializer.REPOSITORY_FILE_KEY, repositoryFile);
		
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
		TypeLocations typeLocations = TypeLocations.instance();

		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Resolving entry assembly...");

		String applicationStart = config.getProjects().getApplicationStart();
		String entryAssembly = typeLocations.getAssemblyByType(applicationStart);

		if (entryAssembly == null)
		{
			int dot = applicationStart.lastIndexOf('.');
			entryAssembly = applicationStart.substring(0, dot);

			Debug.out(Debug.MODE_WARNING, MODULE_NAME, "The entry assembly could not be reliably determined. Using '"
					+ entryAssembly + "'.");
		}
		else
		{
			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Resolved '" + entryAssembly + "' as entry assembly.");
		}

		return entryAssembly;
	}

	private void writeAssemblyReferenceDefinitions(CommonResources resources, String entryAssembly)
			throws ModuleException
	{
		TypeLocations typeLocations = TypeLocations.instance();

		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Writing assembly reference block...");
		out.println("<assemblies>");

		writeAssemblyDefinitionRecord("ComposeStarRuntimeInterpreter", "0.0.0.0");
		writeAssemblyDefinitionRecord("ComposeStarDotNETRuntimeInterpreter", "0.0.0.0");

		Iterator prjIt = config.getProjects().getProjects().iterator();
		while (prjIt.hasNext())
		{
			Project p = (Project) prjIt.next();
			String dummies = p.getName() + ".dummies";
			writeAssemblyDefinitionRecord(dummies, "0.0.0.0", true);
		}

		prjIt = config.getProjects().getProjects().iterator();
		while (prjIt.hasNext())
		{
			Project project = (Project) prjIt.next();
			Iterator depIt = project.getDependencies().iterator();
			while (depIt.hasNext())
			{
				Dependency dependency = (Dependency) depIt.next();
				String dep = dependency.getFileName();

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
		}

		Set assemblyNames = typeLocations.assemblies();
		Iterator anIt = assemblyNames.iterator();
		while (anIt.hasNext())
		{
			String an = (String) anIt.next();
			if (!entryAssembly.equals(an))
			{
				writeAssemblyDefinitionRecord(an, "0.0.0.0", entryAssembly);
			}
			else
			{
				writeAssemblyDefinitionRecord(an, "0.0.0.0");
			}
		}

		out.println("</assemblies>");
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Assembly reference block has been written.");
	}

	private void writeMethodDefinitions()
	{
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Writing method definitions block...");
		out.println("<methods>");

		int debugLevel = config.getProjects().getRunDebugLevel();

		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Inserting method definition for 'handleApplicationStart'.");
		out.println("<method id=\"application_start\" assembly=\"ComposeStarDotNETRuntimeInterpreter\" class=\"Composestar.RuntimeDotNET.FLIRT.DotNETMessageHandlingFacility\" name=\"handleDotNETApplicationStart\">");
		out.println("<argument value=\"" + repositoryFile.getName() + "\" type=\"string\"/>");
		out.println("<argument value=\"" + debugLevel + "\" type=\"int\"/>");
		out.println("</method>");

		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Inserting method definition for 'handleInstanceCreation'.");
		out.println("<method id=\"after_instantiation\" assembly=\"ComposeStarRuntimeInterpreter\" class=\"Composestar.RuntimeCore.FLIRT.MessageHandlingFacility\" name=\"handleInstanceCreation\">");
		out.println("<argument value=\"%senderobject\"/>");
		out.println("<argument value=\"%createdobject\"/>");
		out.println("<argument value=\"%originalparameters\"/>");
		out.println("</method>");

		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Inserting method definition for 'handleVoidMethodCall'.");
		out.println("<method id=\"invocation_void\" assembly=\"ComposeStarRuntimeInterpreter\" class=\"Composestar.RuntimeCore.FLIRT.MessageHandlingFacility\" name=\"handleVoidMethodCall\">");
		out.println("<argument value=\"%senderobject\"/>");
		out.println("<argument value=\"%targetobject\"/>");
		out.println("<argument value=\"%targetmethod\"/>");
		out.println("<argument value=\"%originalparameters\"/>");
		out.println("</method>");

		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Inserting method definition for 'handleReturnMethodCall'.");
		out.println("<method id=\"invocation_with_return\" assembly=\"ComposeStarRuntimeInterpreter\" class=\"Composestar.RuntimeCore.FLIRT.MessageHandlingFacility\" name=\"handleReturnMethodCall\" returnType=\"object\">");
		out.println("<argument value=\"%senderobject\"/>");
		out.println("<argument value=\"%targetobject\"/>");
		out.println("<argument value=\"%targetmethod\"/>");
		out.println("<argument value=\"%originalparameters\"/>");
		out.println("</method>");

		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Inserting method definition for 'handleCast'.");
		out.println("<method id=\"cast\" assembly=\"ComposeStarRuntimeInterpreter\" class=\"Composestar.RuntimeCore.FLIRT.CastingFacility\" name=\"handleCast\" returnType=\"object\">");
		out.println("<argument value=\"%targetobject\"/>");
		out.println("<argument value=\"%casttarget\"/>");
		out.println("</method>");

		out.println("</methods>");
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Method definitions block has been written.");
	}

	private List<String> getAfterInstantiationClasses()
	{
		List<String> result = new ArrayList<String>();
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Searching for instantiation interceptions...");

		DataStore ds = DataStore.instance();
		Iterator it = ds.getAllInstancesOf(CompiledImplementation.class);
		while (it.hasNext())
		{
			CompiledImplementation ci = (CompiledImplementation) it.next();
			String className = ci.getClassName();
			if (className != null)
			{
				result.add(className);
				Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Adding interception for instantiation of class '" + className
						+ "' to our internal list.");
			}
		}

		it = ds.getAllInstancesOf(CpsConcern.class);
		while (it.hasNext())
		{
			CpsConcern c = (CpsConcern) it.next();
			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Found CpsConcern with name[" + c.getName() + "]: "
					+ c.getQualifiedName());

			Object impl = c.getDynObject("IMPLEMENTATION");
			if (impl != null)
			{
				PrimitiveConcern pc = (PrimitiveConcern) impl;

				Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Found primitive concern with name: " + pc.getQualifiedName());
				result.add(pc.getQualifiedName());
				Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Adding interception for instantiation of class '"
						+ pc.getQualifiedName() + "' to our internal list.");
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
				Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Adding interception for instantiation of class '"
						+ c.getQualifiedName() + "' to our internal list.");
			}
		}

		return result;
	}

	private void writeMethodInvocations(CommonResources resources)
	{
		FILTHService filthservice = FILTHService.getInstance(resources);

		out.println("<methodInvocations>");

		Iterator iterConcerns = DataStore.instance().getAllInstancesOf(Concern.class);
		while (iterConcerns.hasNext())
		{
			Concern c = (Concern) iterConcerns.next();

			try
			{
				List list = filthservice.getOrder(c);
				if (!list.isEmpty())
				{
					writeMethodInvocationRecord(c.getQualifiedName());
				}
			}
			catch (Exception e)
			{
				Debug.out(Debug.MODE_WARNING, MODULE_NAME, "Unable to get ordering for concern '" + c.getName()
						+ "' from FILTH.");
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

				Iterator iterFilterModules = fmo.order();
				while (iterFilterModules.hasNext())
				{
					String fmn = (String) iterFilterModules.next();
					FilterModule fm = (FilterModule) DataStore.instance().getObjectByID(fmn);

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
		Projects projects = config.getProjects();
		TypeLocations typeLocations = TypeLocations.instance();

		out.println("<classReplacements>");

		Set typeNames = typeLocations.typeNames();
		Iterator it = typeNames.iterator();
		while (it.hasNext())
		{
			String typeName = (String) it.next();
			String assembly = typeLocations.getAssemblyByType(typeName);

			String sourceFile = typeLocations.getSourceByType(typeName);
			if (sourceFile == null)
			{
				throw new RuntimeException("sourceFile for " + typeName + " is null");
			}

			Source source = projects.getSource(sourceFile);
			if (source == null)
			{
				throw new RuntimeException("Source for " + typeName + " is null");
			}

			String projectName = source.getProject().getName();
			String dummies = projectName + ".dummies";

			writeClassReplacementRecord(dummies, typeName, assembly, typeName);
		}

		out.println("</classReplacements>");
	}

	private void writeClassDefinitions(CommonResources resources)
	{
		// Write definitions for inputfilters and dummy unlinking
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Writing definition for class '*'...");

		out.println("<class name=\"*\">");

		writeMethodInvocations(resources);
		writeCastingInterceptions();
		writeClassReplacements();

		out.println("</class>");

		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Class definition for class '*' has been written.");

		// Write definitions for outputfilters and instantiations
		List<String> instantiationClasses = getAfterInstantiationClasses();

		Iterator iterConcerns = DataStore.instance().getAllInstancesOf(Concern.class);
		while (iterConcerns.hasNext())
		{
			Concern c = (Concern) iterConcerns.next();

			if (c.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY) != null)
			{
				FilterModuleOrder fmo = (FilterModuleOrder) c.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY);

				Iterator iterFilterModules = fmo.order();
				while (iterFilterModules.hasNext())
				{
					String fmn = (String) iterFilterModules.next();
					FilterModule fm = (FilterModule) DataStore.instance().getObjectByID(fmn);

					if (!fm.getOutputFilters().isEmpty())
					{
						// Outputfilters defined for this concern
						Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Writing definition for class '"
								+ c.getQualifiedName() + "'...");

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

						Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Class definition for class '" + c.getQualifiedName()
								+ "' has been written.");

						break;
					}
				}
			}
		}

		// Write remaining class instantiations
		for (String className : instantiationClasses)
		{
			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Writing definition for class '" + className + "'...");
			writeAfterInstantiationRecord(className);
			Debug
					.out(Debug.MODE_DEBUG, MODULE_NAME, "Class definition for class '" + className
							+ "' has been written.");
		}
	}

	private void writeApplicationInfo(String applicationStart)
	{
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Writing application information...");
		out.println("<application name=\"" + applicationStart + ".exe\">");
		out.println("<notifyStart id=\"application_start\"/>");
		out.println("</application>");
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Application information has been written.");
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
			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Inserting assembly reference to '" + name + "'.");
			out.println("<assembly name=\"" + name + "\" version=\"" + version
					+ "\" publicKeytoken=\"\" forceReferenceIn=\"" + forceReferenceIn + "\"/>");
		}
		else
		{
			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Inserting assembly reference to '" + name + "' for removal.");
			out.println("<assembly name=\"" + name + "\" version=\"" + version
					+ "\" publicKeytoken=\"\" remove=\"yes\"/>");
		}
	}

	private void writeMethodInvocationRecord()
	{
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Adding interception for all outgoing calls.");
		out.println("<callToMethod class=\"*\" name=\"*\">");
		out.println("<voidRedirectTo id=\"invocation_void\"/>");
		out.println("<returnvalueRedirectTo id=\"invocation_with_return\"/>");
		out.println("</callToMethod>");
	}

	private void writeMethodInvocationRecord(String target)
	{
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Adding interception for all methods of class '" + target + "'.");
		out.println("<callToMethod class=\"" + target + "\" name=\"*\">");
		out.println("<voidRedirectTo id=\"invocation_void\"/>");
		out.println("<returnvalueRedirectTo id=\"invocation_with_return\"/>");
		out.println("</callToMethod>");
	}

	private void writeClassReplacementRecord(String oldAssembly, String oldClass, String newAssembly, String newClass)
	{
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Replacing class '[" + oldAssembly + "]" + oldClass + "' with '["
				+ newAssembly + "]" + newClass + "'.");
		out.println("<classReplacement assembly=\"" + oldAssembly + "\" class=\"" + oldClass + "\">");
		out.println("<replaceWith assembly=\"" + newAssembly + "\" class=\"" + newClass + "\"/>");
		out.println("</classReplacement>");
	}

	private void writeAfterInstantiationRecord()
	{
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Adding notification after instantiation.");
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
