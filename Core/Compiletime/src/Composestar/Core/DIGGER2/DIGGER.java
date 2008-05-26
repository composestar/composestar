/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.DIGGER2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.Annotations.ComposestarModule;
import Composestar.Core.Annotations.ModuleSetting;
import Composestar.Core.Annotations.ResourceManager;
import Composestar.Core.Annotations.ComposestarModule.Importance;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.FIRE2Resources;
import Composestar.Core.FIRE2.model.FireModel;
import Composestar.Core.FIRE2.model.FireModel.FilterDirection;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.RepositoryImplementation.RepositoryEntity;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Perf.CPSTimer;

/**
 * DIspatch Grapg GEneratoR. Connects the execution graphs of the concerns with
 * each other to create a fully connected graph of the program. Also checks for
 * recursive filter definitions.
 * 
 * @author Michiel Hendriks
 */
@ComposestarModule(ID = ModuleNames.DIGGER, dependsOn = { ModuleNames.FIRE, ModuleNames.FILTH }, importancex = Importance.VALIDATION)
public class DIGGER implements CTCommonModule
{
	/**
	 * If set to true Fire2 execution graphs will be shown. Should only be used
	 * for debugging of digger2
	 */
	private static final boolean SHOW_GRAPH = false;

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.DIGGER);

	/**
	 * The initial dispatch graph created by digger. This instance is added
	 * under the repository key defined by
	 * {@link DispatchGraph#REPOSITORY_KEY DispatchGraph.REPOSITORY_KEY}
	 */
	protected DispatchGraph graph;

	/**
	 * Temporary list of all created breadcrumbs. This list will be empty when
	 * DIGGER is done. It's used for faster access to all crumbs to perform
	 * operations on.
	 */
	protected List<Breadcrumb> allCrumbs;

	@ResourceManager
	protected FIRE2Resources f2res;

	/**
	 * The mode to execute DIGGER in.
	 */
	@ModuleSetting(name = "Mode")
	protected int mode = 1;

	/**
	 * Resolve all dispatch transitions to a complete graph
	 */
	@ModuleSetting(ID = "resolve", name = "Resolve Graph", isAdvanced = true)
	protected boolean resolveCrumbs = true;

	/**
	 * Maximum depth in the recursion check algorithm. If set to 0 no recursion
	 * check will be performed in the dispatch graph
	 */
	@ModuleSetting(ID = "recursionCheck", name = "Recursion Check Depth", isAdvanced = true)
	protected int recursionCheckDepth = 5;

	protected CPSTimer timer = CPSTimer.getTimer(ModuleNames.DIGGER);

	public DIGGER()
	{}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Master.CommonResources)
	 */
	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		// initialize
		graph = new DispatchGraph(mode);
		graph.setAutoResolve(false);
		resources.put(DispatchGraph.REPOSITORY_KEY, graph);
		allCrumbs = new ArrayList<Breadcrumb>();

		// f2res = resources.getResourceManager(FIRE2Resources.class);

		// step 1: breadcrumb creation, the crumbs will not be resolved
		logger.info("Step 1: breadcrumb creation");
		createBreadcrumbs(resources.repository());

		// step 2: resolve and check the created crumbs
		if (resolveCrumbs)
		{
			logger.info("Step 2: resolving breadcrumbs");
			resolveBreadcrumbs();
			if (recursionCheckDepth > 0)
			{
				logger.info("Step 2b: checking recursion");
				checkRecursion();
			}
		}

		// step 3: export the generated data
		// if (exportXml)
		// {
		//
		// logger.info("Step 2b: exporting result");
		// DispatchGraphExporter exporter = new XMLDispatchGraphExporter(graph);
		// exporter.export();
		// }

		// cleanup
		graph.setAutoResolve(true);
		allCrumbs.clear();
		return ModuleReturnValue.Ok;
	}

	/**
	 * Create the breadcrumbs
	 * 
	 * @throws ModuleException
	 */
	protected void createBreadcrumbs(DataStore ds) throws ModuleException
	{
		timer.start("Creating breadcrumbs in mode " + graph.getMode());
		Iterator<Concern> concerns = ds.getAllInstancesOf(Concern.class);
		while (concerns.hasNext())
		{
			Concern concern = concerns.next();
			FilterModuleOrder fmOrder = (FilterModuleOrder) concern.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY);
			if (fmOrder != null)
			{
				if (logger.isInfoEnabled())
				{
					logger.info("Generating dispatch graph for: " + concern.getQualifiedName());
				}

				FireModel fm = f2res.getFireModel(concern, fmOrder);
				switch (graph.getMode())
				{
					case DispatchGraph.MODE_BASIC:

						if (SHOW_GRAPH)
						{
							new Composestar.Core.FIRE2.util.viewer.Viewer(fm.getExecutionModel(FilterDirection.Input));
						}

						processFireModel(concern, fm.getExecutionModel(FilterDirection.Input).getEntranceStates(),
								FilterDirection.Input);
						// no output filter support
						// processFireModel(concern, fm,
						// FireModel.OUTPUT_FILTERS);
						break;
					default:
						processFullFireModel(concern, fm);

				}
			}
		}
		timer.stop();
	}

	/**
	 * Resolve the destination crumbs of the trails
	 * 
	 * @throws ModuleException
	 */
	protected void resolveBreadcrumbs() throws ModuleException
	{
		timer.start("Resolving breadcrumbs");
		for (Breadcrumb crumb : allCrumbs)
		{
			graph.getResolver().resolve(crumb);
		}
		timer.stop();
	}

	/**
	 * Check for recusion in the created breadcrumb. This actually requests the
	 * results for all crumbs and checks for an RecursiveFilterException to be
	 * thrown.
	 * 
	 * @throws ModuleException
	 */
	protected void checkRecursion() throws ModuleException
	{
		timer.start("Checking for recursive filter definitions");
		for (Breadcrumb crumb : allCrumbs)
		{
			List<AbstractMessageResult> results = graph.getResultingMessages(crumb);
			if (results.size() > 0)
			{
				logger.trace("" + crumb + " results in:");
				for (AbstractMessageResult msgResult : results)
				{
					if (msgResult.isValidResult())
					{
						logger.trace(" " + msgResult.getConcern().getName() + "."
								+ ((MessageResult) msgResult).getSelector());
					}
					else
					{
						reportRecursion((RecursiveMessageResult) msgResult);
					}
				}
			}

		}
		timer.stop();
	}

	/**
	 * Report the recursive filter exception to the user in a more or less
	 * friendly way. So that the user can inspect the cause.
	 * 
	 * @param e
	 */
	public void reportRecursion(RecursiveMessageResult e)
	{
		StringBuffer sb = new StringBuffer();

		sb.append(e.getCrumb().getConcern().getName());
		sb.append(".");
		sb.append(e.getCrumb().getMessage().getSelector());

		RepositoryEntity re = null;
		for (Trail trail : e.getTrace())
		{
			sb.append(" -> ");

			sb.append(trail.getTargetConcern().getName());
			sb.append(".");
			sb.append(trail.getResultMessage().getSelector());

			// the first trail point to the start
			if (re == null)
			{
				re = trail.getRE();
			}
		}

		if (e.numVars() == 0)
		{
			logger.error("Infinite recursive filter definition: " + sb.toString(), re);
		}
		else
		{
			logger.warn("Possibly infitite recursive filter definition (depends on ~" + e.numVars()
					+ " conditionals): " + sb.toString(), re);
		}
	}

	/**
	 * Process a collection of entrance states to breadcrumbs. This method is
	 * used in BASIC mode.
	 * 
	 * @param concern
	 * @param entranceStates
	 * @param filterChain
	 * @throws ModuleException
	 */
	protected void processFireModel(Concern concern, Iterator<ExecutionState> entranceStates,
			FilterDirection filterPosition) throws ModuleException
	{
		while (entranceStates.hasNext())
		{
			ExecutionState es = entranceStates.next();
			Breadcrumb crumb = graph.getResolver().resolve(concern, es, filterPosition);
			graph.addCrumb(crumb);
			allCrumbs.add(crumb);
		}
	}

	/**
	 * Do a full processing of the firemodel. This constructs an execution graph
	 * for every method in the concern. Only in this case will signature
	 * matching be performed. This method is used in the FULL mode.
	 * 
	 * @param concern
	 * @param fm
	 * @throws ModuleException
	 */
	protected void processFullFireModel(Concern concern, FireModel fm) throws ModuleException
	{
		Type type = (Type) concern.getPlatformRepresentation();
		if (type == null)
		{
			return;
		}
		Iterator<MethodInfo> it = type.getMethods().iterator();
		while (it.hasNext())
		{
			MethodInfo methodInfo = it.next();
			FilterDirection filterPosition = FilterDirection.Input;

			ExecutionModel em = fm.getExecutionModel(filterPosition, methodInfo, FireModel.STRICT_SIGNATURE_CHECK);
			if (em.getEntranceMessages().size() > 1)
			{
				logger.warn(concern.getName() + "." + methodInfo.getName() + " has " + em.getEntranceMessages().size()
						+ " entrance messages");
			}
			Iterator<ExecutionState> entranceStates = em.getEntranceStates();
			ExecutionState es = entranceStates.next();

			if (SHOW_GRAPH)
			{
				new Composestar.Core.FIRE2.util.viewer.Viewer(em);
			}

			Breadcrumb crumb = graph.getResolver().resolve(concern, es, filterPosition);
			graph.addCrumb(crumb);
			allCrumbs.add(crumb);
		}
	}
}
