package Composestar.Utils;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

public class FileUtilsTest extends TestCase
{
	public void testQuote()
	{
		try
		{
			FileUtils.quote(null);
			throw new AssertionFailedError("IllegalArgumentException expected");
		}
		catch (IllegalArgumentException e)
		{
		}

		assertEquals("\"\"", FileUtils.quote(""));
		assertEquals("\"readme.txt\"", FileUtils.quote("readme.txt"));
	}

	public void testUnquote()
	{
		try
		{
			FileUtils.unquote(null);
			throw new AssertionFailedError("IllegalArgumentException expected");
		}
		catch (IllegalArgumentException e)
		{
		}

		assertEquals("", FileUtils.unquote(""));
		assertEquals("", FileUtils.unquote("\""));
		assertEquals("", FileUtils.unquote("\"\""));
		assertEquals("readme.txt", FileUtils.unquote("\"readme.txt\""));
		assertEquals("foo\"bar.txt", FileUtils.unquote("\"foo\"bar.txt\""));
	}

	public void testRemoveExtension()
	{
		try
		{
			FileUtils.removeExtension(null);
			throw new AssertionFailedError("IllegalArgumentException expected");
		}
		catch (IllegalArgumentException e)
		{
		}

		assertEquals("", FileUtils.removeExtension(""));
		assertEquals("foo", FileUtils.removeExtension("foo.bar"));
		assertEquals("foo.bar", FileUtils.removeExtension("foo.bar.baz"));
	}

	public void testReplaceExtension()
	{
		try
		{
			FileUtils.replaceExtension(null, null);
			throw new AssertionFailedError("IllegalArgumentException expected");
		}
		catch (IllegalArgumentException e)
		{
		}

		try
		{
			FileUtils.replaceExtension("", null);
			throw new AssertionFailedError("IllegalArgumentException expected");
		}
		catch (IllegalArgumentException e)
		{
		}

		try
		{
			FileUtils.replaceExtension(null, "");
			throw new AssertionFailedError("IllegalArgumentException expected");
		}
		catch (IllegalArgumentException e)
		{
		}

		// assertEquals("", FileUtils.replaceExtension("", "")); // should throw
		// exception?
		assertEquals("foo.baz", FileUtils.replaceExtension("foo.bar", "baz"));
		assertEquals("foo.baz", FileUtils.replaceExtension("foo.bar", ".baz"));
		assertEquals("foo.bar.xyz", FileUtils.replaceExtension("foo.bar.baz", ".xyz"));
	}
}
