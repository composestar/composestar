/*
 * Represents a Predicate Selector element in the composestar tree.
 * 
 * Connects the selector to the prolog engine; i.e. this class also contains the
 * code that interprets the predicate and returns a set of Language Units
 * 
 * Created on Nov 3, 2004 by wilke
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SimpleSelectorDef;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class PredicateSelector extends SimpleSelExpression
{
	private static final long serialVersionUID = 5921852132415178944L;

	String outputVar;

	String query;

	Vector selectedUnitRefs; // contains references to selected units

	HashSet selectedUnits; // Contains set of selected language units

	HashMap tymInfo; // contains TYM information extracted while executing

	// query

	HashSet annotations; // contains the names of annotations extracted while

	// executing query

	public PredicateSelector(String inOutputVar, String inQuery)
	{
		super();
		outputVar = inOutputVar;
		query = inQuery;
		selectedUnitRefs = new Vector();
		selectedUnits = new HashSet();
		tymInfo = new HashMap();
		annotations = new HashSet();
	}

	public PredicateSelector()
	{
		this(null, null);
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.
	 * SimpleSelectorDef.SimpleSelExpression#interpret()
	 */
	public Vector interpret()
	{
		return selectedUnitRefs;
	}

	/**
	 * @return Returns the query.
	 */
	public String getQuery()
	{
		return query;
	}

	/**
	 * @param predicate The query to set.
	 * @param inQuery
	 */
	public void setQuery(String inQuery)
	{
		query = inQuery;
	}

	/**
	 * @return Returns the outputVar.
	 */
	public String getOutputVar()
	{
		return outputVar;
	}

	/**
	 * @param inOutputVar The outputVar to set.
	 */
	public void setOutputVar(String inOutputVar)
	{
		outputVar = inOutputVar;
	}

	/**
	 * @return set of selected program elements (the real units, not refs). Only
	 *         valid after 'run' has been called to execute the actual selector
	 *         predicate.
	 */
	public Set getSelectedUnits()
	{
		return selectedUnits;
	}

	public void setSelectedUnits(HashSet value)
	{
		selectedUnits = value;
	}

	public void setSelectedUnitRefs(Vector value)
	{
		selectedUnitRefs = value;
	}

	public HashMap getTymInfo()
	{
		return this.tymInfo;
	}

	public HashSet getAnnotations()
	{
		return this.annotations;
	}

	public void addAnnotations(String value)
	{
		annotations.add(value);
	}

	// /**
	// * @param key - FullName of a class
	// * @param obj - Type information represented FieldNode,MethodNode or Path
	// * @param method
	// */
	// public void addTYMInfo(String key, MethodNode method)
	// {
	//
	// HashMap list = new HashMap();
	// if (this.tymInfo.containsKey(key))
	// {
	// list = (HashMap) tymInfo.get(key);
	// }
	//
	// list.put(method.getReference(), method);
	// this.tymInfo.put(key, list);
	// }

}
