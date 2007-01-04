package ComposestarEclipsePlugin.C.Actions;

import java.util.HashSet;
import java.util.Iterator;

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

import ComposestarEclipsePlugin.C.BuildConfiguration.BuildConfigurationManager;
import ComposestarEclipsePlugin.C.Dialogs.SettingsDialog;
import ComposestarEclipsePlugin.Core.Debug;
import ComposestarEclipsePlugin.Core.Actions.Sources;
import ComposestarEclipsePlugin.Core.BuildConfiguration.ModuleSetting;
import ComposestarEclipsePlugin.Core.BuildConfiguration.Path;
import ComposestarEclipsePlugin.Core.BuildConfiguration.Project;
import ComposestarEclipsePlugin.Core.BuildConfiguration.Settings;

public class SettingsAction implements IWorkbenchWindowActionDelegate
{
	private IWorkbenchWindow window;

	private static final IProject[] NO_PROJECTS = new IProject[0];

	private IProject[] selectedProjects = NO_PROJECTS;

	private Settings settings = null;

	private IPath projectLocation = null;

	private Project projectConfig = null;

	private IProjectDescription projectFile = null;

	private String builddlString;

	private String rundlString;

	private String mainString = "";

	private String language = "";

	private Path basePath = new Path();

	private Path buildPath = new Path();

	private Path outputPath = new Path();

	private HashSet sources = new HashSet();

	private HashSet concerns = new HashSet();

	/**
	 * The constructor.
	 */
	public SettingsAction()
	{

	}

	public void run(IAction action)
	{
		//TODO: don't save the buildconfig.xml it's done by IDialogSettings
		SettingsDialog dialog = new SettingsDialog(window.getShell(), selectedProjects);
		dialog.open();
		if (dialog.getReturnCode() == Window.OK)
		{
			BuildConfigurationManager.instance().clearConfigManager();

			rundlString = dialog.getRundl();
			BuildConfigurationManager.instance().setRunDebugLevel(rundlString);

			/**
			 * TODO: buildDL is a setting, maybe not a module setting, however
			 * xml-writer does not support this yet
			 */

			projectConfig = new Project();
			settings = new Settings();
			Sources source = new Sources(selectedProjects);
			mainString = dialog.getMainString();
			builddlString = dialog.getBuilddl();
			language = dialog.getLanguage();

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
				Debug.instance().Log(projectLocation.toOSString());
			}
			else
			{
				Debug.instance().Log("Use default location" + (selectedProjects[0]).getFullPath().toOSString());
				projectLocation = (selectedProjects[0]).getFullPath();
			}
			projectConfig.addProperty("language", language);
			projectConfig.addProperty("basePath", dialog.getBaseString());
			projectConfig.addProperty("outputPath", dialog.getOutputString());
			projectConfig.addProperty("buildPath", dialog.getBuildString());

			// BuildConfigurationManager.instance().setRunDebugLevel(rundlString);
			BuildConfigurationManager.instance().setApplicationStart(mainString);
			BuildConfigurationManager.instance().setOutputPath(dialog.getOutputString());

			// Sources from base program
			sources.addAll(source.getSources(language));
			setSources(sources, projectLocation);

			// Concerns
			concerns.addAll(source.getSources(".cps"));
			setConcernSources(concerns, projectLocation);

			// setPaths
			outputPath.setPath(dialog.getOutputString());
			outputPath.setName("outputPath");
			BuildConfigurationManager.instance().addPath(outputPath);
			basePath.setPath(dialog.getBaseString());
			basePath.setName("basePath");
			BuildConfigurationManager.instance().addPath(basePath);
			buildPath.setPath(dialog.getBuildString());
			buildPath.setName("buildPath");
			BuildConfigurationManager.instance().addPath(buildPath);

			// Standard Settings information
			rundlString = dialog.getRundl();
			BuildConfigurationManager.instance().setRunDebugLevel(rundlString);

			ModuleSetting ms = new ModuleSetting();
			ms.addSetting("buildDebugLevel", dialog.getBuilddl());
			BuildConfigurationManager.instance().addModuleSettings(ms);
			BuildConfigurationManager.instance().setBuildDBlevel(dialog.getBuilddl());

			ModuleSetting incre = new ModuleSetting();
			incre.setName("INCRE");
			incre.addSetting("enabled", dialog.getIncrementalString());
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
			filth.addSetting("input", dialog.getFilterModuleOrderString());
			BuildConfigurationManager.instance().addModuleSettings(filth);

			// create BuildConfiguration file and save to disk
			BuildConfigurationManager.instance().setProject(projectConfig);
			String projectPath = selectedProjects[0].getProject().getLocation().toOSString();
			projectPath += java.io.File.separatorChar;
			//BuildConfigurationManager.instance().saveToXML(projectPath + "BuildConfiguration.xml");
			BuildConfigurationManager.instance().clearConfigManager();
		}
	}

	private void setSources(HashSet list, IPath workspace)
	{
		Iterator l = list.iterator();
		workspace = workspace.removeLastSegments(1);
		while (l.hasNext())
		{
			IPath test = (IPath) l.next();
			test = workspace.append(test);
			projectConfig.addSource(test.toOSString());
		}
	}

	private void setConcernSources(HashSet list, IPath workspace)
	{
		Iterator l = list.iterator();
		workspace = workspace.removeLastSegments(1);
		while (l.hasNext())
		{
			IPath test = (IPath) l.next();
			test = workspace.append(test);
			BuildConfigurationManager.instance().setConcernSources(test.toOSString());
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

}
