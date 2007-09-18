package Composestar.Core.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * A list of arguments
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

	public CmdLineArgumentList()
	{
		super();
		args = new ArrayList<CmdLineArgument>();
	}

	public void addArgument(CmdLineArgument arg)
	{
		if (arg == null)
		{
			return;
		}
		args.add(arg);
	}

	@Override
	public void addArgs(List<String> tolist, Project proj, Properties prop)
	{
		for (CmdLineArgument arg : args)
		{
			arg.addArgs(tolist, proj, prop);
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
