package Composestar.Java.TYM.TypeHarvester;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import Composestar.Core.Master.ModuleNames;
import Composestar.Utils.Logging.CPSLogger;

public class JarHelper
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.HARVESTER);

	HashSet<JarEntry> resources;

	HashMap<Class<?>, JarEntry> classes;

	HashMap<Class<?>, byte[]> modifiedClasses;

	JarFile jarFile;

	public JarHelper(URL jarURL, ClassLoader classLoader) throws URISyntaxException, IOException,
			ClassNotFoundException
	{
		classes = new HashMap<Class<?>, JarEntry>();
		resources = new HashSet<JarEntry>();
		modifiedClasses = new HashMap<Class<?>, byte[]>();

		File jar = new File(jarURL.toURI());
		jarFile = new JarFile(jar);
		boolean futureClasses = false;

		Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements())
		{
			JarEntry entry = entries.nextElement();
			String classFileName = entry.getName();
			if (classFileName.endsWith(".class"))
			{
				String className = classFileName.replace('/', '.').substring(0, classFileName.lastIndexOf('.'));
				try
				{
					classes.put(classLoader.loadClass(className), entry);
				}
				catch (UnsupportedClassVersionError e)
				{
					logger.error(String.format("Class %s in %s was compiled to an unsupported language version",
							className, jarURL), e);
					futureClasses = true;
				}
			}
			else if (!classFileName.equals("META-INF/"))
			{
				resources.add(entry);
			}
		}
		if (futureClasses)
		{
			throw new UnsupportedClassVersionError();
		}
	}

	public Collection<Class<?>> getClasses()
	{
		return classes.keySet();
	}

	public Collection<JarEntry> getResources()
	{
		return resources;
	}

	public InputStream getStream(JarEntry je) throws IOException
	{
		return jarFile.getInputStream(je);
	}

	public void modifyClass(Class<?> whichClass, byte[] newByteCode)
	{
		modifiedClasses.put(whichClass, newByteCode);
	}

	public void writeToFile(File newJarFile) throws IOException
	{
		JarOutputStream newJar = new JarOutputStream(new FileOutputStream(newJarFile));

		// First, copy all modified classes
		for (Class<?> c : modifiedClasses.keySet())
		{
			JarEntry entry = new JarEntry(c.getName().replace('.', '/') + ".class");
			newJar.putNextEntry(entry);
			newJar.write(modifiedClasses.get(c), 0, modifiedClasses.get(c).length);
			classes.remove(c); // This class was modified, so remove the
			// original
		}

		// Copy remaining (unmodified) classes + resources
		// Java retardation: why should taking the union of 2 sets take 3 lines
		// of code
		HashSet<JarEntry> remainder = new HashSet<JarEntry>();
		remainder.addAll(classes.values());
		remainder.addAll(resources);

		for (JarEntry entry : remainder)
		{
			InputStream is = jarFile.getInputStream(entry);
			newJar.putNextEntry(entry);
			copyContents(newJar, is);
			is.close();
		}
		newJar.close();
	}

	private void copyContents(JarOutputStream writeTo, InputStream is) throws IOException
	{
		int bytesRead;
		byte buffer[] = new byte[16384];

		while ((bytesRead = is.read(buffer)) != -1)
		{
			writeTo.write(buffer, 0, bytesRead);
		}
	}
}
