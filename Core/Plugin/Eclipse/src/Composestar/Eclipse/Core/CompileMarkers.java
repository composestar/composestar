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

package Composestar.Eclipse.Core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * @author Michiel Hendriks
 */
public class CompileMarkers
{
	protected IProject project;

	public CompileMarkers(IProject proj)
	{
		project = proj;
	}

	public void append(String data)
	{
		String[] entries = data.split("[\\n\\r]");
		for (int i = 0; i < entries.length; i++)
		{
			createMarker(entries[i].trim());
		}
	}

	public void createMarker(String markerType, int sevr, String msg, String filename, int line, int linepos)
	{
		IResource dest = null;

		if (filename != null && !filename.isEmpty())
		{
			IPath path = new Path(filename);
			int segs = path.matchingFirstSegments(project.getLocation());
			if (segs > 0)
			{
				path = path.removeFirstSegments(segs);
			}
			dest = project.findMember(path, true);
		}

		if (dest == null)
		{
			dest = project;
		}
		try
		{
			IMarker mark = dest.createMarker(markerType);
			if (mark.exists())
			{
				mark.setAttribute(IMarker.MESSAGE, msg);
				if (line > -1)
				{
					mark.setAttribute(IMarker.LINE_NUMBER, line);
				}
				mark.setAttribute(IMarker.SEVERITY, sevr);
			}
			if (filename != null && !filename.isEmpty())
			{
				mark.setAttribute(IMarker.LOCATION, filename);
			}
		}
		catch (CoreException e)
		{
		}
	}

	public void createMarker(String logEntry)
	{
		String type = IMarker.PROBLEM;
		int sevr = IMarker.SEVERITY_INFO;
		String msg = "";
		String filename = null;
		int line = -1;
		int linepos = -1;

		if (logEntry == null || logEntry.isEmpty())
		{
			return;
		}

		try
		{
			// filename(line:colum) priority [module]: description
			final Pattern pat = Pattern.compile("^(.*)\\(([0-9]+):([0-9]+)\\) ([\\w]+) \\[([\\w+._]+)\\]: (.*)$");
			// old format
			Matcher m = pat.matcher(logEntry);
			if (!m.matches())
			{
				return;
			}

			msg = String.format("[%s] %s", m.group(5), m.group(6));
			String prio = m.group(4);
			if ("INFO".equalsIgnoreCase(prio))
			{
				sevr = IMarker.SEVERITY_INFO;
			}
			else if ("WARN".equalsIgnoreCase(prio))
			{
				sevr = IMarker.SEVERITY_WARNING;
			}
			else if ("ERROR".equalsIgnoreCase(prio))
			{
				sevr = IMarker.SEVERITY_ERROR;
			}
			else
			{
				return;
			}
			filename = m.group(1);
			if (filename != null && !filename.isEmpty())
			{
				line = Integer.parseInt(m.group(2));
				linepos = Integer.parseInt(m.group(3));
			}

			if ("COMP".equals(m.group(5)) && (sevr != IMarker.SEVERITY_ERROR) && filename != null
					&& filename.replace("\\", "/").contains(".composestar/dummies"))
			{
				// ignore compile warnings in the dummies
				return;
			}

			createMarker(type, sevr, msg, filename, line, linepos);
		}
		catch (Exception e)
		{
			// ignore errors
		}
	}
}
