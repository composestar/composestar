/*
 * Created on Mar 9, 2006
 */
package Composestar.Core.INCRE;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Dependency;
import Composestar.Core.Master.Config.ModuleSettings;

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
		if(config.getModuleSettings().getModule("SECRET")!=null){
			ModuleSettings m = config.getModuleSettings().getModule("SECRET");
			if(m.getProperty("mode")!=null)
				props.put("SECRETMode",m.getProperty("mode"));
			else
				props.put("SECRETMode","EMPTY_CONFIG");
		}
		
		/* FILTH_INPUT */
		if(config.getModuleSettings().getModule("FILTH")!=null){
			ModuleSettings m = config.getModuleSettings().getModule("FILTH");
			if(m.getProperty("input")!=null)
				props.put("FILTH_INPUT",m.getProperty("input"));
			else
				props.put("FILTH_INPUT","EMPTY_CONFIG");	
		}
		
		/* Dependencies */
		Iterator dependencies = config.getProjects().getDependencies().iterator();
    	String depstr = "";
		while(dependencies.hasNext())
    	{
        	Dependency d = (Dependency)dependencies.next();
        	depstr += d.getFileName();
        	if(dependencies.hasNext())
        		depstr += ",";
    	}
		props.put("Dependencies",depstr);
		
		/* Harvester input */
		String dummyStr = "";
		List dummies = config.getProjects().getCompiledDummies();
		Iterator dumIt = dummies.iterator();
		while (dumIt.hasNext()) 
		{
			dummyStr += dumIt.next();
			if (dumIt.hasNext()) dummyStr += ",";
        }
		props.put("HarvesterInput", depstr + "," + dummyStr);
				
		/* ApplicationStart */
		String as = config.getProjects().getApplicationStart();
		if (as != null && as.length() != 0)
			props.put("ApplicationStart", as);
						
		/* RunDebugLevel */
		int rdl = config.getProjects().getRunDebugLevel();
		props.put("RunDebugLevel", "" + rdl);
	}
	
	public void addConfiguration(String key, String val)
	{
		current.put(key,val);
	}
	
	public String getConfiguration(String key)
	{
		INCRE incre = INCRE.instance();
		if(incre.searchingHistory){
			return history.getProperty(key);
		}
		else {
			return current.getProperty(key);
		}
	}

	public Properties getHistory()
	{
		return this.history;
	}
}