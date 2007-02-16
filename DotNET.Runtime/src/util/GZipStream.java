/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id$
 * 
 */
package Composestar.RuntimeDotNET.Utils;

import System.IO.Stream;

import java.io.*;
import java.util.zip.GZIPInputStream;

/**
 * Wrapper class to provide GZip support to .NET
 */
public class GZipStream extends Stream
{
	protected InputStream stream;
	protected byte[] buf;

	public GZipStream(String file) throws FileNotFoundException, IOException
	{
		this(new FileInputStream(file));
	}

	public GZipStream(InputStream file) throws IOException
	{
		super();
		stream = new GZIPInputStream(file);		
	}

	public void Flush()
	{
		//read only stream
	}

	public int Read(ubyte[] buffer, int offset, int count) throws System.IO.IOException
	{
		int cnt;
		try
		{
			if (buf == null)
			{
				buf = new byte[buffer.length];
			}
			cnt = stream.read(buf, offset, count);
			if (cnt < 0) cnt = 0;
			System.Buffer.BlockCopy(buf, offset, buffer, offset, cnt);
		}
		catch (java.io.IOException e)
		{
			throw new System.IO.IOException(e.getMessage());
		}
		return cnt;
	}

	public long Seek(long offset, System.IO.SeekOrigin origin)
	{
		throw new System.NotSupportedException();
	}

	public void SetLength(long value)
	{
		throw new System.NotSupportedException();
	}

	public void Write(ubyte[] buffer, int offset, int count)
	{
		throw new System.NotSupportedException();
	}

	public boolean get_CanRead()
	{
		return true;
	}

	public boolean get_CanSeek()
	{
		return false;
	}

	public boolean get_CanWrite()
	{
		return false;
	}

	public long get_Length()
	{
		throw new System.NotSupportedException();
	}

	public long get_Position()
	{
		throw new System.NotSupportedException();
	}

	public void set_Position(long pos)
	{
		throw new System.NotSupportedException();
	}
}
