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
package Composestar.Core.CpsRepository2;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import Composestar.Core.CpsRepository2.SuperImposition.SuperImposition;

/**
 * Base test case of repository implementation
 * 
 * @author Michiel Hendriks
 */
public abstract class RepositoryTestBase extends TestCase
{
	protected Repository repos;

	protected RepositoryEntity item1;

	protected RepositoryEntity item2;

	protected QualifiedRepositoryEntity qitem1;

	protected QualifiedRepositoryEntity qitem2;

	public RepositoryTestBase()
	{
		super();
	}

	public RepositoryTestBase(String name)
	{
		super(name);
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.RepositoryImpl#clear()}.
	 */
	public void testClear()
	{
		repos.add(item1);
		repos.add(item2);
		repos.add(qitem1);
		repos.add(qitem2);
		assertNotNull(repos.get(qitem1.getFullyQualifiedName()));
		assertNotNull(repos.get(qitem2.getFullyQualifiedName()));
		repos.clear();
		assertEquals(0, repos.size());
		assertNull(repos.get(qitem1.getFullyQualifiedName()));
		assertNull(repos.get(qitem2.getFullyQualifiedName()));
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.RepositoryImpl#add(Composestar.Core.CpsRepository2.RepositoryEntity)}.
	 */
	public void testAddRepositoryEntity()
	{
		assertEquals(0, repos.size());
		repos.add(item1);
		assertEquals(1, repos.size());
		repos.add(qitem1);
		assertEquals(2, repos.size());
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.RepositoryImpl#remove(java.lang.Object)}.
	 */
	public void testRemoveObject()
	{
		repos.add(item1);
		assertEquals(1, repos.size());
		repos.remove(item1);
		assertEquals(0, repos.size());

		repos.add(qitem1);
		assertEquals(1, repos.size());
		assertNotNull(repos.get(qitem1.getFullyQualifiedName()));
		repos.remove(qitem1);
		assertEquals(0, repos.size());
		assertNull(repos.get(qitem1.getFullyQualifiedName()));
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.RepositoryImpl#removeAll(java.util.Collection)}.
	 */
	public void testRemoveAllCollectionOfQ()
	{
		repos.add(item1);
		repos.add(item2);
		repos.add(qitem1);
		repos.add(qitem2);
		assertEquals(4, repos.size());
		List<RepositoryEntity> lst = new ArrayList<RepositoryEntity>();
		lst.add(item1);
		lst.add(qitem1);
		repos.removeAll(lst);
		assertEquals(2, repos.size());
		assertNull(repos.get(qitem1.getFullyQualifiedName()));
		assertNotNull(repos.get(qitem2.getFullyQualifiedName()));
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.RepositoryImpl#retainAll(java.util.Collection)}.
	 */
	public void testRetainAllCollectionOfQ()
	{
		repos.add(item1);
		repos.add(item2);
		repos.add(qitem1);
		repos.add(qitem2);
		assertEquals(4, repos.size());
		List<RepositoryEntity> lst = new ArrayList<RepositoryEntity>();
		lst.add(item1);
		lst.add(qitem1);
		repos.retainAll(lst);
		assertEquals(2, repos.size());
		assertNotNull(repos.get(qitem1.getFullyQualifiedName()));
		assertNull(repos.get(qitem2.getFullyQualifiedName()));
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.RepositoryImpl#get(java.lang.String)}.
	 */
	public void testGetString()
	{
		repos.add(qitem1);
		assertNotNull(repos.get(qitem1.getFullyQualifiedName()));
		assertNull(repos.get(qitem2.getFullyQualifiedName()));
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.RepositoryImpl#get(java.lang.String, java.lang.Class)}.
	 */
	public void testGetStringClassOfT()
	{
		repos.add(qitem1);
		assertNotNull(repos.get(qitem1.getFullyQualifiedName(), qitem1.getClass()));
		assertNull(repos.get(qitem1.getFullyQualifiedName(), SuperImposition.class));
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.RepositoryImpl#getAll(java.lang.Class)}.
	 */
	public void testGetAll()
	{
		repos.add(item1);
		repos.add(item2);
		repos.add(qitem1);
		repos.add(qitem2);
		int cnt = 0;
		for (RepositoryEntity item : repos.getAll(RepositoryEntity.class))
		{
			if (item != null)
			{
				++cnt;
			}
		}
		assertEquals(4, cnt);
		cnt = 0;
		for (QualifiedRepositoryEntity item : repos.getAll(QualifiedRepositoryEntity.class))
		{
			if (item != null)
			{
				++cnt;
			}
		}
		assertEquals(2, cnt);
		cnt = 0;
		for (Object item : repos.getAll(item1.getClass()))
		{
			if (item != null)
			{
				++cnt;
			}
		}
		assertEquals(2, cnt);
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.RepositoryImpl#getAllAsSet(java.lang.Class)}.
	 */
	public void testGetAllAsSet()
	{
		repos.add(item1);
		repos.add(item2);
		repos.add(qitem1);
		repos.add(qitem2);
		assertEquals(4, repos.getAllAsSet(RepositoryEntity.class).size());
		assertEquals(2, repos.getAllAsSet(QualifiedRepositoryEntity.class).size());
		assertEquals(2, repos.getAllAsSet(item1.getClass()).size());
	}

}
