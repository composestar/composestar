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

import Composestar.Core.Config.CustomFilter;
import Composestar.Core.Config.FilterAction;
import Composestar.Core.Config.FilterType;
import Composestar.Core.Config.Filters;

/**
 * @author Michiel Hendriks
 */
public class FilterHandler extends DefaultBuildConfigHandler
{
	/**
	 * Processing a &lt;filters&gt; element
	 */
	protected static final int STATE_FILTERS = 1;

	/**
	 * Processing a custom filter element
	 */
	protected static final int STATE_CUSTOM_FILTER = 2;

	/**
	 * Processing a filter type element
	 */
	protected static final int STATE_FILTER_TYPE = 3;

	/**
	 * Processing a filter action element
	 */
	protected static final int STATE_FILTER_ACTION = 4;

	/**
	 * The filters to which the elements are added
	 */
	protected Filters filters;

	/**
	 * Temporary variable used during handling of custom filters
	 */
	protected CustomFilter currentCustomFilter;

	/**
	 * Temporary variable used during filter type handling
	 */
	protected FilterType currentFilterType;

	/**
	 * Temporary variable used during filter action handling.
	 */
	protected FilterAction currentFilterAction;

	/**
	 * @param inReader
	 * @param inParent
	 */
	public FilterHandler(XMLReader inReader, DefaultHandler inParent)
	{
		super(inReader, inParent);
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
		if (state == 0 && "filters".equals(currentName))
		{
			state = STATE_FILTERS;
			filters = config.getFilters();
		}
		else if (state == STATE_FILTERS && "customfilter".equals(currentName))
		{
			state = STATE_CUSTOM_FILTER;
			currentCustomFilter = new CustomFilter();
			currentCustomFilter.setName(attributes.getValue("name"));
		}
		else if (state == STATE_FILTERS && "filtertype".equals(currentName))
		{
			state = STATE_FILTER_TYPE;
			currentFilterType = new FilterType();
			try
			{
				currentFilterType.setName(attributes.getValue("name"));
				currentFilterType.setAcceptCallAction(attributes.getValue("acceptcallaction"));
				currentFilterType.setRejectCallAction(attributes.getValue("rejectcallaction"));
				currentFilterType.setAcceptReturnAction(attributes.getValue("acceptreturnaction"));
				currentFilterType.setRejectReturnAction(attributes.getValue("rejectreturnaction"));
			}
			catch (IllegalArgumentException e)
			{
				throw new SAXParseException(e.getMessage(), locator);
			}
			filters.add(currentFilterType);
		}
		else if (state == STATE_FILTERS && "filteraction".equals(currentName))
		{
			state = STATE_FILTER_ACTION;
			currentFilterAction = new FilterAction();
			try
			{
				currentFilterAction.setName(attributes.getValue("name"));
				currentFilterAction.setFullName(attributes.getValue("fullname"));
				currentFilterAction.setCreateJpc(Boolean.parseBoolean(attributes.getValue("createjpc")));
				String val;
				val = attributes.getValue("flowbehavior");
				if (val != null)
				{
					currentFilterAction.setFlowBehavior(Integer.parseInt(val));
				}
				val = attributes.getValue("messagechangebehavior");
				if (val != null)
				{
					currentFilterAction.setMessageChangeBehavior(Integer.parseInt(val));
				}
			}
			catch (IllegalArgumentException e)
			{
				throw new SAXParseException(e.getMessage(), locator);
			}
		}
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
		if (state == STATE_FILTERS && "filters".equals(name))
		{
			returnHandler();
		}
		else if (state == STATE_CUSTOM_FILTER && "customfilter".equals(name))
		{
			state = STATE_FILTERS;
			String lib = charData.toString();
			if (lib.length() > 0)
			{
				try
				{
					currentCustomFilter.setLibrary(lib);
				}
				catch (IllegalArgumentException e)
				{
					throw new SAXParseException(e.getMessage(), locator);
				}
				filters.add(currentCustomFilter);
			}
			currentCustomFilter = null;

		}
		else if (state == STATE_FILTER_TYPE && "filtertype".equals(name))
		{
			state = STATE_FILTERS;
		}
		else if (state == STATE_FILTER_ACTION && "filteraction".equals(name))
		{
			state = STATE_FILTERS;
			String lib = charData.toString();
			if (lib.length() > 0)
			{
				try
				{
					currentFilterAction.setLibrary(lib);
				}
				catch (IllegalArgumentException e)
				{
					throw new SAXParseException(e.getMessage(), locator);
				}
				filters.add(currentFilterAction);
			}
			currentFilterAction = null;
		}
	}

}
