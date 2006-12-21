package Composestar.Ant.Taskdefs.Java;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
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
import org.apache.tools.ant.types.DirSet;

import Composestar.Ant.Taskdefs.BaseTask;

/**
 * Runs multiple tests
 */
public class CstarTest extends BaseTask
{

	protected static final String CORRECT_OUTPUT = "correct.txt";

	/**
	 * Eclipse launcher
	 */
	protected String launcher = "org.eclipse.core.launcher.Main";

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
	 * The location of eclipse workspace; location of the examples.
	 */
	protected String workspace;

	/**
	 * The location of eclipse (%ECLIPSE_HOME%).
	 */
	protected String eclipseHome;

	/**
	 * Application-id as defined in plugin.xml
	 */
	protected String application;

	private final List m_dirSets;

	public CstarTest()
	{
		super();
		m_dirSets = new ArrayList();
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

	public void setWorkspace(String workspace)
	{
		this.workspace = workspace;
	}

	public void setEclipseHome(String eclipseHome)
	{
		this.eclipseHome = eclipseHome;
	}

	public void setApplication(String application)
	{
		this.application = application;
	}

	public void addDirset(DirSet ds)
	{
		m_dirSets.add(ds);
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
		log("" + "total: " + cntTotal + "; success: " + cntSuccess + "; timeouts: " + cntTimeout + "; failed: "
				+ cntFail + "; ratio: " + (cntSuccess * 100 / cntTotal) + "%", (cntFail == 0) ? Project.MSG_INFO
				: Project.MSG_WARN);

		if (cntFail > 0)
		{
			log("The following tests failed:", Project.MSG_ERR);
			reportFailures();
		}
	}

	private void runTests(List tests)
	{
		if (tests.size() == 0) throw new BuildException("No tests to run");

		log("Testing " + tests.size() + " Compose* programs", Project.MSG_INFO);

		Iterator it = tests.iterator();
		while (it.hasNext())
		{
			String prj = (String) it.next();
			runTest(prj);
		}
	}

	protected void runTest(String projectname) throws BuildException
	{
		log("" + (cntCurrent * 100 / cntTotal) + "% - " + projectname, Project.MSG_INFO);
		cntCurrent++;

		log(projectname, Project.MSG_VERBOSE);

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
			execute.setWorkingDirectory(new File(eclipseHome));

			String[] cmd = { "java", "-cp", eclipseHome + File.separator + "startup.jar", launcher, "-application",
					"ComposestarEclipsePlugin.testrun", projectname, "-data", workspace, "-clean" };
			execute.setCommandline(cmd);

			int err = execute.execute();

			if (execute.killedProcess())
			{
				cntTimeout++;
				throw new Exception("Process killed; Time-out reached.");
			}

			if (err != 0)
			{
				throw new Exception("Exit code is not zero: " + err);
			}

			checkOutput(projectname, outputStream.toString());

			cntSuccess++;
		}
		catch (Exception e)
		{
			cntFail++;
			if (failOnFirstError)
			{
				throw new BuildException("Testing of " + projectname + " failed; " + e.getMessage());
			}
			else
			{
				addFailure(projectname, e.getMessage());
			}
		}
	}

	private void checkOutput(String projectname, String output) throws Exception
	{
		String projectBase = workspace + java.io.File.separator + projectname;
		File correct = new File(projectBase, CORRECT_OUTPUT);

		BufferedReader expectedReader = null;
		BufferedReader actualReader = null;

		try
		{
			expectedReader = new BufferedReader(new FileReader(correct));
			actualReader = new BufferedReader(new StringReader(output));

			while (true)
			{
				String expected = expectedReader.readLine();
				String actual = actualReader.readLine();

				if (!compareLines(expected, actual))
				{
					throw new Exception("Invalid output: expected " + quote(expected) + ", but encountered "
							+ quote(actual));
				}

				if (expected == null || actual == null) break;
			}
		}
		finally
		{
			close(expectedReader);
			close(actualReader);
		}
	}

	private boolean compareLines(String e, String a)
	{
		// if one is null then both must be
		if (e == null || a == null) return e == a;

		// else just check for equality
		return e.equals(a);
	}

	private String quote(String line)
	{
		return (line == null ? "<EOF>" : "'" + line + "'");
	}

	private void close(Reader r)
	{
		try
		{
			if (r != null) r.close();
		}
		catch (IOException e)
		{
		} // ignore
	}

	protected List collectInputs() throws BuildException
	{
		List result = new ArrayList();
		Iterator it = m_dirSets.iterator();
		while (it.hasNext())
		{
			try
			{
				DirSet ds = (DirSet) it.next();
				String[] dirs = ds.getDirectoryScanner(getProject()).getIncludedDirectories();
				for (int i = 0; i < dirs.length; i++)
				{
					result.add(dirs[i]);
				}
			}
			catch (Exception e)
			{
				throw new BuildException("Error while collecting inputs: " + e.getMessage());
			}
		}
		return result;
	}
}
