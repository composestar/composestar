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

public class LanguageHandler extends DefaultHandler
{
	protected XMLReader parser;

	protected ContentHandler returnHandler;

	protected Language language;

	public LanguageHandler(Language lang, XMLReader inParser, ContentHandler inreturnHandler)
	{
		language = lang;
		parser = inParser;
		returnHandler = inreturnHandler;
	}

	public void startElement(String uri, String localName, String qName, Attributes amap) throws SAXException
	{
		if ("Compiler".equals(qName))
		{
			for (int i = 0; i < amap.getLength(); i++)
			{
				String key = amap.getQName(i);
				String val = amap.getValue(key);
				language.getCompilerSettings().addProperty(key, val);
			}

			// look further
			CompilerHandler comphandler = new CompilerHandler(language, parser, this);
			parser.setContentHandler(comphandler);
		}
		else if ("DummyGeneration".equals(qName))
		{
			if (amap.getValue("emitter") != null)
			{
				language.setEmitter(amap.getValue("emitter"));
			}
		}
		else if ("FileExtensions".equals(qName))
		{
			// look further
			FileExtensionsHandler fexhandler = new FileExtensionsHandler(language, parser, this);
			parser.setContentHandler(fexhandler);
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if ("Language".equals(qName))
		{
			// end <language>
			parser.setContentHandler(returnHandler);
		}
	}
}
