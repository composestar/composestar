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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
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
	private static Map<String, String[]> pkgToJar;

	static
	{
		pkgToJar = new HashMap<String, String[]>();
		String[] jars = new String[] { "ComposestarDotNET.jar", "Starlight.jar" };
		pkgToJar.put("DotNET", jars);
		jars = new String[] { "ComposestarJava.jar" };
		pkgToJar.put("Java", jars);
		jars = new String[] { "ComposestarC.jar" };
		pkgToJar.put("C", jars);
	}

	protected Map<String, URLClassLoader> loaders = new HashMap<String, URLClassLoader>();

	protected VisComObjectInputStream() throws IOException, SecurityException
	{
		super();
	}

	public VisComObjectInputStream(InputStream is) throws IOException
	{
		super(is);
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
					ClassLoader loader = getLoader(pkg[1]);
					if (loader == null)
					{
						throw e;
					}
					return loader.loadClass(desc.getName());
				}
			}
			throw e;
		}
	}

	/**
	 * Returns an class loader for the specified composestar package
	 * 
	 * @param pkg
	 * @return
	 */
	protected ClassLoader getLoader(String pkg)
	{
		String[] jars = pkgToJar.get(pkg);
		if (jars == null)
		{
			logger.fatal("Unregistered jar archive for Composestar package: Composestar." + pkg);
			return null;
		}
		logger.debug("Found jars: " + jars);

		// find an existing loader
		URLClassLoader loader = null;
		for (String jar : jars)
		{
			if (loaders.containsKey(jar))
			{
				loader = loaders.get(jar);
				break;
			}
		}

		// create a new loader
		if (loader == null)
		{
			ArrayList<URL> urls = new ArrayList<URL>();
			for (String jar : jars)
			{
				File fl = new File(jar);
				URL url = null;
				try
				{
					url = fl.toURI().toURL();
				}
				catch (MalformedURLException e)
				{
					// nop
				}
				if (url != null)
				{
					urls.add(url);
				}

			}

			if (urls.size() == 0)
			{
				logger.fatal("Unable to load any of the suggested jars: " + jars);
				return null;
			}

			// register loader for this package
			URL[] urlsArray = new URL[urls.size()];
			urls.toArray(urlsArray);
			loader = URLClassLoader.newInstance(urlsArray);
			for (String jar : jars)
			{
				loaders.put(jar, loader);
			}
		}

		return loader;
	}
}
