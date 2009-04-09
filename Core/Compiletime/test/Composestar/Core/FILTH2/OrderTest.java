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

package Composestar.Core.FILTH2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;
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
import Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule;
import Composestar.Core.CpsRepository2.SISpec.FilterModuleBinding;
import Composestar.Core.CpsRepository2.SISpec.Constraints.Constraint;
import Composestar.Core.CpsRepository2.SISpec.Constraints.ConstraintFactory;
import Composestar.Core.CpsRepository2.SISpec.Constraints.ConstraintValue;
import Composestar.Core.CpsRepository2Impl.AbstractQualifiedRepositoryEntity;
import Composestar.Core.CpsRepository2Impl.SISpec.Constraints.ConstraintFactoryImpl;
import Composestar.Core.CpsRepository2Impl.SISpec.Constraints.FilterModuleConstraintValueImpl;
import Composestar.Core.CpsRepository2Impl.SISpec.Constraints.OrderingConstraint;
import Composestar.Core.FILTH2.Ordering.OrderGenerator;

/**
 * @author Michiel Hendriks
 */
public class OrderTest extends TestCase
{
	protected List<ImposedFilterModule> actions;

	protected Set<Constraint> emptyConstraints, constraints;

	protected ConstraintFactory factory = new ConstraintFactoryImpl();

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		emptyConstraints = Collections.emptySet();
		constraints = new HashSet<Constraint>();
		actions = new ArrayList<ImposedFilterModule>();
		actions.add(new DummyIFM("A"));
		actions.add(new DummyIFM("B"));
		actions.add(new DummyIFM("C"));
		actions.add(new DummyIFM("D"));
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
		actions = null;
	}

	public static int factorial(int i)
	{
		int result = 1;
		for (int n = 2; n <= i; n++)
		{
			result *= n;
		}
		return result;
	}

	/**
	 * @param imposedFilterModule
	 * @param imposedFilterModule2
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalArgumentException
	 * @throws NullPointerException
	 */
	protected Constraint createConstraint(ImposedFilterModule imposedFilterModule,
			ImposedFilterModule imposedFilterModule2) throws NullPointerException, IllegalArgumentException,
			InstantiationException
	{
		List<ConstraintValue> args = new ArrayList<ConstraintValue>();
		args.add(new FilterModuleConstraintValueImpl(imposedFilterModule.getFilterModule()));
		args.add(new FilterModuleConstraintValueImpl(imposedFilterModule2.getFilterModule()));
		Constraint c = factory.createConstraint(OrderingConstraint.NAME, args);
		constraints.add(c);
		return c;
	}

	public void testPermutation()
	{
		System.out.println("testPermutation");

		Set<List<ImposedFilterModule>> results = OrderGenerator.generate(actions, emptyConstraints, -1);
		assertEquals(factorial(actions.size()), results.size());
		for (List<ImposedFilterModule> singleResult : results)
		{
			assertEquals(actions.size(), singleResult.size());
			System.out.println(singleResult);
		}
	}

	public void testSingleConstraint() throws Exception
	{
		System.out.println("testSingleConstraint");
		System.out.println(createConstraint(actions.get(0), actions.get(1)));

		Set<List<ImposedFilterModule>> results = OrderGenerator.generate(actions, constraints, -1);
		assertEquals(factorial(actions.size()) / 2, results.size());
		for (List<ImposedFilterModule> singleResult : results)
		{
			assertEquals(actions.size(), singleResult.size());
			System.out.println(singleResult);
		}
	}

	public void testTwoConstraint() throws Exception
	{
		System.out.println("testTwoConstraint");
		System.out.println(createConstraint(actions.get(0), actions.get(1)));
		System.out.println(createConstraint(actions.get(0), actions.get(2)));

		Set<List<ImposedFilterModule>> results = OrderGenerator.generate(actions, constraints, -1);
		assertEquals(factorial(actions.size()) / 3, results.size());
		for (List<ImposedFilterModule> singleResult : results)
		{
			assertEquals(actions.size(), singleResult.size());
			System.out.println(singleResult);
		}
	}

	public void testThreeConstraint() throws Exception
	{
		System.out.println("testTwoConstraint");
		System.out.println(createConstraint(actions.get(0), actions.get(1)));
		System.out.println(createConstraint(actions.get(0), actions.get(2)));
		System.out.println(createConstraint(actions.get(0), actions.get(3)));

		Set<List<ImposedFilterModule>> results = OrderGenerator.generate(actions, constraints, -1);
		assertEquals(factorial(actions.size()) / 4, results.size());
		for (List<ImposedFilterModule> singleResult : results)
		{
			assertEquals(actions.size(), singleResult.size());
			System.out.println(singleResult);
		}
	}

	public void testOnePossibleOrder() throws Exception
	{
		System.out.println("testOnePossibleOrder");
		System.out.println(createConstraint(actions.get(0), actions.get(1)));
		System.out.println(createConstraint(actions.get(1), actions.get(2)));
		System.out.println(createConstraint(actions.get(2), actions.get(3)));

		Set<List<ImposedFilterModule>> results = OrderGenerator.generate(actions, constraints, -1);
		assertEquals(1, results.size());
		for (List<ImposedFilterModule> singleResult : results)
		{
			assertEquals(actions.size(), singleResult.size());
			System.out.println(singleResult);
		}
	}

	public void testNoOrder() throws Exception
	{
		System.out.println("testNoOrder");
		System.out.println(createConstraint(actions.get(0), actions.get(1)));
		System.out.println(createConstraint(actions.get(1), actions.get(0)));

		Set<List<ImposedFilterModule>> results = OrderGenerator.generate(actions, constraints, -1);
		assertEquals(0, results.size());
		for (List<ImposedFilterModule> singleResult : results)
		{
			assertEquals(actions.size(), singleResult.size());
			System.out.println(singleResult);
		}
	}

	public void testNoOrder2() throws Exception
	{
		System.out.println("testNoOrder2");
		System.out.println(createConstraint(actions.get(0), actions.get(1)));
		System.out.println(createConstraint(actions.get(1), actions.get(2)));
		System.out.println(createConstraint(actions.get(2), actions.get(0)));

		Set<List<ImposedFilterModule>> results = OrderGenerator.generate(actions, constraints, -1);
		assertEquals(0, results.size());
		for (List<ImposedFilterModule> singleResult : results)
		{
			assertEquals(actions.size(), singleResult.size());
			System.out.println(singleResult);
		}
	}

	public void testNoOrder3() throws Exception
	{
		System.out.println("testNoOrder3");
		System.out.println(createConstraint(actions.get(0), actions.get(0)));

		Set<List<ImposedFilterModule>> results = OrderGenerator.generate(actions, constraints, -1);
		assertEquals(0, results.size());
		for (List<ImposedFilterModule> singleResult : results)
		{
			assertEquals(actions.size(), singleResult.size());
			System.out.println(singleResult);
		}
	}

	public void testPhantomActions() throws Exception
	{
		System.out.println("testPhantomActions");
		System.out.println(createConstraint(actions.get(0), new DummyIFM("X")));
		System.out.println(createConstraint(actions.get(2), new DummyIFM("Y")));
		System.out.println(createConstraint(actions.get(3), new DummyIFM("Z")));

		Set<List<ImposedFilterModule>> results = OrderGenerator.generate(actions, constraints, -1);
		assertEquals(factorial(actions.size()), results.size());
		for (List<ImposedFilterModule> singleResult : results)
		{
			assertEquals(actions.size(), singleResult.size());
			System.out.println(singleResult);
		}
	}

	public void testAstronomical()
	{
		actions.add(new DummyIFM("E"));
		actions.add(new DummyIFM("F"));
		actions.add(new DummyIFM("G"));
		actions.add(new DummyIFM("H"));
		actions.add(new DummyIFM("I"));
		actions.add(new DummyIFM("J"));
		OrderGenerator.generate(actions, emptyConstraints, 10);
		OrderGenerator.generate(actions, emptyConstraints, 100);
		OrderGenerator.generate(actions, emptyConstraints, 1000);
		OrderGenerator.generate(actions, emptyConstraints, 10000);
	}

	protected static class DummyIFM implements ImposedFilterModule
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		protected FilterModule fm;

		public DummyIFM(String FMname)
		{
			fm = new DummyFM(FMname);
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			return getFilterModule().getFullyQualifiedName();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule#getCondition
		 * ()
		 */
		public MethodReference getCondition()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.SIInfo.ImposedFilterModule#
		 * getFilterModule()
		 */
		public FilterModule getFilterModule()
		{
			return fm;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule#getImposedBy
		 * ()
		 */
		public FilterModuleBinding getImposedBy()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.SIInfo.ImposedFilterModule#
		 * setFilterModule
		 * (Composestar.Core.CpsRepository2.FilterModules.FilterModule)
		 */
		public void setFilterModule(FilterModule fm) throws NullPointerException
		{}

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.CpsRepository2.RepositoryEntity#getOwner()
		 */
		public RepositoryEntity getOwner()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.RepositoryEntity#getSourceInformation
		 * ()
		 */
		public SourceInformation getSourceInformation()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.RepositoryEntity#setOwner(Composestar
		 * .Core.CpsRepository2.RepositoryEntity)
		 */
		public RepositoryEntity setOwner(RepositoryEntity newOwner)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.RepositoryEntity#setSourceInformation
		 * (Composestar.Core.CpsRepository2.Meta.SourceInformation)
		 */
		public void setSourceInformation(SourceInformation srcInfo)
		{}
	}

	protected static class DummyFM extends AbstractQualifiedRepositoryEntity implements FilterModule
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public DummyFM(String name)
		{
			super(name);
		}

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
		 * @seeComposestar.Core.CpsRepository2.FilterModules.FilterModule#
		 * getDeclaredName()
		 */
		public String getDeclaredName()
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
		 * @seeComposestar.Core.CpsRepository2.FilterModules.FilterModule#
		 * isUniqueMemberName(java.lang.String)
		 */
		public boolean isUniqueMemberName(String memberName)
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

			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#getReferenceId()
		 */
		public String getReferenceId()
		{

			return getFullyQualifiedName();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#isResolved()
		 */
		public boolean isResolved()
		{

			return true;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#isSelfReference
		 * ()
		 */
		public boolean isSelfReference()
		{

			return true;
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
	}
}
