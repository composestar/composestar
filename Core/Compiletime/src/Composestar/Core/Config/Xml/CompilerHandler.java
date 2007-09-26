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

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Config.CmdLineArgListFile;
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
	// note: states must be bit fields
	protected static final int STATE_COMPILER = 1;

	protected static final int STATE_ACTION = 2;

	protected static final int STATE_ARG = 3;

	protected static final int STATE_ARG_LIST = 4;

	protected static final int STATE_RCFILE = 5;

	protected SourceCompiler compiler;

	protected CompilerAction currentAction;

	protected Stack<CmdLineArgumentList> argList;

	protected Stack<Integer> states;

	public CompilerHandler(XMLReader inReader, DefaultHandler inParent)
	{
		super(inReader, inParent);
		states = new Stack<Integer>();
	}

	public SourceCompiler getCompiler()
	{
		return compiler;
	}

	protected void pushState(int newState)
	{
		states.push(state);
		state = newState;
	}

	/**
	 * Pop a state and check for the new state to be in the expected states. Use
	 * 0 to prevent checking.
	 * 
	 * @param expectedState
	 */
	protected void popState()
	{
		state = states.pop();
	}

	/**
	 * Add an argument to the current list
	 * 
	 * @param arg
	 */
	protected void addArgument(CmdLineArgument arg)
	{
		if (argList.size() > 0)
		{
			argList.peek().addArgument(arg);
		}
		else
		{
			currentAction.addArgument(arg);
		}
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
	{
		super.startElement(uri, localName, name, attributes);
		if (state == 0 && "compiler".equals(name))
		{
			pushState(STATE_COMPILER);
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
			pushState(STATE_ACTION);
			currentAction = new CompilerAction();
			argList = new Stack<CmdLineArgumentList>();
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
		else if ((state == STATE_ACTION || state == STATE_ARG_LIST || state == STATE_RCFILE) && "arg".equals(name))
		{
			pushState(STATE_ARG);
		}
		else if (state == STATE_ACTION && "sources".equals(name))
		{
			pushState(STATE_ARG_LIST);
			SourcesCmdLineArgumentList currentArgList = new SourcesCmdLineArgumentList();
			currentArgList.setMerge(Boolean.parseBoolean(attributes.getValue("merge")));
			currentArgList.setDelimiter(attributes.getValue("delimiter"));
			addArgument(currentArgList);
			argList.push(currentArgList);
		}
		else if (state == STATE_ACTION && "deps".equals(name))
		{
			pushState(STATE_ARG_LIST);
			DepsCmdLineArgumentList currentArgList = new DepsCmdLineArgumentList();
			currentArgList.setMerge(Boolean.parseBoolean(attributes.getValue("merge")));
			currentArgList.setDelimiter(attributes.getValue("delimiter"));
			addArgument(currentArgList);
			argList.push(currentArgList);
		}
		else if (state == STATE_ACTION && "rcfile".equals(name))
		{
			pushState(STATE_RCFILE);
			CmdLineArgListFile currentRcFile = new CmdLineArgListFile();
			currentRcFile.setPrefix(attributes.getValue("prefix"));
			currentRcFile.setSuffix(attributes.getValue("suffix"));
			currentRcFile.setMerge(Boolean.parseBoolean(attributes.getValue("merge")));
			currentRcFile.setDelimiter(attributes.getValue("delimiter"));
			addArgument(currentRcFile);
			argList.push(currentRcFile);
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
			popState(/* STATE_COMPILER */);
			currentAction = null;
			argList = null;
		}
		else if (state == STATE_ARG && "arg".equals(name))
		{
			CmdLineArgument arg = new CmdLineArgument();
			arg.setValue(charData.toString());
			addArgument(arg);
			popState(/* STATE_ACTION | STATE_ARG_LIST | STATE_RCFILE */);
		}
		else if (state == STATE_ARG_LIST && "sources".equals(name))
		{
			popState(/* STATE_ACTION | STATE_RCFILE */);
			argList.pop();
		}
		else if (state == STATE_ARG_LIST && "deps".equals(name))
		{
			popState(/* STATE_ACTION | STATE_RCFILE */);
			argList.pop();
		}
		else if (state == STATE_RCFILE && "rcfile".equals(name))
		{
			popState( /*
			 * STATE_ACTION | STATE_ARG_LIST | STATE_ARG_LIST |
			 * STATE_RCFILE
			 */);
			argList.pop();
		}
		else
		{
			endUnknownElement(uri, localName, name);
		}
	}
}
