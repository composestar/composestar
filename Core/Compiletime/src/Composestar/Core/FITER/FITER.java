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
import java.util.List;
import java.util.Set;

import Composestar.Core.Annotations.ComposestarModule;
import Composestar.Core.Annotations.ComposestarModule.Importance;
import Composestar.Core.COPPER2.FilterTypeMapping;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;
import Composestar.Core.CpsProgramRepository.Filters.LegacyCustomFilterType;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.Annotation;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.UnitResult;
import Composestar.Core.LOLA.metamodel.ERelationType;
import Composestar.Core.LOLA.metamodel.EUnitType;
import Composestar.Core.LOLA.metamodel.UnitDictionary;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Checks whether the used filter types can actually be found.
 * 
 * @author pascal
 */
@ComposestarModule(ID = ModuleNames.FITER, dependsOn = { ModuleNames.LOLA }, importancex = Importance.VALIDATION)
public class FITER implements CTCommonModule
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.FITER);

	protected static final String CUSTOM_FILER_CLASS = "Composestar.RuntimeCore.FLIRT.Filtertypes.CustomFilter";

	protected static final String FILTER_ACTION_ANNOT_BASE = "Composestar.RuntimeCore.FLIRT.Annotations.FilterAction";

	protected static final String FILTER_ACTION_ACCEPT_CALL = "Composestar.RuntimeCore.FLIRT.Annotations.FilterActionAcceptCall";

	protected static final String FILTER_ACTION_ACCEPT_RETURN = "Composestar.RuntimeCore.FLIRT.Annotations.FilterActionAcceptReturn";

	protected static final String FILTER_ACTION_REJECT_CALL = "Composestar.RuntimeCore.FLIRT.Annotations.FilterActionRejectCall";

	protected static final String FILTER_ACTION_REJECT_RETURN = "Composestar.RuntimeCore.FLIRT.Annotations.FilterActionRejectReturn";

	protected UnitDictionary unitDict;

	protected CommonResources resc;

	public FITER()
	{}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Resources.CommonResources)
	 */
	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		logger.info("Verifying Filter Types...");
		resc = resources;
		unitDict = (UnitDictionary) resources.get(UnitDictionary.REPOSITORY_KEY);
		List<LegacyCustomFilterType> customfilters = getCustomFilterTypes(resources);
		if (customfilters.size() > 0)
		{
			resolveCustomFilterTypes(customfilters);
		}
		// Return error when filters could not be resolved (instead of
		// exception)
		return ModuleReturnValue.Ok;
	}

	/**
	 * Retrieves the list of custom legacy filter types
	 * 
	 * @param resources
	 * @return
	 */
	private List<LegacyCustomFilterType> getCustomFilterTypes(CommonResources resources)
	{
		List<LegacyCustomFilterType> customfilters = new ArrayList<LegacyCustomFilterType>();
		FilterTypeMapping filterTypes = resources.get(FilterTypeMapping.RESOURCE_KEY);
		for (FilterType ft : filterTypes.getFilterTypes())
		{
			if (ft instanceof LegacyCustomFilterType)
			{
				customfilters.add((LegacyCustomFilterType) ft);
			}
		}
		return customfilters;
	}

	/**
	 * Resolve the custom filter types by checking if the type harvester found a
	 * type with the correct name
	 * 
	 * @param customfilters
	 * @throws ModuleException
	 */
	private void resolveCustomFilterTypes(List<LegacyCustomFilterType> customfilters) throws ModuleException
	{
		List<LegacyCustomFilterType> working = new ArrayList<LegacyCustomFilterType>(customfilters);
		List<LegacyCustomFilterType> result = new ArrayList<LegacyCustomFilterType>(customfilters);
		if (unitDict != null)
		{
			UnitResult ur = unitDict.getByName(CUSTOM_FILER_CLASS, EUnitType.CLASS.toString());
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
								ftype.setClassName(customFilterType.getUnitName());
								result.remove(ftype);
								harvestFilterActions(ftype, customFilterType);
							}
						}
						else
						{
							if (customFilterType.getUnitName().endsWith("." + ftype.getName()))
							{
								logger.info("Resolved filter type: " + ftype.getName() + " to "
										+ customFilterType.getUnitName());
								ftype.setClassName(customFilterType.getUnitName());
								result.remove(ftype);
								harvestFilterActions(ftype, customFilterType);
							}
						}
					}
				}
			}
		}
		for (LegacyCustomFilterType ftype : result)
		{
			logger.error("Unable to resolve filter type: " + ftype.getName());
		}

		if (!result.isEmpty())
		{
			throw new ModuleException("Unable to resolve custom filter types", ModuleNames.FITER);
		}
	}

	/**
	 * Get all subclasses of a given type
	 * 
	 * @param filterType
	 * @return
	 */
	private Set<ProgramElement> getChildsofClass(ProgramElement filterType)
	{
		Set<ProgramElement> total = new HashSet<ProgramElement>();
		@SuppressWarnings("unchecked")
		Set<ProgramElement> children = filterType.getUnitRelation(ERelationType.CHILD_CLASSES.toString()).multiValue();
		total.addAll(children);
		for (Object aChild : children)
		{
			total.addAll(getChildsofClass((ProgramElement) aChild));
		}
		return total;
	}

	/**
	 * Harvest filter action information from the annotations of the filter
	 * types.
	 * 
	 * @param ft
	 * @param type
	 */
	private void harvestFilterActions(LegacyCustomFilterType ft, ProgramElement type)
	{
		List<Annotation> annots = (List<Annotation>) type.getAnnotations();
		for (Annotation annot : annots)
		{
			if (annot.getType() == null)
			{
				continue;
			}
			String typeName = annot.getType().getFullName();
			if (typeName == null || typeName.length() == 0)
			{
				typeName = annot.getTypeName();
			}
			if (typeName == null || typeName.length() == 0)
			{
				continue;
			}
			if (!typeName.startsWith(FILTER_ACTION_ANNOT_BASE))
			{
				continue;
			}

			FilterAction fa = new FilterAction();
			fa.setName(ft.getName() + annot.getType().getName());
			fa.setFullName(fa.getName());

			String val = annot.getValue("mcb");
			if ("Substituted".equalsIgnoreCase(val))
			{
				fa.setMessageChangeBehaviour(FilterAction.MESSAGE_SUBSTITUTED);
			}
			else if ("Any".equalsIgnoreCase(val))
			{
				fa.setMessageChangeBehaviour(FilterAction.MESSAGE_ANY);
			}
			else
			{
				fa.setMessageChangeBehaviour(FilterAction.MESSAGE_ORIGINAL);
			}

			val = annot.getValue("flow");
			if ("Return".equalsIgnoreCase(val))
			{
				fa.setFlowBehaviour(FilterAction.FLOW_RETURN);
			}
			else if ("Exit".equalsIgnoreCase(val))
			{
				fa.setFlowBehaviour(FilterAction.FLOW_EXIT);
			}
			else
			{
				fa.setFlowBehaviour(FilterAction.FLOW_CONTINUE);
			}

			val = annot.getValue("operations");
			if (val != null)
			{
				fa.setResourceOperations(val);
			}

			if (typeName.equals(FILTER_ACTION_ACCEPT_CALL))
			{
				ft.setAcceptCallAction(fa);
			}
			else if (typeName.equals(FILTER_ACTION_ACCEPT_RETURN))
			{
				ft.setAcceptReturnAction(fa);
			}
			else if (typeName.equals(FILTER_ACTION_REJECT_CALL))
			{
				ft.setRejectCallAction(fa);
			}
			else if (typeName.equals(FILTER_ACTION_REJECT_RETURN))
			{
				ft.setRejectReturnAction(fa);
			}
			resc.repository().addObject(fa);
		}
	}
}
