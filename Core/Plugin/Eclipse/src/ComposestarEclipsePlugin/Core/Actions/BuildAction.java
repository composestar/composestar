package ComposestarEclipsePlugin.Core.Actions;

import ComposestarEclipsePlugin.Core.BuildConfiguration.*;
import ComposestarEclipsePlugin.Core.Utils.FileUtils;

import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;

/** 
 *  TODO: Add support for building multiple projects.
 */

public class BuildAction {
	
	protected IWorkbenchWindow window;
	protected static final IProject[] NO_PROJECTS = new IProject[0];
	protected IProject[] selectedProjects = NO_PROJECTS;
	protected IProject project = null;
	protected IPath projectLocation = null;
	protected IResource[] projectMembers= null;
	
	protected BuildConfigurationManager buildConfig;
	protected HashSet concerns = new HashSet();
	protected HashSet sources = new HashSet();
	protected Path basePath = new Path();
	protected Path composestarPath = new Path();
	protected Project projectConfig = null;
	protected String language;
	
	protected String buildPath = "";
	protected String outputPath = "";
	public boolean builtOk = true;
		
	/**
	 * The constructor.
	 */
	public BuildAction() 
	{
		buildConfig = BuildConfigurationManager.instance();
		projectConfig = new Project();
	}

	public void dispose() 
	{
	}
	
	public static IProject[] extractProjects(Object[] selection) 
	{
		HashSet projects = new HashSet();
			
		for (int i = 0; i < selection.length; i++) 
		{
			if (selection[i] instanceof IResource) 
			{
				projects.add(((IResource)selection[i]).getProject());
			} 
			else if (selection[i] instanceof IAdaptable)
			{
				IAdaptable adaptable = (IAdaptable)selection[i];
				IResource resource = (IResource)adaptable.getAdapter(IResource.class);
				if (resource != null)
				{	
					projects.add(resource.getProject());
				}
			}
		}
		return (IProject[]) projects.toArray(new IProject[projects.size()]);
	}
	
	public void init(IWorkbenchWindow window) 
	{
		this.window = window;
	}
	
	public void selectionChanged(IAction action, ISelection selection) 
	{
		if (selection == null || selection.isEmpty() || (!(selection instanceof IStructuredSelection)))
		{
			selectedProjects = NO_PROJECTS;
		}
		else
		{
			selectedProjects = extractProjects(((IStructuredSelection)selection).toArray());
		}
		action.setEnabled(selectedProjects.length > 0); //&& !ResourcesPlugin.getWorkspace().isAutoBuilding());
	}
	
	public void setConcernSources(HashSet list, IPath workspace) 
	{
		Iterator l=list.iterator();
		workspace=workspace.removeLastSegments(1);
		while(l.hasNext())
		{
			IPath test = (IPath)l.next();
			test=workspace.append(test);
			BuildConfigurationManager.instance().setConcernSources(FileUtils.fixFilename(test.toOSString()));
		}
	}
	
	public void setSources(HashSet list, IPath workspace)
	{
		Iterator l=list.iterator();
		workspace=workspace.removeLastSegments(1);
		while(l.hasNext())
		{
			IPath test = (IPath)l.next();
			test=workspace.append(test);
			projectConfig.addSource(FileUtils.fixFilename(test.toOSString()));
		}
	}
}