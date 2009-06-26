/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2007 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * $Id$
 */

package Composestar.Core.Config.Xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Config.Project;

/**
 * Handle the project element
 * 
 * @author Michiel Hendriks
 */
public class ProjectHandler extends DefaultBuildConfigHandler
{
	/**
	 * Processing a project element
	 */
	protected static final int STATE_PROJECT = 1;

	/**
	 * Processing the concerns
	 */
	protected static final int STATE_CONCERNS = 2;

	/**
	 * Processing source definition
	 */
	protected static final int STATE_SOURCES = 3;

	/**
	 * Processing the dependency elements
	 */
	protected static final int STATE_DEPENDENCIES = 4;

	/**
	 * Processing project resource elements
	 */
	protected static final int STATE_RESOURCES = 5;

	/**
	 * The resulting project definition
	 */
	protected Project project;

	/**
	 * SAX handler for the sources
	 */
	protected SourceTypeHandler sourceTypeHandler;

	/**
	 * SAX handler for the dependencies
	 */
	protected DependencyTypeHandler dependencyTypeHandler;

	/**
	 * SAX handler for the resources
	 */
	protected ResourceTypeHandler resourceTypeHandler;

	/**
	 * Temporary variable used during the concern processing.
	 */
	protected boolean concernEnabled;

	/**
	 * @param inReader
	 * @param inParent
	 */
	public ProjectHandler(XMLReader inReader, DefaultHandler inParent)
	{
		super(inReader, inParent);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.Config.Xml.CpsBaseHandler#endElement(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String name) throws SAXException
	{
		super.endElement(uri, localName, name);
		if ("project".equals(currentName) && state == STATE_PROJECT)
		{
			returnHandler();
		}
		else if ("sources".equals(currentName) && state == STATE_SOURCES)
		{
			state = STATE_PROJECT;
			project.addSources(sourceTypeHandler.getSources());
			sourceTypeHandler = null;
		}
		else if ("concerns".equals(currentName) && state == STATE_CONCERNS)
		{
			state = STATE_PROJECT;
		}
		else if ("concern".equals(currentName) && state == STATE_CONCERNS)
		{
			project.addConcern(charData.toString(), concernEnabled);
		}
		else if ("dependencies".equals(currentName) && state == STATE_DEPENDENCIES)
		{
			state = STATE_PROJECT;
			project.addDependencies(dependencyTypeHandler.getDependencies());
			dependencyTypeHandler = null;
		}
		else if ("resources".equals(currentName) && state == STATE_RESOURCES)
		{
			state = STATE_PROJECT;
			project.addResources(resourceTypeHandler.getResources());
			resourceTypeHandler = null;
		}
		else
		{
			endUnknownElement(uri, localName, name);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.Config.Xml.CpsBaseHandler#startElement(java.lang.String,
	 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
	{
		super.startElement(uri, localName, name, attributes);
		if ("project".equals(currentName) && state == 0)
		{
			state = STATE_PROJECT;
			project = config.getNewProject();
			try
			{
				project.setName(attributes.getValue("name"));
				project.setPlatform(attributes.getValue("platform"));
				project.setLanguage(attributes.getValue("language"));
				project.setBase(attributes.getValue("base"));
				if (attributes.getIndex("mainclass") != -1)
				{
					project.setMainclass(attributes.getValue("mainclass"));
				}
				project.setOutput(attributes.getValue("output"));
				project.setIntermediate(attributes.getValue("intermediate"));
			}
			catch (IllegalArgumentException e)
			{
				throw new SAXParseException(e.getMessage(), locator);
			}
		}
		else if ("sources".equals(currentName) && state == STATE_PROJECT)
		{
			state = STATE_SOURCES;
			sourceTypeHandler = new SourceTypeHandler(reader, this, "sources");
			reader.setContentHandler(sourceTypeHandler);
			sourceTypeHandler.startElement(uri, localName, name, attributes);
		}
		else if ("concerns".equals(currentName) && state == STATE_PROJECT)
		{
			state = STATE_CONCERNS;
		}
		else if ("concern".equals(currentName) && state == STATE_CONCERNS)
		{
			String en = attributes.getValue("enabled");
			if (en == null)
			{
				concernEnabled = true;
			}
			else
			{
				en = en.trim();
				concernEnabled = "true".equalsIgnoreCase(en) || "1".equals(en) || en.length() == 0;
			}
		}
		else if ("dependencies".equals(currentName) && state == STATE_PROJECT)
		{
			state = STATE_DEPENDENCIES;
			dependencyTypeHandler = new DependencyTypeHandler(reader, this, "dependencies");
			reader.setContentHandler(dependencyTypeHandler);
			dependencyTypeHandler.startElement(uri, localName, name, attributes);
		}
		else if ("resources".equals(currentName) && state == STATE_PROJECT)
		{
			state = STATE_RESOURCES;
			resourceTypeHandler = new ResourceTypeHandler(reader, this, "resources");
			reader.setContentHandler(resourceTypeHandler);
			resourceTypeHandler.startElement(uri, localName, name, attributes);
		}
		else
		{
			startUnknownElement(uri, localName, name, attributes);
		}
	}

}
