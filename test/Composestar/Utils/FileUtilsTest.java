package Composestar.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.AssertionFailedError;
import junit.framework.ComparisonFailure;
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
	
	public void testGetExtension()
	{
		try
		{
			FileUtils.getExtension(null);
			throw new AssertionFailedError("IllegalArgumentException expected");
		}
		catch (IllegalArgumentException e)
		{
		}

		assertEquals(null, FileUtils.getExtension("foo"));
		assertEquals("bar", FileUtils.getExtension("foo.bar"));
		assertEquals("bar", FileUtils.getExtension("foo.quux.bar"));
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

		// assertEquals("", FileUtils.replaceExtension("", "")); // or throw exception?
		assertEquals("foo.baz", FileUtils.replaceExtension("foo.bar", "baz"));
		assertEquals("foo.baz", FileUtils.replaceExtension("foo.bar", ".baz"));
		assertEquals("foo.bar.xyz", FileUtils.replaceExtension("foo.bar.baz", ".xyz"));
	}
	
	public void testCopyWholeBytes()
	{
		byte[] input = new byte[1337];
		for (int i = 0; i < input.length; i++)
			input[i] = (byte)(i % 256);
			
		ByteArrayInputStream bis = null;
		ByteArrayOutputStream bos = null;
		try
		{
			bis = new ByteArrayInputStream(input);
			bos = new ByteArrayOutputStream();
			
			FileUtils.copy(bis, bos);			
		}
		catch (IOException e)
		{
			throw new AssertionFailedError(
					"IOException: " + e.getMessage());
		}
		finally
		{
			FileUtils.close(bis);
			FileUtils.close(bos);
		}

		assertEquals(input, bos.toByteArray());
	}
	
	public void testCopyWholeChars()
	{
		String input = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			
		StringReader sr = null;
		StringWriter sw = null;
		try
		{
			sr = new StringReader(input);
			sw = new StringWriter();
			
			FileUtils.copy(sr, sw);			
		}
		catch (IOException e)
		{
			throw new AssertionFailedError(
					"IOException: " + e.getMessage());
		}
		finally
		{
			FileUtils.close(sr);
			FileUtils.close(sw);
		}

		assertEquals(input, sw.toString());
	}

	public static void testCopyPartChars()
	{
		String input    = "ABCDE_GHI_KLMNOPQ_S_UVWXYZ";
		String expected = "ABCDEF_GHIJ_KLMNOPQR_ST_UVWXYZ";

		StringReader sr = null;
		StringWriter sw = null;
		try {
			sr = new StringReader(input);
			sw = new StringWriter();
			
			FileUtils.copy(sr, sw, 5);
			sw.write('F');
			FileUtils.copy(sr, sw, 4);
			sw.write('J');
			FileUtils.copy(sr, sw, 8);
			sw.write('R');
			FileUtils.copy(sr, sw, 2);
			sw.write('T');
			FileUtils.copy(sr, sw, 7);
		}
		catch (IOException e)
		{
			throw new AssertionFailedError(
					"IOException: " + e.getMessage());
		}
		finally {
			FileUtils.close(sr);
			FileUtils.close(sw);
		}

		assertEquals(expected, sw.toString());
	}
	
	protected static void assertEquals(byte[] expected, byte[] actual)
	{
		if (expected.length != actual.length)
		{
			throw new ComparisonFailure(
					"Array lengths do not match.", 
					"" + expected.length, "" + actual.length);
		}

		for (int i = 0; i < actual.length; i++)
		{
			if (expected[i] != actual[i])
			{
				throw new ComparisonFailure(
						"Array elements at index " + i + " do not match.", 
						"" + expected[i], "" + actual[i]);
			}
		}
	}
}
