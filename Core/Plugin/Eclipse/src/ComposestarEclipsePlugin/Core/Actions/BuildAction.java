package ComposestarEclipsePlugin.Core.Actions;

import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

import ComposestarEclipsePlugin.Core.ComposestarEclipsePluginPlugin;
import ComposestarEclipsePlugin.Core.Debug;
import ComposestarEclipsePlugin.Core.IComposestarConstants;
import ComposestarEclipsePlugin.Core.BuildConfiguration.BuildConfigurationManager;
import ComposestarEclipsePlugin.Core.BuildConfiguration.Path;
import ComposestarEclipsePlugin.Core.BuildConfiguration.Project;
import ComposestarEclipsePlugin.Core.Utils.FileUtils;

/**
 * Each language should extends this class. (e.g. JavaBuildAction ,
 * CBuildAction)
 */
public class BuildAction extends Action
{

	/**
	 * Used to add information to the build configuration file.
	 */
	protected BuildConfigurationManager buildConfig;

	/**
	 * The project to be built.
	 */
	protected IProject project;

	/**
	 * The location of the project.
	 */
	protected IPath projectLocation;

	/**
	 * The concerns found in the project. The HashSet contains filenames
	 * (Strings).
	 */
	protected HashSet<IPath> concerns = new HashSet<IPath>();

	/**
	 * The sources found in the project. The HashSet contains filenames
	 * (Strings).
	 */
	protected HashSet<IPath> sources = new HashSet<IPath>();

	/**
	 * The project to be built. Contains only information that is needed in the
	 * build configuration file.
	 */
	protected Project projectConfig;

	/**
	 * Build path. ( obj/ )
	 */
	protected String buildPath = "";

	/**
	 * Output path. Location of the compiled sources.
	 */
	protected String outputPath = "";

	/**
	 * If false, some error occured while building a project. Public access
	 * needed for headless mode (see TestBuild)
	 */
	public boolean builtOk = false;

	/**
	 * The constructor.
	 */
	public BuildAction()
	{
		buildConfig = BuildConfigurationManager.instance();
		projectConfig = new Project();
	}

	/**
	 * Adds the concernsources to the build configuration file.
	 * 
	 * @param list - a list of concernsources
	 * @param projectBase - base location of project
	 */
	public void setConcernSources(HashSet<IPath> list, IPath projectBase)
	{
		Iterator<IPath> l = list.iterator();
		projectBase = projectBase.removeLastSegments(1);
		while (l.hasNext())
		{
			IPath path = l.next();
			path = projectBase.append(path);
			BuildConfigurationManager.instance().setConcernSources(FileUtils.fixFilename(path.toOSString()));
		}
	}

	/**
	 * Adds the sources to the build configuration file.
	 * 
	 * @param list - a list of sources
	 * @param projectBase - base location of project
	 */
	public void setSources(HashSet<IPath> list, IPath projectBase)
	{
		Iterator<IPath> l = list.iterator();
		projectBase = projectBase.removeLastSegments(1);
		while (l.hasNext())
		{
			IPath path = l.next();
			path = projectBase.append(path);
			projectConfig.addSource(FileUtils.fixFilename(path.toOSString()));
		}
	}

	/**
	 * Adds the paths to the build configuration file.
	 */
	public void setPaths()
	{
		if (selectedProjects == NO_PROJECTS)
		{
			// no projects selected
			Debug.instance().Log("No projects selected!", IComposestarConstants.MSG_ERROR);
		}

		// buildPath
		buildPath = FileUtils.fixFilename(projectLocation.toOSString() + "/obj/");

		BuildConfigurationManager buildConfig = BuildConfigurationManager.instance();
		buildConfig.setOutputPath(outputPath);

		projectConfig.addProperty("basePath", FileUtils.fixFilename(projectLocation.toOSString() + "/"));
		
		// more paths
		Path p = new Path();
		p.setName("Base");
		p.setPath(FileUtils.fixFilename(projectLocation.toOSString() + "/"));
		buildConfig.addPath(p);

		p = new Path();
		p.setName("Composestar");
		p.setPath(ComposestarEclipsePluginPlugin.getAbsolutePath("/")+"/");
		buildConfig.addPath(p);

		p = new Path();
		p.setName("EmbeddedSources");
		p.setPath("embedded/");
		buildConfig.addPath(p);

		p = new Path();
		p.setName("Dummy");
		p.setPath("dummies/");
		buildConfig.addPath(p);
	}
}
