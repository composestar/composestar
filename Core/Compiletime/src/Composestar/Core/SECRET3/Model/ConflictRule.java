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

import java.io.Serializable;

import Composestar.Core.FIRE2.util.regex.Pattern;
import Composestar.Core.FIRE2.util.regex.PatternParseException;
import Composestar.Core.FIRE2.util.regex.RegularPattern;

/**
 * @author Michiel Hendriks
 */
public class ConflictRule implements Serializable
{
	private static final long serialVersionUID = 2793013419251949944L;

	/**
	 * A predefine conflict pattern that defines that a value may never be
	 * written twice before being read
	 */
	public static final String PATTERN_NO_WRITE_WRITE_READ = "(write)(![write,read]*(write)![write,read]*)+(read)";

	/**
	 * The resource this rule applies to
	 */
	protected Resource resource;

	/**
	 * The (compiled) pattern
	 */
	protected Pattern pattern;

	/**
	 * The type of rule (constraint, assertion, ...)
	 */
	protected RuleType type;

	/**
	 * A message to show when the rule is violated
	 */
	protected String message;

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

	/**
	 * @see #pattern
	 * @param patternString
	 * @throws PatternParseException
	 */
	public void setPattern(String patternString) throws PatternParseException
	{
		pattern = RegularPattern.compile(patternString);
	}

	/**
	 * @see #message
	 * @param msg
	 */
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
}
