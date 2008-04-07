package Composestar.DotNET.COMP;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import Composestar.Core.COMP.CompilerException;
import Composestar.Core.COMP.LangCompiler;
import Composestar.Core.Config.CompilerAction;
import Composestar.Core.Config.Dependency;
import Composestar.Core.Config.Project;
import Composestar.Core.Config.Source;
import Composestar.Core.Config.SourceCompiler;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.CommandLineExecutor;
import Composestar.Utils.FileUtils;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Logging.LogMessage;

public class DotNETCompiler implements LangCompiler
{
	public static final String MODULE_NAME = "RECOMA";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	/**
	 * The resource string used to store the location of the dummies.dll
	 */
	public static final String DUMMY_ASSEMBLY = ".NetDummies";

	protected SourceCompiler compConfig;

	protected CommonResources resources;

	public DotNETCompiler()
	{}

	public void setCompilerConfig(SourceCompiler compilerConfig)
	{
		compConfig = compilerConfig;
	}

	public void setCommonResources(CommonResources resc)
	{
		resources = resc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.COMP.LangCompiler#compileSources(Composestar.Core.Config.Project,
	 *      java.util.Set)
	 */
	public void compileSources(Project p, Set<Source> sources) throws CompilerException, ModuleException
	{
		Properties prop = new Properties();
		Dependency dummyDep = new Dependency((File) resources.get(DUMMY_ASSEMBLY));
		p.addDependency(dummyDep);
		try
		{
			for (Source source : sources)
			{
				CompilerAction action;
				File result;
				List<String> types = p.getTypeMapping().getTypes(source);
				String resultName;
				if (types.size() > 0)
				{
					resultName = types.get(0);
					String matchType = FileUtils.removeExtension(source.getRawFile().getName());
					for (String type : types)
					{
						if (type.equals(matchType) || type.endsWith("." + matchType))
						{
							resultName = type;
							break;
						}
					}
				}
				else
				{
					logger.warn(new LogMessage(String.format("%s does not contain any types", source.getRawFile()),
							source.getFile().toString(), 0));
					resultName = FileUtils.removeExtension(source.getFile().getName());
				}
				if (source.equals(p.getMainSource()))
				{
					action = compConfig.getAction("CompileExecutable");
					result = new File(p.getIntermediate(), "out/" + resultName + ".exe");
				}
				else
				{
					action = compConfig.getAction("CompileLibrary");
					result = new File(p.getIntermediate(), "out/" + resultName + ".dll");
				}
				if (!result.getParentFile().exists())
				{
					result.getParentFile().mkdirs();
				}
				Set<File> files = new HashSet<File>();
				files.add(source.getFile());
				prop.setProperty("OUT", result.toString());
				String[] cmdline = action.getCmdLine(p, files, prop);
				logger.debug(Arrays.toString(cmdline));

				CommandLineExecutor cmdExec = new CommandLineExecutor();
				int exitCode;
				try
				{
					exitCode = cmdExec.exec(cmdline);
				}
				catch (IOException e)
				{
					throw new CompilerException(e.getMessage());
				}
				catch (InterruptedException e)
				{
					throw new CompilerException(e.getMessage());
				}
				processOutput(exitCode, cmdExec.outputNormal());
				source.setAssembly(result);
			}
		}
		finally
		{
			p.removeDependency(dummyDep);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.COMP.LangCompiler#compileDummies(Composestar.Core.Config.Project,
	 *      java.util.Set)
	 */
	public void compileDummies(Project p, Set<Source> sources) throws CompilerException
	{
		Properties prop = new Properties();
		Set<File> files = new HashSet<File>();
		for (Source source : sources)
		{
			files.add(source.getStub());
		}
		CompilerAction action = compConfig.getAction("CompileLibrary");
		File result = new File(p.getIntermediate(), "__" + p.getName() + ".dummies.dll");
		resources.add(DUMMY_ASSEMBLY, result);
		prop.setProperty("OUT", result.toString());

		String[] cmdline = action.getCmdLine(p, files, prop);
		logger.debug(Arrays.toString(cmdline));

		CommandLineExecutor cmdExec = new CommandLineExecutor();
		int exitCode;
		try
		{
			exitCode = cmdExec.exec(cmdline);
		}
		catch (IOException e)
		{
			throw new CompilerException(e.getMessage());
		}
		catch (InterruptedException e)
		{
			throw new CompilerException(e.getMessage());
		}
		processOutput(exitCode, cmdExec.outputNormal());
	}

	private void processOutput(int result, String output) throws CompilerException
	{
		if (result != 0) // there was an error
		{
			if (output.length() == 0)
			{
				output = "Could not execute compiler. Make sure the .NET Framework folder is set in the path and restart Visual Studio.";
			}

			StringTokenizer st = new StringTokenizer(output, "\n");
			while (st.hasMoreTokens())
			{
				String line = st.nextToken();
				logger.error("Compilation error: " + line);
			}

			throw new CompilerException("COMP encountered errors during compilation.");
		}
	}
}
