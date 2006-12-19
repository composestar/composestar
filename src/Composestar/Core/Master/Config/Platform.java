package Composestar.Core.Master.Config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Platform implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6326989729059538537L;

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
