package Composestar.Java.COMP;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import Composestar.Core.COMP.CompilerException;
import Composestar.Core.COMP.LangCompiler;
import Composestar.Core.Config.CompilerAction;
import Composestar.Core.Config.Dependency;
import Composestar.Core.Config.Project;
import Composestar.Core.Config.Source;
import Composestar.Core.Config.SourceCompiler;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CommonResources;
import Composestar.Utils.CommandLineExecutor;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Triggers <i>javac</i> and <i>jar</i> commands. Used for compiling dummies
 * and sources.
 */
public class JavaCompiler implements LangCompiler
{
	public static final String MODULE_NAME = "COMP";

	/**
	 * Key used to store the File handle to the dummies
	 */
	public static final String DUMMY_JAR = "JavaDummies";

	/**
	 * Key used to store the path to the source output
	 */
	public static final String SOURCE_OUT = "JavaSourceOut";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	protected SourceCompiler compConfig;

	protected CommonResources resources;

	private String compilerOutput;

	public void setCompilerConfig(SourceCompiler compilerConfig)
	{
		compConfig = compilerConfig;
	}

	public void setCommonResources(CommonResources resc)
	{
		resources = resc;
	}

	public void compileSources(Project p, Set<Source> sources) throws CompilerException, ModuleException
	{
		// TODO this might need to be compiled per file

		Properties prop = new Properties();
		// add the dummy jar to the classpath for expanded signatures
		Dependency dummyDep = new Dependency((File) resources.get(DUMMY_JAR));
		p.addDependency(dummyDep);
		try
		{
			File sourceOut = new File(p.getIntermediate(), "unwoven");
			if (!sourceOut.exists() && !sourceOut.mkdirs())
			{
				throw new ModuleException(String.format("Unable to create source output directory: %s", sourceOut
						.toString()), MODULE_NAME);
			}
			resources.add(SOURCE_OUT, sourceOut);
			prop.put("OUT", sourceOut.toString());
			CompilerAction action = compConfig.getAction("Compile");

			for (Source source : sources)
			{
				Set<File> files = new HashSet<File>();
				files.add(source.getFile());

				String[] cmdline = action.getCmdLine(p, files, prop);
				logger.debug(Arrays.toString(cmdline));
				CommandLineExecutor cmdExec = new CommandLineExecutor();
				int result = cmdExec.exec(cmdline);
				compilerOutput = cmdExec.outputError();

				if (result != 0)
				{
					// there was an error
					try
					{
						java.util.StringTokenizer st = new java.util.StringTokenizer(compilerOutput, "\n");
						String lastToken = null;
						while (st.hasMoreTokens())
						{
							lastToken = st.nextToken();
							logger.error(lastToken);
						}
						throw new CompilerException("COMP reported errors during compilation.");
					}
					catch (Exception ex)
					{
						throw new CompilerException(ex.getMessage());
					}
				}
			}
		}
		finally
		{
			p.removeDependency(dummyDep);
		}
	}

	public void compileDummies(Project p, Set<Source> sources) throws CompilerException
	{
		logger.info("Compiling dummies");

		Properties prop = new Properties();
		File dummiesDir = new File(p.getIntermediate(), "dummies");
		prop.put("OUT", dummiesDir.toString());
		CompilerAction action = compConfig.getAction("Compile");

		Set<File> files = new HashSet<File>();
		for (Source source : sources)
		{
			files.add(source.getStub());
		}

		String[] cmdline = action.getCmdLine(p, files, prop);
		logger.debug(Arrays.toString(cmdline));
		CommandLineExecutor cmdExec = new CommandLineExecutor();
		int result = cmdExec.exec(cmdline);
		compilerOutput = cmdExec.outputError();

		if (result != 0)
		{
			// there was an error
			try
			{
				java.util.StringTokenizer st = new java.util.StringTokenizer(compilerOutput, "\n");
				String lastToken = null;
				while (st.hasMoreTokens())
				{
					lastToken = st.nextToken();
					logger.error(lastToken);
				}
				throw new CompilerException("COMP reported errors during compilation.");
			}
			catch (Exception ex)
			{
				throw new CompilerException(ex.getMessage());
			}
		}

		// create archive

		logger.info("Creating dummies jar file");

		prop = new Properties();
		File dummiesJar = new File(p.getIntermediate(), p.getName() + ".dummies.jar");
		prop.put("JAR", dummiesJar.toString());
		prop.put("SOURCEDIR", dummiesDir.toString());
		action = compConfig.getAction("CreateJar");

		files = Collections.emptySet();
		cmdline = action.getCmdLine(p, files, prop);
		logger.debug(Arrays.toString(cmdline));
		cmdExec = new CommandLineExecutor();
		result = cmdExec.exec(cmdline);
		compilerOutput = cmdExec.outputError();

		if (result != 0)
		{
			// there was an error
			try
			{
				java.util.StringTokenizer st = new java.util.StringTokenizer(compilerOutput, "\n");
				String lastToken = null;
				while (st.hasMoreTokens())
				{
					lastToken = st.nextToken();
					logger.error(lastToken);
				}
				throw new CompilerException("COMP reported errors during jar creations.");
			}
			catch (Exception ex)
			{
				throw new CompilerException(ex.getMessage());
			}
		}

		resources.add(DUMMY_JAR, dummiesJar);
	}
}
