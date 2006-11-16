package Composestar.Core.Master.Config;

import java.io.Serializable;

public class TypeSource implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3478303188238617896L;

	private String name;

	private String fileName;

	public TypeSource()
	{

	}

	public String getName()
	{
		return name;
	}

	public void setName(String inName)
	{
		name = inName;
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
