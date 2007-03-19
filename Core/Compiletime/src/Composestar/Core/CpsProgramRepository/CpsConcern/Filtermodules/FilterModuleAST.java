/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

import java.util.Iterator;
import java.util.Vector;

import Composestar.Core.RepositoryImplementation.DeclaredRepositoryEntity;
import Composestar.Utils.CPSIterator;

/**
 * 
 */
public class FilterModuleAST extends DeclaredRepositoryEntity
{

	private static final long serialVersionUID = -1367274558403122165L;

	public Vector parameters;

	public Vector conditions;

	public Vector internals;

	public Vector externals;

	public Vector methods;

	public Vector inputFilters;

	public Vector outputFilters;

	public FilterModuleAST()
	{
		super();
		parameters = new Vector();
		conditions = new Vector();
		internals = new Vector();
		externals = new Vector();
		methods = new Vector();
		inputFilters = new Vector();
		outputFilters = new Vector();
	}

	/**
	 * conditions
	 * 
	 * @param condition
	 * @return boolean
	 */
	public boolean addCondition(Condition condition)
	{
		conditions.addElement(condition);
		return true;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition
	 */
	public Condition removeCondition(int index)
	{
		Object o = conditions.elementAt(index);
		conditions.removeElementAt(index);
		return (Condition) o;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition
	 */
	public Condition getCondition(int index)
	{
		return (Condition) conditions.elementAt(index);
	}

	/**
	 * @return java.util.Iterator
	 */
	public Iterator getConditionIterator()
	{
		return new CPSIterator(conditions);
	}

	/**
	 * internals
	 * 
	 * @param internal
	 * @return boolean
	 */
	public boolean addInternal(InternalAST internal)
	{
		if (isIdentifierUnique(internal.getName()))
		{
			internals.addElement(internal);
			return true;
		}
		return false;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal
	 */
	public InternalAST removeInternal(int index)
	{
		Object o = internals.elementAt(index);
		internals.removeElementAt(index);
		return (InternalAST) o;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal
	 */
	public InternalAST getInternal(int index)
	{
		return (InternalAST) internals.elementAt(index);
	}

	/**
	 * @return java.util.Iterator
	 */
	public Iterator getInternalIterator()
	{
		return new CPSIterator(internals);
	}

	/**
	 * externals
	 * 
	 * @param external
	 * @return boolean
	 */
	public boolean addExternal(External external)
	{
		if (isIdentifierUnique(external.getName()))
		{
			externals.addElement(external);
			return true;
		}
		return false;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.External
	 */
	public External removeExternal(int index)
	{
		Object o = externals.elementAt(index);
		externals.removeElementAt(index);
		return (External) o;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.External
	 */
	public External getExternal(int index)
	{
		return (External) externals.elementAt(index);
	}

	/**
	 * @return java.util.Iterator
	 */
	public Iterator getExternalIterator()
	{
		return new CPSIterator(externals);
	}

	/**
	 * methods
	 * 
	 * @param method
	 * @return boolean
	 */
	public boolean addMethod(Method method)
	{
		methods.addElement(method);
		return true;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Method
	 */
	public Method removeMethod(int index)
	{
		Object o = methods.elementAt(index);
		methods.removeElementAt(index);
		return (Method) o;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Method
	 */
	public Method getMethod(int index)
	{
		return (Method) methods.elementAt(index);
	}

	/**
	 * @return java.util.Iterator
	 */
	public Iterator getMethodIterator()
	{
		return new CPSIterator(methods);
	}

	/**
	 * inputfilters
	 * 
	 * @param inputfilter
	 * @return boolean
	 */
	public boolean addInputFilter(FilterAST inputfilter)
	{
		if (isIdentifierUnique(inputfilter.getName()))
		{
			inputFilters.addElement(inputfilter);
			return true;
		}
		return false;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter
	 */
	public Filter removeInputFilter(int index)
	{
		Object o = inputFilters.elementAt(index);
		inputFilters.removeElementAt(index);
		return (Filter) o;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter
	 */
	public Filter getInputFilter(int index)
	{
		return (Filter) inputFilters.elementAt(index);
	}

	/**
	 * @return java.util.Iterator
	 */
	public Iterator getInputFilterIterator()
	{
		return new CPSIterator(inputFilters);
	}

	/**
	 * outputfilters
	 * 
	 * @param outputfilter
	 * @return boolean
	 */
	public boolean addOutputFilter(FilterAST outputfilter)
	{
		if (isIdentifierUnique(outputfilter.getName()))
		{
			outputFilters.addElement(outputfilter);
			return true;
		}
		return false;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter
	 */
	public Filter removeOutputFilter(int index)
	{
		Object o = outputFilters.elementAt(index);
		outputFilters.removeElementAt(index);
		return (Filter) o;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter
	 */
	public FilterAST getOutputFilter(int index)
	{
		return (FilterAST) outputFilters.elementAt(index);
	}

	/**
	 * @return java.util.Iterator
	 */
	public Iterator getOutputFilterIterator()
	{
		return new CPSIterator(outputFilters);
	}

	/*
	 * Returns true when this identifier has not been used yet, within this
	 * filtermodule.
	 */

	public boolean isIdentifierUnique(String identifier)
	{ // Not sure if methods and conditions should also be included here? - WH
		Vector[] allIdentifiers = { internals, externals, inputFilters, outputFilters };

		for (int i = 0; i < allIdentifiers.length; i++)
		{
			for (int j = 0; j < allIdentifiers[i].size(); j++)
			{
				if (((DeclaredRepositoryEntity) allIdentifiers[i].elementAt(j)).getName().equals(identifier))
				{
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @ return boolean Checks whether a parameter does already exists. Fixme:
	 *   no difference yet between ?foo and ??foo
	 * @param fmp
	 */
	public boolean doesParameterExists(FilterModuleParameterAST fmp)
	{
		boolean exist = false;
		for (int i = 0; i < parameters.size(); i++)
		{
			FilterModuleParameterAST temp = (FilterModuleParameterAST) parameters.elementAt(i);
			if (temp.getName().equals(fmp.getName()))
			{
				exist = true;
			}
		}
		return exist;
	}

	/**
	 * Adds a parameter to a filtermodule. pre requirment is to call boolean
	 * doesParameterExists(FilterModuleParameter fmp) please note that this
	 * construction is different to the other boolean addWhatever
	 * 
	 * @param fmp
	 */
	public void addParameter(FilterModuleParameterAST fmp)
	{
		parameters.add(fmp);
	}

	/**
	 * @return java.util.Iterator
	 */
	public Iterator getParameterIterator()
	{
		return new CPSIterator(parameters);
	}

	public Vector getOutputFilters()
	{
		return outputFilters;
	}
}

/**
 * void FilterModule.accept(RepositoryVisitor){ visitor.visit(this); }
 */
