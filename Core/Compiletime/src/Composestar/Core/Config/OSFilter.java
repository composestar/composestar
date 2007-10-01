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

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Defines an OS filter. It checks if the OS matches the defined regular
 * expressions. Each filter item is a regular expression that is matched against
 * the java system properties for the OS and architecture. Unless filters will
 * default to true.
 * 
 * @author Michiel Hendriks
 */
public class OSFilter implements Serializable
{
	private static final long serialVersionUID = 7270592876698241817L;

	/**
	 * os.name regex
	 */
	protected Pattern name;

	/**
	 * os.version regex
	 */
	protected Pattern version;

	/**
	 * os.arch regex
	 */
	protected Pattern arch;

	protected transient String[] currentOs;

	public OSFilter()
	{}

	protected String[] getCurrentOs()
	{
		if (currentOs == null)
		{
			currentOs = new String[3];
			currentOs[0] = System.getProperty("os.name");
			currentOs[1] = System.getProperty("os.version");
			currentOs[2] = System.getProperty("os.arch");
		}
		return currentOs;
	}

	public void setName(String expr)
	{
		if (expr == null || expr.trim().length() == 0)
		{
			expr = null;
			return;
		}
		name = Pattern.compile(expr);
	}

	public void setArch(String expr)
	{
		if (expr == null || expr.trim().length() == 0)
		{
			expr = null;
			return;
		}
		arch = Pattern.compile(expr);
	}

	public void setVersion(String expr)
	{
		if (expr == null || expr.trim().length() == 0)
		{
			expr = null;
			return;
		}
		version = Pattern.compile(expr);
	}

	/**
	 * Returns true if this filter matches the current OS.
	 * 
	 * @return
	 */
	public boolean matches()
	{
		return matches(null);
	}

	/**
	 * Returns true if this filter matches the given OS
	 * 
	 * @param os the OS triple: name, version, arch
	 * @return
	 */
	public boolean matches(String[] os)
	{
		if (os == null)
		{
			os = getCurrentOs();
		}
		if (os == null || os.length < 3)
		{
			throw new IllegalArgumentException("Expected OS triple: name, version, arch");
		}
		if (name != null)
		{
			if (!name.matcher(os[0]).matches())
			{
				return false;
			}
		}
		if (version != null)
		{
			if (!version.matcher(os[1]).matches())
			{
				return false;
			}
		}
		if (arch != null)
		{
			if (!arch.matcher(os[2]).matches())
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		if (name != null)
		{
			sb.append("name=\"");
			sb.append(name);
			sb.append("\"");
		}
		if (version != null)
		{
			if (sb.length() > 1)
			{
				sb.append("; ");
			}
			sb.append("version=\"");
			sb.append(version);
			sb.append("\"");
		}
		if (arch != null)
		{
			if (sb.length() > 1)
			{
				sb.append("; ");
			}
			sb.append("arch=\"");
			sb.append(arch);
			sb.append("\"");
		}
		sb.append("}");
		return sb.toString();
	}
}
