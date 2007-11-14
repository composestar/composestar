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

package Composestar.Core.CKRET;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Composestar.Core.CKRET.Config.ConflictRule;
import Composestar.Core.CKRET.Config.OperationSequence;
import Composestar.Core.CKRET.Config.Resource;
import Composestar.Core.FIRE2.util.regex.Labeler;
import Composestar.Core.Resources.ModuleResourceManager;

/**
 * Resource manager for SECRET
 * 
 * @author Michiel Hendriks
 */
public class SECRETResources implements ModuleResourceManager
{
	private static final long serialVersionUID = -1374865313942169590L;

	/**
	 * Resource declarations
	 */
	protected Map<String, Resource> resources;

	/**
	 * Conflict rules to check for.
	 */
	protected Set<ConflictRule> rules;

	/**
	 * Operation sequences extracted from the configuration sources.
	 */
	protected Set<OperationSequence> opSequences;

	/**
	 * The execution model labeler to use
	 */
	protected Labeler labeler;

	public SECRETResources()
	{
		resources = new HashMap<String, Resource>();
		rules = new HashSet<ConflictRule>();
		opSequences = new HashSet<OperationSequence>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.Resources.ModuleResourceManager#getModuleName()
	 */
	public String getModuleName()
	{
		return CKRET.MODULE_NAME;
	}

	public Set<ConflictRule> getRules()
	{
		return Collections.unmodifiableSet(rules);
	}

	public void addRule(ConflictRule rule)
	{
		if (rule == null)
		{
			return;
		}
		rules.add(rule);
	}

	public Collection<Resource> getResources()
	{
		return Collections.unmodifiableCollection(resources.values());
	}

	/**
	 * Adds a resource to the repository. If a resource with the same name
	 * already exists the vocabulary of the new resource will be added to the
	 * existing resource.
	 * 
	 * @param resc
	 * @return false when the new resource was merged with an existing resource.
	 */
	public boolean addResource(Resource resc)
	{
		if (resc == null)
		{
			return false;
		}
		if (resc instanceof MetaResource)
		{
			throw new IllegalArgumentException("Can not add MetaResources to the resource list");
		}
		String key = resc.getName().toLowerCase();
		if (resources.containsKey(key))
		{
			resources.get(key).addVocabulary(resc.getVocabulary());
			return false;
		}
		else
		{
			resources.put(key, resc);
			return true;
		}
	}

	public Resource getResource(String key)
	{
		if (key == null)
		{
			throw new IllegalArgumentException("Key can not be null");
		}
		return resources.get(key.toLowerCase());
	}

	public void addOperationSequence(OperationSequence opesq)
	{
		if (opesq == null)
		{
			return;
		}
		opSequences.add(opesq);
	}

	public Set<OperationSequence> getOperationSequences()
	{
		return Collections.unmodifiableSet(opSequences);
	}

	/**
	 * Get the resource operation labeler
	 * 
	 * @param value
	 */
	public void setLabeler(Labeler value)
	{
		labeler = value;
	}

	public Labeler getLabeler()
	{
		return labeler;
	}
}
