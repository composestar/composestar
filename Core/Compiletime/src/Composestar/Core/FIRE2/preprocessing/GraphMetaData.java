/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2008 University of Twente.
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

package Composestar.Core.FIRE2.preprocessing;

import groove.graph.DefaultLabel;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.FIRE2.model.FlowNode;

/**
 * Stores meta data for groove graph nodes, this can't be stored directly in the
 * Groove graphs due to technical limitations.
 * 
 * @author Michiel Hendriks
 */
public class GraphMetaData
{
	protected static final String EDGE_LINK_ID = "metaDataID";

	protected Label linkLabel;

	protected int id;

	protected Map<String, RepositoryEntity> repositoryLinks;

	protected Map<String, FlowNode> flowNodes;

	public GraphMetaData()
	{
		linkLabel = DefaultLabel.createLabel(EDGE_LINK_ID);
		repositoryLinks = new HashMap<String, RepositoryEntity>();
		flowNodes = new HashMap<String, FlowNode>();
	}

	/**
	 * Returns the meta data ID for the given node
	 * 
	 * @param graph
	 * @param forNode
	 * @param createMissing
	 * @return
	 */
	public String getNodeLinkID(Graph graph, Node forNode, boolean createMissing)
	{
		for (Edge e : graph.edgeSet(forNode))
		{
			if (e.label().equals(linkLabel))
			{
				Node idNode = e.opposite();
				if (idNode != null)
				{
					Collection<? extends Edge> edges = graph.outEdgeSet(idNode);
					for (Edge idEdge : edges)
					{
						// find self edge
						if (idEdge.opposite().equals(idEdge.opposite()))
						{
							return idEdge.label().text();
						}
					}
					if (createMissing)
					{
						String newid = nextIdLabel();
						graph.addEdge(idNode, DefaultLabel.createLabel(newid), idNode);
						return newid;
					}
					return null;
				}
			}
		}
		if (createMissing)
		{
			String newid = nextIdLabel();
			Node n = graph.addNode();
			graph.addEdge(n, DefaultLabel.createLabel(newid), n);
			graph.addEdge(forNode, linkLabel, n);
			return newid;
		}
		return null;
	}

	/**
	 * Create a new unique ID
	 * 
	 * @return
	 */
	protected String nextIdLabel()
	{
		return "id#" + (++id);
	}

	public void addRepositoryLink(Graph graph, Node forNode, RepositoryEntity re)
	{
		String id = getNodeLinkID(graph, forNode, true);
		if (id != null)
		{
			repositoryLinks.put(id, re);
		}
	}

	public RepositoryEntity getRepositoryLink(Graph graph, Node forNode)
	{
		String id = getNodeLinkID(graph, forNode, false);
		if (id != null)
		{
			return repositoryLinks.get(id);
		}
		return null;
	}

	public void addFlowNode(Graph graph, Node forNode, FlowNode fnode)
	{
		String id = getNodeLinkID(graph, forNode, false);
		if (id != null)
		{
			flowNodes.put(id, fnode);
		}
	}

	public FlowNode getFlowNode(Graph graph, Node forNode)
	{
		String id = getNodeLinkID(graph, forNode, false);
		if (id != null)
		{
			return flowNodes.get(id);
		}
		return null;
	}
}
