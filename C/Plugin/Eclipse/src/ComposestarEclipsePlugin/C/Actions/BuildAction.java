package ComposestarEclipsePlugin.C.Actions;

import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import ComposestarEclipsePlugin.C.IComposestarCConstants;
import ComposestarEclipsePlugin.C.MasterManager;
import ComposestarEclipsePlugin.C.BuildConfiguration.BuildConfigurationManager;
import ComposestarEclipsePlugin.C.Dialogs.BuildDialog;
import ComposestarEclipsePlugin.C.Makefile.MakefileCreator;
import ComposestarEclipsePlugin.Core.ComposestarEclipsePluginPlugin;
import ComposestarEclipsePlugin.Core.Debug;
import ComposestarEclipsePlugin.Core.Actions.Sources;
import ComposestarEclipsePlugin.Core.BuildConfiguration.ModuleSetting;
import ComposestarEclipsePlugin.Core.BuildConfiguration.Path;
import ComposestarEclipsePlugin.Core.BuildConfiguration.Project;
import ComposestarEclipsePlugin.Core.BuildConfiguration.Settings;

/**
 * This action should call the BuildConfigurationManager, furthermore a property
 * page should be opened (to select main class of selected project) finally the
 * compose* compiler should be called (call to Master) TODO: add timer to time
 * the building proces. TODO: support for building multiple projects.
 */

public class BuildAction implements IWorkbenchWindowActionDelegate
{
	private IWorkbenchWindow window;

	private static final IProject[] NO_PROJECTS = new IProject[0];

	private IProject[] selectedProjects = NO_PROJECTS;

	private IProject project = null;

	private Settings settings = null;

	private IPath projectLocation = null;

	private IResource[] projectMembers = null;

	private Project projectConfig = null;

	private IProjectDescription projectFile = null;

	private String mainString;

	private String builddlString;

	private String customFilters = "";

	private String rundlString;

	private String language;

	private Path basePath = new Path();

	private Path buildPath = new Path();

	private Path outputPath = new Path();

	private Path composestarPath = new Path();

	private HashSet sources = new HashSet();

	private HashSet concerns = new HashSet();

	/**
	 * The constructor.
	 */
	public BuildAction()
	{}

	public void run(IAction action)
	{
		String[] eclipseInstallation = org.eclipse.core.runtime.Platform.getInstallLocation().getURL().getPath().split(
				"/");// toCharArray(); //toString
		String osName = System.getProperty("os.name");
		String eclipseInstallationOS = "";

		for (int i = 0; i < eclipseInstallation.length; i++)
		{
			eclipseInstallationOS += eclipseInstallation[i] + java.io.File.separatorChar;
		}
		if (osName.equals("Windows NT") || osName.equals("Windows 2000") || osName.equals("Windows CE")
				|| osName.equals("Windows XP") || osName.equals("Windows 95") || osName.equals("Windows 98")
				|| osName.equals("Windows ME"))
		{
			eclipseInstallationOS = eclipseInstallationOS.substring(1);
		}

		BuildDialog dialog = new BuildDialog(window.getShell(), selectedProjects);
		dialog.open();
		if (dialog.getReturnCode() == Window.OK)
		{

			Debug.instance().Log("------ Composestar build started ------\n");

			projectConfig = new Project();
			settings = new Settings();
			Sources source = new Sources(selectedProjects);
			BuildConfigurationManager.instance().clearConfigManager();
			mainString = dialog.getMain();
			builddlString = dialog.getBuilddl();
			language = "C";

			try
			{
				projectFile = selectedProjects[0].getProject().getDescription();
				projectLocation = selectedProjects[0].getProject().getLocation();
			}
			catch (CoreException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (projectLocation != null)
			{
				Debug.instance().Log("project location " + projectLocation.toOSString());
			}
			else
			{
				Debug.instance().Log("Use default location" + (selectedProjects[0]).getFullPath().toOSString());
				projectLocation = (selectedProjects[0]).getFullPath();
			}
			projectConfig.addProperty("name", selectedProjects[0].getProject().getName());
			projectConfig.addProperty("language", language);
			projectConfig.addProperty("basePath", dialog.getBasePath());
			projectConfig.addProperty("outputPath", dialog.getOutputPath());
			projectConfig.addProperty("buildPath", dialog.getBuildPath());

			// BuildConfigurationManager.instance().setRunDebugLevel(rundlString);
			BuildConfigurationManager.instance().setApplicationStart(mainString);
			BuildConfigurationManager.instance().setOutputPath(dialog.getOutputPath());

			// Sources from base program
			sources.addAll(source.getSources(language));
			setSources(sources, projectLocation);

			// Concerns
			concerns.addAll(source.getSources("cps"));
			setConcernSources(concerns, projectLocation);

			// setPaths
			outputPath.setPath(dialog.getOutputPath());
			outputPath.setName("Output");
			BuildConfigurationManager.instance().addPath(outputPath);
			basePath.setPath(dialog.getBasePath());
			basePath.setName("Base");
			BuildConfigurationManager.instance().addPath(basePath);
			buildPath.setPath(dialog.getBuildPath());
			buildPath.setName("Build");
			BuildConfigurationManager.instance().addPath(buildPath);
			composestarPath.setPath(ComposestarEclipsePluginPlugin.getAbsolutePath("/") + "/");
			composestarPath.setName("Composestar");
			BuildConfigurationManager.instance().addPath(composestarPath);

			// Standard Settings information
			rundlString = dialog.getRundl();
			BuildConfigurationManager.instance().setRunDebugLevel(rundlString);
			customFilters = dialog.getCustomFilterString();
			BuildConfigurationManager.instance().addCustomFilter(customFilters);

			ModuleSetting ms = new ModuleSetting();
			BuildConfigurationManager.instance().setBuildDBlevel(dialog.getBuilddl());
			ms.setName("buildDebugLevel");
			ms.addSetting("buildDebugLevel", dialog.getBuilddl());

			BuildConfigurationManager.instance().addModuleSettings(ms);

			ModuleSetting incre = new ModuleSetting();
			incre.setName("INCRE");
			incre.addSetting("enabled", dialog.getIncrementalString());
			incre.addSetting("config", ComposestarEclipsePluginPlugin.getAbsolutePath("/INCREconfig.xml",
					IComposestarCConstants.BUNDLE_ID));
			BuildConfigurationManager.instance().addModuleSettings(incre);

			ModuleSetting ilicit = new ModuleSetting();
			ilicit.setName("ILICIT");
			ilicit.addSetting("verifyAssemblies", dialog.getVerifyAssembliesString());
			BuildConfigurationManager.instance().addModuleSettings(ilicit);

			ModuleSetting secret = new ModuleSetting();
			secret.setName("SECRET");
			secret.addSetting("mode", dialog.getSecretString());
			BuildConfigurationManager.instance().addModuleSettings(secret);

			ModuleSetting filth = new ModuleSetting();
			filth.setName("FILTH");
			filth.addSetting("input", dialog.getIncrementalString());
			BuildConfigurationManager.instance().addModuleSettings(filth);

			// create BuildConfiguration file and save to disk
			BuildConfigurationManager.instance().setProject(projectConfig);
			String projectPath = selectedProjects[0].getProject().getLocation().toOSString();
			projectPath += java.io.File.separatorChar;
			BuildConfigurationManager.instance().saveToXML(projectPath + "BuildConfiguration.xml");
			BuildConfigurationManager.instance().clearConfigManager();

			// create make-file
			MakefileCreator mc = new MakefileCreator(projectPath, projectFile, sources, concerns, customFilters);
			mc.saveToFile();

			// start compile-proces
			MasterManager m = new MasterManager();
			m.setBasePath(dialog.getBasePath());
			m.run();

			if (m.completed)
			{
				Debug.instance().Log("");
				Debug.instance().Log("---------------------- Done ----------------------");
				Debug.instance().Log("");
				Debug.instance().Log("Composestar build succeeded");
				Debug.instance().Log("");
				Debug.instance().Log("");
			}
			else
			{
				Debug.instance().Log("");
				Debug.instance().Log("---------------------- Done ----------------------");
				Debug.instance().Log("");
				Debug.instance().Log("Composestar build failed.", Debug.MSG_ERROR);
				Debug.instance().Log("");
				Debug.instance().Log("");
			}
		}

	}

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
		action.setEnabled(selectedProjects.length > 0); // &&
		// !ResourcesPlugin.getWorkspace().isAutoBuilding());
	}

