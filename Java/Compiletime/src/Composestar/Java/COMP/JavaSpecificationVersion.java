/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2009 University of Twente.
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

package Composestar.Java.COMP;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler for Java Specification Versions
 * 
 * @author Michiel Hendriks
 */
public final class JavaSpecificationVersion implements Comparable<JavaSpecificationVersion>
{
	/**
	 * @return The current java specification version
	 */
	public static JavaSpecificationVersion get()
	{
		return get(null);
	}

	/**
	 * @param versionString
	 * @return
	 */
	public static JavaSpecificationVersion get(String versionString)
	{
		if (versionString == null || versionString.length() == 0)
		{
			versionString = System.getProperty("java.specification.version");
		}
		if (!SPECS.containsKey(versionString))
		{
			JavaSpecificationVersion jsv = new JavaSpecificationVersion(versionString);
			SPECS.put(versionString, jsv);
			return jsv;
		}
		return SPECS.get(versionString);
	}

	/**
	 * Cache specifications
	 */
	private static final Map<String, JavaSpecificationVersion> SPECS = new HashMap<String, JavaSpecificationVersion>();

	/**
	 * The first element in the version string
	 */
	private int major = -1;

	/**
	 * The second element
	 */
	private int minor = -1;

	/**
	 * The last element, rarely ever used
	 */
	private int micro = -1;

	private JavaSpecificationVersion(String versionString)
	{
		parse(versionString);
	}

	/**
	 * @return True if it's a valid specification version
	 */
	public boolean isValid()
	{
		return major > -1 && minor > -1;
	}

	/**
	 * @return True if the assert directive is supported
	 */
	public boolean assertSupported()
	{
		return major >= 1 && minor >= 4;
	}

	/**
	 * @return True if annotations are supported
	 */
	public boolean annotationsSuppported()
	{
		return major >= 1 && minor >= 5;
	}

	/**
	 * @return True if enums are supported
	 */
	public boolean enumSuppported()
	{
		return major >= 1 && minor >= 5;
	}

	/**
	 * @return True if generics are supported
	 */
	public boolean genericsSupported()
	{
		return major >= 1 && minor >= 5;
	}

	/**
	 * @param versionString
	 */
	private void parse(String versionString)
	{
		String[] parts = versionString.split("\\.");
		switch (parts.length)
		{
			case 3:
				int idx;
				if ((idx = parts[2].indexOf('_')) != -1)
				{
					parts[2] = parts[2].substring(0, idx - 1);
				}
				else if ((idx = parts[2].indexOf('-')) != -1)
				{
					parts[2] = parts[2].substring(0, idx - 1);
				}
				try
				{
					micro = Integer.parseInt(parts[2]);
				}
				catch (NumberFormatException e)
				{
					micro = -1;
				}
				/* fall through */
			case 2:
				try
				{
					minor = Integer.parseInt(parts[1]);
				}
				catch (NumberFormatException e)
				{
					minor = -1;
				}
				/* fall through */
			case 1:
				try
				{
					major = Integer.parseInt(parts[0]);
				}
				catch (NumberFormatException e)
				{
					major = -1;
				}
				/* fall through */
			default:
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		if (major > -1)
		{
			sb.append(major);
			if (minor > -1)
			{
				sb.append(".");
				sb.append(minor);
				if (micro > -1)
				{
					sb.append(".");
					sb.append(micro);
				}
			}
		}
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(JavaSpecificationVersion other)
	{
		if (major != other.major)
		{
			return major - other.major;
		}
		if (minor != other.minor)
		{
			return minor - other.minor;
		}
		if (micro != other.micro)
		{
			return micro - other.micro;
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + major;
		result = prime * result + micro;
		result = prime * result + minor;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		JavaSpecificationVersion other = (JavaSpecificationVersion) obj;
		if (major != other.major)
		{
			return false;
		}
		if (micro != other.micro)
		{
			return false;
		}
		if (minor != other.minor)
		{
			return false;
		}
		return true;
	}
}
