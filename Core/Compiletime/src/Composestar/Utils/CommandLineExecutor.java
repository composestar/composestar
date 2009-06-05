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
import java.io.IOException;
import java.util.List;

/**
 * CommandLineExecutor is a tool for running command line programs. It's
 * intention is to handle command line calls for all operating systems. Things
 * that do not work: - programs that require user input. - redirection in
 * windows. Usage: List cmd = new ArrayList(); cmd.add("csc");
 * cmd.add("foo.cs"); CommandLineExecutor cle = new CommandLineExecutor();
 * cle.exec(cmd); System.out.println(e.outputNormal());
 */
public class CommandLineExecutor
{
	private StreamGobbler errorGobbler;

	private StreamGobbler outputGobbler;

	private File workingDir;

	public void setWorkingDir(File wd)
	{
		workingDir = wd;
	}

	public int exec(List<String> cmdList) throws IOException, InterruptedException
	{
		String[] cmdArray = new String[cmdList.size()];
		cmdList.toArray(cmdArray);
		return exec(cmdArray);
	}

	public int exec(String[] command) throws IOException, InterruptedException
	{
		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec(command, null, workingDir);

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

	/**
	 * Returns the program output to STDOUT or null if no program was executed.
	 */
	public String outputNormal()
	{
		if (outputGobbler == null)
		{
			return null;
		}
		else
		{
			return outputGobbler.result();
		}
	}

	/**
	 * Returns the program output to STDERR or null if no program was executed.
	 */
	public String outputError()
	{
		if (errorGobbler == null)
		{
			return null;
		}
		else
		{
			return errorGobbler.result();
		}
	}
}
