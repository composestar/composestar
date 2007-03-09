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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

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
	 * Composestar package to jar file table. Loaded from the
	 * packages.properties file
	 */
	protected Map<String, String[]> packageMapping;

	protected Map<String, URLClassLoader> loaders = new HashMap<String, URLClassLoader>();

	protected VisComObjectInputStream() throws IOException
	{
		super();
		loadPackageMapping();
	}

	public VisComObjectInputStream(InputStream is) throws IOException
	{
		super(is);
		loadPackageMapping();
	}

	protected void loadPackageMapping()
	{
		packageMapping = new HashMap<String, String[]>();
		Properties props = new Properties();
		try
		{
			props.load(VisComObjectInputStream.class.getResourceAsStream("packages.properties"));
		}
		catch (IOException e)
		{
			logger.error("Unable to load packages.properties", e);
			return;
		}
		for (Entry<Object, Object> entry : props.entrySet())
		{
			String[] value = ((String) entry.getValue()).split(" ");
			packageMapping.put((String) entry.getKey(), value);
		}
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
			logger.info("Resolving class " + desc.getName());
			ClassLoader loader = getLoader(desc.getName());
			if (loader == null)
			{
				throw e;
			}
			return loader.loadClass(desc.getName());
		}
	}

	/**
	 * Returns an class loader for the specified composestar package
	 * 
	 * @param pkg
	 * @return
	 */
	protected ClassLoader getLoader(String className)
	{
		String[] jars = null;
		String pkg = className.substring(0, className.lastIndexOf('.'));
		while ((pkg != null) && (pkg.length() > 0))
		{
			if (packageMapping.containsKey(pkg))
			{
				jars = packageMapping.get(pkg);
				break;
			}
			pkg = pkg.substring(0, pkg.lastIndexOf('.'));
		}
		if (jars == null)
		{
			logger.fatal("Unable to find package jar file for class " + className);
			return null;
		}
		logger.debug("Found jars: " + Arrays.toString(jars) + " for package: " + pkg);

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
				URL url;
				try
				{
					url = fl.toURI().toURL();
				}
				catch (MalformedURLException e)
				{
					url = null;
				}
				if (url != null)
				{
					urls.add(url);
				}

			}

			if (urls.size() == 0)
			{
				logger.fatal("Unable to load any of the suggested jars: " + Arrays.toString(jars));
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
