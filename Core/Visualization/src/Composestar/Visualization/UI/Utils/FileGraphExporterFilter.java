/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Visualization.UI.Utils;

import java.io.File;
import java.util.Set;

import javax.swing.filechooser.FileFilter;

import Composestar.Utils.FileUtils;
import Composestar.Visualization.Export.FileGraphExporter;

/**
 * @author Michiel Hendriks
 */
public class FileGraphExporterFilter extends FileFilter
{
	protected FileGraphExporter exporter;
	
	protected Set<String> exts;

	protected boolean allowDirectories = true;

	public FileGraphExporterFilter(FileGraphExporter inExporter)
	{
		exporter = inExporter;
		// cache the formats
		exts = exporter.getFormats();
	}
	
	public FileGraphExporter getExporter()
	{
		return exporter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File filename)
	{
		if (allowDirectories && filename.isDirectory())
		{
			return true;
		}
		return exts.contains(FileUtils.getExtension(filename));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return exporter.getDescription();
	}

}
