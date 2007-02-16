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

public class SettingsHandler extends DefaultHandler
{
	protected XMLReader parser;

	protected ContentHandler returnHandler;

	public SettingsHandler(XMLReader inParser, ContentHandler documentHandler)
	{
		parser = inParser;
		returnHandler = documentHandler;
	}

	public void startElement(String uri, String localName, String qName, Attributes amap) throws SAXException
	{
		if ("Modules".equals(qName))
		{// in <modules>
			// look further
			ModulesHandler moduleshandler = new ModulesHandler(parser, this);
			parser.setContentHandler(moduleshandler);
		}
		if ("Paths".equals(qName))
		{// in <paths>
			// look further
			PathsHandler pathshandler = new PathsHandler(parser, this);
			parser.setContentHandler(pathshandler);
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if ("Settings".equals(qName))
		{
			// end <settings>
			// System.out.println("end settings");
			parser.setContentHandler(returnHandler);
		}
	}
}
