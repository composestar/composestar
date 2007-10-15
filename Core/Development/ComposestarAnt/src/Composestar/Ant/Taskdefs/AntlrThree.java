/*
 * Copyright  2000-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  2006-12-29: Modified to work for antlr3 by Jürgen Pfundt
 *  2007-01-04: Some minor correction after checking code with findBugs tool
 *  2007-02-10: Adapted the grammar type recognition to the changed naming
 *              conventions for Tree Parser
 *  2007-05-16  Added -depend and -XdbgST options
 *              Removed -trace, -traceLexer and -traceParser options
 *              Recompiled with source 1.5 and target 1.5
 */

package Composestar.Ant.Taskdefs;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.apache.tools.ant.util.LoaderUtils;
import org.apache.tools.ant.util.TeeOutputStream;

/**
 * Invokes the ANTLR3 Translator generator on a grammar file.
 */
public class AntlrThree extends Task
{
	private CommandlineJava commandline = new CommandlineJava();

	private List fileSets;

	/** where to output the result */
	private File outputDirectory = null;

	private File activeOutputDirectory = null;

	/** location of token files */
	private File libDirectory = null;

	private File activeLibDirectory = null;

	/** an optional super grammar file */
	private File superGrammar;

	/** fork */
	private boolean fork;

	/** name of output style for messages */
	private String messageFormatName;

	/** optional flag to print out a diagnostic file */
	private boolean diagnostic;

	/** working directory */
	private File workingdir = null;

	private File activeWorkingdir = null;

	/** captures ANTLR's output */
	private ByteArrayOutputStream bos = new ByteArrayOutputStream();

	/** The depend attribute */
	private boolean depend;

	/** The debug attribute */
	private boolean debug;

	/** The report attribute */
	private boolean report;

	/** The print attribute */
	private boolean print;

	/** The profile attribute */
	private boolean profile;

	/** The nfa attribute */
	private boolean nfa;

	/** The dfa attribute */
	private boolean dfa;

	/** multi threaded analysis */
	private boolean multiThreaded;

	/** print AST */
	private boolean grammarTree;

	/* debug stringtemplate */
	private boolean dbgST;

	/** Instance of a utility class to use for file operations. */
	private FileUtils fileUtils;

	public AntlrThree()
	{
		fileSets = new ArrayList();
		commandline.setVm(JavaEnvUtils.getJreExecutable("java"));
		commandline.setClassname("org.antlr.Tool");
		fileUtils = FileUtils.getFileUtils();
	}

	public void addFileset(FileSet fs)
	{
		fileSets.add(fs);
	}

	/**
	 * The directory to write the generated files to.
	 */
	public void setOutputdirectory(File outputDirectory)
	{
		log("Setting output directory to: " + outputDirectory.toString(), Project.MSG_VERBOSE);
		this.outputDirectory = outputDirectory;
	}

	/**
	 * The token files output directory.
	 */
	public void setLibdirectory(File libDirectory)
	{
		log("Setting lib directory to: " + libDirectory.toString(), Project.MSG_VERBOSE);
		this.libDirectory = libDirectory;
	}

	/**
	 * The output style for messages.
	 */
	public void setMessageformat(String name)
	{
		log("Setting message-format to: " + name, Project.MSG_VERBOSE);
		this.messageFormatName = name;
	}

	/**
	 * Sets an optional super grammar file
	 * 
	 * @since ant 1.6
	 */
	public void setGlib(File superGrammar)
	{
		this.superGrammar = superGrammar;
	}

	/**
	 * Sets a flag to depend mode
	 */
	public void setDepend(boolean enable)
	{
		this.depend = enable;
	}

	/**
	 * Sets a flag to enable ParseView debugging
	 */
	public void setDebug(boolean enable)
	{
		this.debug = enable;
	}

	/**
	 * Sets a flag to enable report statistics
	 */
	public void setReport(boolean enable)
	{
		this.report = enable;
	}

