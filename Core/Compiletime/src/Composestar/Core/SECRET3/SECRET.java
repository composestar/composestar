/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2004-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.SECRET3;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;

import Composestar.Core.Annotations.ComposestarModule;
import Composestar.Core.Annotations.ModuleSetting;
import Composestar.Core.Annotations.ResourceManager;
import Composestar.Core.Annotations.ComposestarModule.Importance;
import Composestar.Core.CpsRepository2.Concern;
import Composestar.Core.CpsRepository2.Filters.FilterAction;
import Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule;
import Composestar.Core.Exception.ConfigurationException;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FIRE2.model.FIRE2Resources;
import Composestar.Core.FIRE2.util.regex.RegularState;
import Composestar.Core.FIRE2.util.regex.RegularTransition;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.SECRET3.Config.ConflictRule;
import Composestar.Core.SECRET3.Config.OperationSequence;
import Composestar.Core.SECRET3.Config.Resource;
import Composestar.Core.SECRET3.Config.ResourceType;
import Composestar.Core.SECRET3.Config.Xml.XmlConfiguration;
import Composestar.Core.SECRET3.Report.SECRETReport;
import Composestar.Core.SECRET3.Report.XMLReport;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Logging.LogMessage;
import Composestar.Utils.Perf.CPSTimer;

// FIXME: rename package to SECRET
//FIXME not yet adjusted to the canonical model
/**
 * If multiple filtermodules are imposed on the same joinpoint, certain
 * conflicts may be introduced. The concern containing these filtermodules are
 * often developed at different times and locations by different developers.
 * These filtermodules may have unintended side effects which only effect other
 * filtermodules. If these aspects are combined, semantic conflicts becomes
 * apparent. SECRET aims to reason about these kind of semantic conflicts. It
 * does a static analysis on the semantics of the filters and detects possible
 * conflicts. The used model is, through the use of an XML input specification,
 * completely user adaptable. In input specification, the accept- and
 * reject-actions of filtertypes are specified. Every action is specified by a
 * list of named operations on abstract resources. Also, patterns can be
 * specified that specify the allowed sequence of operations on a resource. When
 * SECRET analyzes a concern, it will fetch the selected filtermodule-order and
 * generate all possible executions of the filterset. Every execution is a
 * unique combination of accept- and rejectactions of the filters in the
 * filterset. The operations of these actions are taken from the input
 * specification and performed on the resources. Then the specified patterns are
 * matches against the sequences of operations performed on the resources.
 */
