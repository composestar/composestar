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

package Composestar.CwC.INLINE.CodeGen;

import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;

import Composestar.Core.LAMA.MethodInfo;

/**
 * The abstract header file generator is used to create a header file with
 * method declarations of methods that are used by the filter code. Within C all
 * methods must be declared in a single C file. A dispatch could be made to a
 * method that is implemented in a different file, but not declared in the
 * current file. This call implements some functionality to construct a special
 * header file that includes these declarations.
 * 
 * @author Michiel Hendriks
 */
public abstract class AbstractHeaderFileGenerator
{
	protected Set<MethodInfo> methods;

	public AbstractHeaderFileGenerator()
	{
		methods = new HashSet<MethodInfo>();
	}

	public void addMethod(MethodInfo mi)
	{
		methods.add(mi);
	}

	public boolean hasMethods()
	{
		return methods.size() > 0;
	}

	/**
	 * Write the header file to this writer
	 * 
	 * @param target
	 */
	public abstract boolean generateHeader(FileWriter target);
}
