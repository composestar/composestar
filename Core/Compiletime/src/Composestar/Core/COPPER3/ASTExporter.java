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

package Composestar.Core.COPPER3;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.lang.reflect.Method;

import org.antlr.runtime.tree.DOTTreeGenerator;
import org.antlr.runtime.tree.Tree;

import Composestar.Core.Master.ModuleNames;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Exports the AST to a file. Uses reflection to call the DOTTreeGenerator
 * 
 * @author Michiel Hendriks
 */
public final class ASTExporter
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.COPPER + ".ASTExporter");

	/**
	 * Create a GraphViz DOT file
	 * 
	 * @param output
	 * @param node
	 */
	public static final void dotExport(File output, Tree node)
	{
		try
		{
			logger.debug("Writing AST to " + output.toString());
			Object res = null;
			DOTTreeGenerator gen = new DOTTreeGenerator();
			Method m = DOTTreeGenerator.class.getMethod("toDOT", Tree.class);
			if (m == null)
			{
				logger.debug("Could not find toDOT method");
				return;
			}
			res = m.invoke(gen, node);
			if (res != null)
			{
				Writer fos = new FileWriter(output);
				try
				{
					fos.append(res.toString());
				}
				finally
				{
					fos.close();
				}
			}
		}
		catch (Throwable e)
		{
			logger.debug("Error writing AST: " + e.getMessage());
		}
	}
}
