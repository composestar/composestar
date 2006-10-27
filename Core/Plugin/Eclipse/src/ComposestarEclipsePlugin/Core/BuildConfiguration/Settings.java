package ComposestarEclipsePlugin.Core.BuildConfiguration;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class representing a compose* setting.
 * Used by BuildConfigurationManager to build configuration-file.
 */
public class Settings {

	private HashMap globalSettings = new HashMap();
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
	
	public void addSetting(String key, String value){
		globalSettings.put(key,value);
	}
	
	public HashMap getSettings(){
		return globalSettings;
	}
	
	public void clearPaths(){
		paths.clear();
	}
	
}
