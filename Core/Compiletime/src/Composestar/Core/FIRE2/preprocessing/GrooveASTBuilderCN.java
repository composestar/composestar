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
import groove.graph.Label;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import Composestar.Core.CpsRepository2.PropertyNames;
import Composestar.Core.CpsRepository2.PropertyPrefix;
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
	 * A key for the annotations in {@link AnnotatedEdge} and
	 * {@link AnnotatedNode} to the repository entity related to the edge or
	 * node.
	 */
	public static final String ANNOT_REPOSITORY_ENTITY = "RepositoryEntity";

	public static final String ASSIGNMENT_EDGE = "assignment";

	/**
	 * Used for compare and assignment values who's value is deferred (i.e.
	 * message property)
	 */
	public static final String DEFERRED_EDGE = "deferred";

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

	protected Map<String, AnnotatedNode> cachedValueNodes;

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
	public static Graph createAST(FilterModule fm, FilterDirection dir) throws NullPointerException,
			IllegalStateException
	{
		GrooveASTBuilderCN builder = new GrooveASTBuilderCN(fm, dir);
		builder.createFMGraph();
		return builder.getGraph();
	}

	public static final Label createLabel(String labelStr)
	{
		return DefaultLabel.createLabel(labelStr);
	}

	/**
	 * Create a graph builder for the given filter module and filter direction
	 * 
	 * @param fm
	 * @param dir
	 */
	protected GrooveASTBuilderCN(FilterModule fm, FilterDirection dir)
	{
		cachedValueNodes = new HashMap<String, AnnotatedNode>();
		if (fm == null)
		{
			throw new NullPointerException("Filter module can not be null");
		}
		fex = null;
		switch (dir)
		{
			case Output:
				fex = fm.getOutputFilterExpression();
				break;
			case Input:
			default:
				fex = fm.getInputFilterExpression();
				break;
		}
		graph = new DefaultGraph();
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
	protected void makeFlowNode(AnnotatedNode node)
	{
		AnnotatedEdge edge = new AnnotatedEdge(node, createLabel(FlowNode.FLOW_NODE), node);
		graph.addEdge(edge);
	}

	/**
	 * Create a compare statement node
	 * 
	 * @param expr
	 * @return
	 * @throws IllegalStateException
	 */
	protected AnnotatedNode createCompareStatementNode(MECompareStatement cmp) throws IllegalStateException
	{
		AnnotatedNode cmpNode = new AnnotatedNode();
		cmpNode.addAnnotation(ANNOT_REPOSITORY_ENTITY, cmp);
		graph.addNode(cmpNode);

		AnnotatedEdge edge = new AnnotatedEdge(cmpNode, createLabel(FlowNode.COMPARE_STATEMENT_NODE), cmpNode);
		graph.addEdge(edge);

		if (cmp instanceof InstanceMatching)
		{
			edge = new AnnotatedEdge(cmpNode, createLabel(FlowNode.INSTANCE_MATCHING), cmpNode);
			graph.addEdge(edge);
		}
		else if (cmp instanceof SignatureMatching)
		{
			edge = new AnnotatedEdge(cmpNode, createLabel(FlowNode.SIGNATURE_MATCHING), cmpNode);
			graph.addEdge(edge);
		}
		else if (cmp instanceof CompatibilityMatching)
		{
			edge = new AnnotatedEdge(cmpNode, createLabel(FlowNode.COMPATIBILITY_MATCHING), cmpNode);
			graph.addEdge(edge);
		}
		else if (cmp instanceof AnnotationMatching)
		{
			edge = new AnnotatedEdge(cmpNode, createLabel(FlowNode.ANNOTATION_MATCHING), cmpNode);
			graph.addEdge(edge);
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

		AnnotatedNode node = new AnnotatedNode();
		node.addAnnotation(ANNOT_REPOSITORY_ENTITY, cmp.getLHS());
		graph.addNode(node);

		// TODO: how to handle checking for non-instance/non-selector
		// properties? (i.e. message.bla = this.is.my.class)
		edge = new AnnotatedEdge(node, createLabel(cmp.getLHS().getBaseName()), node);
		graph.addEdge(edge);
		edge = new AnnotatedEdge(cmpNode, createLabel(LHS_EDGE), node);
		graph.addEdge(edge);

		// TODO: how to handle type conversion in case of selectors, in most
		// cases selectors are used as literals, but they could also be method
		// references
		for (CpsVariable var : cmp.getRHS())
		{
			node = createCpsVariableNode(var);
			edge = new AnnotatedEdge(cmpNode, createLabel(RHS_EDGE), node);
			// because the node does not always link to this particular variable
			edge.addAnnotation(ANNOT_REPOSITORY_ENTITY, var);
			graph.addEdge(edge);
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
	 * @return
	 * @throws IllegalStateException Thrown when an unknown CpsVariable was
	 *             encountered
	 */
	protected AnnotatedNode createCpsVariableNode(CpsVariable var) throws IllegalStateException
	{
		String valueEdge = null;
		// note: the used prefixes are only there for convenience
		if (var instanceof CpsSelector)
		{
			valueEdge = VP_SELECTOR + ((CpsSelector) var).getName();
		}
		else if (var instanceof CanonProperty)
		{
			CanonProperty prop = (CanonProperty) var;
			if (prop.getPrefix() == PropertyPrefix.NONE && PropertyNames.INNER.equals(prop.getBaseName()))
			{
				valueEdge = VP_OBJECT + PropertyNames.INNER.toString();
			}
			else if (prop.getPrefix() == PropertyPrefix.MESSAGE)
			{
				// always return unique nodes for deferred values
				AnnotatedNode node = new AnnotatedNode();
				node.addAnnotation(ANNOT_REPOSITORY_ENTITY, var);
				graph.addNode(node);

				AnnotatedNode deferredNode = new AnnotatedNode();
				deferredNode.addAnnotation(ANNOT_REPOSITORY_ENTITY, var);
				graph.addNode(deferredNode);

				AnnotatedEdge edge = new AnnotatedEdge(node, createLabel(DEFERRED_EDGE), deferredNode);
				graph.addEdge(edge);

				edge = new AnnotatedEdge(deferredNode, createLabel(prop.getBaseName()), deferredNode);
				graph.addEdge(edge);

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
		}
		else if (var instanceof CpsLiteral)
		{
			// this could result in very large labels
			valueEdge = VP_LITERAL + ((CpsLiteral) var).getLiteralValue();
		}
		else if (var instanceof CpsProgramElement)
		{
			// different program elements should produce different hashes...
			valueEdge = VP_PROGRAM_ELEMENT + System.identityHashCode(((CpsProgramElement) var).getProgramElement());
		}
		else
		{
			throw new IllegalStateException(String.format("Unknown CpsVariable type: %s", var.getClass().getName()));
		}

		if (cachedValueNodes.containsKey(valueEdge))
		{
			return cachedValueNodes.get(valueEdge);
		}

		AnnotatedNode node = new AnnotatedNode();
		node.addAnnotation(ANNOT_REPOSITORY_ENTITY, var);
		graph.addNode(node);

		AnnotatedEdge edge = new AnnotatedEdge(node, createLabel(valueEdge), node);
		graph.addEdge(edge);

		cachedValueNodes.put(valueEdge, node);

		return node;
	}

	/**
	 * @param asgn
	 * @return
	 * @throws IllegalStateException
	 */
	protected AnnotatedNode createAssignmentNode(CanonAssignment asgn) throws IllegalStateException
	{
		AnnotatedNode cmpNode = new AnnotatedNode();
		cmpNode.addAnnotation(ANNOT_REPOSITORY_ENTITY, asgn);
		graph.addNode(cmpNode);

		AnnotatedEdge edge = new AnnotatedEdge(cmpNode, createLabel(FlowNode.ASSIGNMENT_NODE), cmpNode);
		graph.addEdge(edge);

		makeFlowNode(cmpNode);

		if (PropertyPrefix.MESSAGE != asgn.getProperty().getPrefix())
		{
			logger.error(
					String.format("Assigned property is not a message property: %s", asgn.getProperty().getName()),
					asgn.getProperty());
			throw new IllegalStateException(String.format("Assigned property is not a message property: %s", asgn
					.getProperty().getName()));
		}

		AnnotatedNode node = new AnnotatedNode();
		node.addAnnotation(ANNOT_REPOSITORY_ENTITY, asgn.getProperty());
		graph.addNode(node);

		edge = new AnnotatedEdge(node, createLabel(asgn.getProperty().getBaseName()), node);
		graph.addEdge(edge);
		edge = new AnnotatedEdge(cmpNode, createLabel(LHS_EDGE), node);
		graph.addEdge(edge);

		node = createCpsVariableNode(asgn.getValue());
		edge = new AnnotatedEdge(cmpNode, createLabel(RHS_EDGE), node);
		// because the node does not always link to this particular variable
		edge.addAnnotation(ANNOT_REPOSITORY_ENTITY, asgn.getValue());
		graph.addEdge(edge);

		return cmpNode;
	}

	/**
	 * @param expr
	 * @return
	 * @throws IllegalStateException Thrown in case of an unknown filter element
	 *             expression class
	 */
	protected AnnotatedNode createFilterElementExpressionNode(FilterElementExpression expr)
			throws IllegalStateException
	{
		AnnotatedNode exprNode = null;
		if (expr instanceof FilterElement)
		{
			exprNode = createFilterElementNode((FilterElement) expr);
		}
		else if (expr instanceof CORFilterElmOper)
		{
			exprNode = new AnnotatedNode();
			exprNode.addAnnotation(ANNOT_REPOSITORY_ENTITY, expr);

			AnnotatedEdge edge = new AnnotatedEdge(exprNode, createLabel(FlowNode.COR_NODE), exprNode);
			graph.addEdge(edge);

			// the left hand side
			edge = new AnnotatedEdge(exprNode, createLabel(LHS_EDGE),
					createFilterElementExpressionNode(((CORFilterElmOper) expr).getLHS()));
			graph.addEdge(edge);

			// the right hand side
			edge = new AnnotatedEdge(exprNode, createLabel(RHS_EDGE),
					createFilterElementExpressionNode(((CORFilterElmOper) expr).getRHS()));
			graph.addEdge(edge);
		}
		else
		{
			throw new IllegalStateException(String.format("Unknown filter element expression class: %s", expr
					.getClass().getName()));
		}

		if (exprNode != null)
		{
			AnnotatedEdge edge = new AnnotatedEdge(exprNode, createLabel(FlowNode.FILTER_ELEMENT_EXPRESSION_NODE),
					exprNode);
			graph.addEdge(edge);

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
	protected AnnotatedNode createFilterElementNode(FilterElement elm)
	{
		AnnotatedNode elmNode = new AnnotatedNode();
		elmNode.addAnnotation(ANNOT_REPOSITORY_ENTITY, elm);
		graph.addNode(elmNode);

		AnnotatedEdge edge = new AnnotatedEdge(elmNode, createLabel(FlowNode.FILTER_ELEMENT_NODE), elmNode);
		graph.addEdge(edge);

		AnnotatedNode matchExprNode = createMatchingExpressionNode(elm.getMatchingExpression());
		edge = new AnnotatedEdge(elmNode, createLabel(EXPRESSION_EDGE), matchExprNode);
		graph.addEdge(edge);

		// create a linked list of assignments
		AnnotatedNode lastNode = elmNode;
		Collection<CanonAssignment> asgns = elm.getAssignments();
		int asgnsNodes = 0;
		for (CanonAssignment asgn : asgns)
		{
			if (PropertyPrefix.MESSAGE != asgn.getProperty().getPrefix())
			{
				continue;
			}
			AnnotatedNode assignNode = createAssignmentNode(asgn);
			edge = new AnnotatedEdge(lastNode, createLabel(ASSIGNMENT_EDGE), assignNode);
			graph.addEdge(edge);
			lastNode = assignNode;
			asgnsNodes++;
		}

		if (asgnsNodes == 0)
		{
			// add a dummy assignment node
			AnnotatedNode dummy = new AnnotatedNode();
			graph.addNode(dummy);

			edge = new AnnotatedEdge(dummy, createLabel(FlowNode.ASSIGNMENT_NODE), dummy);
			graph.addEdge(edge);
			makeFlowNode(dummy);

			edge = new AnnotatedEdge(elmNode, createLabel(ASSIGNMENT_EDGE), dummy);
			graph.addEdge(edge);
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
	protected AnnotatedNode createFilterExpressionNode(FilterExpression expr) throws IllegalStateException
	{
		AnnotatedNode exprNode = null;
		if (expr instanceof Filter)
		{
			exprNode = createFilterNode((Filter) expr);
		}
		else if (expr instanceof SequentialFilterOper)
		{
			exprNode = new AnnotatedNode();
			exprNode.addAnnotation(ANNOT_REPOSITORY_ENTITY, expr);

			AnnotatedEdge edge = new AnnotatedEdge(exprNode, createLabel(FlowNode.SEQUENTIAL_FILTER_OPER_NODE),
					exprNode);
			graph.addEdge(edge);

			// the left hand side
			edge = new AnnotatedEdge(exprNode, createLabel(LHS_EDGE),
					createFilterExpressionNode(((SequentialFilterOper) expr).getLHS()));
			graph.addEdge(edge);

			// the right hand side
			edge = new AnnotatedEdge(exprNode, createLabel(RHS_EDGE),
					createFilterExpressionNode(((SequentialFilterOper) expr).getRHS()));
			graph.addEdge(edge);
		}
		else
		{
			throw new IllegalStateException(String.format("Unknown filter expression class: %s", expr.getClass()
					.getName()));
		}
		if (expr != null)
		{
			AnnotatedEdge edge = new AnnotatedEdge(exprNode, createLabel(FlowNode.FILTER_EXPRESSION_NODE), exprNode);
			graph.addEdge(edge);

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
	protected AnnotatedNode createFilterNode(Filter filter) throws IllegalStateException
	{
		AnnotatedNode filterNode = new AnnotatedNode();
		filterNode.addAnnotation(ANNOT_REPOSITORY_ENTITY, filter);
		graph.addNode(filterNode);

		AnnotatedEdge edge = new AnnotatedEdge(filterNode, createLabel(FlowNode.FILTER_NODE), filterNode);
		graph.addEdge(edge);

		edge = new AnnotatedEdge(filterNode, createLabel(EXPRESSION_EDGE), createFilterElementExpressionNode(filter
				.getElementExpression()));
		graph.addEdge(edge);

		FilterType ft = filter.getType();
		if (ft instanceof PrimitiveFilterType)
		{
			AnnotatedNode node = createFilterActionNode(((PrimitiveFilterType) ft).getAcceptReturnAction());
			edge = new AnnotatedEdge(node, createLabel(FlowNode.ACCEPT_RETURN_ACTION_NODE), node);
			graph.addEdge(edge);

			edge = new AnnotatedEdge(filterNode, createLabel(ACCEPT_RETURN_EDGE), node);
			graph.addEdge(edge);

			node = createFilterActionNode(((PrimitiveFilterType) ft).getAcceptCallAction());
			edge = new AnnotatedEdge(node, createLabel(FlowNode.ACCEPT_CALL_ACTION_NODE), node);
			graph.addEdge(edge);

			edge = new AnnotatedEdge(filterNode, createLabel(ACCEPT_CALL_EDGE), node);
			graph.addEdge(edge);

			node = createFilterActionNode(((PrimitiveFilterType) ft).getRejectReturnAction());
			edge = new AnnotatedEdge(node, createLabel(FlowNode.REJECT_RETURN_ACTION_NODE), node);
			graph.addEdge(edge);

			edge = new AnnotatedEdge(filterNode, createLabel(REJECT_RETURN_EDGE), node);
			graph.addEdge(edge);

			node = createFilterActionNode(((PrimitiveFilterType) ft).getRejectCallAction());
			edge = new AnnotatedEdge(node, createLabel(FlowNode.REJECT_CALL_ACTION_NODE), node);
			graph.addEdge(edge);

			edge = new AnnotatedEdge(filterNode, createLabel(REJECT_CALL_EDGE), node);
			graph.addEdge(edge);
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
	protected AnnotatedNode createFilterActionNode(FilterAction action)
	{
		AnnotatedNode actionNode = new AnnotatedNode();
		actionNode.addAnnotation(ANNOT_REPOSITORY_ENTITY, action);
		graph.addNode(actionNode);

		AnnotatedEdge edge = new AnnotatedEdge(actionNode, createLabel(FlowNode.FILTER_ACTION_NODE), actionNode);
		graph.addEdge(edge);

		makeFlowNode(actionNode);

		switch (action.getFlowBehavior())
		{
			case EXIT:
				edge = new AnnotatedEdge(actionNode, createLabel(FlowNode.EXIT_ACTION_NODE), actionNode);
				graph.addEdge(edge);
				break;
			case RETURN:
				edge = new AnnotatedEdge(actionNode, createLabel(FlowNode.RETURN_ACTION_NODE), actionNode);
				graph.addEdge(edge);
				break;
			case CONTINUE:
			default:
				edge = new AnnotatedEdge(actionNode, createLabel(FlowNode.CONTINUE_ACTION_NODE), actionNode);
				graph.addEdge(edge);
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
		AnnotatedNode fmNode = new AnnotatedNode();
		fmNode.addAnnotation(ANNOT_REPOSITORY_ENTITY, filterModule);
		graph.addNode(fmNode);

		AnnotatedEdge edge = new AnnotatedEdge(fmNode, createLabel(FlowNode.FILTER_MODULE_NODE), fmNode);
		graph.addEdge(edge);

		makeFlowNode(fmNode);

		if (fex != null)
		{
			AnnotatedNode feNode = createFilterExpressionNode(fex);
			edge = new AnnotatedEdge(fmNode, createLabel(EXPRESSION_EDGE), feNode);
			graph.addEdge(edge);
		}

		// end node
		AnnotatedNode endNode = new AnnotatedNode();
		endNode.addAnnotation(ANNOT_REPOSITORY_ENTITY, filterModule);
		graph.addNode(endNode);

		edge = new AnnotatedEdge(endNode, createLabel(FlowNode.END_NODE), endNode);
		graph.addEdge(edge);
		makeFlowNode(endNode);

		// exit node
		AnnotatedNode exitNode = new AnnotatedNode();
		exitNode.addAnnotation(ANNOT_REPOSITORY_ENTITY, filterModule);
		graph.addNode(exitNode);

		edge = new AnnotatedEdge(exitNode, createLabel(FlowNode.EXIT_NODE), exitNode);
		graph.addEdge(edge);
		makeFlowNode(exitNode);

		// return node
		AnnotatedNode returnNode = new AnnotatedNode();
		returnNode.addAnnotation(ANNOT_REPOSITORY_ENTITY, filterModule);
		graph.addNode(returnNode);

		edge = new AnnotatedEdge(returnNode, createLabel(FlowNode.RETURN_NODE), returnNode);
		graph.addEdge(edge);
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
	protected AnnotatedNode createMatchingExpressionNode(MatchingExpression expr) throws IllegalStateException
	{
		AnnotatedNode exprNode = null;

		if (expr instanceof BinaryMEOperator)
		{
			exprNode = new AnnotatedNode();
			exprNode.addAnnotation(ANNOT_REPOSITORY_ENTITY, expr);
			graph.addNode(exprNode);

			AnnotatedEdge edge;
			if (expr instanceof AndMEOper)
			{
				edge = new AnnotatedEdge(exprNode, createLabel(FlowNode.MATCH_AND_NODE), exprNode);
				graph.addEdge(edge);
			}
			else if (expr instanceof OrMEOper)
			{
				edge = new AnnotatedEdge(exprNode, createLabel(FlowNode.MATCH_OR_NODE), exprNode);
				graph.addEdge(edge);
			}
			else
			{
				throw new IllegalStateException(String.format("Unknown matching expression class: %s", expr.getClass()
						.getName()));
			}

			// the left hand side
			edge = new AnnotatedEdge(exprNode, createLabel(LHS_EDGE),
					createMatchingExpressionNode(((BinaryMEOperator) expr).getLHS()));
			graph.addEdge(edge);

			// the right hand side
			edge = new AnnotatedEdge(exprNode, createLabel(RHS_EDGE),
					createMatchingExpressionNode(((BinaryMEOperator) expr).getRHS()));
			graph.addEdge(edge);
		}
		else if (expr instanceof UnaryMEOperator)
		{
			exprNode = new AnnotatedNode();
			exprNode.addAnnotation(ANNOT_REPOSITORY_ENTITY, expr);
			graph.addNode(exprNode);

			AnnotatedEdge edge;
			if (expr instanceof NotMEOper)
			{
				edge = new AnnotatedEdge(exprNode, createLabel(FlowNode.MATCH_NOT_NODE), exprNode);
				graph.addEdge(edge);
			}
			else
			{
				throw new IllegalStateException(String.format("Unknown matching expression class: %s", expr.getClass()
						.getName()));
			}

			// the right hand side
			edge = new AnnotatedEdge(exprNode, createLabel(OPERAND_EDGE),
					createMatchingExpressionNode(((UnaryMEOperator) expr).getOperand()));
			graph.addEdge(edge);
		}
		else if (expr instanceof MECompareStatement)
		{
			exprNode = createCompareStatementNode((MECompareStatement) expr);
		}
		else if (expr instanceof MECondition)
		{
			exprNode = new AnnotatedNode();
			exprNode.addAnnotation(ANNOT_REPOSITORY_ENTITY, expr);
			graph.addNode(exprNode);

			AnnotatedEdge edge = new AnnotatedEdge(exprNode, createLabel(FlowNode.CONDITION_NODE), exprNode);
			graph.addEdge(edge);
		}
		else if (expr instanceof MELiteral)
		{
			exprNode = new AnnotatedNode();
			exprNode.addAnnotation(ANNOT_REPOSITORY_ENTITY, expr);
			graph.addNode(exprNode);

			if (((MELiteral) expr).getLiteralValue())
			{
				AnnotatedEdge edge = new AnnotatedEdge(exprNode, createLabel(FlowNode.TRUE_NODE), exprNode);
				graph.addEdge(edge);
			}
			else
			{
				AnnotatedEdge edge = new AnnotatedEdge(exprNode, createLabel(FlowNode.FALSE_NODE), exprNode);
				graph.addEdge(edge);
			}
		}
		else
		{
			throw new IllegalStateException(String.format("Unknown matching expression class: %s", expr.getClass()
					.getName()));
		}

		if (exprNode != null)
		{
			AnnotatedEdge edge = new AnnotatedEdge(exprNode, createLabel(FlowNode.MATCHING_EXPRESSION_NODE), exprNode);
			graph.addEdge(edge);
			makeFlowNode(exprNode);
		}
		return exprNode;
	}
}
