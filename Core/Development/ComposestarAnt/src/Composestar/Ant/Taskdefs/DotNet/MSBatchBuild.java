package Composestar.Ant.Taskdefs.DotNet;

import java.io.File;
import java.io.IOException;
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
	
	private String target;
	private boolean failOnError = false;
	
	public MSBatchBuild()
	{
		super();
	}
	
	public void setTarget(String target)
	{
		this.target = target;
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

			log("Building " + inputs.size() + " projects/solutions", Project.MSG_INFO);

			Iterator it = inputs.iterator();		
			while (it.hasNext())
			{
				File buildFile = (File)it.next();
				cmd[1] = buildFile.getAbsolutePath();
				exec.setCommandline(cmd);
				
				log(buildFile.getAbsolutePath(), Project.MSG_INFO);
	
				int result = exec.execute();			
				if (Execute.isFailure(result))
				{
					if (failOnError)
						throw new BuildException("MSBuild failed. Exitcode is " + result);
					else
						log("! Failed ! Exitcode is " + result, Project.MSG_INFO);
				}
			}
		}
		catch (IOException e)
		{
			throw new BuildException("Unable to execute command: " + e.getMessage());
		}
	}
	
	private void validateAttributes() throws BuildException
	{
		if (target == null)
			throw new BuildException("target attribute is required");		
	}
	
	private String[] createCommand()
	{
		String cmd[] = new String[5];
		cmd[0] = EXECUTABLE;
		cmd[1] = "<buildfile>";
		cmd[2] = "/t:" + target;
		cmd[3] = "/v:q";
		cmd[4] = "/nologo";
		
		return cmd;
	}
}
