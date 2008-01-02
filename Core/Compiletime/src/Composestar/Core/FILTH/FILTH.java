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
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.SANE.FilterModuleSuperImposition;
import Composestar.Core.SANE.SIinfo;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Perf.CPSTimer;

/**
 * Calculates orders of the superimposed filtermodules
 */
public class FILTH implements CTCommonModule
{
	public static final String MODULE_NAME = "FILTH";

	public static final String FILTER_ORDERING_SPEC = "FILTER_ORDERING_SPEC";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	public FILTH()
	{}

	public void run(CommonResources resources) throws ModuleException
	{
		FILTHService filthservice = new FILTHServiceImpl(resources, InnerDispatcher
				.createInnerDispatchReference(resources.repository()));

		Iterator<Concern> conIter = resources.repository().getAllInstancesOf(Concern.class);
		while (conIter.hasNext())
		{
			Concern c = conIter.next();

			SIinfo sinfo = (SIinfo) c.getDynObject(SIinfo.DATAMAP_KEY);
			if (sinfo != null)
			{
				List<List<FilterModuleSuperImposition>> list;

				/* Calculate FilterModuleOrders */
				CPSTimer timer = CPSTimer.getTimer(MODULE_NAME, c.getUniqueID());
				list = filthservice.getMultipleOrder(c);
				timer.stop();

				if (list.size() > 1)
				{
					logger.info("Encountered shared join point: " + c.getQualifiedName(), c);

					FilterModuleOrder singleOrder = (FilterModuleOrder) c
							.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY);

					StringBuffer sb = new StringBuffer();
					if (singleOrder != null)
					{
						List<FilterModuleSuperImposition> tmplist = singleOrder.filterModuleSIList();
						int last = tmplist.size() - 1;
						for (int i = 0; i < tmplist.size(); i++)
						{
							sb.append(tmplist.get(i).getFilterModule().getRef().getOriginalQualifiedName());
							if (i != last)
							{
								sb.append(" --> ");
							}
						}
					}
					logger.debug("Selecting filter module order: " + sb.toString());
				}
			}
		}
	}
}
