package Composestar.Eclipse.Java.Actions;

import java.io.File;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import Composestar.Core.Exception.ConfigurationException;
import Composestar.Eclipse.Core.Debug;
import Composestar.Eclipse.Core.IComposestarConstants;
import Composestar.Eclipse.Core.Actions.BuildAction;
import Composestar.Eclipse.Core.Utils.Timer;
import Composestar.Eclipse.Java.JavaBuildConfigGenerator;
import Composestar.Eclipse.Java.MasterManager;

/**
 * Action for building a Java project with Compose*.
 */
public class JavaBuildAction extends BuildAction implements IWorkbenchWindowActionDelegate
{
	/**
	 * Constructor.
	 */
	public JavaBuildAction()
	{}

	/**
	 * Performs the Action.
	 * <p>
	 * This method is called by the proxy action when the action has been
	 * triggered.
	 * <p>
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run
	 */
	public void run(IAction action)
	{
		// A thread is necessary for UI refreshing.
		new Thread(new Runnable()
		{
			public void run()
			{
				build();
			}
		}).start();
	}

	/**
	 * The action.
	 */
	public void build()
	{
		// time the build proces
		Timer t = new Timer();
		t.start();

		Debug.instance().clear();
		Debug.instance().Log("------ Composestar build started ------\n");
		Debug.instance().Log("Creating configuration");

		builtOk = true;

		JavaBuildConfigGenerator bconfiggen = new JavaBuildConfigGenerator();
		try
		{
			bconfiggen.addProject(selectedProjects[0]);
		}
		catch (ConfigurationException e)
		{
			Debug.instance().Log("Configuration exception: " + e.getMessage(), Debug.MSG_ERROR);
			return;
		}
		File buildConfigFile = new File(selectedProjects[0].getLocation().toFile(), "BuildConfiguration.xml");
		if (!bconfiggen.generate(buildConfigFile))
		{
			// TODO improve
			Debug.instance().Log("Could not generate build config", Debug.MSG_ERROR);
			return;
		}

		MasterManager m = MasterManager.getInstance();
		if (builtOk)
		{
			Debug.instance().Log("Invoking Master...");

			m.run(selectedProjects[0], buildConfigFile, null);
			if (!m.completed)
			{
				builtOk = false;
			}
		}

		if (builtOk)
		{
			t.stop();
			double time_in_seconds = ((double) t.getElapsed()) / 1000;
			Debug.instance().Log("---------------- Done -----------------");
			Debug.instance().Log("");
			Debug.instance().Log("Composestar build complete in " + time_in_seconds + " seconds");
			Debug.instance().Log("");
			Debug.instance().Log("");
		}
		else
		{
			Debug.instance().Log("---------------- Done -----------------");
			Debug.instance().Log("");
			Debug.instance().Log("Composestar build failed.", IComposestarConstants.MSG_ERROR);
			Debug.instance().Log("");
			Debug.instance().Log("");
		}
	}
}
