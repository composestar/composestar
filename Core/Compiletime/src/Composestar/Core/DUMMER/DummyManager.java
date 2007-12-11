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
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.FileUtils;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Dummy manager module. This will set up the information for dummy generation
 * and then call the appropriate dummy emitters.
 */
public class DummyManager implements CTCommonModule
{
	public static final String MODULE_NAME = "DUMMER";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	/**
	 * The directory name for the stubs
	 */
	public static final String DUMMY_PATH = "dummies";

	protected BuildConfig config;

	protected CommonResources resc;

	public DummyManager()
	{}

	public void run(CommonResources resources) throws ModuleException
	{
		resc = resources;
		config = resources.configuration();
		createDummies();
	}

	private void createDummies() throws ModuleException
	{
		File dummyPath = new File(config.getProject().getIntermediate(), DUMMY_PATH);
		if (!dummyPath.exists() && !dummyPath.mkdirs())
		{
			throw new ModuleException(String.format("Unable to create directory: %s", dummyPath.toString()),
					MODULE_NAME);
		}
		createProjectDummies(dummyPath, config.getProject());
	}

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
						.toString()), MODULE_NAME);
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
						.getPlatform().getId()), MODULE_NAME);
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
				throw new ModuleException(String.format("Error compiling dummies: %s", e.getMessage()), MODULE_NAME);
			}
		}
	}
}
