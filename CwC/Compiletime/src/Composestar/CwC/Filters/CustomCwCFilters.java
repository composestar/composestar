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

package Composestar.CwC.Filters;

import java.util.Collection;

import Composestar.Core.CpsProgramRepository.Filters.DefaultFilterFactory;
import Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator;
import Composestar.Core.RepositoryImplementation.DataStore;

/**
 * Interface for custom CFilters. This class will be used to register
 * FilterTypes, FilterActions and the CodeGenerators for the FilterActions. More
 * than one FilterType/FilterAction may be registered at the time. A default
 * constructor (with no arguments) is required for all implementing classes.
 * 
 * @author Michiel Hendriks
 */
public interface CustomCwCFilters
{
	/**
	 * Will be called at startup before any modules are executed but after the
	 * base environment is initialized. This method should register all filter
	 * types and actions with the repository. The factory can be used to access
	 * some default filter actions and filter types.
	 * 
	 * @param repository
	 * @param factory
	 */
	void registerFilters(DataStore repository, DefaultFilterFactory factory);

	/**
	 * Return the code generators for all custom filter actions defined by this
	 * class.
	 * 
	 * @return
	 */
	Collection<FilterActionCodeGenerator<String>> getCodeGenerators();
}
