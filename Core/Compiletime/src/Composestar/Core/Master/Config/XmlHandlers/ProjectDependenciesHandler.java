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

import Composestar.Core.Master.Config.Dependency;
import Composestar.Core.Master.Config.Project;

@Deprecated
public class ProjectDependenciesHandler extends DefaultHandler
{
	protected XMLReader parser;

	protected ContentHandler returnHandler;

	protected Project project;

	public ProjectDependenciesHandler(Project inProject, XMLReader inParser, ContentHandler documentHandler)
	{
		project = inProject;
		parser = inParser;
		returnHandler = documentHandler;
	}

	public void startElement(String uri, String localName, String qName, Attributes amap) throws SAXException
	{
		if ("Dependency".equals(qName))
		{
			// in <dependency>
			if (amap.getValue("fileName") != null)
			{
				String filename = amap.getValue("fileName");
				Dependency d = new Dependency();
				d.setFileName(filename);
				project.addDependency(d);
			}

		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if ("Dependencies".equals(qName))
		{
			// end <dependencies>
			parser.setContentHandler(returnHandler);
		}
	}
}
