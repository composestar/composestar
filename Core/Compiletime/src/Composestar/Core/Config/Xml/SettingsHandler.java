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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Processes the settings element in the build configuration.
 * 
 * @author Michiel Hendriks
 */
public class SettingsHandler extends DefaultBuildConfigHandler
{
	/**
	 * The id of the settings currently being processed
	 */
	protected String curSetting;

	/**
	 * @param inReader
	 * @param inParent
	 */
	public SettingsHandler(XMLReader inReader, DefaultHandler inParent)
	{
		super(inReader, inParent);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.Config.Xml.CpsBaseHandler#endElement(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String name) throws SAXException
	{
		super.endElement(uri, localName, name);
		if ("settings".equals(currentName))
		{
			returnHandler();
		}
		else if ("setting".equals(currentName))
		{
			if (curSetting != null)
			{
				config.addSetting(curSetting.trim(), charData.toString());
			}
		}
		else
		{
			endUnknownElement(uri, localName, name);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.Config.Xml.CpsBaseHandler#startElement(java.lang.String,
	 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
	{
		super.startElement(uri, localName, name, attributes);
		if ("settings".equals(currentName))
		{
			config.clearSettings();
		}
		else if ("setting".equals(currentName))
		{
			curSetting = attributes.getValue("name");
		}
		else
		{
			startUnknownElement(uri, localName, name, attributes);
		}
	}
}
