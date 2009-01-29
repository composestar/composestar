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

package Composestar.Perf;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

import Composestar.Perf.Data.Report;
import Composestar.Perf.Data.ReportBundle;
import Composestar.Perf.Data.Xml.TimerResultsHandler;
import Composestar.Perf.Export.XMLExport;

/**
 * This is the Composestar performance tool. It processes performance reports of
 * individual compile sessions and combines them into an overview.
 * 
 * @author Michiel Hendriks
 */
public class Tool
{
	/**
	 * The file name pattern for timer result files
	 */
	public static final Pattern TIMER_RESULT_FILENAME = Pattern.compile("^(?i)TimerResults_[0-9]+\\.xml$");

	public Tool()
	{}

	/**
	 * Scan for subdirectories and process very one
	 * 
	 * @param basedir
	 * @return True if all directories were properly processed
	 * @see #processSingleDirectory(File)
	 */
	public boolean processDirectory(File basedir)
	{
		File[] subdirs = basedir.listFiles(new FileFilter()
		{
			public boolean accept(File pathname)
			{
				return pathname.isDirectory();
			}
		});
		for (File dir : subdirs)
		{
			if (!processSingleDirectory(dir))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Process a single directory
	 * 
	 * @param directory
	 * @return
	 */
	public boolean processSingleDirectory(File directory)
	{
		File[] resultFiles = directory.listFiles(new FilenameFilter()
		{
			/*
			 * (non-Javadoc)
			 * @see java.io.FilenameFilter#accept(java.io.File,
			 * java.lang.String)
			 */
			public boolean accept(File dir, String name)
			{
				return TIMER_RESULT_FILENAME.matcher(name).matches();
			}
		});
		ReportBundle bundle = new ReportBundle(directory);
		for (File result : resultFiles)
		{
			System.out.println(String.format("Processing: %s ...", result.toString()));
			Report report = new Report(result);
			try
			{
				if (!TimerResultsHandler.loadReport(result, report))
				{
					return false;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace(System.err);
				return false;
			}
			bundle.addReport(report);
		}
		File output = new File(directory.getParentFile(), String.format("%s.xml", directory.getName()));
		System.out.println(String.format("Exporting to: %s ...", output.toString()));
		try
		{
			XMLExport exporter = new XMLExport(bundle, new FileOutputStream(output));
			exporter.export();
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}

	/**
	 * Takes one argument, a base directory. In that base directory is will read
	 * all directories. For each subdirectory it will process the
	 * TimerResults_*.xml files. If the argument -1 is given is will handle the
	 * given directory as a single subdirectory.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		File baseDir = null;
		boolean singleDir = false;
		for (String arg : args)
		{
			if ("-1".equals(arg))
			{
				singleDir = true;
			}
			else if (baseDir == null)
			{
				baseDir = new File(arg);
				if (!baseDir.isDirectory())
				{
					System.err.println(String.format("'%s' is not a directory", arg));
					System.exit(-1);
				}
			}
			else
			{
				System.err.print(String.format("Unsupported argument: %s", arg));
				System.exit(-1);
			}
		}
		if (baseDir == null)
		{
			System.err.println("No directory specified");
			System.exit(-1);
		}
		Tool tool = new Tool();
		if (singleDir)
		{
			if (!tool.processSingleDirectory(baseDir))
			{
				System.exit(-1);
			}
		}
		else
		{
			if (!tool.processDirectory(baseDir))
			{
				System.exit(-1);
			}
		}
	}
}
