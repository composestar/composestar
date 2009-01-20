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

package Composestar.Utils.Logging;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Level;

/**
 * Redirects a print stream to a CPSLogger
 * 
 * @author Michiel Hendriks
 */
public class OutputStreamRedirector extends OutputStream
{
	/**
	 * The CPSLogger instance to use
	 */
	protected CPSLogger log;

	/**
	 * The log level to use
	 */
	protected Level lev;

	/**
	 * Used for buffer the output
	 */
	protected StringBuffer sb = new StringBuffer();

	public OutputStreamRedirector(CPSLogger logger, Level level)
	{
		super();
		log = logger;
		lev = level;
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException
	{
		if (b == '\n')
		{
			log.log(lev, sb.toString());
			sb = new StringBuffer();
		}
		else if (b > 32)
		{
			sb.append((char) b);
		};
	}
}
