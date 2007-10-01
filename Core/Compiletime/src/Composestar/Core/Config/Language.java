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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import Composestar.Core.DUMMER.DummyEmitter;
import Composestar.Core.DUMMER.DummyManager;
import Composestar.Core.Exception.ModuleException;

/**
 * Defines an accepted programming language/dialect for the current platform. A
 * project is allows associated with at least one language. Depending on the
 * platform different source files might be associated with an other language.
 * 
 * @author Michiel Hendriks
 * @see Platform
 * @see Project
 */
public class Language implements Serializable
{
	private static final long serialVersionUID = 2488167651016666956L;

	/**
	 * The language name.
	 */
	protected String name;

	/**
	 * File extension associated with this language
	 */
	protected Set<String> extensions;

	/**
	 * The compiler information associated with this language.
	 */
	protected SourceCompiler compiler;

	/**
	 * The dummyGenerator's classname
	 */
	protected String dummyGenerator;

	/**
	 * Resolved instance to the dummy generator. It will be resolved on the
	 * first time it is used.
	 */
	protected transient DummyEmitter dummyEmitter;

	public Language(String inName)
	{
		setName(inName);
		extensions = new HashSet<String>();
	}

	public String getName()
	{
		return name;
	}

	/**
	 * Set the name of the language. The name can not be null or empty. The name
	 * may not be changed when the language has been associated with a project.
	 * 
	 * @param inName
	 */
	protected void setName(String inName)
	{
		if (inName == null || inName.trim().length() == 0)
		{
			throw new IllegalArgumentException("Name can not be null or empty");
		}
		name = inName.trim();
	}

	public void addExtension(String ext)
	{
		if (ext == null || ext.trim().length() == 0)
		{
			throw new IllegalArgumentException("Extension can not be null or empty");
		}
		if (ext.startsWith("."))
		{
			// auto trim leading .
			ext = ext.substring(1);
		}
		extensions.add(ext);
	}

	public boolean removeExtension(String ext)
	{
		if (ext == null || ext.trim().length() == 0)
		{
			return false;
		}
		return extensions.remove(ext);
	}

	/**
	 * Get a read only list with all accepted extensions.
	 * 
	 * @return
	 */
	public Set<String> getExtensions()
	{
		return Collections.unmodifiableSet(extensions);
	}

	public void setDummyGenerator(String classname)
	{
		if (classname == null || classname.trim().length() == 0)
		{
			throw new IllegalArgumentException("DummyGenerator can not be null or empty");
		}
		dummyGenerator = classname.trim();
		dummyEmitter = null;
	}

	/**
	 * Get the value of the dummy generator. To get an instance to the dummy
	 * generator use the getDummyEmitter method.
	 * 
	 * @return
	 * @see #getDummyEmitter()
	 */
	public String getDummyGenerator()
	{
		return dummyGenerator;
	}

	public void setCompiler(SourceCompiler comp)
	{
		if (comp == null)
		{
			throw new IllegalArgumentException("Compiler can not be null");
		}
		compiler = comp;
	}

	/**
	 * Get access to the compiler for this language. Used by the DUMMER module
	 * to compile the dummies and my the TYM module to compile the original
	 * sources with against the dummies to produce the pre-weaving assemblies.
	 * 
	 * @return
	 */
	public SourceCompiler getCompiler()
	{
		return compiler;
	}

	/**
	 * Get a reference to the dummy generator/emitter. Used by the DUMMER
	 * module.
	 * 
	 * @return
	 * @throws ModuleException
	 */
	public DummyEmitter getDummyEmitter() throws ModuleException
	{
		if (dummyEmitter == null)
		{
			try
			{
				Class<?> emittedClass = Class.forName(dummyGenerator);
				dummyEmitter = (DummyEmitter) emittedClass.newInstance();
			}
			catch (Exception e)
			{
				throw new ModuleException("Error while instantiating DummyEmitter: " + dummyGenerator,
						DummyManager.MODULE_NAME);
			}
		}
		return dummyEmitter;
	}
}
