/*
 * Created on 13-mrt-2006
 *
 */
package Composestar.Core.CORE2;

import java.util.Iterator;

import Composestar.Core.Annotations.ResourceManager;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE2.model.FIRE2Resources;
import Composestar.Core.FIRE2.model.FireModel;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.SANE.SIinfo;
import Composestar.Utils.Logging.CPSLogger;

/**
 * @author Arjan de Roo
 */
public class Core implements CTCommonModule
{
	public static final String MODULE_NAME = "CORE";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	@ResourceManager
	private FIRE2Resources f2res;

	private CoreConflictDetector detector = new CoreConflictDetector();

	public Core()
	{}

	public void run(CommonResources resources) throws ModuleException
	{
		// f2res = resources.getResourceManager(FIRE2Resources.class);
		// Iterate over all concerns
		Iterator conIter = DataStore.instance().getAllInstancesOf(Concern.class);
		while (conIter.hasNext())
		{
			Concern concern = (Concern) conIter.next();

			// Check whether the concern has filters superimposed
			if (concern.getDynObject(SIinfo.DATAMAP_KEY) != null)
			{
				// Check for conflicts
				findConflicts(concern);
			}
		}
	}

	private void findConflicts(Concern concern)
	{
		logger.debug("Checking concern: " + concern.getName() + " ...");

		FilterModuleOrder filterModules = (FilterModuleOrder) concern.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY);

		FireModel fireModel = f2res.getFireModel(concern, filterModules);

		CoreConflict[] conflicts = detector.findConflicts(fireModel);
		printConflicts(conflicts);
	}

	private void printConflicts(CoreConflict[] conflicts)
	{
		for (CoreConflict conflict : conflicts)
		{
			logger.warn(conflict.getDescription(), conflict.getLocation());
		}
	}
}
