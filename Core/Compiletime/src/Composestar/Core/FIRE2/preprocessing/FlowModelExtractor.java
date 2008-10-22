/*
 * Created on 22-feb-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.Core.FIRE2.preprocessing;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.FIRE2.model.FlowModel;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.model.FlowTransition;

/**
 * This class extracts the flowmodel from the groove representation of the
 * flowmodel
 * 
 * @author Arjan de Roo
 */
public class FlowModelExtractor
{
	/**
	 * Annotation used to store the FlowNode instance in Groove graph
	 */
	public static final String ANNOT_FLOW_NODE = "FireFlowNode";

	private static final Label FILTERMODULE_LABEL = GrooveASTBuilderCN.createLabel(FlowNode.FILTER_MODULE_NODE);

	private static final Label END_LABEL = GrooveASTBuilderCN.createLabel(FlowNode.END_NODE);

	private static final String FLOW_TRUE_LABEL = "flowTrue";

	private static final String FLOW_FALSE_LABEL = "flowFalse";

	private static final String FLOW_NEXT_LABEL = "flow";

	private GraphMetaData meta;

	private BasicFlowModel model;

	private Map<Node, BasicFlowNode> nodeCache;

	public static FlowModel extract(Graph graph, GraphMetaData metaData)
	{
		FlowModelExtractor extractor = new FlowModelExtractor();
		extractor.meta = metaData;
		extractor.nodeCache = new HashMap<Node, BasicFlowNode>();
		return extractor.internalExtract(graph);
	}

	public FlowModel internalExtract(Graph graph)
	{
		model = new BasicFlowModel();

		BasicFlowNode startFlowNode, endFlowNode;
		FlowTransition transition;
		for (Edge edge : graph.edgeSet())
		{
			String label = edge.label().text();
			int type;
			if (label.equals(FLOW_NEXT_LABEL))
			{
				type = FlowTransition.FLOW_NEXT_TRANSITION;
			}
			else if (label.equals(FLOW_TRUE_LABEL))
			{
				type = FlowTransition.FLOW_TRUE_TRANSITION;
			}
			else if (label.equals(FLOW_FALSE_LABEL))
			{
				type = FlowTransition.FLOW_FALSE_TRANSITION;
			}
			else
			{
				continue;
			}

			startFlowNode = createFlowNode(graph, edge.source());
			endFlowNode = createFlowNode(graph, edge.opposite());

			transition = new BasicFlowTransition(type, startFlowNode, endFlowNode);

			model.addTransition(transition);
		}

		// startnode:
		Collection<? extends Edge> col = graph.labelEdgeSet(2, FILTERMODULE_LABEL);
		Iterator<? extends Edge> iter = col.iterator();
		if (!iter.hasNext())
		{
			// should never happen.
			throw new IllegalStateException("FilterModule node not found!");
		}

		Edge edge = iter.next();
		model.setStartNode(meta.getFlowNode(graph, edge.source()));

		// endnode:
		col = graph.labelEdgeSet(2, END_LABEL);
		iter = col.iterator();
		if (!iter.hasNext())
		{
			// should never happen.
			throw new IllegalStateException("End node not found!");
		}

		edge = iter.next();
		model.setEndNode(meta.getFlowNode(graph, edge.source()));

		return model;
	}

	private BasicFlowNode createFlowNode(Graph graph, Node graphNode)
	{
		if (nodeCache.containsKey(graphNode))
		{
			return nodeCache.get(graphNode);
		}

		BasicFlowNode node = (BasicFlowNode) meta.getFlowNode(graph, graphNode);

		if (node != null)
		{
			// node already has been created
			return node;
		}

		RepositoryEntity entity = meta.getRepositoryLink(graph, graphNode);

		Collection<? extends Edge> col = graph.edgeSet(graphNode);
		Set<String> names = new HashSet<String>();
		String label;
		for (Edge edge : col)
		{
			if (edge.source().equals(graphNode) && edge.opposite().equals(graphNode))
			{
				label = edge.label().text();
				names.add(label);
			}
		}

		node = new BasicFlowNode(names, entity);

		meta.addFlowNode(graph, graphNode, node);
		nodeCache.put(graphNode, node);

		model.addNode(node);

		return node;
	}

	private static class BasicFlowModel implements FlowModel
	{
		private static final long serialVersionUID = -8015349976168852314L;

		private FlowNode startNode;

		private FlowNode endNode;

		private List<FlowNode> nodes;

		private List<FlowTransition> transitions;

		public BasicFlowModel()
		{
			super();

			nodes = new ArrayList<FlowNode>();
			transitions = new ArrayList<FlowTransition>();
		}

		public void addNode(FlowNode node)
		{
			nodes.add(node);
		}

