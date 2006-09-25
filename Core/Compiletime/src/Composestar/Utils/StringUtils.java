package Composestar.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StringUtils
{
	private StringUtils() {}

	public static String join(List parts, String glue)
	{
		StringBuffer sb = new StringBuffer();
		Iterator it = parts.iterator();
		while (it.hasNext())
		{
			Object part = it.next();
			sb.append(part).append(glue);		
		}
		return sb.toString();
	}
	
	public static String join(List parts)
	{
		return join(parts, " ");
	}
	
	public static String[] split(String whole, char delim)
	{
		List parts = new ArrayList();
		int length = whole.length();
		int cur = 0;
		
		do {
			int pos = whole.indexOf(delim, cur);
			if (pos == -1) break;

			parts.add(whole.substring(cur, pos));
			cur = pos + 1;
		} while (cur < length);
		
		if (cur < length)
			parts.add(whole.substring(cur));
		
		String[] result = new String[parts.size()];
		parts.toArray(result);
		return result;
	}
}
