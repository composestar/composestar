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
 * $Id: InternalCompiler.java 4499 2008-11-25 10:50:15Z elmuerte $
 */
package Composestar.Java.COMP;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import Composestar.Core.Master.ModuleNames;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Creates a jar file
 * 
 * @author Michiel Hendriks
 */
public final class JarCreator
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(CStarJavaCompiler.MODULE_NAME + ".JarCreator");

	public static final boolean create(File output, Collection<File> files, File base)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(output);
			JarOutputStream jar = null;
			try
			{
				jar = new JarOutputStream(fos);
				for (File file : files)
				{
					String name;
					if (file.toString().startsWith(base.toString()))
					{
						name = file.toString().substring(base.toString().length()+1);
					}
					else
					{
						// TODO: warning and stuff
						name = file.toString();
					}
					JarEntry entry = new JarEntry(name.replace('\\', '/'));
					jar.putNextEntry(entry);
					FileInputStream fis = new FileInputStream(file);
					try
					{
						byte[] buffer = new byte[1024];
						while (true)
						{
							int bytes = fis.read(buffer);
							if (bytes < 0)
							{
								break;
							}
							jar.write(buffer, 0, bytes);
						}
					}
					finally
					{
						fis.close();
					}
					jar.closeEntry();
				}
				// FileTreeNode root = createFileTree(files, base);
				// writeFileTree(jar, root);
				return true;
			}
			finally
			{
				if (jar != null)
				{
					jar.close();
				}
				fos.close();
			}
		}
		catch (Exception e)
		{
			logger.error(e, e);
			return false;
		}
	}

	private static void writeFileTree(JarOutputStream jar, FileTreeNode root)
	{
		JarEntry entry = new JarEntry(root.getName());
	}

	private static FileTreeNode createFileTree(Collection<File> files, File base)
	{
		return null;
	}

	/**
	 * Represents part of a file hierarchy
	 * 
	 * @author Michiel Hendriks
	 */
	public static class FileTreeNode
	{
		String name;

		File file;

		Collection<FileTreeNode> childs;

		public FileTreeNode(String nodeName, File nodeFile)
		{
			name = nodeName;
			file = nodeFile;
			childs = new ArrayList<FileTreeNode>();
		}

		public String getName()
		{
			return name;
		}

		public File getFile()
		{
			return file;
		}

		public boolean isDirectory()
		{
			return !childs.isEmpty();
		}

		public Collection<FileTreeNode> getChildren()
		{
			return Collections.unmodifiableCollection(childs);
		}

		public void addChild(FileTreeNode node)
		{
			childs.add(node);
		}
	}
}
