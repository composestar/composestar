/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2005-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: Constraint.java 3906 2007-11-01 12:35:06Z elmuerte $
 */
package Composestar.Core.CKRET.Config;

import Composestar.Core.FIRE2.util.regex.Pattern;
import Composestar.Core.FIRE2.util.regex.PatternParseException;
import Composestar.Core.FIRE2.util.regex.RegexPattern;

/**
 * A conflict rule. Either an constraint (must not match) or an assertion (must
 * match).
 */
public class ConflictRule
{
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
		resource = inresource;
		type = intype;
	}

	public ConflictRule(Resource inresource, String patternString, String inmessage, RuleType intype)
			throws PatternParseException
	{
		this(inresource, intype);
		setPattern(patternString);
		setMessage(inmessage);
	}

	public void setPattern(String patternString) throws PatternParseException
	{
		pattern = RegexPattern.compile(patternString);
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

	public void setResource(Resource newresc)
	{
		resource = newresc;
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
}
