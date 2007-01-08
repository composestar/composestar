/*
 * This file is part of WeaveC project [http://weavec.sf.net].
 * Copyright (C) 2005 University of Twente.
 *
 * Licensed under the BSD License.
 * [http://www.opensource.org/licenses/bsd-license.php]
 * 
 * Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 * 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the University of Twente nor the names of its 
 contributors may be used to endorse or promote products derived from
 this software without specific prior written permission.

 * THIS SOFTWARE IS PROVIDED BY AUTHOR AND CONTRIBUTORS ``AS IS'' AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODSOR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * $Id: CSymbolTable.java,v 1.1 2006/09/04 08:12:15 johantewinkel Exp $
 */
package Composestar.C.wrapper.parsing;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

public class CSymbolTable
{

	/** holds list of scopes */
	private Vector scopeStack;

	public Hashtable getSymTable()
	{
		return symTable;
	}

	public void printSymbolTable()
	{
		Iterator it = symTable.keySet().iterator();
		while (it.hasNext())
		{
			String str = (String) it.next();
			System.out.println(" +++ " + str + " \t\t " + symTable.get(str));
		}
		System.out.println("------------------------");
		// Enumeration symenum = symTable.keys();
		// while (enum.hasMoreElements())
		// {
		// System.out.println("--- " + enum.nextElement());
		//
		// }
	}

	/**
	 * table where all defined names are mapped to
	 * com.ideals.weavec.wrapper.parsing.TNode tree nodes
	 */
	private Hashtable symTable;

	public CSymbolTable()
	{
		scopeStack = new Vector(10);
		symTable = new Hashtable(533);
	}

	/**
	 * push a new scope onto the scope stack.
	 */
	public void pushScope(String s)
	{
		// System.out.println("!!! push scope:" + s);
		scopeStack.addElement(s);
	}

	/**
	 * pop the last scope off the scope stack.
	 */
	public void popScope()
	{
		// System.out.println("!!! pop scope");
		int size = scopeStack.size();
		if (size > 0)
		{
			scopeStack.removeElementAt(size - 1);
		}
	}

	/**
	 * return the current scope as a string
	 */
	public String currentScopeAsString()
	{
		StringBuffer buf = new StringBuffer(100);
		boolean first = true;
		Enumeration e = scopeStack.elements();
		while (e.hasMoreElements())
		{
			if (first)
			{
				first = false;
			}
			else
			{
				buf.append("::");
			}
			buf.append(e.nextElement().toString());
		}
		return buf.toString();
	}

	/**
	 * given a name for a type, append it with the current scope.
	 */
	public String addCurrentScopeToName(String name)
	{
		// System.out.println("!!! addCurrentScopeToName " + name);
		String currScope = currentScopeAsString();
		return addScopeToName(currScope, name);
	}

	/**
	 * given a name for a type, append it with the given scope. MBZ
	 */
	public String addScopeToName(String scope, String name)
	{
		if (scope == null || scope.length() > 0)
		{
			return scope + "::" + name;
		}
		else
		{
			return name;
		}
	}

	/** remove one level of scope from name MBZ */
	public String removeOneLevelScope(String scopeName)
	{
		int index = scopeName.lastIndexOf("::");
		if (index > 0)
		{
			return scopeName.substring(0, index);
		}
		if (scopeName.length() > 0)
		{
			return "";
		}
		return null;
	}

	/**
	 * add a node to the table with it's key as the current scope and the name
	 */
	public TNode add(String name, TNode node)
	{
		// System.out.println("!!! add " + name+" == "+node.toString());
		return (TNode) symTable.put(addCurrentScopeToName(name), node);
	}

	/** lookup a fully scoped name in the symbol table */
	public TNode lookupScopedName(String scopedName)
	{
		// System.out.println("!!! lookupScopedName " + scopedName);
		return (TNode) symTable.get(scopedName);
	}

	/**
	 * lookup an unscoped name in the table by prepending the current scope. MBZ --
	 * if not found, pop scopes and look again
	 */
	public TNode lookupNameInCurrentScope(String name)
	{
		// System.out.println("!!! lookupNameInCurrentScope");
		String scope = currentScopeAsString();
		String scopedName;
		TNode tnode = null;

		// System.out.println( "\n!!!"+ this.toString() );

		while (tnode == null && scope != null)
		{
			scopedName = addScopeToName(scope, name);
			// System.out.println("!!! lookup trying " + scopedName);
			tnode = (TNode) symTable.get(scopedName);
			scope = removeOneLevelScope(scope);
		}
		return tnode;
	}

	/** convert this table to a string */
	public String toString()
	{
		StringBuffer buff = new StringBuffer(300);
        buff.append("Composestar.C.parsing.CSymbolTable { \nCurrentScope: ").append(currentScopeAsString()).append("\nDefinedSymbols:\n");
		Enumeration ke = symTable.keys();
		Enumeration ve = symTable.elements();
		while (ke.hasMoreElements())
		{
            buff.append(ke.nextElement().toString()).append(" (").append(TNode.getNameForType(((TNode) ve.nextElement()).getType())).append(")\n");
		}
		buff.append("}\n");
		return buff.toString();
	}

}
