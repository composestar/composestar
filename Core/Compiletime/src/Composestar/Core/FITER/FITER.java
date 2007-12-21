/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.FITER;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import Composestar.Core.CpsProgramRepository.Legacy.LegacyCustomFilterType;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.UnitResult;
import Composestar.Core.LOLA.metamodel.UnitDictionary;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Logging.CPSLogger;

/**
 * This Compose* module checks whether the used filter types can acutally be
 * found!
 * 
 * @author pascal
 */
public class FITER implements CTCommonModule
{
	public static final String MODULE_NAME = "FITER";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	protected UnitDictionary unitDict;

	public FITER()
	{}

	public void run(CommonResources resources) throws ModuleException
	{
		logger.info("Verifying Filter Types...");
		unitDict = (UnitDictionary) resources.get(UnitDictionary.REPOSITORY_KEY);
		List<LegacyCustomFilterType> customfilters = getCustomFilterTypes();
		resolveCustomFilterTypes(customfilters);
	}

	private List<LegacyCustomFilterType> getCustomFilterTypes()
	{
		List<LegacyCustomFilterType> customfilters = new ArrayList<LegacyCustomFilterType>();
		DataStore ds = DataStore.instance();
		Iterator<LegacyCustomFilterType> it = ds.getAllInstancesOf(LegacyCustomFilterType.class);
		while (it.hasNext())
		{
			LegacyCustomFilterType type = (LegacyCustomFilterType) it.next();
			customfilters.add(type);
		}
		return customfilters;
	}

	private void resolveCustomFilterTypes(List<LegacyCustomFilterType> customfilters) throws ModuleException
	{
		List<LegacyCustomFilterType> working = new ArrayList<LegacyCustomFilterType>(customfilters);
		List<LegacyCustomFilterType> result = new ArrayList<LegacyCustomFilterType>(customfilters);
		String filterSuperClass = "Composestar.RuntimeCore.FLIRT.Filtertypes.CustomFilter";
		if (unitDict != null)
		{
			UnitResult ur = unitDict.getByName(filterSuperClass, "Class");
			if (ur != null && ur.singleValue() != null)
			{
				ProgramElement filterType = ur.singleValue();
				Set<ProgramElement> allFilterTypes = getChildsofClass(filterType);
				for (ProgramElement customFilterType : allFilterTypes)
				{
					for (LegacyCustomFilterType ftype : working)
					{
						if (customFilterType.getUnitName().indexOf('.') < 0)
						{
							if (customFilterType.getUnitName().endsWith(ftype.getName()))
							{
								logger.info("Resolved filter type: " + ftype.getName() + " to "
										+ customFilterType.getUnitName());
								result.remove(ftype);
							}
						}
						else
						{
							if (customFilterType.getUnitName().endsWith("." + ftype.getName()))
							{
								logger.info("Resolved filter type: " + ftype.getName() + " to "
										+ customFilterType.getUnitName());
								result.remove(ftype);
							}
						}
					}
				}
			}
		}
		for (LegacyCustomFilterType ftype : result)
		{
			logger.error("Unable to resolve filter type:" + ftype.getName() + "!");
		}

		if (!result.isEmpty())
		{
			throw new ModuleException("Unable to resolve filter type: " + result.get(0).getName() + "!", MODULE_NAME,
					result.get(0));
		}
	}

	private Set<ProgramElement> getChildsofClass(ProgramElement filterType)
	{
		Set<ProgramElement> total = new HashSet<ProgramElement>();
		@SuppressWarnings("unchecked")
		Set<ProgramElement> children = filterType.getUnitRelation("ChildClasses").multiValue();
		total.addAll(children);
		for (Object aChild : children)
		{
			total.addAll(getChildsofClass((ProgramElement) aChild));
		}
		return total;
	}

}
