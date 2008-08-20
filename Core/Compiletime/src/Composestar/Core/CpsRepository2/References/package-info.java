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

/**
 * This package contains interfaces for external references. An external
 * reference points to data which is alien to the CPS language. It depends on
 * information retrieved or produced somewhere else within Compose*. All
 * external references are soft-references, which means that they can loose the
 * direct association with the data they are referring to during
 * deserialization. References are commonly used in cases where information from
 * the language model (LAMA) is used, or in case of soft-references to qualified
 * CPS language elements like with references to filter modules.
 */
package Composestar.Core.CpsRepository2.References;

