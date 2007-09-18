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
import java.util.List;
import java.util.Properties;

/**
 * @author Michiel Hendriks
 */
public class SourcesCmdLineArgumentList extends CmdLineArgumentList
{
	private static final long serialVersionUID = 4967203235807658445L;

	public SourcesCmdLineArgumentList()
	{
		super();
	}

	@Override
	public void addArgs(List<String> tolist, Project proj, Properties prop)
	{
		for (File file : proj.getSourceFiles())
		{
			prop.setProperty("SOURCE", file.toString());
			super.addArgs(tolist, proj, prop);
		}
		prop.remove("SOURCE");
	}

}
