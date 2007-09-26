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
public class RequiredFilesHandler extends DefaultHandler implements ContentHandler
{
	protected XMLReader parser;

	protected ContentHandler returnHandler;

	public RequiredFilesHandler(XMLReader inParser, ContentHandler inReturnHandler)
	{
		parser = inParser;
		returnHandler = inReturnHandler;
	}

	public void startElement(String uri, String localName, String qName, Attributes amap) throws SAXException
	{
		if ("RequiredFile".equals(qName))
		{
			// in <requiredfile>
			Configuration config = Configuration.instance();
			if (amap.getValue("fileName") != null)
			{
				config.getPlatform().addRequiredFile(amap.getValue("fileName"));
			}
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if ("RequiredFiles".equals(qName))
		{
			// end <requiredfiles>
			parser.setContentHandler(returnHandler);
		}
	}
}
