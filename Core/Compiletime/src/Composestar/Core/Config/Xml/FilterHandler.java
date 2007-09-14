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

import Composestar.Core.Config.CustomFilter;
import Composestar.Core.Config.FilterAction;
import Composestar.Core.Config.FilterType;
import Composestar.Core.Config.Filters;

/**
 * @author Michiel Hendriks
 */
public class FilterHandler extends DefaultBuildConfigHandler
{
	protected final static int STATE_FILTERS = 1;

	protected final static int STATE_CUSTOM_FILTER = 2;

	protected final static int STATE_FILTER_TYPE = 3;

	protected final static int STATE_FILTER_ACTION = 4;

	protected Filters filters;

	protected CustomFilter currentCustomFilter;

	protected FilterType currentFilterType;

	protected FilterAction currentFilterAction;

	/**
	 * @param inReader
	 * @param inParent
	 */
	public FilterHandler(XMLReader inReader, DefaultHandler inParent)
	{
		super(inReader, inParent);
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
	{
		super.startElement(uri, localName, name, attributes);
		if (state == 0 && "filters".equals(name))
		{
			state = STATE_FILTERS;
			filters = config.getFilters();
		}
		else if (state == STATE_FILTERS && "customfilter".equals(name))
		{
			state = STATE_CUSTOM_FILTER;
			currentCustomFilter.setName(attributes.getValue("name"));
		}
		else if (state == STATE_FILTERS && "filtertype".equals(name))
		{
			state = STATE_FILTER_TYPE;
		}
		else if (state == STATE_FILTERS && "filteraction".equals(name))
		{
			state = STATE_FILTER_ACTION;
		}
	}

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
				currentCustomFilter.setLibrary(lib);
				// filters.add(...)
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
		}
	}

}