	/**
	 * Sets a flag to print out the grammar without actions
	 */
	public void setPrint(boolean enable)
	{
		this.print = enable;
	}

	/**
	 * Sets a flag to enable profiling
	 */
	public void setProfile(boolean enable)
	{
		this.profile = enable;
	}

	/**
	 * Sets a flag to enable nfa generation
	 */
	public void setNfa(boolean enable)
	{
		this.nfa = enable;
	}

	/**
	 * Sets a flag to enable nfa generation
	 */
	public void setDfa(boolean enable)
	{
		this.dfa = enable;
	}

	/**
	 * Run the analysis multithreaded
	 */
	public void setMultithreaded(boolean enable)
	{
		multiThreaded = enable;
	}

	/**
	 * Set a flag to enable printing of the grammar tree
	 */
	public void setGrammartree(boolean enable)
	{
		grammarTree = enable;
	}

	/**
	 * Set a flag to enable printing of the grammar tree
	 */
	public void setDgbST(boolean enable)
	{
		dbgST = enable;
	}

	/**
	 * Sets a flag to emit diagnostic text
	 */
	public void setDiagnostic(boolean enable)
	{
		diagnostic = enable;
	}

	// we are forced to fork ANTLR since there is a call
	// to System.exit() and there is nothing we can do
	// right now to avoid this. :-( (SBa)
	// I'm not removing this method to keep backward compatibility
	/**
	 * @ant.attribute ignore="true"
	 */
	public void setFork(boolean s)
	{
		this.fork = s;
	}

	/**
	 * The working directory of the process
	 */
	public void setDir(File d)
	{
		this.workingdir = d;
	}

	/**
	 * Adds a classpath to be set because a directory might be given for Antlr
	 * debug.
	 */
	public Path createClasspath()
	{
		return commandline.createClasspath(getProject()).createPath();
	}

	/**
	 * Adds a new JVM argument.
	 * 
	 * @return create a new JVM argument so that any argument can be passed to
	 *         the JVM.
	 * @see #setFork(boolean)
	 */
	public Commandline.Argument createJvmarg()
	{
		return commandline.createVmArgument();
	}

	/**
	 * Adds the jars or directories containing Antlr and associates. This should
	 * make the forked JVM work without having to specify it directly.
	 */
	public void init() throws BuildException
	{
		addClasspathEntry("/antlr/ANTLRGrammarParseBehavior.class");
		addClasspathEntry("/org/antlr/tool/ANTLRParser.class");
		addClasspathEntry("/org/antlr/stringtemplate/StringTemplate.class");
	}

	/**
	 * Search for the given resource and add the directory or archive that
	 * contains it to the classpath.
	 * <p>
	 * Doesn't work for archives in JDK 1.1 as the URL returned by getResource
	 * doesn't contain the name of the archive.
	 * </p>
	 */
	protected void addClasspathEntry(String resource)
	{
		/*
		 * pre Ant 1.6 this method used to call getClass().getResource while Ant
		 * 1.6 will call ClassLoader.getResource(). The difference is that
		 * Class.getResource expects a leading slash for "absolute" resources
		 * and will strip it before delegating to ClassLoader.getResource - so
		 * we now have to emulate Class's behavior.
		 */
		if (resource.startsWith("/"))
		{
			resource = resource.substring(1);
		}
		else
		{
			resource = "org/apache/tools/ant/taskdefs/optional/" + resource;
		}

		File f = LoaderUtils.getResourceSource(getClass().getClassLoader(), resource);
		if (f != null)
		{
			log("Found " + f.getAbsolutePath(), Project.MSG_VERBOSE);
			createClasspath().setLocation(f);
		}
		else
		{
			log("Couldn\'t find " + resource, Project.MSG_VERBOSE);
		}
	}

