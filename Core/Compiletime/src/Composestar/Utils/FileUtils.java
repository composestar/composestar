/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Contains utility methods that have to do with file handling; e.g. converting
 * backslashes in filenames to slashes
 */
public final class FileUtils
{
	private static final int BUFSIZE = 1024;

	private FileUtils()
	{}

	/**
	 * Adds double quotes around the specified filename.
	 */
	public static String quote(String filename)
	{
		if (filename == null)
		{
			throw new IllegalArgumentException("filename cannot be null");
		}

		return '"' + filename + '"';
	}

	/**
	 * Removes double quotes around the specified filename.
	 */
	public static String unquote(String filename)
	{
		if (filename == null)
		{
			throw new IllegalArgumentException("filename cannot be null");
		}

		filename = filename.trim();

		if ("".equals(filename) || "\"".equals(filename))
		{
			return "";
		}

		if (filename.charAt(0) == '"')
		{
			filename = filename.substring(1);
		}

		int end = filename.length() - 1;
		if (filename.charAt(end) == '"')
		{
			filename = filename.substring(0, end);
		}

		return filename;
	}

	/**
	 * This method converts filenames so they contain only 'slashes' instead of
	 * backslashes. This works on all platforms (Java will automatically convert
	 * to backslashes on the Windows platform, which is about the only platform
	 * using backslashes as a directory separator). Because backslashes are
	 * often interpreted as escape characters (e.g. by the prolog engine!) we
	 * can't have them in filenames - besides, it would only work in Windows.
	 * 
	 * @param name A filename, possibly containing backslashes.
	 * @return The filename, with all backslashes converted to slashes.
	 */
	public static String normalizeFilename(String name)
	{
		return name.replace('\\', '/');
	}

	/**
	 * @deprecated use normalizeFilename
	 */
	@Deprecated
	public static String fixFilename(String name)
	{
		return normalizeFilename(name);
	}

	/**
	 * Returns true if the specified filename refers to an existing file.
	 */
	public static boolean fileExist(String filename)
	{
		return new File(filename).exists();
	}

	/**
	 * Returns true if the file specified by the filename was succesfully
	 * deleted.
	 */
	public static boolean delete(String filename)
	{
		return new File(filename).delete();
	}

	public static boolean deleteIfExists(String filename)
	{
		File f = new File(filename);
		if (f.exists())
		{
			return f.delete();
		}
		else
		{
			return true;
		}
	}

	public static void copyFile(String dest, String source) throws IOException
	{
		copyFile(new File(dest), new File(source));
	}

	public static void copyFile(File dest, File source) throws IOException
	{
		InputStream is = null;
		OutputStream os = null;
		if (source.equals(dest))
		{
			return;
		}
		try
		{
			is = new BufferedInputStream(new FileInputStream(source));
			os = new BufferedOutputStream(new FileOutputStream(dest));

			copy(is, os);
		}
		finally
		{
			close(is);
			close(os);
		}
	}

	public static void copy(InputStream is, OutputStream os) throws IOException
	{
		byte[] buffer = new byte[BUFSIZE];

		int read;
		while ((read = is.read(buffer)) > 0)
		{
			os.write(buffer, 0, read);
		}
	}

	public static void copy(Reader r, Writer w) throws IOException
	{
		char[] buffer = new char[BUFSIZE];

		int read;
		while ((read = r.read(buffer)) > 0)
		{
			w.write(buffer, 0, read);
		}
	}

	public static void copy(Reader r, Writer w, int count) throws IOException
	{
		char[] buffer = new char[count];

		int read = 0;
		while (read != count)
		{
			read += r.read(buffer, read, count - read);
		}
		w.write(buffer);
	}

	public static String getExtension(String filename)
	{
		if (filename == null)
		{
			throw new IllegalArgumentException("filename can not be null");
		}

		int lastdot = filename.lastIndexOf('.');
		if (lastdot == -1)
		{
			return null;
		}
		else
		{
			return filename.substring(lastdot + 1);
		}
	}

	public static String getExtension(File filename)
	{
		if (filename == null)
		{
			throw new IllegalArgumentException("filename can not be null");
		}
		return getExtension(filename.toString());
	}

	public static String removeExtension(String filename)
	{
		if (filename == null)
		{
			throw new IllegalArgumentException("filename can not be null");
		}

		int lastdot = filename.lastIndexOf('.');
		if (lastdot == -1)
		{
			return filename;
		}
		else
		{
			return filename.substring(0, lastdot);
		}
	}

