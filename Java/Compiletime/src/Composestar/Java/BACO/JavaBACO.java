package Composestar.Java.BACO;

import Composestar.Core.BACO.BACO;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Dependency;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class JavaBACO extends BACO
{
	protected void addRepository(Set filesToCopy)
	{
		Configuration config = Configuration.instance();
		
		//add repository.dat 
		String projectPath = config.getPathSettings().getPath("Base");
		String repository = projectPath + "repository.dat";
		
		Debug.out(Debug.MODE_DEBUG,"BACO","Adding repository: '" + repository + "'");
		filesToCopy.add(repository);
	}
	
	protected void addBuiltLibraries(Set filesToCopy)
	{
		List weavedClasses = (List)DataStore.instance().getObjectByID("WeavedClasses");
		Iterator it = weavedClasses.iterator();
		while (it.hasNext())
		{
			String clazz = (String)it.next();

			Debug.out(Debug.MODE_DEBUG,"BACO","Adding weaved class: '" + clazz + "'");
			filesToCopy.add(FileUtils.unquote(clazz));
		}
	}
	
	protected void addRequiredFiles(Set filesToCopy)
	{
		//Required files don't need to be copied, since it only contains jar-files.
		//Therefore they still have to be added to the classpath.
		//So this is a redundant operation.
	}
	
	protected void copyFile(String outputPath, String source) throws ModuleException
	{
		String dest;
		Configuration config = Configuration.instance();
		String weavedPath = config.getPathSettings().getPath("Base") + "obj/weaver/";
		if(source.startsWith(weavedPath))
		{
			dest = outputPath + source.substring(weavedPath.length());
			if (!FileUtils.createFullPath(FileUtils.getDirectoryPart(dest)))
			{
				throw new ModuleException("Unable to create destination directory: '" + FileUtils.getDirectoryPart(dest) + "'", "BACO");
			}
		}
		else 
		{
			dest = outputPath + FileUtils.getFilenamePart(source);
		}
		try 
		{
			Debug.out(Debug.MODE_DEBUG,"BACO","Copying '" + source + "' to '" + dest + "'");
			FileUtils.copyFile(dest, source);
		}
		catch (IOException e) 
		{
			Debug.out(Debug.MODE_WARNING,"BACO","Unable to copy '" + source + "' to '" + dest + "': " + e.getMessage());
		}
	}
	
	protected boolean isNeededDependency(Dependency dependency)
	{
		//TODO: dependencies that are already in the required files list are not needed.
		return true;
	}

}
