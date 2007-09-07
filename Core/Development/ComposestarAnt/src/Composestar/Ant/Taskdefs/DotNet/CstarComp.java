package Composestar.Ant.Taskdefs.DotNet;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

import Composestar.Ant.TestOutput;
import Composestar.Ant.Taskdefs.BaseTask;
import Composestar.Ant.Taskdefs.SupJava;

/**
 * Compiles Compose* projects.
 * 
 * @author Michiel Hendriks
 */
public class CstarComp extends BaseTask
{
	/**
	 * If true fail the build if a single project failed to compile
	 */
	protected boolean failOnError = true;

	/**
	 * If true fail on the first build that failed to compile
	 */
	protected boolean failOnFirstError = false;
	
	/**
	 * If true send error output to the ant output instead of build.txt
	 */
	protected boolean logError = true;

	/**
	 * The location of ComposeStar; assumes the jar files are in
	 * composestarBase+"/lib"
	 */
	protected String composestarBase;

	/**
	 * The master to execute
	 */
	protected String mainJar = "ComposestarDotNET.jar";

	/**
	 * Total projects examined
	 */
	protected int cntTotal;

	/**
	 * Current number of projects examined
	 */
	protected int cntCurrent;

	/**
	 * Number of succesful builds
	 */
	protected int cntSuccess;

	/**
	 * Number of failed builds
	 */
	protected int cntFail;
	
	/**
	 * File to save the results to
	 */
	protected String resultOutput;
	
	protected TestOutput testOutput;
	
	public CstarComp()
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
	
	public void setLogError(boolean logError)
	{
		this.logError = logError;
	}

	public void setMainJar(String mainJar)
	{
		this.mainJar = mainJar;
	}

	public void setComposestarBase(String composestarBase)
	{
		this.composestarBase = composestarBase;
	}
	
	public void setResultOutput(String in)
	{
		resultOutput = in;
	}

	public void addFileset(FileSet set)
	{
		super.addFileset(set);
	}

	public void execute() throws BuildException
	{
		testOutput = new TestOutput("Composestar.Testing.DotNET.Compilation");
		
		List projects = collectInputs();
		cntTotal = projects.size();
		cntSuccess = 0;
		cntFail = 0;
		cntCurrent = 0;
		failList.clear();

		log("Compiling " + cntTotal + " Compose* projects", Project.MSG_INFO);

		Iterator it = projects.iterator();
		while (it.hasNext())
		{
			File buildFile = (File)it.next();
			compileProject(buildFile);
		}

		reportResults();
		
		testOutput.save(resultOutput);

		if (failOnError && (cntFail > 0))
		{
			throw new BuildException("Compilation of " + cntFail + " project(s) failed.");
		}
	}
	
	private void reportResults()
	{
		log("" +
				"total: " + cntTotal + 
				"; success: " + cntSuccess +
				"; failed: " + cntFail +
				"; ratio: " + (cntSuccess * 100 / cntTotal) + "%", 
				(cntFail == 0 ? Project.MSG_INFO : Project.MSG_WARN));
			
		if (cntFail > 0)
		{
			log("Compilation of the following projects failed:", Project.MSG_ERR);		
			reportFailures();
		}
	}

	protected void compileProject(File buildXML) throws BuildException
	{
		File projectDir = buildXML.getParentFile();
		testOutput.beginTest(projectDir.toString());
		log("" + (cntCurrent * 100 / cntTotal) + "% - " + projectDir, Project.MSG_INFO);
		cntCurrent++;

		try
		{
			if (!buildXML.exists()) throw new Exception(buildXML.getName() + " does not exist.");
			
			File oldLog = new File(projectDir, "buildlog.txt");
			if (oldLog.exists())
			{
				oldLog.renameTo(new File(projectDir, "buildlog.old.txt"));
			}

			SupJava java = new SupJava(this);
			
			java.init();
			java.setDir(projectDir);

			// set jar file to execute
			File binaries = new File(composestarBase, "lib");
			java.setJar(new File(binaries, mainJar));
			
			// add arguments
			java.addArg("-d4"); // set compile debug output to DEBUG
			java.addArg("-t2"); // set error threshold to WARNING
			java.addArg(buildXML.toString()); // set build file
			
			java.setLogError(logError);
			
			java.setFork(true);
			java.setOutput(new File(projectDir, "buildlog.txt"));

			int err = java.executeJava();
			if (err != 0)
			{
				throw new Exception("Exit code is not zero: " + err);
			}

			cntSuccess++;
		}
		catch (Exception e)
		{
			cntFail++;
			testOutput.endTest(e.getMessage());
			if (failOnFirstError)
			{
				throw new BuildException("Compilation of project in " + projectDir + " failed; " + e.getMessage());
			}
			else
			{
				addFailure(projectDir.getAbsolutePath(), e.getMessage());
			}
		}
		testOutput.endTest();
	}
}
