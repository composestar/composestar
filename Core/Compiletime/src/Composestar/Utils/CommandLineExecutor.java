/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Utils;

import java.io.File;

/**
 * CommandLineExecutor is a tool for running command line programs. 
 * It's intention is to handle command line calls for all operating systems.
 * Things that do not work:
 * - programs that require user input.
 * - redirection in windows.
 * 
 * Usage:
 *   CommandLineExecutor e = new CommandLineExecutor();
 *   e.exec("format c: /q");
 *   System.out.println(e.outputNormal());
 */
public class CommandLineExecutor
{
	private StreamGobbler ErrorGobbler;
	private StreamGobbler OutputGobbler;

	/**
	 * Execute command.
	 * exec executes the command and waits for it to return. WARNING: If the program 
	 * hangs this function will never return.
	 * Please note that return values indicating error differ between programs and 
	 * operating systems.
	 * 
	 * @param command The command to execute.
	 * @return The exit code of the program to run.
	 * @roseuid 404DCCF50112
	 */
	public int exec(String command)
	{
		return exec(command, null);
	}

	public int exec(String command, File dir)
	{
		try {
			String osName = System.getProperty("os.name");

			// "some" OSs need special treatment to be able to use built in functions
			if (osName.equals( "Windows NT")
					|| osName.equals("Windows 2000")
					|| osName.equals("Windows CE")
					|| osName.equals("Windows XP")) {
				command = "cmd.exe /C " + command;
			}
			else if (osName.equals("Windows 95")
					|| osName.equals("Windows 98")
					|| osName.equals("Windows ME")) {
				command = "command.exe /C " + command;
			}
			// real operating systems handle this flawlessly

			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(command, null, dir);

			// connect error and output filters
			// these are threads because the buffers used to hold the output data
			// could otherwise overrun which blocks the program.
			ErrorGobbler = new StreamGobbler(proc.getErrorStream());
			OutputGobbler = new StreamGobbler(proc.getInputStream());
			ErrorGobbler.start();
			OutputGobbler.start();

			// wait for program return.
			int result = proc.waitFor();

			// wait for the output threads
			OutputGobbler.waitForResult();
			ErrorGobbler.waitForResult();
			
			return result;
		}
		catch (Throwable t) {
			// TODO: New throw specific to project
			t.printStackTrace();
		}
		return -1;
	}


	/**
	 * Get the program output to STDOUT.
	 * @return java.lang.String
	 * @roseuid 404DCCF500D4
	 */
	public String outputNormal()
	{
		return OutputGobbler.result();
	}

	/**
	 * Get the program output to STDERR.
	 * @return java.lang.String
	 * @roseuid 404DCCF500F3
	 */
	public String outputError()
	{
		return ErrorGobbler.result();
	}

	/**
	 * For testing purposes
	 */
	public static void main(String args[])
	{
		CommandLineExecutor e = new CommandLineExecutor();
		e.exec(args[0]);
		System.out.println(e.outputNormal());     
	}
}
