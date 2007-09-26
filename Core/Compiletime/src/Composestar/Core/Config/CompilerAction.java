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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * A compiler action.
 * 
 * @author Michiel Hendriks
 */
public class CompilerAction implements Serializable
{
	private static final long serialVersionUID = -3489491657540648030L;

	/**
	 * The name of the action
	 */
	protected String name;

	/**
	 * The executable to execute
	 */
	protected String executable;

	protected CmdLineArgumentList args;

	public CompilerAction()
	{
		args = new CmdLineArgumentList();
	}

	public void setName(String inName)
	{
		if (inName == null || inName.trim().length() == 0)
		{
			throw new IllegalArgumentException("Name can not be null or empty");
		}
		name = inName.trim();
	}

	public String getName()
	{
		return name;
	}

	public String getExecutable()
	{
		return executable;
	}

	public void setExecutable(String exec)
	{
		if (exec == null || exec.trim().length() == 0)
		{
			throw new IllegalArgumentException("Executable can not be null or empty");
		}
		executable = exec.trim();
	}

	public void addArgument(CmdLineArgument arg)
	{
		args.addArgument(arg);
	}

	/**
	 * Return the commandline for the given rpoject and source files.
	 * 
	 * @param proj
	 * @param source
	 * @param prop
	 * @return
	 */
	public String[] getCmdLine(Project proj, Set<File> sources, Properties prop)
	{
		List<String> result = new ArrayList<String>();
		result.add(executable);
		args.addArgs(result, proj, sources, prop);
		String[] argsArray = result.toArray(new String[result.size()]);
		return argsArray;
	}
}
