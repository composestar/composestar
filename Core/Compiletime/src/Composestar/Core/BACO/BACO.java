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
import Composestar.Utils.FileUtils;
import Composestar.Utils.Logging.CPSLogger;

public abstract class BACO implements CTCommonModule
{
	public static final String MODULE_NAME = "BACO";
	private static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	public BACO()
	{}

	public void run(CommonResources resources) throws ModuleException
	{
		logger.debug("Copying files to output directory...");

		Set filesToCopy = new HashSet();
		addRequiredFiles(filesToCopy);
		addBuiltLibraries(filesToCopy);
		addCustomFilters(filesToCopy);
		addDependencies(filesToCopy);
		addRepository(filesToCopy);

		copyFiles(filesToCopy, false);
	}

	protected void addRequiredFiles(Set filesToCopy)
	{
		Configuration config = Configuration.instance();
		String cpsPath = config.getPathSettings().getPath("Composestar");
		logger.debug("ComposestarHome: '" + cpsPath + "'");

		Iterator it = config.getPlatform().getRequiredFiles().iterator();
        for (Object o : config.getPlatform().getRequiredFiles()) {
            String requiredFile = (String) o;
            String filename = cpsPath + "binaries/" + requiredFile;

            logger.debug("Adding required file: '" + filename + "'");
            filesToCopy.add(filename);
        }
    }

	protected void addBuiltLibraries(Set filesToCopy)
	{
		List builtLibs = (List) DataStore.instance().getObjectByID("BuiltLibs");
		Iterator it = builtLibs.iterator();
        for (Object builtLib : builtLibs) {
            String lib = (String) builtLib;

            logger.debug("Adding built library: '" + lib + "'");
            filesToCopy.add(FileUtils.unquote(lib));
        }
    }

	protected void addCustomFilters(Set filesToCopy)
	{
		Configuration config = Configuration.instance();
		Iterator it = config.getFilters().getCustomFilters().iterator();
        for (Object o : config.getFilters().getCustomFilters()) {
            CustomFilter filter = (CustomFilter) o;
            String lib = filter.getLibrary();

            logger.debug("Adding custom filter: '" + lib + "'");
            filesToCopy.add(FileUtils.unquote(lib));
        }
    }

	protected void addDependencies(Set filesToCopy)
	{
		Configuration config = Configuration.instance();
		Iterator it = config.getProjects().getProjects().iterator();
        for (Object o1 : config.getProjects().getProjects()) {
            Project project = (Project) o1;

            // add deps
            Iterator projectit = project.getDependencies().iterator();
            for (Object o : project.getDependencies()) {
                Dependency dependency = (Dependency) o;
                if (isNeededDependency(dependency)) {
                    String depFilename = dependency.getFileName();
                    filesToCopy.add(FileUtils.unquote(depFilename));

                    logger.debug("Adding dependency: '" + depFilename + "'");
                }
            }
        }
    }

	protected void addRepository(Set filesToCopy)
	{
		Configuration config = Configuration.instance();

		String basePath = config.getPathSettings().getPath("Base");
		String repository = basePath + "repository.xml";

		logger.debug("Adding repository: '" + repository + "'");
		filesToCopy.add(repository);
	}

	private void copyFiles(Set filesToCopy, boolean fatal) throws ModuleException
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
		Iterator filesIt = filesToCopy.iterator();
        for (Object aFilesToCopy : filesToCopy) {
            String source = (String) aFilesToCopy;
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
