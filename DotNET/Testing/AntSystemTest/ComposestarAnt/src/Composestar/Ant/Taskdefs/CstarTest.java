package Composestar.Ant.Taskdefs;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.taskdefs.ExecuteWatchdog;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;
import org.apache.tools.ant.types.FileSet;

/**
 * Runs a test
 * 
 * @author Michiel Hendriks
 */
public class CstarTest extends Task
{

	protected String CORRECT_OUTPUT = "correct.txt";

	protected long TEST_TIMEOUT = 300000; // 5 minutes

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

	/**
	 * List of failed tests. Incleased with final exception.
	 */
	protected String failList;

	public void setFailOnError(boolean failOnError)
	{
		this.failOnError = failOnError;
	}

	public void setFailOnFirstError(boolean failOnFirstError)
	{
		this.failOnFirstError = failOnFirstError;
	}

	public void setTimeout(long timeout)
	{
		this.TEST_TIMEOUT = timeout;
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
		failList = "";

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
			throw new BuildException("" + cntFail + " test(s) failed: " + failList);
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
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
			ExecuteStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);
			ExecuteWatchdog watchdog = new ExecuteWatchdog(TEST_TIMEOUT);
			Execute execute = new Execute(streamHandler, watchdog);
			execute.setAntRun(getProject());
			execute.setSpawn(false);
			execute.setWorkingDirectory(execPath.getParentFile());
			String[] cmd = { exec };
			execute.setCommandline(cmd);

			int err = execute.execute();

			if (execute.killedProcess())
			{
				throw new Exception("Process killed; Time-out reached.");
			}

			if (err != 0)
			{
				throw new Exception("Exit code is not zero");
			}

			BufferedReader correctFile = new BufferedReader(new FileReader(execPath.getParent() + File.separator
					+ CORRECT_OUTPUT));
			BufferedReader actualOutput = new BufferedReader(new StringReader(outputStream.toString()));
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
				if (failList.length() > 0) failList += "; ";
				failList += exec;
			}
		}
	}
}
