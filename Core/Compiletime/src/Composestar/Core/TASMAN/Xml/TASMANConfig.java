/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2008 University of Twente.
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

package Composestar.Core.TASMAN.Xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Stack;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Config.ModuleInfoManager;
import Composestar.Core.Config.Xml.CpsBaseHandler;
import Composestar.Core.Exception.ConfigurationException;
import Composestar.Core.TASMAN.Manager;
import Composestar.Core.TASMAN.ModuleTask;
import Composestar.Core.TASMAN.ParallelTask;
import Composestar.Core.TASMAN.SequentialTask;
import Composestar.Core.TASMAN.TaskCollection;
import Composestar.Utils.Logging.CPSLogger;

/**
 * @author Michiel Hendriks
 */
public class TASMANConfig extends CpsBaseHandler
{
	public static final String NAMESPACE = "http://composestar.sourceforge.net/schema/TASMANConfig";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(Manager.MODULE_NAME + ".Config");

	protected static final int STATE_MODULE = 4;

	protected static final int STATE_PARALLEL = 3;

	protected static final int STATE_SEQUENTIAL = 2;

	protected static final int STATE_TASKS = 1;

	protected Stack<Integer> stateStack;

	protected Stack<TaskCollection> taskStack;

	protected URL basePath;

	public static SequentialTask loadConfig(File file, URL baseClassPath) throws ConfigurationException
	{
		if (file == null)
		{
			throw new IllegalArgumentException("file can not be null");
		}
		try
		{
			InputStream is = new FileInputStream(file);
			if (file.getName().endsWith(".gz"))
			{
				try
				{
					is = new GZIPInputStream(is);
				}
				catch (IOException e)
				{
					throw new ConfigurationException("IOException: " + e.getMessage());
				}
			}
			return loadConfig(is, baseClassPath);
		}
		catch (FileNotFoundException e)
		{
			throw new ConfigurationException("TASMAN configuration file not found: " + file);
		}
	}

	public static SequentialTask loadConfig(InputStream stream, URL baseClassPath) throws ConfigurationException
	{
		return loadConfig(new InputSource(stream), baseClassPath);
	}

	public static SequentialTask loadConfig(InputSource source, URL baseClassPath) throws ConfigurationException
	{
		try
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			// factory.setNamespaceAware(true);
			SAXParser parser = factory.newSAXParser();
			TASMANConfig config = new TASMANConfig(parser.getXMLReader(), null);
			config.basePath = baseClassPath;
			parser.parse(source, config);
			return (SequentialTask) config.taskStack.pop();
		}
		catch (ParserConfigurationException e)
		{
			throw new ConfigurationException("Parser Configuration Exception: " + e.getMessage());
		}
		catch (SAXParseException e)
		{
			throw new ConfigurationException("SAX Parse Exception at #" + e.getLineNumber() + "," + e.getLineNumber()
					+ ": " + e.getMessage());
		}
		catch (SAXException e)
		{
			throw new ConfigurationException("SAX Exception: " + e.getMessage());
		}
		catch (IOException e)
		{
			throw new ConfigurationException("Exception reading configuration: " + e.getMessage());
		}
	}

	/**
	 * @param inReader
	 * @param inParent
	 */
	public TASMANConfig(XMLReader inReader, DefaultHandler inParent)
	{
		super(inReader, inParent);
		namespace = NAMESPACE;
		taskStack = new Stack<TaskCollection>();
		stateStack = new Stack<Integer>();
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
		if (state == 0 && "tasks".equals(currentName))
		{
			state = STATE_TASKS;
			taskStack.push(new SequentialTask());
		}
		else if ((state == STATE_TASKS || state == STATE_PARALLEL || state == STATE_SEQUENTIAL)
				&& "module".equals(currentName))
		{
			stateStack.push(state);
			state = STATE_MODULE;
			ModuleTask mt = new ModuleTask();
			try
			{
				mt.setClasspath(attributes.getValue("classpath"), basePath);
				mt.setModuleClass(attributes.getValue("class"));
				ModuleInfoManager.get(mt.getModuleClass());
			}
			catch (ClassNotFoundException e)
			{
				logger.error(String.format("Unknown module class: %s", attributes.getValue("class")), e);
			}
			taskStack.peek().addTask(mt);
		}
		else if ((state == STATE_TASKS || state == STATE_PARALLEL || state == STATE_SEQUENTIAL)
				&& "parallel".equals(currentName))
		{
			stateStack.push(state);
			state = STATE_PARALLEL;
			ParallelTask pt = new ParallelTask();
			try
			{
				pt.setMax(Integer.valueOf(attributes.getValue("max")));
			}
			catch (NumberFormatException nfe)
			{
			}
			try
			{
				pt.setPerProcessor(Integer.valueOf(attributes.getValue("perprocessor")));
			}
			catch (NumberFormatException nfe)
			{
			}
			taskStack.peek().addTask(pt);
			taskStack.push(pt);
		}
		else if ((state == STATE_TASKS || state == STATE_PARALLEL || state == STATE_SEQUENTIAL)
				&& "sequential".equals(currentName))
		{
			stateStack.push(state);
			state = STATE_SEQUENTIAL;
			SequentialTask st = new SequentialTask();
			taskStack.peek().addTask(st);
			taskStack.push(st);
		}
		else
		{
			startUnknownElement(uri, localName, name, attributes);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String name) throws SAXException
	{
		super.endElement(uri, localName, name);
		if (state == STATE_TASKS && "tasks".equals(currentName))
		{
			returnHandler();
		}
		else if (state == STATE_MODULE && "module".equals(currentName))
		{
			state = stateStack.pop();
		}
		else if (state == STATE_PARALLEL && "parallel".equals(currentName))
		{
			state = stateStack.pop();
			taskStack.pop();
		}
		else if (state == STATE_SEQUENTIAL && "sequential".equals(currentName))
		{
			state = stateStack.pop();
			taskStack.pop();
		}
		else
		{
			endUnknownElement(uri, localName, name);
		}
	}
}
