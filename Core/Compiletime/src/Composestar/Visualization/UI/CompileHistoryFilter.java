/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Visualization.UI;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import Composestar.Core.Master.CompileHistory;
import Composestar.Utils.FileUtils;

/**
 * File Filter for Compose* Compile History files
 * 
 * @author Michiel Hendriks
 */
public class CompileHistoryFilter extends FileFilter
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File filename)
	{
		String ext = FileUtils.getExtension(filename.toString());
		return (CompileHistory.EXT_NORMAL.equalsIgnoreCase(ext) || CompileHistory.EXT_COMPRESSED.equalsIgnoreCase(ext));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return "Compose* Compile History";
	}

}
