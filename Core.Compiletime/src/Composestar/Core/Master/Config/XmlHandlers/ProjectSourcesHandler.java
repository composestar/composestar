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
import Composestar.Core.Master.Config.Source;

public class ProjectSourcesHandler extends DefaultHandler
{
	protected XMLReader parser;

	protected ContentHandler returnHandler;

	protected Project project;

	public ProjectSourcesHandler(Project inProject, XMLReader inParser, ContentHandler documentHandler)
	{
		project = inProject;
		parser = inParser;
		returnHandler = documentHandler;
	}

	public void startElement(String uri, String localName, String qName, Attributes amap) throws SAXException
	{
		if ("Source".equals(qName))
		{
			// in <source>
			Source s = new Source();
			if (amap.getValue("fileName") != null)
			{
				s.setFileName(amap.getValue("fileName"));
			}
			// if(amap.getValue("fileName").equals("True"))
			// s.setIsExecutable(true);

			project.addSource(s);
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if ("Sources".equals(qName))
		{
			// end <sources>
			parser.setContentHandler(returnHandler);
		}
	}
}
