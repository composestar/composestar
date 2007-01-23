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
import java.util.Set;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.UnitResult;
import Composestar.Core.LOLA.LOLA;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;

/**
 * This Compose* module checks whether the used filter types can acutally be
 * found!
 * 
 * @author pascal
 */
public class FITER implements CTCommonModule
{
	public static final String MODULE_NAME = "FITER";

	public FITER()
	{}

	public void run(CommonResources resources) throws ModuleException
	{
		Debug.out(Debug.MODE_INFORMATION, MODULE_NAME, "Verifying Filter Types...");
		ArrayList<FilterType> customfilters = getCustomFilterTypes();
		resolveCustomFilterTypes(customfilters);
	}

	private ArrayList<FilterType> getCustomFilterTypes()
	{
		ArrayList<FilterType> customfilters = new ArrayList<FilterType>();
		DataStore ds = DataStore.instance();
		Iterator it = ds.getAllInstancesOf(FilterType.class);
		while (it.hasNext())
		{
			FilterType type = (FilterType) it.next();
			if (type.getType().equals(FilterType.CUSTOM))
			{
				customfilters.add(type);
			}
		}
		return customfilters;
	}

	private void resolveCustomFilterTypes(ArrayList<FilterType> customfilters) throws ModuleException
	{
		ArrayList<FilterType> working = new ArrayList<FilterType>(customfilters);
		ArrayList<FilterType> result = new ArrayList<FilterType>(customfilters);
		LOLA lola = (LOLA) INCRE.instance().getModuleByName(LOLA.MODULE_NAME);
		String filterSuperClass = "Composestar.RuntimeCore.FLIRT.Filtertypes.CustomFilter";
		if (lola.unitDict != null)
		{
			UnitResult ur = lola.unitDict.getByName(filterSuperClass, "Class");
			if (ur != null && ur.singleValue() != null)
			{
				ProgramElement filterType = ur.singleValue();
				Set allFilterTypes = getChildsofClass(filterType);
				for (Object obj : allFilterTypes)
				{
					if (obj instanceof ProgramElement)
					{
						ProgramElement customFilterType = (ProgramElement) obj;
						for (FilterType ftype : working)
						{
							if (customFilterType.getUnitName().indexOf('.') < 0)
							{
								if (customFilterType.getUnitName().endsWith(ftype.getName()))
								{
									Debug.out(Debug.MODE_INFORMATION, MODULE_NAME, "Resolved filter type: "
											+ ftype.getName() + " to " + customFilterType.getUnitName());
									result.remove(ftype);
								}
							}
							else
							{
								if (customFilterType.getUnitName().endsWith("." + ftype.getName()))
								{
									Debug.out(Debug.MODE_INFORMATION, MODULE_NAME, "Resolved filter type: "
											+ ftype.getName() + " to " + customFilterType.getUnitName());
									result.remove(ftype);
								}
							}
						}
					}
				}
			}
		}
		for (FilterType ftype : result)
		{
			Debug.out(Debug.MODE_ERROR, MODULE_NAME, "Unable to resolve filter type: " + ftype.getName() + "!");
		}

		if (!result.isEmpty())
		{
			throw new ModuleException("Unable to resolve filter type: " + ((FilterType) result.get(0)).getName() + "!",
					MODULE_NAME, (FilterType) (result.get(0)));
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
