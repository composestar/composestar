package Composestar.Core.INCRE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Composestar.Core.Exception.ModuleException;

public class FileDependency extends Dependency
{
	private static final long serialVersionUID = -1106616256876579742L;

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
	@Override
	public Object getDepObject(Object obj) throws ModuleException
	{
		List<String> files = new ArrayList<String>();
		Object dep = obj;

		if (!mypath.isEmpty())
		{
			dep = mypath.follow(obj);
		}

		if (dep.getClass().equals(String.class))
		{
			String filename = ((String) dep).replaceAll("\"", "");
			files = new ArrayList<String>(Arrays.asList(filename.split(",")));
		}
		else if (dep.getClass().equals(ArrayList.class))
		{
			return dep;
		}
		else
		{
			throw new ModuleException("Unknown filedependency found", "INCRE");
		}

		return files;
	}

	public void setIsAdded(boolean b)
	{
		isAdded = b;
	}

	public boolean isAdded()
	{
		return isAdded;
	}
}
