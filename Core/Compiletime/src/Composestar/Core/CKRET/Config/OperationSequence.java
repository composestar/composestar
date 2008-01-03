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

package Composestar.Core.CKRET.Config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A single operation sequence definition. As extracted from the configuration
 * sources.
 * 
 * @author Michiel Hendriks
 */
public class OperationSequence implements Serializable
{
	private static final long serialVersionUID = -4277166345789156242L;

	/**
	 * The precedence of this sequence. Can be used in configuration files to
	 * set a given order. FilterAction sequences always have the precedence of
	 * 0.
	 */
	protected int priority;

	protected Set<GraphLabel> labels;

	protected Map<Resource, List<String>> operations;

	public OperationSequence()
	{
		labels = new HashSet<GraphLabel>();
		operations = new HashMap<Resource, List<String>>();
	}

	public void setPriority(int prio)
	{
		priority = prio;
	}

	public int getPriority()
	{
		return priority;
	}

	public void addLabel(GraphLabel lbl)
	{
		if (lbl == null)
		{
			return;
		}
		labels.add(lbl);
	}

	public void addLabel(String lbl, String type)
	{
		if (type == null || type.trim().length() == 0)
		{
			throw new IllegalArgumentException("Type can not be null or empty");
		}
		if (lbl == null || lbl.trim().length() == 0)
		{
			throw new IllegalArgumentException("Labale can not be null or empty");
		}
		LabelType lt;
		if ("transition".equals(type) || "edge".equals(type))
		{
			lt = LabelType.Transition;
		}
		else if ("node".equals(type))
		{
			lt = LabelType.Node;
		}
		else
		{
			throw new IllegalArgumentException(String.format("Unknown label type: %s", type));
		}
		addLabel(new GraphLabel(lbl, lt));
	}

	public Set<GraphLabel> getLabels()
	{
		return Collections.unmodifiableSet(labels);
	}

	public void addOperations(Resource resource, List<String> opsequence)
	{
		if (resource == null)
		{
			throw new IllegalArgumentException("Resource can not be null");
		}
		// remove empty operations
		while (opsequence.remove(""))
		{
			// nop
		}
		if (opsequence.size() == 0)
		{
			return;
		}
		List<String> lst = operations.get(resource);
		if (lst == null)
		{
			lst = new ArrayList<String>();
			operations.put(resource, lst);
		}
		lst.addAll(opsequence);
	}

	public void addOperations(Resource resource, String[] opsequence)
	{
		addOperations(resource, Arrays.asList(opsequence));
	}

	/**
	 * Add a operation sequence to the given resource list. The provides string
	 * is split on the semicolon.
	 * 
	 * @param resource
	 * @param opsequence
	 */
	public void addOperations(Resource resource, String opsequence)
	{
		if (opsequence == null || opsequence.trim().length() == 0)
		{
			throw new IllegalArgumentException("Operation sequence can not be null or empty");
		}
		addOperations(resource, opsequence.trim().split(";"));
	}

	public Map<Resource, List<String>> getOperations()
	{
		return Collections.unmodifiableMap(operations);
	}

	/**
	 * A single label in a graph
	 */
	public static class GraphLabel implements Serializable
	{
		private static final long serialVersionUID = -1471321128861061209L;

		protected String label;

		protected LabelType type;

		public GraphLabel(String inlabel, LabelType intype)
		{
			label = inlabel;
			type = intype;
		}

		public String getLabel()
		{
			return label;
		}

		public LabelType getType()
		{
			return type;
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
			int result = 1;
			result = prime * result + (label == null ? 0 : label.hashCode());
			result = prime * result + (type == null ? 0 : type.hashCode());
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
			if (obj == null)
			{
				return false;
			}
			if (getClass() != obj.getClass())
			{
				return false;
			}
			final GraphLabel other = (GraphLabel) obj;
			if (label == null)
			{
				if (other.label != null)
				{
					return false;
				}
			}
			else if (!label.equals(other.label))
			{
				return false;
			}
			if (type == null)
			{
				if (other.type != null)
				{
					return false;
				}
			}
			else if (!type.equals(other.type))
			{
				return false;
			}
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			StringBuffer sb = new StringBuffer();
			sb.append("{");
			sb.append(type.toString());
			sb.append("} ");
			sb.append(label);
			return sb.toString();
		}

	}

	/**
	 * The type of label, either a transition label or node label
	 */
	public enum LabelType
	{
		Transition, Node
	}
}
