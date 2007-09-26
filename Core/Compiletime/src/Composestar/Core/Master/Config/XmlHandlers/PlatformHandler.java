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

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Language;
import Composestar.Core.Master.Config.Project;

@Deprecated
public class PlatformHandler extends DefaultHandler implements ContentHandler
{
	protected XMLReader parser;

	protected ContentHandler returnHandler;

	public PlatformHandler(XMLReader inParser, ContentHandler inReturnHandler)
	{
		parser = inParser;
		returnHandler = inReturnHandler;
	}

	public void startElement(String uri, String localName, String qName, Attributes amap) throws SAXException
	{
		if ("Language".equals(qName))
		{// in <language>
			// look further
			if (amap.getValue("name") != null)
			{
				String languagename = amap.getValue("name");
				Configuration config = Configuration.instance();
				if (config.getProjects().getProjectsByLanguage(languagename) != null)
				{
					// look further
					Language lang = new Language();
					lang.setName(languagename);
					LanguageHandler langhandler = new LanguageHandler(lang, parser, this);
					parser.setContentHandler(langhandler);

					// add language to projects
					List<Project> projects = config.getProjects().getProjectsByLanguage(languagename);
					for (Project p : projects)
					{
						p.setLanguage(lang);
					}
				}
				else
				{
					// next language
				}
			}
		}
		else if ("RequiredFiles".equals(qName))
		{
			// in <RequiredFiles>
			// look further
			RequiredFilesHandler fileshandler = new RequiredFilesHandler(parser, this);
			parser.setContentHandler(fileshandler);
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if ("Platform".equals(qName))
		{
			// end <platform>
			parser.setContentHandler(returnHandler);
		}
	}
}
