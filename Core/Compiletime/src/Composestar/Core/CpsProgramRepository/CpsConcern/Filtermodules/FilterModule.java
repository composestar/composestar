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

/**
 * @modelguid {A1F21307-81AA-41BE-8D4C-79216E1F0EC6}
 */
public class FilterModule extends DeclaredRepositoryEntity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4898318100147611976L;

	public FilterModuleAST fmAst;

	public Vector parameters;

	// public Vector conditions;
	public Vector internals; // instances

	// public Vector externals;
	// public Vector methods;
	public Vector inputFilters;

	public Vector outputFilters;

	public int uniqueNumber;

	/**
	 * @modelguid {04F87A00-10B0-476E-8C87-A6BD604DEF19}
	 * @roseuid 401FAA6303DA
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

	public FilterModule(FilterModuleAST inFmAst, Vector args, int number)
	{
		super();
		uniqueNumber = number;
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
			FilterModuleParameter fmp = new FilterModuleParameter(fmpAst, args.elementAt(index), counter);
			fmp.setParent(this);
			this.addParameter(fmp);
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
				this.addInternal(internal);
			}
			else
			{
				Internal internal = new Internal(internalAST);
				// counter++;
				internal.setParent(this);
				internal.setType(internalAST.getType());
				this.addInternal(internal);
			}
		}

		Iterator filterIt = fmAst.getInputFilterIterator();
		while (filterIt.hasNext())
		{
			Filter filter = new Filter((FilterAST) filterIt.next());
			this.addInputFilter(filter);
			filter.setParent(this);
			ds.addObject(filter);
		}

		filterIt = fmAst.getOutputFilterIterator();
		while (filterIt.hasNext())
		{
			Filter filter = new Filter((FilterAST) filterIt.next());
			this.addOutputFilter(filter);
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
	 * @modelguid {638C9279-9DD7-4E4B-92D1-52A44F9D6D93}
	 * @roseuid 401FAA640041
	 */
	public boolean addInternal(Internal internal)
	{
		internals.addElement(internal);
		return true;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal
	 * @modelguid {E1CCC976-8BB3-40C4-A7E8-024139E6EF68}
	 * @roseuid 401FAA640060
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
	 * @modelguid {0A8DFDE2-FD90-47F2-8C9E-1BE78B38EE07}
	 * @roseuid 401FAA640073
	 */
	public Internal getInternal(int index)
	{
		return (Internal) internals.elementAt(index);
	}

	/**
	 * @return java.util.Iterator
	 * @modelguid {0DD565EF-8A52-4205-AF2F-45EBC462C6DC}
	 * @roseuid 401FAA64007E
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
	 * @modelguid {F8B3B43F-D2DC-42F4-8BB7-960E34077798}
	 * @roseuid 401FAA640006
	 */
	public boolean addCondition(Condition condition)
	{
		return fmAst.addCondition(condition);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition
	 * @modelguid {E224F0A7-E476-40C6-AA91-DAAABCC9F2C8}
	 * @roseuid 401FAA640010
	 */
	public Condition removeCondition(int index)
	{
		return fmAst.removeCondition(index);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition
	 * @modelguid {6101C495-ACFE-4235-BABF-072163C217F8}
	 * @roseuid 401FAA640023
	 */
	public Condition getCondition(int index)
	{
		return fmAst.getCondition(index);
	}

	/**
	 * @return java.util.Iterator
	 * @modelguid {C1BFB6AC-3D27-4283-A51B-1D86CD52E9DD}
	 * @roseuid 401FAA64002E
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
	 * @modelguid {638C9279-9DD7-4E4B-92D1-52A44F9D6D93}
	 * @roseuid 401FAA640041
	 */
	public boolean addInternalAST(InternalAST internal)
	{
		return fmAst.addInternal(internal);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal
	 * @modelguid {E1CCC976-8BB3-40C4-A7E8-024139E6EF68}
	 * @roseuid 401FAA640060
	 */
	public InternalAST removeInternalAST(int index)
	{
		return fmAst.removeInternal(index);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal
	 * @modelguid {0A8DFDE2-FD90-47F2-8C9E-1BE78B38EE07}
	 * @roseuid 401FAA640073
	 */
	public InternalAST getInternalAST(int index)
	{
		return fmAst.getInternal(index);
	}

	/**
	 * @return java.util.Iterator
	 * @modelguid {0DD565EF-8A52-4205-AF2F-45EBC462C6DC}
	 * @roseuid 401FAA64007E
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
	 * @modelguid {66A6FE94-6148-4AEA-8398-9AA8194866B2}
	 * @roseuid 401FAA640091
	 */
	public boolean addExternal(External external)
	{
		return fmAst.addExternal(external);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.External
	 * @modelguid {E43D6A89-5769-4307-93CF-0C25F68FD0B6}
	 * @roseuid 401FAA64009C
	 */
	public External removeExternal(int index)
	{
		return fmAst.removeExternal(index);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.External
	 * @modelguid {DC6CEBA4-836A-40A4-BD34-CCDCDE04AE80}
	 * @roseuid 401FAA6400B0
	 */
	public External getExternal(int index)
	{
		return fmAst.getExternal(index);
	}

	/**
	 * @return java.util.Iterator
	 * @modelguid {7ED0DFC2-C26F-4AD8-930D-D8619B60A094}
	 * @roseuid 401FAA6400C4
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
	 * @modelguid {C26D4769-374D-4328-9EC6-1B739DEADC28}
	 * @roseuid 401FAA6400D7
	 * @deprecated
	 */
	public boolean addMethod(Method method)
	{
		return fmAst.addMethod(method);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Method
	 * @modelguid {5993A474-B082-4B82-9271-BB10A261D535}
	 * @roseuid 401FAA6400F6
	 * @deprecated
	 */
	public Method removeMethod(int index)
	{
		return fmAst.removeMethod(index);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Method
	 * @modelguid {251921D1-08F7-40BD-BE2F-EA2E20B28B83}
	 * @roseuid 401FAA640109
	 * @deprecated
	 */
	public Method getMethod(int index)
	{
		return fmAst.getMethod(index);
	}

	/**
	 * @return java.util.Iterator
	 * @modelguid {55FF6623-0ACA-4974-A6F7-CABA99F1CB93}
	 * @roseuid 401FAA64011D
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
	 * @modelguid {057E1939-BF1B-488B-9433-D9AFDDE5F3DC}
	 * @roseuid 401FAA640127
	 */
	public boolean addInputFilter(Filter inputfilter)
	{
		inputFilters.add(inputfilter);
		return true;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter
	 * @modelguid {8BAC3FDA-92BB-4806-8B21-497E6E09CC1E}
	 * @roseuid 401FAA64013C
	 */
	public Filter removeInputFilter(int index)
	{
		return (Filter) inputFilters.remove(index);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter
	 * @modelguid {92657675-90E7-4A7E-AEA9-D13717A78D94}
	 * @roseuid 401FAA64014F
	 */
	public Filter getInputFilter(int index)
	{
		return (Filter) inputFilters.get(index);
	}

	/**
	 * @return java.util.Iterator
	 * @modelguid {C4063FB6-952E-433E-8B4B-464D5ECC562C}
	 * @roseuid 401FAA640163
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
	 * @modelguid {33854751-CB00-46A6-BAEE-EE06C8E13F29}
	 * @roseuid 401FAA640177
	 */
	public boolean addOutputFilter(Filter outputfilter)
	{
		outputFilters.add(outputfilter);
		return true;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter
	 * @modelguid {7C532F57-5948-453E-AF4B-5E9BEC0C30A2}
	 * @roseuid 401FAA64018B
	 */
	public Filter removeOutputFilter(int index)
	{
		return (Filter) outputFilters.remove(index);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter
	 * @modelguid {4F30E1EA-4EAA-4352-B0F6-E72F3297C18B}
	 * @roseuid 401FAA64019F
	 */
	public Filter getOutputFilter(int index)
	{
		return (Filter) outputFilters.get(index);
	}

	/**
	 * @return java.util.Iterator
	 * @modelguid {4BDED6A5-B80F-401A-82B6-6CED9F185669}
	 * @roseuid 401FAA6401B4
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
	 * @ return boolean Checks whether a parameter does already exists. Fixme:
	 *   no difference yet between ?foo and ??foo
	 * @param fmp
	 */
	public boolean doesParameterASTExists(FilterModuleParameterAST fmp)
	{
		return fmAst.doesParameterExists(fmp);
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

	public Object clone() throws CloneNotSupportedException
	{
		return fmAst.clone();
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

	public int getUniqueNumber()
	{
		return uniqueNumber;
	}

	public void setUniqueNumber(int inUniqueNumber)
	{
		uniqueNumber = inUniqueNumber;
	}

	public String getName()
	{
		return name + '!' + uniqueNumber;
	}

	public String getOriginalName()
	{
		return name;
	}

}

/**
 * void FilterModule.accept(RepositoryVisitor){ visitor.visit(this); }
 */