	public void execute() throws BuildException
	{
		Iterator it = fileSets.iterator();
		while (it.hasNext())
		{
			FileSet fs = (FileSet) it.next();
			DirectoryScanner ds = fs.getDirectoryScanner(getProject());
			File basedir = ds.getBasedir();

			String[] files = ds.getIncludedFiles();
			for (int i = 0; i < files.length; i++)
			{
				execute(new File(basedir, files[i]));
			}
		}
	}

	public void execute(File target) throws BuildException
	{
		validateAttributes(target);

		File generatedFile = getGeneratedFile(target);

		boolean targetIsOutOfDate = !fileUtils.isUpToDate(target, generatedFile);
		if (targetIsOutOfDate)
		{
			log("Compiling " + target + " as it is newer than " + generatedFile, Project.MSG_VERBOSE);
		}

		boolean superGrammarIsOutOfDate = superGrammar != null
				&& (superGrammar.lastModified() > generatedFile.lastModified());
		if (superGrammarIsOutOfDate)
		{
			log("Compiling " + target + " as " + superGrammar + " is newer than " + generatedFile, Project.MSG_VERBOSE);
		}
		if (targetIsOutOfDate || superGrammarIsOutOfDate)
		{

			populateAttributes();
			commandline.createArgument().setValue(target.toString());

			log(commandline.describeCommand(), Project.MSG_VERBOSE);
			int err = run(commandline.getCommandline());
			if (err != 0)
			{
				throw new BuildException("ANTLR3 returned: " + err, getLocation());
			}
			else
			{
				String output = bos.toString();
				if (output.indexOf("error:") > -1)
				{
					throw new BuildException("ANTLR3 signaled an error: " + output, getLocation());
				}
			}
		}
		else
		{
			log("Skipped grammar file. Generated file " + generatedFile + " is newer.", Project.MSG_VERBOSE);
		}
	}

	/**
	 * A refactored method for populating all the command line arguments based
	 * on the user-specified attributes.
	 */
	private void populateAttributes()
	{
		commandline.createArgument().setValue("-o");
		commandline.createArgument().setValue(activeOutputDirectory.toString());

		commandline.createArgument().setValue("-lib");
		commandline.createArgument().setValue(activeLibDirectory.toString());
		if (superGrammar != null)
		{
			commandline.createArgument().setValue("-glib");
			commandline.createArgument().setValue(superGrammar.toString());
		}

		if (diagnostic)
		{
			commandline.createArgument().setValue("-diagnostic");
		}
		if (depend)
		{
			commandline.createArgument().setValue("-depend");
		}
		if (debug)
		{
			commandline.createArgument().setValue("-debug");
		}
		if (report)
		{
			commandline.createArgument().setValue("-report");
		}
		if (print)
		{
			commandline.createArgument().setValue("-print");
		}
		if (profile)
		{
			commandline.createArgument().setValue("-profile");
		}
		if (messageFormatName != null)
		{
			commandline.createArgument().setValue("-message-format");
			commandline.createArgument().setValue(messageFormatName);
		}
		if (nfa)
		{
			commandline.createArgument().setValue("-nfa");
		}
		if (dfa)
		{
			commandline.createArgument().setValue("-dfa");
		}
		if (multiThreaded)
		{
			commandline.createArgument().setValue("-Xmultithreaded");
		}
		if (grammarTree)
		{
			commandline.createArgument().setValue("-Xgrtree");
		}
		if (dbgST)
		{
			commandline.createArgument().setValue("-XdbgST");
		}
	}

