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

import Composestar.Core.Master.Config.CompilerConverter;
import Composestar.Core.Master.Config.Language;

@Deprecated
public class ConvertersHandler extends DefaultHandler
{
	protected XMLReader parser;

	protected ContentHandler returnHandler;

	protected Language language;

	public ConvertersHandler(Language lang, XMLReader parser, ContentHandler returnHandler)
	{
		this.language = lang;
		this.parser = parser;
		this.returnHandler = returnHandler;
	}

	public void startElement(String uri, String localName, String qName, Attributes amap) throws SAXException
	{
		if ("Converter".equals(qName))
		{
			// end <converter>
			if (amap.getValue("name") != null)
			{
				String name = amap.getValue("name");
				String replaceBy = amap.getValue("replaceBy");
				String expression = amap.getValue("expression");
				CompilerConverter converter = new CompilerConverter();
				converter.setName(name);
				converter.setReplaceBy(replaceBy);
				converter.setExpression(expression);
				language.getCompilerSettings().addCompilerConverter(converter);
			}
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if ("Converters".equals(qName))
		{
			// end <converter>
			parser.setContentHandler(returnHandler);
		}
	}
}
