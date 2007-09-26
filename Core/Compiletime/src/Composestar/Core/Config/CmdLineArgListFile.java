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
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Collects the commandline arguments and saves it to a file. This file will in
 * turn be returns as a commandline argument.
 * 
 * @author Michiel Hendriks
 */
public class CmdLineArgListFile extends CmdLineArgumentList
{
	private static final long serialVersionUID = -7752997324099539587L;

	/**
	 * Text to prepend to the filename on the commandline argument. For for the
	 * temp file /foo/bar it will return the following as a commandline
	 * argument: {prefix}/foo/bar{suffix}
	 */
	protected String prefix = "";

	/**
	 * Text to add the end of the filename. It is not part of the actual
	 * filename.
	 */
	protected String suffix = "";

	public CmdLineArgListFile()
	{
		super();
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix()
	{
		return prefix;
	}

	/**
	 * @param inPrefix the prefix to set
	 */
	public void setPrefix(String inPrefix)
	{
		prefix = inPrefix;
	}

	/**
	 * @return the suffix
	 */
	public String getSuffix()
	{
		return suffix;
	}

	/**
	 * @param inSuffix the suffix to set
	 */
	public void setSuffix(String inSuffix)
	{
		suffix = inSuffix;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.Config.CmdLineArgumentList#addArgs(java.util.List,
	 *      Composestar.Core.Config.Project, java.util.Set,
	 *      java.util.Properties)
	 */
	@Override
	public void addArgs(List<String> tolist, Project proj, Set<File> sources, Properties prop)
	{
		List<String> fileArgs = new ArrayList<String>();
		super.addArgs(fileArgs, proj, sources, prop);
		try
		{
			File tmpFile = File.createTempFile("cstar_", null);
			PrintStream w = new PrintStream(tmpFile);
			for (String s : fileArgs)
			{
				w.println(s);
			}
			w.close();
			tmpFile.deleteOnExit();
			tolist.add(prefix + tmpFile.getAbsolutePath() + suffix);
		}
		catch (IOException e)
		{
			// TODO report error
			return;
		}
	}

}
