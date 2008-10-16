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

import Composestar.Core.CpsRepository2.PropertyPrefix;
import Composestar.Core.CpsRepository2.FilterElements.BinaryMEOperator;
import Composestar.Core.CpsRepository2.FilterElements.CanonAssignment;
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

/**
 * Builds the AST graph to be used by Groove to create the FlowModel. This class
 * creates the AST graph for the canonical filter notation.
 * 
 * @author Michiel Hendriks
 */
public class GrooveASTBuilderCN
{
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
	 * Create a compare statement node
	 * 
	 * @param expr
	 * @return
	 * @throws IllegalStateException
	 */
	private AnnotatedNode createCompareStatementNode(MECompareStatement cmp) throws IllegalStateException
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

		// TODO lhs
		// TODO rhs

		return cmpNode;
	}

	protected AnnotatedNode createAssignmentNode(CanonAssignment asgn)
	{
		// TODO Auto-generated method stub
		return null;
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
		if (asgns.size() == 0)
		{
			// add a dummy assignment node
			AnnotatedNode dummy = new AnnotatedNode();
			graph.addNode(dummy);

			edge = new AnnotatedEdge(elmNode, createLabel(ASSIGNMENT_EDGE), dummy);
			graph.addEdge(edge);
		}
		else
		{
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
			}
		}

		return null;
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

		// TODO: actions

		return filterNode;
	}

	/**
	 * @return The graph for the filter module
	 * @throws IllegalStateException TODO
	 */
	protected void createFMGraph() throws IllegalStateException
	{
		AnnotatedNode fmNode = new AnnotatedNode();
		fmNode.addAnnotation(ANNOT_REPOSITORY_ENTITY, filterModule);
		graph.addNode(fmNode);

		AnnotatedEdge edge = new AnnotatedEdge(fmNode, createLabel(FlowNode.FILTER_MODULE_NODE), fmNode);
		graph.addEdge(edge);

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

		// exit node
		AnnotatedNode exitNode = new AnnotatedNode();
		exitNode.addAnnotation(ANNOT_REPOSITORY_ENTITY, filterModule);
		graph.addNode(exitNode);

		edge = new AnnotatedEdge(exitNode, createLabel(FlowNode.EXIT_NODE), exitNode);
		graph.addEdge(edge);

		// return node
		AnnotatedNode returnNode = new AnnotatedNode();
		returnNode.addAnnotation(ANNOT_REPOSITORY_ENTITY, filterModule);
		graph.addNode(returnNode);

		edge = new AnnotatedEdge(returnNode, createLabel(FlowNode.RETURN_NODE), returnNode);
		graph.addEdge(edge);

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
		}
		return exprNode;
	}
}
