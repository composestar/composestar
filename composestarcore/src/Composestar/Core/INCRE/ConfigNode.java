package Composestar.Core.INCRE;

import Composestar.Utils.*;
import Composestar.Core.RepositoryImplementation.DataStore;

import java.util.Properties;

public class ConfigNode extends Node
{
	public ConfigNode(String objectref)
	{
		super(objectref);
	}

	/**
	 * @return the referenced configuration object
	 * @param Object obj
	 */
	public Object visit(Object obj)
	{ 
		try 
		{
			DataStore ds = INCRE.instance().getCurrentRepository();
			Properties prop = (Properties)ds.getObjectByID("config");
			if(prop.getProperty(reference).equals(""))
			{
				Debug.out(Debug.MODE_DEBUG, "INCRE","INCRE::ConfigNode EMPTY value for configuration "+reference);
				return "EMPTY_CONFIG"; 
			}
			else 
				return prop.getProperty(reference);
		}
		catch(Exception excep){
			Debug.out(Debug.MODE_WARNING, "INCRE","Cannot find value for config node "+reference+" due to "+excep.getMessage());
			return null;
		}
	}
	
	/**
	 * @return an unique id for a referenced configuration
	 */
	public String getUniqueID(Object obj){
		return this.reference;
	}
}
