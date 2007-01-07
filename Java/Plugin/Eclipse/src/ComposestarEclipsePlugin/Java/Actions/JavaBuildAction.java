package ComposestarEclipsePlugin.Java.Actions;

import java.io.File;
import java.util.HashSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
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
import ComposestarEclipsePlugin.Core.Actions.BuildAction;
import ComposestarEclipsePlugin.Core.Actions.Sources;
import ComposestarEclipsePlugin.Core.BuildConfiguration.ModuleSetting;
import ComposestarEclipsePlugin.Core.BuildConfiguration.Project;
import ComposestarEclipsePlugin.Core.Utils.FileUtils;
import ComposestarEclipsePlugin.Core.Utils.Timer;
import ComposestarEclipsePlugin.Java.IComposestarJavaConstants;
import ComposestarEclipsePlugin.Java.MasterManager;

/**
 * Action for building a Java project with Compose*.
 */
public class JavaBuildAction extends BuildAction implements IWorkbenchWindowActionDelegate
{

	/**
	 * The language (used in BuildConfiguration file)
	 */
	private final String language = "Java";

	/**
	 * Constructor.
	 */
	public JavaBuildAction()
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
		Debug.instance().Log("Preprocessing...");

		builtOk = true;
		projectConfig = new Project();
		projectLocation = selectedProjects[0].getProject().getLocation();

		setDependencies();
		setPaths();
		setSources();

		// project settings
		projectConfig.addProperty("language", language);
		projectConfig.addProperty("name", selectedProjects[0].getName());

		// make sure ComposestarRuntimeInterpreter.jar is properly referenced
		for (int i = 0; i < projectConfig.getDependencies().size(); i++)
		{
			String dep = (String) projectConfig.getDependencies().get(i);
			if (dep.toLowerCase().endsWith("composestarruntimeinterpreter.jar"))
			{
				File runtime = new File(dep);
				if (!runtime.exists())
				{
					dep = ComposestarEclipsePluginPlugin.getAbsolutePath("/binaries/ComposestarRuntimeInterpreter.jar");
					projectConfig.getDependencies().set(i, dep);
				}
				break;
			}
		}

		// load project compose* settings
		loadDialogSettings(projectLocation.toOSString());

		if (builtOk)
		{
			// set project
			buildConfig.setProject(projectConfig);
			String projectPath = selectedProjects[0].getProject().getLocation().toOSString();
			projectPath += java.io.File.separatorChar;

			buildConfig.setPlatformConfigFile(ComposestarEclipsePluginPlugin.getAbsolutePath(
					"/PlatformConfigurations.xml", IComposestarJavaConstants.BUNDLE_ID));

			// create buildconfig and save to disk
			buildConfig.saveToXML(FileUtils.fixFilename(projectPath + "BuildConfiguration.xml"));

			// clean
			buildConfig.clearConfigManager();
		}

		MasterManager m = MasterManager.getInstance();
		if (builtOk)
		{
			// start compile-proces
			IDialogSettings settings = ComposestarEclipsePluginPlugin.getDefault().getDialogSettings(
					projectLocation.toOSString());
			Debug.instance().Log("Invoking Master...\n");

			m.run(settings);
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

	/**
	 * Loads the dialog settings. Prints a debug message if the dialog settings
	 * cannot be found.
	 * 
	 * @param location - absolute directory path.
	 */
	public void loadDialogSettings(String location)
	{

		// locate project compose*-settings
		ComposestarEclipsePluginPlugin plugin = ComposestarEclipsePluginPlugin.getDefault();
		IDialogSettings settings = plugin.getDialogSettings(location);

		if (plugin.dialogSettingsFound)
		{
			buildConfig.setApplicationStart(settings.get("mainClass"));
			buildConfig.addSetting("buildDebugLevel", settings.get("buildDebugLevel"));
			buildConfig.setRunDebugLevel(settings.get("runDebugLevel"));

			if (settings.get("filterModuleOrder") != null)
			{
				ModuleSetting filth = new ModuleSetting();
				filth.setName("FILTH");
				filth.addSetting("input", settings.get("filterModuleOrder"));
				buildConfig.addModuleSetting(filth);
			}

			if (settings.get("incremental") != null)
			{
				ModuleSetting incre = new ModuleSetting();
				incre.setName("INCRE");
				incre.addSetting("enabled", settings.get("incremental"));
				incre.addSetting("config", ComposestarEclipsePluginPlugin.getAbsolutePath("/INCREconfig.xml",
						IComposestarJavaConstants.BUNDLE_ID));
				buildConfig.addModuleSetting(incre);
			}

			if (settings.get("secretMode") != null)
			{
				ModuleSetting secret = new ModuleSetting();
				secret.setName("SECRET");
				secret.addSetting("mode", settings.get("secretMode"));
				buildConfig.addModuleSetting(secret);
			}
		}
		else
		{
			builtOk = false;
			Debug.instance().Log("The compose* project settings are not set! (See properties page of project)\n",
					IComposestarConstants.MSG_ERROR);
		}
	}

	/**
	 * Returns the IJavaProject corresponding to the project name
	 */
	protected IJavaProject getJavaProject(IProject p)
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
	 * Adds the dependencies to a project configuration.
	 * <p>
	 * The dependencies are retrieved by using IJavaProject.getRawClasspath().
	 * <p>
	 * This method also sets the outputPath.
	 * 
	 * @see IJavaProject#getRawClasspath()
	 */
	public void setDependencies()
	{
		IJavaProject javaProject = getJavaProject(selectedProjects[0]);

		try
		{
			// outputPath
			outputPath = projectLocation.removeLastSegments(1).append(javaProject.getOutputLocation())
					.addTrailingSeparator().toPortableString().toString();

			IClasspathEntry[] classpaths = javaProject.getRawClasspath();

			for (int i = 0; i < classpaths.length; i++)
			{
				// dependencies
				if (classpaths[i].getEntryKind() == IClasspathEntry.CPE_LIBRARY)
				{
					projectConfig.addDependency(FileUtils.fixFilename(classpaths[i].getPath().toOSString()));
				}
			}
		}
		catch (JavaModelException jme)
		{
			Debug.instance().Log("Java Model Exception: " + jme.getMessage(), IComposestarConstants.MSG_ERROR);
		}
	}

	/**
	 * Adds the sources to a project configuration.
	 * <p>
	 * The implementation uses the class Sources to find the sources.
	 * 
	 * @see Sources
	 */
	public void setSources()
	{
		// skip folderlist
		HashSet skiplist = new HashSet();
		skiplist.add(outputPath);
		skiplist.add(buildPath);

		// sources from base program
		Sources source = new Sources(selectedProjects);
		sources.clear();
		sources.addAll(source.getSources("java", skiplist));
		setSources(sources, projectLocation);

		// concerns
		concerns.clear();
		concerns.addAll(source.getSources("cps", skiplist));
		setConcernSources(concerns, projectLocation);
	}
}
