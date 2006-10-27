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
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class JavaBuildAction extends BuildAction implements IWorkbenchWindowActionDelegate {

	public JavaBuildAction() {
		
	}
	
	public void run(IAction action) {
		
		new Thread(new Runnable() {
            public void run() {
                
            	Timer t = new Timer();
        		t.start();
        		
            	Debug.instance().clear();
            	Debug.instance().Log("------ Composestar build started ------\n");
            	Debug.instance().Log("Preprocessing...");
						
            	builtOk = true;
            	projectConfig = new Project();
            	language = "Java";
		
            	projectLocation = selectedProjects[0].getProject().getLocation();
		
            	//sources from base program
            	Sources source = new Sources(selectedProjects);
            	sources.clear();
            	sources.addAll(source.getSources("java"));
            	setSources(sources, projectLocation);
		
            	//dependencies
            	IJavaProject javaProject = getJavaProject(selectedProjects[0]);
               	try {
            		IClasspathEntry[] classpaths = javaProject.getResolvedClasspath(true);
            		//skip JRE libraries
            		Iterator jres = Arrays.asList(PreferenceConstants.getDefaultJRELibrary()).iterator();
            		ArrayList paths = new ArrayList();
            		while(jres.hasNext()) {
            			IClasspathEntry entry = (IClasspathEntry)jres.next();
            			IClasspathEntry[] jre_libraries = JavaCore.getClasspathContainer(entry.getPath(),javaProject).getClasspathEntries();
            			for(int j=0; j<jre_libraries.length; j++) {
            				paths.add(FileUtils.fixFilename(jre_libraries[j].getPath().toOSString()));
            			}
            		}
            		for(int i=0; i<classpaths.length; i++) {
            			if((classpaths[i].getEntryKind() == IClasspathEntry.CPE_LIBRARY) &&(!paths.contains(FileUtils.fixFilename(classpaths[i].getPath().toOSString())))) {
            				projectConfig.addDependency(FileUtils.fixFilename(classpaths[i].getPath().toOSString()));
            			}
            		}
            		
            		//typesources
                	IJavaElement[] elements = javaProject.getChildren();
                	ICompilationUnit unit;
                	IType[] types;
                	String sourceFile;
                	TypeSource typesource;
                	
                	for(int i=0; i<elements.length; i++)
                	{
                		if(elements[i].getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT)
                		{
                			IJavaElement[] packageFragments = ((IPackageFragmentRoot)elements[i]).getChildren();
                			for(int j=0; j<packageFragments.length; j++)
                        	{
                				IJavaElement[] packageChildren = ((IPackageFragment)packageFragments[j]).getChildren();
                				for(int k=0; k<packageChildren.length; k++)
                            	{
                					//compilation units / sources
                					if(packageChildren[k].getElementType() == IJavaElement.COMPILATION_UNIT)
                					{
                						unit = ((ICompilationUnit)packageChildren[k]);
                						sourceFile = unit.getResource().getLocation().toString();
                						types = unit.getTypes();
                						for(int p=0; p<types.length; p++)
                						{
                							typesource = new TypeSource(types[p].getFullyQualifiedName(),sourceFile);
                							projectConfig.addTypeSource(typesource);
                						}
                					}
                            	}
                			}
                		}
                	}
            	}
            	catch(JavaModelException jme){System.out.println("java model exception "+jme.getMessage());}
		                    		
            	//concerns
            	concerns.clear();
            	concerns.addAll(source.getSources("cps"));
            	setConcernSources(concerns, projectLocation);
		
            	//paths
            	BuildConfigurationManager buildConfig = BuildConfigurationManager.instance();
            	String location = projectLocation.toOSString();
            	buildConfig.setOutputPath(FileUtils.fixFilename(location + "/bin/"));
            	projectConfig.addProperty("basePath",FileUtils.fixFilename(location + "/"));
            	projectConfig.addProperty("outputPath",FileUtils.fixFilename(location + "/bin/"));
            	projectConfig.addProperty("buildPath",FileUtils.fixFilename(location + "/obj/"));
            	basePath.setPath(FileUtils.fixFilename(location + "/"));
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
		
            	//project settings
            	projectConfig.addProperty("language", language);
            	projectConfig.addProperty("name", selectedProjects[0].getName());
		
            	//load project compose* settings
            	loadDialogSettings(location);
		
            	if(builtOk) {
            		//set project
            		buildConfig.setProject(projectConfig);
            		String projectPath = selectedProjects[0].getProject().getLocation().toOSString();
            		projectPath += java.io.File.separatorChar;
			
            		//create buildconfig and save to disk
            		buildConfig.saveToXML(FileUtils.fixFilename(projectPath+"BuildConfiguration.xml"));
            		buildConfig.clearConfigManager();			
            	}
		
            	MasterManager m = new MasterManager();
            	if(builtOk) {
            		//start compile-proces
            		IDialogSettings settings = ComposestarEclipsePluginPlugin.getDefault().getDialogSettings(location);
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
        }).start();
	}
	
	public void loadDialogSettings(String location){
		
		//locate project compose*-settings
		ComposestarEclipsePluginPlugin plugin = ComposestarEclipsePluginPlugin.getDefault();
		IDialogSettings settings = plugin.getDialogSettings(location);
		if(plugin.dialogSettingsFound){
			
			buildConfig.setApplicationStart(settings.get("mainClass"));
			buildConfig.addSetting("buildDebugLevel",settings.get("buildDebugLevel"));
			buildConfig.setRunDebugLevel(settings.get("runDebugLevel"));
			
			if(settings.get("filterModuleOrder")!=null){
				ModuleSetting filth = new ModuleSetting();
				filth.setName("FILTH");
				filth.addSetting("input",settings.get("filterModuleOrder"));
				buildConfig.addModuleSetting(filth);
			}
			
			if(settings.get("incremental")!=null){
				ModuleSetting incre = new ModuleSetting();
				incre.setName("INCRE");
				incre.addSetting("enabled",settings.get("incremental"));
				buildConfig.addModuleSetting(incre);
			}
			
			if(settings.get("secretMode")!=null){
				ModuleSetting secret = new ModuleSetting();
				secret.setName("SECRET");
				secret.addSetting("mode",settings.get("secretMode"));
				buildConfig.addModuleSetting(secret);
			}
			
			//TODO: add case for not properly set main class
			//TODO: find main class automatically
		}
		else{
			builtOk = false;
			Debug.instance().Log("The compose* project settings are not set! (See properties page of project)\n",Debug.MSG_ERROR);
		}
	}
	
	/**
	 * Return the IJavaProject corresponding to the project name
	 */
	protected IJavaProject getJavaProject(IProject p) {
		String projectName = p.getName();
		if (projectName.length() < 1) {
			return null;
		}
		return getJavaModel().getJavaProject(projectName);		
	}
	
	/**
	 * Convenience method to get access to the java model.
	 */
	private IJavaModel getJavaModel() {
		return JavaCore.create(getWorkspaceRoot());
	}

	/**
	 * Convenience method to get the workspace root.
	 */
	private IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}
}
