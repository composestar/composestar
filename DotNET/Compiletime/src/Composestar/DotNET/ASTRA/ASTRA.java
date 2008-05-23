package Composestar.DotNET.ASTRA;

import java.io.File;
import java.util.Iterator;

import Composestar.Core.Annotations.ComposestarModule;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.Resources.ResourceException;
import Composestar.DotNET.COMP.DotNETCompiler;
import Composestar.Utils.Logging.CPSLogger;

/**
 * ASTRA applies the signature changes determined by SIGN to the dummies
 * assembly.
 */
@ComposestarModule(ID = ASTRA.MODULE_NAME, dependsOn = { ModuleNames.SIGN })
public class ASTRA implements CTCommonModule
{
	public static final String MODULE_NAME = "ASTRA";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	public ASTRA()
	{}

	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		// only run ASTRA when SIGN found ADDED or REMOVED methods
		try
		{
			if (!resources.getBoolean("signaturesmodified"))
			{
				logger.debug("No need to transform assemblies");
				return ModuleReturnValue.Ok;
			}
		}
		catch (ResourceException e)
		{
			logger.info("No need to transform assemblies");
			return ModuleReturnValue.Ok;
		}

		ILCodeParser codeParser = new ILCodeParser();

		DataStore ds = resources.repository();
		Iterator iterator = ds.getAllInstancesOf(Concern.class);
		while (iterator.hasNext())
		{
			Concern concern = (Concern) iterator.next();
			codeParser.addConcern(concern);
		}

		try
		{
			codeParser.setAssemblyName((File) resources.get(DotNETCompiler.DUMMY_ASSEMBLY));
			codeParser.run();
		}
		catch (ModifierException e)
		{
			throw new ModuleException(e.getMessage(), MODULE_NAME);
		}
		return ModuleReturnValue.Ok;
	}
}
