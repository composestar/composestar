package Composestar.Ant.Taskdefs.C;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DirSet;

import Composestar.Ant.Taskdefs.BaseTask;

/**
 * Compiles Compose* projects.
 */
public class CstarComp extends BaseTask
{
	
	private final List m_dirSets;
	
	/**
	 * Eclipse launcher
	 */
	protected String launcher = "org.eclipse.core.launcher.Main";
	
	/**
	 * If true fail the build if a single project failed to compile
	 */
	protected boolean failOnError = true;

	/**
	 * If true fail on the first build that failed to compile
	 */
	protected boolean failOnFirstError = false;

	/**
	 * The location of eclipse workspace; location of the examples.
	 */
	protected String workspace;
	
	/**
	 * The location of eclipse (%ECLIPSE_HOME%).
	 */
	protected String eclipseHome;

	/**
	*Used customfilters of the project
	*/
	protected String customfilterString;
	
	/**
	 * Application-id as defined in plugin.xml
	 */
	protected String application;

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
		m_dirSets = new ArrayList();
	}
	
	public void setcustomfilterString(String customfilterString)
	{
		this.customfilterString = customfilterString;
	}
	public void setFailOnError(boolean failOnError)
	{
		this.failOnError = failOnError;
	}

	public void setFailOnFirstError(boolean failOnFirstError)
	{
		this.failOnFirstError = failOnFirstError;
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
			String projectName = (String)it.next();
			compileProject(projectName);
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

	protected void compileProject(String projectName) throws BuildException
	{
		log("" + (cntCurrent * 100 / cntTotal) + "% - " + projectName, Project.MSG_INFO);
		cntCurrent++;

		try
		{
			
			/*********
			 * java -cp startup.jar org.eclipse.core.launcher.Main 
			 * -application ComposestarEclipsePlugin.headlessCEPTest 
			 * EncryptionExample //ProjectName 
			 * -customfilter //Customfilters should be added
			 * C:\Utwente\Afstuderen\jwtewinkel\TestOmgeving\SystemTest\EncryptionExample\CustomFilters\Decryption.jar 
			 * C:\Utwente\Afstuderen\jwtewinkel\TestOmgeving\SystemTest\EncryptionExample\CustomFilters\Encryption.jar
			 */
			Runtime rt = Runtime.getRuntime();

			String command = "java -cp "+ eclipseHome+"/startup.jar "+ launcher + " -application "+"ComposestarEclipsePlugin.headlessCEPTest "+ projectName  + " -customfilter "+ customfilterString+ " -data "+ workspace;
			

			log("Execution command: " + command);

	        Process proc = rt.exec(command);
			int err = proc.waitFor();			
			/**Java java = (Java) getProject().createTask("java");
			java.init();
			java.setDir(new File(eclipseHome));
			java.setClassname(launcher);

			// create arguments
			Argument arg = java.createArg();
			arg.setValue("-application");
			arg = java.createArg();
			arg.setValue(application);
			arg = java.createArg();
			arg.setValue(projectName);
			arg = java.createArg();
			arg.setValue("-data");
			arg = java.createArg();
			arg.setValue(workspace);
			arg = java.createArg();
			arg.setValue("-customfilter");
			arg = java.createArg();
			arg.setValue(customfilterString);
			arg = java.createArg();
			
			arg.setValue("-clean");

			Path cpath = java.createClasspath();
			FileSet startupJar = new FileSet();
			startupJar.setDir(new File(eclipseHome));
			NameEntry inc = startupJar.createInclude();
			inc.setName("startup.jar");
			cpath.addFileset(startupJar); 
			
			java.setFork(true);
			
			int err = java.executeJava();**/
			if (err != 0)
			{
				throw new Exception("Exit code is not zero: "+err+" (check buildlog.txt in project basedir for more information)");
			}
			
			cntSuccess++;
		}
		catch (Exception e)
		{
			cntFail++;
			if (failOnFirstError)
			{
				throw new BuildException("Compilation of project " + projectName + " failed; " + e.getMessage());
			}
			else
			{
				addFailure(projectName, e.getMessage());
			}
		}
	}
	
	protected List collectInputs() throws BuildException
	{
		List result = new ArrayList();
		Iterator it = m_dirSets.iterator();
		while (it.hasNext())
		{
			try {
				DirSet ds = (DirSet)it.next();
				String[] dirs = ds.getDirectoryScanner(getProject()).getIncludedDirectories();
				for(int i=0; i<dirs.length; i++)
				{
					result.add(dirs[i]);
				}
			}
			catch(Exception e)
			{
				throw new BuildException("Error while collecting inputs: "+e.getMessage());
			}
		}
		return result;
	}
}
