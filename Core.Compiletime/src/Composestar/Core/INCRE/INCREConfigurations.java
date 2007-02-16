/*
 * Created on Mar 9, 2006
 */
package Composestar.Core.INCRE;

import java.util.Iterator;
import java.util.Properties;

import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Dependency;

/**
 * @author Dennis
 */
public class INCREConfigurations
{
	private Properties current;

	private Properties history;

	public Configuration historyconfig;

	public INCREConfigurations()
	{
		current = new Properties();
		history = new Properties();
	}

	public void init()
	{
		// TODO: store in history
		initConfig(historyconfig, history);
		initConfig(Configuration.instance(), current);
	}

	public void initConfig(Configuration config, Properties props)
	{
		/* SECRETMode */
		props.setProperty("SECRETMode", config.getModuleProperty("SECRET", "mode", "EMPTY_CONFIG"));

		/* FILTH_INPUT */
		props.setProperty("FILTH_INPUT", config.getModuleProperty("FILTH", "input", "EMPTY_CONFIG"));

		/* Dependencies */
		StringBuffer deps = new StringBuffer();
		Iterator depIt = config.getProjects().getDependencies().iterator();
		while (depIt.hasNext())
		{
			Dependency d = (Dependency) depIt.next();
			deps.append(d.getFileName());
			if (depIt.hasNext())
			{
				deps.append(",");
			}
		}
		props.setProperty("Dependencies", deps.toString());

		/* Harvester input */
		StringBuffer dummies = new StringBuffer();
		Iterator dumIt = config.getProjects().getCompiledDummies().iterator();
		while (dumIt.hasNext())
		{
			dummies.append(dumIt.next());
			if (dumIt.hasNext())
			{
				dummies.append(",");
			}
		}
		props.setProperty("HarvesterInput", deps + "," + dummies);

		/* ApplicationStart */
		String as = config.getProjects().getApplicationStart();
		if (as != null && as.length() != 0)
		{
			props.setProperty("ApplicationStart", as);
		}

		/* RunDebugLevel */
		int rdl = config.getProjects().getRunDebugLevel();
		props.setProperty("RunDebugLevel", "" + rdl);
	}

	public void addConfiguration(String key, String val)
	{
		current.put(key, val);
	}

	public String getConfiguration(String key)
	{
		INCRE incre = INCRE.instance();
		if (incre.searchingHistory)
		{
			return history.getProperty(key);
		}
		else
		{
			return current.getProperty(key);
		}
	}

	public Properties getHistory()
	{
		return this.history;
	}
}
