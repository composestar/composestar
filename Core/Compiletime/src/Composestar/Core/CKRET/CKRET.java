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
import java.util.LinkedList;
import java.util.List;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.PrimitiveConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.INCRETimer;
import Composestar.Core.LAMA.Annotation;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.PathSettings;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.SANE.SIinfo;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;

/**
 * SECRET
 */
public class CKRET implements CTCommonModule
{
	public static final int PROGRESSIVE = 2;

	public static final int REDUNDANT = 1;

	public static final int NORMAL = 0;

	public static final String[] MODES = { "NORMAL", "REDUNDANT", "PROGRESSIVE" };

	protected static int mode;	

	private static Reporter reporter;

	private String reportFile = "";

	public static int getMode()
	{
		return mode;
	}

	public void run(CommonResources resources) throws ModuleException
	{
		INCRE incre = INCRE.instance();
		Configuration config = Configuration.instance();

		// make sure it has been initialized at least once...
		try
		{
			INCRETimer initckret = incre.getReporter().openProcess("CKRET", "Initializing CKRET repository",
					INCRETimer.TYPE_NORMAL);
			Repository.instance().init();
			initckret.stop();
		}
		catch (ModuleException me)
		{
			Debug.out(Debug.MODE_WARNING, "CKRET", me.getMessage());
			return;
		}

		// fetch the ckret runmode
		int new_mode = config.getModuleProperty("SECRET", "mode", mode);
		if (new_mode >= 0 && new_mode <= 2)
		{
			Debug.out(Debug.MODE_INFORMATION, "CKRET", "CKRET mode set to " + MODES[new_mode]);
			mode = new_mode;
		}
		else
		{
			Debug.out(Debug.MODE_WARNING, "CKRET", "Unknown CKRET mode: " + new_mode + ", CKRET will run in " + MODES[mode]
					+ " mode");
		}

		try
		{
			PathSettings ps = config.getPathSettings();
			String basedir = ps.getPath("Base");

			File file = new File(basedir, "analyses");
			if (!file.exists())
			{
				file.mkdir();
			}

			if (file.isDirectory())
			{
				reportFile = file.getAbsolutePath() + "\\CKRET.html";

				String cssFile = "file://" + basedir + "CKRET.css";
				if (!FileUtils.fileExist(cssFile))
				{
					cssFile = "file://" + ps.getPath("Composestar") + "CKRET.css";
				}

				reporter = new HTMLReporter(reportFile, cssFile, resources);
				reporter.open();

				Debug.out(Debug.MODE_DEBUG, "CKRET", "CKRET report file (" + reportFile + ") created...");
			}
		}
		catch (Exception e)
		{
			throw new ModuleException("CKRET", "CKRET report file creation failed (" + reportFile + "), with reason: "
					+ e.getMessage());
		}

		Iterator conIt = DataStore.instance().getAllInstancesOf(Concern.class);
		while (conIt.hasNext())
		{
			Concern concern = (Concern) conIt.next();

			if (concern.getDynObject(SIinfo.DATAMAP_KEY) != null)
			{
				if (incre.isProcessedByModule(concern, "CKRET"))
				{
					this.copyOperation(concern);
				}
				else
				{
					INCRETimer ckretrun = incre.getReporter().openProcess("CKRET", concern.getUniqueID(),
							INCRETimer.TYPE_NORMAL);
					this.run(concern);
					ckretrun.stop();
				}
			}
		}

		getReporter().close();
	}

