package composestarEclipsePlugin.buildConfiguration;

/**
 * Class representing a compose* path.
 * Used by BuildConfigurationManager to build configuration-file.
 */
public class Path {

	private String name;
	private String path;
	
	public Path(){
		
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void setPath(String path){
		this.path = path;
	}
	
	public String getPath(){
		return path;
	}
}
