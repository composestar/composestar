/*
 * Created on Mar 9, 2006
 */
package Composestar.Core.INCRE;

import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Dependency;
import Composestar.Core.Master.Config.Module;

import java.util.ArrayList;
import java.util.Iterator;
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
		if(config.getModuleSettings().getModule("SECRET")!=null){
			Module m = config.getModuleSettings().getModule("SECRET");
			if(m.getProperty("mode")!=null)
				props.put("SECRETMode",m.getProperty("mode"));
			else
				props.put("SECRETMode","EMPTY_CONFIG");
		}
		
		/* FILTH_INPUT */
		if(config.getModuleSettings().getModule("FILTH")!=null){
			Module m = config.getModuleSettings().getModule("FILTH");
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
		ArrayList dummies = config.getProjects().getCompiledDummies();
		Iterator dumIt = dummies.iterator();
		while(dumIt.hasNext()) 
		{
			String name = (String)dumIt.next();
			dummyStr += name;
			if(dumIt.hasNext()) dummyStr += ",";
        }
		props.put("HarvesterInput",depstr+ ',' +dummyStr);
				
		/* ApplicationStart */
		String as = config.getProjects().getProperty("applicationStart");
		if(null!=as && as.length() != 0){
			props.put("ApplicationStart",as);
		}
						
		/* RunDebugLevel */
		String rdl = config.getProjects().getProperty("runDebugLevel");
		props.put("RunDebugLevel",rdl);
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
