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

package Composestar.Core.Resources;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;

import Composestar.Utils.Logging.CPSLogger;

/**
 * Utility class to resolve various paths to Compose* files. It will assume the
 * following setup: <code>/path/to/composestar/lib/ComposestarCORE.jar</code>
 * In that case the core will point to <code>/path/to/composestar/</code>.
 * The base element will point to whatever has been set as resourceBase. in most
 * cases core == base.
 * 
 * @author Michiel Hendriks
 */
public class PathResolver
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger("Resources.PathResolver");

	/**
	 * Path skew is appended at the detected base and core paths. It's intended
	 * to be used in special environment cases like running the compiler from
	 * within Eclipse.
	 */
	protected String pathSkew;

	protected File base;

	protected File core;

	protected Class<?> resourceBase;

	public PathResolver()
	{
		resourceBase = getClass();
	}

	public PathResolver(Class<?> inResourceBase)
	{
		resourceBase = inResourceBase;
	}

	/**
	 * Get's the base directory. Could be the same as the Core directory.
	 * 
	 * @return
	 */
	public File getBase()
	{
		if (base == null)
		{
			try
			{
				base = new File(resourceBase.getProtectionDomain().getCodeSource().getLocation().toURI());
				if (base.toString().endsWith("Compiletime" + File.separator + "build"))
				{
					base = new File(base.getParentFile(), "dist");
				}
				else
				{
					// because base is: /path/to/composestar.jar/
					// (including the last /)
					base = base.getParentFile().getParentFile();
				}
				logger.info(String.format("Base set to: %s", base));
			}
			catch (URISyntaxException e)
			{
				// TODO: nice erroe
				e.printStackTrace();
			}
		}
		return base;
	}

	/**
	 * Get the core directory.
	 * 
	 * @return
	 */
	public File getCore()
	{
		if (core == null)
		{
			try
			{
				core = new File(PathResolver.class.getProtectionDomain().getCodeSource().getLocation().toURI());
				if (core.toString().endsWith("Compiletime" + File.separator + "build"))
				{
					core = new File(core.getParentFile(), "dist");
				}
				else
				{
					core = core.getParentFile().getParentFile();
				}
				logger.info(String.format("Core set to: %s", core));
			}
			catch (URISyntaxException e)
			{
				// TODO: nice erroe
				e.printStackTrace();
			}
		}
		return core;
	}

	/**
	 * Return a file handle to a given Compose* resource file. Should only be
	 * used for external files. It will return null when the given resource
	 * could not be found.
	 * 
	 * @param name
	 * @return
	 */
	public File getResource(String name)
	{
		return getResource(new File(name));
	}

	/**
	 * Returns an existing file in either the base or the core directory. It
	 * will return null when the given resource could not be found.
	 * 
	 * @param relativeFile
	 * @return
	 */
	public File getResource(File relativeFile)
	{
		if (relativeFile.isAbsolute())
		{
			return relativeFile;
		}
		File file = new File(getBase(), relativeFile.toString());
		if (file.exists())
		{
			return file;
		}
		file = new File(getCore(), relativeFile.toString());
		if (file.exists())
		{
			return file;
		}
		logger.info(String.format("Unable to find resource %s", relativeFile));
		return null;
	}

	/**
	 * Return an inputstearm for the given internal resource. It will first try
	 * to find the resource in the resourceBase and then in the core.
	 * 
	 * @param name
	 * @return
	 */
	public InputStream getInternalResourceStream(String name)
	{
		InputStream res = resourceBase.getResourceAsStream(name);
		if (res == null)
		{
			res = PathResolver.class.getResourceAsStream(name);
		}
		return res;
	}
}
