package Composestar.Ant.Taskdefs.DotNet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.taskdefs.LogStreamHandler;

import Composestar.Ant.Taskdefs.BaseTask;

/**
* @author Marcus Klimstra
*/
public final class MSBatchBuild extends BaseTask
{
	private final static String EXECUTABLE = "msbuild";
	private final static int BUILDFILE_INDEX = 1;
	
	private String target;
	private boolean failFast = false;
	private boolean failOnError = false;
	
	public MSBatchBuild()
	{
		super();
	}
	
	public void setTarget(String target)
	{
		this.target = target;
	}
	
	public void setFailFast(boolean failFast)
	{
		this.failFast = failFast;
	}
	
	public void setFailOnError(boolean failOnError)
	{
		this.failOnError = failOnError;
	}
	
	public void execute() throws BuildException
	{
		validateAttributes();
		String[] cmd = createCommand();
		
		ExecuteStreamHandler sh = new LogStreamHandler(this, Project.MSG_INFO, Project.MSG_WARN);		
		Execute exec = new Execute(sh);
		exec.setAntRun(getProject());
		
		try
		{
			List inputs = collectInputs();
			int totalCount = inputs.size();
			int failCount = 0; 
			failList.clear();

			String targetName = (target == null ? "default target" : "target '" + target + "'");
			log("Executing " + targetName + " on " + totalCount + " projects/solutions", Project.MSG_INFO);

			Iterator it = inputs.iterator();		
			while (it.hasNext())
			{
				File buildFile = (File)it.next();
				cmd[BUILDFILE_INDEX] = buildFile.getAbsolutePath();
				exec.setCommandline(cmd);
				
				log(buildFile.getAbsolutePath(), Project.MSG_INFO);
	
				int result = exec.execute();			
				if (Execute.isFailure(result))
				{
					String msg = "! Failed ! Exitcode is " + result;
					if (failFast)
						throw new BuildException(msg);
					else
					{
						failCount++;
						failList.add(buildFile.getAbsolutePath());
						log(msg, Project.MSG_INFO);
					}
				}
			}
			
			reportResults(totalCount, failCount);
			
			if (failOnError && failCount > 0)
			{
				throw new BuildException("" + failCount + " failures.");
			}
		}
		catch (IOException e)
		{
			throw new BuildException("Unable to execute command: " + e.getMessage());
		}
	}
	
	private void validateAttributes() throws BuildException
	{
	}
	
	private String[] createCommand()
	{
		List cmd = new ArrayList();
		cmd.add(EXECUTABLE);
		cmd.add("<buildfile>");
		
		if (target != null)
			cmd.add("/t:" + target);
		
		cmd.add("/v:q");
		cmd.add("/nologo");
		
		String[] cmdArray = new String[cmd.size()];
		cmd.toArray(cmdArray);
		
		return cmdArray;
	}

	private void reportResults(int total, int fail)
	{
		log("" +
				"total: " + total + 
				"; success: " + (total - fail) +
				"; failed: " + fail + ".",
				(fail == 0 ? Project.MSG_INFO : Project.MSG_WARN));
			
		if (fail > 0)
		{
			log("Failed to build the following projects:", Project.MSG_ERR);		
			reportFailures();
		}
	}
}
