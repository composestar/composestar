/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
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
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.model.FireModel;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.model.Message;
import Composestar.Core.RepositoryImplementation.TypedDeclaration;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Resolves an fire execution state to a breadcrumb
 * 
 * @author Michiel Hendriks
 */
public class Resolver
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(DIGGER.MODULE_NAME);

	protected DispatchGraph graph;

	public Resolver(DispatchGraph inGraph)
	{
		graph = inGraph;
	}

	/**
	 * Resolve an execution state to a breadcrumb. This must be a starting state
	 * to get the proper results.
	 * 
	 * @param concern
	 * @param state
	 * @param filterChain
	 * @return
	 * @throws ModuleException
	 */
	public Breadcrumb resolve(Concern concern, ExecutionState state, int filterChain) throws ModuleException
	{
		Message msg = state.getMessage();
		logger.debug("[resolver] entrance message: " + msg.toString());
		Breadcrumb crumb = new Breadcrumb(concern, msg, filterChain);
		traverseState(state, crumb, crumb.addTrail());
		Iterator results = crumb.getTrails();
		while (results.hasNext())
		{
			Trail trail = (Trail) results.next();
			msg = trail.getResultMessage();
			logger.debug("[resolver] message result: " + msg.toString());
		}
		return crumb;
	}

	/**
	 * Resolves the trails in the breadcrumb to the next breadcrumb
	 * 
	 * @param crumb
	 * @throws ModuleException
	 */
	public void resolve(Breadcrumb crumb) throws ModuleException
	{
		Iterator trails = crumb.getTrails();
		while (trails.hasNext())
		{
			Trail trail = (Trail) trails.next();
			logger.debug("[resolver] " + trail.getResultMessage().toString() + " for " + crumb.toString());
			if (trail.getTargetConcern() != null)
			{
				// crumbs always refer to input crumbs
				Breadcrumb toCrumb = graph.getInputCrumb(trail.getTargetConcern(), trail.getResultMessage());
				logger.debug("[resolver]  resolved to: " + toCrumb);
				trail.setDestinationCrumb(toCrumb);
			}
			else
			{
				logger.debug("[resolver]  end of trail");
				trail.setDestinationCrumb(null);
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
	protected void traverseState(ExecutionState state, Breadcrumb crumb, Trail trail) throws ModuleException
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
				if (!flowNode.containsName(FlowNode.STOP_NODE))
				{
					logger.debug("[resolver] reached the end of a trail without a STOP node");
				}
				if (trail.getResultMessage() == null)
				{
					logger.debug("[resolver] remove resultless trail");
					crumb.removeTrail(trail);
				}
				return;
			}
			else if (outs.size() > 1)
			{
				logger.debug("[resolver] Branching into " + outs.size() + " trails");
				// fork for each transition additional transition
				// the first transition will be followed inline
				for (int i = 1; i < outs.size(); i++)
				{
					ExecutionTransition trans = (ExecutionTransition) outs.get(i);
					traverseState(trans.getEndState(), crumb, crumb.addTrail());
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
		int direction = crumb.filterPosition;
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
				throw new ModuleException("Unresolved internal/external: " + targetString, DIGGER.MODULE_NAME);
			}
		}
		throw new ModuleException("Unresolved target: " + targetString, DIGGER.MODULE_NAME);
	}
}