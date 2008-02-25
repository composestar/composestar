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

import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.RepositoryImplementation.DeclaredRepositoryEntity;
import Composestar.Utils.CPSIterator;

public class FilterModule extends DeclaredRepositoryEntity
{
	private static final long serialVersionUID = 4898318100147611976L;

	public FilterModuleAST fmAst;

	public Vector parameters;

	// public Vector conditions;
	public Vector internals; // instances

	// public Vector externals;
	// public Vector methods;
	public Vector inputFilters;

	public Vector outputFilters;

	/**
	 * A unique token for the filter module name. This token is used to
	 * distinguish filter module instances (for example with filter module
	 * parameters or conditional superimposition)
	 */
	public String uniqueToken;

	/**
	 * @deprecated
	 */
	public FilterModule()
	{
		super();
		fmAst = new FilterModuleAST();
		parameters = new Vector();
		// conditions = new Vector();
		internals = new Vector();
		// externals = new Vector();
		// methods = new Vector();
		inputFilters = new Vector();
		outputFilters = new Vector();
	}

	public FilterModule(FilterModuleAST inFmAst, Vector args, String id)
	{
		super();
		uniqueToken = id;
		fmAst = inFmAst;
		descriptionFileName = fmAst.getDescriptionFileName();
		descriptionLineNumber = fmAst.getDescriptionLineNumber();
		setParent(fmAst.getParent());
		setName(fmAst.getName());
		int counter = 1;
		DataStore ds = DataStore.instance();

		((CpsConcern) getParent()).addFilterModule(this);

		// set the unique Vectors;
		parameters = new Vector();
		internals = new Vector();
		inputFilters = new Vector();
		outputFilters = new Vector();

		// create the FilterModuleParameter (instances)
		Iterator parameterIterator = fmAst.getParameterIterator();
		int index = 0;
		while (parameterIterator.hasNext())
		{
			FilterModuleParameterAST fmpAst = (FilterModuleParameterAST) parameterIterator.next();
			// we take the assumption that #parameters == # arguments
			FilterModuleParameter fmp = new FilterModuleParameter(fmpAst, (FilterModuleParameter) args.get(index),
					counter);
			fmp.setParent(this);
			addParameter(fmp);
			index++;
			counter++;
		}

		// create unique internals
		Iterator internalIt = fmAst.getInternalIterator();
		while (internalIt.hasNext())
		{
			InternalAST internalAST = (InternalAST) internalIt.next();
			// parameterized internal
			if (internalAST.getType() == null)
			{
				ParameterizedInternal internal = new ParameterizedInternal((ParameterizedInternalAST) internalAST);
				// counter++;
				internal.setParent(this);
				addInternal(internal);
			}
			else
			{
				Internal internal = new Internal(internalAST);
				// counter++;
				internal.setParent(this);
				internal.setType(internalAST.getType());
				addInternal(internal);
			}
		}

		Iterator filterIt = fmAst.getInputFilterIterator();
		while (filterIt.hasNext())
		{
			Filter filter = new Filter((FilterAST) filterIt.next());
			addInputFilter(filter);
			filter.setParent(this);
			ds.addObject(filter);
		}

		filterIt = fmAst.getOutputFilterIterator();
		while (filterIt.hasNext())
		{
			Filter filter = new Filter((FilterAST) filterIt.next());
			addOutputFilter(filter);
			filter.setParent(this);
			ds.addObject(filter);
		}
	}

	public FilterModuleParameter getParameter(String parameterName)
	{
		Iterator it = getParameterIterator();
		FilterModuleParameter result = null;
		while (it.hasNext())
		{
			FilterModuleParameter fmp = (FilterModuleParameter) it.next();
			if (fmp.getName().equals(parameterName))
			{
				result = fmp;
			}
		}
		return result;
	}

