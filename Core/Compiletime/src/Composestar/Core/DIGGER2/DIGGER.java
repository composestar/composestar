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

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.DeclaredObjectReference;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.model.FireModel;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.model.Message;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.INCRETimer;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.RepositoryImplementation.TypedDeclaration;
import Composestar.Utils.Debug;
import Composestar.Utils.Logging.CPSLogger;

/**
 * DIspatch Grapg GEneratoR.
 * 
 * @author Michiel Hendriks
 */
public class DIGGER implements CTCommonModule
{
	public static final String MODULE_NAME = "DIGGER";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	/**
	 * 
	 */
	public DIGGER()
	{

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Master.CommonResources)
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		INCRE incre = INCRE.instance();
		INCRETimer filthinit;

		filthinit = incre.getReporter().openProcess(MODULE_NAME, "Creating dispatch graph", INCRETimer.TYPE_NORMAL);
		dig();
		filthinit.stop();
	}

	/**
	 * @throws ModuleException
	 */
	protected void dig() throws ModuleException
	{
		Iterator concerns = DataStore.instance().getAllInstancesOf(Concern.class);
		while (concerns.hasNext())
		{
			Concern concern = (Concern) concerns.next();
			FilterModuleOrder fmOrder = (FilterModuleOrder) concern.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY);
			if (fmOrder != null)
			{
				if (Debug.willLog(Debug.MODE_INFORMATION))
				{
					logger.info("Generating dispatch graph for: " + concern.getQualifiedName());
				}

				FireModel fm = new FireModel(concern, fmOrder);
				processFireModel(concern, fm, FireModel.INPUT_FILTERS);
				processFireModel(concern, fm, FireModel.OUTPUT_FILTERS);
			}
		}
	}

	protected void processFireModel(Concern concern, FireModel fm, int filterChain) throws ModuleException
	{
		Iterator it = fm.getExecutionModel(filterChain).getEntranceStates();
		while (it.hasNext())
		{
			ExecutionState es = (ExecutionState) it.next();
			Message msg = es.getMessage();
			logger.debug("Entrace message: " + msg.getTarget().getName() + "." + msg.getSelector().getName());
			Breadcrumb crumb = new Breadcrumb(concern, msg, filterChain);
			traverseExecutionModel(es, crumb, crumb.addTrail());

			Iterator results = crumb.getTrails();
			while (results.hasNext())
			{
				Trail trail = (Trail) results.next();
				msg = trail.getResultMessage();
				logger.debug("Message result: " + msg.getTarget().getName() + "." + msg.getSelector().getName());
			}
		}
	}

	/**
	 * Traverse the execution model from a given entrace message
	 * 
	 * @param state
	 * @param crumb
	 * @param trail
	 * @throws ModuleException
	 */
	protected void traverseExecutionModel(ExecutionState state, Breadcrumb crumb, Trail trail) throws ModuleException
	{
		while (state != null)
		{
			FlowNode flowNode = state.getFlowNode();
			if (flowNode.containsName(FlowNode.CONDITION_EXPRESSION_NODE))
			{
				// needed for certainty checks
				ConditionExpression cond = (ConditionExpression) flowNode.getRepositoryLink();
				trail.setCondition(cond);
			}
			else if (flowNode.containsName(FlowNode.CONDITION_OPERATOR_NODE))
			{
				// trail.addOperator(); ?? needed ??
			}
			else if (flowNode.containsName(FlowNode.MATCHING_PART_NODE))
			{
				// ?? needeed ??
			}
			else if (flowNode.containsName(FlowNode.SUBSTITUTION_PART_NODE))
			{
				trail.setResultMessage(state.getMessage());
				Concern targetConcern = findTargetConcern(crumb, state.getMessage().getTarget());
				trail.setTargetConcern(targetConcern);
			}
			// TODO: handle error action (what about other actions?)

			// Create a list of the outgoing transitions. This is done to
			// optimize the algorithm, in most cases there is only one outgoing
			// transition and only in cases of multiple transitions we need to
			// branch (because of an alternative trail).
			List outs = new ArrayList();
			Iterator it = state.getOutTransitions();
			while (it.hasNext())
			{
				outs.add(it.next());
			}
			if (outs.size() == 0)
			{
				logger.debug("Reached the end of a trail");
				if (trail.getResultMessage() == null)
				{
					logger.debug("Remove resultless trail");
					crumb.removeTrail(trail);
				}
				return;
			}
			else if (outs.size() > 1)
			{
				logger.debug("Branching into " + outs.size() + " trails");
				// fork for each transition additional transition
				// the first transition will be followed inline
				for (int i = 1; i < outs.size(); i++)
				{
					ExecutionTransition trans = (ExecutionTransition) outs.get(i);
					traverseExecutionModel(trans.getEndState(), crumb, crumb.addTrail());
				}
			}
			// follow the remaining transition
			ExecutionTransition trans = (ExecutionTransition) outs.get(0);
			state = trans.getEndState();
		}
	}

	/**
	 * Resolve the traget to a concern. Returns null when there is no target
	 * concern (for example in case of inner.* for input filters)
	 * 
	 * @param concern
	 * @param target
	 * @return
	 * @throws ModuleException
	 */
	protected Concern findTargetConcern(Breadcrumb crumb, Target target) throws ModuleException
	{
		Concern concern = crumb.getConcern();
		String targetString = target.getName();
		int direction = crumb.direction;
		if ("*".equals(targetString))
		{
			if (direction == FireModel.INPUT_FILTERS)
			{
				// send to itself
				return concern;
			}
			else if (direction == FireModel.OUTPUT_FILTERS)
			{
				// TODO: unresolved target; need data from concern internals
				return null;
			}
		}
		else if ("inner".equals(targetString))
		{
			if (direction == FireModel.INPUT_FILTERS)
			{
				// actual method is executed
				return null;
			}
			else if (direction == FireModel.OUTPUT_FILTERS)
			{
				// like *+INPUT
				return concern;
			}
		}
		else
		{
			// uses an internal/external, look it up in the filter module
			DeclaredObjectReference ref = (DeclaredObjectReference) target.getRef();
			if ((ref != null) && ref.getResolved())
			{
				TypedDeclaration typeDecl = ref.getRef();
				ConcernReference concernRef = typeDecl.getType();
				return concernRef.getRef();
			}
			else
			{
				throw new ModuleException("Unresolved internal/external: " + targetString, MODULE_NAME);
			}
		}
		throw new ModuleException("Unresolved target: " + targetString, MODULE_NAME);
	}
}
