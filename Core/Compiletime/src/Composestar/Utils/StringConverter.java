/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

public final class StringConverter
{
	private StringConverter()
	{}

	/**
	 * Gets the iterator containing the elements for the given values.
	 * 
	 * @param value String The value to convert
	 * @param separator String The separator character(s)
	 * @return Iterator The list of values of the value
	 * @deprecated Use String.split(...)
	 */
	public static Iterator<String> stringToStringList(String value, String separator)
	{
		ArrayList<String> list = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(value, separator, false);
		while (st.hasMoreTokens())
		{
			list.add(st.nextToken());
		}
		return list.iterator();
	}
}
