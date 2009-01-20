/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Java.FITER;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Composestar.Core.COPPER3.FilterTypeFactory;
import Composestar.Core.CpsRepository2.Filters.FilterAction;
import Composestar.Core.CpsRepository2.Filters.FilterActionNames;
import Composestar.Core.CpsRepository2.Filters.FilterAction.FlowBehavior;
import Composestar.Core.CpsRepository2Impl.Filters.CustomFilterType;
import Composestar.Core.CpsRepository2Impl.Filters.FilterActionImpl;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.Annotation;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.Type;
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
// @ComposestarModule(ID = ModuleNames.FITER, dependsOn = { ModuleNames.LOLA },
// importance = Importance.VALIDATION)
public class FITER implements CTCommonModule
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.FITER);

	protected static final String FILER_ACTION_CLASS = "Composestar.Java.FLIRT.Actions.RTFilterAction";

	protected static final String CUSTOM_FILER_CLASS = "Composestar.Java.FLIRT.Filters.RTCustomFilterType";

	protected static final String FILTER_ACTION_ANNOT = "Composestar.Java.FLIRT.Annotations.FilterActionDef";

	protected static final String FILTER_TYPE_ANNOT = "Composestar.Java.FLIRT.Annotations.FilterTypeDef";

	protected UnitDictionary unitDict;

	protected CommonResources resc;

	protected Map<String, FilterAction> harvestedFilterActions;

	protected Set<String> missingActions;

	protected FilterTypeFactory filterFactory;

	public FITER()
	{}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getModuleName()
	 */
	public String getModuleName()
	{
		return ModuleNames.FITER;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getDependencies()
	 */
	public String[] getDependencies()
	{
		return new String[] { ModuleNames.LOLA };
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
		logger.info("Verifying Filter Types...");
		resc = resources;
		harvestedFilterActions = new HashMap<String, FilterAction>();
		missingActions = new HashSet<String>();
		filterFactory = resources.get(FilterTypeFactory.RESOURCE_KEY);
		unitDict = (UnitDictionary) resources.get(UnitDictionary.REPOSITORY_KEY);
		Set<CustomFilterType> customfilters = resources.repository().getAllSet(CustomFilterType.class);
		if (customfilters.size() > 0)
		{
			return resolveCustomFilterTypes(customfilters);
		}
		// Return error when filters could not be resolved (instead of
		// exception)
		return ModuleReturnValue.OK;
	}

	/**
	 * Resolve the custom filter types by checking if the type harvester found a
	 * type with the correct name
	 * 
	 * @param customfilters
	 * @throws ModuleException
	 */
	private ModuleReturnValue resolveCustomFilterTypes(Set<CustomFilterType> customfilters) throws ModuleException
	{
		if (unitDict != null)
		{
			UnitResult ur = unitDict.getByName(FILER_ACTION_CLASS, EUnitType.CLASS.toString());
			if (ur != null && ur.singleValue() != null)
			{
				ProgramElement filterType = ur.singleValue();
				Set<ProgramElement> allActions = getChildsofClass(filterType);
				for (ProgramElement actionType : allActions)
				{
					harvestFilterAction(actionType);
				}
			}

			ur = unitDict.getByName(CUSTOM_FILER_CLASS, EUnitType.CLASS.toString());
			if (ur != null && ur.singleValue() != null)
			{
				ProgramElement filterType = ur.singleValue();
				Set<ProgramElement> allFilterTypes = getChildsofClass(filterType);
				for (ProgramElement customFilterType : allFilterTypes)
				{
					harvestFilterType(customFilterType, customfilters);
				}
			}
		}
		for (CustomFilterType ftype : customfilters)
		{
			logger.error("Unable to resolve filter type: " + ftype.getFilterName());
		}

		if (!customfilters.isEmpty() && !missingActions.isEmpty())
		{
			return ModuleReturnValue.ERROR;
		}
		return ModuleReturnValue.OK;
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
		Set<? extends ProgramElement> children =
				filterType.getUnitRelation(ERelationType.CHILD_CLASSES.toString()).multiValue();
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
	private void harvestFilterType(ProgramElement type, Set<CustomFilterType> customfilters)
	{
		List<Annotation> annots = type.getAnnotations();
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
			if (!typeName.equals(FILTER_TYPE_ANNOT))
			{
				continue;
			}
			String ftName = annot.getValue("name");
			for (CustomFilterType ft : customfilters)
			{
				if (ft.getFilterName().equalsIgnoreCase(ftName))
				{
					logger.info(String.format("Resolved custom filter type %s to %s", ft.getFilterName(), ((Type) type)
							.getFullName()));
					completeCustomFilterType(ft, annot);
					customfilters.remove(ft);
					return;
				}
			}
		}
	}

	/**
	 * Complete the custom filter type
	 * 
	 * @param ft
	 * @param annot
	 */
	protected void completeCustomFilterType(CustomFilterType ft, Annotation annot)
	{
		ft.setAcceptCallAction(getFilterAction(annot.getValue("acceptCall")));
		ft.setAcceptReturnAction(getFilterAction(annot.getValue("acceptReturn")));
		ft.setRejectCallAction(getFilterAction(annot.getValue("rejectCall")));
		ft.setRejectReturnAction(getFilterAction(annot.getValue("rejectReturn")));
	}

	/**
	 * Get the filter action by its name
	 * 
	 * @param actionClass
	 * @return
	 */
	protected FilterAction getFilterAction(String actionClass)
	{
		if (actionClass.startsWith("class "))
		{
			// class to string always starts with "class TheclassName"
			actionClass = actionClass.substring(6);
		}
		if (harvestedFilterActions.containsKey(actionClass))
		{
			return harvestedFilterActions.get(actionClass);
		}
		UnitResult ur = unitDict.getByName(actionClass, EUnitType.CLASS.toString());
		if (ur != null && ur.singleValue() != null)
		{
			harvestFilterAction(ur.singleValue());
			if (harvestedFilterActions.containsKey(actionClass))
			{
				return harvestedFilterActions.get(actionClass);
			}
		}
		logger.error(String.format("Could not find filter action '%s'", actionClass));
		missingActions.add(actionClass);
		return filterFactory.getContinueAction();
	}

	/**
	 * Harvest the filter action
	 * 
	 * @param type
	 */
	protected void harvestFilterAction(ProgramElement type)
	{
		if (type == null)
		{
			return;
		}
		List<Annotation> annots = type.getAnnotations();
		for (Annotation annot : annots)
		{
			if (annot.getType() == null)
			{
				continue;
			}
			String annotType = annot.getType().getFullName();
			if (annotType == null || annotType.length() == 0)
			{
				annotType = annot.getTypeName();
			}
			if (annotType == null || annotType.length() == 0)
			{
				continue;
			}
			if (!annotType.equals(FILTER_ACTION_ANNOT))
			{
				continue;
			}
			String typeName = ((Type) type).getFullName();
			String faName = annot.getValue("name");
			if (faName.equals(FilterActionNames.CONTINUE_ACTION))
			{
				harvestedFilterActions.put(typeName, filterFactory.getContinueAction());
				return;
			}
			else if (faName.equals(FilterActionNames.ADVICE_ACTION))
			{
				harvestedFilterActions.put(typeName, filterFactory.getAdviceAction());
				return;
			}
			else if (faName.equals(FilterActionNames.DISPATCH_ACTION))
			{
				harvestedFilterActions.put(typeName, filterFactory.getDispatchAction());
				return;
			}
			else if (faName.equals(FilterActionNames.ERROR_ACTION))
			{
				harvestedFilterActions.put(typeName, filterFactory.getErrorAction());
				return;
			}
			else if (faName.equals(FilterActionNames.META_ACTION))
			{
				harvestedFilterActions.put(typeName, filterFactory.getMetaAction());
				return;
			}
			else if (faName.equals(FilterActionNames.SEND_ACTION))
			{
				harvestedFilterActions.put(typeName, filterFactory.getSendAction());
				return;
			}
			else if (faName.equals(FilterActionNames.SUBSTITUTION_ACTION))
			{
				// subst doesn't do anything
				harvestedFilterActions.put(typeName, filterFactory.getContinueAction());
				return;
			}

			FilterActionImpl fa = new FilterActionImpl(faName, typeName);
			resc.repository().add(fa);
			harvestedFilterActions.put(typeName, fa);

			fa.setResourceOperations(annot.getValue("resourceOperations"));
			String flow = annot.getValue("messageChangeBehavior");
			if ("continue".equalsIgnoreCase(flow))
			{
				fa.setFlowBehavior(FlowBehavior.CONTINUE);
			}
			else if ("return".equalsIgnoreCase(flow))
			{
				fa.setFlowBehavior(FlowBehavior.RETURN);
			}
			else if ("exit".equalsIgnoreCase(flow))
			{
				fa.setFlowBehavior(FlowBehavior.EXIT);
			}

			// FIXME add argument processing

			logger.info(String.format("Resolved custom filter actions %s to %s", fa.getName(), fa.getSystemName()));
			return;
		}
	}
}
