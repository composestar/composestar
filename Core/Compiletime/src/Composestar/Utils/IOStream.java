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

package Composestar.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A simple class that provides an output stream and input stream for the same
 * data.
 * 
 * @author Michiel Hendriks
 */
public class IOStream
{
	protected File tempFile;

	protected InputStream is;

	protected OutputStream os;

	public IOStream(boolean useTempFile)
	{
		if (useTempFile)
		{
			try
			{
				tempFile = File.createTempFile("cstar_", ".tmp");
			}
			catch (IOException e)
			{
				// TODO: show error, no temp file could be created (use memory
				// instead)
				tempFile = null;
			}
		}
		// force creation of OS
		getOutputStream();
	}

	public InputStream getInputStream()
	{
		if (is == null)
		{
			if (tempFile != null)
			{
				try
				{
					is = new BufferedInputStream(new FileInputStream(tempFile));
				}
				catch (FileNotFoundException e)
				{
					// file not found... can't be...
					is = null;
				}
			}
			else
			{
				// TODO error when os == null
				is = new ByteArrayInputStream(((ByteArrayOutputStream) os).toByteArray());
			}
		}
		return is;
	}

	public OutputStream getOutputStream()
	{
		if (os == null)
		{
			if (tempFile != null)
			{
				try
				{
					os = new BufferedOutputStream(new FileOutputStream(tempFile));
				}
				catch (FileNotFoundException e)
				{
					// unable to create file output, will use memory instead
					os = null;
					tempFile = null;
				}
			}
			if (os == null)
			{
				os = new ByteArrayOutputStream();
			}
		}
		return os;
	}
}
