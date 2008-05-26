/*
 * Created on 13-mrt-2006
 *
 */
package Composestar.Core.CORE2;

import java.util.Iterator;

import Composestar.Core.Annotations.ComposestarModule;
import Composestar.Core.Annotations.ResourceManager;
import Composestar.Core.Annotations.ComposestarModule.Importance;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE2.model.FIRE2Resources;
import Composestar.Core.FIRE2.model.FireModel;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.SANE.SIinfo;
import Composestar.Utils.Logging.CPSLogger;

/**
 * CORE checks the filter sets, specified by the developer, for inconsistencies.
 * Unreachable filters or actions, conditions with a contradiction or tautology,
 * are examples of problems found by CORE. If a problem is found, the developer
 * will be notified.
 * 
 * @author Arjan de Roo
 */
@ComposestarModule(ID = ModuleNames.CORE, dependsOn = { ModuleNames.FIRE, ModuleNames.FILTH }, importancex = Importance.VALIDATION)
public class Core implements CTCommonModule
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.CORE);

	@ResourceManager
	private FIRE2Resources f2res;

	/**
	 * Conflict detection algorithm
	 */
	private CoreConflictDetector detector = new CoreConflictDetector();

	public Core()
	{}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Resources.CommonResources)
	 */
	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		// f2res = resources.getResourceManager(FIRE2Resources.class);
		// Iterate over all concerns
		Iterator<Concern> conIter = resources.repository().getAllInstancesOf(Concern.class);
		while (conIter.hasNext())
		{
			Concern concern = conIter.next();

			// Check whether the concern has filters superimposed
			if (concern.getDynObject(SIinfo.DATAMAP_KEY) != null)
			{
				// Check for conflicts
				findConflicts(concern);
			}
		}
		// TODO return Error when conflicts are detected
		return ModuleReturnValue.Ok;
	}

	/**
	 * Process a concern for consistency errors
	 * 
	 * @param concern
	 */
	private void findConflicts(Concern concern)
	{
		logger.debug("Checking concern: " + concern.getName() + " ...");

		FilterModuleOrder filterModules = (FilterModuleOrder) concern.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY);

		FireModel fireModel = f2res.getFireModel(concern, filterModules);

		CoreConflict[] conflicts = detector.findConflicts(fireModel);
		printConflicts(conflicts);
	}

	/**
	 * Report the discovered conflicts
	 * 
	 * @param conflicts
	 */
	private void printConflicts(CoreConflict[] conflicts)
	{
		for (CoreConflict conflict : conflicts)
		{
			logger.warn(conflict.getDescription(), conflict.getLocation());
		}
	}
}
