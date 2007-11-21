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
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import Composestar.Core.CKRET.Config.OperationSequence;
import Composestar.Core.CKRET.Config.Resource;
import Composestar.Core.CKRET.Config.ResourceType;
import Composestar.Core.CKRET.Config.Xml.XmlConfiguration;
import Composestar.Core.Config.BuildConfig;
import Composestar.Core.Config.ModuleInfo;
import Composestar.Core.Config.ModuleInfoManager;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.PrimitiveConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction;
import Composestar.Core.Exception.ConfigurationException;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE2.model.FIRE2Resources;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.INCRETimer;
import Composestar.Core.LAMA.Annotation;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.SANE.FilterModuleSuperImposition;
import Composestar.Core.SANE.SIinfo;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Logging.LogMessage;

// FIXME: rename package to SECRET
/**
 * SECRET
 */
public class CKRET implements CTCommonModule
{
	public static final String MODULE_NAME = "SECRET";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	protected static SECRETMode mode;

	private static Reporter reporter;

	private File reportFile;

	private SECRETResources secretResources;

	public static SECRETMode getMode()
	{
		return mode;
	}

	public void run(CommonResources resources) throws ModuleException
	{
		INCRE incre = INCRE.instance();

		secretResources = resources.getResourceManager(SECRETResources.class, true);
		secretResources.setFIRE2Resources(resources.getResourceManager(FIRE2Resources.class));
		loadConfiguration(resources);

		try
		{
			BuildConfig config = resources.configuration();
			File file = new File(config.getProject().getIntermediate(), "Analyses");
			if (!file.exists())
			{
				file.mkdirs();
			}

			if (file.isDirectory())
			{
				reportFile = new File(file, "CKRET.html");

				// TODO CSS should be inlined? or resolve path
				String cssFile = file.toURL().toString() + "/CKRET.css";

				// String cssFile = "file://" + basedir + "CKRET.css";
				// if (!FileUtils.fileExist(cssFile))
				// {
				// cssFile = "file://" + ps.getPath("Composestar") +
				// "CKRET.css";
				// }

				reporter = new HTMLReporter(resources, reportFile, cssFile);
				reporter.open();

				logger.debug("CKRET report file (" + reportFile + ") created...");
			}
		}
		catch (Exception e)
		{
			throw new ModuleException(MODULE_NAME, "CKRET report file creation failed (" + reportFile
					+ "), with reason: " + e.getMessage());
		}

		secretResources.setLabeler(new ResourceOperationLabelerEx(secretResources));

		Iterator conIt = DataStore.instance().getAllInstancesOf(Concern.class);
		while (conIt.hasNext())
		{
			Concern concern = (Concern) conIt.next();

			if (concern.getDynObject(SIinfo.DATAMAP_KEY) != null)
			{
				INCRETimer ckretrun = incre.getReporter().openProcess(MODULE_NAME, concern.getUniqueID(),
						INCRETimer.TYPE_NORMAL);
				this.run(concern);
				ckretrun.stop();
			}
		}

		getReporter().close();
	}

	private void loadConfiguration(CommonResources resources) throws ConfigurationException
	{
		ModuleInfo mi = ModuleInfoManager.get(MODULE_NAME);
		mode = mi.getSetting("mode", SECRETMode.Normal);

		File configFile = null;
		String cfgfile = mi.getSetting("config", "");
		if (cfgfile != null && cfgfile.trim().length() > 0)
		{
			configFile = new File(cfgfile.trim());
			if (!configFile.exists())
			{
				configFile = null;
			}
		}
		if (configFile == null)
		{
			configFile = resources.getPathResolver().getResource("SECRETConfig.xml");
		}
		if (configFile != null)
		{
			logger.info(String.format("Loading SECRET config from: %s", configFile.toString()));
			XmlConfiguration.loadBuildConfig(configFile, secretResources);
		}
		else
		{
			logger.debug("Loading internal SECRET configuration");
			XmlConfiguration.loadBuildConfig(CKRET.class.getResourceAsStream("SECRETConfig.xml"), secretResources);
		}

		if (resources.configuration().getSecretResources() != null)
		{
			secretResources.inheritConfiguration(resources.configuration().getSecretResources());
		}

		// load operation sequences from filter action
		Iterator facts = DataStore.instance().getAllInstancesOf(FilterAction.class);
		while (facts.hasNext())
		{
			FilterAction fact = (FilterAction) facts.next();
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
				}
			}
		}
		// TODO: check rules
	}

	private void run(Concern concern) throws ModuleException
	{
		getReporter().openConcern(concern);

		FilterModuleOrder singleOrder = (FilterModuleOrder) concern.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY);
		if (singleOrder != null)
		{
			// ok need to do some checking
			ConcernAnalysis ca = new ConcernAnalysis(concern, secretResources);
			List<List<FilterModuleSuperImposition>> fmolist = (List<List<FilterModuleSuperImposition>>) concern
					.getDynObject(FilterModuleOrder.ALL_ORDERS_KEY);

			switch (CKRET.mode)
			{
				case Normal:
					if (!ca.checkOrder(singleOrder, true))
					{
						logger.warn(new LogMessage("Semantic conflict(s) detected on concern "
								+ concern.getQualifiedName(), reportFile.toString(), 0));
					}
					break;

				case Redundant:
					if (!ca.checkOrder(singleOrder, true))
					{
						logger.warn(new LogMessage("Semantic conflict(s) detected on concern "
								+ concern.getQualifiedName(), reportFile.toString(), 0));
					}
					for (List<FilterModuleSuperImposition> aFmolist1 : fmolist)
					{
						FilterModuleOrder fmo = new FilterModuleOrder(aFmolist1);

						if (!fmo.equals(singleOrder))
						{
							ca.checkOrder(fmo, false);
						}
					}
					break;

				case Progressive:
					boolean foundGoodOrder = ca.checkOrder(singleOrder, true);

					for (List<FilterModuleSuperImposition> aFmolist : fmolist)
					{
						FilterModuleOrder fmo = new FilterModuleOrder(aFmolist);
						if (!fmo.equals(singleOrder))
						{
							if (ca.checkOrder(fmo, !foundGoodOrder))
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

		getReporter().closeConcern();
	}

	public List<Annotation> getSemanticAnnotations(PrimitiveConcern pc)
	{
		return getSemanticAnnotations((Concern) pc);
	}

	public List<Annotation> getSemanticAnnotations(CpsConcern cps)
	{
		return getSemanticAnnotations((Concern) cps);
	}

	public List<Annotation> getSemanticAnnotations(Concern c)
	{
		List<Annotation> annos = new ArrayList<Annotation>();
		INCRE incre = INCRE.instance();
		DataStore ds = incre.getCurrentRepository();

		// iterate over concerns
		Iterator iterConcerns = ds.getAllInstancesOf(Concern.class);
		while (iterConcerns.hasNext())
		{
			Concern concern = (Concern) iterConcerns.next();
			Type type = (Type) concern.getPlatformRepresentation();
			if (type == null)
			{
				continue;
			}
			// iterate over methods
			for (Object o : type.getMethods())
			{
				MethodInfo method = (MethodInfo) o;
				// iterate over annotations
				for (Object o1 : method.getAnnotations())
				{
					Annotation anno = (Annotation) o1;
					if (anno.getType().getUnitName().endsWith("Semantics"))
					{
						annos.add(anno);
					}
				}
			}
		}

		return annos;
	}

	protected static Reporter getReporter()
	{
		return reporter;
	}
}
