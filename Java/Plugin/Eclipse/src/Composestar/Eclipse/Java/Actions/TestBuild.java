package Composestar.Eclipse.Java.Actions;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import Composestar.Eclipse.Core.Debug;

public class TestBuild implements IPlatformRunnable
{
	public Object run(Object args) throws Exception
	{
		Object[] args_ = (Object[]) args;

		try
		{
			Debug.instance().setLogToStd(true);

			// disable debugging
			Debug.instance().setEnabled(false);

			IWorkspaceDescription description = ResourcesPlugin.getWorkspace().getDescription();
			description.setAutoBuilding(false);
			ResourcesPlugin.getWorkspace().setDescription(description);

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

			Debug.instance().setFileLog(new File(project.getLocation().toFile(), "buildlog.txt"));

			try
			{
				project.refreshLocal(IResource.DEPTH_INFINITE, npm);
			}
			catch (CoreException e)
			{
				// just ignore for now
			}
			project.build(IncrementalProjectBuilder.FULL_BUILD, npm);

			// JavaBuildAction j = new JavaBuildAction();
			// j.setSelectedProjects(projects);
			// j.build();
			//
			// Debug.instance().setFileLog(null);
			// if (!j.builtOk)
			// {
			// return new Integer(1);
			// }
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
			e.printStackTrace();
			return new Integer(2);
		}

		return EXIT_OK;
	}
}
