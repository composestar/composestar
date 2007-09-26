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

package Composestar.Core.Config.Xml;

import org.xml.sax.SAXException;

/**
 * Exception thrown when the read configuration file format is not up to date.
 * The calling system should convert the the system first.
 * 
 * @author Michiel Hendriks
 */
public class OutdatedFormatException extends SAXException
{
	private static final long serialVersionUID = -4043837721201726057L;

	/**
	 * The version that was detected
	 */
	protected String detectedVersion;

	/**
	 * The version that is required.
	 */
	protected String needVersion;

	public OutdatedFormatException(String detected, String need)
	{
		detectedVersion = detected;
		needVersion = need;
	}

	public String getDetectedVersion()
	{
		return detectedVersion;
	}

	public String getNeedVersion()
	{
		return needVersion;
	}

	public String toString()
	{
		return String.format("Outdated Format Exception. Need %s; but detected %s", needVersion, detectedVersion);
	}
}
