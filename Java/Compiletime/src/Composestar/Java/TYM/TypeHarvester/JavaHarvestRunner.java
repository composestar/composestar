package Composestar.Java.TYM.TypeHarvester;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
	public static final String CLASS_MAP = "ClassMap";  
	
	/**
	 * Iterates over all elements in a JARfile, loading those that end in .class
	 * (This is the only way to load classes in a JARfile without knowing their names in advance)
	 */
	public static HashMap<String,Class> harvest(URL jarURL) throws URISyntaxException, ClassNotFoundException, IOException
	{
		ClassLoader classLoader = new URLClassLoader(new URL[] { jarURL });
		HashMap<String,Class> map = new HashMap<String,Class>();
		File jar = new File(jarURL.toURI());
		JarFile jarFile = new JarFile(jar);

		Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements())
		{
			JarEntry entry = entries.nextElement();
			String classFileName = entry.getName();
			if (classFileName.endsWith(".class"))
			{
                String className = classFileName.replace('/', '.').substring(0, classFileName.lastIndexOf('.'));
                Class c = classLoader.loadClass(className);
                map.put(c.getName(), c);
			}
		}
		return map;
	}
	
	/**
	 * Module run method.
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		List<URL> toBeHarvested = new ArrayList<URL>();
		HashMap<String,Class> classMap = new HashMap<String,Class>();
		resources.put(JavaHarvestRunner.CLASS_MAP, classMap); // make the classmap accesible to other modules

		try {
			// Harvest dummy classes
			toBeHarvested.add(((File) resources.get(CStarJavaCompiler.DUMMY_JAR)).toURL());
	
			// As well as all project dependencies
			for (File deps : resources.configuration().getProject().getFilesDependencies())
				toBeHarvested.add(deps.toURL());

			// Now harvest these jarFiles for type information
			for (URL jarFile : toBeHarvested)
				classMap.putAll(harvest(jarFile));
		}
		catch(Exception e) {
			throw new ModuleException("Error while harvesting types: " + e.getMessage(), "HARVESTER");
		}
	}
}// end class JavaHarvestRunner

