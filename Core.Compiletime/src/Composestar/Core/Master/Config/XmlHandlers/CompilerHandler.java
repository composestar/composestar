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

import Composestar.Core.Master.Config.Language;

public class CompilerHandler extends DefaultHandler
{
	protected XMLReader parser;

	protected ContentHandler returnHandler;

	protected Language language;

	public CompilerHandler(Language lang, XMLReader inParser, ContentHandler inReturnHandler)
	{
		language = lang;
		parser = inParser;
		returnHandler = inReturnHandler;
	}

	public void startElement(String uri, String localName, String qName, Attributes amap) throws SAXException
	{
		if ("Actions".equals(qName))
		{
			// in <actions>
			// look further
			ActionsHandler actionshandler = new ActionsHandler(language, parser, this);
			parser.setContentHandler(actionshandler);
		}
		else if ("Converters".equals(qName))
		{
			// in <converters>
			// look further
			ConvertersHandler convhandler = new ConvertersHandler(language, parser, this);
			parser.setContentHandler(convhandler);
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if ("Compiler".equals(qName))
		{
			// end <compiler>
			parser.setContentHandler(returnHandler);
		}
	}
}
