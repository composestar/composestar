package Composestar.Eclipse.Java;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;

import Composestar.Eclipse.Core.ComposestarEclipsePluginPlugin;
import Composestar.Eclipse.Core.Debug;
import Composestar.Eclipse.Core.IComposestarConstants;

/**
 * Class for triggering the compose* compiler
 */
public class MasterManager
{

	public static final String EXECUTION_ENV_ID = "JavaSE-1.6";

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

	public MasterManager()
	{}

	public static MasterManager getInstance()
	{
		if (Instance == null)
		{
			Instance = new MasterManager();
		}
		return Instance;
	}

	public void run(File buildconfig, IProgressMonitor monitor)
	{
		try
		{
			Debug.instance().clear();
			if (monitor == null)
			{
				monitor = new NullProgressMonitor();
			}
			monitor.beginTask(String.format("Compiling Comose*/Java project"), 3);
			monitor.subTask("Resolving classpath");

			// automatically resolve classpath
			List<String> cp = ComposestarEclipsePluginPlugin.getJarClassPath(ComposestarEclipsePluginPlugin
					.getAbsolutePath(IComposestarConstants.LIB_DIR + "ComposestarCORE.jar"));
			cp.addAll(ComposestarEclipsePluginPlugin.getJarClassPath(ComposestarEclipsePluginPlugin.getAbsolutePath(
					IComposestarConstants.LIB_DIR + "ComposestarJava.jar", IComposestarJavaConstants.BUNDLE_ID)));

			monitor.subTask("Resolving classpath (Eclipse Java Compiler)");
			// this will register the eclipse java compiler as compiler service
			cp.add(ComposestarEclipsePluginPlugin.getAbsolutePath("/", JavaCore.PLUGIN_ID));

			monitor.worked(1);

			monitor.subTask("Setting up execution environment");
			IExecutionEnvironment exenv = JavaRuntime.getExecutionEnvironmentsManager()
					.getEnvironment(EXECUTION_ENV_ID);
			if (exenv == null)
			{
				Debug.instance().Log(
						String.format("Unable to find the %s Java execution environment.", EXECUTION_ENV_ID),
						Debug.MSG_ERROR);
				completed = false;
				return;
			}

			IVMInstall vminstall = exenv.getDefaultVM();
			if (vminstall == null)
			{
				for (IVMInstall imvi : exenv.getCompatibleVMs())
				{
					vminstall = imvi;
					break;
				}
			}
			if (vminstall == null)
			{
				Debug.instance().Log(
						String.format("Unable to find a compatible Java execution environment for %s.",
								EXECUTION_ENV_ID), Debug.MSG_ERROR);
				completed = false;
				return;
			}
			for (LibraryLocation libloc : JavaRuntime.getLibraryLocations(vminstall))
			{
				cp.add(libloc.getSystemLibraryPath().makeAbsolute().toFile().toString());
			}
			Debug.instance().print("Classpath: " + cp.toString() + "\n", Debug.MSG_INFORMATION);

			IVMRunner runner = vminstall.getVMRunner(ILaunchManager.RUN_MODE);
			VMRunnerConfiguration runconfig = new VMRunnerConfiguration(mainClass, cp.toArray(new String[cp.size()]));
			String[] args = { buildconfig.toString() };
			runconfig.setProgramArguments(args);
			ILaunchConfigurationType configType = DebugPlugin.getDefault().getLaunchManager()
					.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
			ILaunch launch = new Launch(configType.newInstance(null, "Compose*/Java Compiler"),
					ILaunchManager.RUN_MODE, null);
			monitor.worked(1);
			monitor.subTask("Compiling");

			runner.run(runconfig, launch, monitor);
			IProcess[] procs = launch.getProcesses();
			IProcess proc = procs[0];
			proc.getStreamsProxy().getOutputStreamMonitor().addListener(new IStreamListener()
			{
				public void streamAppended(String text, IStreamMonitor monitor)
				{
					Debug.instance().print(text, Debug.MSG_INFORMATION);
				}
			});
			proc.getStreamsProxy().getErrorStreamMonitor().addListener(new IStreamListener()
			{
				public void streamAppended(String text, IStreamMonitor monitor)
				{
					Debug.instance().print(text, Debug.MSG_ERROR);
				}
			});
			while (!proc.isTerminated())
			{
				Thread.sleep(50);
				if (monitor.isCanceled())
				{
					if (proc.canTerminate())
					{
						proc.terminate();
					}
				}
			}
			completed = proc.getExitValue() == 0;
			monitor.done();
		}
		catch (Exception e)
		{
			Debug.instance().Log("Master run failure reported: " + e.getCause().getMessage(),
					IComposestarConstants.MSG_ERROR);
			completed = false;
		}
	}
}
