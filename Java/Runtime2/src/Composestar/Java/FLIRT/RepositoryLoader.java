/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2008 University of Twente.
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

package Composestar.Java.FLIRT;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import Composestar.Core.CpsRepository2.Repository;

/**
 * Loads the repository from somewhere
 * 
 * @author Michiel Hendriks
 */
public final class RepositoryLoader
{
	public static final Logger logger = Logger.getLogger(FLIRTConstants.MODULE_NAME + ".RepositoryLoader");

	private RepositoryLoader()
	{}

	/**
	 * Loads the repository as a resource relative to the main class
	 * 
	 * @param file
	 * @param mainclass
	 * @return
	 */
	public static Repository load(String file, Class<?> mainclass)
	{
		ObjectInputStream ois = null;
		try
		{
			InputStream is = mainclass.getResourceAsStream("/" + file);
			if (is == null)
			{
				is = new FileInputStream(new File(file));
			}
			if (file.endsWith(".gz"))
			{
				is = new GZIPInputStream(is);
			}
			BufferedInputStream bis = new BufferedInputStream(is);
			ois = new ObjectInputStream(bis);
			return (Repository) ois.readObject();
		}
		catch (FileNotFoundException fne)
		{
			logger.log(Level.SEVERE, "File not found: " + fne.getMessage(), fne);
		}
		catch (Exception ex)
		{
			logger.log(Level.SEVERE, "Exception while deserializing repository: " + ex.getMessage(), ex);
		}
		finally
		{
			try
			{
				if (ois != null)
				{
					ois.close();
				}
			}
			catch (IOException e)
			{
				logger.log(Level.SEVERE, "Unable to close stream: " + e.getMessage(), e);
			}
		}
		return null;
	}
}