	protected static IProject[] extractProjects(Object[] selection)
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

	public void dispose()
	{}

	public void init(IWorkbenchWindow window)
	{
		this.window = window;
	}

	private HashSet getSources(String extension)
	{
		String languageTest = null;
		HashSet list = new HashSet();

		if (selectedProjects.length == 1)
		{
			project = selectedProjects[0];
		}
		try
		{
			projectMembers = project.members();
		}
		catch (CoreException ce)
		{
		}

		for (int i = 0; i < projectMembers.length; i++)
		{
			if (projectMembers[i].getType() == IResource.FILE)
			{
				languageTest = (((IFile) projectMembers[i]).getName()).substring(((((IFile) projectMembers[i])
						.getName()).indexOf('.') + 1));
				if (languageTest.equalsIgnoreCase(extension))
				{
					list.add(((IFile) projectMembers[i]).getFullPath());
				}
			}
			if (projectMembers[i].getType() == IResource.FOLDER)
			{
				list.addAll(getAllFilesFromDirectory(projectMembers[i], list, extension));
			}
		}
		return list;
	}

	private void setSources(HashSet list, IPath workspace)
	{
		Iterator l = list.iterator();
		// workspace=workspace.removeLastSegments(1);
		while (l.hasNext())
		{
			IPath test = (IPath) l.next();
			test = workspace.append(test.removeFirstSegments(1));
			projectConfig.addSource(test.toOSString());
		}
	}

	private void setConcernSources(HashSet list, IPath workspace)
	{
		Iterator l = list.iterator();
		// workspace=workspace.removeLastSegments(1);
		while (l.hasNext())
		{
			IPath test = (IPath) l.next();
			test = workspace.append(test.removeFirstSegments(1));
			BuildConfigurationManager.instance().setConcernSources(test.toOSString());
		}
	}

	private HashSet getAllFilesFromDirectory(IResource dir, HashSet list, String extension)
	{
		String languageTest = null;
		if (dir.getType() == IResource.FOLDER)
		{
			IResource[] children = null;
			try
			{
				children = ((IFolder) dir).members();
			}
			catch (CoreException e)
			{
				Debug.instance().Log("Exception:" + dir.getName());
				e.printStackTrace();
			}
			for (int i = 0; i < children.length; i++)
			{
				if (children[i].getType() == IResource.FILE)
				{
					languageTest = (((IFile) children[i]).getName()).substring(((((IFile) children[i]).getName())
							.lastIndexOf('.') + 1));

					if ((languageTest).equalsIgnoreCase(extension))
					{
						list.add(((IFile) children[i]).getFullPath());// toOSString
					}
				}
				else if (children[i].getType() == IResource.FOLDER)
				{
					list.addAll(getAllFilesFromDirectory(children[i], list, extension));
				}
			}
		}
		return list;
	}
}
