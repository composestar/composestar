package ComposestarEclipsePlugin.Java.Actions;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import ComposestarEclipsePlugin.Core.ComposestarEclipsePluginPlugin;
import ComposestarEclipsePlugin.Core.Debug;
import ComposestarEclipsePlugin.Core.IComposestarConstants;
import ComposestarEclipsePlugin.Core.Actions.Action;
import ComposestarEclipsePlugin.Core.Utils.CommandLineExecutor;
import ComposestarEclipsePlugin.Core.Utils.FileUtils;
import ComposestarEclipsePlugin.Java.IComposestarJavaConstants;

/**
 * Action for running a Java project with Compose*
 */
public class JavaRunAction extends Action implements IWorkbenchWindowActionDelegate
{
	/**
	 * The java command
	 */
	private String command = "";

	/**
	 * The project to run.
	 */
	private IProject project;

	/**
	 * The location of the project.
	 */
	private IPath projectLocation;

	/**
	 * The Main class of the project.
	 */
	private String mainClass;

	/**
	 * Main class arguments.
	 */
	private String arguments = "";

	/**
	 * Classpath of project (this includes dependencies). Contains Strings.
	 */
	private HashSet classpath = new HashSet();

	/**
	 * If true, run proces is finished properly
	 */
	public boolean completed = false;

	/**
	 * Constructor
	 */
	public JavaRunAction()
	{

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

		classpath = new HashSet();
		command = "";
		arguments = "";

		project = selectedProjects[0];
		projectLocation = project.getProject().getLocation();

		// main class can be retrieved from dialog settings.
		loadDialogSettings();

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

		// get composestar binaries
		getCStarBinaries();
	}

	/**
	 * Creates the command.
	 */
	private void createCommand()
	{
		command += "java ";
		command += "-cp ";

		Iterator cpIt = classpath.iterator();
		while (cpIt.hasNext())
		{
			command += '"' + ((String) cpIt.next()) + '"';
			if (cpIt.hasNext())
			{
				command += ";";
			}
		}

		command += " " + mainClass;
		command += " " + arguments;
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
			int result = cmdExec.exec(/* "call " + */command, new File(project.getLocation().toOSString()));

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

	private void getCStarBinaries()
	{
		// FIXME: why is this necessary? should not be hardcoded...
		for (String lib : IComposestarJavaConstants.RUNTIME_LIBS)
		{
			String rsolvedlib = FileUtils.fixFilename(ComposestarEclipsePluginPlugin
					.getAbsolutePath(IComposestarConstants.BIN_DIR + lib));
			classpath.add(rsolvedlib);
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
			String outputPath = projectLocation.removeLastSegments(1).append(javaProject.getOutputLocation())
					.addTrailingSeparator().toPortableString().toString();

			classpath.add(outputPath);

			IClasspathEntry[] classpaths = javaProject.getRawClasspath();

			for (int i = 0; i < classpaths.length; i++)
			{
				// dependencies
				if (classpaths[i].getEntryKind() == IClasspathEntry.CPE_LIBRARY)
				{
					classpath.add(FileUtils.fixFilename(classpaths[i].getPath().toOSString()));
				}
				else if (classpaths[i].getEntryKind() == IClasspathEntry.CPE_VARIABLE)
				{
					IClasspathEntry entry = JavaCore.getResolvedClasspathEntry(classpaths[i]);
					if (entry != null)
					{
						classpath.add(FileUtils.fixFilename(entry.getPath().toOSString()));
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

	/**
	 * Loads the dialog settings. Also sets the main class.
	 */
	private void loadDialogSettings()
	{
		String location = project.getLocation().toOSString();

		ComposestarEclipsePluginPlugin plugin = ComposestarEclipsePluginPlugin.getDefault();
		IDialogSettings settings = plugin.getDialogSettings(location);

		if (plugin.dialogSettingsFound)
		{
			if (settings.get("mainClass") == null)
			{
				// mainClass not set!
				Debug.instance().Log("Main class not set.", IComposestarConstants.MSG_ERROR);
			}
			else
			{
				mainClass = settings.get("mainClass");
			}
		}
		else
		{
			// composestar settings not set!
			Debug.instance().Log("Compose* settings not set.", IComposestarConstants.MSG_ERROR);
		}
	}
}
