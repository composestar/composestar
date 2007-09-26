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

import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Master.Config.Configuration;

@Deprecated
public class ModulesHandler extends DefaultHandler
{
	protected XMLReader parser;

	protected ContentHandler returnHandler;

	public ModulesHandler(XMLReader inParser, ContentHandler inReturnHandler)
	{
		parser = inParser;
		returnHandler = inReturnHandler;
	}

	public void startElement(String uri, String localName, String qName, Attributes amap) throws SAXException
	{
		if ("Module".equals(qName))
		{// in <module>
			// look further
			if (amap.getValue("name") != null)
			{
				String name = amap.getValue("name");
				HashMap<String, String> props = new HashMap<String, String>();

				for (int i = 0; i < amap.getLength(); i++)
				{
					String key = amap.getQName(i);
					if ("name".equals(key))
					{
						continue;
					}
					String val = amap.getValue(key);
					props.put(key, val);
				}

				Configuration.instance().addTmpModuleSettings(name, props);
			}
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if ("Modules".equals(qName))
		{
			// end <modules>
			parser.setContentHandler(returnHandler);
		}
	}
}
