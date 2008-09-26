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

package Composestar.Core.EMBEX;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.ModuleResourceManager;

/**
 * @author Michiel Hendriks
 */
public class EmbeddedSources implements ModuleResourceManager
{
	private static final long serialVersionUID = -8495010262687130879L;

	/**
	 * The embedded sources
	 */
	protected Set<EmbeddedSource> sources;

	/**
	 * 
	 */
	public EmbeddedSources()
	{
		sources = new HashSet<EmbeddedSource>();
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Resources.ModuleResourceManager#getModuleName()
	 */
	public String getModuleName()
	{
		return ModuleNames.EMBEX;
	}

	/**
	 * Add a new embedded source
	 * 
	 * @param src
	 */
	public void addSource(EmbeddedSource src)
	{
		sources.add(src);
	}

	/**
	 * @return
	 */
	public Collection<EmbeddedSource> getSources()
	{
		return Collections.unmodifiableCollection(sources);
	}
}
