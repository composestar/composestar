/**
 * 
 */
package ComposestarEclipsePlugin.Core.Actions;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import ComposestarEclipsePlugin.Core.ComposestarEclipsePluginPlugin;
import ComposestarEclipsePlugin.Core.Debug;

/**
 * Starts the Compose* visualization tool
 * 
 * @author Michiel Hendriks
 */
public class VisAction extends Action implements IWorkbenchWindowActionDelegate
{
	public VisAction()
	{}

	public void run(IAction action)
	{
		showViz();
	}

	protected void showViz()
	{
		String path = ComposestarEclipsePluginPlugin.getAbsolutePath("/Binaries");
		String projectLocation = selectedProjects[0].getProject().getLocation().toOSString();
		File cchz = new File(projectLocation, "ComposestarHistory.cchz");
		if (!cchz.exists())
		{
			Debug.instance().Log("No compile history available");
			return;
		}

		File dir = new File(path);
		String[] cmdarray = new String[4];
		cmdarray[0] = "java";
		cmdarray[1] = "-jar";
		cmdarray[2] = (new File(dir, "ComposestarVisualization.jar")).toString();
		cmdarray[3] = cchz.toString();
		String[] envp = new String[0];

		try
		{
			// Debug.instance().Log("Executing: " + Arrays.toString(cmdarray));
			Runtime.getRuntime().exec(cmdarray, envp, dir);
		}
		catch (IOException e)
		{
			Debug.instance().Log("Failed to launch compose* Visualizer: " + e.toString());
		}
	}
}
