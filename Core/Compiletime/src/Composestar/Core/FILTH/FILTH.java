package Composestar.Core.FILTH;

import java.util.Iterator;
import java.util.List;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.INCRETimer;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.SANE.SIinfo;
import Composestar.Utils.Debug;

/**
 * 
 */
public class FILTH implements CTCommonModule
{
	/**
	 * Calculates orders of the superimposed filtermodules
	 */
	public FILTH()
	{
	/* empty constructor */
	}

	/**
	 * 
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		/* get a INCRE instance */
		INCRE incre = INCRE.instance();

		INCRETimer filthinit = incre.getReporter().openProcess("FILTH", "Init FILTH service", INCRETimer.TYPE_NORMAL);
		/* first set the ordering spec file!!!!! */
		resources.addResource("ConstraintFile", "XMLTest.xml");
		/* get a FILTHService instance */
		FILTHService filthservice = FILTHService.getInstance(resources);
		InnerDispatcher.getInnerDispatchReference();
		filthinit.stop();

		Iterator conIter = DataStore.instance().getAllInstancesOf(Concern.class);
		while (conIter.hasNext())
		{
			Concern c = (Concern) conIter.next();

			SIinfo sinfo = (SIinfo) c.getDynObject(SIinfo.DATAMAP_KEY);
			if (sinfo != null)
			{
				List list;

				if (incre.isProcessedByModule(c, "FILTH"))
				{
					/* Copy FilterModuleOrders */
					INCRETimer filthcopy = incre.getReporter().openProcess("FILTH", c.getUniqueID(),
							INCRETimer.TYPE_INCREMENTAL);
					filthservice.copyOperation(c, incre);
					list = (List) c.getDynObject(FilterModuleOrder.ALL_ORDERS_KEY);
					filthcopy.stop();

				}
				else
				{
					/* Calculate FilterModuleOrders */
					INCRETimer filthrun = incre.getReporter().openProcess("FILTH", c.getUniqueID(),
							INCRETimer.TYPE_NORMAL);
					list = filthservice.getMultipleOrder(c);
					filthrun.stop();
				}

				if (list.size() > 1)
				{
					Debug.out(Debug.MODE_INFORMATION, "FILTH",
							"Encountered shared join point: " + c.getQualifiedName(), c.getDescriptionFileName());
					FilterModuleOrder singleOrder = (FilterModuleOrder) c
							.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY);
					String tmpstr = "";
					if (singleOrder != null)
					{
						List tmplist = singleOrder.orderAsList();
						for (int i = 0; i < tmplist.size(); i++)
						{
							String fmr = (String) tmplist.get(i);
							if (i != (tmplist.size() - 1))
							{
								tmpstr += fmr + " --> ";
							}
							else
							{
								tmpstr += fmr;
							}
						}
					}
					Debug.out(Debug.MODE_DEBUG, "FILTH", "Selecting filter module order: " + tmpstr);
				}
			}
		}
	}
}
