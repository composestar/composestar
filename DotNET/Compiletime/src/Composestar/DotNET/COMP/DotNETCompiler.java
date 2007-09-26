package Composestar.DotNET.COMP;

import java.io.File;
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
import Composestar.Core.Master.CommonResources;
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
				int exitCode = cmdExec.exec(cmdline);
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
		int exitCode = cmdExec.exec(cmdline);
		processOutput(exitCode, cmdExec.outputNormal());
	}

	// public void compileSources(Project project) throws CompilerException,
	// ModuleException
	// {
	// Configuration config = Configuration.instance();
	// Language lang = project.getLanguage();
	//
	// if (lang == null)
	// {
	// throw new CompilerException("Project has no language object");
	// }
	//
	// CompilerSettings cs = lang.getCompilerSettings();
	//
	// // work out the libraries string
	// CompilerConverter compconv = cs.getCompilerConverter("libraryParam");
	// if (compconv == null)
	// {
	// throw new CompilerException("Cannot obtain CompilerConverter");
	// }
	//
	// String libs = "";
	// String clstring = compconv.getReplaceBy();
	// Iterator depIt = project.getDependencies().iterator();
	// while (depIt.hasNext())
	// {
	// // set the libraries
	// Dependency dependency = (Dependency) depIt.next();
	// String lib = FileUtils.normalizeFilename(dependency.getFileName());
	// libs += clstring.replaceAll(TOKEN_LIB, FileUtils.quote(lib)) + " ";
	// }
	//
	// String dumlib =
	// FileUtils.normalizeFilename(project.getCompiledDummies());
	// libs += clstring.replaceAll(TOKEN_LIB, FileUtils.quote(dumlib)) + " ";
	//
	// // compile each source
	// String objPath = config.getPathSettings().getPath("Base") + "obj/";
	//
	// List sources = project.getSources();
	// Iterator sourcesIt = sources.iterator();
	// while (sourcesIt.hasNext())
	// {
	// Source source = (Source) sourcesIt.next();
	// compileSource(project, cs, source, objPath, libs);
	// }
	// }
	//
	// private void compileSource(Project project, CompilerSettings cs, Source
	// source, String basePath, String libs)
	// throws CompilerException, ModuleException
	// {
	// Configuration config = Configuration.instance();
	// TypeLocations tl = TypeLocations.instance();
	//
	// String filename = new File(source.getFileName()).getAbsolutePath();
	// String targetPath = basePath + source.getTarget();
	//
	// // incremental compilation
	// INCRE incre = INCRE.instance();
	// if (new File(targetPath).exists() && incre.isProcessedByModule(source,
	// MODULE_NAME))
	// {
	// Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "No need to recompile " +
	// filename);
	//
	// config.getLibraries().addLibrary(targetPath);
	// project.addCompiledSource(targetPath);
	//
	// tl.setSourceAssembly(filename, source.getTarget());
	// return; // next source plz
	// }
	//
	// // time compilation of source
	// INCRETimer timer = incre.getReporter().openProcess(MODULE_NAME, filename,
	// INCRETimer.TYPE_NORMAL);
	//
	// // construct the command line
	// String an = (source.isExecutable() ? "CompileExecutable" :
	// "CompileLibrary");
	// CompilerAction action = cs.getCompilerAction(an);
	// if (action == null)
	// {
	// throw new CompilerException("Cannot obtain compileraction");
	// }
	//
	// String args = action.getArgument();
	// String command = cs.getProperty("executable") + " " + args;
	//
	// // fill in the placeholders
	// TokenReplacer tr = new TokenReplacer();
	// tr.addReplacement("OUT", FileUtils.quote(targetPath));
	// tr.addReplacement("LIBS", libs);
	// tr.addReplacement("OPTIONS", cs.getProperty("options"));
	// tr.addReplacement("SOURCES", FileUtils.quote(filename));
	//
	// command = tr.process(command);
	// Debug.out(Debug.MODE_DEBUG, "COMP", "Command " + command);
	//
	// config.getLibraries().addLibrary(targetPath);
	// project.addCompiledSource(targetPath);
	//
	// tl.setSourceAssembly(source.getFileName(), source.getTarget());
	//
	// // execute command
	// CommandLineExecutor cmdExec = new CommandLineExecutor();
	// int result = cmdExec.exec(command);
	// String output = cmdExec.outputNormal();
	//
	// processOutput(result, output);
	// timer.stop();
	// }
	//
	// /**
	// * Compiles the dummy files for the specified project into an assembly.
	// */
	// public void compileDummies(Project project) throws CompilerException
	// {
	// Language lang = project.getLanguage();
	// if (lang == null)
	// {
	// throw new CompilerException("Project has no language object");
	// }
	//
	// CompilerSettings cs = lang.getCompilerSettings();
	//
	// // generate and execute command
	// CompilerAction action = cs.getCompilerAction("CompileLibrary");
	// if (action == null)
	// {
	// throw new CompilerException("Cannot obtain CompilerAction");
	// }
	//
	// // what's the target file?
	// String targetPath = getDummiesFilePath(project);
	// project.setCompiledDummies(targetPath);
	//
	// String command = cs.getProperty("executable") + " " +
	// action.getArgument();
	// TokenReplacer tr = new TokenReplacer();
	// tr.addReplacement("OUT", FileUtils.quote(targetPath));
	// tr.addReplacement("LIBS", getLibrariesString(project, cs));
	// tr.addReplacement("OPTIONS", cs.getProperty("options"));
	// tr.addReplacement("SOURCES", getDummySources(project));
	// command = tr.process(command);
	//
	// Debug.out(Debug.MODE_DEBUG, "COMP", "Command " + command);
	//
	// // execute command
	// CommandLineExecutor cmdExec = new CommandLineExecutor();
	// int result = cmdExec.exec(command);
	// String output = cmdExec.outputNormal();
	//
	// processOutput(result, output);
	// }

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

	// /**
	// * Returns the filename of the dummy-assembly to generate for the
	// specified
	// * project. "{basepath}/obj/dummies/{projectname}.dummies.dll"
	// */
	// private String getDummiesFilePath(Project p)
	// {
	// String dummiesFile = p.getName() + ".dummies.dll";
	// String basePath =
	// Configuration.instance().getPathSettings().getPath("Base");
	//
	// File base = new File(basePath);
	// File target = new File(base, "obj/dummies/" + dummiesFile);
	//
	// return FileUtils.normalizeFilename(target.getAbsolutePath());
	// }

	// /**
	// * Returns a space-separated list of sourcefiles in the specified project.
	// */
	// private String getDummySources(Project p)
	// {
	// StringBuffer sb = new StringBuffer();
	//
	// List sources = p.getSources();
	// Iterator sourcesIter = sources.iterator();
	// while (sourcesIter.hasNext())
	// {
	// Source s = (Source) sourcesIter.next();
	// String dummy = s.getDummy();
	// sb.append(' ').append(FileUtils.quote(dummy));
	// }
	//
	// return sb.toString();
	// }

	// private String getLibrariesString(Project p, CompilerSettings cs) throws
	// CompilerException
	// {
	// CompilerConverter cc = cs.getCompilerConverter("libraryParam");
	// if (cc == null)
	// {
	// throw new CompilerException("Cannot obtain compilerconverter");
	// }
	//
	// StringBuffer sb = new StringBuffer();
	// TokenReplacer tr = new TokenReplacer();
	// String replaceBy = cc.getReplaceBy();
	//
	// Iterator depIter = p.getDependencies().iterator();
	// while (depIter.hasNext())
	// {
	// Dependency dependency = (Dependency) depIter.next();
	// String filename = dependency.getFileName();
	//
	// tr.addReplacement("LIB", FileUtils.quote(filename));
	// String dep = tr.process(replaceBy);
	//
	// sb.append(dep).append(' ');
	// // Debug.out(Debug.MODE_DEBUG,"COMP","Replace: " + replaceBy + ",
	// // File: " + filename + ", Dep: " + dep);
	// }
	//
	// return sb.toString();
	// }

	// /**
	// * Returns a list containing the filenames of all externally linked
	// sources
	// * Used by INCRE
	// */
	// public List externalSources(Source src) throws ModuleException
	// {
	// INCRE incre = INCRE.instance();
	// ArrayList extSources = new ArrayList();
	// ArrayList asmReferences = new ArrayList();
	// String line;
	//
	// // step 1: open il code of source
	// PathSettings ps = Configuration.instance().getPathSettings();
	// String targetFile = ps.getPath("Base") + "obj/" + src.getTarget();
	// String ilFile = ps.getPath("Base") + "obj/Weaver/" + src.getTarget();
	//
	// String ext = (src.isExecutable() ? ".exe" : ".dll");
	// ilFile = ilFile.replaceAll(ext, ".il");
	//
	// // step 2: extract all external assemblies
	// BufferedReader in = null;
	// try
	// {
	// in = new BufferedReader(new InputStreamReader(new
	// FileInputStream(ilFile)));
	// while ((line = in.readLine()) != null)
	// {
	// // read the lines
	// if (line.trim().startsWith(".assembly extern"))
	// {
	// // get name of external assembly
	// String[] elems = line.split(" ");
	// String asmref = elems[elems.length - 1];
	// asmReferences.add(asmref);
	// }
	// }
	// }
	// catch (FileNotFoundException e)
	// {
	// throw new ModuleException("Cannot read " + ilFile, "COMP");
	// }
	// catch (IOException e)
	// {
	// throw new ModuleException("Error occured while reading " + ilFile,
	// "COMP");
	// }
	// finally
	// {
	// FileUtils.close(in);
	// }
	//
	// // step 3: convert external assemblies to user sources on disk
	// TypeLocations locations = TypeLocations.instance();
	// Iterator refs = asmReferences.iterator();
	// while (refs.hasNext())
	// {
	// String ref = (String) refs.next();
	// String source = locations.getSourceByType(ref);
	// if (source != null)
	// {
	// extSources.add(source);
	// }
	// }
	//
	// incre.externalSourcesBySource.put(src, extSources);
	// return extSources;
	// }
	//
	// /**
	// * Returns a list containing modified signatures (signatures with
	// * ADDED/REMOVED methodwrappers) of concerns extracted from external
	// linked
	// * source files Used by INCRE
	// */
	// public List fullSignatures(Source src) throws ModuleException
	// {
	// INCRE incre = INCRE.instance();
	// List extSources = new ArrayList();
	// List signatures = new ArrayList();
	// List concernsToCheck;
	// Set concernsCheckedByKey = new HashSet();
	//
	// String buildPath =
	// Configuration.instance().getPathSettings().getPath("Base") + "obj/";
	// concernsToCheck = incre.getConcernsWithModifiedSignature();
	//
	// if (!concernsToCheck.isEmpty())
	// {
	// // add full signatures of external linked sources
	// String target = buildPath + src.getTarget();
	// extSources = (ArrayList) incre.externalSourcesBySource.get(src);
	//
	// if (extSources == null)
	// {
	// extSources = (ArrayList) externalSources(src);
	// }
	//
	// Iterator conIter = concernsToCheck.iterator();
	// while (conIter.hasNext())
	// {
	// Concern c = (Concern) conIter.next();
	//
	// if (incre.declaredInSources(c, extSources))
	// {
	// if (!concernsCheckedByKey.contains(c.getQualifiedName()))
	// {
	// signatures.add(c.getSignature());
	// concernsCheckedByKey.add(c.getQualifiedName());
	// }
	// }
	// }
	// }
	//
	// return signatures;
	// }
}
