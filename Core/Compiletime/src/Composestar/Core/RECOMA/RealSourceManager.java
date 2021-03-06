/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.RECOMA;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import Composestar.Core.COMP.CompilerException;
import Composestar.Core.COMP.LangCompiler;
import Composestar.Core.Config.BuildConfig;
import Composestar.Core.Config.Language;
import Composestar.Core.Config.Project;
import Composestar.Core.Config.Source;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Takes care of compiling the real user sources. Links with the dummies and
 * takes care not to destroy them during compilation.
 */
// @ComposestarModule(ID = ModuleNames.RECOMA, dependsOn = {
// ComposestarModule.DEPEND_ALL })
public class RealSourceManager implements CTCommonModule
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.RECOMA);

	protected BuildConfig config;

	public RealSourceManager()
	{}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getModuleName()
	 */
	public String getModuleName()
	{
		return ModuleNames.RECOMA;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getDependencies()
	 */
	public String[] getDependencies()
	{
		return new String[] { DEPEND_ALL };
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getImportance()
	 */
	public ModuleImportance getImportance()
	{
		return ModuleImportance.REQUIRED;
	}

	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		// List compiledSources = new ArrayList();

		config = resources.configuration();
		// compile sources of projects

		Project project = config.getProject();

		Map<String, Set<Source>> sources = new HashMap<String, Set<Source>>();
		for (Source source : project.getSources())
		{
			String language = source.getLanguage();
			if (language == null)
			{
				language = project.getLanguage();
			}
			Set<Source> lsources = sources.get(language);
			if (lsources == null)
			{
				lsources = new HashSet<Source>();
				sources.put(language, lsources);
			}
			lsources.add(source);
		}

		for (Entry<String, Set<Source>> entry : sources.entrySet())
		{
			Language lang = project.getPlatform().getLanguage(entry.getKey());
			if (lang == null)
			{
				throw new ModuleException(String.format("No language called %s in platform %s", entry.getKey(), project
						.getPlatform().getId()), ModuleNames.RECOMA);
			}

			LangCompiler comp = lang.getCompiler().getCompiler();
			comp.setCommonResources(resources);
			try
			{
				comp.compileSources(project, entry.getValue());
			}
			catch (CompilerException e)
			{
				throw new ModuleException("Error compiling dummies: " + e.getMessage(), ModuleNames.RECOMA);
			}
		}

		// TODO where is this used
		// resources.add("CompiledSources", compiledSources);

		return ModuleReturnValue.OK;
	}

	// /**
	// * Returns the name of the sourcefile in which the specified executable
	// type
	// * is defined.
	// */
	// private String getExeFile(Project project, String exec) throws
	// ModuleException
	// {
	// for (Object o : project.getTypeSources())
	// {
	// TypeSource ts = (TypeSource) o;
	// if (ts.getName().equals(exec))
	// {
	// return ts.getFileName();
	// }
	// }
	//
	// throw new ModuleException("Source file for executable type '" + exec + "'
	// unknown", "RECOMA");
	// }

	// /**
	// * Converts sourcefile to a compilation targetfile
	// *
	// * @param sourcePath
	// * @param isExec whether sourcefile contains executable
	// */
	// private static String getTargetFile(String sourcePath, boolean isExec)
	// {
	// String targetFile = "";
	//
	// // convert / to \ because of build.ini format
	// // nsp = normalized source path
	// String nsp = sourcePath.replace('/', '\\');
	//
	// // last part of sourcefile's path, without extension
	// // e.g. C:\pacman\Main.jsl => Main
	// String srcType = nsp.substring(nsp.lastIndexOf('\\') + 1);
	// srcType = FileUtils.removeExtension(srcType);
	//
	// TypeLocations locations = TypeLocations.instance();
	// List types = locations.getTypesBySource(nsp);
	//
	// // iterate over typesources to find type with full namespace
	// for (Object type1 : types)
	// {
	// String type = (String) type1;
	// String[] elems = type.split("\\.");
	// List list = Arrays.asList(elems);
	// if (list.contains(srcType))
	// {
	// targetFile = type;
	// break; // found full namespace
	// }
	// }
	//
	// if (targetFile.length() == 0) // full namespace not found
	// {
	// if (!types.isEmpty())
	// {
	// // first type declared in sourcefile
	// targetFile = (String) types.get(0);
	// }
	// else
	// {
	// Debug.out(Debug.MODE_WARNING, "RECOMA", srcType + " is not a fully
	// qualified target of source "
	// + sourcePath);
	//
	// // last part of sourcefile's path
	// targetFile = srcType;
	// }
	// }
	//
	// // finish by adding .dll or .exe
	// targetFile += isExec ? ".exe" : ".dll";
	//
	// return targetFile;
	// }
}
