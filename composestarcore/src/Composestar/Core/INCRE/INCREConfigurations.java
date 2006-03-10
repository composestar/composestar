/*
 * Created on Mar 9, 2006
 */
package Composestar.Core.INCRE;

import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Module;

import Composestar.Utils.StringConverter;

import java.util.ArrayList;
import java.util.Properties;
/**
 * @author Dennis
 */
public class INCREConfigurations {
	
	private Properties current;
	private Properties history;
	public Configuration historyconfig;
	
	public INCREConfigurations(){
		current = new Properties();
		history = new Properties();
	}
	
	public void init(){
		//TODO: store in history
		initConfig(historyconfig,history);
		initConfig(Configuration.instance(),current);
	}
	
	public void initConfig(Configuration config,Properties props)
	{
		/* SECRETMode */
		if(config.moduleSettings.getModule("SECRET")!=null){
			Module m = config.moduleSettings.getModule("SECRET");
			if(m.getProperty("mode")!=null)
				props.put("SECRETMode",m.getProperty("mode"));
		}
		
		/* FILTH_INPUT */
		if(config.moduleSettings.getModule("FILTH")!=null){
			Module m = config.moduleSettings.getModule("FILTH");
			if(m.getProperty("input")!=null)
				props.put("FILTH_INPUT",m.getProperty("input"));
		}
		
		/* SECRETConfigFile */
		// SECRET add this one
		
		/* Dependencies */
		ArrayList depsList = config.projects.getDependencies();
		String[] deps = (String[]) depsList.toArray(new String[depsList.size()]);
		String depstr = StringConverter.stringListToString(deps);
		System.out.println("dependencies are "+depstr);
		props.put("Dependencies",depstr);
		
		/* ApplicationStart */
		String as = config.projects.getProperty("applicationStart");
		if(null!=as && !as.equals("")){
			props.put("ApplicationStart",as);
		}
		/*ArrayList projects = config.projects.getProjects();
		Iterator prjIter = projects.iterator();
		while(prjIter.hasNext())
		{
			Project p = (Project)prjIter.next();
			String as = p.getProperty("applicationStart");
			if(null!=as && !as.equals("")){
				props.put("ApplicationStart",as);
				break;
			}
		}*/
				
		/* RunDebugLevel */
		String rdl = config.projects.getProperty("runDebugLevel");
		props.put("RunDebugLevel",rdl);
	}
	
	public void addConfiguration(String key, String val)
	{
		current.put(key,val);
	}
	
	public String getConfiguration(String key)
	{
		INCRE incre = INCRE.instance();
		if(incre.searchingHistory)
			return history.getProperty(key);
		else
			return current.getProperty(key);
	}

}
