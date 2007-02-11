package Composestar.Utils;

import java.util.Arrays;
import java.util.List;

import junit.framework.ComparisonFailure;
import junit.framework.TestCase;

public class StringUtilsTest extends TestCase
{
	public void testJoin1()
	{
		List p1 = Arrays.asList(new String[] {});
		assertEquals("", StringUtils.join(p1));

		List p2 = Arrays.asList(new String[] { "1", "2", "3", "4" });
		assertEquals("1 2 3 4", StringUtils.join(p2));
	}

	public void testJoin2()
	{
		List p1 = Arrays.asList(new String[] {});
		assertEquals("", StringUtils.join(p1, ","));

		List p2 = Arrays.asList(new String[] { "1", "2", "3", "4" });
		assertEquals("1,2,3,4", StringUtils.join(p2, ","));
	}

	public void testSplit()
	{
		String[] p1 = StringUtils.split("", ',');
		assertEquals(new String[] {}, p1);

		String[] p2 = StringUtils.split("x", ',');
		assertEquals(new String[] { "x" }, p2);

		String[] p3 = StringUtils.split("1,2,3,4", ',');
		assertEquals(new String[] { "1", "2", "3", "4" }, p3);
	}

	public void testCapitalize()
	{
		assertEquals(null, StringUtils.capitalize(null));
		assertEquals("", StringUtils.capitalize(""));
		assertEquals("Q", StringUtils.capitalize("q"));
		assertEquals("Foo", StringUtils.capitalize("foo"));
		assertEquals("Foo", StringUtils.capitalize("Foo"));
		assertEquals("BAR", StringUtils.capitalize("BAR"));
		assertEquals("Foo bar", StringUtils.capitalize("foo bar"));
	}

	private static void assertEquals(String message, Object[] expected, Object[] actual)
	{
		int el = expected.length;
		int al = actual.length;
		if (el != al)
		{
			throw new ComparisonFailure("Array lengths do not match.", "" + el, "" + al);
		}

		for (int i = 0; i < el; i++)
		{
			Object e = expected[i];
			Object a = actual[i];
			if (!e.equals(a))
			{
				throw new ComparisonFailure("Array elements at index " + i + " do not match.", e.toString(), a
						.toString());
			}
		}
	}

	private static void assertEquals(Object[] expected, Object[] actual)
	{
		assertEquals(null, expected, actual);
	}
}
