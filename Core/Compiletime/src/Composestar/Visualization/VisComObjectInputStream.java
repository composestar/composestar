/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Visualization;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import Composestar.Utils.Logging.CPSLogger;

/**
 * ObjectInputStream that tries to resolve Composestar classes from additional
 * jar files. This should make VisCom easier to use (when compiled to a jar
 * file). It doesn't work when being used from plain class files.
 * 
 * @author Michiel Hendriks
 */
public class VisComObjectInputStream extends ObjectInputStream
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger("VisCom.ObjectInputStream");

	/**
	 * Composestar package to jar file table
	 */
	protected static Map<String, String> jarNames;

	static
	{
		jarNames = new HashMap<String, String>();
		jarNames.put("DotNET", "ComposestarDotNET.jar");
		jarNames.put("Java", "ComposestarJava.jar");
		jarNames.put("C", "ComposestarC.jar");
	}

	protected Map<String, URLClassLoader> loaders = new HashMap<String, URLClassLoader>();

	protected VisComObjectInputStream() throws IOException, SecurityException
	{
		super();
	}

	public VisComObjectInputStream(InputStream arg0) throws IOException
	{
		super(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.ObjectInputStream#resolveClass(java.io.ObjectStreamClass)
	 */
	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException
	{
		try
		{
			return super.resolveClass(desc);
		}
		catch (ClassNotFoundException e)
		{
			if (desc.getName().startsWith("Composestar."))
			{
				logger.info("Resolving class " + desc.getName());
				String[] pkg = desc.getName().split("\\.");
				if (pkg.length >= 2)
				{
					// our packages have the form: Composestar<Port>.jar
					String jarName = jarNames.get(pkg[1]);
					if (jarName == null)
					{
						logger.fatal("Unregistered jar archive for Composestar package: Composestar." + pkg[1]);
						throw e;
					}

					logger.debug("Jar name: " + jarName);
					if (!loaders.containsKey(jarName))
					{
						URL[] urls = new URL[1];
						File fl = new File(jarName);
						logger.debug(fl);
						urls[0] = fl.toURI().toURL();
						if (urls[0] == null)
						{
							logger.fatal("Unable to load required JAR file: " + jarName);
							throw e;
						}
						else
						{
							loaders.put(jarName, new URLClassLoader(urls));
						}
					}
					URLClassLoader loader = loaders.get(jarName);
					return loader.loadClass(desc.getName());
				}
			}
			throw e;
		}
	}
}
