package ComposestarEclipsePlugin.Core.Actions;

import java.util.HashSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Represents an Action. All Actions should extend this class.
 */
public class Action
{

	/**
	 * The workbench.
	 */
	protected IWorkbenchWindow window;

	/**
	 * Empty project list.
	 */
	protected static final IProject[] NO_PROJECTS = new IProject[0];

	/**
	 * Selected projects. Default zero projects selected.
	 */
	protected IProject[] selectedProjects = NO_PROJECTS;

	/**
	 * Constructor.
	 */
	public Action()
	{

	}

	/**
	 * @see IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose()
	{

	}

	/**
	 * Extracts projects from a given selection. Uses NO_PROJECTS if no projects
	 * are found.
	 */
	public static IProject[] extractProjects(Object[] selection)
	{
		HashSet projects = new HashSet();

		for (int i = 0; i < selection.length; i++)
		{
			if (selection[i] instanceof IResource)
			{
				projects.add(((IResource) selection[i]).getProject());
			}
			else if (selection[i] instanceof IAdaptable)
			{
				IAdaptable adaptable = (IAdaptable) selection[i];
				IResource resource = (IResource) adaptable.getAdapter(IResource.class);
				if (resource != null)
				{
					projects.add(resource.getProject());
				}
			}
		}
		return (IProject[]) projects.toArray(new IProject[projects.size()]);
	}

	/**
	 * @see IWorkbenchWindowActionDelegate#init()
	 */
	public void init(IWorkbenchWindow window)
	{
		this.window = window;
	}

	/**
	 * @see IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
		if (selection == null || selection.isEmpty() || (!(selection instanceof IStructuredSelection)))
		{
			selectedProjects = NO_PROJECTS;
		}
		else
		{
			selectedProjects = extractProjects(((IStructuredSelection) selection).toArray());
		}
	}

	/**
	 * Sets selected projects manually.
	 * <p>
	 * E.g. this is used by TestBuild and TestRun for running action in headless
	 * mode.
	 * 
	 * @see TestBuild
	 * @see TestRun
	 * @param jp list of projects
	 */
	public void setSelectedProjects(IProject[] jp)
	{
		this.selectedProjects = jp;
	}
}
