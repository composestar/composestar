package ComposestarEclipsePlugin.Java.Actions;

import java.io.PrintStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import ComposestarEclipsePlugin.Core.Debug;

public class TestRun implements IPlatformRunnable
{

	/**
	 * Error exit code, returned when run action cannot be performed.
	 */
	private final int RUN_FAILURE = 19;

	/**
	 * Error exit code, returned when an error occurs while running a program.
	 */
	private final int RUN_PROCESS_FAILURE = 20;

	public Object run(Object args) throws Exception
	{
		Object[] args_ = (Object[]) args;

		try
		{
			// disable debugging
			Debug.instance().setEnabled(false);

			// disable auto building
			IWorkspace ws = ResourcesPlugin.getWorkspace();
			IWorkspaceDescription desc = ws.getDescription();
			desc.setAutoBuilding(false);
			ws.setDescription(desc);

			// find project
			IJavaProject jp = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getJavaProject("" + args_[0]);
			IProject[] projects = new IProject[1];
			IProject project = jp.getProject();
			projects[0] = project;

			JavaRunAction j = new JavaRunAction();
			j.setSelectedProjects(projects);
			j.perform();

			if (!j.completed)
			{
				return new Integer(RUN_PROCESS_FAILURE);
			}
		}
		catch (Exception e)
		{
			return new Integer(RUN_FAILURE);
		}
		return EXIT_OK;
	}
}
