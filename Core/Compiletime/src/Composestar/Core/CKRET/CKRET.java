/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2004-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CKRET;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;

import Composestar.Core.Annotations.ResourceManager;
import Composestar.Core.CKRET.Config.ConflictRule;
import Composestar.Core.CKRET.Config.OperationSequence;
import Composestar.Core.CKRET.Config.Resource;
import Composestar.Core.CKRET.Config.ResourceType;
import Composestar.Core.CKRET.Config.Xml.XmlConfiguration;
import Composestar.Core.CKRET.Report.SECRETReport;
import Composestar.Core.CKRET.Report.XMLReport;
import Composestar.Core.Config.ModuleInfo;
import Composestar.Core.Config.ModuleInfoManager;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction;
import Composestar.Core.Exception.ConfigurationException;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE2.model.FIRE2Resources;
import Composestar.Core.FIRE2.util.regex.RegularState;
import Composestar.Core.FIRE2.util.regex.RegularTransition;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.SANE.FilterModuleSuperImposition;
import Composestar.Core.SANE.SIinfo;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Logging.LogMessage;
import Composestar.Utils.Perf.CPSTimer;

// FIXME: rename package to SECRET
/**
 * SECRET performs semantic reasoning on the resource operations of the filters
 * and their actions.
 */
public class CKRET implements CTCommonModule
{
	public static final String MODULE_NAME = "SECRET";

	public static final String CONFIG_NAME = "SECRETConfig.xml";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	/**
	 * The mode secret should execute in.
	 */
	protected SECRETMode mode;

	@ResourceManager
	protected SECRETResources secretResources;

	/**
	 * The class that will be used to produce the report. If this value is
	 * "true" then the {@link XMLReport} class will be used. This variable is
	 * set through the project configuration.
	 */
	protected String reportClass;

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Resources.CommonResources)
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		secretResources.setFIRE2Resources(resources.getResourceManager(FIRE2Resources.class));
		loadConfiguration(resources);

		secretResources.setLabeler(new ResourceOperationLabelerEx(secretResources));

		Iterator<Concern> conIt = resources.repository().getAllInstancesOf(Concern.class);
		while (conIt.hasNext())
		{
			Concern concern = conIt.next();

			if (concern.getDynObject(SIinfo.DATAMAP_KEY) != null)
			{
				CPSTimer timer = CPSTimer.getTimer(MODULE_NAME, concern.getUniqueID());
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
		ModuleInfo mi = ModuleInfoManager.get(MODULE_NAME);
		mode = mi.getSetting("mode", SECRETMode.Normal);
		reportClass = mi.getSetting("reportGenerator", "");

		File configFile = null;
		String cfgfile = mi.getSetting("baseconfig", "");
		if (cfgfile != null && cfgfile.trim().length() > 0)
		{
			configFile = new File(cfgfile.trim());
			if (!configFile.isAbsolute())
			{
				configFile = new File(resources.configuration().getProject().getBase(), cfgfile.trim());
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
			XmlConfiguration.loadBuildConfig(CKRET.class.getResourceAsStream(CONFIG_NAME), secretResources);
		}

		if (resources.configuration().getSecretResources() != null)
		{
			secretResources.inheritConfiguration(resources.configuration().getSecretResources());
		}

		// load operation sequences from filter action
		Iterator<FilterAction> facts = resources.repository().getAllInstancesOf(FilterAction.class);
		while (facts.hasNext())
		{
			FilterAction fact = facts.next();
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
		cfgfile = mi.getSetting("config", "");
		if (cfgfile != null && cfgfile.trim().length() > 0)
		{
			configFile = new File(cfgfile.trim());
			if (!configFile.isAbsolute())
			{
				configFile = new File(resources.configuration().getProject().getBase(), cfgfile.trim());
			}
		}
		if (configFile != null && configFile.exists())
		{
			logger.info(String.format("Loading additional SECRET config from: %s", configFile.toString()));
			XmlConfiguration.loadBuildConfig(configFile, secretResources);
		}

		if (mi.getSetting("validate", true))
		{
			validateResources();
		}
	}

	/**
	 * Validate the current SECRET resources. This will only produce log
	 * warnings.
	 */
	private void validateResources()
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
		FilterModuleOrder singleOrder = (FilterModuleOrder) concern.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY);
		if (singleOrder != null)
		{
			// ok need to do some checking
			ConcernAnalysis ca = new ConcernAnalysis(concern, secretResources);
			secretResources.addConcernAnalysis(ca);
			List<List<FilterModuleSuperImposition>> fmolist = (List<List<FilterModuleSuperImposition>>) concern
					.getDynObject(FilterModuleOrder.ALL_ORDERS_KEY);

			switch (mode)
			{
				case Normal:
					if (!ca.analyseOrder(singleOrder, true))
					{
						logger.warn(new LogMessage("Semantic conflict(s) detected on concern "
								+ concern.getQualifiedName(), "", 0));
					}
					break;

				case Redundant:
					if (!ca.analyseOrder(singleOrder, true))
					{
						logger.warn(new LogMessage("Semantic conflict(s) detected on concern "
								+ concern.getQualifiedName(), "", 0));
					}
					for (List<FilterModuleSuperImposition> aFmolist1 : fmolist)
					{
						FilterModuleOrder fmo = new FilterModuleOrder(aFmolist1);

						if (!fmo.equals(singleOrder))
						{
							ca.analyseOrder(fmo, false);
						}
					}
					break;

				case Progressive:
					boolean foundGoodOrder = ca.analyseOrder(singleOrder, true);

					for (List<FilterModuleSuperImposition> aFmolist : fmolist)
					{
						FilterModuleOrder fmo = new FilterModuleOrder(aFmolist);
						if (!fmo.equals(singleOrder))
						{
							if (ca.analyseOrder(fmo, fmo.equals(singleOrder)))
							{
								if (!foundGoodOrder)
								{
									// so this is the first good order found...
									foundGoodOrder = true;
									concern.addDynObject(FilterModuleOrder.SINGLE_ORDER_KEY, fmo);
									logger.info("Selected filtermodule order for concern " + concern.getQualifiedName()
											+ ':');
									logger.info('\t' + fmo.toString());
								}
							}
						}
					}
					if (!foundGoodOrder)
					{
						logger.warn("Unable to find a filtermodule order without conflicts for concern:");
						logger.warn('\t' + concern.getQualifiedName());
					}

					break;

				default: // OOPS
					logger.warn("Unknown mode used");
					break;
			}
		}
	}
}
