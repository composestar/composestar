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

package Composestar.Core.CKRET;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import Composestar.Core.CKRET.Config.CustomResource;
import Composestar.Core.CKRET.Config.OperationSequence;
import Composestar.Core.CKRET.Config.Resource;
import Composestar.Core.CKRET.Config.ResourceType;
import Composestar.Core.CKRET.Config.OperationSequence.GraphLabel;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.util.regex.LabelSequence;
import Composestar.Core.FIRE2.util.regex.Labeler;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Improved resource operation labeler that can be configured
 * 
 * @author Michiel Hendriks
 */
public class ResourceOperationLabelerEx implements Labeler
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(CKRET.MODULE_NAME);

	protected static final LabelSequence defaultSequence = new LabelSequence();

	protected Map<Resource, SortedMap<PrioGraphLabel, LabelSequence>> labelMapping;

	protected SortedMap<PrioGraphLabel, LabelSequence> currentMap;

	protected Concern currentConcern;

	protected Resource currentResource;

	public ResourceOperationLabelerEx()
	{
		labelMapping = new HashMap<Resource, SortedMap<PrioGraphLabel, LabelSequence>>();
	}

	public ResourceOperationLabelerEx(SECRETResources resources)
	{
		this();
		configure(resources.getOperationSequences(), resources);
	}

	/**
	 * Configure the labeler according to operation sequence.
	 * 
	 * @param opseq
	 */
	public void configure(Collection<OperationSequence> opseq, SECRETResources resources)
	{
		List<OperationSequence> sorted = new ArrayList<OperationSequence>(opseq);
		Collections.sort(sorted);
		for (OperationSequence seq : sorted)
		{
			Set<GraphLabel> lbls = seq.getLabels();
			for (Entry<String, List<String>> resops : seq.getOperations().entrySet())
			{
				String resName = resops.getKey();
				ResourceType rt = ResourceType.parse(resName);
				if (rt.isMeta())
				{
					logger.warn(String.format("Meta resources can not be used in resource operations"));
					continue;
				}
				Resource res;
				if (rt == ResourceType.Custom)
				{
					res = resources.getResource(resName);
				}
				else
				{
					res = resources.getResource(rt.toString());
				}
				if (res == null)
				{
					logger.warn(String.format("Unknown resource used in operation sequence (check spelling): %s",
							resName));
					res = new CustomResource(resName);
					resources.addResource(res);
				}
				SortedMap<PrioGraphLabel, LabelSequence> resmap = labelMapping.get(res);
				if (resmap == null)
				{
					resmap = new TreeMap<PrioGraphLabel, LabelSequence>();
					labelMapping.put(res, resmap);
				}
				for (GraphLabel lbl : lbls)
				{
					PrioGraphLabel plbl = new PrioGraphLabel(lbl, seq.getPriority());
					LabelSequence ops = resmap.get(plbl);
					if (ops == null)
					{
						ops = new LabelSequence();
						resmap.put(plbl, ops);
					}
					ops.addLabels(resops.getValue());
				}
			}
		}
	}

	protected final boolean hasLabel(ExecutionTransition transition, PrioGraphLabel gl)
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
	 * 
	 * @see Composestar.Core.FIRE2.util.regex.Labeler#getLabels(Composestar.Core.FIRE2.model.ExecutionTransition)
	 */
	public LabelSequence getLabels(ExecutionTransition transition)
	{
		if (currentMap == null)
		{
			return defaultSequence;
		}
		LabelSequence seq = new LabelSequence();
		for (Entry<PrioGraphLabel, LabelSequence> entry : currentMap.entrySet())
		{
			if (hasLabel(transition, entry.getKey()))
			{
				seq.addLabels(entry.getValue().getLabelsEx());
			}
		}
		return seq;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.FIRE2.util.regex.Labeler#getResourceOperations(Composestar.Core.FIRE2.model.ExecutionTransition)
	 */
	public List<String> getResourceOperations(ExecutionTransition transition)
	{
		List<String> result = new ArrayList<String>();

		String filterAction = null;
		if (transition.getStartState().getFlowNode().containsName(FlowNode.FILTER_ACTION_NODE))
		{
			// filter the operations for filter action (should be done during
			// weaving).
			FlowNode node = transition.getStartState().getFlowNode();
			Filter filter = (Filter) node.getRepositoryLink();

			if (node.containsName(FlowNode.ACCEPT_CALL_ACTION_NODE))
			{
				filterAction = filter.getFilterType().getAcceptCallAction().getName();
			}
			else if (node.containsName(FlowNode.REJECT_CALL_ACTION_NODE))
			{
				filterAction = filter.getFilterType().getRejectCallAction().getName();
			}
			else if (node.containsName(FlowNode.ACCEPT_RETURN_ACTION_NODE))
			{
				filterAction = filter.getFilterType().getAcceptReturnAction().getName();
			}
			else if (node.containsName(FlowNode.REJECT_RETURN_ACTION_NODE))
			{
				filterAction = filter.getFilterType().getRejectReturnAction().getName();
			}
		}

		for (Entry<Resource, SortedMap<PrioGraphLabel, LabelSequence>> rmap : labelMapping.entrySet())
		{
			for (Entry<PrioGraphLabel, LabelSequence> entry : rmap.getValue().entrySet())
			{
				if (filterAction != null && filterAction.equals(entry.getKey().getLabel()))
				{
					if (!result.contains(FILTER_ACTION_SEPARATOR))
					{
						result.add(FILTER_ACTION_SEPARATOR);
					}
					else if (result.indexOf(FILTER_ACTION_SEPARATOR) != result.size() - 1)
					{
						logger
								.error("Filter action separator is not the last in the operation list. Should never happen.");
					}
					continue;
				}
				if (hasLabel(transition, entry.getKey()))
				{
					for (String s : entry.getValue().getLabelsEx())
					{
						result.add(rmap.getKey().getName() + "." + s);
					}
				}
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.FIRE2.util.regex.Labeler#setCurrentConcern(Composestar.Core.CpsProgramRepository.Concern)
	 */
	public void setCurrentConcern(Concern curConcern)
	{
		currentConcern = curConcern;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.FIRE2.util.regex.Labeler#setCurrentResource(java.lang.String)
	 */
	public void setCurrentResource(Resource resource)
	{
		currentResource = resource;
		currentMap = labelMapping.get(currentResource);
	}

	class PrioGraphLabel extends GraphLabel implements Comparable<PrioGraphLabel>
	{
		protected int priority;

		public PrioGraphLabel(GraphLabel base, int inpriority)
		{
			super(base.getLabel(), base.getType());
			priority = inpriority;
		}

		public int compareTo(PrioGraphLabel o)
		{
			int res = priority - o.priority;
			if (res != 0)
			{
				return res;
			}
			res = label.compareTo(o.label);
			if (res != 0)
			{
				return res;
			}
			return type.compareTo(o.type);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + priority;
			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (!super.equals(obj))
			{
				return false;
			}
			if (getClass() != obj.getClass())
			{
				return false;
			}
			final PrioGraphLabel other = (PrioGraphLabel) obj;
			if (priority != other.priority)
			{
				return false;
			}
			return true;
		}

	}
}
