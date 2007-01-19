package Composestar.Java.TYM.SignatureTransformer;

import java.io.IOException;
import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.jar.*;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.MethodWrapper;
import Composestar.Core.CpsProgramRepository.Signature;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Java.TYM.TypeHarvester.JarLoader;
import Composestar.Java.TYM.TypeHarvester.JarLoaderException;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;

/**
 * Class that transforms the sources(dummies) contained in a jar resource.
 */
public class JarTransformer
{
	private String jarFile;

	private HashMap unmodifiedEntries;

	private HashMap modifiedClasses;

	/**
	 * Constructor.
	 * 
	 * @param jarFile the path of a jarfile.
	 */
	public JarTransformer(String jarFile)
	{
		this.jarFile = jarFile;
		unmodifiedEntries = new HashMap();
		modifiedClasses = new HashMap();
	}

	/**
	 * Helper method. Converts a package name (contains '.') to a jar entry name
	 * (contains '/' and ends with .class)
	 * 
	 * @param name
	 * @return
	 */
	public String convertPackageToJar(String name)
	{
		String entry = name.replace(JarLoader.PACKAGE_SEPARATOR, JarLoader.JAR_SEPARATOR);
		entry += JarLoader.CLASS_EXTENSION;
		return entry;
	}

	/**
	 * Extracts the contents of a jarfile.
	 * <p>
	 * Classes are added to a modified list. All other files don't need to be
	 * transformed so they are added to an unmodified list.
	 * 
	 * @throws ModuleException - e.g.when jarfile cannot be found.
	 */
	public void read() throws ModuleException
	{
		HashMap temp = new HashMap();

		// extract files
		try
		{
			JarFile jar = new JarFile(jarFile);
			Enumeration entries = jar.entries();
			while (entries.hasMoreElements())
			{
				JarEntry entry = (JarEntry) entries.nextElement();
				String name = entry.getName();
				if (name.endsWith(".class"))
				{
					// add to temporary list
					temp.put(name, entry);
				}
				else
				{
					// add to unmodified list
					if (!name.equals("META-INF/")) 
					{
						unmodifiedEntries.put(name, entry);
					}
				}
			}
		}
		catch (IOException io)
		{
			throw new ModuleException("Error while reading jar: " + io.getMessage(), "SITRA");
		}

		boolean modify = false;

		// extract classes
		try
		{
			JarLoader jl = new JarLoader(jarFile);
			HashMap classen = jl.getLoadedClasses();
			for (Object o : classen.keySet())
			{

				String classname = (String) o;
				Class clazz = (Class) classen.get(classname);

				DataStore ds = DataStore.instance();
				Concern concern = (Concern) ds.getObjectByID(clazz.getName());
				Signature signature = concern.getSignature();
				if (signature != null)
				{
					List methodsAdded = signature.getMethods(MethodWrapper.ADDED);
					List methodsRemoved = signature.getMethods(MethodWrapper.REMOVED);
					if (methodsAdded.size() > 0 || methodsRemoved.size() > 0)
					{
						modify = true;
					}
				}

				// convert classname to jarentry
				String entry = convertPackageToJar(classname);
				if (modify)
				{
					ClassWrapper c = new ClassWrapper(clazz, concern, null);
					modifiedClasses.put(entry, c);
					modify = false;
				}
				else
				{
					JarEntry je = (JarEntry) temp.get(entry);
					unmodifiedEntries.put(entry, je);
				}
			}
		}
		catch (JarLoaderException e)
		{
			throw new ModuleException("Error while loading classes from jar: " + e.getMessage(), "SITRA");
		}
	}

