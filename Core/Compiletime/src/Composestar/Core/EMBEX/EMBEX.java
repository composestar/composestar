package Composestar.Core.EMBEX;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import Composestar.Core.Annotations.ComposestarModule;
import Composestar.Core.Config.BuildConfig;
import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.Implementation;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.Source;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.FileUtils;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Extracts the embedded sources from the concern sources and saves them to the
 * disk for further use.
 */
@ComposestarModule(ID = ModuleNames.EMBEX, dependsOn = { ModuleNames.COPPER })
public class EMBEX implements CTCommonModule
{
	public static final String EMBEDDED_PATH = "embedded";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.EMBEX);

	protected BuildConfig config;

	public EMBEX()
	{}

	/**
	 * Iterates over cps concerns and calls saveToFile for embedded sources
	 * found Creates directory for embedded sources
	 */
	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		config = resources.configuration();
		DataStore ds = resources.repository();
		// create directory for embedded code
		File embeddedDir = new File(config.getProject().getIntermediate(), EMBEDDED_PATH);
		if (!embeddedDir.exists() && !embeddedDir.mkdirs())
		{
			throw new ModuleException(String.format("Unable to create directory for embedded sources: %s", embeddedDir
					.toString()), ModuleNames.EMBEX);
		}

		// iterate over all cps concerns
		Iterator<CpsConcern> concernIt = ds.getAllInstancesOf(CpsConcern.class);
		while (concernIt.hasNext())
		{
			// fetch implementation
			CpsConcern cps = concernIt.next();
			Implementation imp = cps.getImplementation();

			if (imp instanceof Source)
			{
				// fetch embedded source and save
				Source sourceCode = (Source) imp;
				String language = sourceCode.getLanguage();
				File target = new File(embeddedDir, sourceCode.getSourceFile());

				logger.debug("Found embedded source: " + sourceCode.getClassName() + "; Language: " + language
						+ "; File: " + sourceCode.getSourceFile());
				Composestar.Core.Config.Source source = new Composestar.Core.Config.Source();
				source.setEmbedded(true);
				source.setLanguage(language);
				source.setFile(target);
				config.getProject().addSource(source);
				config.getProject().getTypeMapping().addType(sourceCode.getClassName(), source);

				logger.debug("Added embedded code to project: " + config.getProject().getName());

				if (sourceCode.getSource() != null)
				{
					saveToFile(target, sourceCode);
				}
			}
		}
		// TODO return Error when code could not be extracted
		return ModuleReturnValue.Ok;
	}

	/**
	 * Stores the embedded source in a new file.
	 */
	private void saveToFile(File target, Source src) throws ModuleException
	{
		BufferedWriter bw = null;
		try
		{
			bw = new BufferedWriter(new FileWriter(target));
			bw.write(src.getSource());
		}
		catch (IOException e)
		{
			throw new ModuleException("Could not save embedded source: " + e.getMessage(), ModuleNames.EMBEX);
		}
		finally
		{
			FileUtils.close(bw);
		}
	}
}
