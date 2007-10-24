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
 * A specialized CmdLineArgumentList that creates a set of commandline arguments
 * for each file dependency in a project. This list will default to a single
 * argument with the value ${DEP}
 * 
 * @author Michiel Hendriks
 */
public class DepsCmdLineArgumentList extends CmdLineArgumentList
{
	private static final long serialVersionUID = 2465539159360682422L;

	public DepsCmdLineArgumentList()
	{
		super();
	}

	@Override
	public void addArgs(List<String> tolist, Project proj, Set<File> sources, Properties prop)
	{
		if (args.size() == 0)
		{
			addArgument(new CmdLineArgument("${DEP}"));
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
		for (File file : proj.getFilesDependencies())
		{
			prop.setProperty("DEP", file.toString());
			super.addArgs(argList, proj, sources, prop);
		}
		prop.remove("DEP");
		if (isMerge)
		{
			tolist.add(StringUtils.join(argList, delimiter));
			merge = isMerge;
		}
	}
}