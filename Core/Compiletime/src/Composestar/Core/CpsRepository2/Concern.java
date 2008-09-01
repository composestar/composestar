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

package Composestar.Core.CpsRepository2;

import java.util.List;

/**
 * The base interface for all concern types. A concern is a root element in the
 * repository.
 * 
 * @author Michiel Hendriks
 */
public interface Concern extends QualifiedRepositoryEntity
{
	/**
	 * @return The namespace of this concern. This is the part of the fually
	 *         qualified name of a concern before the concern's name. Each
	 *         segment of the namespace is divided by periods. Returns an empty
	 *         string when the concern has no namespace.
	 * @see #getNamespaceAsList()
	 */
	String getNamespace();

	/**
	 * @return The namespace of the concern in an ordered list. Returns an empty
	 *         list when the concern has no namespace. The returned list is read
	 *         only.
	 * @see #getNamespace()
	 */
	List<String> getNamespaceAsList();
}