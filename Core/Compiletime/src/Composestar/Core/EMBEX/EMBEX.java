package Composestar.Core.EMBEX;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import Composestar.Core.Annotations.ResourceManager;
import Composestar.Core.Config.BuildConfig;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.FileUtils;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Extracts the embedded sources from the concern sources and saves them to the
 * disk for further use.
 */
public class EMBEX implements CTCommonModule
{
	public static final String EMBEDDED_PATH = "embedded";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.EMBEX);

	protected BuildConfig config;

	@ResourceManager
	protected EmbeddedSources embeddedSourceManager;

	public EMBEX()
	{}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getModuleName()
	 */
	public String getModuleName()
	{
		return ModuleNames.EMBEX;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getDependencies()
	 */
	public String[] getDependencies()
	{
		return new String[] { ModuleNames.COPPER };
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getImportance()
	 */
	public ModuleImportance getImportance()
	{
		return ModuleImportance.REQUIRED;
	}

	/**
	 * Iterates over cps concerns and calls saveToFile for embedded sources
	 * found Creates directory for embedded sources
	 */
	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		config = resources.configuration();
		// create directory for embedded code
		File embeddedDir = new File(config.getProject().getIntermediate(), EMBEDDED_PATH);
		if (!embeddedDir.exists() && !embeddedDir.mkdirs())
		{
			throw new ModuleException(String.format("Unable to create directory for embedded sources: %s", embeddedDir
					.toString()), ModuleNames.EMBEX);
		}

		boolean success = true;
		for (EmbeddedSource src : embeddedSourceManager.getSources())
		{
			String language = src.getLanguage();
			File target = new File(embeddedDir, src.getFilename());

			logger.debug("Found embedded source; Language: " + language + "; File: " + src.getFilename(), src
					.getSourceInformation());
			Composestar.Core.Config.Source source = new Composestar.Core.Config.Source();
			source.setEmbedded(true);
			// TODO: set original source location and line offset
			source.setLanguage(language);
			source.setFile(target);
			config.getProject().addSource(source);

			logger.debug("Added embedded code to project: " + config.getProject().getName());

			if (src.getCode() != null)
			{
				success &= saveToFile(target, src);
			}
		}
		if (success)
		{
			return ModuleReturnValue.OK;
		}
		else
		{
			return ModuleReturnValue.ERROR;
		}
	}

	/**
	 * Stores the embedded source in a new file.
	 */
	private boolean saveToFile(File target, EmbeddedSource src) throws ModuleException
	{
		BufferedWriter bw = null;
		try
		{
			bw = new BufferedWriter(new FileWriter(target));
			bw.write(src.getCode());
			return true;
		}
		catch (IOException e)
		{
			logger.error("Could not save embedded source: " + e.getMessage(), src.getSourceInformation());
		}
		finally
		{
			FileUtils.close(bw);
		}
		return false;
	}
}
