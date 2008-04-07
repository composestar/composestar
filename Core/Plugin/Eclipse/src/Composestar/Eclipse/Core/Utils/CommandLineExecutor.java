package Composestar.Eclipse.Core.Utils;

import java.io.File;
import java.io.PrintStream;

import Composestar.Eclipse.Core.Debug;

/**
 * CmdExec is a tool for running command line programs. Its intention is to
 * handle command line calls for all operating systems. Things that do not work: -
 * programs that require user input. - redirection in windows. Usage: CmdExec e =
 * new CmdExec(); e.exec( "dir" ); System.out.println( e.outputNormal() );
 * e.exec( ...... )
 */
public class CommandLineExecutor
{
	private StreamGobbler ErrorGobbler;

	private StreamGobbler OutputGobbler;

	protected PrintStream fwdOut;

	protected PrintStream fwdErr;

	public CommandLineExecutor()
	{
		fwdErr = Debug.instance().getErrorStream();
		fwdOut = Debug.instance().getOutputStream();
	}

	public CommandLineExecutor(PrintStream toOut)
	{
		this();
		if (toOut != null)
		{
			fwdOut = toOut;
		}
	}

	public CommandLineExecutor(PrintStream toOut, PrintStream toErr)
	{
		this(toOut);
		if (toErr != null)
		{
			fwdErr = toErr;
		}
	}

	/**
	 * Get the program output to STDOUT.
	 * 
	 * @return java.lang.String
	 */
	public String outputNormal()
	{
		return OutputGobbler.result();
	}

	/**
	 * Get the program output to STDERR.
	 * 
	 * @return java.lang.String
	 */
	public String outputError()
	{
		return ErrorGobbler.result();
	}

	/**
	 * Execute command. exec executes the command and waits for it to return.
	 * WARNING: If the program hangs this function will never return. Please
	 * note that return values indicating error differ between programs and
	 * operating systems.
	 * 
	 * @param execString The command to execute.
	 * @return The exit code of the program to run.
	 */
	public int exec(String[] cmdLine)
	{
		return exec(cmdLine, null);
	}

	public int exec(String[] cmdLine, File dir)
	{
		try
		{
			Runtime rt = Runtime.getRuntime();

			Process proc = rt.exec(cmdLine, null, dir);

			// connect error and output filters
			// these are threads because the buffers used to hold the output
			// data
			// could otherwise overrun which blocks the program.
			ErrorGobbler = new StreamGobbler(proc.getErrorStream(), fwdErr);
			OutputGobbler = new StreamGobbler(proc.getInputStream(), fwdOut);
			ErrorGobbler.start();
			OutputGobbler.start();

			// wait for program return.
			int exitVal = proc.waitFor();

			// wait for the output threads
			OutputGobbler.waitForResult();
			ErrorGobbler.waitForResult();
			return exitVal;
		}
		catch (Throwable t)
		{
			Debug.instance().Log(t.getMessage(), Debug.MSG_ERROR);
			t.printStackTrace();
		}
		return -1;
	}
}
