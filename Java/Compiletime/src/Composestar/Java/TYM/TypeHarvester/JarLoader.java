package Composestar.Java.TYM.TypeHarvester;

import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import Composestar.Core.TYM.TypeHarvester.HarvestRunner;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Utility class to load classes from a jar resource.
 */
public class JarLoader extends ClassLoader
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(HarvestRunner.MODULE_NAME);

	private HashMap classesFromJar = new HashMap();

	private HashMap classesDefined = new HashMap();

	public static final char PACKAGE_SEPARATOR = '.';

	public static final char JAR_SEPARATOR = '/';

	public static final String CLASS_EXTENSION = ".class";

	private boolean redundant;

	/**
	 * Constructor.
	 * 
	 * @param jarFile - path to jar resource.
	 * @throws JarLoaderException
	 */
	public JarLoader(File jarFile) throws JarLoaderException
	{
		try
		{
			JarFile jar = new JarFile(jarFile);
			Enumeration e = jar.entries();
			while (e.hasMoreElements())
			{
				JarEntry je = (JarEntry) e.nextElement();
				String jarName = je.getName();
				if (jarName.endsWith(CLASS_EXTENSION))
				{
					int actuallyRead = 0;
					InputStream inStream = jar.getInputStream(je);
					byte[] classData = new byte[(int) je.getSize()];
					int accumulated = 0;
					while (accumulated < classData.length)
					{
						actuallyRead = inStream.read(classData, accumulated, classData.length - accumulated);
						accumulated += actuallyRead;
					}
					String className = convertJarToPackage(extractClassExtension(je));
					this.classesFromJar.put(className, classData);
					inStream.close();
				}
			}
		}
		catch (Exception e)
		{
			throw new JarLoaderException("" + e.toString());
		}

		defineAllClasses();

	}

	/**
	 * Helper method. Returns an
	 * <code>Iterator<code> of the keyset of the extracted classes.
	 * @return
	 */
	public Iterator classIterator()
	{
		return this.classesFromJar.keySet().iterator();
	}

	/**
	 * Define all classes.
	 * 
	 * @throws JarLoaderException
	 */
	public void defineAllClasses() throws JarLoaderException
	{
		Iterator iterator = classIterator();
		if (!iterator.hasNext())
		{
			// no classes
			return;
		}
		String name = (String) iterator.next();
		try
		{
			this.findSystemClass(name);
			this.redundant = true;
		}
		// note: UnsupportedClassVersionError is thrown when JRE and JDK are not
		// the same major version
		catch (ClassNotFoundException e)
		{
			this.redundant = false;
		}
		iterator = classIterator();
		while (iterator.hasNext())
		{
			name = (String) iterator.next();
			this.defineLoadedClass(name);
		}
	}

	public byte[] getClassData(String name)
	{
		return (byte[]) this.classesFromJar.get(name);
	}

	/**
	 * Converts the bytecode of a class to a <code>Class</code> instance.
	 * Returns null if class is not found.
	 * 
	 * @param name - the fully qualified name of the class.
	 * @throws JarLoaderException
	 */
	private Class defineLoadedClass(String name) throws JarLoaderException
	{
		if (this.classesDefined.containsKey(name))
		{
			return (Class) this.classesDefined.get(name);
		}

		logger.debug(String.format("defineLoadedClass(%s)", name));

		byte[] classData = getClassData(name);
		Class resultClass = null;

		if (this.redundant)
		{
			try
			{
				resultClass = this.findSystemClass(name);
			}
			catch (Exception e)
			{

			}
		}
		else
		{
			try
			{
				resultClass = defineClass(null, classData, 0, classData.length);
			}
			catch (NoClassDefFoundError ncdfe)
			{
				throw new JarLoaderException("NoClassDefFoundError while trying to define class " + name + " ... "
						+ ncdfe.getMessage() + " not found!");
			}
			catch (Exception e)
			{
				throw new JarLoaderException("" + e.toString());
			}
		}

		this.classesDefined.put(name, resultClass);
		return resultClass;
	}

	/**
	 * Helper method. Removes the ".class" from the jar entry name.
	 * 
	 * @param entry - a <code>JarEntry</code>.
	 */
	public static String extractClassExtension(JarEntry entry)
	{
		String withoutExtension = entry.getName();
		int extension = withoutExtension.indexOf(CLASS_EXTENSION);
		return withoutExtension.substring(0, extension);
	}

	/**
	 * Converts a jar entry name to a package name.
	 * 
	 * @param original - jar entry name.
	 */
	public static String convertJarToPackage(String original)
	{
		String withoutExtension = original;
		String className = withoutExtension.replace(JAR_SEPARATOR, PACKAGE_SEPARATOR);
		return className;
	}

	/**
	 * Returns the loaded classes.
	 */
	public HashMap getLoadedClasses()
	{
		return this.classesDefined;
	}

}// end class JarLoader

