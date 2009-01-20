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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Config.Xml.CpsBaseHandler;
import Composestar.Core.SECRET3.SECRETResources;
import Composestar.Core.SECRET3.Config.OperationSequence;
import Composestar.Core.SECRET3.Config.Resource;
import Composestar.Core.SECRET3.Config.ResourceType;

/**
 * Processes the &lt;action&gt; element in the SECRET configuration.
 * 
 * @author Michiel Hendriks
 */
public class ActionHandler extends CpsBaseHandler
{
	/**
	 * Currently processing an action definition
	 */
	protected static final int STATE_ACTION = 1;

	/**
	 * Current processing a label
	 */
	protected static final int STATE_LABEL = 1;

	/**
	 * Processing an operation sequence.
	 */
	protected static final int STATE_SEQUENCE = 3;

	protected SECRETResources resources;

	/**
	 * The newly created action
	 */
	protected OperationSequence action;

	/**
	 * Temporary storage for the label type which is stored in the attribute
	 * list
	 */
	protected String currentLt;

	/**
	 * A reference to the resource that is used in the current operation
	 * sequence definition.
	 */
	protected Resource currentResource;

	/**
	 * @param inReader
	 * @param inParent
	 */
	public ActionHandler(XMLReader inReader, DefaultHandler inParent, SECRETResources resc)
	{
		super(inReader, inParent);
		namespace = XmlConfiguration.NAMESPACE;
		resources = resc;
	}

	/**
	 * @return the created action instance
	 */
	public OperationSequence getAction()
	{
		return action;
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
		if (state == 0 && "action".equals(currentName))
		{
			state = STATE_ACTION;
			action = new OperationSequence();
			try
			{
				action.setPriority(Integer.parseInt(attributes.getValue("priority")));
			}
			catch (NumberFormatException e)
			{
				action.setPriority(0);
			}
		}
		else if (state == STATE_ACTION && "label".equals(currentName))
		{
			state = STATE_LABEL;
			currentLt = attributes.getValue("type");
		}
		else if (state == STATE_ACTION && "sequence".equals(currentName))
		{
			state = STATE_SEQUENCE;
			try
			{
				String rescName = attributes.getValue("resource");
				ResourceType rescType = ResourceType.parse(rescName);
				if (rescType == ResourceType.Custom)
				{
					currentResource = resources.getResource(rescName.trim());
				}
				else if (!rescType.isMeta())
				{
					currentResource = resources.getResource(rescType.toString());
				}

				if (currentResource == null)
				{
					currentResource = ResourceType.createResource(rescName, false);
					if (!currentResource.getType().isMeta())
					{
						resources.addResource(currentResource);
					}
				}
			}
			catch (IllegalArgumentException e)
			{
				throw new SAXParseException(e.toString(), locator);
			}
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
		if (state == STATE_ACTION && "action".equals(currentName))
		{
			if (action.getOperations().size() > 0 && action.getLabels().size() > 0)
			{
				resources.addOperationSequence(action);
			}
			returnHandler();
		}
		else if (state == STATE_LABEL && "label".equals(currentName))
		{
			state = STATE_ACTION;
			try
			{
				action.addLabel(charData.toString().trim(), currentLt);
			}
			catch (IllegalArgumentException e)
			{
				throw new SAXParseException(e.getMessage(), locator, e);
			}
		}
		else if (state == STATE_SEQUENCE && "sequence".equals(currentName))
		{
			state = STATE_ACTION;
			try
			{
				action.addOperations(currentResource, charData.toString().trim());
			}
			catch (IllegalArgumentException e)
			{
				throw new SAXParseException(e.getMessage(), locator, e);
			}
		}
		else
		{
			endUnknownElement(uri, localName, name);
		}
	}

}
