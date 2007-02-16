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

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MessageSelector;
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
		Iterator<Trail> results = crumb.getTrails();
		while (results.hasNext())
		{
			Trail trail = results.next();
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
		Iterator<Trail> trails = crumb.getTrails();
		while (trails.hasNext())
		{
			Trail trail = trails.next();
			logger.debug("[resolver] " + trail.getResultMessage().toString() + " for " + crumb.toString());
			if (trail.getTargetConcern() != null)
			{
				// crumbs always refer to input crumbs
				String selector = trail.getResultMessage().getSelector();
				if (Message.STAR_SELECTOR.equals(selector))
				{
					selector = crumb.getMessage().getSelector();
				}
				Breadcrumb toCrumb = graph.getInputCrumb(trail.getTargetConcern(), selector);
				logger.debug("[resolver]  leads to crumb: " + toCrumb);
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
			else if (flowNode.containsName(FlowNode.FILTER_ELEMENT_NODE))
			{
				trail.setRE(flowNode.getRepositoryLink());
			}
			else if (flowNode.containsName(FlowNode.CONTINUE_ACTION_NODE))
			{
				// ignore
			}
			else if (flowNode.containsName("DispatchAction"))
			{
				Message newMsg = new Message(state.getSubstitutionMessage().getTarget(), state.getSubstitutionMessage()
						.getSelector());
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
			Iterator it = state.getOutTransitions();
			int idx = 0;
			while (it.hasNext())
			{
				idx++;
				ExecutionTransition trans = (ExecutionTransition) it.next();
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
					traverseState(trans.getEndState(), crumb, crumb.addTrail(trail));
				}
			}
			if (idx == 0)
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
			else if (idx > 1)
			{
				logger.debug("[resolver] Branched into " + idx + " trails");
			}
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
		else if (Target.INNER.equals(targetString))
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