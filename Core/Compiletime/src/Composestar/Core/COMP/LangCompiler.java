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
package Composestar.Core.COMP;

import java.util.Set;

import Composestar.Core.Config.Project;
import Composestar.Core.Config.Source;
import Composestar.Core.Config.SourceCompiler;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Resources.CommonResources;

/**
 * Defines the generic interface for all language compilers
 */
public interface LangCompiler
{
	/**
	 * Will be called from {@link SourceCompiler} when an instance to this
	 * implementor is requested by a module.
	 * 
	 * @param compilerConfig
	 */
	void setCompilerConfig(SourceCompiler compilerConfig);

	/**
	 * Must be called by a module that uses an implementation to set the current
	 * commonresources
	 * 
	 * @param resc
	 */
	void setCommonResources(CommonResources resc);

	/**
	 * Compile the actual sources
	 * 
	 * @param p the project that has to be called
	 * @param sources a set of sources that must be compiled by this compiler
	 * @throws CompilerException
	 * @throws ModuleException
	 */
	void compileSources(Project p, Set<Source> sources) throws CompilerException;

	/**
	 * Compile the created dummies
	 * 
	 * @param p the project that has to be called
	 * @param sources a set of sources that must be compiled by this compiler
	 * @throws CompilerException
	 */
	void compileDummies(Project p, Set<Source> sources) throws CompilerException;
}
