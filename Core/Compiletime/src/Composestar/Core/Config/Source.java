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
 * @author Michiel Hendriks
 */
public class Source extends FileCollection implements Serializable
{
	private static final long serialVersionUID = -2838432989846583032L;

	/**
	 * Language override. Not supported by most platforms.
	 */
	protected String language;

	public Source()
	{}

	public String getLanguage()
	{
		return language;
	}

	/**
	 * Return the language override. Returns null when there is no override.
	 * 
	 * @param inLanguage
	 */
	public void setLanguage(String inLanguage)
	{
		if (inLanguage == null || inLanguage.trim().length() == 0)
		{
			language = null;
		}
		else
		{
			language = inLanguage;
		}
	}

}
