/*
 * Created on 14-nov-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package composestarEclipsePlugin;

import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import composestarEclipsePlugin.actions.Sources;
import composestarEclipsePlugin.buildConfiguration.BuildConfigurationManager;
import composestarEclipsePlugin.buildConfiguration.ModuleSetting;
import composestarEclipsePlugin.buildConfiguration.Path;
import composestarEclipsePlugin.buildConfiguration.Project;
import composestarEclipsePlugin.buildConfiguration.Settings;
import composestarEclipsePlugin.makefile.MakefileCreator;

/**
 * @author Johan
 */
public class PlatformRunnable implements IPlatformRunnable
{

	private IProject project = null;

	private IResource[] projectMembers = null;

	private Project projectConfig = new Project();

	private IProjectDescription projectFile = null;

	private String customFilters = "";

	private String rundlString;

	private IProject[] selectedProjects;

	private Path basePath = new Path();

	private Path buildPath = new Path();

	private Path outputPath = new Path();

	private Path composestarPath = new Path();

	public Object run(Object args) throws Exception
	{
		String[] cmdArgs = (String[]) args;
		HashSet sources = new HashSet();
		HashSet concerns = new HashSet();
		String[] eclipseInstallation = org.eclipse.core.runtime.Platform.getInstallLocation().getURL().getPath().split(
				"/");// toCharArray(); //toString
		String osName = System.getProperty("os.name");
		String eclipseInstallationOS = "";

		Debug.instance().DebugModeOff();

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
		Debug.instance().Log("------ Testing Compose*/C ---------");
		if (cmdArgs.length == 0)
		{
			Debug.instance().Log("No project specified");
		}
		else
		{
			// ICProject cp = CCore.create();
			IJavaProject jp = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getJavaProject("" + cmdArgs[0]);
			selectedProjects = new IProject[1];
			IProject project = jp.getProject();
			selectedProjects[0] = project;

			NullProgressMonitor npm = new NullProgressMonitor();
			if (!project.exists())
			{
				// create it first
				project.create(npm);
			}
			project.open(npm);
			project.refreshLocal(project.DEPTH_INFINITE, null);

			Settings settings = new Settings();
			Sources source = new Sources(selectedProjects);
			BuildConfigurationManager.instance().clearConfigManager();
			IPath projectLocation = null;
			try
			{
				projectFile = project.getProject().getDescription();
				projectLocation = project.getProject().getLocation();
			}
			catch (CoreException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Debug.instance().Log("ProjectLocation:" + projectLocation.toOSString() + " of project " + cmdArgs[0]);

			/** Settings for BuildConfiguration * */
			projectConfig.addProperty("language", "C");
			projectConfig.addProperty("basePath", projectLocation.toOSString() + java.io.File.separatorChar);
			projectConfig.addProperty("outputPath", projectLocation.toOSString() + java.io.File.separatorChar + "bin"
					+ java.io.File.separatorChar);
			projectConfig.addProperty("buildPath", projectLocation.toOSString() + java.io.File.separatorChar + "bin"
					+ java.io.File.separatorChar);

			// BuildConfigurationManager.instance().setRunDebugLevel(rundlString);
			BuildConfigurationManager.instance().setOutputPath(
					projectLocation.toOSString() + java.io.File.separatorChar + "bin" + java.io.File.separatorChar);

			// Sources from base program
			sources.addAll(source.getSources("C"));
			setSources(sources, projectLocation);

			// Concerns
			concerns.addAll(source.getSources("cps"));
			setConcernSources(concerns, projectLocation);

			// setPaths
			outputPath.setPath(projectLocation.toOSString() + java.io.File.separatorChar + "bin"
					+ java.io.File.separatorChar);
			outputPath.setName("Output");
			BuildConfigurationManager.instance().addPath(outputPath);
			basePath.setPath(projectLocation.toOSString() + java.io.File.separatorChar);
			basePath.setName("Base");
			BuildConfigurationManager.instance().addPath(basePath);
			buildPath.setPath(projectLocation.toOSString() + java.io.File.separatorChar + "bin"
					+ java.io.File.separatorChar);
			buildPath.setName("Build");
			BuildConfigurationManager.instance().addPath(buildPath);
			composestarPath.setPath(eclipseInstallationOS + "plugins" + java.io.File.separatorChar
					+ "ComposestarEclipsePlugin" + java.io.File.separatorChar + java.io.File.separatorChar);
			composestarPath.setName("Composestar");
			BuildConfigurationManager.instance().addPath(composestarPath);

			// Standard Settings information
			rundlString = "1";
			BuildConfigurationManager.instance().setRunDebugLevel(rundlString);

			// TODO: how to set the custom filters
			if (cmdArgs.length > 2)
			{
				if (cmdArgs[1].equals("-customfilter"))
				{
					for (int i = 2; i < cmdArgs.length; i++)
					{
						customFilters += cmdArgs[i] + ";\n";
					}
					BuildConfigurationManager.instance().addCustomFilter(customFilters);
				}
			}
			ModuleSetting ms = new ModuleSetting();
			BuildConfigurationManager.instance().setBuildDBlevel("1");
			ms.setName("buildDebugLevel");
			ms.addSetting("buildDebugLevel", "1");

			BuildConfigurationManager.instance().addModuleSettings(ms);

			ModuleSetting incre = new ModuleSetting();
			incre.setName("INCRE");
			incre.addSetting("enabled", "False");
			BuildConfigurationManager.instance().addModuleSettings(incre);

			ModuleSetting ilicit = new ModuleSetting();
			ilicit.setName("ILICIT");
			ilicit.addSetting("verifyAssemblies", "False");
			BuildConfigurationManager.instance().addModuleSettings(ilicit);

			ModuleSetting secret = new ModuleSetting();
			secret.setName("SECRET");
			secret.addSetting("mode", "NotSet");
			BuildConfigurationManager.instance().addModuleSettings(secret);

			ModuleSetting filth = new ModuleSetting();
			filth.setName("FILTH");
			filth.addSetting("input", "");
			BuildConfigurationManager.instance().addModuleSettings(filth);

			Debug.instance().Log("ProjectLocation:" + projectLocation.toOSString() + " of project " + cmdArgs[0]);
			BuildConfigurationManager.instance().setProject(projectConfig);

			String projectPath = selectedProjects[0].getProject().getLocation().toOSString();
			projectPath += java.io.File.separatorChar;
			BuildConfigurationManager.instance().saveToXML(projectPath + "BuildConfiguration.xml");
			BuildConfigurationManager.instance().clearConfigManager();
			Debug.instance().Log("Build config created");

			// create make-file
			MakefileCreator mc = new MakefileCreator(projectPath, projectFile, sources, concerns, customFilters);
			mc.saveToFile();
			System.out.println("Makefile created");
			// start compile-proces
			MasterManager m = new MasterManager();
			m.setBasePath(projectLocation.toOSString());
			m.run();
			Debug.instance().Log("Compose*C runned");

		}
		return EXIT_OK;
	}

	private HashSet getSources(String extension)
	{
		String languageTest = null;
		HashSet list = new HashSet();
		Debug.instance().Log("getConcernSources");
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
		Debug.instance().Log("SetSources");
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
		Debug.instance().Log("SetConcernSources");

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
		Debug.instance().Log("GetAllFilesFromDirectory" + dir.getName());

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
