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

import Composestar.Core.Master.Config.Project;
import Composestar.Core.Master.Config.TypeSource;

public class ProjectTypeSourcesHandler extends DefaultHandler
{
	protected XMLReader parser;

	protected ContentHandler returnHandler;

	protected Project project;

	public ProjectTypeSourcesHandler(Project inProject, XMLReader inParser, ContentHandler documentHandler)
	{
		project = inProject;
		parser = inParser;
		returnHandler = documentHandler;
	}

	public void startElement(String uri, String localName, String qName, Attributes amap) throws SAXException
	{
		if ("TypeSource".equals(qName))
		{
			// in <typesource>
			TypeSource typesource = new TypeSource();
			if (amap.getValue("fileName") != null)
			{
				typesource.setFileName(amap.getValue("fileName"));
			}
			if (amap.getValue("name") != null)
			{
				typesource.setName(amap.getValue("name"));
			}

			project.addTypeSource(typesource);
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if ("TypeSources".equals(qName))
		{
			// end <typesources>
			parser.setContentHandler(returnHandler);
		}
	}
}
