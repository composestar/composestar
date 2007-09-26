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

	// /**
	// * Compiles the sources in a project.
	// *
	// * @param p the project to be compiled.
	// * @see
	// Composestar.Core.COMP.LangCompiler#compileSources(Composestar.Core.Master.Config.Project)
	// * @throws CompilerException
	// */
	// public void compileSources(Project p) throws CompilerException
	// {
	// String command = "";
	// StringBuilder options = new StringBuilder("-classpath ");
	// Language lang = p.getLanguage();
	//
	// if (lang == null)
	// {
	// throw new CompilerException("Project has no language object");
	// }
	//
	// // add dummies to classpath
	// options.append('"').append(p.getCompiledDummies()).append('"');
	//
	// // add dependencies to classpath
	// for (Object o1 : p.getDependencies())
	// {
	// options.append(';').append(FileUtils.quote(((Dependency)
	// o1).getFileName()));
	// }
	//
	// // add destination directory
	// String buildPath = p.getBasePath() + "obj/";
	// options.append(" -d ").append(FileUtils.quote(buildPath));
	//
	// // create command
	// CompilerAction action =
	// lang.getCompilerSettings().getCompilerAction("Compile");
	// if (action == null)
	// {
	// throw new CompilerException("Cannot obtain compileraction");
	// }
	//
	// command = action.getArgument();
	// command = lang.getCompilerSettings().getProperty("executable") + " " +
	// command;
	// command = command.replaceAll("\\{OPTIONS\\}", options.toString());
	//
	// for (Object o : p.getSources())
	// {
	// compileSource(command, ((Source) o).getFileName(), buildPath);
	// }
	// }
	//
	// private void compileSource(String command, String source, String
	// buildPath) throws CompilerException
	// {
	//
	// command = command.replaceAll("\\{SOURCES\\}", FileUtils.quote(source));
	//
	// // compile
	// CommandLineExecutor cmdExec = new CommandLineExecutor();
	// int result = cmdExec.exec("call " + command, new File(buildPath));
	// compilerOutput = cmdExec.outputError();
	//
	// if (result != 0)
	// { // there was an error
	// try
	// {
	// java.util.StringTokenizer st = new
	// java.util.StringTokenizer(compilerOutput, "\n");
	// String lastToken = null;
	// while (st.hasMoreTokens())
	// {
	// lastToken = st.nextToken();
	// Debug.out(Debug.MODE_ERROR, "COMP", "COMPILEERROR:" + lastToken);
	// }
	//
	// throw new CompilerException("COMP reported errors during compilation.");
	// }
	// catch (Exception ex)
	// {
	// throw new CompilerException(ex.getMessage());
	// }
	// }
	// }

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

	// /**
	// * Compiles the dummy sources of a project.
	// *
	// * @param p the project
	// * @throws CompilerException
	// * @see
	// Composestar.Core.COMP.LangCompiler#compileDummies(Composestar.Core.Master.Config.Project)
	// */
	// public void compileDummies(Project p) throws CompilerException
	// {
	// String command = "";
	// StringBuilder options = new StringBuilder();
	// Language lang = p.getLanguage();
	//
	// if (lang == null)
	// {
	// throw new CompilerException("Project has no language object");
	// }
	//
	// Iterator deps = p.getDependencies().iterator();
	// if (deps.hasNext())
	// {
	// options.append("-classpath").append(' ');
	// options.append('"').append(((Dependency)
	// deps.next()).getFileName()).append('"');
	// while (deps.hasNext())
	// {
	// options.append(';').append('"').append(((Dependency)
	// deps.next()).getFileName()).append('"');
	// }
	// }
	//
	// // create file containing all dummies
	// String buildPath = p.getBasePath() + "obj/";
	// String target = buildPath + "dummies.txt";
	// String argfiles = "@dummies.txt";
	// createFile(p, true, target);
	//
	// CompilerAction action =
	// lang.getCompilerSettings().getCompilerAction("Compile");
	// if (action == null)
	// {
	// throw new CompilerException("Cannot obtain compileraction");
	// }
	//
	// command = action.getArgument();
	// command = lang.getCompilerSettings().getProperty("executable") + " " +
	// command;
	// command = command.replaceAll("\\{OPTIONS\\}", options.toString());
	// command = command.replaceAll("\\{SOURCES\\}", argfiles);
	//
	// Debug.out(Debug.MODE_DEBUG, "DUMMER", "command for compiling dummies: " +
	// command);
	//
	// // compile
	// CommandLineExecutor cmdExec = new CommandLineExecutor();
	// int result = cmdExec.exec("call " + command, new File(buildPath));
	// compilerOutput = cmdExec.outputError();
	//
	// if (result != 0)
	// { // there was an error
	// try
	// {
	// java.util.StringTokenizer st = new
	// java.util.StringTokenizer(compilerOutput, "\n");
	// String lastToken = null;
	// while (st.hasMoreTokens())
	// {
	// lastToken = st.nextToken();
	// Debug.out(Debug.MODE_ERROR, "COMP", "COMPILEERROR:" + lastToken);
	// }
	//
	// throw new CompilerException("COMP reported errors during compilation.");
	// }
	// catch (Exception ex)
	// {
	// throw new CompilerException(ex.getMessage());
	// }
	// }
	//
	// // create jar-archive
	// createArchive(p);
	// }
	//
	// /**
	// * Creates a jar archive of the dummies.
	// *
	// * @param p the project of the dummies.
	// * @throws CompilerException
	// */
	// public void createArchive(Project p) throws CompilerException
	// {
	// String command = "";
	// HashSet classpaths = new HashSet();
	//
	// Configuration config = Configuration.instance();
	// String dummyPath = config.getPathSettings().getPath("Dummy");
	// String basePath = config.getPathSettings().getPath("Base");
	// String targetPath = basePath + "obj/" + dummyPath;
	//
	// for (Object o : p.getSources())
	// {
	// Source source = (Source) o;
	// String dummyfile = FileUtils.normalizeFilename(source.getDummy());
	// String classPath = dummyfile.substring(dummyfile.indexOf(dummyPath) +
	// dummyPath.length());
	// classPath = classPath.replaceAll(FileUtils.getFilenamePart(dummyfile),
	// "*.class");
	// classpaths.add(classPath);
	// }
	//
	// StringBuilder paths = new StringBuilder();
	// for (Object classpath : classpaths)
	// {
	// String path = (String) classpath;
	// paths.append(' ').append(path);
	// }
	//
	// File targetDir = new File(targetPath);
	// String name = p.getName() + ".dummies.jar";
	// String compiledUnit = targetPath + name;
	//
	// command =
	// p.getLanguage().getCompilerSettings().getCompilerAction("CreateJar").getArgument();
	// command = command.replaceAll("\\{OPTIONS\\}", "-cf");
	// command = command.replaceAll("\\{NAME\\}", name);
	// command = command.replaceAll("\\{CLASSES\\}", paths.toString());
	//
	// CommandLineExecutor cmdExec = new CommandLineExecutor();
	// int result = cmdExec.exec(command, targetDir);
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
	// Debug.out(Debug.MODE_ERROR, "DUMMER", "COMPILEERROR:" + lastToken);
	// }
	// throw new CompilerException("DUMMER reported errors during archive
	// creation.");
	// }
	// catch (Exception ex)
	// {
	// throw new CompilerException(ex.getMessage());
	// }
	// }
	// else
	// {
	// p.setCompiledDummies(compiledUnit);
	// Debug.out(Debug.MODE_DEBUG, "DUMMER", "compiled unit created: " +
	// compiledUnit);
	// }
	// }
	//
	// /**
	// * Helper method. Creates a file containing all sources of a project to be
	// * compiled.
	// *
	// * @param p the project.
	// * @param dummies true if the sources are dummies, false if not.
	// * @param target target location.
	// * @throws CompilerException
	// */
	// public void createFile(Project p, boolean dummies, String target) throws
	// CompilerException
	// {
	// StringBuffer sourcefiles = new StringBuffer();
	//
	// for (Object o : p.getSources())
	// {
	// Source s = (Source) o;
	// if (dummies)
	// {
	// sourcefiles.append("\"").append(FileUtils.normalizeFilename(s.getDummy())).append("\"");
	// }
	// else
	// {
	// sourcefiles.append("\"").append(s.getFileName()).append("\"" + "\n");
	// }
	// }
	//
	// // emit file
	// try
	// {
	// BufferedWriter bw = new BufferedWriter(new FileWriter(target));
	// bw.write(sourcefiles.toString());
	// bw.close();
	// }
	// catch (IOException io)
	// {
	// throw new CompilerException("ERROR while trying to create file! :\n" +
	// io.getMessage());
	// }
	// }
	//
	// /**
	// * @see Composestar.Core.COMP.LangCompiler#getOutput()
	// */
	// public String getOutput()
	// {
	// return this.compilerOutput;
	// }

}
