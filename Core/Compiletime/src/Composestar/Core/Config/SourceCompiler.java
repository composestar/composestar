/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2007 University of Twente.
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

package Composestar.Core.Config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import Composestar.Core.COMP.LangCompiler;
import Composestar.Core.DUMMER.DummyManager;
import Composestar.Core.Exception.ModuleException;

/**
 * Information about the compiler that is used to compile the associated
 * language. It does not refer to the actual compiler, but rather to a class
 * that handles compilation.
 * 
 * @author Michiel Hendriks
 */
public class SourceCompiler implements Serializable
{
	private static final long serialVersionUID = 6182815168461588844L;

	/**
	 * The class that manages the compilation stuff
	 */
	protected String classname;

	/**
	 * Cached handle to an instance of the compiler
	 */
	protected transient LangCompiler compiler;

	/**
	 * List of actions the compiler handler will use
	 */
	protected Map<String, CompilerAction> actions;

	public SourceCompiler()
	{
		actions = new HashMap<String, CompilerAction>();
	}

	public void setClassname(String inClassname)
	{
		if (inClassname == null || inClassname.trim().length() == 0)
		{
			throw new IllegalArgumentException("Classname can not be null or empty");
		}
		classname = inClassname.trim();
	}

	public String getClassname()
	{
		return classname;
	}

	public void addAction(CompilerAction action)
	{
		if (action == null)
		{
			throw new IllegalArgumentException("Action can not be null");
		}
		String name = action.getName();
		if (name == null || name.trim().length() == 0)
		{
			throw new IllegalArgumentException("Action has an invalid name");
		}
		actions.put(name, action);
	}

	public CompilerAction getAction(String name)
	{
		return actions.get(name);
	}

	/**
	 * Get the instance to the LangCompiler that managed this compilation stuff.
	 * 
	 * @return
	 * @throws ModuleException
	 */
	public LangCompiler getCompiler() throws ModuleException
	{
		if (compiler == null)
		{
			try
			{
				Class<?> emittedClass = Class.forName(classname);
				compiler = (LangCompiler) emittedClass.newInstance();
				compiler.setCompilerConfig(this);
			}
			catch (Exception e)
			{
				throw new ModuleException("Error while instantiating LangCompiler: " + classname,
						DummyManager.MODULE_NAME, e);
			}
		}
		return compiler;
	}

}
