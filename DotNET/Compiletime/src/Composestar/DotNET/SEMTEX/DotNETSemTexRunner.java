package Composestar.DotNET.SEMTEX;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.CommandLineExecutor;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;
import Composestar.Utils.StringUtils;

/**
 * Calls the SemanticAnalyser with the just created assemblies and the SemanticComposestarPlugin.
 * The result will be stored in the semtex.xml file.
 * @author Michiel van Oudheusden
 */
public class DotNETSemTexRunner implements CTCommonModule
{
	public DotNETSemTexRunner()
	{
	}

	/**
	 * Calls the SemanticExtractor to generate the semtex.xml file
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		Configuration config = Configuration.instance();
		String cpsPath = config.getPathSettings().getPath("Composestar");
		String projectPath = config.getPathSettings().getPath("Base");

		String exe = getExecutable();
		if (exe != null)
		{
			List cmdList = new ArrayList();
			cmdList.add(exe);

			List builtLibs = (List)DataStore.instance().getObjectByID("BuiltLibs");
			Iterator it = builtLibs.iterator();
			while (it.hasNext())
			{
				String lib = (String)it.next();
				cmdList.add(FileUtils.quote(lib));
			}

			cmdList.add("/plugin:" + FileUtils.quote(cpsPath + "binaries/SemanticComposestarPlugins.dll"));
			cmdList.add("/exportFilename:" + FileUtils.quote(projectPath + "obj/semtex.xml"));

			Debug.out(Debug.MODE_DEBUG,"SEMTEX","Command for DotNet SemTex: " + StringUtils.join(cmdList));

			CommandLineExecutor cle = new CommandLineExecutor();
			int result = cle.exec(cmdList);			
			if (result != 0)
				throw new ModuleException("SemTex Analyzer failed with error: " + cle.outputError(),"SEMTEX");

			Debug.out(Debug.MODE_DEBUG,"SEMTEX","SemTex Analyzer completed.");
		}
	}

	private String getExecutable()
	{
		Configuration config = Configuration.instance();
		String cpsPath = config.getPathSettings().getPath("Composestar");
		File exe = new File(cpsPath, "binaries/SemanticExtractorConsole.exe");
		
		if (exe.exists())
			return exe.getAbsolutePath();
		else
		{
			Debug.out(Debug.MODE_WARNING , "SEMTEX", "SemTex Analyzer not found on it's expected location: " + exe + ". Semantic Analyzing will be skipped.");
			Debug.out(Debug.MODE_INFORMATION , "SEMTEX", "Semantic Analyzer cannot be executed because of missing files. See http://janus.cs.utwente.nl:8000/twiki/bin/view/Composer/SemanticAnalyser for more information.");
			return null;
		}
	}
}
