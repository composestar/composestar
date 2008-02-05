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

package Composestar.Utils.Perf.Report;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;

import Composestar.Core.Master.Master;
import Composestar.Utils.FileUtils;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Perf.CPSTimer;
import Composestar.Utils.Perf.CPSTimerEvent;

/**
 * Produces an XML file with the timing results
 * 
 * @author Michiel Hendriks
 */
public class XMLTimerReport implements CPSTimerReport
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(Master.MODULE_NAME + ".Report");

	protected OutputStream output;

	protected OutputStream styleOut;

	protected String styleName;

	public XMLTimerReport(OutputStream os)
	{
		output = os;
	}

	public XMLTimerReport(OutputStream os, OutputStream xsltOut, String xsltNam)
	{
		output = os;
		styleOut = xsltOut;
		styleName = xsltNam;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Utils.Perf.Report.CPSTimerReport#generateReport(Composestar.Utils.Perf.Report.CPSTimerTree)
	 */
	public boolean generateReport(CPSTimerTree root)
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try
		{
			builder = factory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			logger.warn(String.format("Error creating a document builder: %s", e));
			return false;
		}
		Document xmlDoc = builder.newDocument();

		// try to copy the Xslt
		InputStream is = XMLTimerReport.class.getResourceAsStream(styleName);
		if (is != null)
		{
			try
			{
				FileUtils.copy(is, styleOut);
				ProcessingInstruction pi = xmlDoc.createProcessingInstruction("xml-stylesheet",
						"type=\"text/xml\" href=\"" + styleName + "\"");
				xmlDoc.appendChild(pi);
			}
			catch (IOException e)
			{
				logger.info("Error writing stylesheet: " + e.getLocalizedMessage());
			}
		}

		writeReport(xmlDoc, root, null);

		OutputFormat format = new OutputFormat();
		format.setIndenting(true);
		format.setIndent(4);
		format.setMethod("xml");
		XMLSerializer ser = new XMLSerializer(output, format);
		try
		{
			ser.serialize(xmlDoc);
		}
		catch (IOException e)
		{
			logger.warn(String.format("Error writing report: %s", e));
			return false;
		}
		return true;
	}

	protected void writeReport(Document xmlDoc, CPSTimerTree treeItem, Element parentNode)
	{
		Element node = xmlDoc.createElement("timer");
		if (parentNode == null)
		{
			xmlDoc.appendChild(node);
		}
		else
		{
			parentNode.appendChild(node);
		}
		if (treeItem.getName().length() > 0)
		{
			node.setAttribute("name", treeItem.getName());
		}
		if (treeItem.getTimer() != null)
		{
			CPSTimer timer = treeItem.getTimer();
			node.setAttribute("creation", "" + timer.getCreationTime());
			for (CPSTimerEvent event : timer.getEvents())
			{
				Element eventNode = xmlDoc.createElement("event");
				eventNode.setAttribute("start", "" + event.getStart());
				eventNode.setAttribute("stop", "" + event.getStop());
				eventNode.setAttribute("duration", "" + event.getDuration());
				eventNode.setAttribute("memory", "" + event.getMemoryDelta());
				eventNode.setTextContent(event.getMessage());
				node.appendChild(eventNode);
			}
		}
		for (CPSTimerTree child : treeItem.getChildren())
		{
			writeReport(xmlDoc, child, node);
		}
	}
}
