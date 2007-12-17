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

/**
 * Uses the internal compiler access exposed in Java 1.6. However, the
 * performance gain over executing javac is minimal (~4%). To use this feature
 * tools.jar must be on the classpath for execution.
 * 
 * @author Michiel Hendriks
 */
public class InternalCompiler
{
	// protected static final CPSLogger logger =
	// CPSLogger.getCPSLogger(CStarJavaCompiler.MODULE_NAME);
	//
	// public boolean compileSources(Set<File> sources, File dest, Set<File>
	// classpath, boolean separate)
	// throws CompilerException
	// {
	// JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
	// StandardJavaFileManager fm = javac.getStandardFileManager(null, null,
	// null);
	// List<String> options = new ArrayList<String>();
	// options.add("-implicit:none");
	// // output dir
	// options.add("-d");
	// options.add(dest.toString());
	// // classpath
	// fm.getJavaFileObjectsFromFiles(classpath);
	//
	// options.add("-cp");
	// StringBuffer cp = new StringBuffer();
	// for (File file : classpath)
	// {
	// if (cp.length() > 0)
	// {
	// cp.append(";");
	// }
	// cp.append(file.toString());
	// }
	// options.add(cp.toString());
	//
	// if (!separate)
	// {
	// Iterable<? extends JavaFileObject> fo =
	// fm.getJavaFileObjectsFromFiles(sources);
	// CompilationTask task = javac.getTask(new IntErrOut(), fm, null, options,
	// null, fo);
	// return task.call();
	// }
	// else
	// {
	// Writer err = new IntErrOut();
	// for (File file : sources)
	// {
	// Iterable<? extends JavaFileObject> fo = fm.getJavaFileObjects(file);
	// CompilationTask task = javac.getTask(err, fm, null, options, null, fo);
	// if (!task.call())
	// {
	// return false;
	// }
	// }
	// return true;
	// }
	// }
	//
	// /**
	// * Simple write used to report compiler errors to the logger
	// *
	// * @author Michiel Hendriks
	// */
	// class IntErrOut extends Writer
	// {
	// protected StringBuffer sb;
	//
	// public IntErrOut()
	// {
	// sb = new StringBuffer();
	// }
	//
	// @Override
	// public void close() throws IOException
	// {}
	//
	// @Override
	// public void flush() throws IOException
	// {}
	//
	// @Override
	// public void write(char[] str, int offset, int len) throws IOException
	// {
	// sb.append(str, offset, len);
	// int nl = sb.indexOf("\n");
	// if (nl > -1)
	// {
	// logger.error(sb.substring(0, nl).trim());
	// sb = new StringBuffer(sb.substring(nl + 1));
	// }
	// }
	// }
}
