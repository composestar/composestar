package Composestar.Core.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import Composestar.Utils.StringUtils;

/**
 * A list of arguments. The list can be merged to a single commandline argument
 * when the merge property is set.
 * 
 * @author Michiel Hendriks
 */
public class CmdLineArgumentList extends CmdLineArgument
{
	private static final long serialVersionUID = 5138033317751017682L;

	/**
	 * The list of arguments
	 */
	protected List<CmdLineArgument> args;

	/**
	 * If true the list will be merged to a single argument
	 */
	protected boolean merge;

	/**
	 * The delimiter string to use during merging.
	 */
	protected String delimiter = " ";

	public CmdLineArgumentList()
	{
		super();
		args = new ArrayList<CmdLineArgument>();
	}

	/**
	 * Add a single child argument
	 * 
	 * @param arg
	 */
	public void addArgument(CmdLineArgument arg)
	{
		if (arg == null)
		{
			return;
		}
		args.add(arg);
	}

	/**
	 * Set the merge flag. When set the child arguments will be converted to a
	 * single commandline argument.
	 * 
	 * @param inMerge
	 */
	public void setMerge(boolean inMerge)
	{
		merge = inMerge;
	}

	public boolean getMerge()
	{
		return merge;
	}

	/**
	 * Sets the delimiter to be used when the list is merged to a single
	 * argument.
	 * 
	 * @param dim
	 */
	public void setDelimiter(String dim)
	{
		if (dim == null)
		{
			delimiter = "";
		}
		else
		{
			delimiter = dim;
		}
	}

	public String getDlimiter()
	{
		return delimiter;
	}

	@Override
	public void addArgs(List<String> tolist, Project proj, Set<File> sources, Properties prop)
	{
		List<String> argList;
		if (merge)
		{
			argList = new ArrayList<String>();
		}
		else
		{
			argList = tolist;
		}
		for (CmdLineArgument arg : args)
		{
			arg.addArgs(argList, proj, sources, prop);
		}
		if (merge)
		{
			tolist.add(StringUtils.join(argList, delimiter));
		}
	}

	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		for (CmdLineArgument arg : args)
		{
			if (sb.length() > 0)
			{
				sb.append(" ");
			}
			sb.append(arg.toString());
		}
		return sb.toString();
	}

}
