package Composestar.Core.DUMMER;

import java.io.File;
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
import Composestar.Utils.FileUtils;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Creates dummies of the source files. A dummy source file only contains the
 * declaration part of the methods and classes. The method bodies are omitted.
 * This is a platform depended module.
 */
// @ComposestarModule(ID = ModuleNames.DUMMER)
public class DummyManager implements CTCommonModule
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.DUMMER);

	/**
	 * The directory name for the stubs
	 */
	public static final String DUMMY_PATH = "dummies";

	protected BuildConfig config;

	protected CommonResources resc;

	public DummyManager()
	{}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getModuleName()
	 */
	public String getModuleName()
	{
		return ModuleNames.DUMMER;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getDependencies()
	 */
	public String[] getDependencies()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getImportance()
	 */
	public ModuleImportance getImportance()
	{
		return ModuleImportance.REQUIRED;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Resources
	 * .CommonResources)
	 */
	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		resc = resources;
		config = resources.configuration();
		createDummies();
		// TODO return error when dummies failed
		return ModuleReturnValue.OK;
	}

	/**
	 * Create the dummies
	 * 
	 * @throws ModuleException
	 */
	private void createDummies() throws ModuleException
	{
		File dummyPath = new File(config.getProject().getIntermediate(), DUMMY_PATH);
		if (!dummyPath.exists() && !dummyPath.mkdirs())
		{
			throw new ModuleException(String.format("Unable to create directory: %s", dummyPath.toString()),
					ModuleNames.DUMMER);
		}
		createProjectDummies(dummyPath, config.getProject());
	}

	/**
	 * Create a dummy file for every source file in the project
	 * 
	 * @param dummyPath the target directory
	 * @param project
	 * @throws ModuleException
	 */
	private void createProjectDummies(File dummyPath, Project project) throws ModuleException
	{
		Map<String, Set<Source>> dummies = new HashMap<String, Set<Source>>();
		logger.info("Constructing dummy list");
		for (Source source : project.getSources())
		{
			File sourceFile = source.getFile();
			if (!sourceFile.exists())
			{
				logger.warn(String.format("Source file does not exist: %s", sourceFile.toString()));
				continue;
			}
			File target = FileUtils.relocateFile(project.getBase(), sourceFile, dummyPath);
			if (!target.getParentFile().exists() && !target.getParentFile().mkdirs())
			{
				throw new ModuleException(String.format("Unable to create parent directories for: %s", target
						.toString()), ModuleNames.DUMMER);
			}
			source.setStub(target);

			String language = source.getLanguage();
			if (language == null)
			{
				language = project.getLanguage();
			}
			Set<Source> ldummies = dummies.get(language);
			if (ldummies == null)
			{
				ldummies = new HashSet<Source>();
				dummies.put(language, ldummies);
			}
			ldummies.add(source);
		}
		for (Entry<String, Set<Source>> entry : dummies.entrySet())
		{
			Language lang = project.getPlatform().getLanguage(entry.getKey());
			if (lang == null)
			{
				throw new ModuleException(String.format("No language called %s in platform %s", entry.getKey(), project
						.getPlatform().getId()), ModuleNames.DUMMER);
			}

			logger.info("Constructing dummies");
			DummyEmitter emitter = lang.getDummyEmitter();
			emitter.setCommonResources(resc);
			emitter.createDummies(project, entry.getValue());

			logger.info("Compiling dummies");
			LangCompiler comp = lang.getCompiler().getCompiler();
			comp.setCommonResources(resc);
			try
			{
				comp.compileDummies(project, entry.getValue());
			}
			catch (CompilerException e)
			{
				throw new ModuleException(String.format("Error compiling dummies: %s", e.getMessage()),
						ModuleNames.DUMMER, e);
			}
		}
	}
}
