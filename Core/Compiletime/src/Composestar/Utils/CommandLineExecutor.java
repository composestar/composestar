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
import java.util.List;

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
	private StreamGobbler errorGobbler;
	private StreamGobbler outputGobbler;

	/**
	 * Executess the command and waits for it to return. 
	 * WARNING: If the program hangs this function will never return.
	 * Please note that return values indicating error differ between programs and 
	 * operating systems.
	 * @deprecated
     */
	public int exec(String command)
	{
		return exec(command, null);
	}

	/**
	 * @deprecated
	 */
	public int exec(String command, File dir)
	{
		try {

			// "some" OSs need special treatment to be able to use built in functions
			// shouldnt be needed, since we're not using 'built in functions' (aka 'call')
			String osName = System.getProperty("os.name");
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
			errorGobbler = new StreamGobbler(proc.getErrorStream());
			outputGobbler = new StreamGobbler(proc.getInputStream());
			errorGobbler.start();
			outputGobbler.start();

			// wait for program return.
			int result = proc.waitFor();

			// wait for the output threads
			outputGobbler.waitForResult();
			errorGobbler.waitForResult();
			
			return result;
		}
		catch (Throwable t) {
			// TODO: New throw specific to project
			t.printStackTrace();
			return -1;
		}
	}
	
	public int exec(List cmdList)
	{
		String[] cmdArray = new String[cmdList.size()];
		cmdList.toArray(cmdArray);
		
		return exec(cmdArray);
	}
	
	public int exec(String[] command)
	{
		try {
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(command);
			
			// connect error and output filters
			errorGobbler = new StreamGobbler(proc.getErrorStream());
			outputGobbler = new StreamGobbler(proc.getInputStream());
			errorGobbler.start();
			outputGobbler.start();

			// wait for program return.
			int result = proc.waitFor();
			
			// wait for the output threads
			outputGobbler.waitForResult();
			errorGobbler.waitForResult();

			return result;
		}
		catch (Exception e) {
			// TODO: New throw specific to project
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * Returns the program output to STDOUT.
	 */
	public String outputNormal()
	{
		return outputGobbler.result();
	}

	/**
	 * Returns the program output to STDERR.
	 */
	public String outputError()
	{
		return errorGobbler.result();
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