	private void validateAttributes(File target) throws BuildException
	{

		if (target == null)
		{
			throw new BuildException("No target grammar, lexer grammar or tree parser specified!");
		}
		else if (!target.isFile())
		{
			throw new BuildException("Target: " + target + " is not a file!");
		}

		// if no output directory is specified, use the target's directory
		activeOutputDirectory = outputDirectory;
		if (activeOutputDirectory == null)
		{
			setOutputdirectory(new File(target.getParent()));
		}

		if (!activeOutputDirectory.isDirectory())
		{
			throw new BuildException("Invalid output directory: " + activeOutputDirectory);
		}

		activeWorkingdir = workingdir;
		if (activeWorkingdir != null && !activeWorkingdir.isDirectory())
		{
			throw new BuildException("Invalid working directory: " + activeWorkingdir);
		}

		// if no libDirectory is specified, use the target's directory
		activeLibDirectory = libDirectory;
		if (activeLibDirectory == null)
		{
			setLibdirectory(new File(target.getParent()));
		}

		if (!activeLibDirectory.isDirectory())
		{
			throw new BuildException("Invalid lib directory: " + activeLibDirectory);
		}
	}

	private File getGeneratedFile(File target) throws BuildException
	{
		String generatedFileName = null;

		Pattern p = Pattern
				.compile(
						"^\\p{javaWhitespace}*(grammar|lexer\\p{javaWhitespace}+grammar|tree\\p{javaWhitespace}+grammar)\\p{javaWhitespace}+\\w+\\p{javaWhitespace}*;.*$",
						Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ);
		Matcher m = null;
		BufferedReader in = null;

		try
		{
			in = new BufferedReader(new FileReader(target));
		}
		catch (IOException e)
		{
			throw new BuildException("Could not open file:" + target + " for reading!");
		}

		try
		{
			String line;
			while ((line = in.readLine()) != null)
			{
				m = p.matcher(line);
				log("Trying to match grammar in '" + line + "'", Project.MSG_VERBOSE);
				if (m.matches())
				{
					String type = line.trim();
					if (type.startsWith("lexer"))
					{
						type = "Lexer";
					}
					else if (type.startsWith("tree"))
					{
						type = "";
					}
					else
					{
						type = "Parser";
					}
					log("Matched '" + type + " grammar' in '" + line + "'", Project.MSG_VERBOSE);

					p = Pattern
							.compile(
									"^\\p{javaWhitespace}*(grammar|lexer\\p{javaWhitespace}+grammar|tree\\p{javaWhitespace}+grammar)\\p{javaWhitespace}+",
									Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ);
					m.usePattern(p);
					int startIndex = -1;
					if (m.lookingAt())
					{
						startIndex = m.end();
						log("Start index of grammar filename at position " + startIndex, Project.MSG_VERBOSE);
						int extendsIndex = line.indexOf(";");
						log("End index of grammar filename at position " + extendsIndex, Project.MSG_VERBOSE);
						generatedFileName = line.substring(startIndex, extendsIndex).trim() + type;
						log("Grammar file name extracted: '" + generatedFileName + "'", Project.MSG_VERBOSE);
						break;
					}
				}
			}
		}
		catch (IOException e)
		{
			throw new BuildException("Unable to read from file: " + target, e);
		}
		finally
		{
			try
			{
				in.close();
			}
			catch (IOException e)
			{
				throw new BuildException("Could not close file: " + target + " after reading!", e);
			}
		}

		if (generatedFileName == null)
		{
			throw new BuildException("Unable to determine generated class");
		}
		return new File(activeOutputDirectory, generatedFileName + ".java");
	}

	/** execute in a forked VM */
	private int run(String[] command) throws BuildException
	{
		PumpStreamHandler psh = new PumpStreamHandler(new LogOutputStream(this, Project.MSG_INFO), new TeeOutputStream(
				new LogOutputStream(this, Project.MSG_WARN), bos));
		Execute exe = new Execute(psh, null);
		exe.setAntRun(getProject());
		if (activeWorkingdir != null)
		{
			exe.setWorkingDirectory(activeWorkingdir);
		}
		exe.setCommandline(command);
		try
		{
			return exe.execute();
		}
		catch (IOException e)
		{
			throw new BuildException(e, getLocation());
		}
		finally
		{
			try
			{
				bos.close();
			}
			catch (IOException e)
			{
				// ignore
			}
		}
	}
}
