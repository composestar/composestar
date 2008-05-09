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

package Composestar.Core.FILTH2.Model;

import Composestar.Core.SANE.FilterModuleSuperImposition;

/**
 * Action implementation used by Compose* to associate the filter module
 * superimposition with.
 * 
 * @author Michiel Hendriks
 */
public class FilterModuleAction extends Action
{
	protected FilterModuleSuperImposition fmsi;

	public FilterModuleAction(FilterModuleSuperImposition actionFmsi)
	{
		super(actionFmsi.getFilterModule().getQualifiedName());
		fmsi = actionFmsi;
	}

	/**
	 * @return the associated filter module superimposition
	 */
	public FilterModuleSuperImposition getFMSI()
	{
		return fmsi;
	}

}
