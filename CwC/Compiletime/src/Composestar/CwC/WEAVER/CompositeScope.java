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

package Composestar.CwC.WEAVER;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import weavec.cmodel.declaration.Declaration;
import weavec.cmodel.scope.BlockScope;
import weavec.cmodel.scope.CNamespaceKind;
import weavec.cmodel.scope.Namespace;
import weavec.cmodel.util.BlockScopeImpl;
import weavec.util.RecursivePrinter;

class CompositeScope extends BlockScopeImpl
{
	protected BlockScope parent1;

	protected BlockScope parent2;

	public CompositeScope(BlockScope scope1, BlockScope scope2)
	{
		super(null, false);
		parent1 = scope1;
		parent2 = scope2;
	}

	@Override
	public BlockScope getNewChild(boolean functionBlockScope)
	{
		return new BlockScopeImpl(this, functionBlockScope);
	}

	@Override
	public void addDeclaration(CNamespaceKind kind, Declaration declaration)
	{
		throw new UnsupportedOperationException("addDeclaration");
	}

	@Override
	public void addDefinition(CNamespaceKind kind, Declaration declaration)
	{
		throw new UnsupportedOperationException("addDeclaration");
	}

	@Override
	public Declaration get(CNamespaceKind kind, String id)
	{
		Declaration result = parent1.get(kind, id);
		if (result == null)
		{
			result = parent1.get(kind, id);
		}
		return result;
	}

	@Override
	public Collection<Declaration> getAll(CNamespaceKind kind, String id)
	{
		Collection<Declaration> result = new HashSet<Declaration>();
		Collection<Declaration> x;
		x = parent1.getAll(kind, id);
		if (x != null)
		{
			result.addAll(x);
		}
		x = parent2.getAll(kind, id);
		if (x != null)
		{
			result.addAll(x);
		}
		return result;
	}

	@Override
	public Collection<BlockScope> getChildren()
	{
		Collection<BlockScope> result = new HashSet<BlockScope>();
		Collection<BlockScope> x;
		x = parent1.getChildren();
		if (x != null)
		{
			result.addAll(x);
		}
		x = parent2.getChildren();
		if (x != null)
		{
			result.addAll(x);
		}
		return null;
	}

	@Override
	public Namespace getNamespace(CNamespaceKind kind)
	{
		Namespace result = parent1.getNamespace(kind);
		if (result == null)
		{
			parent2.getNamespace(kind);
		}
		return result;
	}

	@Override
	public BlockScope getParent()
	{
		return null;
	}

	@Override
	public Set<String> keySet(CNamespaceKind kind)
	{
		Set<String> result = new HashSet<String>();
		Set<String> x;
		x = parent1.keySet(kind);
		if (x != null)
		{
			result.addAll(x);
		}
		x = parent2.keySet(kind);
		if (x != null)
		{
			result.addAll(x);
		}
		return result;
	}

	@Override
	public Set<String> localKeySet(CNamespaceKind kind)
	{
		Set<String> result = new HashSet<String>();
		Set<String> x;
		x = parent1.localKeySet(kind);
		if (x != null)
		{
			result.addAll(x);
		}
		x = parent2.localKeySet(kind);
		if (x != null)
		{
			result.addAll(x);
		}
		return result;
	}

	@Override
	public Set<String> objectKeySet(CNamespaceKind kind)
	{
		Set<String> result = new HashSet<String>();
		Set<String> x;
		x = parent1.objectKeySet(kind);
		if (x != null)
		{
			result.addAll(x);
		}
		x = parent2.objectKeySet(kind);
		if (x != null)
		{
			result.addAll(x);
		}
		return result;
	}

	@Override
	public boolean isParameterScope()
	{
		return false;
	}

	@Override
	public boolean isRootScope()
	{
		return false;
	}

	@Override
	public void printNonrecursive(RecursivePrinter rp)
	{
	// nop
	}

	@Override
	public void printRecursive(RecursivePrinter rp)
	{
	// n op
	}

}