	public static String replaceExtension(String filename, String newext)
	{
		if (filename == null)
		{
			throw new IllegalArgumentException("filename can not be null");
		}

		if (newext == null)
		{
			throw new IllegalArgumentException("newext can not be null");
		}

		StringBuffer sb = new StringBuffer(filename.length() + newext.length());
		sb.append(removeExtension(filename));
		if (!newext.startsWith("."))
		{
			sb.append(".");
		}
		sb.append(newext);

		return sb.toString();
	}

	/**
	 * Create a name for an output file based on the input name; this is used
	 * e.g. when creating dummies. The conversion works as follows: A sourceName
	 * should look like this:
	 * "basePath/project/specific/subdirs/SomeSourceFile.ext". The converted
	 * version will look like:
	 * "basePath/prefix/project/specific/subdirs/SomeSourceFile.ext".
	 * 
	 * @param basePath
	 * @param prefix the prefix to be prepended before the project-specific
	 *            directories
	 * @param sourceName the name of the original file
	 * @return The converted (output)-filename.
	 */
	public static String createOutputFilename(String basePath, String prefix, String sourceName)
	{
		if (!sourceName.startsWith(basePath))
		{
			throw new IllegalArgumentException("File + '" + sourceName + "' should be within the project basePath '"
					+ basePath + "'");
		}

		return basePath + prefix + sourceName.substring(basePath.length());
	}

	/**
	 * Create a file in destDir based on source with the same relative path as
	 * source has in base. If base is "/foo/bar", source is
	 * "/foo/bar/src/test/file", and destDir is "/target" the returned file will
	 * be "/target/src/test/file"
	 * 
	 * @param base the base directory where source resides in
	 * @param source the source file to use to create the result. This file is
	 *            relative it will be assumed to be relative to the given base
	 *            directory.
	 * @param destDirectory the destination directory where the source file
	 * @return
	 */
	public static File relocateFile(File base, File source, File destDir)
	{
		if (source.toString().startsWith(base.toString()))
		{
			return new File(destDir, source.toString().substring(base.toString().length()));
		}
		else if (!source.isAbsolute())
		{
			return new File(destDir, source.toString());
		}
		else
		{
			throw new IllegalArgumentException("Source file is not contained with the base directory");
		}
	}

	/**
	 * @param path A valid directory path
	 * @return Whether the entire path could be created (also true if it already
	 *         exists)
	 */
	public static boolean createFullPath(String path)
	{
		try
		{
			File f = new File(path);

			return f.exists() || f.mkdirs();
		}
		catch (SecurityException e)
		{
			// We don't really care about the reason; the important thing is the
			// path could not be created
			return false;
		}
	}

	public static String getFilenamePart(String pathToFile)
	{
		return new File(pathToFile).getName();
	}

	public static String getDirectoryPart(String pathToFile)
	{
		int pathEnd = pathToFile.lastIndexOf('/');
		if (pathEnd > 0)
		{
			return pathToFile.substring(0, pathEnd);
		}
		else
		{
			return pathToFile;
		}
	}

	/**
	 * Get a file stream for the SAX parser without the Root element could not
	 * be found exception. This is caused by a BOM character which is skipped by
	 * this file reader.
	 * 
	 * @noinspection ResultOfMethodCallIgnored
	 */
	public static FileInputStream getCleanInputStream(File xmlFile) throws IOException
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
	private static int getBOMCount(File xmlFile) throws IOException
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
		try
		{
			if (reader != null)
			{
				reader.close();
			}
		}
		catch (IOException e)
		{
			// this should not happen
			throw new RuntimeException("Unable to close reader: " + e.getMessage());
		}
	}

	/**
	 * Closes the specified Writer instance, provided it is not null.
	 */
	public static void close(Writer writer)
	{
		try
		{
			if (writer != null)
			{
				writer.close();
			}
		}
		catch (IOException e)
		{
			// this should not happen
			throw new RuntimeException("Unable to close writer: " + e.getMessage());
		}
	}

	/**
	 * Closes the specified InputStream instance, provided it is not null.
	 */
	public static void close(InputStream is)
	{
		try
		{
			if (is != null)
			{
				is.close();
			}
		}
		catch (IOException e)
		{
			// this should not happen
			throw new RuntimeException("Unable to close stream: " + e.getMessage());
		}
	}

	/**
	 * Closes the specified OutputStream instance, provided it is not null.
	 */
	public static void close(OutputStream os)
	{
		try
		{
			if (os != null)
			{
				os.close();
			}
		}
		catch (IOException e)
		{
			// this should not happen
			throw new RuntimeException("Unable to close stream: " + e.getMessage());
		}
	}
}
