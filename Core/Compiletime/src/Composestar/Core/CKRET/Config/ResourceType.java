package Composestar.Core.CKRET.Config;

import Composestar.Core.CKRET.MetaResource;

/**
 * The resource type
 * 
 * @author Michiel Hendriks
 */
public enum ResourceType
{
	Unknown, Wildcard, Message, Target, Selector, Return, ArgumentList, ArgumentEntry, Custom;

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
		if (value == null)
		{
			throw new IllegalArgumentException("Value can not be null");
		}
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
		if (value.trim() == "")
		{
			return Unknown;
		}
		return Custom;
	}

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
			return new MetaResource(rt);
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
