package Composestar.DotNET.SEMTEX;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.CommandLineExecutor;
import Composestar.Utils.FileUtils;
import Composestar.Utils.StringUtils;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Calls the SemanticAnalyser with the just created assemblies and the
 * SemanticComposestarPlugin. The result will be stored in the semtex.xml file.
 * 
 * @author Michiel van Oudheusden
 */
public class DotNETSemTexRunner implements CTCommonModule
{
	public static final String MODULE_NAME = "SEMTEX";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	public DotNETSemTexRunner()
	{}

	/**
	 * Calls the SemanticExtractor to generate the semtex.xml file
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		String exe = getExecutable(resources);
		if (exe != null)
		{
			List<String> cmdList = new ArrayList<String>();
			cmdList.add(exe);

			List builtLibs = (List) DataStore.instance().getObjectByID("BuiltLibs");
			Iterator it = builtLibs.iterator();
			while (it.hasNext())
			{
				String lib = (String) it.next();
				cmdList.add(FileUtils.quote(lib));
			}

			cmdList.add("/plugin:" + resources.getPathResolver().getResource("bin/SemanticComposestarPlugins.dll"));
			File xml = new File(resources.configuration().getProject().getIntermediate(), "semtex.xml");
			cmdList.add("/exportFilename:" + xml.toString());

			logger.debug("Command for .NET SemTex: " + StringUtils.join(cmdList));

			CommandLineExecutor cle = new CommandLineExecutor();
			int result = cle.exec(cmdList);
			if (result != 0)
			{
				throw new ModuleException("SemTex Analyzer failed with error: " + cle.outputError(), MODULE_NAME);
			}

			logger.debug("SemTex Analyzer completed.");
		}
	}

	private String getExecutable(CommonResources resources)
	{
		File exe = resources.getPathResolver().getResource("bin/SemanticExtractorConsole.exe");
		if (exe != null)
		{
			return exe.getAbsolutePath();
		}
		else
		{
			logger
					.info("SemTex Analyzer not found on its expected location: "
							+ resources.getPathResolver().getResource("bin")
							+ ". Semantic Analyzing will be skipped. "
							+ "See http://janus.cs.utwente.nl:8000/twiki/bin/view/Composer/SemanticAnalyser for more information.");

			return null;
		}
	}
}