	/**
	 * internals
	 * 
	 * @param internal
	 * @return boolean
	 */
	public boolean addInternal(Internal internal)
	{
		internals.addElement(internal);
		return true;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal
	 */
	public Internal removeInternal(int index)
	{
		Object o = internals.elementAt(index);
		internals.removeElementAt(index);
		return (Internal) o;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal
	 */
	public Internal getInternal(int index)
	{
		return (Internal) internals.elementAt(index);
	}

	/**
	 * @return java.util.Iterator
	 */
	public Iterator getInternalIterator()
	{
		return new CPSIterator(internals);
	}

	/**
	 * @ return boolean Checks whether a parameter does already exists. Fixme:
	 *   no difference yet between ?foo and ??foo
	 * @param fmp
	 */
	public boolean doesParameterExists(FilterModuleParameter fmp)
	{
		boolean exist = false;
		for (int i = 0; i < parameters.size(); i++)
		{
			FilterModuleParameter temp = (FilterModuleParameter) parameters.elementAt(i);
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
	public void addParameter(FilterModuleParameter fmp)
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

	/*
	 * Forwarded functions
	 */
	/**
	 * conditions
	 * 
	 * @param condition
	 * @return boolean
	 */
	public boolean addCondition(Condition condition)
	{
		return fmAst.addCondition(condition);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition
	 */
	public Condition removeCondition(int index)
	{
		return fmAst.removeCondition(index);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition
	 */
	public Condition getCondition(int index)
	{
		return fmAst.getCondition(index);
	}

	/**
	 * @return java.util.Iterator
	 */
	public Iterator getConditionIterator()
	{
		return fmAst.getConditionIterator();
	}

	/**
	 * internals
	 * 
	 * @param internal
	 * @return boolean
	 */
	public boolean addInternalAST(InternalAST internal)
	{
		return fmAst.addInternal(internal);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal
	 */
	public InternalAST removeInternalAST(int index)
	{
		return fmAst.removeInternal(index);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal
	 */
	public InternalAST getInternalAST(int index)
	{
		return fmAst.getInternal(index);
	}

	/**
	 * @return java.util.Iterator
	 */
	public Iterator getInternalASTIterator()
	{
		return fmAst.getInternalIterator();
	}

	/**
	 * externals
	 * 
	 * @param external
	 * @return boolean
	 */
	public boolean addExternal(External external)
	{
		return fmAst.addExternal(external);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.External
	 */
	public External removeExternal(int index)
	{
		return fmAst.removeExternal(index);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.External
	 */
	public External getExternal(int index)
	{
		return fmAst.getExternal(index);
	}

	/**
	 * @return java.util.Iterator
	 */
	public Iterator getExternalIterator()
	{
		return fmAst.getExternalIterator();
	}

	/**
	 * methods
	 * 
	 * @param method
	 * @return boolean
	 * @deprecated
	 */
	public boolean addMethod(Method method)
	{
		return fmAst.addMethod(method);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Method
	 * @deprecated
	 */
	public Method removeMethod(int index)
	{
		return fmAst.removeMethod(index);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Method
	 * @deprecated
	 */
	public Method getMethod(int index)
	{
		return fmAst.getMethod(index);
	}

	/**
	 * @return java.util.Iterator
	 * @deprecated
	 */
	public Iterator getMethodIterator()
	{
		return fmAst.getMethodIterator();
	}

	/**
	 * inputfilters
	 * 
	 * @param inputfilter
	 * @return boolean
	 */
	public boolean addInputFilter(Filter inputfilter)
	{
		inputFilters.add(inputfilter);
		return true;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter
	 */
	public Filter removeInputFilter(int index)
	{
		return (Filter) inputFilters.remove(index);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter
	 */
	public Filter getInputFilter(int index)
	{
		return (Filter) inputFilters.get(index);
	}

	/**
	 * @return java.util.Iterator
	 */
	public Iterator getInputFilterIterator()
	{
		return inputFilters.iterator();
	}

	/**
	 * outputfilters
	 * 
	 * @param outputfilter
	 * @return boolean
	 */
	public boolean addOutputFilter(Filter outputfilter)
	{
		outputFilters.add(outputfilter);
		return true;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter
	 */
	public Filter removeOutputFilter(int index)
	{
		return (Filter) outputFilters.remove(index);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter
	 */
	public Filter getOutputFilter(int index)
	{
		return (Filter) outputFilters.get(index);
	}

	/**
	 * @return java.util.Iterator
	 */
	public Iterator getOutputFilterIterator()
	{
		return outputFilters.iterator();
	}

	/*
	 * Returns true when this identifier has not been used yet, within this
	 * filtermodule.
	 */

	/*
	 * private boolean isIdentifierUnique(String identifier) { // Not sure if
	 * methods and conditions should also be included here? - WH Vector
	 * allIdentifiers[] = {internals, externals, inputFilters, outputFilters };
	 * for (int i = 0; i < allIdentifiers.length; i++) { for (int j = 0; j <
	 * allIdentifiers[i].size(); j++) if
	 * (((DeclaredRepositoryEntity)allIdentifiers[i].elementAt(j)).getName().equals(identifier))
	 * return false; } return true; }
	 */

	/**
	 * @return boolean Checks whether a parameter does already exists. Fixme: no
	 *         difference yet between ?foo and ??foo
	 * @param fmp
	 */
	public boolean parameterASTExists(FilterModuleParameterAST fmp)
	{
		return fmAst.parameterExists(fmp);
	}

	/**
	 * Adds a parameter to a filtermodule. pre requirment is to call boolean
	 * doesParameterExists(FilterModuleParameter fmp) please note that this
	 * construction is different to the other boolean addWhatever
	 * 
	 * @param fmp
	 */
	public void addParameterAST(FilterModuleParameterAST fmp)
	{
		fmAst.addParameter(fmp);
	}

	/**
	 * @return java.util.Iterator
	 */
	public Iterator getParameterASTIterator()
	{
		return fmAst.getParameterIterator();
	}

	public Vector getOutputFilters()
	{
		return outputFilters;
	}

	public int getDescriptionLineNumber()
	{
		return fmAst.getDescriptionLineNumber();
	}

	public void setDescriptionLineNumber(int newLineNumber)
	{
		fmAst.setDescriptionLineNumber(newLineNumber);
	}

	public String getDescriptionFileName()
	{
		return fmAst.getDescriptionFileName();
	}

	public void setDescriptionFileName(String newFileName)
	{
		fmAst.setDescriptionFileName(newFileName);
	}

	/*
	 * public void addDynObject(String key, Object obj) {
	 * fm_ast.addDynObject(key, obj); } public Object getDynObject(String key) {
	 * return fm_ast.getDynObject(key); } public Iterator getDynIterator() {
	 * return fm_ast.getDynIterator(); }
	 */

	public FilterModuleAST getFmAst()
	{
		return fmAst;
	}

	public String getUniqueToken()
	{
		return uniqueToken;
	}

	public void setUniqueToken(String inUniqueToken)
	{
		uniqueToken = inUniqueToken;
	}

	public String getName()
	{
		return name + '!' + uniqueToken;
	}

	public String getOriginalName()
	{
		return name;
	}

	/**
	 * Returns the Qualified name without the uniqueToken, this is the real
	 * qualified name as it's found in the source code.
	 * 
	 * @return
	 */
	public String getOriginalQualifiedName()
	{
		StringBuffer sb = new StringBuffer();
		DeclaredRepositoryEntity p = (DeclaredRepositoryEntity) getParent();
		if (p != null)
		{
			sb.append(p.getQualifiedName());
			sb.append(".");
		}
		sb.append(name);
		return sb.toString();
	}

	public String asSourceCode()
	{
		return fmAst.asSourceCode();
	}
}