		public void addTransition(FlowTransition transition)
		{
			transitions.add(transition);
		}

		/**
		 * @return Returns the startNode.
		 */
		public FlowNode getStartNode()
		{
			return startNode;
		}

		/**
		 * @param startNode The startNode to set.
		 */
		public void setStartNode(FlowNode startNode)
		{
			this.startNode = startNode;
		}

		/**
		 * @return Returns the endNode.
		 */
		public FlowNode getEndNode()
		{
			return endNode;
		}

		/**
		 * @param endNode The endNode to set.
		 */
		public void setEndNode(FlowNode endNode)
		{
			this.endNode = endNode;
		}

		/**
		 * @deprecated use getNodesEx()
		 */
		@Deprecated
		public Iterator<FlowNode> getNodes()
		{
			return getNodesEx().iterator();
		}

		public List<FlowNode> getNodesEx()
		{
			return Collections.unmodifiableList(nodes);
		}

		/**
		 * @deprecated use getTransitionsEx();
		 */
		@Deprecated
		public Iterator<FlowTransition> getTransitions()
		{
			return getTransitionsEx().iterator();
		}

		public List<FlowTransition> getTransitionsEx()
		{
			return Collections.unmodifiableList(transitions);
		}
	}

	private static class BasicFlowNode implements FlowNode
	{
		private static final long serialVersionUID = 8046439221416988053L;

		private Set<String> names;

		// /**
		// * The reference to the repository item. Will be used to resolve the
		// * transient repository entity.
		// */
		// private String repositoryLink;

		private/* transient */RepositoryEntity repositoryEntity;

		/**
		 * Contains all transitions originating from this node.
		 */
		private List<FlowTransition> transitions;

		/**
		 * Default constructor
		 * 
		 * @param names
		 * @param repositoryLink
		 */
		public BasicFlowNode(Set<String> innames, RepositoryEntity entity)
		{
			super();

			names = innames;
			repositoryEntity = entity;
			// repositoryLink = repositoryEntity.getRepositoryKey();
			// System.err.println("FlowNode: " + repositoryLink);
			transitions = new ArrayList<FlowTransition>();
		}

		public void addTransition(FlowTransition transition)
		{
			transitions.add(transition);
		}

		public void removeTransition(FlowTransition transition)
		{
			transitions.remove(transition);
		}

		/**
		 * @deprecated use getTransitionsEx()
		 */
		@Deprecated
		public Iterator<FlowTransition> getTransitions()
		{
			return getTransitionsEx().iterator();
		}

		public List<FlowTransition> getTransitionsEx()
		{
			return Collections.unmodifiableList(transitions);
		}

		/**
		 * Returns the (first) transition from this startnode to the given
		 * endnode, or null when no such transition exists.
		 * 
		 * @return
		 */
		public FlowTransition getTransition(FlowNode endNode)
		{
			for (FlowTransition transition : transitions)
			{
				if (transition.getEndNode().equals(endNode))
				{
					return transition;
				}
			}

			return null;
		}

		/**
		 * @return Returns the names.
		 * @deprecated use getNamesEx();
		 */
		@Deprecated
		public Iterator<String> getNames()
		{
			return getNamesEx().iterator();
		}

		/**
		 * @return Returns the names.
		 */
		public Set<String> getNamesEx()
		{
			return Collections.unmodifiableSet(names);
		}

		public boolean containsName(String name)
		{
			return names.contains(name);
		}

		/**
		 * @return Returns the repositoryLink.
		 */
		public RepositoryEntity getRepositoryLink()
		{
			return repositoryEntity;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			return names.toString();
		}
	}

	private static class BasicFlowTransition implements FlowTransition
	{
		private static final long serialVersionUID = -6477106037510629001L;

		/**
		 * The type of the transition;
		 */
		private int type;

		/**
		 * The startNode
		 */
		private BasicFlowNode startNode;

		/**
		 * The endNode
		 */
		private BasicFlowNode endNode;

		/**
		 * The constructor
		 * 
		 * @param startNode
		 * @param type
		 * @param endNode
		 */
		public BasicFlowTransition(int type, BasicFlowNode startNode, BasicFlowNode endNode)
		{
			super();

			this.type = type;
			this.startNode = startNode;
			this.endNode = endNode;

			startNode.addTransition(this);
		}

		/**
		 * @return Returns the type.
		 */
		public int getType()
		{
			return type;
		}

		/**
		 * @return Returns the endNode.
		 */
		public FlowNode getEndNode()
		{
			return endNode;
		}

		/**
		 * @return Returns the startNode.
		 */
		public FlowNode getStartNode()
		{
			return startNode;
		}
	}
}
