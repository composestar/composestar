package Composestar.Java.WEAVER;

import java.util.Iterator;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Project;
import Composestar.Core.Master.Config.TypeSource;

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
		String outputFile;

		// write applicationStart
		// writeApplicationStart();

		Iterator typeIt = p.getTypeSources().iterator();
		while (typeIt.hasNext())
		{
			type = (TypeSource) typeIt.next();
			name = type.getName();

			// create outputFile
			outputFile = p.getProperty("basePath");
			outputFile += "obj/weaver/";

			// weave the class and write to disk
			try
			{
				CtClass clazz = classpool.get(name);
				clazz.instrument(new MethodBodyTransformer());
				clazz.writeFile(outputFile);
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
			String src = "Composestar.RuntimeJava.FLIRT.JavaMessageHandlingFacility.handleApplicationStart("
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
