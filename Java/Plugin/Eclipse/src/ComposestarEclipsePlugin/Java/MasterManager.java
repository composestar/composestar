package ComposestarEclipsePlugin.Java;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ComposestarEclipsePlugin.Core.ComposestarEclipsePluginPlugin;
import ComposestarEclipsePlugin.Core.Debug;
import ComposestarEclipsePlugin.Core.IComposestarConstants;
import ComposestarEclipsePlugin.Core.Utils.CommandLineExecutor;

/**
 * Class for triggering the compose* compiler
 */
public class MasterManager
{

	/**
	 * The instance.
	 */
	private static MasterManager Instance = null;

	/**
	 * If true the compilation has ended succesfully
	 */
	public boolean completed = false;

	/**
	 * The main class to execute
	 */
	protected String mainClass = "Composestar.Java.MASTER.JavaMaster";

	/**
	 * The JVM options
	 */
	protected String[] jvmOptions = { "-Xmx128m" };

	/**
	 * If true the output is logged to a file.
	 */
	public boolean logOutput = false;

	/**
	 * The name of the logfile.
	 */
	public File logFile;

	public MasterManager()
	{

	}

	public static MasterManager getInstance()
	{
		if (Instance == null)
		{
			Instance = new MasterManager();
		}
		return Instance;
	}

	public void run(File buildconfig)
	{
		try
		{
			// automatically resolve classpath
			List<String> cp = ComposestarEclipsePluginPlugin.getJarClassPath(ComposestarEclipsePluginPlugin
					.getAbsolutePath(IComposestarConstants.LIB_DIR + "ComposestarCORE.jar"));
			cp.addAll(ComposestarEclipsePluginPlugin.getJarClassPath(ComposestarEclipsePluginPlugin.getAbsolutePath(
					IComposestarConstants.LIB_DIR + "ComposestarJava.jar", IComposestarJavaConstants.BUNDLE_ID)));

			StringBuilder defaultCp = new StringBuilder();
			for (String cpEntry : cp)
			{
				defaultCp.append(cpEntry);
				defaultCp.append(";");
			}

			Debug.instance().Log("Classpath = " + defaultCp.toString());

			List<String> cmdLine = new ArrayList<String>();
			cmdLine.add("java");
			cmdLine.addAll(Arrays.asList(jvmOptions));
			cmdLine.add("-cp");
			cmdLine.add(defaultCp.toString());
			cmdLine.add(mainClass);
			cmdLine.add(buildconfig.toString());

			PrintStream outStream = null;
			if (logOutput && logFile != null)
			{
				outStream = new PrintStream(logFile);
			}
			CommandLineExecutor cmdExec = new CommandLineExecutor(outStream);
			int result = cmdExec.exec(cmdLine.toArray(new String[cmdLine.size()]));

			if (result == 0)
			{
				completed = true;
			}
			else
			{
				Debug.instance().Log("Master run failure reported by process. Exit code is " + result,
						IComposestarConstants.MSG_ERROR);
			}
		}
		catch (Exception e)
		{
			Debug.instance().Log("Master run failure reported: " + e.getCause().getMessage(),
					IComposestarConstants.MSG_ERROR);
			completed = false;
		}
	}
}
