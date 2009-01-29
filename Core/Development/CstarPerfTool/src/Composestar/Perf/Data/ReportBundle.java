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

package Composestar.Perf.Data;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A bundle of reports
 * 
 * @author Michiel Hendriks
 */
public class ReportBundle
{
	/**
	 * The base directory of all reports
	 */
	protected File directory;

	/**
	 * Reports, sorted by their timestamp
	 */
	protected SortedSet<Report> reports;

	public ReportBundle(File bundleDirectory)
	{
		directory = bundleDirectory;
		reports = new TreeSet<Report>(new Comparator<Report>()
		{
			public int compare(Report o1, Report o2)
			{
				return o1.getReportDate().compareTo(o2.getReportDate());
			}
		});
	}

	/**
	 * @return the directory
	 */
	public File getDirectory()
	{
		return directory;
	}

	/**
	 * @return the reports
	 */
	public SortedSet<Report> getReports()
	{
		return Collections.unmodifiableSortedSet(reports);
	}

	/**
	 * Add a report
	 * 
	 * @param report
	 */
	public void addReport(Report report)
	{
		reports.add(report);
	}
}
