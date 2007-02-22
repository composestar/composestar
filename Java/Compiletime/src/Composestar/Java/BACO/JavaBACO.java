package Composestar.Java.BACO;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import Composestar.Core.BACO.BACO;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Dependency;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;

public class JavaBACO extends BACO
{
	protected void addRepository(Set filesToCopy)
	{
		Configuration config = Configuration.instance();

		// add repository.dat
		String projectPath = config.getPathSettings().getPath("Base");
		String repository = projectPath + "repository.dat";

		Debug.out(Debug.MODE_DEBUG, "BACO", "Adding repository: '" + repository + "'");
		filesToCopy.add(repository);
	}

	protected void addBuiltLibraries(Set filesToCopy)
	{
		List weavedClasses = (List) DataStore.instance().getObjectByID("WeavedClasses");
		for (Object weavedClass : weavedClasses)
		{
			String clazz = (String) weavedClass;

			Debug.out(Debug.MODE_DEBUG, "BACO", "Adding weaved class: '" + clazz + "'");
			filesToCopy.add(FileUtils.unquote(clazz));
		}
	}

	protected void addRequiredFiles(Set filesToCopy)
	{
	// Required files don't need to be copied, since it only contains jar-files.
	// Therefore they still have to be added to the classpath.
	// So this is a redundant operation.
	}

	protected void copyFile(String outputPath, String source, boolean fatal) throws ModuleException
	{
		String dest;
		Configuration config = Configuration.instance();
		String objPath = config.getPathSettings().getPath("Base") + "obj/";
		String weavedPath = objPath + "weaver/";
		if (source.startsWith(weavedPath))
		{
			dest = outputPath + source.substring(weavedPath.length());
			if (!FileUtils.createFullPath(FileUtils.getDirectoryPart(dest)))
			{
				throw new ModuleException("Unable to create destination directory: '"
						+ FileUtils.getDirectoryPart(dest) + "'", "BACO");
			}
		}
		else if (source.startsWith(objPath))
		{
			// FIXME: temp special case added for embbedded sources
			dest = outputPath + source.substring(objPath.length());
			if (!FileUtils.createFullPath(FileUtils.getDirectoryPart(dest)))
			{
				throw new ModuleException("Unable to create destination directory: '"
						+ FileUtils.getDirectoryPart(dest) + "'", "BACO");
			}
		}
		else
		{
			dest = outputPath + FileUtils.getFilenamePart(source);
		}

		try
		{
			Debug.out(Debug.MODE_DEBUG, "BACO", "Copying '" + source + "' to '" + dest + "'");
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
				Debug.out(Debug.MODE_WARNING, MODULE_NAME, msg);
			}
		}
	}

	protected boolean isNeededDependency(Dependency dependency)
	{
		// TODO: dependencies that are already in the required files list are
		// not needed.
		return true;
	}

}
