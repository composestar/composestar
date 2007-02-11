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

public class FileExtensionsHandler extends DefaultHandler
{
	protected XMLReader parser;

	protected ContentHandler returnHandler;

	protected Language language;

	public FileExtensionsHandler(Language lang, XMLReader inParser, ContentHandler inReturnHandler)
	{
		language = lang;
		parser = inParser;
		returnHandler = inReturnHandler;
	}

	public void startElement(String uri, String localName, String qName, Attributes amap) throws SAXException
	{
		if ("FileExtension".equals(qName))
		{
			// in <fileextension>
			if (amap.getValue("extension") != null)
			{
				language.addExtension(amap.getValue("extension"));
			}
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if ("FileExtensions".equals(qName))
		{
			// end <fileextensions>
			parser.setContentHandler(returnHandler);
		}
	}
}
