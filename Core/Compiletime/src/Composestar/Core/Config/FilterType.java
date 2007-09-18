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

package Composestar.Core.Config;

import java.io.Serializable;

public class FilterType implements Serializable
{
	private static final long serialVersionUID = -8825182248197802610L;

	protected static final String DEFAULT_ACTION = "ContinueAction";

	/**
	 * The filter type name
	 */
	protected String name;

	protected String acceptCallAction = DEFAULT_ACTION;

	protected String rejectCallAction = DEFAULT_ACTION;

	protected String acceptReturnAction = DEFAULT_ACTION;

	protected String rejectReturnAction = DEFAULT_ACTION;

	public FilterType()
	{}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param inName the name to set
	 */
	public void setName(String inName)
	{
		if (inName == null || inName.trim().length() == 0)
		{
			throw new IllegalArgumentException("Name can not be null or empty");
		}
		name = inName;
	}

	/**
	 * @return the acceptCallAction
	 */
	public String getAcceptCallAction()
	{
		return acceptCallAction;
	}

	/**
	 * @param inAcceptCallAction the acceptCallAction to set
	 */
	public void setAcceptCallAction(String inAcceptCallAction)
	{
		if (inAcceptCallAction == null)
		{
			inAcceptCallAction = DEFAULT_ACTION;
		}
		acceptCallAction = inAcceptCallAction.trim();
	}

	/**
	 * @return the rejectCallAction
	 */
	public String getRejectCallAction()
	{
		return rejectCallAction;
	}

	/**
	 * @param inRejectCallAction the rejectCallAction to set
	 */
	public void setRejectCallAction(String inRejectCallAction)
	{
		if (inRejectCallAction == null)
		{
			inRejectCallAction = DEFAULT_ACTION;
		}
		rejectCallAction = inRejectCallAction.trim();
	}

	/**
	 * @return the acceptReturnAction
	 */
	public String getAcceptReturnAction()
	{
		return acceptReturnAction;
	}

	/**
	 * @param inAcceptReturnAction the acceptReturnAction to set
	 */
	public void setAcceptReturnAction(String inAcceptReturnAction)
	{
		if (inAcceptReturnAction == null)
		{
			inAcceptReturnAction = DEFAULT_ACTION;
		}
		acceptReturnAction = inAcceptReturnAction.trim();
	}

	/**
	 * @return the rejectReturnAction
	 */
	public String getRejectReturnAction()
	{
		return rejectReturnAction;
	}

	/**
	 * @param inRejectReturnAction the rejectReturnAction to set
	 */
	public void setRejectReturnAction(String inRejectReturnAction)
	{
		if (inRejectReturnAction == null)
		{
			inRejectReturnAction = DEFAULT_ACTION;
		}
		rejectReturnAction = inRejectReturnAction.trim();
	}

}
