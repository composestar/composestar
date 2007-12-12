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
import Composestar.Core.Config.BuildConfig;
import Composestar.Core.Config.CustomFilter;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.Resources.PathResolver;
import Composestar.Utils.FileUtils;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Copies files to the output directory
 */
public abstract class BACO implements CTCommonModule
{
	public static final String MODULE_NAME = "BACO";

	private static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	protected CommonResources resources;

	protected BuildConfig config;

	public BACO()
	{}

	public void run(CommonResources inResources) throws ModuleException
	{
		logger.debug("Copying files to output directory...");

		resources = inResources;
		config = resources.configuration();

		Set<File> filesToCopy = new HashSet<File>();
		addRequiredFiles(filesToCopy);
		addBuiltLibraries(filesToCopy);
		addCustomFilters(filesToCopy);
		addDependencies(filesToCopy);
		addRepository(filesToCopy);

		copyFiles(filesToCopy, false);
	}

	/**
	 * Copy platform files
	 * 
	 * @param filesToCopy
	 */
	protected void addRequiredFiles(Set<File> filesToCopy)
	{
		PathResolver resolver = resources.getPathResolver();
		for (File file : config.getProject().getPlatform().getResourceFiles(resources.getPathResolver()))
		{
			logger.debug("Adding required file: " + file.toString());
			if (!file.isAbsolute())
			{
				file = resolver.getResource(file);
			}
			if (file != null)
			{
				filesToCopy.add(file);
			}
			else
			{
				logger.warn("Unable to resolved required file");
			}
		}
	}

	protected void addBuiltLibraries(Set<File> filesToCopy)
	{
		// TODO: where is this stored, and why like this? use resources?
		List<File> builtLibs = (List<File>) DataStore.instance().getObjectByID("BuiltLibs");
		for (File builtLib : builtLibs)
		{
			String lib = builtLib.toString();
			File file = new File(FileUtils.unquote(lib));
			logger.debug("Adding built library: " + file.toString());
			filesToCopy.add(file);
		}
	}

	protected void addCustomFilters(Set<File> filesToCopy)
	{
		for (CustomFilter cf : config.getFilters().getCustomFilters())
		{
			File file = resolveCustomFilter(cf);
			if (file != null)
			{
				logger.debug("Adding custom filter: " + file.toString());
				filesToCopy.add(file);
			}
		}
	}

	protected void addDependencies(Set<File> filesToCopy)
	{
		for (File file : config.getProject().getFilesDependencies())
		{
			if (isNeededDependency(file))
			{
				logger.debug("Adding dependency: " + file.toString());
				filesToCopy.add(file);
			}
		}
	}

	protected void addRepository(Set<File> filesToCopy)
	{
		File repository = (File) resources.get(RepositorySerializer.REPOSITORY_FILE_KEY);
		if (repository != null)
		{
			logger.debug("Adding repository: '" + repository + "'");
			filesToCopy.add(repository);
		}
	}

	private void copyFiles(Set<File> filesToCopy, boolean fatal) throws ModuleException
	{
		// determine output dir:
		File outputDir = config.getProject().getOutput();
		logger.debug("OutputPath: " + outputDir.toString());

		// create the output dir if needed
		if (!outputDir.exists() && !outputDir.mkdirs())
		{
			throw new ModuleException("Unable to create output directory: " + outputDir.toString(), MODULE_NAME);
		}

		// start the actual copying
		for (File file : filesToCopy)
		{
			copyFile(outputDir, file, fatal);
		}
	}

	protected void copyFile(File outputDir, File source, boolean fatal) throws ModuleException
	{
		File dest = new File(outputDir, source.getName());
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

	protected abstract boolean isNeededDependency(File dependency);

	protected abstract File resolveCustomFilter(CustomFilter filter);
}
