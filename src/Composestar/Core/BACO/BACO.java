/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.BACO;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Composestar.Core.CONE.RepositorySerializer;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.CustomFilter;
import Composestar.Core.Master.Config.Dependency;
import Composestar.Core.Master.Config.Project;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.FileUtils;
import Composestar.Utils.Logging.CPSLogger;

public abstract class BACO implements CTCommonModule
{
	public static final String MODULE_NAME = "BACO";

	private static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);
	
	protected CommonResources resources;

	public BACO()
	{}

	public void run(CommonResources inResources) throws ModuleException
	{
		logger.debug("Copying files to output directory...");
		
		resources = inResources;

		Set<String> filesToCopy = new HashSet<String>();
		addRequiredFiles(filesToCopy);
		addBuiltLibraries(filesToCopy);
		addCustomFilters(filesToCopy);
		addDependencies(filesToCopy);
		addRepository(filesToCopy);

		copyFiles(filesToCopy, false);
	}

	protected void addRequiredFiles(Set<String> filesToCopy)
	{
		Configuration config = Configuration.instance();
		String cpsPath = config.getPathSettings().getPath("Composestar");
		logger.debug("ComposestarHome: '" + cpsPath + "'");

		for (Object o : config.getPlatform().getRequiredFiles())
		{
			String requiredFile = (String) o;
			String filename = cpsPath + "binaries/" + requiredFile;

			logger.debug("Adding required file: '" + filename + "'");
			filesToCopy.add(filename);
		}
	}

	protected void addBuiltLibraries(Set<String> filesToCopy)
	{
		List builtLibs = (List) DataStore.instance().getObjectByID("BuiltLibs");
		for (Object builtLib : builtLibs)
		{
			String lib = (String) builtLib;

			logger.debug("Adding built library: '" + lib + "'");
			filesToCopy.add(FileUtils.unquote(lib));
		}
	}

	protected void addCustomFilters(Set<String> filesToCopy)
	{
		Configuration config = Configuration.instance();
		for (Object o : config.getFilters().getCustomFilters())
		{
			CustomFilter filter = (CustomFilter) o;
			String lib = filter.getLibrary();

			logger.debug("Adding custom filter: '" + lib + "'");
			filesToCopy.add(FileUtils.unquote(lib));
		}
	}

	protected void addDependencies(Set<String> filesToCopy)
	{
		Configuration config = Configuration.instance();
		for (Object o1 : config.getProjects().getProjects())
		{
			Project project = (Project) o1;

			// add deps
			for (Object o : project.getDependencies())
			{
				Dependency dependency = (Dependency) o;
				if (isNeededDependency(dependency))
				{
					String depFilename = dependency.getFileName();
					filesToCopy.add(FileUtils.unquote(depFilename));

					logger.debug("Adding dependency: '" + depFilename + "'");
				}
			}
		}
	}

	protected void addRepository(Set<String> filesToCopy)
	{
		File repository = (File) resources.get(RepositorySerializer.REPOSITORY_FILE_KEY); 
		if (repository != null)
		{
			logger.debug("Adding repository: '" + repository + "'");
			filesToCopy.add(repository.getAbsoluteFile().toString());
		}		
	}

	private void copyFiles(Set<String> filesToCopy, boolean fatal) throws ModuleException
	{
		Configuration config = Configuration.instance();

		// determine output dir:
		String outputPath = config.getProjects().getOutputPath();
		logger.debug("OutputPath: '" + outputPath + "'");

		// create the output dir if needed
		if (!FileUtils.createFullPath(outputPath))
		{
			throw new ModuleException("Unable to create output directory: '" + outputPath + "'", MODULE_NAME);
		}

		// start the actual copying
		for (String source : filesToCopy)
		{
			copyFile(outputPath, source, fatal);
		}
	}

	protected void copyFile(String outputPath, String source, boolean fatal) throws ModuleException
	{
		String dest = outputPath + FileUtils.getFilenamePart(source);
		try
		{
			logger.debug("Copying '" + source + "' to '" + dest + "'");
			FileUtils.copyFile(dest, source);
		}
		catch (IOException e)
		{
			String msg = "Unable to copy '" + source + "' to '" + dest + "': " + e.getMessage();
			if (fatal)
			{
				throw new ModuleException(msg, MODULE_NAME);
			}
			else
			{
				logger.warn(msg);
			}
		}
	}

	protected abstract boolean isNeededDependency(Dependency dependency);
}
