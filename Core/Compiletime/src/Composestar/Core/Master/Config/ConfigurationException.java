/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.Master.Config;

/**
 * A configuration exception
 *
 * @author Michiel Hendriks
 */
public class ConfigurationException extends Exception
{
	/**
	 * @param message
	 */
	public ConfigurationException(String message)
	{
		super(message);
	}
}
