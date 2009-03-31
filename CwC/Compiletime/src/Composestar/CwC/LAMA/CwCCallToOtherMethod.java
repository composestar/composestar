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

package Composestar.CwC.LAMA;

import weavec.ast.TNode;
import Composestar.Core.LAMA.CallToOtherMethod;

/**
 * @author Michiel Hendriks
 */
public class CwCCallToOtherMethod extends CallToOtherMethod
{
	private static final long serialVersionUID = -7757781861461653392L;

	protected TNode astNode;

	/**
	 * The base-call site of this call-to-method. This is a AST node which is a
	 * child of the current scope.
	 */
	protected TNode scopeRootAstNode;

	public CwCCallToOtherMethod()
	{
		super();
	}

	public void setASTNode(TNode node)
	{
		astNode = node;
	}

	public TNode getASTNode()
	{
		return astNode;
	}

	/**
	 * @param node the parentAstNode to set
	 */
	public void setScopeRootAstNode(TNode node)
	{
		scopeRootAstNode = node;
	}

	/**
	 * @return the parentAstNode
	 */
	public TNode getScopeRootAstNode()
	{
		return scopeRootAstNode;
	}
}
