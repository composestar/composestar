package Composestar.Ant.Taskdefs;

import java.util.Vector;
import java.util.Iterator;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import java.io.*;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;

/**
 * Runs a test
 * 
 * @author Michiel Hendriks
 */
public class CstarTest extends Task
{

	protected String CORRECT_OUTPUT = "correct.txt";

	protected Vector fileSets = new Vector();

	/**
	 * If true fail the build if a single test failed
	 */
	protected boolean failOnError = true;

	/**
	 * If true fail on the first test that failed
	 */
	protected boolean failOnFirstError = false;

	/**
	 * Total tests executed 
	 */
	protected int cntTotal;

	/**
	 * Number of succesful tests
	 */
	protected int cntSuccess;

	/**
	 * Number of failed tests
	 */
	protected int cntFail;

	public void setFailOnError(boolean failOnError)
	{
		this.failOnError = failOnError;
	}

	public void setFailOnFirstError(boolean failOnFirstError)
	{
		this.failOnFirstError = failOnFirstError;
	}

	public void addFileset(FileSet set)
	{
		fileSets.add(set);
	}

	public void execute() throws BuildException
	{
		cntTotal = 0;
		cntSuccess = 0;
		cntFail = 0;

		for (Iterator it = fileSets.iterator(); it.hasNext(); /* nop */)
		{
			FileSet fileSet = (FileSet) it.next();
			DirectoryScanner ds = fileSet.getDirectoryScanner(this.getProject());
			String[] files = ds.getIncludedFiles();
			for (int i = 0; i < files.length; i++)
			{
				runTest(ds.getBasedir().getPath() + File.separator + files[i]);
			}
		}

		getProject().log(
				this,
				"" + cntTotal + " test(s); success: " + cntSuccess + "; failed: " + cntFail + "; ratio: "
						+ (cntSuccess * 100 / cntTotal) + "%", Project.MSG_INFO);
		if (failOnError && (cntFail > 0))
		{
			throw new BuildException("" + cntFail + " test(s) failed.");
		}
	}

	protected void runTest(String exec) throws BuildException
	{
		cntTotal++;
		getProject().log(this, "Testing: " + exec, Project.MSG_INFO);

		File execPath = new File(exec);
		log(exec, Project.MSG_VERBOSE);

		// exec command on system runtime
		try
		{
			Process proc = Runtime.getRuntime().exec(exec, null, execPath.getParentFile());

			StreamPumper inputPumper = new StreamPumper(proc.getInputStream());
			StreamPumper errorPumper = new StreamPumper(proc.getErrorStream());

			inputPumper.start();
			errorPumper.start();

			proc.waitFor();
			inputPumper.join();
			errorPumper.join();
			proc.destroy();

			int err = proc.exitValue();
			if (err != 0)
			{
				throw new Exception("Exit code is not zero");
			}

			BufferedReader correctFile = new BufferedReader(new FileReader(execPath.getParent() + File.separator
					+ CORRECT_OUTPUT));
			BufferedReader actualOutput = new BufferedReader(new StringReader(inputPumper.stdOut.toString()));
			String cline = correctFile.readLine();
			String aline = actualOutput.readLine();

			while ((cline != null) && (aline != null))
			{
				if (!cline.equals(aline)) throw new Exception("Output data invalid: '" + cline + "' vs '" + aline + "'");
				cline = correctFile.readLine();
				aline = actualOutput.readLine();
			}
			if ((cline != null) || (aline != null)) throw new Exception("Output data invalid " + cline + " " + aline);

			cntSuccess++;
		}
		catch (Exception e)
		{
			cntFail++;
			if (failOnFirstError)
			{
				throw new BuildException("Testing of " + exec + " failed; " + e.getMessage());
			}
			else
			{
				getProject().log(this, "Testing of " + exec + " failed; " + e.getMessage(), Project.MSG_ERR);
			}
		}
	}

	class StreamPumper extends Thread
	{
		private BufferedReader din;

		private boolean endOfStream = false;

		private static final int SLEEP_TIME = 5;

		public StringBuffer stdOut;

		public StreamPumper(InputStream is)
		{
			stdOut = new StringBuffer();
			this.din = new BufferedReader(new InputStreamReader(is));
		}

		public void pumpStream() throws IOException
		{
			if (!endOfStream)
			{
				String line = din.readLine();

				if (line != null)
				{
					stdOut.append(line + "\n");
				}
				else
				{
					endOfStream = true;
				}
			}
		}

		public void run()
		{
			try
			{
				try
				{
					while (!endOfStream)
					{
						pumpStream();
						sleep(SLEEP_TIME);
					}
				}
				catch (InterruptedException ie)
				{
					// ignore
				}
				din.close();
			}
			catch (IOException ioe)
			{
				// ignore
			}
		}
	}
}
