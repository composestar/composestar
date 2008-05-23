package Composestar.DotNET.VERIFY;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.Annotations.ComposestarModule;
import Composestar.Core.Config.Source;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.CommandLineExecutor;
import Composestar.Utils.StringUtils;
import Composestar.Utils.Logging.CPSLogger;

@ComposestarModule(ID = VERIFY.MODULE_NAME, dependsOn = { ModuleNames.WEAVER })
public class VERIFY implements CTCommonModule
{
	public static final String MODULE_NAME = "VERIFY";

	public static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	public VERIFY()
	{}

	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		List<String> assemblies = new ArrayList<String>();
		for (Source source : resources.configuration().getProject().getSources())
		{
			if (source.getAssembly() == null) continue;
			assemblies.add(source.getAssembly().toString());
		}

		verify(assemblies);
		return ModuleReturnValue.Ok;
	}

	private void verify(List<String> assemblies) throws ModuleException
	{
		CommandLineExecutor cle = new CommandLineExecutor();
		List<String> cmdList = new ArrayList<String>();
		cmdList.add("peverify");
		cmdList.add("/nologo");
		cmdList.add("<assembly>");

		Iterator<String> it = assemblies.iterator();
		while (it.hasNext())
		{
			String asmPath = (String) it.next();
			cmdList.set(2, asmPath);

			logger.debug("Command: " + StringUtils.join(cmdList));

			try
			{
				if (cle.exec(cmdList) != 0)
				{
					String stdout = cle.outputNormal();
					logger.debug(stdout);

					throw new ModuleException("Error verifying assembly '" + asmPath + "'", MODULE_NAME);
				}
			}
			catch (IOException e)
			{
				throw new ModuleException(e.getMessage(), MODULE_NAME, e);
			}
			catch (InterruptedException e)
			{
				throw new ModuleException(e.getMessage(), MODULE_NAME, e);
			}
		}
	}
}
