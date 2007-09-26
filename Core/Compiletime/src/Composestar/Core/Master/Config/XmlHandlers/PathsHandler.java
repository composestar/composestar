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

@Deprecated
public class PathsHandler extends DefaultHandler
{
	protected XMLReader parser;

	protected ContentHandler returnHandler;

	public PathsHandler(XMLReader inParser, ContentHandler inReturnHandler)
	{
		parser = inParser;
		returnHandler = inReturnHandler;
	}

	public void startElement(String uri, String localName, String qName, Attributes amap) throws SAXException
	{
		if ("Path".equals(qName))
		{// in <path>
			// look further
			if (amap.getValue("name") != null)
			{
				String name = amap.getValue("name");
				String path = amap.getValue("pathName");
				Configuration.instance().getPathSettings().addPath(name, path);
			}
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if ("Paths".equals(qName))
		{
			// end <paths>
			parser.setContentHandler(returnHandler);
		}
	}
}
