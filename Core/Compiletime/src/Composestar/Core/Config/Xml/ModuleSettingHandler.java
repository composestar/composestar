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
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Config.ModuleSetting;
import Composestar.Core.Exception.ConfigurationException;

/**
 * @author Michiel Hendriks
 */
public class ModuleSettingHandler extends CpsBaseHandler
{
	protected static final int STATE_SETTING = 1;

	protected static final int STATE_NAME = 2;

	protected static final int STATE_DEFAULT = 3;

	protected ModuleSetting ms;

	/**
	 * @param inReader
	 * @param inParent
	 */
	public ModuleSettingHandler(XMLReader inReader, DefaultHandler inParent)
	{
		super(inReader, inParent);
	}

	public ModuleSetting getModuleSetting()
	{
		return ms;
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
	{
		try
		{
			super.startElement(uri, localName, name, attributes);
			if (state == 0 && "setting".equals(name))
			{
				state = STATE_SETTING;

				String id = attributes.getValue("id");
				Class<?> stype = String.class;
				if (attributes.getValue("type") != null)
				{
					String prefType = attributes.getValue("type");
					if (prefType.equalsIgnoreCase("integer") || prefType.equalsIgnoreCase("int"))
					{
						prefType = Integer.class.getName();
					}
					else if (prefType.equalsIgnoreCase("boolean") || prefType.equalsIgnoreCase("bool"))
					{
						prefType = Boolean.class.getName();
					}
					else if (prefType.equalsIgnoreCase("string"))
					{
						prefType = String.class.getName();
					}
					else if (prefType.equalsIgnoreCase("float"))
					{
						prefType = Float.class.getName();
					}
					try
					{
						stype = Class.forName(prefType);
					}
					catch (ClassNotFoundException e)
					{
						throw new SAXParseException(String.format("Invalid configuration type %s", prefType), locator);
					}
				}
				try
				{
					ms = new ModuleSetting(id, stype);
				}
				catch (ConfigurationException e)
				{
					throw new SAXParseException(String.format("Unable to create module setting: %s", e.toString()),
							locator);
				}
			}
			else if (state == STATE_SETTING && "name".equals(name))
			{
				state = STATE_NAME;
			}
			else if (state == STATE_SETTING && "default".equals(name))
			{
				state = STATE_DEFAULT;
			}
			else
			{
				startUnknownElement(uri, localName, name, attributes);
			}
		}
		catch (IllegalArgumentException e)
		{
			throw new SAXParseException(e.getMessage(), locator);
		}
	}

	@Override
	public void endElement(String uri, String localName, String name) throws SAXException
	{
		try
		{
			super.endElement(uri, localName, name);
			if (state == STATE_SETTING && "setting".equals(name))
			{
				returnHandler(uri, localName, name);
			}
			else if (state == STATE_NAME && "name".equals(name))
			{
				state = STATE_SETTING;
				ms.setName(charData.toString());
			}
			else if (state == STATE_DEFAULT && "default".equals(name))
			{
				state = STATE_SETTING;
				ms.setDefault(charData.toString());
			}
			else
			{
				endUnknownElement(uri, localName, name);
			}
		}
		catch (IllegalArgumentException e)
		{
			throw new SAXParseException(e.getMessage(), locator);
		}
	}

}
