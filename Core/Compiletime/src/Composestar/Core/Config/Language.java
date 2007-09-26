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
 * @author Michiel Hendriks
 */
public class Language implements Serializable
{
	private static final long serialVersionUID = 2488167651016666956L;

	/**
	 * The language name
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
	}

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

	public SourceCompiler getCompiler()
	{
		return compiler;
	}

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
