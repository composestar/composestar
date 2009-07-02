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

import Composestar.Utils.Logging.CPSLogger;

/**
 * @author Michiel Hendriks
 */
public final class CompilerServiceUtil
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(CStarJavaCompiler.MODULE_NAME);

	/**
	 * Tries to find out of the compiler service is available in the current
	 * runtime environment.
	 * 
	 * @return
	 */
	public static boolean hasCompilerService()
	{
		try
		{
			Class.forName("javax.tools.JavaCompiler");
			return InternalCompiler.getJavacService() != null;
		}
		catch (ClassNotFoundException e)
		{
		}
		return false;
	}

	private CompilerServiceUtil()
	{}
}
