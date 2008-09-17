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

package Composestar.Core.CpsRepository2Impl.Reference;

import java.util.Collection;
import java.util.List;

import Composestar.Core.CpsRepository2.FMParams.FMParameter;
import Composestar.Core.CpsRepository2.FilterModules.Condition;
import Composestar.Core.CpsRepository2.FilterModules.External;
import Composestar.Core.CpsRepository2.FilterModules.Filter;
import Composestar.Core.CpsRepository2.FilterModules.FilterExpression;
import Composestar.Core.CpsRepository2.FilterModules.FilterModule;
import Composestar.Core.CpsRepository2.FilterModules.FilterModuleVariable;
import Composestar.Core.CpsRepository2.FilterModules.Internal;
import Composestar.Core.CpsRepository2.Instantiatable.Instantiator;
import Composestar.Core.CpsRepository2.References.FilterModuleReferenceTestBase;
import Composestar.Core.CpsRepository2Impl.AbstractQualifiedRepositoryEntity;
import Composestar.Core.CpsRepository2Impl.FilterModules.FilterModuleImpl;

/**
 * @author Michiel Hendriks
 */
public class FilterModuleReferenceImplTest extends FilterModuleReferenceTestBase
{

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		element = new DummyFM();
		ref = new FilterModuleImpl("foo");
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.Reference.FilterModuleReferenceImpl#FilterModuleReferenceImpl(java.lang.String)}
	 * .
	 */
	public void testFilterModuleReferenceImpl()
	{
		try
		{
			new FilterModuleReferenceImpl(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyFM extends AbstractQualifiedRepositoryEntity implements FilterModule
	{
		private static final long serialVersionUID = 1L;

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#addFilter
		 * (Composestar.Core.CpsRepository2.FilterModules.Filter)
		 */
		public boolean addFilter(Filter filter) throws NullPointerException
		{
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
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getExternal
		 * (java.lang.String)
		 */
		public External getExternal(String externalName)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getFilter
		 * (java.lang.String)
		 */
		public Filter getFilter(String filterName)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getFilters
		 * ()
		 */
		public Collection<Filter> getFilters()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.FilterModules.FilterModule#
		 * getInputFilterExpression()
		 */
		public FilterExpression getInputFilterExpression()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getInternal
		 * (java.lang.String)
		 */
		public Internal getInternal(String internalName)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.FilterModules.FilterModule#
		 * getOutputFilterExpression()
		 */
		public FilterExpression getOutputFilterExpression()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getParameter
		 * (java.lang.String)
		 */
		public FMParameter getParameter(String paramName)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getParameters
		 * ()
		 */
		public List<FMParameter> getParameters()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getVariable
		 * (java.lang.String)
		 */
		public FilterModuleVariable getVariable(String varName)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getVariables
		 * ()
		 */
		public Collection<FilterModuleVariable> getVariables()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#hasParameters
		 * ()
		 */
		public boolean hasParameters()
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#removeFilter
		 * (Composestar.Core.CpsRepository2.FilterModules.Filter)
		 */
		public Filter removeFilter(Filter filter) throws NullPointerException
		{
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
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.FilterModules.FilterModule#
		 * setInputFilterExpression
		 * (Composestar.Core.CpsRepository2.FilterModules.FilterExpression)
		 */
		public void setInputFilterExpression(FilterExpression expr)
		{}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.FilterModules.FilterModule#
		 * setOutputFilterExpression
		 * (Composestar.Core.CpsRepository2.FilterModules.FilterExpression)
		 */
		public void setOutputFilterExpression(FilterExpression expr)
		{}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#dereference()
		 */
		public void dereference() throws UnsupportedOperationException
		{}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#getReference()
		 */
		public FilterModule getReference()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#getReferenceId()
		 */
		public String getReferenceId()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#isResolved()
		 */
		public boolean isResolved()
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#isSelfReference
		 * ()
		 */
		public boolean isSelfReference()
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#setReference
		 * (java.lang.Object)
		 */
		public void setReference(FilterModule element) throws UnsupportedOperationException
		{}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.Instantiatable.Instantiatable#newInstance
		 * (Composestar.Core.CpsRepository2.Instantiatable.Instantiator)
		 */
		public FilterModule newInstance(Instantiator instantiator) throws UnsupportedOperationException
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.FilterModules.FilterModule#
		 * isUniqueFilterName(java.lang.String)
		 */
		public boolean isUniqueMemberName(String filterName)
		{
			return false;
		}
	}

}
