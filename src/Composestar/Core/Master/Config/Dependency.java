package Composestar.Core.Master.Config;

import java.io.Serializable;

public class Dependency implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -710749427319025140L;

	String fileName;

	public Dependency()
	{}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String file)
	{
		fileName = file;
	}

	public boolean equals(Object o)
	{
		if (!(o instanceof Dependency))
		{
			return false;
		}

		Dependency other = (Dependency) o;
		return fileName.equals(other.fileName);
	}

	public int hashCode()
	{
		return fileName.hashCode();
	}
}
