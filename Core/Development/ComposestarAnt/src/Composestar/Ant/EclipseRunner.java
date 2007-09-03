/**
 * 
 */
package Composestar.Ant;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.ExecuteWatchdog;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Commandline.Argument;
import org.apache.tools.ant.types.PatternSet.NameEntry;

/**
 * Used by various tasks that call a headless eclipse application
 * 
 * @author Michiel Hendriks
 */
public class EclipseRunner
{
	/**
	 * Execute Java with startup.jar
	 */
	protected static final int MODE_STARTUPJAR = 1;

	/**
	 * Call eclipsc; used in eclipse 3.3
	 */
	protected static final int MODE_EXCLIPSEC = 2;

	/**
	 * Eclipse launcher
	 */
	protected static final String launcher = "org.eclipse.core.launcher.Main";

	protected static final String startupjar = "startup.jar";

	protected int mode;

	protected Project project;

	protected File home;

	protected String application;

	protected String[] appArgs;

	protected String workspace;

	protected String[] additionalArgs = {"-clean"};

	protected long timeout;

	public EclipseRunner(Project inProject, String eclipseHome)
	{
		project = inProject;
		home = new File(eclipseHome);
		detectMode();
	}

	protected void detectMode()
	{
		if (!home.isDirectory()) return;
		File sjar = new File(home, startupjar);
		if (sjar.exists())
		{
			mode = MODE_STARTUPJAR;
			return;
		}
		String[] ecfiles = home.list(new FilenameFilter()
		{
			public boolean accept(File dir, String name)
			{
				return name.toLowerCase().startsWith("eclipsec");
			}
		});
		if (ecfiles.length > 0)
		{
			mode = MODE_EXCLIPSEC;
			return;
		}
	}

	public void setApplication(String app)
	{
		application = app;
	}

	public void setAppArgs(String[] args)
	{
		appArgs = args;
	}

	public void setWorkspace(String ws)
	{
		workspace = ws;
	}

	public void setArgs(String[] args)
	{
		additionalArgs = args;
	}

	public int execute()
	{
		switch (mode)
		{
			case MODE_STARTUPJAR:
				return executeStartupjar();
			case MODE_EXCLIPSEC:
				return executeEclipsec();
			default:
				project.log("No valid eclipse execution method", Project.MSG_ERR);
				return -1;
		}
	}

	/**
	 * Get the commandline
	 * 
	 * @return
	 */
	public String[] getCmdLine()
	{
		List<String> cmd = new ArrayList<String>();
		switch (mode)
		{
			case MODE_STARTUPJAR:
				cmd.add("java");
				cmd.add("-cp");
				cmd.add((new File(home, startupjar)).getAbsolutePath());
				cmd.add(launcher);
				break;
			case MODE_EXCLIPSEC:
				cmd.add((new File(home, "eclipsec")).getAbsolutePath());
				cmd.add("-nosplash");
				break;
			default:
				// TODO: error
				return null;
		}
		cmd.addAll(Arrays.asList(getArguments()));
		project.log(cmd.toString(), Project.MSG_DEBUG);
		String[] ret = new String[0];
		return cmd.toArray(ret);
	}

	/**
	 * @return the list of arguments
	 */
	public String[] getArguments()
	{
		List<String> args = new ArrayList<String>();

		args.add("-application");
		args.add(application);
		if (appArgs != null)
		{
			for (int i = 0; i < appArgs.length; i++)
			{
				args.add(appArgs[i]);
			}
		}

		if (workspace != null && workspace.length() > 0)
		{
			args.add("-data");
			args.add(workspace);
		}

		if (additionalArgs != null)
		{
			for (int i = 0; i < additionalArgs.length; i++)
			{
				args.add(additionalArgs[i]);
			}
		}

		String[] ret = new String[0];
		return args.toArray(ret);
	}

	protected int executeStartupjar()
	{
		Java java = (Java) project.createTask("java");
		java.init();
		java.setDir(home);
		java.setClassname(launcher);
		if (timeout > 0) java.setTimeout(timeout);

		String[] args = getArguments();
		for (int i = 0; i < args.length; i++)
		{
			Argument arg = java.createArg();
			arg.setValue(args[i]);
		}

		Path cpath = java.createClasspath();
		FileSet startupJar = new FileSet();
		startupJar.setDir(home);
		NameEntry inc = startupJar.createInclude();
		inc.setName("startup.jar");
		cpath.addFileset(startupJar);

		java.setFork(true);

		int err = java.executeJava();
		return err;
	}

	protected int executeEclipsec()
	{
		Execute execute;
		if (timeout > 0)
		{
			ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout);
			execute = new Execute(null, watchdog);
		}
		else
		{
			execute = new Execute();
		}
		execute.setAntRun(project);
		execute.setSpawn(false);
		execute.setWorkingDirectory(home);
		execute.setCommandline(getCmdLine());
		int err;
		try
		{
			err = execute.execute();
			if (execute.killedProcess())
			{
				return -1;
			}
		}
		catch (IOException e)
		{
			return -1;
		}
		return err;
	}
}
