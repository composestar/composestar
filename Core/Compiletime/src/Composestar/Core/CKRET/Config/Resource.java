/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2004-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CKRET.Config;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Michiel Hendriks
 */
public class Resource implements Serializable
{
	private static final long serialVersionUID = -7687830358361188988L;

	protected ResourceType type;

	protected Set<String> vocabulary;

	protected Resource()
	{}

	public Resource(ResourceType intype)
	{
		if (intype.isMeta())
		{
			throw new IllegalArgumentException("ResourceType must be a valid resource, meta types are not allowed.");
		}
		type = intype;
		vocabulary = new HashSet<String>();
	}

	public Resource(ResourceType intype, Collection<String> vocab)
	{
		this(intype);
		addVocabulary(vocab);
	}

	public Resource(ResourceType intype, String... vocab)
	{
		this(intype);
		addVocabulary(Arrays.asList(vocab));
	}

	public String getName()
	{
		return type.toString();
	}

	public ResourceType getType()
	{
		return type;
	}

	public Set<String> getVocabulary()
	{
		return Collections.unmodifiableSet(vocabulary);
	}

	public void clearVocabulary()
	{
		vocabulary.clear();
	}

	public void addVocabulary(String word)
	{
		vocabulary.add(word);
	}

	public void addVocabulary(Collection<String> words)
	{
		vocabulary.addAll(words);
	}

	@Override
	public String toString()
	{
		return getName() + vocabulary.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final Resource other = (Resource) obj;
		if (type == null)
		{
			if (other.type != null)
			{
				return false;
			}
		}
		else if (!type.equals(other.type))
		{
			return false;
		}
		return true;
	}
}
