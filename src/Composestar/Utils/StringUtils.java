package Composestar.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @see Composestar.Utils.StringUtilsTest
 */
public final class StringUtils
{
	/**
	 * Public constructor needed because this class is serialized into the
	 * repository for some reason.
	 */
	public StringUtils()
	{}

	public static String join(Collection parts, String glue)
	{
		if (parts.size() == 0)
		{
			return "";
		}

		StringBuffer sb = new StringBuffer();
		Iterator it = parts.iterator();

		sb.append(it.next());
		while (it.hasNext())
		{
			sb.append(glue).append(it.next());
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

	public static String capitalize(String s)
	{
		if (s == null || "".equals(s)) return s;
		
		char[] chars = s.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		
		return new String(chars);
	}
}
