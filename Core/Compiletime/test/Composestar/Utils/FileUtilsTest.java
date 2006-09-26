package Composestar.Utils;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

public class FileUtilsTest extends TestCase
{
	public void testQuote()
	{
		try {
			FileUtils.quote(null);
			throw new AssertionFailedError("IllegalArgumentException expected");
		}
		catch (IllegalArgumentException e) {
			// ok
		}
		
		assertEquals("\"\"", FileUtils.quote(""));
		assertEquals("\"readme.txt\"", FileUtils.quote("readme.txt"));
	}
	
	public void testUnquote()
	{
		try {
			FileUtils.unquote(null);
			throw new AssertionFailedError("IllegalArgumentException expected");
		}
		catch (IllegalArgumentException e) {
			// ok
		}
		
		assertEquals("", FileUtils.unquote(""));
		assertEquals("", FileUtils.unquote("\""));
		assertEquals("", FileUtils.unquote("\"\""));
		assertEquals("readme.txt", FileUtils.unquote("\"readme.txt\""));
		assertEquals("foo\"bar.txt", FileUtils.unquote("\"foo\"bar.txt\""));
	}
}
