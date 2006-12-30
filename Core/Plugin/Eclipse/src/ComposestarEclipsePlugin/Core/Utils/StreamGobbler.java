package ComposestarEclipsePlugin.Core.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Internal class for CmdExec. It handles input from a running program. These
 * are implemented as threads because the the buffers used to hold the output
 * data could otherwise overrun which blocks the program.
 */
public class StreamGobbler extends Thread
{
	private InputStream Is;

	private String Result = "";

	/**
	 * ctor
	 * 
	 * @param is The input stream to monitor.
	 * @roseuid 404DCCF400D4
	 */
	public StreamGobbler(InputStream is)
	{
		Is = is;
	}

	/**
	 * Fetch the data retrieved from the stream.
	 * 
	 * @return java.lang.String
	 * @roseuid 404DCCF40151
	 */
	public String result()
	{
		return Result;
	}

	/**
	 * Start thread and fetch data from the stream. The thread stops when the
	 * stream is closed.
	 * 
	 * @roseuid 404DCCF4017F
	 */
	public void run()
	{
		try
		{
			InputStreamReader isr = new InputStreamReader(Is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null)
			{
				System.out.println(line);
				Result += line + "\n";
			}
		}
		catch (IOException ioe)
		{
			System.out.println("StreamGobbler::run - This should be impossible, sorry!");
			ioe.printStackTrace();
		}
		done();
	}

	/**
	 * Block until all data is retrieved from the stream.
	 * 
	 * @return boolean
	 * @roseuid 404DCCF401DD
	 */
	public synchronized boolean waitForResult()
	{
		return true;
	}

	/**
	 * All data is retrieved from the stream. Notifies all threads waiting for
	 * it.
	 * 
	 * @roseuid 404DCCF401FC
	 */
	private synchronized void done()
	{
		notifyAll();
	}
}
