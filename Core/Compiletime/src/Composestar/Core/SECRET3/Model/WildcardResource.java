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

import java.util.Collection;

/**
 * @author Michiel Hendriks
 */
public final class WildcardResource extends Resource
{
	protected static WildcardResource inst;

	/**
	 * The name for the wildcard
	 */
	public static final String WILDCARD = "*";

	public static final WildcardResource instance()
	{
		if (inst == null)
		{
			inst = new WildcardResource();
		}
		return inst;
	}

	protected WildcardResource()
	{
		name = WILDCARD;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.SECRET3.Model.Resource#isWildcard()
	 */
	@Override
	public boolean isWildcard()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.SECRET3.Model.Resource#addVocabulary(java.util.Collection
	 * )
	 */
	@Override
	public void addVocabulary(Collection<String> words)
	{}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.SECRET3.Model.Resource#addVocabulary(java.lang.String)
	 */
	@Override
	public void addVocabulary(String word)
	{}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.SECRET3.Model.Resource#addVocabulary(java.lang.String[])
	 */
	@Override
	public void addVocabulary(String... words)
	{}
}
