package Composestar.Core.BACO;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.CustomFilter;
import Composestar.Core.Master.Config.Dependency;
import Composestar.Core.Master.Config.Project;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;

public abstract class BACO implements CTCommonModule
{
	public BACO()
	{		
	}
	
	public void run(CommonResources resources) throws ModuleException
	{
		Debug.out(Debug.MODE_DEBUG, "BACO","Copying files to output directory...");

		Set filesToCopy = new HashSet();
		addRequiredFiles(filesToCopy);
		addBuiltLibraries(filesToCopy);
		addCustomFilters(filesToCopy);
		addDependencies(filesToCopy);
		addRepository(filesToCopy);

		copyFiles(filesToCopy);
	}

	protected void addRequiredFiles(Set filesToCopy)
	{
		Configuration config = Configuration.instance();
		String cpsPath = config.getPathSettings().getPath("Composestar");
		Debug.out(Debug.MODE_DEBUG,"BACO","ComposestarHome: '" + cpsPath + "'");

		Iterator it = config.getPlatform().getRequiredFiles().iterator();
		while (it.hasNext())
		{
			String requiredFile = (String)it.next();
			String filename = cpsPath + "binaries/" + requiredFile;
			
			Debug.out(Debug.MODE_DEBUG,"BACO","Adding required file: '" + filename + "'");
			filesToCopy.add(filename);
		}
	}

	protected void addBuiltLibraries(Set filesToCopy)
	{
		List builtLibs = (List)DataStore.instance().getObjectByID("BuiltLibs");
		Iterator it = builtLibs.iterator();
		while (it.hasNext())
		{
			String lib = (String)it.next();

			Debug.out(Debug.MODE_DEBUG,"BACO","Adding built library: '" + lib + "'");
			filesToCopy.add(FileUtils.unquote(lib));
		}
	}

	protected void addCustomFilters(Set filesToCopy)
	{
		Configuration config = Configuration.instance();
		Iterator it = config.getFilters().getCustomFilters().iterator();
		while (it.hasNext())
		{
			CustomFilter filter = (CustomFilter)it.next();
			String lib = filter.getLibrary();

			Debug.out(Debug.MODE_DEBUG,"BACO","Adding custom filter: '" + lib + "'");
			filesToCopy.add(FileUtils.unquote(lib));
		}
	}

	protected void addDependencies(Set filesToCopy)
	{
		Configuration config = Configuration.instance();
		Iterator it = config.getProjects().getProjects().iterator();
		while (it.hasNext())
		{
			Project project = (Project)it.next();
			
			// add deps
			Iterator projectit = project.getDependencies().iterator();
			while (projectit.hasNext())
			{
				Dependency dependency = (Dependency)projectit.next();
				if (isNeededDependency(dependency))
				{
					String depFilename = dependency.getFileName();
					filesToCopy.add(FileUtils.unquote(depFilename));

					Debug.out(Debug.MODE_DEBUG,"BACO","Adding dependency: '" + depFilename + "'");
				}
			}

			// add dummies
			String dummies = project.getCompiledDummies();
			if (dummies != null)
			{
				Debug.out(Debug.MODE_DEBUG,"BACO","Adding dummies: '" + dummies + "'");
				filesToCopy.add(FileUtils.unquote(dummies));
			}
		}
	}

	protected void addRepository(Set filesToCopy)
	{
		Configuration config = Configuration.instance();

		String basePath = config.getPathSettings().getPath("Base");
		String repository = basePath + "repository.xml";
		
		Debug.out(Debug.MODE_DEBUG,"BACO","Adding repository: '" + repository + "'");
		filesToCopy.add(repository);
	}

	private void copyFiles(Set filesToCopy) throws ModuleException
	{
		Configuration config = Configuration.instance();

		// determine output dir:
		String outputPath = config.getProjects().getOutputPath();
		Debug.out(Debug.MODE_DEBUG,"BACO","outputPath='" + outputPath + "'");
		
		// create the output dir if needed
		if (!FileUtils.createFullPath(outputPath))
			throw new ModuleException("Unable to create output directory: '" + outputPath + "'", "BACO");

		// start the actual copying
		Iterator filesIt = filesToCopy.iterator();
		while (filesIt.hasNext())
		{
			String source = (String)filesIt.next();
			String dest = outputPath + FileUtils.getFilenamePart(source);
			try {
				Debug.out(Debug.MODE_DEBUG,"BACO","Copying '" + source + "' to '" + dest + "'");
				FileUtils.copyFile(dest, source);
			}
			catch (IOException e) {
				Debug.out(Debug.MODE_WARNING,"BACO","Unable to copy '" + source + "' to '" + dest + "': " + e.getMessage());
			}
		}
	}

	protected abstract boolean isNeededDependency(Dependency dependency);
}
