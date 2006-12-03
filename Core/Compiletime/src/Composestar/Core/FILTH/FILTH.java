/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
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
					INCRETimer filthrun = incre.getReporter().openProcess("FILTH", c.getUniqueID(), INCRETimer.TYPE_NORMAL);
					list = filthservice.getMultipleOrder(c);
					filthrun.stop();
				}

				if (list.size() > 1)
				{
					Debug.out(Debug.MODE_INFORMATION, "FILTH",
							"Encountered shared join point: " + c.getQualifiedName(), c);
					
					FilterModuleOrder singleOrder = 
						(FilterModuleOrder)c.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY);
					
					StringBuffer sb = new StringBuffer();
					if (singleOrder != null)
					{
						List tmplist = singleOrder.orderAsList();
						int last = tmplist.size() - 1;
						for (int i = 0; i < tmplist.size(); i++)
						{
							String fmr = (String) tmplist.get(i);
							sb.append(fmr);
							if (i != last) sb.append(" --> ");
						}
					}
					Debug.out(Debug.MODE_DEBUG, "FILTH", "Selecting filter module order: " + sb);
				}
			}
		}
	}
}
