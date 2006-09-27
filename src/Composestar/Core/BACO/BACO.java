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
	public void run(CommonResources resources) throws ModuleException
	{
		Debug.out(Debug.MODE_DEBUG, "BACO","Copying files to output directory...");
		Configuration config = Configuration.instance();

		HashSet filesToCopy = new HashSet();
		addRequiredFiles(filesToCopy);
		addBuiltLibraries(filesToCopy);
		addCustomFilters(filesToCopy);
		addDependencies(filesToCopy);

		// add repository.xml: 
		String projectPath = config.getPathSettings().getPath("Base");
		String repository = projectPath + "repository.xml";
		
		Debug.out(Debug.MODE_DEBUG,"BACO","Adding repository: '" + repository + "'");
		filesToCopy.add(repository);

		// determine output dir:
		String outputPath = config.getProjects().getProperty("OuputPath");
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

	protected void addRequiredFiles(Set filesToCopy)
	{
		Configuration config = Configuration.instance();
		String cpsPath = config.getPathSettings().getPath("Composestar");
		Debug.out(Debug.MODE_DEBUG,"BACO","cpsPath='" + cpsPath + "'");

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
			Debug.out(Debug.MODE_DEBUG,"BACO","Adding dummies: '" + dummies + "'");

			filesToCopy.add(FileUtils.unquote(dummies));
		}
	}

	protected abstract boolean isNeededDependency(Dependency dependency);

/*	public void copyFile(String file, String dest) throws ModuleException
	{
		Debug.out(Debug.MODE_DEBUG,"BACO","Copying file: '" + file + "' to '" + dest + "'");
		try
		{
			File in = new File(file);
		//	String tmp = in.getAbsolutePath().substring(in.getAbsolutePath().lastIndexOf(File.separator) + 1);
			File out = new File(dest + FileUtils.getFilenamePart(file));

			FileInputStream fis  = new FileInputStream(in);
			FileOutputStream fos = new FileOutputStream(out);
			
			byte[] buf = new byte[1024];
			for (int i; (i = fis.read(buf)) != -1;)
			{
				fos.write(buf, 0, i);
			}
			
			fis.close();
			fos.close();
		}
		catch (FileNotFoundException e)
		{
			Debug.out(Debug.MODE_CRUCIAL,"BACO","File not found: file='" + file + "', dest='" + dest + "', msg='" + e.getMessage() + "'");
			Debug.out(Debug.MODE_DEBUG,"BACO",Debug.stackTrace(e));
		}
		catch (IOException e)
		{
			Debug.out(Debug.MODE_CRUCIAL,"BACO","IOException:" + e.getMessage());
			Debug.out(Debug.MODE_DEBUG,"BACO",Debug.stackTrace(e));
		}
	}*/
}