// @ComposestarModule(ID = ModuleNames.SECRET, dependsOn = { ModuleNames.FIRE },
// importance = Importance.ADVISING)
public class SECRET implements CTCommonModule
{
	public static final String CONFIG_NAME = "SECRETConfig.xml";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.SECRET);

	/**
	 * The mode determines what filter module ordering should be validated by
	 * SECRET.
	 */
	@ModuleSetting
	protected SECRETMode mode = SECRETMode.Normal;

	@ResourceManager
	protected SECRETResources secretResources;

	/**
	 * The base configuration file which contain all default configuration
	 * directives. This is usually loaded from an global file and not a project
	 * dependent file.
	 */
	@ModuleSetting(ID = "baseconfig", name = "Base configuration", isAdvanced = true)
	protected String baseConfig;

	/**
	 * Additional configuration directives which are loaded besides the base
	 * configuration file.
	 */
	@ModuleSetting(ID = "config", name = "Configuration")
	protected String userConfig;

	/**
	 * The class that will be used to produce the report. If this value is
	 * "true" then the {@link XMLReport} class will be used.
	 */
	@ModuleSetting(ID = "reportGenerator", name = "Report generator", isAdvanced = true)
	protected String reportClass = "true";

	/**
	 * If set to true the SECRET configuration will be validated to check if all
	 * references resources are completely defined.
	 */
	@ModuleSetting(ID = "validate", name = "Configuration Validation")
	protected boolean validateResources = true;

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getModuleName()
	 */
	public String getModuleName()
	{
		return ModuleNames.SECRET;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getDependencies()
	 */
	public String[] getDependencies()
	{
		return new String[] { ModuleNames.FIRE };
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getImportance()
	 */
	public ModuleImportance getImportance()
	{
		return ModuleImportance.ADVISING;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Resources
	 * .CommonResources)
	 */
	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		if (true)
		{
			logger
					.warn("SECRET has been disabled because it has not been (properly) updated to the canonical filter model");
			return ModuleReturnValue.NO_EXECUTION;
		}
		secretResources.setFIRE2Resources(resources.getResourceManager(FIRE2Resources.class));
		loadConfiguration(resources);

		secretResources.setLabeler(new ResourceOperationLabelerEx(secretResources));

		for (Concern concern : resources.repository().getAll(Concern.class))
		{
			if (concern.getSuperimposed() != null)
			{
				CPSTimer timer = CPSTimer.getTimer(ModuleNames.SECRET, concern.getFullyQualifiedName());
				run(concern);
				timer.stop();
			}
		}

		if (reportClass != null && reportClass.trim().length() > 0)
		{
			if (reportClass.equalsIgnoreCase("true"))
			{
				reportClass = XMLReport.class.getName();
			}
			try
			{
				Class<?> reportCls = Class.forName(reportClass);
				if (SECRETReport.class.isAssignableFrom(reportCls))
				{
					try
					{
						SECRETReport reporter = (SECRETReport) reportCls.newInstance();
						reporter.report(resources, secretResources);
					}
					catch (InstantiationException e)
					{
						logger.warn(String.format("Unable to create report class %s: %s", reportClass, e
								.getLocalizedMessage()));
					}
					catch (IllegalAccessException e)
					{
						logger.warn(String.format("Unable to create report class %s: %s", reportClass, e
								.getLocalizedMessage()));
					}
				}
				else
				{
					logger.warn(String.format("Report class %s does not implement the SECRETReport interface",
							reportClass));
				}
			}
			catch (ClassNotFoundException e)
			{
				logger.warn(String.format("Unable to create report class %s", reportClass));
			}
		}
		return ModuleReturnValue.OK;
	}

	/**
	 * Load the configuration for SECRET. This will extract resource, actions,
	 * and rule definitions from XML configuration files and filter action
	 * elements in the repository.
	 * 
	 * @param resources
	 * @throws ConfigurationException
	 * @see FilterAction#getResourceOperations()
	 */
	private void loadConfiguration(CommonResources resources) throws ConfigurationException
	{
		File configFile = null;
		if (baseConfig != null && baseConfig.trim().length() > 0)
		{
			configFile = new File(baseConfig.trim());
			if (!configFile.isAbsolute())
			{
				configFile = new File(resources.configuration().getProject().getBase(), baseConfig.trim());
			}
			if (!configFile.exists())
			{
				configFile = null;
			}
		}
		if (configFile == null)
		{
			configFile = resources.getPathResolver().getResource(CONFIG_NAME);
		}
		if (configFile != null)
		{
			logger.info(String.format("Loading SECRET config from: %s", configFile.toString()));
			XmlConfiguration.loadBuildConfig(configFile, secretResources);
		}
		else
		{
			logger.debug("Loading internal SECRET configuration");
			XmlConfiguration.loadBuildConfig(SECRET.class.getResourceAsStream(CONFIG_NAME), secretResources);
		}

		if (resources.configuration().getSecretResources() != null)
		{
			secretResources.inheritConfiguration(resources.configuration().getSecretResources());
		}

		// load operation sequences from filter action
		for (FilterAction fact : resources.repository().getAll(FilterAction.class))
		{
			OperationSequence opseq = new OperationSequence();
			opseq.addLabel(fact.getName(), "node");
			secretResources.addOperationSequence(opseq);
			if (fact.getResourceOperations() == null || fact.getResourceOperations().length() == 0)
			{
				if (!fact.getName().equals("ContinueAction") && !fact.getName().equals("SubstitutionAction")
						&& !fact.getName().equals("AdviceAction") && !fact.getName().equals("MetaAction"))
				{
					logger.info(String.format("Filter action \"%s\" has no resource operation information.", fact
							.getName()));
				}
				continue;
			}
			String[] resops = fact.getResourceOperations().split(";");
			for (String resop : resops)
			{
				String[] op = resop.split("\\.");
				if (op.length != 2)
				{
					continue;
				}

				Resource resc = null;
				try
				{
					ResourceType rescType = ResourceType.parse(op[0]);
					if (rescType == ResourceType.Custom)
					{
						resc = secretResources.getResource(op[0].trim());
					}
					else if (!rescType.isMeta())
					{
						resc = secretResources.getResource(rescType.toString());
					}

					if (resc == null)
					{
						resc = ResourceType.createResource(op[0], false);
						if (!resc.getType().isMeta())
						{
							secretResources.addResource(resc);
						}
					}
				}
				catch (IllegalArgumentException e)
				{
					logger.error(String.format(
							"%s used an invalid resource name \"%s\" in the resource operation list", fact.getName(),
							op[0]), e);
					continue;
				}

				opseq.addOperations(resc, op[1]);
			}
			if (opseq.getOperations().size() == 0)
			{
				logger.warn(String.format("Filter action \"%s\" has no valid resource operations.", fact.getName()));
			}
		}

		// load additional configuration directives
		configFile = new File(resources.configuration().getProject().getBase(), CONFIG_NAME);
		if (userConfig != null && userConfig.trim().length() > 0)
		{
			configFile = new File(userConfig.trim());
			if (!configFile.isAbsolute())
			{
				configFile = new File(resources.configuration().getProject().getBase(), userConfig.trim());
			}
		}
		if (configFile != null && configFile.exists())
		{
			logger.info(String.format("Loading additional SECRET config from: %s", configFile.toString()));
			XmlConfiguration.loadBuildConfig(configFile, secretResources);
		}

		if (validateResources)
		{
			validateResourceDefinitions();
		}
	}

	/**
	 * Validate the current SECRET resources. This will only produce log
	 * warnings.
	 */
	private void validateResourceDefinitions()
	{
		for (Resource resc : secretResources.getResources())
		{
			if (resc.getVocabulary().size() == 0)
			{
				logger.warn(String.format("Resource \"%s\" has no defined vocabulary", resc.getName()));
			}
		}
		for (OperationSequence opseq : secretResources.getOperationSequences())
		{
			for (Entry<Resource, List<String>> entry : opseq.getOperations().entrySet())
			{
				if (entry.getKey().getVocabulary().size() == 0)
				{
					continue;
				}
				List<String> copy = new ArrayList<String>(entry.getValue());
				copy.removeAll(entry.getKey().getVocabulary());
				if (copy.size() > 0)
				{
					logger.warn(String.format(
							"Unknown resource operations used for resource \"%s\": %s; Used in actions for: %s", entry
									.getKey().getName(), copy.toString(), opseq.getLabels().toString()));
					// add them to the resource, they might be used in rules
					entry.getKey().addVocabulary(copy);
				}
			}
		}
		for (ConflictRule rule : secretResources.getRules())
		{
			if (rule.getResource().getType().isMeta())
			{
				continue;
			}
			Set<String> ruleLabels = getAllRuleLabels(rule.getPattern().getStartState());
			ruleLabels.removeAll(rule.getResource().getVocabulary());
			if (ruleLabels.size() > 0)
			{
				logger.warn(String.format("Unknown resource operations \"%s\" used in conflict rule: %s", ruleLabels,
						rule));
			}
		}
	}

	/**
	 * Retrieves the set of labels used in the pattern
	 * 
	 * @param state
	 * @return
	 */
	private Set<String> getAllRuleLabels(RegularState state)
	{
		Set<String> result = new HashSet<String>();
		Set<RegularState> visited = new HashSet<RegularState>();
		Queue<RegularState> queue = new LinkedList<RegularState>();
		queue.add(state);
		while (queue.size() > 0)
		{
			RegularState s = queue.remove();
			visited.add(s);
			for (RegularTransition t : s.getOutTransitions())
			{
				result.addAll(t.getLabels());
				if (!visited.contains(t.getEndState()))
				{
					queue.add(t.getEndState());
				}
			}
		}
		result.remove(RegularTransition.WILDCARD);
		return result;
	}

	/**
	 * Check the provided concern for conflicting paths.
	 * 
	 * @param concern
	 * @throws ModuleException
	 */
	private void run(Concern concern) throws ModuleException
	{
		List<ImposedFilterModule> singleOrder = concern.getSuperimposed().getFilterModuleOrder();
		if (singleOrder != null)
		{
			// ok need to do some checking
			ConcernAnalysis ca = new ConcernAnalysis(concern, secretResources);
			secretResources.addConcernAnalysis(ca);
			Collection<List<ImposedFilterModule>> fmolist = concern.getSuperimposed().getAllOrders();

			switch (mode)
			{
				case Normal:
					if (!ca.analyseOrder(singleOrder, true))
					{
						logger.warn(new LogMessage("Semantic conflict(s) detected on concern "
								+ concern.getFullyQualifiedName(), "", 0));
					}
					break;

				case Redundant:
					if (!ca.analyseOrder(singleOrder, true))
					{
						logger.warn(new LogMessage("Semantic conflict(s) detected on concern "
								+ concern.getFullyQualifiedName(), "", 0));
					}
					for (List<ImposedFilterModule> aFmolist1 : fmolist)
					{
						if (!aFmolist1.equals(singleOrder))
						{
							ca.analyseOrder(aFmolist1, false);
						}
					}
					break;

				case Progressive:
					boolean foundGoodOrder = ca.analyseOrder(singleOrder, true);

					for (List<ImposedFilterModule> aFmolist : fmolist)
					{
						if (!aFmolist.equals(singleOrder))
						{
							if (ca.analyseOrder(aFmolist, aFmolist.equals(singleOrder)))
							{
								if (!foundGoodOrder)
								{
									// so this is the first good order found...
									foundGoodOrder = true;
									concern.getSuperimposed().setFilterModuleOrder(aFmolist);
									logger.info("Selected filtermodule order for concern "
											+ concern.getFullyQualifiedName() + ':');
									logger.info('\t' + aFmolist.toString());
								}
							}
						}
					}
					if (!foundGoodOrder)
					{
						logger.warn("Unable to find a filtermodule order without conflicts for concern:");
						logger.warn('\t' + concern.getFullyQualifiedName());
					}

					break;

				default: // OOPS
					logger.warn("Unknown mode used");
					break;
			}
		}
	}
}
