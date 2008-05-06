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
 * A compiler action. These instances are used by the compiler implementation as
 * specified in the language. It's used by the COMP modules. A compiler action
 * contains instructions of an external application to be executed. Internal
 * actions should be handled manually by the language compiler.
 * 
 * @author Michiel Hendriks
 */
public class CompilerAction implements Serializable
{
	private static final long serialVersionUID = -3489491657540648030L;

	/**
	 * The name of the action. It is used by the compiler implementation to
	 * retrieve a specific action.
	 */
	protected String name;

	/**
	 * The executable to execute.
	 */
	protected String executable;

	/**
	 * The commandline arguments for this executable.
	 * 
	 * @see CmdLineArgumentList
	 */
	protected CmdLineArgumentList args;

	public CompilerAction()
	{
		args = new CmdLineArgumentList();
	}

	/**
	 * Set the name of the compiler action
	 * 
	 * @param inName
	 */
	public void setName(String inName)
	{
		if (inName == null || inName.trim().length() == 0)
		{
			throw new IllegalArgumentException("Name can not be null or empty");
		}
		name = inName.trim();
	}

	/**
	 * @return the name of this action
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return the program to execute
	 */
	public String getExecutable()
	{
		return executable;
	}

	/**
	 * Set the executable for this action. The executable can not be null or
	 * empty.
	 * 
	 * @param exec
	 */
	public void setExecutable(String exec)
	{
		if (exec == null || exec.trim().length() == 0)
		{
			throw new IllegalArgumentException("Executable can not be null or empty");
		}
		executable = exec.trim();
	}

	/**
	 * Add a new argument to the action
	 * 
	 * @param arg
	 */
	public void addArgument(CmdLineArgument arg)
	{
		args.addArgument(arg);
	}

	/**
	 * Returns the command line to execute this action for the given project and
	 * source files. The properties contain the values for the variables that
	 * will be expanded when processing the argument list. The return value can
	 * be used in the created Process
	 * 
	 * @param proj
	 * @param source
	 * @param prop
	 * @return
	 * @see CmdLineArgument
	 * @see Process
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
