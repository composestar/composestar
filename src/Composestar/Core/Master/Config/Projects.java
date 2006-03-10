package Composestar.Core.Master.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class Projects {

	private Properties properties;
	private ArrayList projects;
	private ArrayList concernSources;
	
	private HashMap projectsByLanguage;
	
	public Projects() {
		properties = new Properties();
		projects = new ArrayList();
		concernSources = new ArrayList();
		projectsByLanguage = new HashMap();
	}
	
	public void addConcernSource(ConcernSource concernsource) {
		concernSources.add(concernsource);
		//System.out.println("ConcernSource "+concernsource.getFileName()+" added to projects");
	}
	
	public ArrayList getConcernSources() {
		return concernSources;
	}
	
	public void addProject(Project proj) {
		projects.add(proj);
		//System.out.println("Project "+proj.getProperty("name")+" added to projects");
	
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
		System.out.println("Projects property "+key+" added with value "+value);
	}
	
	public String getProperty(String key) {
		if(properties.containsKey(key))
			return properties.getProperty(key);
		return null;
	}
}
