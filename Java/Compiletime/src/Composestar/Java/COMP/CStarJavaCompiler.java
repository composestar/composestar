package Composestar.Java.COMP;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;

import Composestar.Core.Annotations.ModuleSetting;
import Composestar.Core.COMP.CompilerException;
import Composestar.Core.COMP.LangCompiler;
import Composestar.Core.Config.CompilerAction;
import Composestar.Core.Config.Dependency;
import Composestar.Core.Config.Project;
import Composestar.Core.Config.Source;
import Composestar.Core.Config.SourceCompiler;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.CommandLineExecutor;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Triggers <i>javac</i> and <i>jar</i> commands. Used for compiling dummies and
 * sources.
 */
public class CStarJavaCompiler implements LangCompiler
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

	/**
	 * If true try to use the internal Java compiler interface
	 */
	protected boolean useInternal = true;

	/**
	 * Java source compatibility mode.
	 */
	@ModuleSetting(ID = "COMP.source")
	protected String sourceMode;

	/**
	 * Target to create java byte code for.
	 */
	@ModuleSetting(ID = "COMP.target")
	protected String targetMode;

	public void setCompilerConfig(SourceCompiler compilerConfig)
	{
		compConfig = compilerConfig;
	}

	public void setCommonResources(CommonResources resc)
	{
		resources = resc;
		if (System.getProperty("composestar.java.compiler.tryinternal") != null)
		{
			useInternal = Boolean.getBoolean("composestar.java.compiler.tryinternal");
		}
		String s = resc.configuration().getSetting("internalCompiler");
		if (s != null && s.length() > 0)
		{
			useInternal = Boolean.parseBoolean(s);
		}
		if (useInternal)
		{
			useInternal = CompilerServiceUtil.hasCompilerService();
		}
		resources.inject(this);
	}

	public void compileSources(Project p, Set<Source> sources) throws CompilerException
	{
		long time = System.currentTimeMillis();
		// add the dummy jar to the classpath for expanded signatures
		Dependency dummyDep = new Dependency((File) resources.get(DUMMY_JAR));
		p.addDependency(dummyDep);
		try
		{
			File sourceOut = new File(p.getIntermediate(), "unwoven");
			if (!sourceOut.exists() && !sourceOut.mkdirs())
			{
				throw new CompilerException(String.format("Unable to create source output directory: %s", sourceOut
						.toString()));
			}
			resources.put(SOURCE_OUT, sourceOut);

			if (useInternal)
			{
				logger.info("Using the internal Java compiler service");
				Set<File> files = new HashSet<File>();
				for (Source source : sources)
				{
					files.add(source.getFile());
				}
				InternalCompiler icomp = new InternalCompiler();
				resources.inject(icomp);
				if (!icomp.compileSources(files, sourceOut, p.getFilesDependencies(), true))
				{
					throw new CompilerException("COMP reported errors during compilation.");
				}
			}
			else
			{

				Properties prop = new Properties();
				prop.put("OUT", sourceOut.toString());
				if (sourceMode != null)
				{
					prop.put("SOURCE_MODE", sourceMode);
				}
				if (targetMode != null)
				{
					prop.put("TARGET_MODE", targetMode);
				}
				CompilerAction action = compConfig.getAction("Compile");

				// has to be executed for each source independently because of
				// possible signature expansion
				for (Source source : sources)
				{
					Set<File> files = new HashSet<File>();
					files.add(source.getFile());

					String[] cmdline = action.getCmdLine(p, files, prop);
					logger.debug(Arrays.toString(cmdline));
					CommandLineExecutor cmdExec = new CommandLineExecutor();
					int result;
					try
					{
						result = cmdExec.exec(cmdline);
					}
					catch (IOException e)
					{
						throw new CompilerException(e.getMessage());
					}
					catch (InterruptedException e)
					{
						throw new CompilerException(e.getMessage());
					}
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
		}
		finally
		{
			p.removeDependency(dummyDep);
			logger.debug(String.format("Sources compiled in %d ms", (System.currentTimeMillis() - time)));
		}
	}

	public void compileDummies(Project p, Set<Source> sources) throws CompilerException
	{
		logger.info("Compiling dummies");

		File dummiesDir = new File(p.getIntermediate(), "dummies");
		Set<File> files = new HashSet<File>();
		for (Source source : sources)
		{
			files.add(source.getStub());
		}

		long time = System.currentTimeMillis();
		if (useInternal)
		{
			logger.info("Using the internal Java compiler service");
			InternalCompiler icomp = new InternalCompiler();
			resources.inject(icomp);
			if (!icomp.compileSources(files, dummiesDir, p.getFilesDependencies(), false))
			{
				throw new CompilerException("COMP reported errors during compilation.");
			}
		}
		else
		{

			Properties prop = new Properties();
			prop.put("OUT", dummiesDir.toString());
			if (sourceMode != null)
			{
				prop.put("SOURCE_MODE", sourceMode);
			}
			if (targetMode != null)
			{
				prop.put("TARGET_MODE", targetMode);
			}
			CompilerAction action = compConfig.getAction("Compile");
			String[] cmdline = action.getCmdLine(p, files, prop);
			logger.debug(Arrays.toString(cmdline));
			CommandLineExecutor cmdExec = new CommandLineExecutor();
			int result;
			try
			{
				result = cmdExec.exec(cmdline);
			}
			catch (IOException e)
			{
				throw new CompilerException(e.getMessage());
			}
			catch (InterruptedException e)
			{
				throw new CompilerException(e.getMessage());
			}
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
		logger.debug(String.format("Dummies compiled in %d ms", (System.currentTimeMillis() - time)));

		// create archive

		logger.info("Creating dummies jar file");

		File dummiesJar = new File(p.getIntermediate(), p.getName() + ".dummies.jar");
		files.clear();
		files.addAll(getClassFiles(dummiesDir));
		if (!JarCreator.create(dummiesJar, files, dummiesDir))
		{
			throw new CompilerException("Failed to create dummies.jar file");
		}

		// // old implementation
		// Properties prop = new Properties();
		// prop.put("JAR", dummiesJar.toString());
		// prop.put("SOURCEDIR", dummiesDir.toString());
		// CompilerAction action = compConfig.getAction("CreateJar");
		//
		// files = Collections.emptySet();
		// String[] cmdline = action.getCmdLine(p, files, prop);
		// logger.debug(Arrays.toString(cmdline));
		// CommandLineExecutor cmdExec = new CommandLineExecutor();
		// int result;
		// try
		// {
		// result = cmdExec.exec(cmdline);
		// }
		// catch (IOException e)
		// {
		// throw new CompilerException(e.getMessage());
		// }
		// catch (InterruptedException e)
		// {
		// throw new CompilerException(e.getMessage());
		// }
		// compilerOutput = cmdExec.outputError();
		//
		// if (result != 0)
		// {
		// // there was an error
		// try
		// {
		// java.util.StringTokenizer st = new
		// java.util.StringTokenizer(compilerOutput, "\n");
		// String lastToken = null;
		// while (st.hasMoreTokens())
		// {
		// lastToken = st.nextToken();
		// logger.error(lastToken);
		// }
		// throw new
		// CompilerException("COMP reported errors during jar creations.");
		// }
		// catch (Exception ex)
		// {
		// throw new CompilerException(ex.getMessage());
		// }
		// }

		resources.put(DUMMY_JAR, dummiesJar);
	}

	/**
	 * Recursively get all the .class files
	 * 
	 * @param dummiesDir
	 * @return
	 */
	private Collection<? extends File> getClassFiles(File dummiesDir)
	{
		final Queue<File> todoDirs = new LinkedList<File>();
		todoDirs.add(dummiesDir);
		final Set<File> results = new HashSet<File>();
		while (!todoDirs.isEmpty())
		{
			File dir = todoDirs.poll();
			dir.listFiles(new FileFilter()
			{
				public boolean accept(File pathname)
				{
					if (pathname.isDirectory())
					{
						todoDirs.add(pathname);
					}
					else if (pathname.toString().endsWith(".class"))
					{
						results.add(pathname);
						return true;
					}
					return false;
				}
			});
		}
		return results;
	}
}
