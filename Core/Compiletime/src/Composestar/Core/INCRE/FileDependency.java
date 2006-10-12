package Composestar.Core.INCRE;

import Composestar.Core.Exception.ModuleException;

import java.util.ArrayList;
import java.util.Arrays;

public class FileDependency extends Dependency 
{
	private boolean isAdded = true;
	
	/** 
	  * @roseuid 420A2506007D
     * @param name
	  */
	public FileDependency(String name) 
	{
		super(name);
	} 
	   
	/**
	  * @roseuid 420A24BF0138
	  */
	public Object getDepObject(Object obj) throws ModuleException
	{
		ArrayList files = new ArrayList();
		Object dep = obj;
					
		if(!mypath.isEmpty())
			dep = mypath.follow(obj);
					
		if(dep.getClass().equals(String.class))
		{
			String filename = ((String)dep).replaceAll("\"","");
			files = new ArrayList(Arrays.asList(filename.split(",")));
		}
		else if(dep.getClass().equals(ArrayList.class))
			return dep;
		else if(dep!=null)
			throw new ModuleException("Unknown filedependency found","INCRE");

		return files;
	}
	
	public void setIsAdded(boolean b){
		this.isAdded = b;
	}
	
	public boolean isAdded(){
		return this.isAdded;
	}
}