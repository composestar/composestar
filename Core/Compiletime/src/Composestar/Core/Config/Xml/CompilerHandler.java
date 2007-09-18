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

import Composestar.Core.Config.CmdLineArgument;
import Composestar.Core.Config.CmdLineArgumentList;
import Composestar.Core.Config.CompilerAction;
import Composestar.Core.Config.DepsCmdLineArgumentList;
import Composestar.Core.Config.SourceCompiler;
import Composestar.Core.Config.SourcesCmdLineArgumentList;

/**
 * @author Michiel Hendriks
 */
public class CompilerHandler extends CpsBaseHandler
{
	protected static final int STATE_COMPILER = 1;

	protected static final int STATE_ACTION = 2;

	protected static final int STATE_ARG = 3;

	protected static final int STATE_ARG_LIST = 4;

	protected SourceCompiler compiler;

	protected CompilerAction currentAction;

	protected CmdLineArgumentList currentArgList;

	public CompilerHandler(XMLReader inReader, DefaultHandler inParent)
	{
		super(inReader, inParent);
	}

	public SourceCompiler getCompiler()
	{
		return compiler;
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
	{
		super.startElement(uri, localName, name, attributes);
		if (state == 0 && "compiler".equals(name))
		{
			state = STATE_COMPILER;
			compiler = new SourceCompiler();
			try
			{
				compiler.setClassname(attributes.getValue("class"));
			}
			catch (IllegalArgumentException e)
			{
				throw new SAXParseException(e.getMessage(), locator);
			}
		}
		else if (state == STATE_COMPILER && "action".equals(name))
		{
			state = STATE_ACTION;
			currentAction = new CompilerAction();
			try
			{
				currentAction.setName(attributes.getValue("name"));
				currentAction.setExecutable(attributes.getValue("executable"));
			}
			catch (IllegalArgumentException e)
			{
				throw new SAXParseException(e.getMessage(), locator);
			}
			compiler.addAction(currentAction);
		}
		else if ((state == STATE_ACTION || state == STATE_ARG_LIST) && "arg".equals(name))
		{
			state = STATE_ARG;
		}
		else if (state == STATE_ACTION && "sources".equals(name))
		{
			state = STATE_ARG_LIST;
			currentArgList = new SourcesCmdLineArgumentList();
			currentAction.addArgument(currentArgList);
		}
		else if (state == STATE_ACTION && "deps".equals(name))
		{
			state = STATE_ARG_LIST;
			currentArgList = new DepsCmdLineArgumentList();
			currentAction.addArgument(currentArgList);
		}
		else
		{
			startUnknownElement(uri, localName, name, attributes);
		}
	}

	@Override
	public void endElement(String uri, String localName, String name) throws SAXException
	{
		super.endElement(uri, localName, name);
		if (state == STATE_COMPILER && "compiler".equals(name))
		{
			returnHandler(uri, localName, name);
			compiler = null;
		}
		else if (state == STATE_ACTION && "action".equals(name))
		{
			state = STATE_COMPILER;
			currentAction = null;
		}
		else if (state == STATE_ARG && "arg".equals(name))
		{
			CmdLineArgument arg = new CmdLineArgument();
			arg.setValue(charData.toString());
			if (currentArgList != null)
			{
				state = STATE_ARG_LIST;
				currentArgList.addArgument(arg);
			}
			else
			{
				state = STATE_ACTION;
				currentAction.addArgument(arg);
			}
		}
		else if (state == STATE_ARG_LIST && "sources".equals(name))
		{
			state = STATE_ACTION;
			currentArgList = null;
		}
		else if (state == STATE_ARG_LIST && "deps".equals(name))
		{
			state = STATE_ACTION;
			currentArgList = null;
		}
		else
		{
			endUnknownElement(uri, localName, name);
		}
	}

}
