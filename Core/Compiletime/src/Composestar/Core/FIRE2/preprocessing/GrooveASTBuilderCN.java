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

import groove.graph.DefaultGraph;
import groove.graph.DefaultLabel;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.Label;
import groove.graph.Node;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import Composestar.Core.CpsRepository2.PropertyNames;
import Composestar.Core.CpsRepository2.PropertyPrefix;
import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.FilterElements.BinaryMEOperator;
import Composestar.Core.CpsRepository2.FilterElements.CanonAssignment;
import Composestar.Core.CpsRepository2.FilterElements.CanonProperty;
import Composestar.Core.CpsRepository2.FilterElements.FilterElement;
import Composestar.Core.CpsRepository2.FilterElements.FilterElementExpression;
import Composestar.Core.CpsRepository2.FilterElements.MECompareStatement;
import Composestar.Core.CpsRepository2.FilterElements.MECondition;
import Composestar.Core.CpsRepository2.FilterElements.MELiteral;
import Composestar.Core.CpsRepository2.FilterElements.MatchingExpression;
import Composestar.Core.CpsRepository2.FilterElements.UnaryMEOperator;
import Composestar.Core.CpsRepository2.FilterModules.Filter;
import Composestar.Core.CpsRepository2.FilterModules.FilterExpression;
import Composestar.Core.CpsRepository2.FilterModules.FilterModule;
import Composestar.Core.CpsRepository2.Filters.FilterAction;
import Composestar.Core.CpsRepository2.Filters.FilterType;
import Composestar.Core.CpsRepository2.Filters.PrimitiveFilterType;
import Composestar.Core.CpsRepository2.TypeSystem.CpsLiteral;
import Composestar.Core.CpsRepository2.TypeSystem.CpsObject;
import Composestar.Core.CpsRepository2.TypeSystem.CpsProgramElement;
import Composestar.Core.CpsRepository2.TypeSystem.CpsSelector;
import Composestar.Core.CpsRepository2.TypeSystem.CpsTypeProgramElement;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2Impl.FilterElements.AndMEOper;
import Composestar.Core.CpsRepository2Impl.FilterElements.AnnotationMatching;
import Composestar.Core.CpsRepository2Impl.FilterElements.CORFilterElmOper;
import Composestar.Core.CpsRepository2Impl.FilterElements.CompatibilityMatching;
import Composestar.Core.CpsRepository2Impl.FilterElements.InstanceMatching;
import Composestar.Core.CpsRepository2Impl.FilterElements.NotMEOper;
import Composestar.Core.CpsRepository2Impl.FilterElements.OrMEOper;
import Composestar.Core.CpsRepository2Impl.FilterElements.SignatureMatching;
import Composestar.Core.CpsRepository2Impl.FilterModules.SequentialFilterOper;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.model.FireModel.FilterDirection;
import Composestar.Core.Master.ModuleNames;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Builds the AST graph to be used by Groove to create the FlowModel. This class
 * creates the AST graph for the canonical filter notation.
 * 
 * @author Michiel Hendriks
 */
