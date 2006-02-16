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

	public Object visit(Object obj)
	{
		try 
		{
			DataStore ds = INCRE.instance().getCurrentRepository();
			Properties prop = (Properties)ds.getObjectByID("config");
			return prop.getProperty( objectref );
		}
		catch(Exception excep){
			Debug.out(Debug.MODE_WARNING, "INCRE","Cannot find value for config node "+objectref);
			return null;
		}
	}
}
