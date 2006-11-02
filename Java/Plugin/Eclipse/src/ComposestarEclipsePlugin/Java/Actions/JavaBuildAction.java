package ComposestarEclipsePlugin.Java.Actions;

import ComposestarEclipsePlugin.Core.Actions.BuildAction;
import ComposestarEclipsePlugin.Core.Actions.Sources;
import ComposestarEclipsePlugin.Core.BuildConfiguration.*;
import ComposestarEclipsePlugin.Core.ComposestarEclipsePluginPlugin;
import ComposestarEclipsePlugin.Core.Debug;
import ComposestarEclipsePlugin.Core.Utils.FileUtils;
import ComposestarEclipsePlugin.Core.Utils.Timer;
import ComposestarEclipsePlugin.Java.*;

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

import java.util.HashSet;

public class JavaBuildAction extends BuildAction implements IWorkbenchWindowActionDelegate {

	public JavaBuildAction() 
	{
		language = "Java";
	}
	
	public void run(IAction action) 
	{
		new Thread(new Runnable() 
		{
            public void run() 
            {
                build();
            }	
        }).start();
	}
	
	public void build()
	{
		Timer t = new Timer();
		t.start();
		
		builtOk = true;
		projectConfig = new Project();
		
    	Debug.instance().clear();
    	Debug.instance().Log("------ Composestar build started ------\n");
    	Debug.instance().Log("Preprocessing...");
				
    	projectLocation = selectedProjects[0].getProject().getLocation();
    	buildPath = FileUtils.fixFilename(projectLocation.toOSString() + "/obj/");    	
    	
    	setDependencies();
    	setSources();
        setPaths();

    	//project settings
    	projectConfig.addProperty("language", language);
    	projectConfig.addProperty("name", selectedProjects[0].getName());

    	//load project compose* settings
    	loadDialogSettings(projectLocation.toOSString());

    	if(builtOk) {
    		//set project
    		buildConfig.setProject(projectConfig);
    		String projectPath = selectedProjects[0].getProject().getLocation().toOSString();
    		projectPath += java.io.File.separatorChar;
	
    		//create buildconfig and save to disk
    		buildConfig.saveToXML(FileUtils.fixFilename(projectPath+"BuildConfiguration.xml"));
    		buildConfig.clearConfigManager();			
    	}

    	MasterManager m = MasterManager.getInstance();
    	if(builtOk) {
    		//start compile-proces
    		IDialogSettings settings = ComposestarEclipsePluginPlugin.getDefault().getDialogSettings(projectLocation.toOSString());
    		Debug.instance().Log("Invoking Master...\n");
    		m.run(settings);
    		if(!m.completed){
    			builtOk = false;
    		}
    	}
	
    	if(builtOk) {
    		t.stop();
    		double time_in_seconds = ((double)t.getElapsed())/1000;
    		Debug.instance().Log("---------------- Done -----------------");
    		Debug.instance().Log("");
    		Debug.instance().Log("Composestar build complete in "+time_in_seconds+" seconds");
    		Debug.instance().Log("");
    		Debug.instance().Log("");
    	}
    	else{
    		Debug.instance().Log("---------------- Done -----------------");
    		Debug.instance().Log("");
    		Debug.instance().Log("Composestar build failed.",Debug.MSG_ERROR);
    		Debug.instance().Log("");
    		Debug.instance().Log("");
    	}
    }
	
	public void loadDialogSettings(String location){
		
		//locate project compose*-settings
		ComposestarEclipsePluginPlugin plugin = ComposestarEclipsePluginPlugin.getDefault();
		IDialogSettings settings = plugin.getDialogSettings(location);
		
		if(plugin.dialogSettingsFound)
		{
			
			buildConfig.setApplicationStart(settings.get("mainClass"));
			buildConfig.addSetting("buildDebugLevel",settings.get("buildDebugLevel"));
			buildConfig.setRunDebugLevel(settings.get("runDebugLevel"));
			
			if(settings.get("filterModuleOrder")!=null)
			{
				ModuleSetting filth = new ModuleSetting();
				filth.setName("FILTH");
				filth.addSetting("input",settings.get("filterModuleOrder"));
				buildConfig.addModuleSetting(filth);
			}
			
			if(settings.get("incremental")!=null)
			{
				ModuleSetting incre = new ModuleSetting();
				incre.setName("INCRE");
				incre.addSetting("enabled",settings.get("incremental"));
				buildConfig.addModuleSetting(incre);
			}
			
			if(settings.get("secretMode")!=null)
			{
				ModuleSetting secret = new ModuleSetting();
				secret.setName("SECRET");
				secret.addSetting("mode",settings.get("secretMode"));
				buildConfig.addModuleSetting(secret);
			}
		}
		else
		{
			builtOk = false;
			Debug.instance().Log("The compose* project settings are not set! (See properties page of project)\n",Debug.MSG_ERROR);
		}
	}
	
	/**
	 * Return the IJavaProject corresponding to the project name
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
	
	public void setDependencies()
	{
		IJavaProject javaProject = getJavaProject(selectedProjects[0]);
       	
    	try 
       	{
    		// outputPath
    		outputPath = projectLocation.removeLastSegments(1).append(javaProject.getOutputLocation()).addTrailingSeparator().toPortableString().toString();
    		IClasspathEntry[] classpaths = javaProject.getRawClasspath();
    		    		
    		for(int i=0; i<classpaths.length; i++) 
    		{
    			//dependencies
            	//FIXME: skip jre libraries
    			if(classpaths[i].getEntryKind() == IClasspathEntry.CPE_LIBRARY)
    			{
    				projectConfig.addDependency(FileUtils.fixFilename(classpaths[i].getPath().toOSString()));
    			}
    		}
    	}
    	catch(JavaModelException jme){System.out.println("java model exception "+jme.getMessage());}
	}
	
	public void setPaths()
	{
		BuildConfigurationManager buildConfig = BuildConfigurationManager.instance();
    	buildConfig.setOutputPath(outputPath);
    	
    	projectConfig.addProperty("basePath",FileUtils.fixFilename(projectLocation.toOSString() + "/"));
    	projectConfig.addProperty("outputPath",outputPath);
    	projectConfig.addProperty("buildPath",buildPath);
    	
    	basePath.setPath(FileUtils.fixFilename(projectLocation.toOSString() + "/"));
    	basePath.setName("Base");
    	buildConfig.addPath(basePath);
    	composestarPath.setPath(ComposestarEclipsePluginPlugin.getAbsolutePath("/"));
    	composestarPath.setName("Composestar");
    	buildConfig.addPath(composestarPath);
    	Path embeddedPath = new Path();
    	embeddedPath.setName("EmbeddedSources");
    	embeddedPath.setPath("embedded/");
    	buildConfig.addPath(embeddedPath);
    	Path dummyPath = new Path();
    	dummyPath.setName("Dummy");
    	dummyPath.setPath("dummies/");
    	buildConfig.addPath(dummyPath);
	}
	
	public void setSelectedProjects(IProject[] jp)
	{
		this.selectedProjects = jp;
	}
	
	public void setSources()
	{
		//skip folderlist
		HashSet skiplist = new HashSet();
		skiplist.add(outputPath);
		skiplist.add(buildPath);
		
		//sources from base program
    	Sources source = new Sources(selectedProjects);
    	sources.clear();
    	sources.addAll(source.getSources("java",skiplist));
    	setSources(sources, projectLocation);
    	
    	//concerns
    	concerns.clear();
    	concerns.addAll(source.getSources("cps",skiplist));
    	setConcernSources(concerns, projectLocation);
	}
}
