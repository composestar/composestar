package Composestar.Core.Master.Config;

import java.io.Serializable;

public class ConcernSource implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1046241361643868704L;

	String fileName;

	public ConcernSource()
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
