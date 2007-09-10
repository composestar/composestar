package ComposestarEclipsePlugin.Java.Actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import ComposestarEclipsePlugin.Core.Debug;
import ComposestarEclipsePlugin.Java.MasterManager;

public class TestBuild implements IPlatformRunnable
{
	public Object run(Object args) throws Exception
	{
		Object[] args_ = (Object[]) args;

		try
		{
			Debug.instance().setLogToStd(true);

			// log compile results
			MasterManager m = MasterManager.getInstance();
			m.logOutput = true;

			// disable debugging
			Debug.instance().setEnabled(false);

			// find project
			IJavaProject jp = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getJavaProject("" + args_[0]);
			IProject[] projects = new IProject[1];
			IProject project = jp.getProject();
			projects[0] = project;

			// open project
			NullProgressMonitor npm = new NullProgressMonitor();
			if (!project.exists())
			{
				// create it first
				project.create(npm);
			}
			project.open(npm);
			project.refreshLocal(IResource.DEPTH_INFINITE, null);

			JavaBuildAction j = new JavaBuildAction();
			j.setSelectedProjects(projects);

			j.build();

			if (!j.builtOk)
			{
				return new Integer(1);
			}
		}
		catch (Exception e)
		{
			return new Integer(2);
		}

		return EXIT_OK;
	}
}
