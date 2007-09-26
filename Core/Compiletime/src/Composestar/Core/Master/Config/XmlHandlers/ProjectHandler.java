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

import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Project;
import Composestar.Utils.Debug;

@Deprecated
public class ProjectHandler extends DefaultHandler
{
	protected XMLReader parser;

	protected ContentHandler returnHandler;

	protected Project project;

	public ProjectHandler(XMLReader inParser, ContentHandler documentHandler)
	{
		parser = inParser;
		returnHandler = documentHandler;
	}

	public void startElement(String uri, String localName, String qName, Attributes amap) throws SAXException
	{
		if ("Project".equals(qName))
		{// in <Project>
			project = new Project();
			for (int i = 0; i < amap.getLength(); i++)
			{
				String key = amap.getQName(i);
				String val = amap.getValue(i);

				if ("name".equals(key))
				{
					project.setName(val);
				}
				else if ("language".equals(key))
				{
					project.setLanguageName(val);
				}
				else if ("basePath".equals(key))
				{
					project.setBasePath(val);
				}
				else
				{
					Debug.out(Debug.MODE_WARNING, "MASTER", "Unknown attribute " + key + " in Project");
					// project.addProperty(key, val);
				}
			}

			Configuration.instance().getProjects().addProject(project);
		}

		if ("Sources".equals(qName))
		{// in <Sources>
			// look further
			ProjectSourcesHandler sourceshandler = new ProjectSourcesHandler(project, parser, this);
			parser.setContentHandler(sourceshandler);
		}

		if ("Dependencies".equals(qName))
		{// in <Dependencies>
			// look further
			ProjectDependenciesHandler dependencyhandler = new ProjectDependenciesHandler(project, parser, this);
			parser.setContentHandler(dependencyhandler);
		}

	}

	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if ("Project".equals(qName))
		{
			// end <Project>
			parser.setContentHandler(returnHandler);
		}
	}
}
