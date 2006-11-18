package composestarEclipsePlugin.buildConfiguration;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class representing a compose* setting.
 * Used by BuildConfigurationManager to build configuration-file.
 */
public class Settings {

	private ArrayList paths = new ArrayList();
	private HashMap moduleSettings = new HashMap();
	
	public Settings(){
		
	}
	
	public void addPath(Path p){
		paths.add(p);
	}
	
	public ArrayList getPaths(){
		return paths;
	}
	
	public void addModuleSetting(ModuleSetting ms){
		moduleSettings.put(ms.name,ms.settings);
	}
	
	public HashMap getModuleSettings(){
		return moduleSettings;
	}
	
	public void clearPaths(){
		paths.clear();
	}
	
}
