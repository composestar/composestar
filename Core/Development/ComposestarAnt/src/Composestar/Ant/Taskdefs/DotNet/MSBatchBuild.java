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
	
	public MSBatchBuild()
	{
		super();
	}
	
	public void execute() throws BuildException
	{		
		String cmd[] = new String[5];
		cmd[0] = EXECUTABLE;
		cmd[2] = "/t:ComposeStarGen";
		cmd[3] = "/v:q";
		cmd[4] = "/nologo";
		
		ExecuteStreamHandler sh = new LogStreamHandler(this, Project.MSG_INFO, Project.MSG_WARN);		
		Execute exec = new Execute(sh);
		exec.setAntRun(getProject());
		
		try
		{
			List inputs = collectInputs();
			Iterator it = inputs.iterator();		
			while (it.hasNext())
			{
				File buildFile = (File)it.next();
				cmd[1] = buildFile.getAbsolutePath();
				exec.setCommandline(cmd);
				
				log(buildFile.getAbsolutePath(), Project.MSG_INFO);
	
				int result = exec.execute();			
				if (Execute.isFailure(result))
					log("! Failed ! Exitcode is " + result, Project.MSG_INFO);
				
				// TODO: add failOnError
			}
		}
		catch (IOException e)
		{
			throw new BuildException("Unable to execute command: " + e.getMessage());
		}
	}
}
