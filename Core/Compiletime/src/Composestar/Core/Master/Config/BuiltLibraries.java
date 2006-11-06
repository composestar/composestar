package Composestar.Core.Master.Config;

import java.io.Serializable;
import java.util.ArrayList;

public class BuiltLibraries implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2556429479287190711L;

	private ArrayList builtassemblies;

	public BuiltLibraries()
	{
		builtassemblies = new ArrayList();
	}

	public void addLibrary(String file)
	{
		builtassemblies.add(file);
	}

	public ArrayList getLibraries()
	{
		return builtassemblies;
	}
}
