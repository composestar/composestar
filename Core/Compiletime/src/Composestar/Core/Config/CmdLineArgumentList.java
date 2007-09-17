package Composestar.Core.Config;

import java.io.File;
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

	public class SourcesCmdLineArgumentList extends CmdLineArgumentList
	{
		private static final long serialVersionUID = 4967203235807658445L;

		@Override
		public void addArgs(List<String> tolist, Project proj, Properties prop)
		{
			for (File file : proj.getSourceFiles())
			{
				prop.setProperty("SOURCE", file.toString());
				super.addArgs(tolist, proj, prop);
			}
			prop.remove("SOURCE");
		}

	}

	public class DepsCmdLineArgumentList extends CmdLineArgumentList
	{
		private static final long serialVersionUID = 2465539159360682422L;

		@Override
		public void addArgs(List<String> tolist, Project proj, Properties prop)
		{
			for (File file : proj.getFilesDependencies())
			{
				prop.setProperty("DEP", file.toString());
				super.addArgs(tolist, proj, prop);
			}
			prop.remove("DEP");
		}
	}
}
