package Composestar.Java.COMP;

import Composestar.Core.COMP.LangCompiler;
import Composestar.Core.COMP.CompilerException;
import Composestar.Core.Master.Config.*;
import Composestar.Utils.CommandLineExecutor;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Triggers <i>javac</i> and <i>jar</i> commands. Used for compiling dummies
 * and sources.
 */
public class JavaCompiler implements LangCompiler
{
	private String compilerOutput;

	/**
	 * Compiles the sources in a project.
	 * 
	 * @param p the project to be compiled.
	 * @see Composestar.Core.COMP.LangCompiler#compileSources(Composestar.Core.Master.Config.Project)
	 * @throws CompilerException
	 */
	public void compileSources(Project p) throws CompilerException
	{
		String command = "";
		String options = "-classpath ";
		Language lang = p.getLanguage();
		if (lang != null)
		{
			// OK fine
		}
		else
		{
			throw new CompilerException("Project has no language object");
		}

		// add dummies to classpath
		options = options + "\"" + p.getCompiledDummies() + "\"";

		// add dependencies to classpath
		Iterator deps = p.getDependencies().iterator();
        for (Object o1 : p.getDependencies()) {
            options = options + ";" + FileUtils.quote(((Dependency) o1).getFileName());
        }

        // add destination directory
		String buildPath = p.getBasePath() + "obj/";
		options = options + " -d " + FileUtils.quote(buildPath);

		// create command
		CompilerAction action = lang.getCompilerSettings().getCompilerAction("Compile");
		if (action == null)
		{
			throw new CompilerException("Cannot obtain compileraction");
		}

		command = action.getArgument();
		command = lang.getCompilerSettings().getProperty("executable") + " " + command;
		command = command.replaceAll("\\{OPTIONS\\}", options);
		
		Iterator sourceIt = p.getSources().iterator();
        for (Object o : p.getSources()) {
            compileSource(command, ((Source) o).getFileName(), buildPath);
        }
    }
	
	private void compileSource(String command, String source, String buildPath) throws CompilerException
	{
		
		command = command.replaceAll("\\{SOURCES\\}", FileUtils.quote(source));
				
		// compile
		CommandLineExecutor cmdExec = new CommandLineExecutor();
		int result = cmdExec.exec("call " + command, new File(buildPath));
		compilerOutput = cmdExec.outputError();

		if (result != 0)
		{ // there was an error
			try
			{
				java.util.StringTokenizer st = new java.util.StringTokenizer(compilerOutput, "\n");
				String lastToken = null;
				while (st.hasMoreTokens())
				{
					lastToken = st.nextToken();
					Debug.out(Debug.MODE_ERROR, "COMP", "COMPILEERROR:" + lastToken);
				}

				throw new CompilerException("COMP reported errors during compilation.");
			}
			catch (Exception ex)
			{
				throw new CompilerException(ex.getMessage());
			}
		}
	}
	/**
	 * Compiles the dummy sources of a project.
	 * 
	 * @param p the project
	 * @throws CompilerException
	 * @see Composestar.Core.COMP.LangCompiler#compileDummies(Composestar.Core.Master.Config.Project)
	 */
	public void compileDummies(Project p) throws CompilerException
	{
		String command = "";
		String options = "";
		Language lang = p.getLanguage();

		if (lang != null)
		{
			// OK fine
		}
		else
		{
			throw new CompilerException("Project has no language object");
		}

		Iterator deps = p.getDependencies().iterator();
		if (deps.hasNext())
		{
			options = "-classpath ";
			options = options + "\"" + ((Dependency) deps.next()).getFileName() + "\"";
			while (deps.hasNext())
			{
				options = options + ";" + "\"" + ((Dependency) deps.next()).getFileName() + "\"";
			}
		}

		// create file containing all dummies
		String buildPath = p.getBasePath() + "obj/";
		String target = buildPath + "dummies.txt";
		String argfiles = "@dummies.txt";
		createFile(p, true, target);

		CompilerAction action = lang.getCompilerSettings().getCompilerAction("Compile");
		if (action == null) 
		{	
			throw new CompilerException("Cannot obtain compileraction");
		}

		command = action.getArgument();
		command = lang.getCompilerSettings().getProperty("executable") + " " + command;
		command = command.replaceAll("\\{OPTIONS\\}", options);
		command = command.replaceAll("\\{SOURCES\\}", argfiles);

		Debug.out(Debug.MODE_DEBUG, "DUMMER", "command for compiling dummies: " + command);

		// compile
		CommandLineExecutor cmdExec = new CommandLineExecutor();
		int result = cmdExec.exec("call " + command, new File(buildPath));
		compilerOutput = cmdExec.outputError();

		if (result != 0)
		{ // there was an error
			try
			{
				java.util.StringTokenizer st = new java.util.StringTokenizer(compilerOutput, "\n");
				String lastToken = null;
				while (st.hasMoreTokens())
				{
					lastToken = st.nextToken();
					Debug.out(Debug.MODE_ERROR, "COMP", "COMPILEERROR:" + lastToken);
				}

				throw new CompilerException("COMP reported errors during compilation.");
			}
			catch (Exception ex)
			{
				throw new CompilerException(ex.getMessage());
			}
		}

		// create jar-archive
		createArchive(p);
	}

