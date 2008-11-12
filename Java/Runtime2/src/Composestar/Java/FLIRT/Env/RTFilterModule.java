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

package Composestar.Java.FLIRT.Env;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.FMParams.FMParameter;
import Composestar.Core.CpsRepository2.FilterModules.Condition;
import Composestar.Core.CpsRepository2.FilterModules.External;
import Composestar.Core.CpsRepository2.FilterModules.Filter;
import Composestar.Core.CpsRepository2.FilterModules.FilterExpression;
import Composestar.Core.CpsRepository2.FilterModules.FilterModule;
import Composestar.Core.CpsRepository2.FilterModules.FilterModuleVariable;
import Composestar.Core.CpsRepository2.FilterModules.Internal;
import Composestar.Core.CpsRepository2.Instantiatable.Instantiator;
import Composestar.Core.CpsRepository2.Meta.SourceInformation;
import Composestar.Core.CpsRepository2.References.MethodReference;

/**
 * A runtime instance of the filter modules
 * 
 * @author Michiel Hendriks
 */
public class RTFilterModule implements FilterModule
{
	private static final long serialVersionUID = 1L;

	/**
	 * The filter module definition this RT version is based on
	 */
	protected FilterModule baseFM;

	/**
	 * The condition method that should be true before this FM is executed
	 */
	protected MethodReference conditionMethod;

	/**
	 * Objects instances for the various filter module variables
	 */
	protected Map<FilterModuleVariable, RTCpsObject> objects;

	/**
	 * The objects the internal/external CpsObjects point to. This list is used
	 * to keep these objects referenced in order ot prevent GC.
	 */
	protected List<Object> pointers;

	public RTFilterModule(FilterModule base, MethodReference cond)
	{
		super();
		baseFM = base;
		conditionMethod = cond;
		objects = new HashMap<FilterModuleVariable, RTCpsObject>();
		pointers = new ArrayList<Object>();
	}

	/**
	 * Cleanup all references to internals and externals so that those objects
	 * will be queued for GC
	 */
	public void cleanup()
	{
		// releases references to the CpsObjects
		pointers.clear();
		objects.clear();
	}

	/**
	 * @return The condition method that should be true, null if there is no
	 *         condition method
	 */
	public MethodReference getCondition()
	{
		return conditionMethod;
	}

	/**
	 * Get the CpsObject instance for an internal/external
	 * 
	 * @param fmvar
	 * @return
	 */
	public RTCpsObject getMemberObject(FilterModuleVariable fmvar)
	{
		return objects.get(fmvar);
	}

