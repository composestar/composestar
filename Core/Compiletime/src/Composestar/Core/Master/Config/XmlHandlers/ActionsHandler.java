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

import Composestar.Core.Master.Config.CompilerAction;
import Composestar.Core.Master.Config.Language;

@Deprecated
public class ActionsHandler extends DefaultHandler
{
	protected XMLReader parser;

	protected ContentHandler returnHandler;

	protected Language language;

	public ActionsHandler(Language lang, XMLReader inParser, ContentHandler inReturnHandler)
	{
		language = lang;
		parser = inParser;
		returnHandler = inReturnHandler;
	}

	public void startElement(String uri, String localName, String qName, Attributes amap) throws SAXException
	{
		if ("Action".equals(qName))
		{
			// end <action>
			if (amap.getValue("name") != null)
			{
				String name = amap.getValue("name");
				String argument = amap.getValue("argument");
				CompilerAction action = new CompilerAction();
				action.setName(name);
				action.setArgument(argument);
				language.getCompilerSettings().addCompilerAction(action);
			}
		}
	}

	public void endElement(String uri, String local_name, String qName) throws SAXException
	{
		if ("Actions".equals(qName))
		{
			// end <actions>
			parser.setContentHandler(returnHandler);
		}
	}
}
