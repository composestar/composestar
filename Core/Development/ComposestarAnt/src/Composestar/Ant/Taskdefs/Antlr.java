/*
 * Copyright 2000-2004 The Apache Software Foundation Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package Composestar.Ant.Taskdefs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.FileUtils;

/**
 * Invokes the ANTLR Translator generator on a grammar file.
 */
public class Antlr extends Task
{
	/** the classloader used to load AntlrTool */
	private Path classpath;
	
	/** the file to process */
	private File target;

	/** where to output the result */
	private File outputDirectory;

	/** an optional super grammar file */
	private File superGrammar;
	
	/** the target language */
	private String language;

	/** optional flag to add trace methods */
	private boolean trace;

	/** optional flag to add trace methods to the parser only */
	private boolean traceParser;

	/** optional flag to add trace methods to the lexer only */
	private boolean traceLexer;

	/** optional flag to add trace methods to the tree walker only */
	private boolean traceTreeWalker;

	/** The debug attribute */
	private boolean debug;

	public Antlr()
	{
	}

	/**
	 * The grammar file to process.
	 */
	public void setTarget(File target)
	{
		log("Setting target to: " + target.toString(), Project.MSG_VERBOSE);
		this.target = target;
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
	 * Sets an optional super grammar file
	 */
	public void setGlib(File superGrammar)
	{
		this.superGrammar = superGrammar;
	}
	
	/**
	 * Sets an optional target language.
	 * This will override the language option in the grammar file.
	 */
	public void setLanguage(String language)
	{
		this.language = language;
	}

	/**
	 * Sets a flag to enable ParseView debugging
	 */
	public void setDebug(boolean enable)
	{
		this.debug = enable;
	}

	/**
	 * If true, enables all tracing.
	 */
	public void setTrace(boolean enable)
	{
		this.trace = enable;
	}

	/**
	 * If true, enables parser tracing.
	 */
	public void setTraceParser(boolean enable)
	{
		this.traceParser = enable;
	}

	/**
	 * If true, enables lexer tracing.
	 */
	public void setTraceLexer(boolean enable)
	{
		this.traceLexer = enable;
	}

	/**
	 * Sets a flag to allow the user to enable tree walker tracing
	 */
	public void setTraceTreeWalker(boolean enable)
	{
		this.traceTreeWalker = enable;
	}
	
	/**
	 * Sets a classpath to use for loading Antlr.
	 */
	public void setClasspath(Path classpath)
	{
		if (this.classpath != null)
			throw new BuildException("classpath already set");
		
		this.classpath = classpath;
	}
	
	public void setClasspathRef(Reference r)
	{
		createClasspath().setRefid(r);
	}

	public Path createClasspath()
	{
		setClasspath(new Path(getProject()));		
		return classpath;
	}
	
	public void execute() throws BuildException
	{
		validateAttributes();

		File generatedFile = getGeneratedFile();
		boolean targetIsOutOfDate = target.lastModified() > generatedFile.lastModified();
		boolean superGrammarIsOutOfDate = superGrammar != null
				&& (superGrammar.lastModified() > generatedFile.lastModified());
		
		if (targetIsOutOfDate || superGrammarIsOutOfDate)
		{
			log("Compiling " + target + ".", Project.MSG_INFO);
			
			// create arguments
			List args = new ArrayList();			
			populateAttributes(args);
			args.add(target.toString());

			// invoke antlr
			int err = doEverything(args);
			if (err != 0)
			{
				throw new BuildException("ANTLR returned: " + err, getLocation());
			}
		}
		else
		{
			log("Skipped " + target + ".", Project.MSG_INFO);
		}
	}
	
	private int doEverything(List args)
	{
		try
		{
			AntClassLoader acl = getProject().createClassLoader(classpath);
			Class c = Class.forName("antlr.AntlrTool", false, acl);
			
			Object tool = c.newInstance();
			
			log("Setting language to " + language, Project.MSG_VERBOSE);
			Method setLanguage = c.getMethod("setLanguage", new Class[] { String.class });
			setLanguage.invoke(tool, new Object[] { language });
			
			Method doEverything = c.getMethod("doEverything", new Class[] { List.class });
			Integer result = (Integer)doEverything.invoke(tool, new Object[] { args });
			
			return result.intValue();
		}
		catch (Exception e)
		{
			throw new BuildException("" + e.getClass() + ": " + e.getMessage());
		}
	}

	/**
	 * A refactored method for populating all the command line arguments based
	 * on the user-specified attributes.
	 */
	private void populateAttributes(List args)
	{
		args.add("-o");
		args.add(outputDirectory.toString());
		if (superGrammar != null)
		{
			args.add("-glib");
			args.add(superGrammar.toString());
		}
		if (trace)
		{
			args.add("-trace");
		}
		if (traceParser)
		{
			args.add("-traceParser");
		}
		if (traceLexer)
		{
			args.add("-traceLexer");
		}
		if (traceTreeWalker)
		{
			args.add("-traceTreeWalker");
		}
		if (debug)
		{
			args.add("-debug");
		}
	}
	
	private void validateAttributes() throws BuildException
	{
		if (target == null || !target.isFile())
		{
			throw new BuildException("Invalid target: " + target);
		}

		// if no output directory is specified, used the target's directory
		if (outputDirectory == null)
		{
			setOutputdirectory(new File(target.getParent()));
		}
		if (!outputDirectory.isDirectory())
		{
			throw new BuildException("Invalid output directory: " + outputDirectory);
		}
		
		if (language != null)
		{
			if (! ("CSharp".equals(language) || "Java".equals(language)))
			{
				throw new BuildException("Only Java and CSharp target languages are supported");
			}
		}
	}

	private File getGeneratedFile() throws BuildException
	{
		String generatedClassName = getGeneratedClassName();
		String extension = getExtension();
		
		return new File(outputDirectory, generatedClassName + extension);
	}
	
	private String getGeneratedClassName() throws BuildException
	{
		BufferedReader in = null;
		try
		{
			in = new BufferedReader(new FileReader(target));
			String line;
			while ((line = in.readLine()) != null)
			{
				int extendsIndex = line.indexOf(" extends ");
				if (line.startsWith("class ") && extendsIndex > -1)
				{
					return line.substring(6, extendsIndex).trim();
				}
			}
			throw new BuildException("Unable to determine generated class");
		}
		catch (Exception e)
		{
			throw new BuildException("Unable to determine generated class", e);
		}
		finally
		{
			FileUtils.close(in);
		}
	}
	
	private String getExtension()
	{
		if ("CSharp".equals(language))
			return ".cs";
		else
			return ".java";
	}
}
