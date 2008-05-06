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

package Composestar.Core.CKRET.Config.Xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.CKRET.SECRETResources;
import Composestar.Core.CKRET.Config.ConflictRule;
import Composestar.Core.CKRET.Config.Resource;
import Composestar.Core.CKRET.Config.ResourceType;
import Composestar.Core.CKRET.Config.ConflictRule.RuleType;
import Composestar.Core.Config.Xml.CpsBaseHandler;
import Composestar.Core.FIRE2.util.regex.PatternParseException;

/**
 * Processes XML &lt;rule&gt; declarations in the SECRET configuration
 * 
 * @author Michiel Hendriks
 */
public class RuleHandler extends CpsBaseHandler
{
	protected static final int STATE_RULE = 1;

	protected SECRETResources resources;

	/**
	 * The created conflict rule instance
	 */
	protected ConflictRule rule;

	/**
	 * @param inReader
	 * @param inParent
	 */
	public RuleHandler(XMLReader inReader, DefaultHandler inParent, SECRETResources resc)
	{
		super(inReader, inParent);
		namespace = XmlConfiguration.NAMESPACE;
		resources = resc;
	}

	/**
	 * @return the created rule instance
	 */
	public ConflictRule getRule()
	{
		return rule;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.Config.Xml.CpsBaseHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
	{
		super.startElement(uri, localName, name, attributes);
		if (state == 0 && "rule".equals(currentName))
		{
			state = STATE_RULE;
			ConflictRule.RuleType rt = RuleType.Constraint;
			if ("assertion".equalsIgnoreCase(attributes.getValue("type")))
			{
				rt = RuleType.Assertion;
			}

			Resource resc = null;
			try
			{
				String rescName = attributes.getValue("resource");
				ResourceType rescType = ResourceType.parse(rescName);
				if (rescType == ResourceType.Custom)
				{
					resc = resources.getResource(rescName.trim());
				}
				else if (!rescType.isMeta())
				{
					resc = resources.getResource(rescType.toString());
				}

				if (resc == null)
				{
					resc = ResourceType.createResource(rescName, true);
					if (!resc.getType().isMeta())
					{
						resources.addResource(resc);
					}
				}
			}
			catch (IllegalArgumentException e)
			{
				throw new SAXParseException(e.toString(), locator);
			}

			rule = new ConflictRule(resc, rt);
		}
		else if (state == STATE_RULE && "pattern".equals(currentName))
		{
			// nop
		}
		else if (state == STATE_RULE && "message".equals(currentName))
		{
			// nop
		}
		else if (state == STATE_RULE && "scope".equals(currentName))
		{
			// FIXME: implement
		}
		else
		{
			startUnknownElement(uri, localName, name, attributes);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String name) throws SAXException
	{
		super.endElement(uri, localName, name);
		if (state == STATE_RULE && "rule".equals(currentName))
		{
			if (rule.getPattern() == null)
			{
				throw new SAXParseException("Rule does not have a pattern", locator);
			}
			resources.addRule(rule);
			returnHandler();
		}
		else if (state == STATE_RULE && "pattern".equals(currentName))
		{
			try
			{
				rule.setPattern(charData.toString().trim());
			}
			catch (PatternParseException e)
			{
				throw new SAXParseException(String.format("Invalid pattern: %s", e.toString()), locator, e);
			}
		}
		else if (state == STATE_RULE && "message".equals(currentName))
		{
			rule.setMessage(charData.toString().trim());
		}
		else if (state == STATE_RULE && "scope".equals(currentName))
		{
			// FIXME: implement
		}
		else
		{
			endUnknownElement(uri, localName, name);
		}
	}
}
