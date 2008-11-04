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

import java.util.Iterator;
import java.util.LinkedList;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingExpression;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.CpsRepository2.Concern;
import Composestar.Core.CpsRepository2.TypeSystem.CpsMessage;
import Composestar.Core.CpsRepository2.TypeSystem.CpsObject;
import Composestar.Core.CpsRepository2.TypeSystem.CpsSelector;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.model.FireModel.FilterDirection;
import Composestar.Core.FIRE2.preprocessing.GrooveASTBuilderCN;
import Composestar.Core.Master.ModuleNames;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Resolves an fire execution state to a breadcrumb
 * 
 * @author Michiel Hendriks
 */
public class Resolver
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.DIGGER + ".Resolver");

	/**
	 * The dispatch graph this resolver is associated with
	 */
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
	public Breadcrumb resolve(Concern concern, ExecutionState state, FilterDirection filterChain)
			throws ModuleException
	{
		CpsMessage msg = state.getMessage();
		logger.trace("entrance message: " + msg.toString());
		Breadcrumb crumb = new Breadcrumb(concern, msg, filterChain);
		traverseState(state, crumb, crumb.addTrail(), new LinkedList<ExecutionState>());
		Iterator<Trail> results = crumb.getTrails();
		while (results.hasNext())
		{
			Trail trail = results.next();
			msg = trail.getResultMessage();
			logger.trace("message result: " + msg.toString());
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
		Iterator<Trail> trails = crumb.getTrails();
		while (trails.hasNext())
		{
			Trail trail = trails.next();
			logger.trace(trail.getResultMessage().toString() + " for " + crumb.toString());
			if (trail.getTargetConcern() != null)
			{
				// crumbs always refer to input crumbs
				CpsSelector selector = trail.getResultMessage().getSelector();
				// if (Message.STAR_SELECTOR.equals(selector))
				// {
				// selector = crumb.getMessage().getSelector();
				// }
				Breadcrumb toCrumb = graph.getInputCrumb(trail.getTargetConcern(), selector);
				// is `null' when destination has no input crumbs
				logger.trace("  leads to crumb: " + toCrumb);
				trail.setDestinationCrumb(toCrumb);
			}
			else
			{
				logger.trace("  end of trail");
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
	protected void traverseState(ExecutionState state, Breadcrumb crumb, Trail trail,
			LinkedList<ExecutionState> pastStates) throws ModuleException
	{
		while (state != null)
		{
			if (pastStates.contains(state))
			{
				logger.trace("Loop detected, ignoring path");
				crumb.removeTrail(trail);
				return;
			}
			pastStates.add(state);
			FlowNode flowNode = state.getFlowNode();
			if (flowNode.containsName(FlowNode.CONDITION_EXPRESSION_NODE))
			{
				// needed for certainty checks
				MatchingExpression cond = (MatchingExpression) flowNode.getRepositoryLink();
				trail.setCondition(cond);
			}
			else if (flowNode.containsName(FlowNode.FILTER_ELEMENT_NODE))
			{
				trail.setRE(flowNode.getRepositoryLink());
			}
			else if (flowNode.containsName(FlowNode.CONTINUE_ACTION_NODE))
			{
				// ignore
			}
			else if (flowNode.containsName(GrooveASTBuilderCN.createFilterActionText("DispatchAction")))
			{
				// Message newMsg = new
				// Message(state.getSubstitutionMessage().getTarget(),
				// state.getSubstitutionMessage()
				// .getSelector());
				CpsMessage newMsg = state.getMessage();
				trail.setResultMessage(newMsg);
				Concern targetConcern = findTargetConcern(crumb, newMsg.getTarget());
				trail.setTargetConcern(targetConcern);
			}
			else if (flowNode.containsName(FlowNode.EXIT_ACTION_NODE))
			{
				// TODO: implement
				// trail.setErrorException();
			}
			else if (flowNode.containsName(FlowNode.ACTION_NODE))
			{
				// TODO: what about other actions?
			}

			// traverse all outgoing transitions of the state
			// in most cases there is only one outgoing transition, this
			// transition will be traversed inline, additional transitions will
			// be branched (recursive call of this method).
			int idx = 0;
			for (ExecutionTransition trans : state.getOutTransitionsEx())
			{
				idx++;
				if (idx == 1)
				{
					// the first state will be traversed inline
					state = trans.getEndState();
				}
				else
				{
					// branch all additional states, clone the trails from the
					// current one because there is no guarantee that the first
					// state will be the "short" state.
					traverseState(trans.getEndState(), crumb, crumb.addTrail(trail), pastStates);
				}
			}
			if (idx == 0)
			{
				if (!flowNode.containsName(FlowNode.EXIT_NODE) && !flowNode.containsName(FlowNode.RETURN_NODE))
				{
					StringBuffer sb = new StringBuffer();
					for (String name : flowNode.getNamesEx())
					{
						if (sb.length() > 0)
						{
							sb.append(", ");
						}
						sb.append(name);
					}
					logger.info("reached the end of a trail without a STOP or RETURN node. Contains labels: " + sb);
				}
				if (trail.getResultMessage() == null)
				{
					logger.debug("remove resultless trail");
					crumb.removeTrail(trail);
				}
				return;
			}
			else if (idx > 1)
			{
				logger.trace("Branched into " + idx + " trails");
			}
		}
	}

	/**
	 * @see #findTargetConcern(Breadcrumb, Target)
	 * @param concern
	 * @param target
	 * @return
	 * @throws ModuleException
	 */
	public static Concern findTargetConcern(Breadcrumb crumb, CpsObject target) throws ModuleException
	{
		return findTargetConcern(crumb.getConcern(), crumb.getFilterPosition(), target);
	}

	/**
	 * Resolve the traget to a concern. Returns null when there is no target
	 * concern (for example in case of inner.* for input filters)
	 * 
	 * @param concern
	 * @param filterPosition
	 * @param target
	 * @return
	 * @throws ModuleException
	 */
	public static Concern findTargetConcern(Concern concern, FilterDirection filterPosition, CpsObject target)
			throws ModuleException
	{
		if (target.isInnerObject())
		{
			if (filterPosition == FilterDirection.Input)
			{
				// actual method is executed
				return null;
			}
			else if (filterPosition == FilterDirection.Output)
			{
				// like *+INPUT
				return concern;
			}
		}
		return target.getTypeReference().getReference().getConcern();
	}
}
