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

package Composestar.Utils.Regex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * A simple matching buffer for a list of strings strings
 * 
 * @author Michiel Hendriks
 */
public class StringListBuffer implements MatchingBuffer
{
	protected List<String> buffer;

	protected int currentLocation;

	/**
	 * @param input
	 */
	public StringListBuffer(String input)
	{
		this(input.split("\\s"));
	}

	/**
	 * @param input
	 */
	public StringListBuffer(String[] input)
	{
		this(Arrays.asList(input));
	}

	/**
	 * @param input
	 */
	public StringListBuffer(Collection<String> input)
	{
		buffer = new ArrayList<String>();
		for (String s : input)
		{
			if (s.length() > 0)
			{
				buffer.add(s);
			}
		}
	}

	protected StringListBuffer(StringListBuffer basedon)
	{
		buffer = basedon.buffer;
		currentLocation = basedon.currentLocation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Utils.Regex.MatchingBuffer#consume()
	 */
	public void consume()
	{
		if (currentLocation < buffer.size())
		{
			currentLocation++;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Utils.Regex.MatchingBuffer#current()
	 */
	public String current()
	{
		if (currentLocation < buffer.size())
		{
			return buffer.get(currentLocation);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Utils.Regex.MatchingBuffer#fork()
	 */
	public MatchingBuffer fork()
	{
		return new StringListBuffer(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Utils.Regex.MatchingBuffer#next()
	 */
	public String next()
	{
		if (currentLocation < buffer.size() - 1)
		{
			return buffer.get(currentLocation + 1);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Utils.Regex.MatchingBuffer#previous()
	 */
	public String previous()
	{
		if (currentLocation > 0 && currentLocation < buffer.size())
		{
			return buffer.get(currentLocation - 1);
		}
		return null;
	}

}
