package Composestar.Java.TYM.TypeHarvester;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.TYM.TypeHarvester.HarvestRunner;
import Composestar.Java.COMP.CStarJavaCompiler;
import Composestar.Utils.Debug;

/**
 * Module that harvest the classes from the compiled dummies.
 */
public class JavaHarvestRunner implements HarvestRunner
{
	private ClassMap cm; // contains the harvested classes.

	/**
	 * Default constructor.
	 */
	public JavaHarvestRunner()
	{
		cm = ClassMap.instance();
	}

	/**
	 * Module run method.
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		List<File> toBeHarvested = new ArrayList<File>();

		File dummies = (File) resources.get(CStarJavaCompiler.DUMMY_JAR);
		try
		{
			ClassPathModifier.addFile(dummies);
			toBeHarvested.add(dummies);
		}
		catch (Exception e)
		{
			throw new ModuleException("Error while updating classpath" + e.toString(), MODULE_NAME);
		}

		for (File deps : resources.configuration().getProject().getFilesDependencies())
		{
			try
			{
				ClassPathModifier.addFile(deps);
				toBeHarvested.add(deps);
			}
			catch (Exception e)
			{
				throw new ModuleException("Error while updating classpath" + e.toString(), "HARVESTER");
			}
		}

		for (File file : toBeHarvested)
		{
			try
			{
				JarLoader jl = new JarLoader(file);
				HashMap classen = jl.getLoadedClasses();
				for (Object o : classen.values())
				{
					Class c = (Class) o;
					cm.addClass(c);
					Debug.out(Debug.MODE_DEBUG, "HARVESTER", "Class extracted:" + c.getName());
				}
			}
			catch (JarLoaderException e)
			{
				throw new ModuleException("Error while loading classes from " + file + ": " + e.getMessage(),
						"HARVESTER");
			}
		}
	}

	/**
	 * Helper class. A 'hack' to adjust the classpath at runtime.
	 */
	private static class ClassPathModifier
	{

		private static Class[] parameters = new Class[] { URL.class };

		public static void addFile(String s) throws IOException
		{
			File f = new File(s);
			addFile(f);
		}

		public static void addFile(File f) throws IOException
		{
			addURL(f.toURL());
		}

		public static void addURL(URL u) throws IOException
		{
			URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
			Class sysclass = URLClassLoader.class;

			try
			{
				Method method = sysclass.getDeclaredMethod("addURL", parameters);
				method.setAccessible(true);
				method.invoke(sysloader, new Object[] { u });
			}
			catch (Throwable t)
			{
				t.printStackTrace();
				throw new IOException("Error, could not add URL to system classloader");
			}
		}
	}// end class ClassPathModifier
}// end class JavaHarvestRunner

