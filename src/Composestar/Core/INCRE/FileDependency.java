package Composestar.Core.INCRE;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CommonResources;

import java.util.ArrayList;

public class FileDependency extends Dependency 
{
	private String key = null;
	private CommonResources resources;

   /**
    * @roseuid 420A2506007D
    */
	public FileDependency(String name, CommonResources resources) 
	{
		super(name);
		this.resources = resources;
	}
   
   /**
    * @roseuid 420A24BF0138
    */
	public Object getDepObject(Object obj) throws ModuleException
	{
		String filename = "";
		Object dep = obj;
		
		if(!mypath.isEmpty())
		{
			dep = mypath.follow(obj);
		}
		
		if(dep.getClass().equals(String.class))
		{
			filename = (String)dep;
		}
		else if(dep.getClass().equals(ArrayList.class))
			return dep;
		else if(dep!=null)
			throw new ModuleException("Unknown filedependency found","INCRE");

		ArrayList files = new ArrayList();
		files.add(filename);
		return files;
	}
   
   /**
    * @roseuid 420A24F000EA
    */
	public void setKey(String key) 
	{
		this.key = key;
	}
}
