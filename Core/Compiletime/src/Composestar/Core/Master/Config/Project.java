package Composestar.Core.Master.Config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Project implements Serializable
{
	private Properties properties;
	private List dependencies;
	private List sources;
	private List typeSources;
	private Language language;
	private String compiledDummies;
	private List compiledSources;
	
	public Project() {
		properties = new Properties();
		dependencies = new ArrayList();
		sources = new ArrayList();
		typeSources = new ArrayList();
		language = new Language();
		compiledSources = new ArrayList();
	}
	
	public void addProperty(String key, String value) {
		properties.setProperty(key, value);
	}
	
	public String getProperty(String key) {
		if(properties.containsKey(key))
			return properties.getProperty(key);
		return null;
	}
	
	public void addDependency(Dependency dep) {
		dependencies.add(dep);
	}
	
	public List getDependencies() {
		return dependencies;
	}
	
	public void addSource(Source source) {
		sources.add(source);
		source.setProject(this);
	}
	
	public List getTypeSources() {
		return typeSources;
	}
	
	public void addTypeSource(TypeSource typesource) {
		typeSources.add(typesource);
	}
	
	public List getSources() {
		return sources;
	}
	
	public void setLanguage(Language lang) {
		this.language = lang;
	}
	
	public Language getLanguage() {
		return this.language;
	}
	
	public void setCompiledDummies(String fileName) {
		this.compiledDummies = fileName;
	}
	
	public String getCompiledDummies() {
		return compiledDummies;
	}
	
	public void addCompiledSource(String source) {
		compiledSources.add(source);
	}
	
	public List getCompiledSources() {
		return this.compiledSources;
	}
}
