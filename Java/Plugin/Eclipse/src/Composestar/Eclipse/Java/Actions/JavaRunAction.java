package Composestar.Eclipse.Java.Actions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import Composestar.Eclipse.Core.Debug;
import Composestar.Eclipse.Core.IComposestarConstants;
import Composestar.Eclipse.Core.Actions.Action;
import Composestar.Eclipse.Core.Utils.CommandLineExecutor;
import Composestar.Eclipse.Core.Utils.FileUtils;

/**
 * Action for running a Java project with Compose*
 */
public class JavaRunAction extends Action implements IWorkbenchWindowActionDelegate
{
	/**
	 * The project to run.
	 */
	private IProject project;

	/**
	 * The Main class of the project.
	 */
	private String mainClass;

	/**
	 * Main class arguments.
	 */
	private List<String> cmdline;

	/**
	 * Classpath of project (this includes dependencies). Contains Strings.
	 */
	private HashSet<String> classpath = new HashSet<String>();

	/**
	 * If true, run proces is finished properly
	 */
	public boolean completed = false;

	/**
	 * Constructor
	 */
	public JavaRunAction()
	{
		cmdline = new ArrayList<String>();
	}

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
				perform();
			}
		}).start();
	}

	/**
	 * The action.
	 */
	public void perform()
	{
		Debug.instance().clear();
		Debug.instance().Log("------ Composestar run started ------\n");
		Debug.instance().Log("Preprocessing...");

		if (selectedProjects[0] == null)
		{
			Debug.instance().Log("No project selected", IComposestarConstants.MSG_ERROR);
			return;
		}

		classpath = new HashSet<String>();
		cmdline.clear();

		project = selectedProjects[0];

		IScopeContext projectScope = new ProjectScope(project);
		IEclipsePreferences settings = projectScope.getNode(IComposestarConstants.BUNDLE_ID);

		mainClass = settings.get("mainclass", null);
		if (mainClass == null || mainClass.trim().length() == 0)
		{
			Debug.instance().Log("No mainclass configured for this project", Debug.MSG_ERROR);
			return;
		}

		// create classpath
		createClassPath();

		// create command
		createCommand();

		Debug.instance().Log("Starting program...");

		// execute command
		executeCommand();
	}

	/**
	 * Creates the classpath.
	 */
	private void createClassPath()
	{
		// get dependencies
		getDependencies();
	}

	/**
	 * Creates the command.
	 */
	private void createCommand()
	{
		cmdline.add("java");
		cmdline.add("-cp");

		StringBuffer sb = new StringBuffer();
		for (String cp : classpath)
		{
			if (sb.length() > 0)
			{
				sb.append(";");
			}
			sb.append(cp);
		}
		cmdline.add(sb.toString());
		cmdline.add(mainClass);
		// command += " " + arguments;
	}

	/**
	 * Executes the command.
	 */
	private void executeCommand()
	{
		// CommandLineExecutor
		try
		{
			CommandLineExecutor cmdExec = new CommandLineExecutor();
			int result = cmdExec.exec(cmdline.toArray(new String[cmdline.size()]), project.getLocation().toFile());

			if (result == 0)
			{
				// System.out.print(cmdExec.outputNormal());
				completed = true;
			}
			else
			{
				// System.out.print(cmdExec.outputNormal());
				// System.err.print(cmdExec.outputError());
				Debug.instance().Log("Program run failure reported by process. Exit code is " + result,
						IComposestarConstants.MSG_ERROR);
			}
		}
		catch (Exception e)
		{
			Debug.instance().Log("Program run failure reported: " + e.getCause().getMessage(),
					IComposestarConstants.MSG_ERROR);
			completed = false;
		}
	}

	/**
	 * Get the dependencies of a project.
	 * <p>
	 * The dependencies are retrieved by using IJavaProject.getRawClasspath().
	 * 
	 * @see IJavaProject#getRawClasspath()
	 */
	private void getDependencies()
	{
		IJavaProject javaProject = getJavaProject(project);

		try
		{
			// outputPath
			String outputPath = project.getLocation().removeLastSegments(1).append(javaProject.getOutputLocation())
					.addTrailingSeparator().toPortableString().toString();

			classpath.add(outputPath);

			IClasspathEntry[] classpaths = javaProject.getRawClasspath();

			for (IClasspathEntry element : classpaths)
			{
				// dependencies
				if (element.getEntryKind() == IClasspathEntry.CPE_LIBRARY)
				{
					classpath.add(FileUtils.fixFilename(element.getPath().toOSString()));
				}
				else if (element.getEntryKind() == IClasspathEntry.CPE_VARIABLE)
				{
					IClasspathEntry entry = JavaCore.getResolvedClasspathEntry(element);
					if (entry != null)
					{
						classpath.add(FileUtils.fixFilename(entry.getPath().toOSString()));
					}
				}
				else if (element.getEntryKind() == IClasspathEntry.CPE_CONTAINER)
				{
					IClasspathContainer con = JavaCore.getClasspathContainer(element.getPath(), javaProject);
					if (con.getKind() != IClasspathContainer.K_DEFAULT_SYSTEM)
					{
						IClasspathEntry[] concps = con.getClasspathEntries();
						for (IClasspathEntry cp : concps)
						{
							classpath.add(FileUtils.fixFilename(cp.getPath().toOSString()));
						}
					}
				}
			}
		}
		catch (JavaModelException jme)
		{
			Debug.instance().Log("Java Model Exception: " + jme.getMessage(), IComposestarConstants.MSG_ERROR);
		}
	}

	/**
	 * Returns the IJavaProject corresponding to the project name
	 */
	private IJavaProject getJavaProject(IProject p)
	{
		String projectName = p.getName();
		if (projectName.length() < 1)
		{
			return null;
		}
		return getJavaModel().getJavaProject(projectName);
	}

	/**
	 * Convenience method to get access to the java model.
	 */
	private IJavaModel getJavaModel()
	{
		return JavaCore.create(getWorkspaceRoot());
	}

	/**
	 * Convenience method to get the workspace root.
	 */
	private IWorkspaceRoot getWorkspaceRoot()
	{
		return ResourcesPlugin.getWorkspace().getRoot();
	}
}
