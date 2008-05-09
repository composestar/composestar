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

/**
 * A phantom action is only used for the right hand sides of constraints. They
 * should not appear in the eventual order. Phantom actions are used when a
 * constraint references a action that is not part of the action set (e.g. a
 * filter module that is not superimposed).
 * 
 * @author Michiel Hendriks
 */
public class PhantomAction extends Action
{
	public PhantomAction(String actionName)
	{
		super(actionName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.FILTH2.Model.Action#addConstraint(Composestar.Core.FILTH2.Model.Constraint)
	 */
	@Override
	public void addConstraint(Constraint constraint)
	{
		throw new UnsupportedOperationException("PhantomActions can not have constraints");
	}
}
