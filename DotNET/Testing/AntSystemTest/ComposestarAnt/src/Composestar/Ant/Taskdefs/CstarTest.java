package Composestar.Ant.Taskdefs;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.taskdefs.ExecuteWatchdog;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.FileUtils;

/**
 * Runs multiple tests
 * 
 * @author Michiel Hendriks
 */
public class CstarTest extends BaseTask
{
	protected static final String CORRECT_OUTPUT = "correct.txt";

	/**
	 * If true fail the build if a single test failed
	 */
	protected boolean failOnError = true;

	/**
	 * If true fail on the first test that failed
	 */
	protected boolean failOnFirstError = false;

	/**
	 * 5 minutes by default.
	 */
	protected long timeout = 300000; 
	
	/**
	 * Total tests executed
	 */
	protected int cntTotal;

	/**
	 * Number of succesful tests
	 */
	protected int cntSuccess;
	
	/**
	 * Number of timed-out tests.
	 */
	protected int cntTimeout;

	/**
	 * Number of failed tests
	 */
	protected int cntFail;

	/**
	 * Current number of projects examined
	 */
	protected int cntCurrent;

	/**
	 * List of failed tests. Included with final exception.
	 */
	protected List failList = new ArrayList();

	public CstarTest()
	{
		super();
	}
	
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
		this.timeout = timeout;
	}

	public void addFileset(FileSet set)
	{
		super.addFileset(set);
	}
	
	public void execute() throws BuildException
	{
		List tests = collectInputs();

		cntTotal = tests.size();
		cntSuccess = 0;
		cntTimeout = 0;
		cntFail = 0;
		cntCurrent = 0;
		failList.clear();
		
		runTests(tests);
		reportResults();
		
		if (failOnError && (cntFail > 0))
		{
			throw new BuildException("" + cntFail + " test(s) failed.");
		}
	}
	
	private void reportResults()
	{
		log("" +
			"total: " + cntTotal + 
			"; success: " + cntSuccess +
			"; timeouts: " + cntTimeout +
			"; failed: " + cntFail +
			"; ratio: " + (cntSuccess * 100 / cntTotal) + "%", Project.MSG_INFO);
		
		if (cntFail > 0)
		{
			log("The following tests failed:", Project.MSG_INFO);		
			Iterator it = failList.iterator();
			while (it.hasNext())
			{
				String failed = (String)it.next();
				log(failed, Project.MSG_INFO);
			}
		}
	}

	private void runTests(List tests)
	{
		log("Testing " + tests.size() + " Compose* programs", Project.MSG_INFO);

		Iterator it = tests.iterator();
		while (it.hasNext())
		{
			File exec = (File)it.next();
			runTest(exec);
		}
	}
	
	protected void runTest(File exec) throws BuildException
	{
		getProject().log(this, "" + (cntCurrent * 100 / cntTotal) + "% - " + exec, Project.MSG_INFO);
		cntCurrent++;

		log(exec.getAbsolutePath(), Project.MSG_VERBOSE);

		// exec command on system runtime
		try
		{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
			ExecuteStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);
			ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout);
			Execute execute = new Execute(streamHandler, watchdog);
			execute.setAntRun(getProject());
			execute.setSpawn(false);
			execute.setWorkingDirectory(exec.getParentFile());
			
			String[] cmd = { exec.getAbsolutePath() };
			execute.setCommandline(cmd);

			int err = execute.execute();

			if (execute.killedProcess())
			{
				cntTimeout++;
				throw new Exception("Process killed; Time-out reached.");
			}

			if (err != 0)
			{
				throw new Exception("Exit code is not zero");
			}
			
			checkOutput(exec, outputStream.toString());

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
				getProject().log(this, "! Failed ! " + e.getMessage(), Project.MSG_ERR);
				failList.add("" + exec);
			}
		}
	}

	private void checkOutput(File exec, String output) throws Exception
	{
		File correct = new File(exec.getParentFile(), CORRECT_OUTPUT);
		
		BufferedReader expectedReader = null;
		BufferedReader actualReader = null;
		
		try {
			expectedReader = new BufferedReader(new FileReader(correct));
			actualReader = new BufferedReader(new StringReader(output));

			while (true)
			{
				String expected = expectedReader.readLine();
				String actual = actualReader.readLine();
				
				if (! compareLines(expected, actual))
				{
					throw new Exception(
							"Invalid output: expected " + quote(expected) 
							+ ", but encountered " + quote(actual));
				}
				
				if (expected == null || actual == null)
					break;
			}
		}
		finally {
			FileUtils.close(expectedReader);
			FileUtils.close(actualReader);
		}
	}
	
	private boolean compareLines(String e, String a)
	{
		// if one is null then both must be
		if (e == null || a == null)
			return e == a;
		
		// else just check for equality
		return e.equals(a);
	}
	
	private String quote(String line)
	{
		return (line == null ? "<EOF>" : "'" + line + "'");
	}
}
