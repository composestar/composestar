package Composestar.Java.TYM.TypeHarvester;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Dependency;
import Composestar.Core.TYM.TypeHarvester.HarvestRunner;
import Composestar.Utils.Debug;

/**
 * Module that harvest the classes from the compiled dummies.
 */
public class JavaHarvestRunner implements HarvestRunner
{

	private ClassPathModifier cpm; // helper object to modify the classpath.

	private ClassMap cm; // contains the harvested classes.

	/**
	 * Default constructor. 
	 */
	public JavaHarvestRunner()
	{
		cpm = new ClassPathModifier();
		cm = ClassMap.instance();
	}

	/**
	 * Module run method. 
	 */
	public void run(CommonResources resources) throws ModuleException
	{

		ArrayList dummyList = (ArrayList) Configuration.instance().getProjects().getCompiledDummies();
		ArrayList dependencyList = (ArrayList) Configuration.instance().getProjects().getDependencies();
		ArrayList toBeHarvested = new ArrayList();

		Iterator dummyIt = dummyList.iterator();
        for (Object aDummyList : dummyList) {
            String library = (String) aDummyList;
            try {
                cpm.addFile(library);
                toBeHarvested.add(library);
            }
            catch (Exception e) {
                throw new ModuleException("Error while updating classpath" + e.toString(), "HARVESTER");
            }
        }

        Iterator depsIt = dependencyList.iterator();
        for (Object aDependencyList : dependencyList) {
            Dependency dep = (Dependency) aDependencyList;
            String library = (String) dep.getFileName();
            try {
                cpm.addFile(library);
                toBeHarvested.add(library);
            }
            catch (Exception e) {
                throw new ModuleException("Error while updating classpath" + e.toString(), "HARVESTER");
            }
        }

        Iterator libsIt = toBeHarvested.iterator();

        for (Object aToBeHarvested : toBeHarvested) {

            String library = (String) aToBeHarvested;

            try {
                JarLoader jl = new JarLoader(library);
                HashMap classen = jl.getLoadedClasses();
                Iterator classIt = classen.keySet().iterator();
                for (Object o : classen.keySet()) {
                    Class c = (Class) classen.get(o);
                    cm.addClass(c);

                    Debug.out(Debug.MODE_DEBUG, "HARVESTER", "Class extracted:" + c.getName());
                }
            }
            catch (JarLoaderException e) {
                throw new ModuleException("Error while loading classes from " + library + ": " + e.getMessage(),
                        "HARVESTER");
            }
        }
    }

	/** 
	 * Helper class. A 'hack' to adjust the classpath in runtime. 
	 */
	public class ClassPathModifier
	{

		private Class[] parameters = new Class[] { URL.class };

		public void addFile(String s) throws IOException
		{
			File f = new File(s);
			addFile(f);
		}

		public void addFile(File f) throws IOException
		{
			addURL(f.toURL());
		}

		public void addURL(URL u) throws IOException
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

