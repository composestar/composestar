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

package Composestar.Core.Config.Xml.Output;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import Composestar.Core.Config.BuildConfig;
import Composestar.Core.Config.Dependency;
import Composestar.Core.Config.Project;
import Composestar.Core.Config.Source;
import Composestar.Core.Config.Xml.BuildConfigHandler;
import Composestar.Core.Config.Xml.DefaultBuildConfigHandler;

/**
 * Serializes a buildconfiguration structure to a parseable XML file.
 * 
 * @see BuildConfig
 * @author Michiel Hendriks
 */
public class BuildConfigXmlWriter
{
	protected static final String VERSION = BuildConfigHandler.CURRENT_VERSION;

	protected static final String XMLNS = DefaultBuildConfigHandler.NAMESPACE;

	protected BuildConfigXmlWriter()
	{}

	/**
	 * Write the given build configuration to the given stream
	 * 
	 * @param config
	 * @param stream
	 * @return
	 */
	public static final boolean write(BuildConfig config, OutputStream stream)
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try
		{
			builder = factory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			return false;
		}
		Document xmlDoc = builder.newDocument();

		write(config, xmlDoc, null);

		OutputFormat format = new OutputFormat();
		format.setIndenting(true);
		format.setIndent(4);
		format.setMethod("xml");
		XMLSerializer ser = new XMLSerializer(stream, format);
		try
		{
			ser.serialize(xmlDoc);
		}
		catch (IOException e)
		{
			return false;
		}
		return true;
	}

	/**
	 * Write the given build config to the given XML document using parent as
	 * the parent node
	 * 
	 * @param config
	 * @param xmlDoc
	 * @param parent
	 */
	public static final void write(BuildConfig config, Document xmlDoc, Node parent)
	{
		xmlDoc.appendChild(xmlDoc.createComment(String.format("Created by %s on %s", BuildConfigXmlWriter.class,
				DateFormat.getDateTimeInstance().format(new Date()))));

		Element root = xmlDoc.createElement("buildconfiguration");
		if (parent == null)
		{
			xmlDoc.appendChild(root);
		}
		else
		{
			parent.appendChild(root);
		}
		root.setAttribute("version", VERSION);
		root.setAttribute("xmlns", XMLNS);
		writeSettings(config, xmlDoc, root);
		writeProject(config, xmlDoc, root);
		writeFilters(config, xmlDoc, root);
		if (config.getSecretResources() != null)
		{
			// TODO: ...
		}
	}

	/**
	 * Write the project settings
	 * 
	 * @param config
	 * @param xmlDoc
	 * @param root
	 */
	protected static final void writeSettings(BuildConfig config, Document xmlDoc, Node root)
	{
		if (config.getSettings().size() == 0)
		{
			return;
		}
		Element settings = xmlDoc.createElement("settings");
		root.appendChild(settings);
		for (Entry<String, String> entry : config.getSettings().entrySet())
		{
			Element setting = xmlDoc.createElement("setting");
			setting.setAttribute("name", entry.getKey());
			setting.appendChild(xmlDoc.createTextNode(entry.getValue()));
			settings.appendChild(setting);
		}
	}

	/**
	 * Write the project
	 * 
	 * @param config
	 * @param xmlDoc
	 * @param root
	 */
	protected static final void writeProject(BuildConfig config, Document xmlDoc, Node root)
	{
		Project p = config.getProject();
		Element project = xmlDoc.createElement("project");
		root.appendChild(project);
		project.setAttribute("name", p.getName());
		project.setAttribute("platform", p.getPlatformId());
		project.setAttribute("language", p.getLanguage());
		project.setAttribute("base", p.getBase().toString());
		if (p.getMainclass() != null && p.getMainclass().length() > 0)
		{
			project.setAttribute("mainclass", p.getMainclass());
		}
		project.setAttribute("output", p.getOutputRaw());
		project.setAttribute("intermediate", p.getIntermediateRaw());

		Element sources = xmlDoc.createElement("sources");
		project.appendChild(sources);
		for (Source src : p.getSources())
		{
			Element elm = xmlDoc.createElement("source");
			elm.appendChild(xmlDoc.createTextNode(src.getRawFile().toString()));
			if (src.getLanguage() != null)
			{
				elm.setAttribute("language", src.getLanguage());
			}
			sources.appendChild(elm);
		}

		Element concerns = xmlDoc.createElement("concerns");
		project.appendChild(concerns);
		for (File concern : p.getConcernFilesRaw())
		{
			Element elm = xmlDoc.createElement("concern");
			elm.appendChild(xmlDoc.createTextNode(concern.toString()));
			concerns.appendChild(elm);
		}
		for (File concern : p.getDisabledConcernFilesRaw())
		{
			Element elm = xmlDoc.createElement("concern");
			elm.appendChild(xmlDoc.createTextNode(concern.toString()));
			elm.setAttribute("enabled", "false");
			concerns.appendChild(elm);
		}

		Element deps = xmlDoc.createElement("dependencies");
		project.appendChild(deps);
		for (Dependency dep : p.getDependencies())
		{
			// if (isFile)
			Element elm = xmlDoc.createElement("file");
			elm.appendChild(xmlDoc.createTextNode(dep.getFile().toString()));
			deps.appendChild(elm);
			// endif
		}

		// FIXME resources
	}

	/**
	 * Write the filter definitions
	 * 
	 * @param config
	 * @param xmlDoc
	 * @param root
	 */
	protected static final void writeFilters(BuildConfig config, Document xmlDoc, Node root)
	{
	// FIXME not yet implemented
	}
}
