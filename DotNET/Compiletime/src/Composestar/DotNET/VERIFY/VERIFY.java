package Composestar.DotNET.VERIFY;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Project;
import Composestar.Core.Master.Config.Projects;
import Composestar.Utils.CommandLineExecutor;
import Composestar.Utils.Debug;
import Composestar.Utils.StringUtils;

public class VERIFY implements CTCommonModule
{
	public static final String MODULE_NAME = "VERIFY";
	
	private Configuration config;
	
	public VERIFY()
	{
		config = Configuration.instance();
	}

	public void run(CommonResources resources) throws ModuleException
	{
		List assemblies = new ArrayList();
		
		Projects solution = config.getProjects();
		File outDir = new File(solution.getOutputPath());
		
		Iterator it = solution.getProjects().iterator();
		while (it.hasNext())
		{
			Project project = (Project)it.next();
			File outFile = new File(outDir, project.getName() + ".exe");
			assemblies.add(outFile.getAbsolutePath());
		}

		verify(assemblies);
	}
	
	private void verify(List assemblies) throws ModuleException
	{
		CommandLineExecutor cle = new CommandLineExecutor();
		List cmdList = new ArrayList();
		cmdList.add("peverify");
		cmdList.add("/nologo");
		cmdList.add("<assembly>");
		
		Iterator it = assemblies.iterator();
		while (it.hasNext())
		{
			String asmPath = (String)it.next();
			cmdList.set(2, asmPath);

			debug("Command: " + StringUtils.join(cmdList));

			if (cle.exec(cmdList) != 0)
			{
				String stdout = cle.outputNormal();
				debug(stdout);
				
				throw new ModuleException("Error verifying assembly '" + asmPath + "'", MODULE_NAME);
			}
		}		
	}

	private void debug(String msg)
	{
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, msg);
	}
}
