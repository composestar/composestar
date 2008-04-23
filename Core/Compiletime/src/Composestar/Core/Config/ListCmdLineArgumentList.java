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

package Composestar.Core.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import Composestar.Utils.StringUtils;

/**
 * @author Michiel Hendriks
 */
public class ListCmdLineArgumentList extends CmdLineArgumentList
{
	private static final long serialVersionUID = 7173703419467780577L;

	/**
	 * The entry in the properties to use as source
	 */
	protected String varName;

	/**
	 * The pattern to use to split the values
	 */
	protected Pattern split;

	public ListCmdLineArgumentList()
	{
		super();
	}

	public void setVarName(String val)
	{
		if (val == null || val.trim().length() == 0)
		{
			throw new IllegalArgumentException("Var name can not be null or empty");
		}
		varName = val;
	}

	public void setSplit(String pat)
	{
		if (pat == null || pat.trim().length() == 0)
		{
			return;
		}
		split = Pattern.compile(pat);
	}

	@Override
	public void addArgs(List<String> tolist, Project proj, Set<File> sources, Properties prop)
	{
		if (args.size() == 0)
		{
			addArgument(new CmdLineArgument("${ENTRY}"));
		}
		if (split == null)
		{
			split = Pattern.compile(Pattern.quote(File.pathSeparator));
		}
		List<String> argList;
		boolean isMerge = merge;
		if (isMerge)
		{
			// first collect all arguments for all dependencies and then merge
			// it all into a single argument. This is useful for things like the
			// Java classpath
			merge = false;
			argList = new ArrayList<String>();
		}
		else
		{
			argList = tolist;
		}
		String val = prop.getProperty(varName);
		if (val != null)
		{
			for (String entry : split.split(val))
			{
				if (entry.trim().length() == 0)
				{
					continue;
				}
				prop.setProperty("ENTRY", entry.trim());
				super.addArgs(argList, proj, sources, prop);
			}
		}
		prop.remove("ENTRY");
		if (isMerge)
		{
			String quote = "";
			if (isUseQuote())
			{
				quote = "\"";
			}
			String delim = resolve(delimiter, prop);
			tolist.add(quote + StringUtils.join(argList, delim) + quote);
			merge = isMerge;
		}
	}
}
