package Composestar.Ant.Taskdefs;

import java.util.Vector;
import java.util.Iterator;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;

/**
 * Compiles a devenv solution with Compose*
 * 
 * @author Michiel Hendriks
 * @deprecated
 */
public class CstarDevenv extends Task
{
	/**
	 * The VisualStudio command to execute
	 */
	protected static final String COMPOSESTAR_BUILD = "Macros.Composestar.SystemTest.BuildWithComposeStarAndExit";

	/**
	 * The buildlog filename
	 */
	protected static final String BUILDLOG_NAME = "buildlog.txt";

	/**
	 * The message added to the log when the build failed;
	 */
	protected static final String BUILD_FAILED_MSG = "Composestar build failed";

	/**
	 * The devenv.com executable, required for building
	 */
	protected String devenv = "";

	protected Vector fileSets = new Vector();

	protected boolean failOnError = true;

	protected boolean failOnFirstError = false;

	// internals
	protected int cntTotal;

	protected int cntSuccess;

	protected int cntFail;

	public CstarDevenv()
	{
		locateDevenv();
	}

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
		if (devenv == "") throw new BuildException("Unable to locate devenv.exe");
		getProject().log(this, "Found devenv at " + devenv, Project.MSG_DEBUG);

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
				compileSolution(ds.getBasedir().getPath() + File.separator + files[i]);
			}
		}

		getProject().log(
				this,
				"Compiled " + cntTotal + " project(s); success: " + cntSuccess + "; failed: " + cntFail + "; ratio: "
						+ (cntSuccess * 100 / cntTotal) + "%", Project.MSG_INFO);
		if (failOnError && (cntFail > 0))
		{
			throw new BuildException("Compilation of " + cntFail + " solution(s) failed.");
		}
	}

	protected void locateDevenv()
	{
		DirectoryScanner ds = new DirectoryScanner();
		String[] inc = { "**/devenv.exe" };
		ds.setIncludes(inc);
		ds.setBasedir("C:/Program Files/Microsoft Visual Studio .NET 2003/Common7/IDE");
		ds.setCaseSensitive(false);
		ds.scan();
		String[] files = ds.getIncludedFiles();
		if (files.length > 0)
		{
			devenv = ds.getBasedir().getPath() + File.separator + files[0];
		}
	}

	protected void compileSolution(String solution) throws BuildException
	{
		cntTotal++;
		getProject().log(this, "Building Compose* solution: " + solution, Project.MSG_INFO);

		String command = "\"" + devenv + "\" \"" + solution + "\" /command \"" + COMPOSESTAR_BUILD + " "
				+ BUILDLOG_NAME + "\"";
		log(command, Project.MSG_VERBOSE);

		// exec command on system runtime
		try
		{
			File solutionPath = new File(solution);
			File buildLog = new File(solutionPath.getParent() + File.separator + BUILDLOG_NAME);
			if (buildLog.exists()) buildLog.delete();

			Process proc = Runtime.getRuntime().exec(command);

			proc.waitFor();
			proc.destroy();

			int err = proc.exitValue();
			if (err != 0)
			{
				throw new Exception("Exit code is not zero");
			}

			if (!buildLog.exists()) throw new Exception("No buildlog");

			BufferedReader logReader = new BufferedReader(new FileReader(buildLog));
			String line = logReader.readLine();
			while (line != null)
			{
				if (line.startsWith(BUILD_FAILED_MSG)) throw new Exception("Build failed");
				line = logReader.readLine();
			}

			// Composestar build failed

			cntSuccess++;
		}
		catch (Exception e)
		{
			cntFail++;
			if (failOnFirstError)
			{
				throw new BuildException("Compilation of solution " + solution + " failed; " + e.getMessage());
			}
			else
			{
				getProject().log(this, "Compilation of solution " + solution + " failed; " + e.getMessage(),
						Project.MSG_ERR);
			}
		}
	}
}
