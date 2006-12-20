/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.DIGGER.Graph;

import java.util.Iterator;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElement;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPattern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.SubstitutionPart;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.DeclaredObjectReference;
import Composestar.Core.DIGGER.DIGGER;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.RepositoryImplementation.TypedDeclaration;
import Composestar.Utils.Debug;

/**
 * Base for all filter nodes
 * 
 * @author Michiel Hendriks
 */
public abstract class FilterNode extends Node
{
	/**
	 * @deprecated
	 */
	protected FilterElementNode lastElement;

	protected Filter filter;

	/**
	 * The direction of this filter (input\output)
	 */
	protected byte direction = -1;

	public FilterNode(Graph inGraph, Filter inFilter, byte inDirection)
	{
		super(inGraph, inFilter.getQualifiedName());
		filter = inFilter;
		direction = inDirection;
	}

	public void setFilterDirection(byte inDirection) throws ModuleException
	{
		direction = inDirection;
	}

	/**
	 * Return the direction of this filter (input-/outputfilter)
	 * 
	 * @return
	 */
	public byte getFilterDirection()
	{
		return direction;
	}

	/**
	 * Return the filter associated with this node
	 * 
	 * @return
	 */
	public Filter getFilter()
	{
		return filter;
	}

	/**
	 * Quick access to the filter type (as a string)
	 * 
	 * @return
	 */
	public String getType()
	{
		return filter.getFilterType().getType();
	}

	/**
	 * Add a filter element node to this filter node
	 * 
	 * @param inElement
	 * @param inCondition
	 * @deprecated
	 */
	public void appendFilterElement(FilterElementNode inElement, ConditionExpression inCondition)
	{
		inElement.setOwner(this);
		// TODO: link previous FE to this one
		addOutgoingEdge(new ConditionalEdge(inElement, inCondition));
		lastElement = inElement;
	}

	/**
	 * Add a filter element node to this filter node
	 * 
	 * @param inElement
	 * @param inCondition
	 */
	public void appendFilterElement(FilterElementNode inElement, FilterElement fe)
	{
		inElement.setOwner(this);
		addOutgoingEdge(new CondMatchEdge(inElement, fe));
		lastElement = inElement;
	}

	/**
	 * Dig through the filter elements and create the proper nodes
	 * 
	 * @throws ModuleException
	 */
	public void processElements() throws ModuleException
	{
		// go through all elements
		// each elements in another node in the graph
		Iterator filterElements = filter.getFilterElementIterator();
		while (filterElements.hasNext())
		{
			FilterElement elm = (FilterElement) filterElements.next();
			FilterElementNode elmNode = new FilterElementNode(graph, elm);
			// appendFilterElement(elmNode, elm.getConditionPart());
			// processMatchingPatterns(elm, elmNode);

			appendFilterElement(elmNode, elm);
			processSubstitutionParts(elm.getMatchingPattern(), elmNode);
		}
	}

	/**
	 * Dig through the matching patterns
	 * 
	 * @param elm
	 * @param elmNode
	 * @throws ModuleException
	 * @deprecated
	 */
	protected void processMatchingPatterns(FilterElement elm, FilterElementNode elmNode) throws ModuleException
	{
	// go through all patterns.
	// a pattern can have multiple outgoing edges in case of a
	// messagelist
	/*
	 * Iterator matchingPatterns = elm.getMatchingPatternIterator(); while
	 * (matchingPatterns.hasNext()) { MatchingPattern mp = (MatchingPattern)
	 * matchingPatterns.next(); MatchingPatternNode mpNode = new
	 * MatchingPatternNode(graph, mp); elmNode.appendPattern(mpNode,
	 * mp.getMatchingParts()); processSubstitutionParts(mp, mpNode); }
	 */
	}

	/**
	 * Dig through the substituion parts
	 * 
	 * @param mp
	 * @param mpNode
	 * @throws ModuleException
	 * @deprecated
	 */
	protected void processSubstitutionParts(MatchingPattern mp, MatchingPatternNode mpNode) throws ModuleException
	{
		// resolve all substitution targets and add an edge to the
		// target concern
		Iterator substParts = mp.getSubstitutionPartsIterator();
		while (substParts.hasNext())
		{
			SubstitutionPart subst = (SubstitutionPart) substParts.next();
			Node targetNode = resolveTarget(subst.getTarget());
			if (targetNode != null)
			{
				mpNode.addOutgoingEdge(new SubstitutionEdge(targetNode, subst));
			}
		}
	}

	/**
	 * Dig through the substituion parts
	 * 
	 * @param mp
	 * @param mpNode
	 * @throws ModuleException
	 */
	protected void processSubstitutionParts(MatchingPattern mp, FilterElementNode mpNode) throws ModuleException
	{
		// resolve all substitution targets and add an edge to the
		// target concern
		Iterator substParts = mp.getSubstitutionPartsIterator();
		while (substParts.hasNext())
		{
			SubstitutionPart subst = (SubstitutionPart) substParts.next();
			Node targetNode = resolveTarget(subst.getTarget());
			if (targetNode != null)
			{
				mpNode.addOutgoingEdge(new SubstitutionEdge(targetNode, subst));
			}
			else
			{
				Debug.out(Debug.MODE_WARNING, DIGGER.MODULE_NAME, "Unknown substitution target: " + subst.getTarget());
			}
		}
	}

	/**
	 * Resolve the target node
	 * 
	 * @param inTarget
	 * @return
	 */
	public Node resolveTarget(Target inTarget) throws ModuleException
	{
		String targetString = inTarget.getName();
		if ("*".equals(targetString))
		{
			if (direction == FilterChainNode.INPUT)
			{
				// send to itself
				return getOwningConcern();
			}
			else if (direction == FilterChainNode.OUTPUT)
			{
				// TODO: unresolved target; need data from concern internals
				return null;
			}
		}
		else if ("inner".equals(targetString))
		{
			if (direction == FilterChainNode.INPUT)
			{
				// return the special "inner" node
				return getOwningConcern().getInnerNode();
			}
			else if (direction == FilterChainNode.OUTPUT)
			{
				// like *+INPUT
				return getOwningConcern();
			}
		}
		else
		{
			// uses an internal/external, look it up in the filter module
			DeclaredObjectReference ref = (DeclaredObjectReference) inTarget.getRef();
			if ((ref != null) && ref.getResolved())
			{
				TypedDeclaration typeDecl = ref.getRef();
				ConcernReference concernRef = typeDecl.getType();
				return graph.getConcernNode(concernRef.getRef());
			}
			else
			{
				throw new ModuleException("Unresolved internal/external: " + targetString, DIGGER.MODULE_NAME);
			}
		}
		throw new ModuleException("Unresolved target: " + targetString, DIGGER.MODULE_NAME);
	}

	/**
	 * Return the ConcernNode this filter belongs to
	 * 
	 * @return
	 */
	public ConcernNode getOwningConcern()
	{
		// owners: FilterChainNode -> ConcernNode
		return (ConcernNode) getOwner().getOwner();
	}
}
