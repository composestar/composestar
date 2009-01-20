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

package Composestar.Core.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import Composestar.Utils.StringUtils;

/**
 * Just like DepsCmdLineArgumentList this is a specialized list, but it iterates
 * through the list of files.
 * 
 * @author Michiel Hendriks
 */
public class SourcesCmdLineArgumentList extends CmdLineArgumentList
{
	private static final long serialVersionUID = 4967203235807658445L;

	public SourcesCmdLineArgumentList()
	{
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Config.CmdLineArgumentList#addArgs(java.util.List,
	 * Composestar.Core.Config.Project, java.util.Set, java.util.Properties)
	 */
	@Override
	public void addArgs(List<String> tolist, Project proj, Set<File> sources, Properties prop)
	{
		if (args.size() == 0)
		{
			addArgument(new CmdLineArgument("${SOURCE}"));
		}
		List<String> argList;
		boolean isMerge = merge;
		if (isMerge)
		{
			// first collect all arguments for all sources and then merge
			// it all into a single argument.
			merge = false;
			argList = new ArrayList<String>();
		}
		else
		{
			argList = tolist;
		}
		for (File source : sources)
		{
			prop.setProperty("SOURCE", source.toString());
			super.addArgs(argList, proj, sources, prop);
		}
		prop.remove("SOURCE");
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
