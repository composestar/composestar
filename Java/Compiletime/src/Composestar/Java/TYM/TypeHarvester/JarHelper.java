package Composestar.Java.TYM.TypeHarvester;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class JarHelper
{
	HashSet<JarEntry> resources;

	HashMap<Class<?>, JarEntry> classes;

	HashMap<Class<?>, byte[]> modifiedClasses;

	JarFile jarFile;

	public JarHelper(URL jarURL) throws URISyntaxException, IOException, ClassNotFoundException
	{
		classes = new HashMap<Class<?>, JarEntry>();
		resources = new HashSet<JarEntry>();
		modifiedClasses = new HashMap<Class<?>, byte[]>();

		ClassLoader classLoader = new URLClassLoader(new URL[] { jarURL });
		File jar = new File(jarURL.toURI());
		jarFile = new JarFile(jar);

		Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements())
		{
			JarEntry entry = entries.nextElement();
			String classFileName = entry.getName();
			if (classFileName.endsWith(".class"))
			{
				String className = classFileName.replace('/', '.').substring(0, classFileName.lastIndexOf('.'));
				classes.put(classLoader.loadClass(className), entry);
			}
			else if (!classFileName.equals("META-INF/"))
			{
				resources.add(entry);
			}
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

	public void modifyClass(Class<?> whichClass, byte[] newByteCode)
	{
		modifiedClasses.put(whichClass, newByteCode);
	}

	public void writeToFile(File newJarFile) throws FileNotFoundException, IOException
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
