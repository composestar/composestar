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

package Composestar.Core.CpsRepository2.SISpec;

import java.util.Collection;

import Composestar.Core.CpsRepository2.QualifiedRepositoryEntity;
import Composestar.Core.LAMA.ProgramElement;

/**
 * A selector makes a selection of program elements which can in turn be used by
 * annotation binding, or in filter module binding. The exact type of program
 * element in undefined, and should be validated where the selector is used.
 * 
 * @author Michiel Hendriks
 */
public interface Selector extends QualifiedRepositoryEntity
{
	/**
	 * @return The collection of program elements selected by this selector.
	 */
	Collection<ProgramElement> getSelection();
}
