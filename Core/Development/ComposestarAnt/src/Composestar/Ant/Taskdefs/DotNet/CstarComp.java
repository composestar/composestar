package Composestar.Ant.Taskdefs.DotNet;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Commandline.Argument;
import org.apache.tools.ant.types.PatternSet.NameEntry;

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
	 * composestarBase+"/Binaries"
	 */
	protected String composestarBase;

	/**
	 * The master to execute
	 */
	protected String master = "Composestar.DotNET.MASTER.DotNETMaster";

	/**
	 * Classpath for Compose*; required to build the project
	 */
	protected FileSet cstarJars;

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

	public void setMaster(String master)
	{
		this.master = master;
	}

	public void setComposestarBase(String composestarBase)
	{
		this.composestarBase = composestarBase;
	}

	public void addFileset(FileSet set)
	{
		super.addFileset(set);
	}

	public void execute() throws BuildException
	{
		cstarJars = new FileSet();
		cstarJars.setDir(new File(composestarBase, "Binaries"));
		NameEntry inc = cstarJars.createInclude();
		inc.setName("*.jar");

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
				(cntFail == 0)?Project.MSG_INFO:Project.MSG_WARN );
			
		if (cntFail > 0)
		{
			log("Compilation of the following projects failed:", Project.MSG_ERR);		
			reportFailures();
		}
	}

	protected void compileProject(File buildXML) throws BuildException
	{
		File projectDir = buildXML.getParentFile();
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

			Java java = new SupJava(this);
			
			java.init();
			java.setDir(projectDir);
			java.setClassname(master);
			
			Argument arg = java.createArg();
			arg.setValue("-d4"); // set compile debug output to DEBUG
			
			arg = java.createArg();
			arg.setValue("-t2"); // set error threshold to WARNING
			
			arg = java.createArg();
			arg.setValue(buildXML.toString());
			
			Path cpath = java.createClasspath();
			cpath.addFileset(cstarJars);
			
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
			if (failOnFirstError)
			{
				throw new BuildException("Compilation of project in " + projectDir + " failed; " + e.getMessage());
			}
			else
			{
				addFailure(projectDir.getAbsolutePath(), e.getMessage());
			}
		}
	}
}
