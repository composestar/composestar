/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2009 University of Twente.
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

package Composestar.Core.SECRET3.Model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author Michiel Hendriks
 */
public class Resource
{
	protected final static Pattern RESC_NAME = Pattern.compile("[A-Za-z][A-Za-z0-9]*(.[A-Za-z][A-Za-z0-9]*)?");

	/**
	 * The name of the resource.
	 */
	protected String name;

	/**
	 * Aliases of this resource.
	 */
	protected Set<String> alias;

	/**
	 * Known operations of this resource.
	 */
	protected Set<String> vocabulary;

	public final static boolean isValidName(String name)
	{
		return RESC_NAME.matcher(name).matches();
	}

	protected Resource()
	{
		alias = new HashSet<String>();
		vocabulary = new HashSet<String>();
	}

	public Resource(String resourceName)
	{
		this();
		if (resourceName == null)
		{
			throw new NullPointerException("Resource name cannot be null");
		}
		if (resourceName.trim().length() == 0)
		{
			throw new IllegalArgumentException("Resource name cannot be empty");
		}
		name = resourceName.trim().toLowerCase();
	}

	public Resource(String resourceName, String... aliases)
	{
		this(resourceName, aliases == null ? null : Arrays.asList(aliases));
	}

	public Resource(String resourceName, Collection<String> aliases)
	{
		this(resourceName);
		if (aliases != null)
		{
			for (String als : aliases)
			{
				if (als == null || als.trim().length() == 0)
				{
					continue;
				}
				alias.add(als.trim().toLowerCase());
			}
		}
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return the alias
	 */
	public Set<String> getAliases()
	{
		return Collections.unmodifiableSet(alias);
	}

	/**
	 * @return the vocabulary
	 */
	public Set<String> getVocabulary()
	{
		return Collections.unmodifiableSet(vocabulary);
	}

	/**
	 * Empty the vocabulary
	 */
	public void clearVocabulary()
	{
		vocabulary.clear();
	}

	/**
	 * Add a single word to the vocabulary
	 * 
	 * @param word
	 */
	public void addVocabulary(String word)
	{
		vocabulary.add(word);
	}

	/**
	 * Add a set of operations to the vocabulary
	 * 
	 * @param words
	 */
	public void addVocabulary(String... words)
	{
		if (words != null)
		{
			addVocabulary(Arrays.asList(words));
		}
	}

	/**
	 * Add a set of operations to the vocabulary
	 * 
	 * @param words
	 */
	public void addVocabulary(Collection<String> words)
	{
		vocabulary.addAll(words);
	}

	/**
	 * @return
	 */
	public boolean isWildcard()
	{
		return false;
	}
}
