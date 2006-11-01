package ComposestarEclipsePlugin.Java.Actions;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.IJavaProject;
import org.osgi.framework.Bundle;

import java.io.PrintStream;

import ComposestarEclipsePlugin.Core.Debug;
import ComposestarEclipsePlugin.Java.MasterManager;

public class TestBuild implements IPlatformRunnable
{
	public Object run(Object args) throws Exception 
	{
		Object[] args_ = (Object[])args;
		PrintStream orig = System.out;
		
		try {
			
			// FIXME: why is this necessary?
			Bundle b = Platform.getBundle("ComposestarEclipsePlugin");
			b.loadClass("ComposestarEclipsePlugin.Core.Utils.FileUtils");
			b.loadClass("ComposestarEclipsePlugin.Core.Utils.Timer");
			b.loadClass("ComposestarEclipsePlugin.Core.BuildConfiguration.ModuleSetting");
			b.loadClass("ComposestarEclipsePlugin.Java.MasterManager");
			b.loadClass("ComposestarEclipsePlugin.Core.Utils.CommandLineExecutor");
			b.loadClass("ComposestarEclipsePlugin.Core.Utils.StreamGobbler");
		
			// log compile results
			MasterManager m = MasterManager.getInstance();
			m.logOutput = true;
		
			// disable debugging
			Debug.instance().setEnabled(false);
		
			// retrieve the original printstream
			System.setOut(orig);
		
			// find project
			IJavaProject jp = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getJavaProject(""+args_[0]);
			IProject[] projects = new IProject[1];
			IProject project = jp.getProject();
			projects[0] = project;
			
			// open project
			NullProgressMonitor npm = new NullProgressMonitor();
			if(!project.exists())
			{
				// create it first
				project.create(npm);
			}
			project.open(npm);
			project.refreshLocal(project.DEPTH_INFINITE,null);
			
			JavaBuildAction j = new JavaBuildAction();
			j.setSelectedProjects(projects);
			
			j.build();
			
			if(!j.builtOk)
			{
				return new Integer(1);
			}
		}
		catch(Exception e)
		{
			return new Integer(2);
		}
		
		return EXIT_OK;
	}
}
