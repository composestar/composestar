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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Internal class for CmdExec. It handles input from a running program. These
 * are implemented as threads because the the buffers used to hold the output
 * data could otherwise overrun which blocks the program.
 */
public class StreamGobbler extends Thread
{
	private InputStream is;

	private List<String> output;

	/**
	 * Constructor.
	 * 
	 * @param inis The input stream to monitor.
	 */
	public StreamGobbler(InputStream inis)
	{
		is = inis;
		output = new ArrayList<String>(32);
	}

	/**
	 * Fetch the data retrieved from the stream.
	 */
	public String result()
	{
		StringBuffer out = new StringBuffer();
		Iterator<String> it = output.iterator();
		while (it.hasNext())
		{
			out.append(it.next());
			out.append('\n');
		}
		return out.toString();
	}

	/**
	 * Returns a list containing the lines of the data from the stream.
	 */
	public List<String> getResultLines()
	{
		return output;
	}

	/**
	 * Start thread and fetch data from the stream. The thread stops when the
	 * stream is closed.
	 */
	@Override
	public void run()
	{
		try
		{
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null)
			{
				output.add(line);
			}
		}
		catch (IOException e)
		{
			System.out.println("StreamGobbler::run - This should be impossible, sorry!");
			e.printStackTrace();
		}
		finally
		{
			done();
		}
	}

	/**
	 * All data is retrieved from the stream. Notifies all threads waiting for
	 * it.
	 */
	private synchronized void done()
	{
		notifyAll();
	}

	/**
	 * Block until all data is retrieved from the stream.
	 */
	public synchronized boolean waitForResult()
	{
		return true;
	}
}
