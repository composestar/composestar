package ComposestarEclipsePlugin.Java;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogSettings;

import ComposestarEclipsePlugin.Core.ComposestarEclipsePluginPlugin;
import ComposestarEclipsePlugin.Core.Debug;
import ComposestarEclipsePlugin.Core.IComposestarConstants;
import ComposestarEclipsePlugin.Core.BuildConfiguration.BuildConfigurationManager;
import ComposestarEclipsePlugin.Core.Utils.CommandLineExecutor;
import ComposestarEclipsePlugin.Core.Utils.FileUtils;

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
	protected String jvmOptions = "-Xmx128m";

	/**
	 * If true the output is logged to a file.
	 */
	public boolean logOutput = false;

	/**
	 * The name of the logfile.
	 */
	protected String logFile = "buildlog.txt";

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

	public void run(IDialogSettings settings)
	{

		BuildConfigurationManager bcmanager = BuildConfigurationManager.instance();
		String basePath = FileUtils.getDirectoryPart(bcmanager.buildconfigFile);
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

			String pluginPath = ComposestarEclipsePluginPlugin.getAbsolutePath("");
			String classPath = settings.get("classpath");
			classPath = defaultCp.toString() + classPath.replaceAll("%composestar%", pluginPath);

			Debug.instance().Log("Classpath = " + classPath);

			String command = "java " + jvmOptions + " -cp \"" + classPath + "\" " + mainClass + " " + "\""
					+ bcmanager.buildconfigFile + "\"";
			if (logOutput)
			{
				command += " > " + basePath + java.io.File.separator + logFile;
			}
			CommandLineExecutor cmdExec = new CommandLineExecutor();
			int result = cmdExec.exec("call " + command);

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
