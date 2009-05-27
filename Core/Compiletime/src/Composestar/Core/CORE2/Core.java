/*
 * Created on 13-mrt-2006
 *
 */
package Composestar.Core.CORE2;

import java.util.Collection;
import java.util.List;

import Composestar.Core.Annotations.ResourceManager;
import Composestar.Core.CpsRepository2.Concern;
import Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule;
import Composestar.Core.CpsRepository2.SIInfo.Superimposed;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FIRE2.model.FIRE2Resources;
import Composestar.Core.FIRE2.model.FireModel;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Logging.CPSLogger;

/**
 * CORE checks the filter sets, specified by the developer, for inconsistencies.
 * Unreachable filters or actions, conditions with a contradiction or tautology,
 * are examples of problems found by CORE. If a problem is found, the developer
 * will be notified.
 * 
 * @author Arjan de Roo
 */
public class Core implements CTCommonModule
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.CORE);

	@ResourceManager
	private FIRE2Resources f2res;

	/**
	 * Conflict detection algorithm
	 */
	private CoreConflictDetector detector = new CoreConflictDetector();

	/**
	 * Conflicts of severity ERROR where found
	 */
	private boolean isErrorFree;

	public Core()
	{}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getModuleName()
	 */
	public String getModuleName()
	{
		return ModuleNames.CORE;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getDependencies()
	 */
	public String[] getDependencies()
	{
		return new String[] { ModuleNames.FIRE, ModuleNames.FILTH };
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getImportance()
	 */
	public ModuleImportance getImportance()
	{
		return ModuleImportance.VALIDATION;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Resources
	 * .CommonResources)
	 */
	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		isErrorFree = true;
		// Iterate over all concerns
		for (Superimposed si : resources.repository().getAll(Superimposed.class))
		{
			if (!(si.getOwner() instanceof Concern))
			{
				continue;
			}
			// Check for conflicts
			findConflicts((Concern) si.getOwner());
		}
		if (!isErrorFree)
		{
			return ModuleReturnValue.ERROR;
		}
		return ModuleReturnValue.OK;
	}

	/**
	 * Process a concern for consistency errors
	 * 
	 * @param concern
	 */
	private void findConflicts(Concern concern)
	{
		logger.debug("Checking concern: " + concern.getName() + " ...");

		List<ImposedFilterModule> filterModules = concern.getSuperimposed().getFilterModuleOrder();
		FireModel fireModel = f2res.getFireModel(concern, filterModules);
		printConflicts(detector.findConflicts(fireModel));
	}

	/**
	 * Report the discovered conflicts
	 * 
	 * @param conflicts
	 */
	private void printConflicts(Collection<CoreConflict> conflicts)
	{
		for (CoreConflict conflict : conflicts)
		{
			if (conflict.getType().getSeverity() == ConflictSeverity.ERROR)
			{
				isErrorFree = false;
				logger.error(conflict.getDescription(), conflict.getLocation());
			}
			else
			{
				logger.warn(conflict.getDescription(), conflict.getLocation());
			}
		}
	}
}
