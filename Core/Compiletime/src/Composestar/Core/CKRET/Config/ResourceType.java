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
package Composestar.Core.CKRET.Config;

/**
 * The resource type
 * 
 * @author Michiel Hendriks
 */
public enum ResourceType
{
	/**
	 * An unknown/unset type
	 */
	Unknown,
	/**
	 * A special type only allowed in rule definitions. A rule with this
	 * resource type applies to all resources.
	 */
	Wildcard
	{
		@Override
		public String toString()
		{
			return "*";
		}
	},
	Message
	{
		@Override
		public String toString()
		{
			return "message";
		}
	},
	Target
	{
		@Override
		public String toString()
		{
			return "target";
		}
	},
	Selector
	{
		@Override
		public String toString()
		{
			return "selector";
		}
	},
	Return
	{
		@Override
		public String toString()
		{
			return "return";
		}
	},
	ArgumentList
	{
		@Override
		public String toString()
		{
			return "args";
		}
	},
	ArgumentEntry
	{
		@Override
		public String toString()
		{
			return "arg";
		}
	},
	/**
	 * A custom type. The real name is stored in a CustomResource instance
	 */
	Custom;

	/**
	 * Returns true when this resource is a meta resource type like Unknown or
	 * Wildcard
	 * 
	 * @return
	 */
	public boolean isMeta()
	{
		return this == Unknown || this == Wildcard;
	}

	public static ResourceType parse(String value)
	{
		if (value == null || value.trim().length() == 0)
		{
			throw new IllegalArgumentException("Value can not be null or empty");
		}
		value = value.trim();
		if ("msg".equalsIgnoreCase(value) || "message".equalsIgnoreCase(value))
		{
			return Message;
		}
		if ("target".equalsIgnoreCase(value))
		{
			return Target;
		}
		if ("selector".equalsIgnoreCase(value))
		{
			return Selector;
		}
		if ("return".equalsIgnoreCase(value))
		{
			return Return;
		}
		if ("args".equalsIgnoreCase(value) || "arglist".equalsIgnoreCase(value))
		{
			return ArgumentList;
		}
		if (value.toLowerCase().startsWith("arg"))
		{
			return ArgumentEntry;
		}
		if (value.toLowerCase().startsWith("*"))
		{
			return Wildcard;
		}
		if (value.trim().length() == 0)
		{
			return Unknown;
		}
		return Custom;
	}

	private static Resource wildcardResource;

	public static Resource createResource(String name, boolean allowWildcard)
	{
		ResourceType rt;
		Resource resc;
		try
		{
			rt = ResourceType.parse(name);
		}
		catch (IllegalArgumentException e)
		{
			throw new IllegalArgumentException(String.format("Not a valid resource name: %s", name));
		}
		if (allowWildcard && rt == Wildcard)
		{
			if (wildcardResource == null)
			{
				wildcardResource = new MetaResource(rt);
			}
			return wildcardResource;
		}
		if (rt.isMeta())
		{
			throw new IllegalArgumentException(String.format("Not a valid resource name: %s", name));
		}
		if (rt == ResourceType.Custom)
		{
			resc = new CustomResource(name);
		}
		else
		{
			resc = new Resource(rt);
		}

		return resc;
	}
}
