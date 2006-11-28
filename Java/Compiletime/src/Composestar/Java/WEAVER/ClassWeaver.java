package Composestar.Java.WEAVER;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Project;
import Composestar.Core.Master.Config.Source;
import Composestar.Core.Master.Config.TypeSource;
import Composestar.Core.TYM.TypeLocations;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.FileUtils;

/**
 * A Class Weaver. Uses Javassist to transform a class.
 * 
 * @see javassist
 */
public class ClassWeaver
{
	private ClassPool classpool;

	/**
	 * Constructor. Creates a ClassPool.
	 * 
	 * @see javassist.ClassPool
	 */
	public ClassWeaver()
	{
		classpool = new ClassPool();
		classpool.appendSystemPath();
	}

	/**
	 * Adds a directory or jar file to the beginning of the search path
	 * (classpath).
	 * 
	 * @see javassist.ClassPool#insertClassPath(java.lang.String)
	 * @throws ModuleException : When the specified classpath cannot be found.
	 */
	public void addClasspath(String classpath) throws ModuleException
	{
		try
		{
			classpool.insertClassPath(classpath);
		}
		catch (NotFoundException n)
		{
			throw new ModuleException("Classpath (" + classpath + ") not found.", "WEAVER");
		}
	}

	/**
	 * Returns the output file of a class.
	 * It concatenates baseDir and the package of the class.
	 * 
	 * @param baseDir base directory
	 * @param clazz CtClass
	 */
	public String getOutputFile(String baseDir, CtClass clazz)
	{
		String outputFile = baseDir;
		String fqName = clazz.getName();
		fqName = fqName.replace('.',java.io.File.separatorChar);
		outputFile += fqName + ".class";
		return FileUtils.normalizeFilename(outputFile);
	}
	
	/**
	 * Weaves a project.
	 * <p>
	 * 1. Adds the application start info to the Main Class.
	 * <p>
	 * 2. Instruments every TypeSource located in the Project with a
	 * MethodBodyTransformer.
	 * 
	 * @param p the project to be weaved
	 * @throws ModuleException : When instrumenting a class fails, e.g. wrong
	 *             source code added.
	 * @see Composestar.Core.Master.Config.TypeSource
	 * @see Composestar.Core.Master.Config.Project
	 * @see Composestar.Java.WEAVER.MethodBodyTransformer
	 */
	public void weave(Project p) throws ModuleException
	{
		TypeSource type;
		String name;
		String outputDir;
		
		List weavedClasses = new ArrayList();
		DataStore.instance().addObject("WeavedClasses", weavedClasses);

		// write applicationStart
		writeApplicationStart();
		
		// FIXME: this is added because somehow javassist prunes and frozens
		// types from embedded sources. So temporarily disabled weaving on embedded types.
		boolean isEmbeddedType = false;
		TypeLocations types = TypeLocations.instance();
		
		Iterator typeIt = p.getTypeSources().iterator();
		while (typeIt.hasNext())
		{
			type = (TypeSource) typeIt.next();
			name = type.getName();
			
			// FIXME: this is added because somehow javassist prunes and frozens
			// types from embedded sources. So temporarily disabled weaving on embedded types.
			String s = FileUtils.normalizeFilename(types.getSourceByType(name));
			Iterator sourceIt = p.getSources().iterator();
			while(sourceIt.hasNext())
			{
				Source source = (Source)sourceIt.next();
				if(s.equals(source.getFileName()))
				{
					if(source.isEmbedded())
					{
						isEmbeddedType = true;
					}
				}
			}
			
			// create outputFile
			outputDir = p.getBasePath();
			outputDir += "obj/weaver/";

			// weave the class and write to disk
			try
			{
				CtClass clazz = classpool.get(name);
				if(!isEmbeddedType) // FIXME: enable weaving on embedded types
				{
					clazz.instrument(new MethodBodyTransformer(classpool));
					clazz.writeFile(outputDir);
					weavedClasses.add(getOutputFile(outputDir,clazz));
				}
				else
				{
					weavedClasses.add(getOutputFile(p.getBasePath() + "obj/",clazz));
					isEmbeddedType = false;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				throw new ModuleException("Error while instrumenting " + name + ": " + e.getMessage(), "WEAVER");
			}
		}
	}

	/**
	 * Writes the application start info in the Main Class.
	 * 
	 * @throws ModuleException : e.g. when main method is not found.
	 */
	public void writeApplicationStart() throws ModuleException
	{
		Configuration config = Configuration.instance();
		String startobject = config.getProjects().getProperty("applicationStart");
		String rundebuglevel = config.getProjects().getProperty("runDebugLevel");
		try
		{
			CtClass clazz = classpool.get(startobject);
			CtMethod mainmethod = clazz.getMethod("main", "([Ljava/lang/String;)V");
			String src = "Composestar.RuntimeJava.FLIRT.JavaMessageHandlingFacility.handleJavaApplicationStart("
					+ "\"repository.dat\"" + "," + rundebuglevel + ");";
			mainmethod.insertBefore(src);
		}
		catch (Exception e)
		{
			throw new ModuleException("Error while trying to weave application start info: " + e.getCause() + " "
					+ e.getMessage(), "WEAVER");
		}
	}
}
