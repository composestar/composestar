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

import Composestar.Core.CONE.CONE;
import Composestar.Core.Config.BuildConfig;
import Composestar.Core.Config.CustomFilter;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.Resources.PathResolver;
import Composestar.Utils.FileUtils;
import Composestar.Utils.Logging.CPSLogger;

/**
 * BACO is responsible for copying all files required to execute the compiled
 * program to the output directory.
 */
public abstract class BACO implements CTCommonModule
{
	private static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.BACO);

	/**
	 * Resource key for the List&lt;File&gt; of build libraries
	 */
	public static final String BUILDLIBS_KEY = "BuiltLibs";

	protected CommonResources resources;

	protected BuildConfig config;

	public BACO()
	{}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Resources.CommonResources)
	 */
	public ModuleReturnValue run(CommonResources inResources) throws ModuleException
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

		return ModuleReturnValue.Ok;
	}

	/**
	 * Adds the platform specific runtime files to the list of files to copy.
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

	/**
	 * Adds the compiled libraries/assemblies to the list of files to copy.
	 * 
	 * @param filesToCopy
	 */
	protected void addBuiltLibraries(Set<File> filesToCopy)
	{
		List<File> builtLibs = resources.get(BUILDLIBS_KEY);
		for (File builtLib : builtLibs)
		{
			String lib = builtLib.toString();
			File file = new File(FileUtils.unquote(lib));
			logger.debug("Adding built library: " + file.toString());
			filesToCopy.add(file);
		}
	}

	/**
	 * Add the runtime files for the custom filters to the list of files to
	 * copy.
	 * 
	 * @param filesToCopy
	 */
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

	/**
	 * Add the project's dependencies to the list of files.
	 * 
	 * @param filesToCopy
	 */
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

	/**
	 * Adds the saved repository file which is needed at runtime.
	 * 
	 * @param filesToCopy
	 */
	protected void addRepository(Set<File> filesToCopy)
	{
		File repository = resources.get(CONE.REPOSITORY_FILE_KEY);
		if (repository != null)
		{
			logger.debug("Adding repository: '" + repository + "'");
			filesToCopy.add(repository);
		}
	}

	/**
	 * Perform the actual copying of the files
	 * 
	 * @param filesToCopy
	 * @param fatal
	 * @throws ModuleException
	 */
	protected void copyFiles(Set<File> filesToCopy, boolean fatal) throws ModuleException
	{
		// determine output dir:
		File outputDir = config.getProject().getOutput();
		logger.debug("OutputPath: " + outputDir.toString());

		// create the output dir if needed
		if (!outputDir.exists() && !outputDir.mkdirs())
		{
			throw new ModuleException("Unable to create output directory: " + outputDir.toString(), ModuleNames.BACO);
		}

		// start the actual copying
		for (File file : filesToCopy)
		{
			copyFile(outputDir, file, fatal);
		}
	}

	/**
	 * Copies a single file to the destination directory.
	 * 
	 * @param outputDir
	 * @param source
	 * @param fatal
	 * @throws ModuleException
	 */
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
				throw new ModuleException(msg, ModuleNames.BACO);
			}
			else
			{
				logger.warn(msg);
			}
		}
	}

	/**
	 * A validation of a dependency to see if it is really required to be copied
	 * to the output directory. For example dependencies that are part of the
	 * virtual machine should not be included in the final output.
	 * 
	 * @param dependency
	 * @return
	 */
	protected abstract boolean isNeededDependency(File dependency);

	/**
	 * Retrieve the filename to the file that contains the custom filter.
	 * 
	 * @param filter
	 * @return
	 */
	protected abstract File resolveCustomFilter(CustomFilter filter);
}