	private void run(Concern concern) throws ModuleException
	{
		getReporter().openConcern(concern);

		FilterModuleOrder singleOrder = (FilterModuleOrder) concern.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY);
		if (singleOrder != null)
		{
			// ok need to do some checking
			ConcernAnalysis ca = new ConcernAnalysis(concern);
			List fmolist = (List) concern.getDynObject(FilterModuleOrder.ALL_ORDERS_KEY);

			switch (CKRET.mode)
			{
				case NORMAL: // NORMAL
					if (!ca.checkOrder(singleOrder, true))
					{
						Debug.out(Debug.MODE_WARNING, "CKRET", "Semantic conflict(s) detected on concern "
								+ concern.getQualifiedName(), reportFile);
					}
					break;

				case REDUNDANT: // REDUNDANT
					if (!ca.checkOrder(singleOrder, true))
					{
						Debug.out(Debug.MODE_WARNING, "CKRET", "Semantic conflict(s) detected on concern "
								+ concern.getQualifiedName(), reportFile);
					}
					for (Iterator fmoit = fmolist.iterator(); fmoit.hasNext();)
					{
						LinkedList order = (LinkedList) fmoit.next();
						FilterModuleOrder fmo = new FilterModuleOrder(order);

						if (!fmo.equals(singleOrder))
						{
							ca.checkOrder(fmo, false);
						}
					}
					break;

				case PROGRESSIVE: // PROGRESSIVE
					boolean foundGoodOrder = ca.checkOrder(singleOrder, true);

					for (Iterator fmoit = fmolist.iterator(); fmoit.hasNext();)
					{
						LinkedList order = (LinkedList) fmoit.next();
						FilterModuleOrder fmo = new FilterModuleOrder(order);
						if (!fmo.equals(singleOrder))
						{
							if (ca.checkOrder(fmo, !foundGoodOrder))
							{
								if (!foundGoodOrder)
								{
									// so this is the first good order found...
									foundGoodOrder = true;
									concern.addDynObject(FilterModuleOrder.SINGLE_ORDER_KEY, fmo);
									Debug.out(Debug.MODE_INFORMATION, "CKRET",
											"Selected filtermodule order for concern " + concern.getQualifiedName()
													+ ':');
									Debug.out(Debug.MODE_INFORMATION, "CKRET", '\t' + fmo.toString());
								}
							}
						}
					}
					if (!foundGoodOrder)
					{
						Debug.out(Debug.MODE_WARNING, "CKRET",
								"Unable to find a filtermodule order without conflicts for concern:");
						Debug.out(Debug.MODE_WARNING, "CKRET", '\t' + concern.getQualifiedName());
					}

					break;

				default: // OOPS
					Debug.out(Debug.MODE_WARNING, "CKRET", "Unknown mode used");
					break;
			}
		}

		getReporter().closeConcern();
	}

	private void copyOperation(Concern concern) throws ModuleException
	{
		INCRE incre = INCRE.instance();
		INCRETimer ckretcopy = incre.getReporter().openProcess("CKRET", concern.getUniqueID(),
				INCRETimer.TYPE_INCREMENTAL);

		// set singleorder from previous CKRET run
		Concern oldconcern = (Concern) incre.findHistoryObject(concern);
		concern.addDynObject(FilterModuleOrder.SINGLE_ORDER_KEY, oldconcern
				.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY));

		// get CKRET reports and let HTMLReporter print them
		List reports = (List) oldconcern.getDynObject("CKRETReports");
		if (reports != null)
		{
			Debug.out(Debug.MODE_INFORMATION, "INCRE", "Skipping CKRET run for " + oldconcern.getQualifiedName());
			getReporter().openConcern(oldconcern);
			Iterator repItr = reports.iterator();
			while (repItr.hasNext())
			{
				CKRETReport report = (CKRETReport) repItr.next();
				getReporter().reportOrder(report.getOrder(), report.getAnalysis(), report.getSelected(), true);
			}
			getReporter().closeConcern();
			concern.addDynObject("CKRETReports", reports);
		}

		ckretcopy.stop();
	}

	public ArrayList getSemanticAnnotations(PrimitiveConcern pc)
	{
		return getSemanticAnnotations((Concern) pc);
	}

	public ArrayList getSemanticAnnotations(CpsConcern cps)
	{
		return getSemanticAnnotations((Concern) cps);
	}

	public ArrayList getSemanticAnnotations(Concern c)
	{
		ArrayList annos = new ArrayList();
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
			Iterator methods = type.getMethods().iterator();
			while (methods.hasNext())
			{
				MethodInfo method = (MethodInfo) methods.next();
				// iterate over annotations
				Iterator annotations = method.getAnnotations().iterator();
				while (annotations.hasNext())
				{
					Annotation anno = (Annotation) annotations.next();
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
	/*
	 * public static void printState(ActionNode node) {
	 * System.out.print(node.getSelector()); System.out.print(" "); Symbol[]
	 * conditions = node.getConditions(); for( int i = 0; i < conditions.length;
	 * i++ ) { System.out.print("[" + conditions[i].getName() + "]"); }
	 * System.out.println(); } protected static List getFilterList(List
	 * filterModules) { List list = new ArrayList(); Iterator itr =
	 * filterModules.iterator(); while (itr.hasNext()) { String name = (String)
	 * itr.next(); FilterModule fm = (FilterModule)
	 * (DataStore.instance()).getObjectByID(name); Iterator ifItr =
	 * fm.inputFilters.iterator(); while (ifItr.hasNext()) {
	 * Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter f =
	 * (Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter)
	 * ifItr.next(); list.add(f); } } return list; }
	 */
}
