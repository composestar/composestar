/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Utils.Logging;

/**
 * LoggerFactory to create the most suitable ILogger instance.
 * 
 * @author Michiel Hendriks
 */
public interface ILoggerFactory
{
	public ILogger createLogger(String name);
}
