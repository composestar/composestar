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

import Composestar.Core.Master.Config.ConcernSource;
import Composestar.Core.Master.Config.Configuration;

@Deprecated
public class ConcernSourcesHandler extends DefaultHandler
{
	protected XMLReader parser;

	protected ContentHandler returnHandler;

	public ConcernSourcesHandler(XMLReader inParser, ContentHandler inDocumentHandler)
	{
		parser = inParser;
		returnHandler = inDocumentHandler;
	}

	public void startElement(String uri, String localName, String qName, Attributes amap) throws SAXException
	{
		if ("ConcernSource".equals(qName))
		{
			// in <concernsource>
			ConcernSource concernsource = new ConcernSource();
			if (amap.getValue("fileName") != null)
			{
				concernsource.setFileName(amap.getValue("fileName"));
				Configuration.instance().getProjects().addConcernSource(concernsource);
			}
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if ("ConcernSources".equals(qName))
		{
			// end <concernsources>
			parser.setContentHandler(returnHandler);
		}
	}
}