	/**
	 * Creates a jar archive of the dummies.
	 * 
	 * @param p the project of the dummies.
	 * @throws CompilerException
	 */
	public void createArchive(Project p) throws CompilerException
	{
		String command = "";
		HashSet classpaths = new HashSet();

		Configuration config = Configuration.instance();
		String dummyPath = config.getPathSettings().getPath("Dummy");
		String basePath = config.getPathSettings().getPath("Base");
		String targetPath = basePath + "obj/" + dummyPath;

		Iterator sourceIt = p.getSources().iterator();
        for (Object o : p.getSources()) {
            Source source = (Source) o;
            String dummyfile = FileUtils.normalizeFilename(source.getDummy());
            String classPath = dummyfile.substring(dummyfile.indexOf(dummyPath) + dummyPath.length());
            classPath = classPath.replaceAll(FileUtils.getFilenamePart(dummyfile), "*.class");
            classpaths.add(classPath);
        }

        String paths = "";
		Iterator pathIt = classpaths.iterator();
        for (Object classpath : classpaths) {
            String path = (String) classpath;
            paths += " " + path;
        }

        File targetDir = new File(targetPath);
		String name = p.getName() + ".dummies.jar";
		String compiledUnit = targetPath + name;

		command = p.getLanguage().getCompilerSettings().getCompilerAction("CreateJar").getArgument();
		command = command.replaceAll("\\{OPTIONS\\}", "-cf");
		command = command.replaceAll("\\{NAME\\}", name);
		command = command.replaceAll("\\{CLASSES\\}", paths);

		CommandLineExecutor cmdExec = new CommandLineExecutor();
		int result = cmdExec.exec(command, targetDir);
		String CompilerOutput = cmdExec.outputError();

		if (result != 0)
		{ 
			// there was an error
			try
			{
				java.util.StringTokenizer st = new java.util.StringTokenizer(CompilerOutput, "\n");
				String lastToken = null;
				while (st.hasMoreTokens())
				{
					lastToken = st.nextToken();
					Debug.out(Debug.MODE_ERROR, "DUMMER", "COMPILEERROR:" + lastToken);
				}
				throw new CompilerException("DUMMER reported errors during archive creation.");
			}
			catch (Exception ex)
			{
				throw new CompilerException(ex.getMessage());
			}
		}
		else
		{
			p.setCompiledDummies(compiledUnit);
			Debug.out(Debug.MODE_DEBUG, "DUMMER", "compiled unit created: " + compiledUnit);
		}
	}

	/**
	 * Helper method. Creates a file containing all sources of a project to be
	 * compiled.
	 * 
	 * @param p the project.
	 * @param dummies true if the sources are dummies, false if not.
	 * @param target target location.
	 * @throws CompilerException
	 */
	public void createFile(Project p, boolean dummies, String target) throws CompilerException
	{
		StringBuffer sourcefiles = new StringBuffer();

		Iterator sourceIt = p.getSources().iterator();
        for (Object o : p.getSources()) {
            Source s = (Source) o;
            if (dummies) {
                sourcefiles.append("\"").append(FileUtils.normalizeFilename(s.getDummy())).append("\"");
            } else {
                sourcefiles.append("\"").append(s.getFileName()).append("\"" + "\n");
            }
        }

        // emit file
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(target));
			bw.write(sourcefiles.toString());
			bw.close();
		}
		catch (IOException io)
		{
			throw new CompilerException("ERROR while trying to create file! :\n" + io.getMessage());
		}
	}

	/**
	 * @see Composestar.Core.COMP.LangCompiler#getOutput()
	 */
	public String getOutput()
	{
		return this.compilerOutput;
	}
}
