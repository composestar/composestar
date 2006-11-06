package Composestar.Core.Master.Config;

import java.io.Serializable;

public class TypeSource implements Serializable
{

	private String name;

	private String fileName;

	public TypeSource()
	{

	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String filename)
	{
		this.fileName = filename;
	}
}
