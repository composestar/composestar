package Composestar.Eclipse.Java;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
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
import org.eclipse.osgi.baseadaptor.BaseData;
import org.eclipse.osgi.baseadaptor.hooks.StorageHook;
import org.eclipse.osgi.framework.adaptor.BundleData;
import org.eclipse.osgi.framework.internal.core.AbstractBundle;
import org.eclipse.osgi.internal.baseadaptor.BaseStorageHook;
import org.osgi.framework.Bundle;

import Composestar.Eclipse.Core.CompileMarkers;
import Composestar.Eclipse.Core.ComposestarEclipsePluginPlugin;
import Composestar.Eclipse.Core.Debug;
import Composestar.Eclipse.Core.IComposestarConstants;

/**
 * Class for triggering the compose* compiler
 */
public class MasterManager
{

	public static final String EXECUTION_ENV_16 = "JavaSE-1.6";

	public static final String EXECUTION_ENV_15 = "J2SE-1.5";

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

	public void run(IProject proj, File buildconfig, IProgressMonitor monitor)
	{
		Debug dbg = Debug.instance();
		try
		{
			dbg.clear();
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

			monitor.worked(1);

			monitor.subTask("Setting up execution environment");
			IVMInstall vminstall = getJRE(EXECUTION_ENV_16, dbg);
			if (vminstall == null)
			{
				dbg.Log(String.format("No compatible JRE found for %s, falling back to %s.", EXECUTION_ENV_16,
						EXECUTION_ENV_15), Debug.MSG_WARNING);
				vminstall = getJRE(EXECUTION_ENV_15, dbg);
			}
			if (vminstall == null)
			{
				dbg.Log(String.format("Unable to find a compatible Java execution environment for %s or %s.",
						EXECUTION_ENV_16, EXECUTION_ENV_15), Debug.MSG_ERROR);
				completed = false;
				return;
			}

			if (Platform.getBundle("org.eclipse.jdt.compiler.tool") != null)
			{
				// only available when eclipse runs in JRE6
				monitor.subTask("Resolving Eclipse Java compiler");
				// this will register the eclipse java compiler as compiler
				// service
				cp.add(getBundlePath(JavaCore.PLUGIN_ID));
				cp.add(getBundlePath("org.eclipse.jdt.compiler.tool"));
			}

			for (LibraryLocation libloc : JavaRuntime.getLibraryLocations(vminstall))
			{
				cp.add(libloc.getSystemLibraryPath().makeAbsolute().toFile().toString());
			}
			//dbg.print("Classpath: " + cp.toString() + "\n", Debug.MSG_INFORMATION);

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

			proj.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
			CompileMarkers markers = new CompileMarkers(proj);

			runner.run(runconfig, launch, monitor);
			IProcess[] procs = launch.getProcesses();
			IProcess proc = procs[0];
			proc.getStreamsProxy().getOutputStreamMonitor().addListener(new OutputStreamListener(markers));
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
			dbg.Log("Master run failure reported: " + e, IComposestarConstants.MSG_ERROR);
			StringWriter sb = new StringWriter();
			e.printStackTrace(new PrintWriter(sb));
			dbg.Log(sb.toString(), IComposestarConstants.MSG_ERROR);
			completed = false;
		}
	}

	/**
	 * @param dbg
	 * @return
	 */
	private IVMInstall getJRE(String envid, Debug dbg)
	{
		IExecutionEnvironment exenv = JavaRuntime.getExecutionEnvironmentsManager().getEnvironment(envid);
		if (exenv == null)
		{
			// dbg.Log(String.format("Unable to find the %s Java execution environment.",
			// envid), Debug.MSG_ERROR);
			return null;
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
			// dbg.Log(String.format("Unable to find a compatible Java execution environment for %s.",
			// EXECUTION_ENV_ID),
			// Debug.MSG_ERROR);
			return null;
		}
		return vminstall;
	}

	/**
	 * Get the location to a bundle, to be used in the classpath
	 * 
	 * @param bundleId
	 * @return
	 */
	@SuppressWarnings("restriction")
	protected String getBundlePath(String bundleId)
	{
		// first try an "unstable" but fast lookup
		try
		{
			Bundle bndl = org.eclipse.core.runtime.Platform.getBundle(bundleId);
			if (bndl instanceof AbstractBundle)
			{
				AbstractBundle abndl = (AbstractBundle) bndl;
				BundleData bdata = abndl.getBundleData();
				if (bdata instanceof BaseData)
				{
					BaseData bd = (BaseData) bdata;
					StorageHook sh = bd.getStorageHook(BaseStorageHook.KEY);
					if (sh instanceof BaseStorageHook)
					{
						BaseStorageHook bsh = (BaseStorageHook) sh;
						if (bsh != null)
						{
							return (new File(bsh.getFileName())).getAbsolutePath();
						}
					}
				}
			}
		}
		catch (Exception e)
		{
		}
		// this one always works, but it slow
		String path = ComposestarEclipsePluginPlugin.getAbsolutePath("/", bundleId);
		if (path == null)
		{
			return null;
		}
		return (new File(path)).getAbsolutePath();
	}

	/**
	 * @author mhendrik
	 */
	static class OutputStreamListener implements IStreamListener
	{
		int level = Debug.MSG_INFORMATION;

		CompileMarkers markers;

		public OutputStreamListener(CompileMarkers marks)
		{
			markers = marks;
		}

		public void streamAppended(String text, IStreamMonitor monitor)
		{
			Debug.instance().print(text, Debug.MSG_INFORMATION);
			markers.append(text);
		}
	}
}
