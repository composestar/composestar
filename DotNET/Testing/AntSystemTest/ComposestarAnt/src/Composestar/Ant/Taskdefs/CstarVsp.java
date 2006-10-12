package Composestar.Ant.Taskdefs;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Commandline.Argument;
import org.apache.tools.ant.types.PatternSet.NameEntry;

/**
 * Compiles a devenv solution with Compose*.
 * 
 * @author Michiel Hendriks
 */
public class CstarVsp extends Task
{
	/**
	 * List containg visual studio projects
	 */
	protected List fileSets = new ArrayList();

	/**
	 * If true fail the build if a single project failed to compile
	 */
	protected boolean failOnError = true;

	/**
	 * If true fail on the first build that failed to compile
	 */
	protected boolean failOnFirstError = false;

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
	 * Number of succesful builds
	 */
	protected int cntSuccess;

	/**
	 * Number of failed builds
	 */
	protected int cntFail;
	
	/**
	 * List of failed tests. Inceased with final exception.
	 */
	protected List failList = new ArrayList();

	public void setFailOnError(boolean failOnError)
	{
		this.failOnError = failOnError;
	}

	public void setFailOnFirstError(boolean failOnFirstError)
	{
		this.failOnFirstError = failOnFirstError;
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
		fileSets.add(set);
	}

	public void execute() throws BuildException
	{
		cntTotal = 0;
		cntSuccess = 0;
		cntFail = 0;
		failList.clear();
		
		log("Compiling Compose* projects", Project.MSG_INFO);

		cstarJars = new FileSet();
		cstarJars.setDir(new File(composestarBase, "Binaries"));
		NameEntry inc = cstarJars.createInclude();
		inc.setName("*.jar");

		registerCstarAsms();

		for (Iterator it = fileSets.iterator(); it.hasNext(); /* nop */)
		{
			FileSet fileSet = (FileSet)it.next();
			DirectoryScanner ds = fileSet.getDirectoryScanner(this.getProject());
			String[] files = ds.getIncludedFiles();
			for (int i = 0; i < files.length; i++)
			{
				File projectFile = new File(ds.getBasedir(), files[i]);
				compileProject(projectFile);
			}
		}

		String msg = 
			"Compiled " + cntTotal + " project(s)" +
			"; success: " + cntSuccess + 
			"; failed: " + cntFail + 
			"; ratio: " + (cntSuccess * 100 / cntTotal) + "%";
		
		log(msg, Project.MSG_INFO);
		
		if (failOnError && (cntFail > 0))
		{
			throw new BuildException("Compilation of " + cntFail + " project(s) failed: " + failList);
		}
	}

	protected void registerCstarAsms()
	{
		FileSet cstarAsms = new FileSet();
		cstarAsms.setDir(new File(composestarBase, "Binaries"));
		NameEntry inc = cstarAsms.createInclude();
		inc.setName("*.dll");

		DirectoryScanner ds = cstarAsms.getDirectoryScanner(this.getProject());
		String[] files = ds.getIncludedFiles();
		for (int i = 0; i < files.length; i++)
		{
			File asmPath = new File(ds.getBasedir().getPath(), files[i]);
			String asm = asmPath.getName();
			asm = asm.substring(0, asm.lastIndexOf("."));
			Composestar.Ant.XsltUtils.registerAssembly(asm, asmPath.toString());
		}
	}

	protected void compileProject(File buildXML) throws BuildException
	{
		cntTotal++;
		
		File projectDir = buildXML.getParentFile();
		log("-" + projectDir, Project.MSG_INFO);

		try
		{
			if (!buildXML.exists()) throw new Exception(buildXML.getName() + " does not exist.");

			Java java = (Java) getProject().createTask("java");
			java.init();
			java.setDir(projectDir);
			java.setClassname(master);
			
			Argument arg = java.createArg();
			arg.setValue(buildXML.toString());
			
			Path cpath = java.createClasspath();
			cpath.addFileset(cstarJars);
			
			java.setFork(true);
			java.setOutput(new File(projectDir, "buildlog.txt"));

			int err = java.executeJava();
			if (err != 0)
			{
				throw new Exception("Exit code is not zero");
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
				log("Compilation of project in " + projectDir + " failed; " + e.getMessage(), Project.MSG_ERR);
				failList.add(projectDir);
			}
		}
	}
}
