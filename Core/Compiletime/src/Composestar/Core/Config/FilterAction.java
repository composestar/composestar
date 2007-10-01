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

/**
 * A filter action specification. Referred to by the FilterType class.
 * 
 * @author Michiel Hendrik
 * @see FilterType
 */
public class FilterAction implements Serializable
{
	private static final long serialVersionUID = 7354266551225465392L;

	/**
	 * The basic name of the filter action. Used in the filter type
	 * specification.
	 */
	protected String name;

	/**
	 * The fully qualified name of the filter.
	 */
	protected String fullName;

	/**
	 * The library that contains the implementation of this filter action.
	 */
	protected String library;

	/**
	 * Create a Joint Point Context. This might not always be set.
	 */
	protected boolean createJpc;

	/**
	 * The flowbehavior of the filter action.
	 */
	protected int flowBehavior;

	/**
	 * The message change behavior.
	 */
	protected int messageChangeBehavior;

	public FilterAction()
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
		name = inName.trim();
	}

	/**
	 * @return the fullName
	 */
	public String getFullName()
	{
		return fullName;
	}

	/**
	 * @param inFullName the fullName to set
	 */
	public void setFullName(String inFullName)
	{
		if (inFullName == null || inFullName.trim().length() == 0)
		{
			throw new IllegalArgumentException("FullName can not be null or empty");
		}
		fullName = inFullName.trim();
	}

	/**
	 * @return the library
	 */
	public String getLibrary()
	{
		return library;
	}

	/**
	 * @param library the library to set
	 */
	public void setLibrary(String inLibrary)
	{
		if (inLibrary == null || inLibrary.trim().length() == 0)
		{
			throw new IllegalArgumentException("Library can not be null or empty");
		}
		library = inLibrary.trim();
	}

	/**
	 * @return the createJpc
	 */
	public boolean isCreateJpc()
	{
		return createJpc;
	}

	/**
	 * @param inCreateJpc the createJpc to set
	 */
	public void setCreateJpc(boolean inCreateJpc)
	{
		createJpc = inCreateJpc;
	}

	/**
	 * @return the flowBehavior
	 */
	public int getFlowBehavior()
	{
		return flowBehavior;
	}

	/**
	 * @param inFlowBehavior the flowBehavior to set
	 */
	public void setFlowBehavior(int inFlowBehavior)
	{
		flowBehavior = inFlowBehavior;
	}

	/**
	 * @return the messageChangeBehavior
	 */
	public int getMessageChangeBehavior()
	{
		return messageChangeBehavior;
	}

	/**
	 * @param inMessageChangeBehavior the messageChangeBehavior to set
	 */
	public void setMessageChangeBehavior(int inMessageChangeBehavior)
	{
		messageChangeBehavior = inMessageChangeBehavior;
	}

}
