package Composestar.Java.TYM.TypeHarvester;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Dependency;
import Composestar.Core.TYM.TypeHarvester.HarvestRunner;
import Composestar.Utils.Debug;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class JavaHarvestRunner implements HarvestRunner {
	
	private ClassPathModifier cpm;
	private ClassMap cm;
	
	public JavaHarvestRunner() {
		cpm = new ClassPathModifier();
		cm = ClassMap.instance();
	}
	public void run(CommonResources resources) throws ModuleException {
				
		ArrayList dummyList = Configuration.instance().getProjects().getCompiledDummies();
		ArrayList dependencyList = Configuration.instance().getProjects().getDependencies();
		ArrayList toBeHarvested = new ArrayList();
		
		Iterator dummyIt = dummyList.iterator();
		while( dummyIt.hasNext() ) {
			String library = (String)dummyIt.next();
			try {
				cpm.addFile(library);
				toBeHarvested.add(library);
			}
			catch(Exception e) {
				throw new ModuleException("Error while updating classpath"+e.toString(), "HARVESTER");
			}
		}
		
		Iterator depsIt = dependencyList.iterator();
		while( depsIt.hasNext() ) {
			Dependency dep = (Dependency)depsIt.next();
			String library = (String)dep.getFileName();
			try {
				cpm.addFile(library);
				toBeHarvested.add(library);
			}
			catch(Exception e) {
				throw new ModuleException("Error while updating classpath"+e.toString(), "HARVESTER");
			}
		}
		
		Iterator libsIt = toBeHarvested.iterator();
		
		while( libsIt.hasNext() ) {
			
			String library = (String)libsIt.next();
			
			try {
				JarLoader jl = new JarLoader(library);
				HashMap classen = jl.getLoadedClasses(); 
				Iterator classIt = classen.keySet().iterator();
				while( classIt.hasNext() ) {
					Class c = (Class)classen.get(classIt.next());
					cm.addClass( c );
										
					//simpel testje
					Debug.out(Debug.MODE_DEBUG, "HARVESTER", "Class extracted:" + c.getName());
				}
			}
			catch(JarLoaderException e) {
				throw new ModuleException("Error while loading classes from "+library+": "+e.getMessage(), "HARVESTER");
			}
		}
	}

/** helper class **/
public class ClassPathModifier {
		
	private Class[] parameters = new Class[]{URL.class};
		 
	public void addFile(String s) throws IOException {
		File f = new File(s);
		addFile(f);
	}
	 
	public void addFile(File f) throws IOException {
		addURL(f.toURL());
	}
		 
	public void addURL(URL u) throws IOException {
			
		URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
		Class sysclass = URLClassLoader.class;
		 
		try {
			Method method = sysclass.getDeclaredMethod("addURL",parameters);
			method.setAccessible(true);
			method.invoke(sysloader,new Object[]{ u });
		} catch (Throwable t) {
			t.printStackTrace();
		throw new IOException("Error, could not add URL to system classloader");
		}
	}
}//end class ClassPathModifier
}//end class JavaHarvestRunner


