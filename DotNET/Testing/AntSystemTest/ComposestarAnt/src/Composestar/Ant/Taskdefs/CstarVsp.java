package Composestar.Ant.Taskdefs;

import java.util.Vector;
import java.util.Iterator;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.taskdefs.XSLTProcess;
import org.apache.tools.ant.taskdefs.XSLTProcess.Param;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Commandline.Argument;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.PatternSet.NameEntry;
import java.io.*;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;

/**
 * Compiles a devenv solution with Compose*
 * 
 * @author Michiel Hendriks
 */
public class CstarVsp extends Task
{
	/**
	 * The name of the BuildConfiguration.XML to produce
	 */
	protected String BUILD_CONFIGURATION_XML = "BuildConfiguration.xml";

	/**
	 * List containg visual studio projects
	 */
	protected Vector fileSets = new Vector();

	/**
	 * If true fail the build if a single project failed to compile
	 */
	protected boolean failOnError = true;

	/**
	 * If true fail on the first build that failed to compile
	 */
	protected boolean failOnFirstError = false;

	/**
	 * The Xslt document to convert the VisualStudio projects to
	 * BuildConfiguration.xml
	 */
	protected String conversionXslt;

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
	 * Path to AntHelper.exe; which is used to resolve assembly locations
	 */
	protected String antHelperPath;

	/**
	 * Classpath for compose*; required to build the project
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

	public void setFailOnError(boolean failOnError)
	{
		this.failOnError = failOnError;
	}

	public void setFailOnFirstError(boolean failOnFirstError)
	{
		this.failOnFirstError = failOnFirstError;
	}

	public void setConversionXslt(String conversionXslt)
	{
		this.conversionXslt = conversionXslt;
	}

	public void setMaster(String master)
	{
		this.master = master;
	}

	public void setAntHelperPath(String antHelperPath)
	{
		this.antHelperPath = antHelperPath;
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

		if (!Composestar.Ant.XsltUtils.setAntHelperEXE(antHelperPath))
		{
			throw new BuildException("Invalid antHelperPath value: " + antHelperPath);
		}

		cstarJars = new FileSet();
		cstarJars.setDir(new File(composestarBase + File.separator + "Binaries"));
		NameEntry inc = cstarJars.createInclude();
		inc.setName("*.jar");

		registerCstarAsms();

		for (Iterator it = fileSets.iterator(); it.hasNext(); /* nop */)
		{
			FileSet fileSet = (FileSet) it.next();
			DirectoryScanner ds = fileSet.getDirectoryScanner(this.getProject());
			String[] files = ds.getIncludedFiles();
			for (int i = 0; i < files.length; i++)
			{
				compileProject(ds.getBasedir().getPath() + File.separator + files[i]);
			}
		}

		getProject().log(
				this,
				"Compiled " + cntTotal + " project(s); success: " + cntSuccess + "; failed: " + cntFail + "; ratio: "
						+ (cntSuccess * 100 / cntTotal) + "%", Project.MSG_INFO);
		if (failOnError && (cntFail > 0))
		{
			throw new BuildException("Compilation of " + cntFail + " project(s) failed.");
		}
	}

	protected void registerCstarAsms()
	{
		FileSet cstarAsms = new FileSet();
		cstarAsms.setDir(new File(composestarBase + File.separator + "Binaries"));
		NameEntry inc = cstarAsms.createInclude();
		inc.setName("*.dll");

		DirectoryScanner ds = cstarAsms.getDirectoryScanner(this.getProject());
		String[] files = ds.getIncludedFiles();
		for (int i = 0; i < files.length; i++)
		{
			File asmPath = new File(ds.getBasedir().getPath() + File.separator + files[i]);
			String asm = asmPath.getName();
			asm = asm.substring(0, asm.lastIndexOf("."));
			Composestar.Ant.XsltUtils.registerAssembly(asm, asmPath.toString());
		}
	}

	protected void compileProject(String project) throws BuildException
	{
		cntTotal++;
		getProject().log(this, "Building Compose* project: " + project, Project.MSG_INFO);

		File projectFile = new File(project);
		File buildXML = new File(projectFile.getParent() + File.separator + BUILD_CONFIGURATION_XML);

		try
		{
			if (conversionXslt != "")
			{
				Composestar.Ant.XsltUtils.setCurrentDirectory(projectFile.getParent());

				XSLTProcess xslt = (XSLTProcess) getProject().createTask("xslt");
				xslt.init();
				// xslt.setForce(true);
				xslt.setIn(projectFile);
				xslt.setOut(buildXML);
				xslt.setStyle(conversionXslt);
				Param param = xslt.createParam();
				param.setName("basepath");
				param.setExpression(projectFile.getParent() + File.separator);
				param = xslt.createParam();
				param.setName("composestarpath");
				param.setExpression(composestarBase + "/");
				xslt.execute();
			}

			if (!buildXML.exists()) throw new Exception(buildXML.getName() + " does not exist.");

			Java java = (Java) getProject().createTask("java");
			java.init();
			java.setDir(projectFile.getParentFile());
			java.setClassname(master);
			Argument arg = java.createArg();
			arg.setValue(buildXML.toString());
			Path cpath = java.createClasspath();
			cpath.addFileset(cstarJars);
			java.setFork(true);
			java.setOutput(new File(projectFile.getParent() + File.separator + "buildlog.txt"));

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
				throw new BuildException("Compilation of project " + project + " failed; " + e.getMessage());
			}
			else
			{
				getProject().log(this, "Compilation of project " + project + " failed; " + e.getMessage(),
						Project.MSG_ERR);
			}
		}
	}
}
