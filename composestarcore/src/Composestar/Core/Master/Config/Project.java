package Composestar.Core.Master.Config;

import java.util.ArrayList;
import java.util.Properties;

public class Project {

	private Properties properties;
	private ArrayList dependencies;
	private ArrayList sources;
	private ArrayList typeSources;
	private Language language;
	
	public Project() {
		properties = new Properties();
		dependencies = new ArrayList();
		sources = new ArrayList();
		typeSources = new ArrayList();
		language = new Language();
	}
	
	public void addProperty(String key, String value) {
		properties.setProperty(key, value);
		System.out.println("Project property "+key+" added with value "+value);
	}
	
	public String getProperty(String key) {
		if(properties.containsKey(key))
			return properties.getProperty(key);
		return null;
	}
	
	public void addDependency(Dependency dep) {
		dependencies.add(dep);
		System.out.println("Dependency "+dep.getFileName()+" added to project "+this.getProperty("name"));
	}
	
	public ArrayList getDependencies() {
		return dependencies;
	}
	
	public void addSource(Source source) {
		sources.add(source);
		System.out.println("Source "+source.getFileName()+" added to project "+this.getProperty("name"));
	}
	
	public ArrayList getTypeSources() {
		return typeSources;
	}
	
	public void addTypeSource(TypeSource typesource) {
		typeSources.add(typesource);
		System.out.println("TypeSource "+typesource.getName()+" added to project "+this.getProperty("name"));
	}
	
	public ArrayList getSources() {
		return sources;
	}
	
	public void setLanguage(Language lang) {
		System.out.println("set language "+lang.getName());
		this.language = lang;
	}
	
	public Language getLanguage() {
		return this.language;
	}
}
