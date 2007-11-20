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

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Config.BuildConfig;

/**
 * The default handler for the buildconfiguration. It will automatically resolve
 * the reference to the build configuration instance when possible.
 * 
 * @author Michiel Hendriks
 */
public abstract class DefaultBuildConfigHandler extends CpsBaseHandler
{
	public static final String NAMESPACE = "http://composestar.sourceforge.net/schema/BuildConfiguration";

	protected BuildConfig config;

	/**
	 * @param inReader
	 * @param inParent
	 */
	public DefaultBuildConfigHandler(XMLReader inReader, DefaultHandler inParent)
	{
		super(inReader, inParent);
		namespace = NAMESPACE;
		if (inParent instanceof DefaultBuildConfigHandler)
		{
			setBuildConfig(((DefaultBuildConfigHandler) inParent).getBuildConfig());
		}
	}

	public BuildConfig getBuildConfig()
	{
		return config;
	}

	public void setBuildConfig(BuildConfig inConfig)
	{
		config = inConfig;
	}
}
