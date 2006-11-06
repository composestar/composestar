package Composestar.Core.Master.Config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Platform implements Serializable
{

	private List requiredFiles;

	public Platform()
	{
		requiredFiles = new ArrayList();
	}

	public void addRequiredFile(String file)
	{
		requiredFiles.add(file);
	}

	public List getRequiredFiles()
	{
		return requiredFiles;
	}
}
