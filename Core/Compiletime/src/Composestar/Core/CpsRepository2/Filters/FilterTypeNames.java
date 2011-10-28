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

package Composestar.Core.CpsRepository2.Filters;

/**
 * The names for the default filter types.
 * 
 * @author Michiel Hendriks
 */
public final class FilterTypeNames
{
	public static final String AFTER = "After";

	/**
	 * @deprecated use {@link #AFTER}
	 */
	@Deprecated
	public static final String APPEND = "Append";

	public static final String BEFORE = "Before";

	public static final String DISPATCH = "Dispatch";

	/**
	 * Like {@link #ERROR} except that it throws an exception when accepted
	 */
	public static final String EXCEPTION = "Exception";

	public static final String ERROR = "Error";

	public static final String META = "Meta";

	/**
	 * @deprecated use {@link #BEFORE}
	 */
	@Deprecated
	public static final String PREPEND = "Prepend";

	public static final String SEND = "Send";

	/**
	 * Only performs message substitution.
	 * 
	 * @deprecated use {@link #VOID}
	 */
	@Deprecated
	public static final String SUBSTITUTION = "Substitution";

	/**
	 * This filter type does nothing when accepted
	 */
	public static final String VOID = "Void";
	
	public static final String RESULT = "Result";

	private FilterTypeNames()
	{}
}
