/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2007 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * $Id$
 */

package Composestar.Core.SECRET3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import Composestar.Core.CpsRepository2.Concern;
import Composestar.Core.CpsRepository2.PropertyPrefix;
import Composestar.Core.CpsRepository2.FilterElements.CanonAssignment;
import Composestar.Core.CpsRepository2.FilterElements.CanonProperty;
import Composestar.Core.CpsRepository2.FilterElements.MECompareStatement;
import Composestar.Core.CpsRepository2.Filters.FilterAction;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.util.regex.LabelSequence;
import Composestar.Core.FIRE2.util.regex.Labeler;
import Composestar.Core.FIRE2.util.regex.MatcherEx;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.SECRET3.Model.ExecModelOperationSequence;
import Composestar.Core.SECRET3.Model.FilterActionOperationSequence;
import Composestar.Core.SECRET3.Model.OperationSequence;
import Composestar.Core.SECRET3.Model.Resource;
import Composestar.Core.SECRET3.Model.ExecModelOperationSequence.GraphLabel;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Improved resource operation labeler that can be configured. This is used by
 * {@link MatcherEx} to validate the execution model along the configured rules.
 * 
 * @author Michiel Hendriks
 */
public class ResourceOperationLabelerEx implements Labeler
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.SECRET);

	/**
	 * Empty sequence
	 */
	protected static final LabelSequence DEFAULT_SEQUENCE = new LabelSequence();

	/**
	 * The read operation
	 */
	private static final String READ_OP = "read";

	/**
	 * The write operation
	 */
	private static final String WRITE_OP = "write";

	protected Map<Resource, Map<GraphLabel, LabelSequence>> labelMapping;

	protected Map<Resource, Map<FilterAction, LabelSequence>> filterActionMapping;

	/**
	 * The operation map for the current resource.
	 */
	protected Map<GraphLabel, LabelSequence> currentLabelMap;

	protected Map<FilterAction, LabelSequence> currentFAMap;

	/**
	 * Current selected concern
	 */
	protected Concern currentConcern;

	/**
	 * Current active resource
	 */
	protected Resource currentResource;

	protected SECRETResources resources;

	public ResourceOperationLabelerEx()
	{
		labelMapping = new HashMap<Resource, Map<GraphLabel, LabelSequence>>();
		filterActionMapping = new HashMap<Resource, Map<FilterAction, LabelSequence>>();
	}

	public ResourceOperationLabelerEx(SECRETResources inresources)
	{
		this();
		resources = inresources;
		configure();
	}

	/**
	 * Configure the labeler according to operation sequence.
	 * 
	 * @param opseq
	 */
	protected void configure()
	{
		for (OperationSequence os : resources.getOperationSequences())
		{
			if (os instanceof ExecModelOperationSequence)
			{
				ExecModelOperationSequence seq = (ExecModelOperationSequence) os;
				Set<GraphLabel> lbls = seq.getLabels();
				for (Entry<Resource, List<String>> resops : seq.getOperations().entrySet())
				{
					Map<GraphLabel, LabelSequence> resmap = labelMapping.get(resops.getKey());
					if (resmap == null)
					{
						resmap = new HashMap<GraphLabel, LabelSequence>();
						labelMapping.put(resops.getKey(), resmap);
					}
					for (GraphLabel lbl : lbls)
					{
						LabelSequence ops = resmap.get(lbl);
						if (ops == null)
						{
							ops = new LabelSequence();
							resmap.put(lbl, ops);
						}
						ops.addLabels(resops.getValue());
					}
				}
			}
			else if (os instanceof FilterActionOperationSequence)
			{
				FilterActionOperationSequence faos = (FilterActionOperationSequence) os;
				for (Entry<Resource, List<String>> resops : os.getOperations().entrySet())
				{
					Map<FilterAction, LabelSequence> resmap = filterActionMapping.get(resops.getKey());
					if (resmap == null)
					{
						resmap = new HashMap<FilterAction, LabelSequence>();
						filterActionMapping.put(resops.getKey(), resmap);
					}
					LabelSequence ops = resmap.get(faos.getFilterAction());
					if (ops == null)
					{
						ops = new LabelSequence();
						resmap.put(faos.getFilterAction(), ops);
					}
					ops.addLabels(resops.getValue());
				}
			}
		}
	}

	/**
	 * @param transition
	 * @param gl the label to be checked
	 * @return returns true if the current transition/start state contains the
	 *         label.
	 */
	protected final boolean hasLabel(ExecutionTransition transition, GraphLabel gl)
	{
		switch (gl.getType())
		{
			case Transition:
				return gl.getLabel().equals(transition.getLabel());
			case Node:
				return transition.getStartState().getFlowNode().containsName(gl.getLabel());
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.FIRE2.util.regex.Labeler#getLabels(Composestar.Core.
	 * FIRE2.model.ExecutionTransition)
	 */
	public LabelSequence getLabels(ExecutionTransition transition)
	{
		// TODO: read from rhs of compare operators and assignments
		// TODO: write from lhs of assignments
		FlowNode fnode = transition.getStartState().getFlowNode();
		boolean rwMsgProps =
				fnode.containsName(FlowNode.COMPARE_STATEMENT_NODE) || fnode.containsName(FlowNode.ASSIGNMENT_NODE);

		if (currentLabelMap == null && currentFAMap == null && !rwMsgProps)
		{
			return DEFAULT_SEQUENCE;
		}
		LabelSequence seq = new LabelSequence();

		if (currentFAMap != null)
		{
			FilterAction filterAction = getFilterAction(transition);
			LabelSequence faseq = currentFAMap.get(filterAction);
			if (faseq != null)
			{
				seq.addLabels(faseq.getLabelsEx());
			}
		}

		if (rwMsgProps)
		{
			if (fnode.getRepositoryLink() instanceof MECompareStatement)
			{
				MECompareStatement cmp = (MECompareStatement) fnode.getRepositoryLink();
				if (cmp.getLHS().getPrefix() == PropertyPrefix.MESSAGE)
				{
					if (currentResource.getName().equals(cmp.getLHS().getName()))
					{
						seq.addLabel(READ_OP);
					}
				}
				for (CpsVariable cpsvar : cmp.getRHS())
				{
					if (cpsvar instanceof CanonProperty)
					{
						if (((CanonProperty) cpsvar).getPrefix() == PropertyPrefix.MESSAGE)
						{
							if (currentResource.getName().equals(((CanonProperty) cpsvar).getName()))
							{
								seq.addLabel(READ_OP);
							}
						}
					}
				}
			}
			else if (fnode.getRepositoryLink() instanceof CanonAssignment)
			{
				CanonAssignment asgn = (CanonAssignment) fnode.getRepositoryLink();
				if (asgn.getProperty().getPrefix() == PropertyPrefix.MESSAGE)
				{
					if (currentResource.getName().equals(asgn.getProperty().getName()))
					{
						seq.addLabel(WRITE_OP);
					}
				}
				if (asgn.getValue() instanceof CanonProperty)
				{
					if (((CanonProperty) asgn.getValue()).getPrefix() == PropertyPrefix.MESSAGE)
					{
						if (currentResource.getName().equals(((CanonProperty) asgn.getValue()).getName()))
						{
							seq.addLabel(READ_OP);
						}
					}
				}
			}
		}

		if (currentLabelMap != null)
		{
			for (Entry<GraphLabel, LabelSequence> entry : currentLabelMap.entrySet())
			{
				if (hasLabel(transition, entry.getKey()))
				{
					seq.addLabels(entry.getValue().getLabelsEx());
				}
			}
		}
		return seq;
	}

	protected FilterAction getFilterAction(ExecutionTransition transition)
	{
		if (transition.getStartState().getFlowNode().containsName(FlowNode.FILTER_ACTION_NODE))
		{
			FlowNode node = transition.getStartState().getFlowNode();
			return (FilterAction) node.getRepositoryLink();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.FIRE2.util.regex.Labeler#getResourceOperations(Composestar
	 * .Core.FIRE2.model.ExecutionTransition)
	 */
	public List<String> getResourceOperations(ExecutionTransition transition, Set<String> excludeResources)
	{
		List<String> result = new ArrayList<String>();

		// TODO: read from rhs of compare operators and assignments
		// TODO: write from lhs of assignments
		// boolean rwMsgProps = false;

		// FilterAction filterAction = getFilterAction(transition);

		for (OperationSequence os : resources.getOperationSequences())
		{
			if (!(os instanceof ExecModelOperationSequence))
			{
				continue;
			}
			ExecModelOperationSequence seq = (ExecModelOperationSequence) os;

			for (GraphLabel lbl : seq.getLabels())
			{
				// if (filterAction != null && lbl.getType() == LabelType.Node
				// && lbl.getLabel().equals(filterAction))
				// {
				// int idx = result.indexOf(FILTER_ACTION_SEPARATOR);
				// if (idx > -1)
				// {
				// if (idx != result.size() - 1)
				// {
				// logger
				// .error("Multiple operation sequences for this filter action with different precedence.");
				// }
				// else
				// {
				// logger.info("Multiple operation sequences for this filter action.");
				// }
				// }
				// else
				// {
				// result.add(FILTER_ACTION_SEPARATOR);
				// }
				// continue;
				// }

				if (hasLabel(transition, lbl))
				{
					for (Entry<Resource, List<String>> entry : seq.getOperations().entrySet())
					{
						if (excludeResources != null && excludeResources.contains(entry.getKey().getName()))
						{
							continue;
						}
						for (String op : entry.getValue())
						{
							result.add(entry.getKey().getName() + "." + op);
						}
					}
				}
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.FIRE2.util.regex.Labeler#setCurrentConcern(Composestar
	 * .Core.CpsProgramRepository.Concern)
	 */
	public void setCurrentConcern(Concern curConcern)
	{
		currentConcern = curConcern;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.FIRE2.util.regex.Labeler#setCurrentResource(java.lang
	 * .String)
	 */
	public void setCurrentResource(Resource resource)
	{
		currentResource = resource;
		currentLabelMap = labelMapping.get(currentResource);
		currentFAMap = filterActionMapping.get(currentResource);
	}
}
