/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2005-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CKRET.Config;

import java.io.Serializable;

import Composestar.Core.FIRE2.util.regex.Pattern;
import Composestar.Core.FIRE2.util.regex.PatternParseException;
import Composestar.Core.FIRE2.util.regex.RegularPattern;

/**
 * A conflict rule. Either an constraint (must not match) or an assertion (must
 * match).
 */
public class ConflictRule implements Serializable
{
	/**
	 * A predefine conflict pattern that defines that a value may never be
	 * written twice before being read
	 */
	public static final String PATTERN_NO_WRITE_WRITE_READ = "(write)(![write,read]*(write)![write,read]*)+(read)";

	public enum RuleType
	{
		/**
		 * A constraint must not match any path
		 */
		Constraint,
		/**
		 * An assertion must match part of a graph
		 */
		Assertion
	}

	private static final long serialVersionUID = -3896746915950405852L;

	/**
	 * The resource name
	 */
	private Resource resource;

	/**
	 * The (compiled) pattern
	 */
	private Pattern pattern;

	/**
	 * A friendly message to use in the report
	 */
	private String message = "";

	/**
	 * The type of conflict
	 */
	private RuleType type;

	public ConflictRule(Resource inresource, RuleType intype)
	{
		if (inresource == null)
		{
			throw new IllegalArgumentException("Resource can not be null");
		}
		if (intype == null)
		{
			throw new IllegalArgumentException("RuleType can not be null");
		}
		resource = inresource;
		type = intype;
	}

	public ConflictRule(Resource inresource, RuleType intype, String patternString, String inmessage)
			throws PatternParseException
	{
		this(inresource, intype);
		setPattern(patternString);
		setMessage(inmessage);
	}

	public void setPattern(String patternString) throws PatternParseException
	{
		pattern = RegularPattern.compile(patternString);
	}

	public void setMessage(String msg)
	{
		if (msg == null)
		{
			message = "";
		}
		else
		{
			message = msg;
		}
	}

	/**
	 * @see #pattern
	 * @return
	 */
	public Pattern getPattern()
	{
		return pattern;
	}

	/**
	 * @see #resource
	 * @return
	 */
	public Resource getResource()
	{
		return resource;
	}

	/**
	 * @see #message
	 * @return
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * @see #type
	 * @return
	 */
	public RuleType getType()
	{
		return type;
	}

	@Override
	public String toString()
	{
		return String.format("%s on resource %s with pattern \"%s\". Reason: %s", type.toString(), resource.getName(),
				pattern.toString(), message);
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
		result = prime * result + (pattern == null ? 0 : pattern.getPatternString().hashCode());
		result = prime * result + (resource == null ? 0 : resource.hashCode());
		result = prime * result + (type == null ? 0 : type.hashCode());
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
		final ConflictRule other = (ConflictRule) obj;
		if (pattern == null)
		{
			if (other.pattern != null)
			{
				return false;
			}
		}
		else if (!pattern.getPatternString().equals(other.pattern.getPatternString()))
		{
			return false;
		}
		if (resource == null)
		{
			if (other.resource != null)
			{
				return false;
			}
		}
		else if (!resource.equals(other.resource))
		{
			return false;
		}
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
