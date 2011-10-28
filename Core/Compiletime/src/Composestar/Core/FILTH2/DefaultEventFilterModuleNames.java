/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2011 University of Twente.
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

package Composestar.Core.FILTH2;

/**
 *
 * @author roo
 */
public final class DefaultEventFilterModuleNames{
public static final String CONCERN = "CpsDefaultEventFMConcern";

public static final String FILTER_MODULE = "CpsDefaultEventFilterModule";

public static final String OUTPUT_FILTER = "CpsDefaultEventFilter";

/**
 * The fully qualified name of the filtermodule of the default inner
 * dispatch filter.
 */
public static final String FQN_FILTER_MODULE = CONCERN + "." + FILTER_MODULE;


/**
 * The fully qualified name of the default send output filter
 */
public static final String FQN_OUTER_FILTER = FQN_FILTER_MODULE + "." + OUTPUT_FILTER;

private DefaultEventFilterModuleNames()
{}
}