public class GrooveASTBuilderCN
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.FIRE);

	public static final String ACCEPT_CALL_EDGE = "acceptCall";

	public static final String ACCEPT_RETURN_EDGE = "acceptReturn";

	/**
	 * A key for the annotations in {@link AnnotatedEdge} and {@link Node} to
	 * the repository entity related to the edge or node.
	 */
	public static final String ANNOT_REPOSITORY_ENTITY = "RepositoryEntity";

	public static final String ASSIGNMENT_EDGE = "assignment";

	/**
	 * Used for compare and assignment values who's value is deferred (i.e.
	 * message property)
	 */
	public static final String DEFERRED_EDGE = "deferred";

	/**
	 * Used for initialization of deferred values
	 */
	public static final String DEFERRED_VALUE_NODE = "DeferredValue";

	/**
	 * Edge label to expressions (used to point to filter-, filterelement,
	 * matching-expressions)
	 */
	public static final String EXPRESSION_EDGE = "expression";

	/**
	 * The left hand side of the operator
	 */
	public static final String LHS_EDGE = "lhs";

	public static final String OPERAND_EDGE = "operand";

	public static final String REJECT_CALL_EDGE = "rejectCall";

	public static final String REJECT_RETURN_EDGE = "rejectReturn";

	/**
	 * The right hand side of the operator
	 */
	public static final String RHS_EDGE = "rhs";

	/**
	 * Label value prefix for selectors
	 */
	public static final String VP_SELECTOR = "sel=";

	/**
	 * Label value prefix for objects
	 */
	public static final String VP_OBJECT = "obj=";

	/**
	 * Label value prefix for literals
	 */
	public static final String VP_LITERAL = "lit=";

	/**
	 * Label value prefix for program elements
	 */
	public static final String VP_PROGRAM_ELEMENT = "pe=";

	/**
	 * The filter expression to be explored
	 */
	protected FilterExpression fex;

	/**
	 * The filter module being processed
	 */
	protected FilterModule filterModule;

	/**
	 * The graph under construction
	 */
	protected Graph graph;

	protected Map<String, Node> cachedValueNodes;

	protected GraphMetaData metaData;

	/**
	 * Constructs a Groove graph for a giver filter module and filter direction
	 * 
	 * @param fm The filter module to create the graph for
	 * @param dir The filter direction to use
	 * @return A Graph instance
	 * @throws NullPointerException thrown when the filter module is null
	 * @throws IllegalStateException thrown when there was an inconsistency
	 *             during the graph creation. This exception should never occure
	 *             unless an unknown entity was found.
	 */
	public static Graph createAST(FilterModule fm, FilterDirection dir, GraphMetaData meta)
			throws NullPointerException, IllegalStateException
	{
		GrooveASTBuilderCN builder = new GrooveASTBuilderCN(fm, dir, meta);
		builder.createFMGraph();
		return builder.getGraph();
	}

	public static final Label createLabel(String labelStr)
	{
		return DefaultLabel.createLabel(labelStr);
	}

	/**
	 * Create a label for the filter action
	 * 
	 * @param labelStr
	 * @return
	 */
	public static final Label createFilterActionLabel(String labelStr)
	{
		return DefaultLabel.createLabel(createFilterActionText(labelStr));
	}

	public static final String createFilterActionText(String labelStr)
	{
		return '@' + labelStr;
	}

	/**
	 * Create a graph builder for the given filter module and filter direction
	 * 
	 * @param fm
	 * @param dir
	 */
	protected GrooveASTBuilderCN(FilterModule fm, FilterDirection dir, GraphMetaData meta)
	{
		metaData = meta;
		cachedValueNodes = new HashMap<String, Node>();
		if (fm == null)
		{
			throw new NullPointerException("Filter module can not be null");
		}
		filterModule = fm;
		fex = null;
		switch (dir)
		{
			case OUTPUT:
				fex = fm.getOutputFilterExpression();
				break;
			case INPUT:
			default:
				fex = fm.getInputFilterExpression();
				break;
		}
		graph = new DefaultGraph();
		GraphInfo.getInfo(graph, true);
	}

	/**
	 * @return The graph under construction
	 */
	public Graph getGraph()
	{
		return graph;
	}

	/**
	 * Make this node a flow node. This reduces some actions needed by groove
	 * 
	 * @param node
	 */
	protected void makeFlowNode(Node node)
	{
		graph.addEdge(node, createLabel(FlowNode.FLOW_NODE), node);
	}

	/**
	 * Associate the repository entity with the given node
	 * 
	 * @param node
	 * @param re
	 */
	protected void addRepositoryEntity(Node node, RepositoryEntity re)
	{
		metaData.addRepositoryLink(graph, node, re);
	}

	/**
	 * Create a compare statement node
	 * 
	 * @param expr
	 * @return
	 * @throws IllegalStateException
	 */
	protected Node createCompareStatementNode(MECompareStatement cmp) throws IllegalStateException
	{
		Node cmpNode = graph.addNode();
		addRepositoryEntity(cmpNode, cmp);

		graph.addEdge(cmpNode, createLabel(FlowNode.COMPARE_STATEMENT_NODE), cmpNode);

		if (cmp instanceof InstanceMatching)
		{
			graph.addEdge(cmpNode, createLabel(FlowNode.INSTANCE_MATCHING), cmpNode);
		}
		else if (cmp instanceof SignatureMatching)
		{
			graph.addEdge(cmpNode, createLabel(FlowNode.SIGNATURE_MATCHING), cmpNode);
		}
		else if (cmp instanceof CompatibilityMatching)
		{
			graph.addEdge(cmpNode, createLabel(FlowNode.COMPATIBILITY_MATCHING), cmpNode);
		}
		else if (cmp instanceof AnnotationMatching)
		{
			graph.addEdge(cmpNode, createLabel(FlowNode.ANNOTATION_MATCHING), cmpNode);
		}
		else
		{
			throw new IllegalStateException(String.format("Unknown compare statement class: %s", cmp.getClass()
					.getName()));
		}

		if (PropertyPrefix.MESSAGE != cmp.getLHS().getPrefix())
		{
			logger.error(String.format("Compared property is not a message property: %s", cmp.getLHS().getName()), cmp
					.getLHS());
			throw new IllegalStateException(String.format("Compared property is not a message property: %s", cmp
					.getLHS().getName()));
		}

		Node node = graph.addNode();
		addRepositoryEntity(node, cmp.getLHS());

		// TODO: how to handle checking for non-instance/non-selector
		// properties? (i.e. message.bla = this.is.my.class)
		graph.addEdge(node, createLabel(cmp.getLHS().getBaseName()), node);
		graph.addEdge(cmpNode, createLabel(LHS_EDGE), node);

		// TODO: how to handle type conversion in case of selectors, in most
		// cases selectors are used as literals, but they could also be method
		// references
		for (CpsVariable var : cmp.getRHS())
		{
			node = createCpsVariableNode(var, true);
			graph.addEdge(cmpNode, createLabel(RHS_EDGE), node);
			// because the node does not always link to this particular variable
			// edge.addAnnotation(ANNOT_REPOSITORY_ENTITY, var);
			// TODO: ...
		}

		return cmpNode;
	}

	/**
	 * Create a CpsVariable node, for each unique value there is a single node.
	 * The repository entity linked to is the first occurrence of said value.
	 * The values on the nodes do not reflect the actual value, just an encoded
	 * string to make groove processing easier. Check the annotation for the
	 * real value. Reusing the value nodes is an important optimization in the
	 * initialization of the execution flow graphs within groove.
	 * 
	 * @param var
	 * @param createDefValNode TODO
	 * @return
	 * @throws IllegalStateException Thrown when an unknown CpsVariable was
	 *             encountered
	 */
	protected Node createCpsVariableNode(CpsVariable var, boolean createDefValNode) throws IllegalStateException
	{
		String valueEdge = null;
		String typeLabel = null;
		// note: the used prefixes are only there for convenience
		if (var instanceof CpsSelector)
		{
			valueEdge = VP_SELECTOR + ((CpsSelector) var).getName();
			typeLabel = "CpsSelector";
		}
		else if (var instanceof CanonProperty)
		{
			CanonProperty prop = (CanonProperty) var;
			if (prop.getPrefix() == PropertyPrefix.NONE && PropertyNames.INNER.equals(prop.getBaseName()))
			{
				valueEdge = VP_OBJECT + PropertyNames.INNER;
				typeLabel = "CpsObject";
			}
			else if (prop.getPrefix() == PropertyPrefix.MESSAGE)
			{
				// always return unique nodes for deferred values
				Node node = graph.addNode();
				addRepositoryEntity(node, var);

				Node deferredNode = graph.addNode();
				addRepositoryEntity(deferredNode, var);

				graph.addEdge(node, createLabel(DEFERRED_EDGE), deferredNode);

				graph.addEdge(deferredNode, createLabel(prop.getBaseName()), deferredNode);

				valueEdge = "deferred:" + prop.getBaseName();
				if (createDefValNode && !cachedValueNodes.containsKey(valueEdge))
				{
					Node defValueNode = graph.addNode();
					graph.addEdge(defValueNode, createLabel(DEFERRED_VALUE_NODE), defValueNode);

					deferredNode = graph.addNode();
					addRepositoryEntity(deferredNode, var);
					graph.addEdge(defValueNode, createLabel(DEFERRED_EDGE), deferredNode);

					graph.addEdge(deferredNode, createLabel(prop.getBaseName()), deferredNode);

					cachedValueNodes.put(valueEdge, defValueNode);
				}

				return node;
			}
			else
			{
				logger
						.error(String
								.format("Only message properties can be used assigned to other message properties."),
								var);
				throw new IllegalStateException(String
						.format("Only message properties can be used assigned to other message properties."));
			}
		}
		else if (var instanceof CpsObject)
		{
			// the object is always the same instance, hence the hash code
			// should be safe
			valueEdge = VP_OBJECT + System.identityHashCode(var);
			typeLabel = "CpsObject";
		}
		else if (var instanceof CpsLiteral)
		{
			// this could result in very large labels
			valueEdge = VP_LITERAL + ((CpsLiteral) var).getLiteralValue();
			typeLabel = "CpsLiteral";
		}
		else if (var instanceof CpsProgramElement)
		{
			// different program elements should produce different hashes...
			valueEdge = VP_PROGRAM_ELEMENT + System.identityHashCode(((CpsProgramElement) var).getProgramElement());
			if (var instanceof CpsTypeProgramElement)
			{
				typeLabel = "CpsTypeProgramElement";
			}
			else
			{
				typeLabel = "CpsProgramElement";
			}
		}
		else
		{
			throw new IllegalStateException(String.format("Unknown CpsVariable type: %s", var.getClass().getName()));
		}

		if (cachedValueNodes.containsKey(valueEdge))
		{
			return cachedValueNodes.get(valueEdge);
		}

		Node node = graph.addNode();
		addRepositoryEntity(node, var);

		graph.addEdge(node, createLabel(valueEdge), node);
		if (typeLabel != null)
		{
			graph.addEdge(node, createLabel(typeLabel), node);
		}

		cachedValueNodes.put(valueEdge, node);

		return node;
	}

	/**
	 * @param asgn
	 * @return
	 * @throws IllegalStateException
	 */
	protected Node createAssignmentNode(CanonAssignment asgn) throws IllegalStateException
	{
		Node cmpNode = graph.addNode();
		addRepositoryEntity(cmpNode, asgn);

		graph.addEdge(cmpNode, createLabel(FlowNode.ASSIGNMENT_NODE), cmpNode);

		makeFlowNode(cmpNode);

		if (PropertyPrefix.MESSAGE != asgn.getProperty().getPrefix())
		{
			logger.error(
					String.format("Assigned property is not a message property: %s", asgn.getProperty().getName()),
					asgn.getProperty());
			throw new IllegalStateException(String.format("Assigned property is not a message property: %s", asgn
					.getProperty().getName()));
		}

		Node node = graph.addNode();
		addRepositoryEntity(node, asgn.getProperty());

		graph.addEdge(node, createLabel(asgn.getProperty().getBaseName()), node);
		graph.addEdge(cmpNode, createLabel(LHS_EDGE), node);

		node = createCpsVariableNode(asgn.getValue(), false);
		graph.addEdge(cmpNode, createLabel(RHS_EDGE), node);
		// because the node does not always link to this particular variable
		// edge.addAnnotation(ANNOT_REPOSITORY_ENTITY, asgn.getValue());
		// TODO: ...

		return cmpNode;
	}

	/**
	 * @param expr
	 * @return
	 * @throws IllegalStateException Thrown in case of an unknown filter element
	 *             expression class
	 */
	protected Node createFilterElementExpressionNode(FilterElementExpression expr) throws IllegalStateException
	{
		Node exprNode = null;
		if (expr instanceof FilterElement)
		{
			exprNode = createFilterElementNode((FilterElement) expr);
		}
		else if (expr instanceof CORFilterElmOper)
		{
			exprNode = graph.addNode();
			addRepositoryEntity(exprNode, expr);

			graph.addEdge(exprNode, createLabel(FlowNode.COR_NODE), exprNode);

			// the left hand side
			graph.addEdge(exprNode, createLabel(LHS_EDGE), createFilterElementExpressionNode(((CORFilterElmOper) expr)
					.getLHS()));

			// the right hand side
			graph.addEdge(exprNode, createLabel(RHS_EDGE), createFilterElementExpressionNode(((CORFilterElmOper) expr)
					.getRHS()));
		}
		else
		{
			throw new IllegalStateException(String.format("Unknown filter element expression class: %s", expr
					.getClass().getName()));
		}

		if (exprNode != null)
		{
			graph.addEdge(exprNode, createLabel(FlowNode.FILTER_ELEMENT_EXPRESSION_NODE), exprNode);
			makeFlowNode(exprNode);
		}
		return exprNode;
	}

	/**
	 * Create the filter element node
	 * 
	 * @param elm
	 * @return
	 */
	protected Node createFilterElementNode(FilterElement elm)
	{
		Node elmNode = graph.addNode();
		addRepositoryEntity(elmNode, elm);

		graph.addEdge(elmNode, createLabel(FlowNode.FILTER_ELEMENT_NODE), elmNode);

		Node matchExprNode = createMatchingExpressionNode(elm.getMatchingExpression());
		graph.addEdge(elmNode, createLabel(EXPRESSION_EDGE), matchExprNode);

		// create a linked list of assignments
		Node lastNode = elmNode;
		Collection<CanonAssignment> asgns = elm.getAssignments();
		int asgnsNodes = 0;
		for (CanonAssignment asgn : asgns)
		{
			if (PropertyPrefix.MESSAGE != asgn.getProperty().getPrefix())
			{
				continue;
			}
			Node assignNode = createAssignmentNode(asgn);
			graph.addEdge(lastNode, createLabel(ASSIGNMENT_EDGE), assignNode);
			lastNode = assignNode;
			asgnsNodes++;
		}

		if (asgnsNodes == 0)
		{
			// add a dummy assignment node
			Node dummy = graph.addNode();
			// force the creation of a meta data id
			metaData.getNodeLinkID(graph, dummy, true);

			graph.addEdge(dummy, createLabel(FlowNode.ASSIGNMENT_NODE), dummy);
			makeFlowNode(dummy);

			graph.addEdge(elmNode, createLabel(ASSIGNMENT_EDGE), dummy);
		}

		return elmNode;
	}

	/**
	 * Create the nodes for the filter expression
	 * 
	 * @param expr
	 * @return
	 * @throws IllegalStateException thrown when an unknown FilterExpression
	 *             implementation was encounters
	 */
	protected Node createFilterExpressionNode(FilterExpression expr) throws IllegalStateException
	{
		Node exprNode = null;
		if (expr instanceof Filter)
		{
			exprNode = createFilterNode((Filter) expr);
		}
		else if (expr instanceof SequentialFilterOper)
		{
			exprNode = graph.addNode();
			addRepositoryEntity(exprNode, expr);

			graph.addEdge(exprNode, createLabel(FlowNode.SEQUENTIAL_FILTER_OPER_NODE), exprNode);

			// the left hand side
			graph.addEdge(exprNode, createLabel(LHS_EDGE), createFilterExpressionNode(((SequentialFilterOper) expr)
					.getLHS()));

			// the right hand side
			graph.addEdge(exprNode, createLabel(RHS_EDGE), createFilterExpressionNode(((SequentialFilterOper) expr)
					.getRHS()));
		}
		else
		{
			throw new IllegalStateException(String.format("Unknown filter expression class: %s", expr.getClass()
					.getName()));
		}
		if (expr != null)
		{
			graph.addEdge(exprNode, createLabel(FlowNode.FILTER_EXPRESSION_NODE), exprNode);
			makeFlowNode(exprNode);
		}
		return exprNode;
	}

	/**
	 * Create the node for the filter
	 * 
	 * @param filter
	 * @return
	 * @throws IllegalStateException
	 */
	protected Node createFilterNode(Filter filter) throws IllegalStateException
	{
		Node filterNode = graph.addNode();
		addRepositoryEntity(filterNode, filter);

		graph.addEdge(filterNode, createLabel(FlowNode.FILTER_NODE), filterNode);

		graph.addEdge(filterNode, createLabel(EXPRESSION_EDGE), createFilterElementExpressionNode(filter
				.getElementExpression()));

		FilterType ft = filter.getType();
		if (ft instanceof PrimitiveFilterType)
		{
			Node node = createFilterActionNode(((PrimitiveFilterType) ft).getAcceptReturnAction());
			graph.addEdge(node, createLabel(FlowNode.ACCEPT_RETURN_ACTION_NODE), node);
			graph.addEdge(filterNode, createLabel(ACCEPT_RETURN_EDGE), node);

			node = createFilterActionNode(((PrimitiveFilterType) ft).getAcceptCallAction());
			graph.addEdge(node, createLabel(FlowNode.ACCEPT_CALL_ACTION_NODE), node);
			graph.addEdge(filterNode, createLabel(ACCEPT_CALL_EDGE), node);

			node = createFilterActionNode(((PrimitiveFilterType) ft).getRejectReturnAction());
			graph.addEdge(node, createLabel(FlowNode.REJECT_RETURN_ACTION_NODE), node);
			graph.addEdge(filterNode, createLabel(REJECT_RETURN_EDGE), node);

			node = createFilterActionNode(((PrimitiveFilterType) ft).getRejectCallAction());
			graph.addEdge(node, createLabel(FlowNode.REJECT_CALL_ACTION_NODE), node);
			graph.addEdge(filterNode, createLabel(REJECT_CALL_EDGE), node);
		}
		else
		{
			throw new IllegalStateException(String.format("Unsupported filter type class: %s", ft.getClass().getName()));
		}

		return filterNode;
	}

	/**
	 * Create the filter action node
	 * 
	 * @param action
	 * @return
	 */
	protected Node createFilterActionNode(FilterAction action)
	{
		Node actionNode = graph.addNode();
		addRepositoryEntity(actionNode, action);

		graph.addEdge(actionNode, createLabel(FlowNode.FILTER_ACTION_NODE), actionNode);
		graph.addEdge(actionNode, createFilterActionLabel(action.getName()), actionNode);
		makeFlowNode(actionNode);

		switch (action.getFlowBehavior())
		{
			case EXIT:
				graph.addEdge(actionNode, createLabel(FlowNode.EXIT_ACTION_NODE), actionNode);
				break;
			case RETURN:
				graph.addEdge(actionNode, createLabel(FlowNode.RETURN_ACTION_NODE), actionNode);
				break;
			case CONTINUE:
			default:
				graph.addEdge(actionNode, createLabel(FlowNode.CONTINUE_ACTION_NODE), actionNode);
				break;
		}

		return actionNode;
	}

	/**
	 * @return The graph for the filter module
	 * @throws IllegalStateException Thrown in case of unknown entities in the
	 *             AST
	 */
	protected void createFMGraph() throws IllegalStateException
	{
		Node fmNode = graph.addNode();
		addRepositoryEntity(fmNode, filterModule);

		graph.addEdge(fmNode, createLabel(FlowNode.FILTER_MODULE_NODE), fmNode);

		makeFlowNode(fmNode);

		if (fex != null)
		{
			Node feNode = createFilterExpressionNode(fex);
			graph.addEdge(fmNode, createLabel(EXPRESSION_EDGE), feNode);
		}

		// end node
		Node endNode = graph.addNode();
		addRepositoryEntity(endNode, filterModule);

		graph.addEdge(endNode, createLabel(FlowNode.END_NODE), endNode);
		makeFlowNode(endNode);

		// exit node
		Node exitNode = graph.addNode();
		addRepositoryEntity(exitNode, filterModule);

		graph.addEdge(exitNode, createLabel(FlowNode.EXIT_NODE), exitNode);
		makeFlowNode(exitNode);

		// return node
		Node returnNode = graph.addNode();
		addRepositoryEntity(returnNode, filterModule);

		graph.addEdge(returnNode, createLabel(FlowNode.RETURN_NODE), returnNode);
		makeFlowNode(returnNode);

		graph.setFixed();
	}

	/**
	 * Create the matching expression nodes
	 * 
	 * @param expr
	 * @return
	 * @throws IllegalStateException
	 */
	protected Node createMatchingExpressionNode(MatchingExpression expr) throws IllegalStateException
	{
		Node exprNode = null;

		if (expr instanceof BinaryMEOperator)
		{
			exprNode = graph.addNode();
			addRepositoryEntity(exprNode, expr);

			if (expr instanceof AndMEOper)
			{
				graph.addEdge(exprNode, createLabel(FlowNode.MATCH_AND_NODE), exprNode);
			}
			else if (expr instanceof OrMEOper)
			{
				graph.addEdge(exprNode, createLabel(FlowNode.MATCH_OR_NODE), exprNode);
			}
			else
			{
				throw new IllegalStateException(String.format("Unknown matching expression class: %s", expr.getClass()
						.getName()));
			}

			// the left hand side
			graph.addEdge(exprNode, createLabel(LHS_EDGE), createMatchingExpressionNode(((BinaryMEOperator) expr)
					.getLHS()));

			// the right hand side
			graph.addEdge(exprNode, createLabel(RHS_EDGE), createMatchingExpressionNode(((BinaryMEOperator) expr)
					.getRHS()));
		}
		else if (expr instanceof UnaryMEOperator)
		{
			exprNode = graph.addNode();
			addRepositoryEntity(exprNode, expr);

			if (expr instanceof NotMEOper)
			{
				graph.addEdge(exprNode, createLabel(FlowNode.MATCH_NOT_NODE), exprNode);
			}
			else
			{
				throw new IllegalStateException(String.format("Unknown matching expression class: %s", expr.getClass()
						.getName()));
			}

			// the right hand side
			graph.addEdge(exprNode, createLabel(OPERAND_EDGE), createMatchingExpressionNode(((UnaryMEOperator) expr)
					.getOperand()));
		}
		else if (expr instanceof MECompareStatement)
		{
			exprNode = createCompareStatementNode((MECompareStatement) expr);
		}
		else if (expr instanceof MECondition)
		{
			exprNode = graph.addNode();
			addRepositoryEntity(exprNode, expr);

			graph.addEdge(exprNode, createLabel(FlowNode.CONDITION_NODE), exprNode);
		}
		else if (expr instanceof MELiteral)
		{
			exprNode = graph.addNode();
			addRepositoryEntity(exprNode, expr);

			if (((MELiteral) expr).getLiteralValue())
			{
				graph.addEdge(exprNode, createLabel(FlowNode.TRUE_NODE), exprNode);
			}
			else
			{
				graph.addEdge(exprNode, createLabel(FlowNode.FALSE_NODE), exprNode);
			}
		}
		else
		{
			throw new IllegalStateException(String.format("Unknown matching expression class: %s", expr.getClass()
					.getName()));
		}

		if (exprNode != null)
		{
			graph.addEdge(exprNode, createLabel(FlowNode.MATCHING_EXPRESSION_NODE), exprNode);
			makeFlowNode(exprNode);
		}
		return exprNode;
	}
}
