/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2007 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * $Id$
 */

package Composestar.Java.COMP;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

import org.apache.log4j.Level;

import Composestar.Core.Annotations.ModuleSetting;
import Composestar.Core.COMP.CompilerException;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Logging.LogMessage;

/**
 * Uses the internal compiler access exposed in Java 1.6. However, the
 * performance gain over executing javac is minimal (~4%). To use this feature
 * tools.jar must be on the classpath for execution.
 * 
 * @author Michiel Hendriks
 */
public class InternalCompiler
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(CStarJavaCompiler.MODULE_NAME);

	private static JavaCompiler compilerService;

	/**
	 * @return A compiler service
	 */
	public static JavaCompiler getJavacService()
	{
		if (compilerService != null)
		{
			return compilerService;
		}
		ServiceLoader<JavaCompiler> services = ServiceLoader.load(JavaCompiler.class);
		for (JavaCompiler jc : services)
		{
			compilerService = jc;
			return compilerService;
		}
		compilerService = ToolProvider.getSystemJavaCompiler();
		return compilerService;
	}

	/**
	 * Compiler compliance level. Used by eclipse to emulate a certain JDK
	 * version. This has implications on the source and target levels
	 */
	@ModuleSetting(ID = "COMP.compliance")
	protected String complianceLevel;

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

	protected boolean emacsLogEntries = false;

	public boolean compileSources(Set<File> sources, File dest, Set<File> classpath, boolean separate)
			throws CompilerException
	{
		JavaCompiler javac = getJavacService();
		DiagnosticCollector<JavaFileObject> diag = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager fm = javac.getStandardFileManager(diag, null, null);
		List<String> options = new ArrayList<String>();
		logger.debug(String.format("Java Compiler class: %s", javac.getClass().getName()));
		// org.eclipse.jdt.internal.compiler.tool.EclipseCompiler
		if ("org.eclipse.jdt.internal.compiler.tool.EclipseCompiler".equals(javac.getClass().getName()))
		{
			if (complianceLevel != null && complianceLevel.length() > 0)
			{
				options.add("-" + complianceLevel);
			}
			options.add("-Xemacs"); // better error messages
			emacsLogEntries = true;
		}
		else
		{

			options.add("-implicit:none");
		}
		if (sourceMode != null && sourceMode.length() > 0)
		{
			options.add("-source");
			options.add(sourceMode);
		}
		if (targetMode != null && targetMode.length() > 0)
		{
			options.add("-target");
			options.add(targetMode);
		}
		// output dir
		options.add("-d");
		options.add(dest.toString());
		// classpath
		fm.getJavaFileObjectsFromFiles(classpath);

		options.add("-cp");
		StringBuffer cp = new StringBuffer();
		for (File file : classpath)
		{
			if (cp.length() > 0)
			{
				cp.append(File.pathSeparator);
			}
			cp.append(file.toString());
		}
		logger.debug(String.format("Class-path: %s", cp.toString()));
		options.add(cp.toString());
		boolean result = true;

		Writer err = new IntErrOut(emacsLogEntries);
		if (!separate)
		{
			Iterable<? extends JavaFileObject> fo = fm.getJavaFileObjectsFromFiles(sources);
			CompilationTask task = javac.getTask(err, fm, diag, options, null, fo);
			result = task.call();
		}
		else
		{
			for (File file : sources)
			{
				Iterable<? extends JavaFileObject> fo = fm.getJavaFileObjects(file);
				CompilationTask task = javac.getTask(err, fm, diag, options, null, fo);
				result &= task.call();
			}
		}

		if (!"org.eclipse.jdt.internal.compiler.tool.EclipseCompiler".equals(javac.getClass().getName()))
		{
			// eclipse's compiler doesn't create nice entries (no source info)
			for (Diagnostic<? extends JavaFileObject> d : diag.getDiagnostics())
			{
				Level l;
				switch (d.getKind())
				{
					case ERROR:
						l = Level.ERROR;
						break;
					case MANDATORY_WARNING:
					case WARNING:
						l = Level.WARN;
						break;
					default:
						l = Level.INFO;
				}
				String file = null;
				if (d.getSource() != null)
				{
					URI sourceUri = d.getSource().toUri();
					if ("file".equals(sourceUri.getScheme()))
					{
						file = sourceUri.getPath();
					}
					else
					{
						file = sourceUri.toString();
					}
				}
				logger.log(l, new LogMessage(d.getMessage(Locale.getDefault()).replaceAll("[\\r\\n]+", ". "), file,
						(int) d.getLineNumber(), (int) d.getColumnNumber()));
			}
		}
		return result;
	}

	/**
	 * Simple write used to report compiler errors to the logger
	 * 
	 * @author Michiel Hendriks
	 */
	static class IntErrOut extends Writer
	{
		protected StringBuffer sb;

		protected boolean emacsLogEntries;

		public IntErrOut(boolean emacsStyle)
		{
			sb = new StringBuffer();
			emacsLogEntries = emacsStyle;
		}

		/*
		 * (non-Javadoc)
		 * @see java.io.Writer#close()
		 */
		@Override
		public void close() throws IOException
		{}

		/*
		 * (non-Javadoc)
		 * @see java.io.Writer#flush()
		 */
		@Override
		public void flush() throws IOException
		{}

		/*
		 * (non-Javadoc)
		 * @see java.io.Writer#write(char[], int, int)
		 */
		@Override
		public void write(char[] str, int offset, int len) throws IOException
		{
			// only used for eclipse's compiler
			sb.append(str, offset, len);
			int nl = sb.indexOf("\n");
			if (nl > -1)
			{
				if (emacsLogEntries)
				{
					/*
					 * /workspace/X.java:8: warning: The method...
					 */
					// String[] res = sb.substring(0, nl).trim().split(":", 4);
					final Pattern pat = Pattern.compile("^(.*):([0-9]+): ([a-z]+): (.*)$", Pattern.CASE_INSENSITIVE);
					Matcher match = pat.matcher(sb.substring(0, nl).trim());
					if (!match.matches())
					{
						logger.debug(sb.substring(0, nl).replaceAll("[\\r\\n]+", ""));
					}
					else
					{
						int line = -1;
						try
						{
							line = Integer.parseInt(match.group(2));
						}
						catch (NumberFormatException e)
						{
						}
						if ("warning".equalsIgnoreCase(match.group(3)))
						{
							logger.warn(new LogMessage(match.group(4), match.group(1), line));
						}
						else if ("error".equalsIgnoreCase(match.group(3)))
						{
							logger.error(new LogMessage(match.group(4), match.group(1), line));
						}
						else
						{
							logger.info(new LogMessage(match.group(4), match.group(1), line));
						}
					}
				}
				else
				{
					// logger.warn(sb.substring(0, nl).trim());
				}
				sb = new StringBuffer(sb.substring(nl + 1));
			}
		}
	}
}
