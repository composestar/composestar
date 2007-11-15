package Composestar.Java.TYM.TypeHarvester;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.TYM.TypeHarvester.HarvestRunner;
import Composestar.Java.COMP.CStarJavaCompiler;

/**
 * Module that harvest the classes from the compiled dummies.
 */
public class JavaHarvestRunner implements HarvestRunner
{
	public static final String CLASS_MAP = "ClassMap";

	/**
	 * Module run method.
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		List<URL> toBeHarvested = new ArrayList<URL>();
		HashSet<Class> classes = new HashSet<Class>();
		resources.put(JavaHarvestRunner.CLASS_MAP, classes); // make the set of classes accesible to other modules

		try {
			// Harvest dummy classes
			toBeHarvested.add(((File) resources.get(CStarJavaCompiler.DUMMY_JAR)).toURI().toURL());
	
			// As well as all project dependencies
			for (File deps : resources.configuration().getProject().getFilesDependencies())
				toBeHarvested.add(deps.toURI().toURL());

			// Now harvest these jarFiles for type information
			for (URL jarFile : toBeHarvested)
				classes.addAll(new JarHelper(jarFile).getClasses());
			
		}
		catch(Exception e) {
			throw new ModuleException("Error while harvesting types: " + e.getMessage(), "HARVESTER");
		}
	}
}// end class JavaHarvestRunner