	/**
	 * Run method. It does the following things:
	 * <p>
	 * 1. Reads the contents of a jarfile.
	 * <p>
	 * 2. Transforms the classes extracted from the jar resource.
	 * <p>
	 * 3. Creates a new jarfile containing the changes.
	 * 
	 * @throws ModuleException - when an error occurs while transforming the
	 *             classes.
	 */
	public void run() throws ModuleException
	{

		read();

		try
		{
			transform();
		}
		catch (Exception e)
		{
			throw new ModuleException("Exception occured while transforming classes in jar: " + e.getMessage(), "SITRA");
		}

		write();
	}

	/**
	 * Calls <code>ClassModifier</code> to transform the classes retrieved
	 * from the jar resource.
	 * 
	 * @throws Exception - when an error occurs while transforming a class.
	 */
	public void transform() throws Exception
	{

		for (Object o : modifiedClasses.keySet())
		{
			String key = (String) o;
			ClassWrapper c = (ClassWrapper) modifiedClasses.get(key);
			ClassModifier.instance().modifyClass(c, jarFile);
		}
	}

	/**
	 * Writes the changes back to the original jarfile.
	 */
	public void write() throws ModuleException
	{

		// original file
		JarFile jar = null;
		try
		{
			jar = new JarFile(jarFile);
		}
		catch (IOException ignored)
		{
		}

		// create temporary file
		File tempJar = null;
		try
		{
			String dummyPath = "";
			dummyPath = jarFile.substring(0, jarFile.lastIndexOf(JarLoader.JAR_SEPARATOR));
			tempJar = File.createTempFile("dummies", null, new File(dummyPath));
		}
		catch (IOException ignored)
		{
		}

		// Only rename file at end on success
		boolean success = false;

		try
		{
			JarOutputStream newJar = new JarOutputStream(new FileOutputStream(tempJar));

			byte buffer[] = new byte[1024];
			int bytesRead;

			try
			{
				// add original unmodified files
				for (Object o : unmodifiedEntries.keySet())
				{
					String key = (String) o;
					JarEntry entry = (JarEntry) unmodifiedEntries.get(key);
					InputStream is = jar.getInputStream(entry);
					newJar.putNextEntry(entry);
					while ((bytesRead = is.read(buffer)) != -1)
					{
						newJar.write(buffer, 0, bytesRead);
					}
					is.close();
				}

				// add modified entries
				Iterator keyset = modifiedClasses.keySet().iterator();
				while (keyset.hasNext())
				{
					String key = (String) keyset.next();
					ClassWrapper c = (ClassWrapper) modifiedClasses.get(key);
					JarEntry entry = new JarEntry(key);
					newJar.putNextEntry(entry);
					newJar.write(c.getByteCode(), 0, c.getByteCode().length);
				}
				success = true;
			}
			catch (IOException io)
			{
				throw new ModuleException("Error occured while updating contents of jarfile: " + io.getMessage(),
						"SITRA");
			}
			finally
			{
				try
				{
					newJar.close();
				}
				catch (IOException ignored)
				{
				}
			}
		}
		catch (IOException ignored)
		{
		}
		finally
		{
			try
			{
				jar.close();
			}
			catch (IOException ignored)
			{
			}

			if (!success)
			{
				tempJar.delete();
			}
		}

		if (success)
		{
			File origFile = new File(jarFile);
			origFile.delete();
			success = tempJar.renameTo(origFile);
			if (!success)
			{
				Debug.out(Debug.MODE_DEBUG, "SITRA", "Renaming jar not successfull (jarfile: " + jarFile + ")");
				// fix copy temp file and delete temp file
				try
				{
					Debug.out(Debug.MODE_DEBUG, "SITRA", "Copying '" + jarFile + "' to '" + tempJar.getAbsolutePath()
							+ "'");
					FileUtils.copyFile(jarFile, tempJar.getAbsolutePath());
					tempJar.delete();
				}
				catch (IOException e)
				{
					Debug.out(Debug.MODE_WARNING, "SITRA", "Unable to copy '" + jarFile + "' to '"
							+ tempJar.getAbsolutePath() + "': " + e.getMessage());
				}
			}
		}
	}
}
