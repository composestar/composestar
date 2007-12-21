package Composestar.Core.INCRE;

import java.io.File;
import java.io.IOException;

import Composestar.Core.Config.ModuleInfo;
import Composestar.Core.Config.ModuleInfoManager;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CompileHistory;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Perf.CPSTimer;

public class INCRESerializer implements CTCommonModule
{
	public static final String MODULE_NAME = "INCRESerializer";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	public INCRESerializer()
	{}

	public void run(CommonResources resources) throws ModuleException
	{
		ModuleInfo increMi = ModuleInfoManager.get(INCRE.MODULE_NAME);
		ModuleInfo mi = ModuleInfoManager.get(MODULE_NAME);
		if (increMi.getBooleanSetting("enabled") || mi.getBooleanSetting("force"))
		{
			CPSTimer timer = CPSTimer.getTimer("INCRESerializer", "Creation of INCRE history");

			CompileHistory history = new CompileHistory(DataStore.instance(), resources);
			File dest = new File(resources.configuration().getProject().getIntermediate(),
					CompileHistory.DEFAULT_FILENAME);
			try
			{
				logger.info("Saving compile history to: " + dest.toString());
				history.save(dest);
			}
			catch (IOException e)
			{
				logger.error("Unable to save compile history. Received exception: " + e, e);
			}
			timer.stop();
		}
	}
}
