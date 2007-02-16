/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.Master.Config.XmlHandlers;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.CustomFilter;

public class CustomFiltersHandler extends DefaultHandler
{
	protected XMLReader parser;

	protected ContentHandler returnHandler;

	public CustomFiltersHandler(XMLReader inParser, ContentHandler inReturnHandler)
	{
		parser = inParser;
		returnHandler = inReturnHandler;
	}

	public void startElement(String uri, String localName, String qName, Attributes amap) throws SAXException
	{
		if ("Filter".equals(qName))
		{// in <path>
			// look further
			if (amap.getValue("filtername") != null && amap.getValue("library") != null)
			{
				String name = amap.getValue("filterName");
				String path = amap.getValue("library");
				CustomFilter cf = new CustomFilter();
				cf.setFilter(name);
				cf.setLibrary(path);
				Configuration.instance().getFilters().addCustomFilters(cf);
			}
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if ("CustomFilters".equals(qName))
		{
			// end <paths>
			parser.setContentHandler(returnHandler);
		}
	}
}
