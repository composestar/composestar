package Composestar.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

public class StringConverter
{
	private StringConverter()
	{}

	/**
	 * Converts a String to an int. If the value is not an int the default value
	 * is returned.
	 * 
	 * @param value String The key to search for
	 * @param defaultvalue int The defaultvalue that may be returned.
	 * @return int The int value of the key
	 * @roseuid 404DCD030018
	 */
	public static int stringToInt(String value, int defaultvalue)
	{
		int i;
		try
		{
			i = Integer.valueOf(value).intValue();
		}
		catch (Exception e)
		{
			i = defaultvalue;
		}
		return i;
	}

	/**
	 * Converts an int to a String
	 * 
	 * @param value int The value
	 * @return String The string value
	 * @roseuid 404DCD030066
	 */
	public static String intToString(String value)
	{
		return value + "";
	}

	/**
	 * Gets the iterator containing the integer properties of the given value.
	 * If the value is not an int the value is skipped.
	 * 
	 * @param value String The value to convert
	 * @param separator String The separator character(s)
	 * @return Iterator The list of int values of the value
	 * @roseuid 404DCD0300A5
	 */
	public static Iterator stringToIntList(String value, String separator)
	{
		ArrayList list = new ArrayList();
		StringTokenizer st = new StringTokenizer(value, separator, false);
		while (st.hasMoreTokens())
		{
			String tmp = st.nextToken();
			try
			{
				list.add(Integer.valueOf(tmp));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return list.iterator();
	}

	/**
	 * Gets the iterator containing the integer properties of the given value.
	 * If the value is not an int the value is skipped.
	 * 
	 * @param value String The key to search for
	 * @return Iterator The list of int values of the value
	 * @roseuid 404DCD0300E3
	 */
	public static Iterator stringToIntList(String value)
	{
		return StringConverter.stringToIntList(value, ",");
	}

	/**
	 * Converts a list of ints to a String
	 * 
	 * @param values int[] The value list
	 * @param separator String The separator character(s)
	 * @return String The string containing the ints
	 * @roseuid 404DCD030122
	 */
	public static String intListToString(int[] values, String separator)
	{
		StringBuffer buffer = new StringBuffer(values.length * 2);
		for (int i = 0; i < values.length; i++)
		{
			if (i != 0)
			{
				buffer.append(separator);
			}
			buffer.append(values[i]);
		}
		return buffer.toString();
	}

	/**
	 * Converts a list of ints to a String
	 * 
	 * @param values int[] The value list
	 * @return String The string containing the ints
	 * @roseuid 404DCD030160
	 */
	public static String intListToString(int[] values)
	{
		return StringConverter.intListToString(values, ",");
	}

	/**
	 * Gets the iterator containing the elements for the given values.
	 * 
	 * @param value String The value to convert
	 * @param separator String The separator character(s)
	 * @return Iterator The list of values of the value
	 * @roseuid 404DCD03018F
	 */
	public static Iterator stringToStringList(String value, String separator)
	{
		ArrayList list = new ArrayList();
		StringTokenizer st = new StringTokenizer(value, separator, false);
		while (st.hasMoreTokens())
		{
			list.add(st.nextToken());
		}
		return list.iterator();
	}

	/**
	 * Gets the iterator containing the elements for the given values.
	 * 
	 * @param value String The value to convert
	 * @return Iterator The list of values of the value
	 * @roseuid 404DCD0301ED
	 */
	public static Iterator stringToStringList(String value)
	{
		return StringConverter.stringToStringList(value, ",");
	}

	/**
	 * Convert a list to a single String.
	 * 
	 * @param values String[] The value
	 * @param separator String The separator character(s)
	 * @return String The string containing the ints
	 * @roseuid 404DCD03020C
	 */
	public static String stringListToString(String[] values, String separator)
	{
		StringBuffer buffer = new StringBuffer(values.length * 2);
		for (int i = 0; i < values.length; i++)
		{
			if (i != 0)
			{
				buffer.append(separator);
			}
			buffer.append(values[i]);
		}
		return buffer.toString();
	}

	/**
	 * Convert a list to a single String.
	 * 
	 * @param values String[] The value
	 * @param separator String The separator character(s)
	 * @return String The string containing the ints
	 * @roseuid 404DCD03025A
	 */
	public static String stringListToString(String[] values)
	{
		return StringConverter.stringListToString(values, ",");
	}
}
