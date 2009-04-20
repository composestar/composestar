/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2009 University of Twente.
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

package Composestar.Core.SECRET3.Model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * An operation sequence defined on certain Groove graph elements
 * 
 * @author Michiel Hendriks
 */
public class ExecModelOperationSequence extends OperationSequence
{
	/**
	 * Set of graph labels that are associated with this sequence.
	 */
	protected Set<GraphLabel> labels;

	public ExecModelOperationSequence()
	{
		super();
		labels = new HashSet<GraphLabel>();
	}

	/**
	 * Add a new label to this operation sequence
	 * 
	 * @param lbl
	 */
	public void addLabel(GraphLabel lbl)
	{
		if (lbl == null)
		{
			return;
		}
		labels.add(lbl);
	}

	/**
	 * Add a new label by name and type
	 * 
	 * @param lbl
	 * @param type must be either "node", "transition", "edge"
	 */
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

	/**
	 * @return the set of graph labels
	 */
	public Set<GraphLabel> getLabels()
	{
		return Collections.unmodifiableSet(labels);
	}

	/**
	 * A single label in a graph
	 */
	public static class GraphLabel implements Serializable
	{
		private static final long serialVersionUID = -1471321128861061209L;

		/**
		 * The label name
		 */
		protected String label;

		/**
		 * the graph element this label is applied to
		 */
		protected LabelType type;

		public GraphLabel(String inlabel, LabelType intype)
		{
			label = inlabel;
			type = intype;
		}

		/**
		 * @return the label
		 */
		public String getLabel()
		{
			return label;
		}

		/**
		 * @return the type
		 */
		public LabelType getType()
		{
			return type;
		}

		/*
		 * (non-Javadoc)
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
		/**
		 * A edge or transition from one node to the other
		 */
		Transition,
		/**
		 * A node in the graph
		 */
		Node
	}
}
