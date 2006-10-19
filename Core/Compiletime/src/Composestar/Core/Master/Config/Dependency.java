package Composestar.Core.Master.Config;

import java.io.Serializable;

public class Dependency implements Serializable
{
	String fileName;
	
	public Dependency()
	{
	}
	
	public String getFileName()
	{
		return fileName;
	}
	
	public void setFileName(String file)
	{
		fileName = file;
	}
}

