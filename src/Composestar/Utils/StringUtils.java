package Composestar.Utils;

import java.util.ArrayList;
import java.util.List;

public class StringUtils
{
	/**
	 * Public constructor needed because this class is
	 * serialized into the repository for some reason.
	 */
	public StringUtils() {}

	public static String join(List parts, String glue)
	{
		int count = parts.size();
		if (count == 0) return "";

		StringBuffer sb = new StringBuffer();
		sb.append(parts.get(0));
		for (int i = 1; i < count; i++)
			sb.append(glue).append(parts.get(i));
		
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
