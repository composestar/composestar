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

package Composestar.Core.SECRET3.Config.Xml;

import java.util.HashSet;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Config.Xml.CpsBaseHandler;
import Composestar.Core.SECRET3.SECRETResources;
import Composestar.Core.SECRET3.Model.Resource;

/**
 * Processes the &lt;resource&gt; element in the SECRET configuration.
 * 
 * @author Michiel Hendriks
 */
public class ResourceHandler extends CpsBaseHandler
{
	protected static final int STATE_RESC = 1;

	protected SECRETResources resources;

	/**
	 * The creates resource type
	 */
	protected Resource resc;

	protected String rescName;

	protected Set<String> aliases;

	protected Set<String> vocab;

	/**
	 * Set to true when this resource instance replaces a previously defined
	 * resource
	 */
	protected boolean replaceResc;

	/**
	 * @param inReader
	 * @param inParent
	 */
	public ResourceHandler(XMLReader inReader, DefaultHandler inParent, SECRETResources sresc)
	{
		super(inReader, inParent);
		namespace = XmlConfiguration.NAMESPACE;
		resources = sresc;
	}

	/**
	 * @return get the newly created resource
	 */
	public Resource getRecource()
	{
		return resc;
	}

	/**
	 * @return if true this resource should replace the previously defined
	 *         resource
	 */
	public boolean replaceResource()
	{
		return replaceResc;
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
		if (state == 0 && "resource".equals(currentName))
		{
			state = STATE_RESC;
			resc = null;
			rescName = attributes.getValue("name");
			if (rescName != null)
			{
				rescName = rescName.trim();
			}
			if (!Resource.isValidName(rescName))
			{
				throw new SAXParseException(String.format("Invalid resource name: %s", rescName), locator);
			}
			aliases = new HashSet<String>();
			vocab = new HashSet<String>();
			replaceResc = Boolean.parseBoolean(attributes.getValue("override"));
		}
		else if (state == STATE_RESC && "operation".equals(currentName))
		{
			// nop
		}
		else if (state == STATE_RESC && "alias".equals(currentName))
		{
			String alias = attributes.getValue("name");
			if (alias != null)
			{
				alias = alias.trim();
			}
			if (!Resource.isValidName(alias))
			{
				throw new SAXParseException(String.format("Invalid resource name: %s", alias), locator);
			}
			aliases.add(alias);
		}
		else
		{
			startUnknownElement(uri, localName, name, attributes);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String name) throws SAXException
	{
		super.endElement(uri, localName, name);
		if (state == STATE_RESC && "resource".equals(currentName))
		{
			resc = new Resource(rescName, aliases);
			if (replaceResc)
			{
				Resource r2 = resources.getResource(resc.getName());
				r2.clearVocabulary();
			}
			// will automatically merge if needed
			resc.addVocabulary(vocab);
			resources.addResource(resc);
			vocab = null;
			aliases = null;
			returnHandler();
		}
		else if (state == STATE_RESC && "operation".equals(currentName))
		{
			String w = charData.toString().trim();
			if (w.length() > 0)
			{
				vocab.add(w);
			}
		}
		else if (state == STATE_RESC && "alias".equals(currentName))
		{
			// nop
		}
		else
		{
			endUnknownElement(uri, localName, name);
		}
	}

}
