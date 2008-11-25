package Composestar.Java.BACO;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import Composestar.Core.BACO.BACO;
import Composestar.Core.CONE.CONE;
import Composestar.Core.Config.CustomFilter;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.ModuleNames;
import Composestar.Java.WEAVER.JavaWeaver;
import Composestar.Utils.FileUtils;

public class JavaBACO extends BACO
{
	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.BACO.BACO#addRepository(java.util.Set)
	 */
	@Override
	protected void addRepository(Set<File> filesToCopy)
	{
		// add repository.dat
		File repository = (File) resources.get(CONE.REPOSITORY_FILE_KEY);
		logger.debug("Adding repository: '" + repository + "'");
		filesToCopy.add(repository);
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.BACO.BACO#addBuiltLibraries(java.util.Set)
	 */
	@Override
	protected void addBuiltLibraries(Set<File> filesToCopy)
	{
		List<File> weavedClasses = resources.get(JavaWeaver.WOVEN_CLASSES);// DataStore.instance().getObjectByID("WeavedClasses");
		for (File weavedClass : weavedClasses)
		{
			logger.debug("Adding weaved class: '" + weavedClass + "'");
			filesToCopy.add(weavedClass);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.BACO.BACO#addRequiredFiles(java.util.Set)
	 */
	@Override
	protected void addRequiredFiles(Set<File> filesToCopy)
	{
	// Required files don't need to be copied, since it only contains jar-files.
	// Therefore they still have to be added to the classpath.
	// So this is a redundant operation.
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.BACO.BACO#copyFile(java.io.File, java.io.File,
	 * boolean)
	 */
	@Override
	protected void copyFile(File outputDir, File source, boolean fatal) throws ModuleException
	{
		File dest;
		File weaverPath = new File(config.getProject().getIntermediate(), JavaWeaver.WEAVE_PATH);
		if (source.getAbsolutePath().startsWith(weaverPath.getAbsolutePath()))
		{
			dest = FileUtils.relocateFile(weaverPath, source, outputDir);
		}
		else
		{
			dest = new File(outputDir, source.getName());
		}

		if (!dest.getParentFile().exists() && !dest.getParentFile().mkdirs())
		{
			throw new ModuleException("Unable to create destination directory: '" + dest.getParent() + "'", "BACO");
		}

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

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.BACO.BACO#isNeededDependency(java.io.File)
	 */
	@Override
	protected boolean isNeededDependency(File dependency)
	{
		// TODO: dependencies that are already in the required files list are
		// not needed.
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.BACO.BACO#resolveCustomFilter(Composestar.Core.Config
	 * .CustomFilter)
	 */
	@Override
	protected File resolveCustomFilter(CustomFilter filter)
	{
		File file = new File(filter.getLibrary());
		if (!file.isAbsolute())
		{
			file = new File(config.getProject().getBase(), file.toString());
		}
		return file;
	}

}
