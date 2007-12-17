package Composestar.Java.WEAVER;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import Composestar.Core.Config.Project;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Resources.CommonResources;

/**
 * A Class Weaver. Uses Javassist to transform a class.
 * 
 * @see javassist
 */
public class ClassWeaver
{
	private ClassPool classpool;

	protected CommonResources resources;

	/**
	 * Constructor. Creates a ClassPool.
	 * 
	 * @see javassist.ClassPool
	 */
	public ClassWeaver(CommonResources resc)
	{
		resources = resc;
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
	public void addClasspath(File classpath) throws ModuleException
	{
		try
		{
			classpool.insertClassPath(classpath.toString());
		}
		catch (NotFoundException n)
		{
			throw new ModuleException("Classpath (" + classpath + ") not found.", JavaWeaver.MODULE_NAME);
		}
	}

	/**
	 * Returns the output file of a class. It concatenates baseDir and the
	 * package of the class.
	 * 
	 * @param baseDir base directory
	 * @param clazz CtClass
	 */
	public File getOutputFile(File baseDir, CtClass clazz)
	{
		String fqName = clazz.getName();
		fqName = fqName.replace('.', File.separatorChar);
		return new File(baseDir, fqName + ".class");
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
	 * @see Composestar.Java.WEAVER.MethodBodyTransformer
	 */
	public void weave(Project p) throws ModuleException
	{
		List<File> weavedClasses = new ArrayList<File>();
		resources.put(JavaWeaver.WOVEN_CLASSES, weavedClasses);

		// write applicationStart
		writeApplicationStart();

		for (String typeName : p.getTypeMapping().getTypes())
		{
			File outputDir = new File(p.getIntermediate(), JavaWeaver.WEAVE_PATH);

			// weave the class and write to disk
			try
			{
				CtClass clazz = classpool.get(typeName);
				// FIXME: this is added because somehow javassist prunes and
				// frozens types from embedded sources. So temporarily disabled
				// weaving on embedded types.
				// if (!p.getTypeMapping().getSource(typeName).isEmbedded())
				// {
				clazz.instrument(new MethodBodyTransformer(classpool));
				clazz.writeFile(outputDir.toString());
				weavedClasses.add(getOutputFile(outputDir, clazz));
				// }
				// else
				// {
				// // simply copy the original file (for now)
				// File srcPath = (File) resources.get(JavaCompiler.SOURCE_OUT);
				// File dest = getOutputFile(outputDir, clazz);
				// FileUtils.copyFile(dest, getOutputFile(srcPath, clazz));
				// weavedClasses.add(dest);
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
				throw new ModuleException("Error while instrumenting " + typeName + ": " + e.getMessage(),
						JavaWeaver.MODULE_NAME);
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
		String startobject = resources.configuration().getProject().getMainclass();
		String rundebuglevel = resources.configuration().getSetting("runDebugLevel");
		try
		{
			CtClass clazz = classpool.get(startobject);
			CtMethod mainmethod = clazz.getMethod("main", "([Ljava/lang/String;)V");
			String src = "Composestar.RuntimeJava.FLIRT.JavaMessageHandlingFacility.handleJavaApplicationStart("
					+ "\"repository.dat\"" + "," + rundebuglevel + ", " + clazz.getName() + ".class);";
			mainmethod.insertBefore(src);
		}
		catch (Exception e)
		{
			throw new ModuleException("Error while trying to weave application start info: " + e.getCause() + " "
					+ e.getMessage(), "WEAVER");
		}
	}
}
