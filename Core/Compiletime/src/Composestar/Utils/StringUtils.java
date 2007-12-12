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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public final class StringUtils
{
	private StringUtils()
	{}

	public static String join(Collection<String> parts, String glue)
	{
		if (parts.size() == 0)
		{
			return "";
		}

		StringBuffer sb = new StringBuffer();
		Iterator<String> it = parts.iterator();

		sb.append(it.next());
		while (it.hasNext())
		{
			sb.append(glue).append(it.next());
		}

		return sb.toString();
	}

	public static String join(Collection<String> parts)
	{
		return join(parts, " ");
	}

	/**
	 * @deprecated Use String.split()
	 * @param whole
	 * @param delim
	 * @return
	 */
	@Deprecated
	public static String[] split(String whole, char delim)
	{
		List<String> parts = new ArrayList<String>();
		int length = whole.length();
		int cur = 0;

		do
		{
			int pos = whole.indexOf(delim, cur);
			if (pos == -1)
			{
				break;
			}

			parts.add(whole.substring(cur, pos));
			cur = pos + 1;
		} while (cur < length);

		if (cur < length)
		{
			parts.add(whole.substring(cur));
		}

		String[] result = new String[parts.size()];
		parts.toArray(result);
		return result;
	}
}
