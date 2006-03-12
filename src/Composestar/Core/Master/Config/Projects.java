package Composestar.Core.Master.Config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

public class Projects implements Serializable{

	private Properties properties;
	private ArrayList projects;
	private ArrayList concernSources;
	
	private HashMap projectsByLanguage;
	private ArrayList dependencies;
	private ArrayList compiledDummies;
	
	public Projects() {
		properties = new Properties();
		projects = new ArrayList();
		concernSources = new ArrayList();
		projectsByLanguage = new HashMap();
		dependencies = new ArrayList();
		compiledDummies = new ArrayList();
	}
	
	public void addConcernSource(ConcernSource concernsource) {
		concernSources.add(concernsource);
	}
	
	public ArrayList getConcernSources() {
		return concernSources;
	}
	
	public void addProject(Project proj) {
		projects.add(proj);
			
		String language = proj.getProperty("language");
		if(projectsByLanguage.containsKey(language)) {
			ArrayList projects = (ArrayList)projectsByLanguage.get(language);
			projects.add(proj);
			projectsByLanguage.put(language,projects);
		}
		else {
			ArrayList projects = new ArrayList();
			projects.add(proj);
			projectsByLanguage.put(language,projects);
		}	
	}
	
	public ArrayList getProjects() {
		return projects;
	}
	
	public ArrayList getProjectsByLanguage(String language) {
		if(projectsByLanguage.containsKey(language))
			return (ArrayList)projectsByLanguage.get(language);
		return null;
	}
	
	public void addProperty(String key, String value) {
		properties.setProperty(key, value);
	}
	
	public String getProperty(String key) {
		if(properties.containsKey(key))
			return properties.getProperty(key);
		return null;
	}
	
	public ArrayList getDependencies() {
		Iterator projIt = projects.iterator();
		while( projIt.hasNext() ) {
			Project p = (Project)projIt.next();
			ArrayList projectdeps = p.getDependencies();
			Iterator deps = projectdeps.iterator();
			while( deps.hasNext() ) {
				Dependency dependency = (Dependency)deps.next();
				if(!dependencies.contains(dependency)) {
					dependencies.add(dependency);
				}
			}
		}
		return dependencies;
	}
	
	public ArrayList getCompiledDummies() {
		Iterator projIt = projects.iterator();
		while( projIt.hasNext() ) {
			Project p = (Project)projIt.next();
			if(p.getCompiledDummies()!= null) {
				compiledDummies.add(p.getCompiledDummies());
			}
		}
		return compiledDummies;
	}
	
}
