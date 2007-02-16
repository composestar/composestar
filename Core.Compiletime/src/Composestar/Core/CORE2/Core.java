/*
 * Created on 13-mrt-2006
 *
 */
package Composestar.Core.CORE2;

import java.util.Iterator;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE2.model.FireModel;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.SANE.SIinfo;
import Composestar.Utils.Debug;

/**
 * @author Arjan de Roo
 */
public class Core implements CTCommonModule
{
	public static final String MODULE_NAME = "CORE";

	private CoreConflictDetector detector = new CoreConflictDetector();

	public Core()
	{}

	public void run(CommonResources resources) throws ModuleException
	{
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
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Checking concern:   o/ ");
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Checking concern:  /|   ... " + concern.getName() + " ...");
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Checking concern:  / \\ ");

		FilterModuleOrder filterModules = (FilterModuleOrder) concern.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY);

		FireModel fireModel = new FireModel(concern, filterModules);

		CoreConflict[] conflicts = detector.findConflicts(fireModel);
		printConflicts(conflicts);
	}

	private void printConflicts(CoreConflict[] conflicts)
	{
		for (CoreConflict conflict : conflicts)
		{
			Debug.out(Debug.MODE_WARNING, MODULE_NAME, conflict.getDescription(), conflict.getLocation());
		}
	}
}
