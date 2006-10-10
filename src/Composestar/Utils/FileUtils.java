//Created on Nov 11, 2004 by wilke
package Composestar.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Contains utility methods that have to do with file handling;
 * e.g. converting backslashes in filenames to slashes
 */
public class FileUtils
{
	private FileUtils() {}

	/**
	 * Adds double quotes around the specified filename.
	 */
	public static String quote(String filename)
	{
		if (filename == null)
			throw new IllegalArgumentException("filename cannot be null");
		
		return '"' + filename + '"';
	}
	
	/**
	 * Removes double quotes around the specified filename.
	 */
	public static String unquote(String filename)
	{
		if (filename == null)
			throw new IllegalArgumentException("filename cannot be null");

		filename = filename.trim();
		
		if ("".equals(filename) || "\"".equals(filename))
			return "";
		
		if (filename.charAt(0) == '"')
			filename = filename.substring(1);
		
		int end = filename.length() - 1;
		if (filename.charAt(end) == '"')
			filename = filename.substring(0, end);
		
		return filename;
	}

	/**
	 * This method converts filenames so they contain only 'slashes' instead of
	 * backslashes. This works on all platforms (Java will automatically convert
	 * to backslashes on the Windows platform, which is about the only platform
	 * using backslashes as a directory separator).
	 * 
	 * Because backslashes are often interpreted as escape characters (e.g. by
	 * the prolog engine!) we can't have them in filenames - besides, it would
	 * only work in Windows.
	 * 
	 * @param name A filename, possibly containing backslashes.
	 * @return The filename, with backslashes converted to slashes.
	 * 
	 * TODO: perhaps rename to 'normalizeFilename'?
	 */
	public static String fixFilename(String name)
	{
		return name.replace('\\', '/');
	}

	/**
	 * FIXME: wtf!? It makes no sense I tell you!
	 * The places where this method is used should be checked out, 
	 * to see what functionality is really needed. 
	 */
	public static String fixSlashes(String command)
	{
		char[] cmd = command.toCharArray();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < cmd.length; i++)
		{
			if (cmd[i] == '/')
				buffer.append(File.separator).append(File.separator);
			else if (cmd[i] == '\\')
				buffer.append(File.separator).append(File.separator);
			else
				buffer.append(cmd[i]);
		}
		return buffer.toString();
	}

	/**
	 * Returns true if the specified filename refers to an existing file.
	 */
	public static boolean fileExist(String filename)
	{
		return new File(filename).exists();
	}
	
	/**
	 * Returns true if the file specified by the filename was succesfully deleted.
	 */
	public static boolean delete(String filename)
	{
		return new File(filename).delete();
	}

	public static void copyFile(String dest, String source) throws IOException
	{
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new BufferedInputStream(new FileInputStream(source));
			os = new BufferedOutputStream(new FileOutputStream(dest));

			// transfer bytes from in to out
			byte[] buf = new byte[65536];
			int len;
			while ((len = is.read(buf)) > 0)
				os.write(buf, 0, len);
		}
		finally {
			close(is);
			close(os);
		}
	}

	public static String removeExtension(String filename)
	{
		int lastdot = filename.lastIndexOf('.');
		if (lastdot > 0)
			return filename.substring(0, lastdot);

		return filename;
	}
	
	public static String replaceExtension(String filename, String newext)
	{
		return removeExtension(filename) + "." + newext;
	}

	/**
	 * Create a name for an output file based on the input name; this is used
	 * e.g. when creating dummies. The conversion works as follows: a sourceName
	 * should like this: basePath/project/specific/subdirs/SomeSourceFile.ext
	 * The converted version will look like:
	 * basePath/prefix/project/specific/subdirs/SomeSourceFile.ext
	 * 
	 * @param basePath
	 * @param prefix the prefix to be prepended before the project-specific directories
	 * @param sourceName the name of the original file
	 * @return The converted (output)-filename.
	 */
	public static String createOutputFilename(String basePath, String prefix, String sourceName)
		throws Exception
	{
		if (!sourceName.startsWith(basePath))
			throw new Exception("File + '" + sourceName
					+ "' should be within the project basePath '" + basePath + "'");

		return basePath + prefix + sourceName.substring(basePath.length());
	}

	/**
	 * @param path A valid directory path
	 * @return Whether the entire path could be created (also true if it already exists)
	 */
	public static boolean createFullPath(String path)
	{		
		try {
			File f = new File(path);
			
			if (f.exists())
				return true;
			else
				return f.mkdirs(); // Returns false if the string is not a valid pathname
		}
		catch (SecurityException e) { 
			// We don't really care about the reason; the important thing is the path could not be created
			return false;
		}
	}

	public static String getFilenamePart(String pathToFile)
	{
		return new File(pathToFile).getName();
//		int pathEnd = pathToFile.lastIndexOf('/');
//		if (pathEnd > 0)
//			return pathToFile.substring(pathEnd + 1);
//		else
//			return pathToFile;
	}

	public static String getDirectoryPart(String pathToFile)
	{
//		return new File(pathToFile).getParent();
		int pathEnd = pathToFile.lastIndexOf('/');
		if (pathEnd > 0)
			return pathToFile.substring(0, pathEnd);
		else
			return pathToFile;
	}

	/**
	 * Get a file stream for the SAX parser whitout the Root element could not
	 * be found exception. This is caused by a BOM character which is skipped by
	 * this file reader.
	 */
	public static FileInputStream getCleanInputStream(File xmlFile)
		throws FileNotFoundException, IOException
	{
		FileInputStream in = new FileInputStream(xmlFile);
		int bomKount = getBOMCount(xmlFile);
		if (bomKount > 0)
		{
			byte[] buf = new byte[bomKount];
			in.read(buf);
		}
		return in;
	}

	/**
	 * Get count of leading characters that denote the byte order mark, part of
	 * the unicode standard. These make SaxParser barf
	 */
	private static int getBOMCount(File xmlFile) 
		throws FileNotFoundException, IOException
	{
		DataInputStream din = new DataInputStream(new FileInputStream(xmlFile));
		int bomKount = 0;
		byte b = din.readByte();
		while (b < 0)
		{
			bomKount++;
			b = din.readByte();
		}
		din.close();
		// System.out.println("Skipping BOM, " + bomKount + " bytes");
		return bomKount;
	}

	/**
	 * Closes the specified Reader instance, provided it is not null.
	 */
	public static void close(Reader reader)
	{
		try {
			if (reader != null)
				reader.close();
		}
		catch (IOException e) {
			// this shouldnt happen
			throw new RuntimeException("Unable to close reader: " + e.getMessage());
		}
	}

	/**
	 * Closes the specified Writer instance, provided it is not null.
	 */
	public static void close(Writer writer)
	{
		try {
			if (writer != null)
				writer.close();
		}
		catch (IOException e) {
			// this shouldnt happen
			throw new RuntimeException("Unable to close writer: " + e.getMessage());
		}
	}
	
	/**
	 * Closes the specified InputStream instance, provided it is not null.
	 */
	public static void close(InputStream is)
	{
		try {
			if (is != null)
				is.close();
		}
		catch (IOException e) {
			// this shouldnt happen
			throw new RuntimeException("Unable to close stream: " + e.getMessage());
		}		
	}

	/**
	 * Closes the specified OutputStream instance, provided it is not null.
	 */
	public static void close(OutputStream os)
	{
		try {
			if (os != null)
				os.close();
		}
		catch (IOException e) {
			// this shouldnt happen
			throw new RuntimeException("Unable to close stream: " + e.getMessage());
		}		
	}
}