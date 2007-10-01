package ComposestarEclipsePlugin.C;

import java.util.Arrays;

import ComposestarEclipsePlugin.C.BuildConfiguration.BuildConfigurationManager;
import ComposestarEclipsePlugin.Core.Debug;
import ComposestarEclipsePlugin.Core.Utils.CommandLineExecutor;

/**
 * Class for triggering the compose* compiler
 */
public class MasterManager
{

	public boolean completed = false;

	private String basePath = "";

	public MasterManager()
	{

	}

	// TODO implement this
	public void run()
	{

		BuildConfigurationManager bcmanager = BuildConfigurationManager.instance();
		// create process
		Runtime rt = Runtime.getRuntime();
		try
		{

			String[] command = new String[4];
			command[0] = "make";
			command[1] = "build";
			command[2] = "-C";
			command[3] = basePath;
			Debug.instance().Log("Calling command: " + Arrays.toString(command));
			CommandLineExecutor cmdExec = new CommandLineExecutor();
			int result = cmdExec.exec(command);

			if (result == 0)
			{
				// Debug.instance().Log(cmdExec.outputNormal());
				completed = true;
			}
			else
			{
				// Debug.instance().Log(cmdExec.outputError());
				Debug.instance().Log("Master run failure reported by process. Exit code is " + result, Debug.MSG_ERROR);
			}
		}
		catch (Exception e)
		{

		}
	}

	public void setOutputPath(String outputPath)
	{}

	public void setBasePath(String basePath)
	{
		this.basePath = basePath;
	}

}