	/**
	 * Set the member object for a given fmvar
	 * 
	 * @param fmvar
	 * @param obj
	 */
	public void setMemberObject(FilterModuleVariable fmvar, RTCpsObject obj)
	{
		objects.put(fmvar, obj);
		pointers.add(obj.getObject());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#addFilter(
	 * Composestar.Core.CpsRepository2.FilterModules.Filter)
	 */
	public boolean addFilter(Filter filter) throws NullPointerException
	{
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#addParameter
	 * (Composestar.Core.CpsRepository2.FMParams.FMParameter)
	 */
	public boolean addParameter(FMParameter param) throws NullPointerException
	{
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#addVariable
	 * (Composestar.Core.CpsRepository2.FilterModules.FilterModuleVariable)
	 */
	public boolean addVariable(FilterModuleVariable var) throws NullPointerException
	{
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getCondition
	 * (java.lang.String)
	 */
	public Condition getCondition(String conditionName)
	{
		return baseFM.getCondition(conditionName);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getExternal
	 * (java.lang.String)
	 */
	public External getExternal(String externalName)
	{
		return baseFM.getExternal(externalName);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getFilter(
	 * java.lang.String)
	 */
	public Filter getFilter(String filterName)
	{
		return baseFM.getFilter(filterName);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getFilters()
	 */
	public Collection<Filter> getFilters()
	{
		return baseFM.getFilters();
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.FilterModules.FilterModule#
	 * getInputFilterExpression()
	 */
	public FilterExpression getInputFilterExpression()
	{
		return baseFM.getInputFilterExpression();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getInternal
	 * (java.lang.String)
	 */
	public Internal getInternal(String internalName)
	{
		return baseFM.getInternal(internalName);
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.FilterModules.FilterModule#
	 * getOutputFilterExpression()
	 */
	public FilterExpression getOutputFilterExpression()
	{
		return baseFM.getOutputFilterExpression();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getParameter
	 * (java.lang.String)
	 */
	public FMParameter getParameter(String paramName)
	{
		return baseFM.getParameter(paramName);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getParameters
	 * ()
	 */
	public List<FMParameter> getParameters()
	{
		return baseFM.getParameters();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getVariable
	 * (java.lang.String)
	 */
	public FilterModuleVariable getVariable(String varName)
	{
		return baseFM.getVariable(varName);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getVariables()
	 */
	public Collection<FilterModuleVariable> getVariables()
	{
		return baseFM.getVariables();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#hasParameters
	 * ()
	 */
	public boolean hasParameters()
	{
		return baseFM.hasParameters();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#isUniqueMemberName
	 * (java.lang.String)
	 */
	public boolean isUniqueMemberName(String memberName)
	{
		return baseFM.isUniqueMemberName(memberName);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#removeFilter
	 * (Composestar.Core.CpsRepository2.FilterModules.Filter)
	 */
	public Filter removeFilter(Filter filter) throws NullPointerException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#removeFilter
	 * (java.lang.String)
	 */
	public Filter removeFilter(String filterName)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#removeVariable
	 * (Composestar.Core.CpsRepository2.FilterModules.FilterModuleVariable)
	 */
	public FilterModuleVariable removeVariable(FilterModuleVariable var) throws NullPointerException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#removeVariable
	 * (java.lang.String)
	 */
	public FilterModuleVariable removeVariable(String varName)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.FilterModules.FilterModule#
	 * setInputFilterExpression
	 * (Composestar.Core.CpsRepository2.FilterModules.FilterExpression)
	 */
	public void setInputFilterExpression(FilterExpression expr)
	{
	// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.FilterModules.FilterModule#
	 * setOutputFilterExpression
	 * (Composestar.Core.CpsRepository2.FilterModules.FilterExpression)
	 */
	public void setOutputFilterExpression(FilterExpression expr)
	{
	// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.QualifiedRepositoryEntity#
	 * getFullyQualifiedName()
	 */
	public String getFullyQualifiedName()
	{
		return baseFM.getFullyQualifiedName();
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.QualifiedRepositoryEntity#getName()
	 */
	public String getName()
	{
		return baseFM.getName();
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.RepositoryEntity#getOwner()
	 */
	public RepositoryEntity getOwner()
	{
		return baseFM.getOwner();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.RepositoryEntity#getSourceInformation()
	 */
	public SourceInformation getSourceInformation()
	{
		return baseFM.getSourceInformation();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.RepositoryEntity#setOwner(Composestar
	 * .Core.CpsRepository2.RepositoryEntity)
	 */
	public RepositoryEntity setOwner(RepositoryEntity newOwner)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.RepositoryEntity#setSourceInformation
	 * (Composestar.Core.CpsRepository2.Meta.SourceInformation)
	 */
	public void setSourceInformation(SourceInformation srcInfo)
	{
	// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.References.Reference#dereference()
	 */
	public void dereference() throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.References.Reference#getReference()
	 */
	public FilterModule getReference()
	{
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.References.Reference#getReferenceId()
	 */
	public String getReferenceId()
	{
		return baseFM.getReferenceId();
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.References.Reference#isResolved()
	 */
	public boolean isResolved()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.References.Reference#isSelfReference()
	 */
	public boolean isSelfReference()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.References.Reference#setReference(java
	 * .lang.Object)
	 */
	public void setReference(FilterModule element) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.Instantiatable.Instantiatable#newInstance
	 * (Composestar.Core.CpsRepository2.Instantiatable.Instantiator)
	 */
	public FilterModule newInstance(Instantiator instantiator) throws UnsupportedOperationException
	{
		return baseFM.newInstance(instantiator);
	}
}
