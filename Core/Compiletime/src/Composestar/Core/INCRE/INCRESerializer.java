package Composestar.Core.INCRE;

import java.io.File;
import java.io.IOException;

import Composestar.Core.Config.ModuleInfo;
import Composestar.Core.Config.ModuleInfoManager;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CompileHistory;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Perf.CPSTimer;

public class INCRESerializer implements CTCommonModule
{
	public static final String MODULE_NAME = "INCRESerializer";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	public INCRESerializer()
	{}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getModuleName()
	 */
	public String getModuleName()
	{
		return INCRESerializer.MODULE_NAME;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getDependencies()
	 */
	public String[] getDependencies()
	{
		return new String[] { DEPEND_ALL };
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getImportance()
	 */
	public ModuleImportance getImportance()
	{
		return ModuleImportance.REQUIRED;
	}

	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		ModuleInfo mi = ModuleInfoManager.get(MODULE_NAME);
		if (mi.getBooleanSetting("force"))
		{
			CPSTimer timer = CPSTimer.getTimer("INCRESerializer", "Creation of INCRE history");

			CompileHistory history = new CompileHistory(resources);
			File dest =
					new File(resources.configuration().getProject().getIntermediate(), CompileHistory.DEFAULT_FILENAME);
			try
			{
				logger.info("Saving compile history to: " + dest.toString());
				history.save(dest);
			}
			catch (IOException e)
			{
				logger.error("Unable to save compile history. Received exception: " + e, e);
				return ModuleReturnValue.ERROR;
			}
			finally
			{
				timer.stop();
			}
		}
		return ModuleReturnValue.OK;
	}
}
