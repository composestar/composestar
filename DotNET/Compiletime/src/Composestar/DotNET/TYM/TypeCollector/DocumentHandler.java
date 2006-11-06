/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.DotNET.TYM.TypeCollector;

import Composestar.DotNET.LAMA.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Main XML document parser. Handles the top level XML structures.
 */
public class DocumentHandler extends DefaultHandler implements ContentHandler
{
	private boolean inDocument = false;

	private XMLReader xr;

	public DocumentHandler(XMLReader parser)
	{
		xr = parser;
	}

	public void startElement(String namespaceURI, String localName, String rawName, Attributes atts)
			throws SAXException
	{
		if (!inDocument && "Types".equals(rawName))
		{
			inDocument = true;
			// doc started
		}
		else if (inDocument && "Type".equals(rawName))
		{
			// create new TypeHandler and install it
			DotNETType type = new DotNETType();
			String fullName = atts.getValue("name");
			if (fullName != null)
			{
				type.setName(fullName);
			}
			else
			{
				throw new SAXNotRecognizedException("Type must have a name attribute");
			}
			// add type to type map

			DotNETTypeHandler typeHandler = new DotNETTypeHandler(type, xr, this);
			xr.setContentHandler(typeHandler);
		}
		else
		{
			throw new SAXNotRecognizedException("Cannot process element " + rawName);
		}
	}

	public void endElement(String namespaceURI, String localName, String rawName) throws SAXException
	{
		if ("Types".equals(rawName))
		{
			inDocument = false;
		}
		else
		{
			throw new SAXNotRecognizedException("Unknown end of document: " + rawName);
		}
	}

	public static void main(String[] args)
	{
		try
		{
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLReader parser = saxParser.getXMLReader();
			DocumentHandler handler = new DocumentHandler(parser);
			parser.setContentHandler(handler);
			parser.parse(new InputSource("types.xml"));
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
}
