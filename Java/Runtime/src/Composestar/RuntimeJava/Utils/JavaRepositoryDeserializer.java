/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2005-2008 University of Twente.
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
package Composestar.RuntimeJava.Utils;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import Composestar.Core.CpsRepository2.Repository;
import Composestar.RuntimeCore.Utils.Debug;
import Composestar.RuntimeCore.Utils.RepositoryDeserializer;

public class JavaRepositoryDeserializer extends RepositoryDeserializer
{
	private static final long serialVersionUID = 1731463901506487997L;

	protected Class<?> mainclass;

	public JavaRepositoryDeserializer(Class<?> mclass)
	{
		mainclass = mclass;
	}

	@Override
	public Repository deserialize(String file)
	{
		ObjectInputStream ois = null;
		try
		{
			InputStream is = mainclass.getResourceAsStream("/" + file);
			if (is == null)
			{
				is = new FileInputStream(new File(file));
			}
			BufferedInputStream bis = new BufferedInputStream(is);
			ois = new ObjectInputStream(bis);
			return (Repository) ois.readObject();
		}
		catch (EOFException eof)
		{
			// no need to print something, EOFException will always happen.
		}
		catch (FileNotFoundException fne)
		{
			Debug.out(Debug.MODE_ERROR, "Util", "File not found: " + fne.getMessage());
		}
		catch (Exception ex)
		{
			Debug.out(Debug.MODE_ERROR, "Util", "Exception while deserializing repository: " + ex.getMessage());
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
				throw new RuntimeException("Unable to close stream: " + e.getMessage());
			}
		}

		// fixing not needed for native serialization
		// RepositoryFixer.fixRepository(ds);
		return null;
	}

}
