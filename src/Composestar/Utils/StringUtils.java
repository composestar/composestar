package Composestar.Utils;

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
}
