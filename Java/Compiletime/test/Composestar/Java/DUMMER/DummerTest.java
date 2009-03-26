/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2009 University of Twente.
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

package Composestar.Java.DUMMER;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import junit.framework.TestCase;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.TASMAN.Manager;
import Composestar.Core.TASMAN.ParallelTask;
import Composestar.Core.TASMAN.Task;
import Composestar.Utils.CommandLineExecutor;

/**
 * @author Michiel Hendriks
 */
public class DummerTest extends TestCase
{
	protected File outputDir;

	protected File classOut;

	protected File sourceDir;

	protected List<File> sources;

	protected Set<File> success;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		outputDir = File.createTempFile("cstar", "");
		outputDir.deleteOnExit();
		outputDir = new File(outputDir.toString() + ".tmp");
		outputDir.mkdirs();
		classOut = new File(outputDir, "classes");
		classOut.mkdir();

		sourceDir = new File(DummerTest.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		sourceDir = new File(sourceDir.getParentFile(), "test-data");
		sourceDir = new File(sourceDir, "jacks-pass");
		populateSources();
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
		cleanOutput();
	}

	/**
	 * Get all files to test
	 */
	protected void populateSources()
	{
		sources = new ArrayList<File>();
		final Set<File> hist = new HashSet<File>();
		final Queue<File> queue = new LinkedList<File>();
		queue.add(sourceDir);
		while (!queue.isEmpty())
		{
			File dir = queue.remove();
			hist.add(dir);
			File[] res = dir.listFiles(new FileFilter()
			{
				/*
				 * (non-Javadoc)
				 * @see java.io.FileFilter#accept(java.io.File)
				 */
				public boolean accept(File pathname)
				{
					if (pathname.isDirectory() && !hist.contains(pathname))
					{
						queue.add(pathname);
						return false;
					}
					return pathname.getName().endsWith(".java");
				}
			});
			for (File f : res)
			{
				sources.add(f);
			}
		}
	}

	protected void cleanOutput()
	{
		final Queue<File> queue = new LinkedList<File>();
		queue.add(outputDir);
		while (!queue.isEmpty())
		{
			File dir = queue.remove();
			File[] res = dir.listFiles(new FileFilter()
			{
				/*
				 * (non-Javadoc)
				 * @see java.io.FileFilter#accept(java.io.File)
				 */
				public boolean accept(File pathname)
				{
					if (pathname.isDirectory())
					{
						queue.add(pathname);
						return false;
					}
					if (pathname.getName().endsWith(".class"))
					{
						// never interested in the .class files
						if (!pathname.delete())
						{
							pathname.deleteOnExit();
						}
					}
					// keep .java files which were left over from failed
					// compilations
					return pathname.getName().endsWith(".java");
				}
			});
			if (res.length == 0)
			{
				// delete "empty" dirs
				if (!dir.delete())
				{
					dir.deleteOnExit();
				}
			}
		}
	}

	public void testDummyGen() throws Exception
	{
		success = new HashSet<File>();
		testCnt = 0;
		ParallelTask ptask = new ParallelTask();
		ptask.setPerProcessor(2);
		for (File source : sources)
		{
			ptask.addTask(new DummerTask(source));
		}
		ptask.execute(null, null);

		List<File> failed = new ArrayList<File>(sources);
		failed.removeAll(success);
		if (failed.size() > 0)
		{
			try
			{
				FileOutputStream out = new FileOutputStream(new File(sourceDir, "jacks.failed.log"));
				PrintStream ps = new PrintStream(out);
				for (File f : failed)
				{
					ps.println(f.toString());
				}
				ps.flush();
				out.flush();
				out.close();
			}
			catch (Exception e)
			{
			}
		}
		assertEquals(sources.size(), success.size());
	}

	protected int testCnt;

	protected synchronized void reportTest(File sourceFile)
	{
		System.out.print('.');
		++testCnt;
		if (testCnt % 80 == 0)
		{
			System.out.println(String.format(" %d%%", testCnt * 100 / sources.size()));
		}
		System.out.flush();
	}

	/**
	 * @param source
	 */
	protected void createDummy(File source)
	{
		File target = new File(outputDir.toString() + source.toString().substring(sourceDir.toString().length()));

		synchronized (sources)
		{
			target.getParentFile().mkdirs();
		}

		JavaDummyEmitter dummer = new JavaDummyEmitter();
		try
		{
			// create the dummy
			dummer.createDummy(source, target);
			// validate the dummy by compiling
			compileDummy(target);

			synchronized (success)
			{
				success.add(source);
			}
			if (!target.delete())
			{
				target.deleteOnExit();
			}
		}
		catch (Exception e)
		{
			System.out.println();
			System.out.flush();
			System.err.println("Source : " + source);
			System.err.println("Result : " + target);
			e.printStackTrace(System.err);
			System.err.println();
			System.err.flush();
		}
	}

	/**
	 * @param target
	 */
	protected void compileDummy(File target) throws Exception
	{
		CommandLineExecutor cmdExec = new CommandLineExecutor();
		String[] cmd = new String[4];
		cmd[0] = "javac";
		cmd[1] = "-d";
		cmd[2] = classOut.toString();
		cmd[3] = target.getName();
		cmdExec.setWorkingDir(target.getParentFile());
		if (cmdExec.exec(cmd) != 0)
		{
			throw new Exception("Compile error: \n" + cmdExec.outputError() + "\n" + cmdExec.outputNormal());
		}
	}

	class DummerTask extends Task
	{
		File source;

		public DummerTask(File forSource)
		{
			super();
			source = forSource;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.TASMAN.Task#execute(Composestar.Core.TASMAN.Manager,
		 * Composestar.Core.Resources.CommonResources)
		 */
		@Override
		public void execute(Manager manager, CommonResources resources) throws ModuleException
		{
			reportTest(source);
			createDummy(source);
		}
	}

	public static void main(String[] args)
	{
		DummerTest dt = new DummerTest();
		try
		{
			dt.outputDir = File.createTempFile("cstar", "");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		dt.outputDir.deleteOnExit();
		dt.outputDir = new File(dt.outputDir.toString() + ".tmp");
		dt.outputDir.mkdirs();
		dt.classOut = new File(dt.outputDir, "classes");
		dt.classOut.mkdir();
		File fl = new File(args[0]);
		dt.sourceDir = fl.getParentFile();
		dt.success = new HashSet<File>();
		dt.sources = new ArrayList<File>();
		dt.createDummy(fl);
		dt.cleanOutput();
	}
}
