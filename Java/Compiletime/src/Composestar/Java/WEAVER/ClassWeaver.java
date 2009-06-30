package Composestar.Java.WEAVER;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import Composestar.Core.CONE.CONE;
import Composestar.Core.Config.Project;
import Composestar.Core.Config.Source;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Logging.CPSLogger;

/**
 * A Class Weaver. Uses Javassist to transform a class.
 * 
 * @see javassist
 */
public class ClassWeaver
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.WEAVER);

	private ClassPool classpool;

	protected CommonResources resources;

	protected HookDictionary hd;

	/**
	 * Constructor. Creates a ClassPool.
	 * 
	 * @see javassist.ClassPool
	 */
	public ClassWeaver(CommonResources resc, HookDictionary hookdict)
	{
		resources = resc;
		classpool = new ClassPool();
		classpool.appendSystemPath();
		hd = hookdict;
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
			throw new ModuleException("Classpath (" + classpath + ") not found.", ModuleNames.WEAVER);
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
	 * Searches for the most commonly used package name in the sources
	 * 
	 * @param p
	 * @return
	 */
	protected String findBasePackage(Project p)
	{
		Map<String, Integer> pkgCount = new HashMap<String, Integer>();
		String curPkg = "";
		int curCnt = 0;
		for (Source s : p.getSources())
		{
			for (String type : p.getTypeMapping().getTypes(s))
			{
				String[] fqn = type.split("\\.");
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < fqn.length - 1; i++)
				{
					if (sb.length() > 0)
					{
						sb.append(".");
					}
					sb.append(fqn[i]);
					String cur = sb.toString();
					int cnt = 1;
					if (pkgCount.containsKey(cur))
					{
						cnt = pkgCount.get(cur) + 1;
					}
					pkgCount.put(cur, cnt);
					if (cnt > curCnt)
					{
						curPkg = cur;
						curCnt = cnt;
					}
				}
			}
		}
		return curPkg;
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

		File outputDir = new File(p.getIntermediate(), JavaWeaver.WEAVE_PATH);

		Set<CtClass> classes = new HashSet<CtClass>();
		for (String typeName : p.getTypeMapping().getTypes())
		{
			try
			{
				CtClass clazz = classpool.get(typeName);
				classes.add(clazz);
				classes.addAll(Arrays.asList(clazz.getNestedClasses()));
			}
			catch (Exception e)
			{
				e.printStackTrace();
				throw new ModuleException("Error while instrumenting " + typeName + ": " + e.getMessage(),
						ModuleNames.WEAVER);
			}
		}

		// create the initializer class
		StringBuffer sb = new StringBuffer();
		String pkgName = findBasePackage(p);
		if (pkgName != null && pkgName.length() > 0)
		{
			sb.append(pkgName);
		}
		String projectName = p.getName();
		if (projectName != null && projectName.length() > 0)
		{
			if (sb.length() > 0)
			{
				sb.append(".");
			}
			sb.append(projectName);
		}
		sb.append("__composestar$init");
		String initClassName = sb.toString();
		logger.debug(String.format("Initializer classname = %s", initClassName));
		weavedClasses.add(writeRTInitializer(initClassName, outputDir));

		for (CtClass clazz : classes)
		{
			// weave the class and write to disk
			try
			{
				clazz.instrument(new MethodBodyTransformer(classpool, hd, resources.repository()));
				if (clazz.isModified())
				{
					// add a reference to the static class initializer so that
					// the runtime will be initialized when this modified class
					// is loaded. We call a static final method so that this
					// code won't be optimized away by a JIT
					CtConstructor cinit = clazz.getClassInitializer();
					if (cinit == null)
					{
						cinit = clazz.makeClassInitializer();
					}
					cinit.insertBefore(String.format("%s.init();", initClassName));
				}

				clazz.writeFile(outputDir.toString());
				File outfile = getOutputFile(outputDir, clazz);
				weavedClasses.add(outfile);
				logger.debug(String.format("Wrote file %s", outfile.toString()));
			}
			catch (Exception e)
			{
				e.printStackTrace();
				throw new ModuleException("Error while instrumenting " + clazz.getName() + ": " + e.getMessage(),
						ModuleNames.WEAVER);
			}
		}
	}

	/**
	 * Create the special initializer class. This class will initialize the
	 * runtime when its loaded.
	 */
	protected File writeRTInitializer(String initClassName, File outputDir) throws ModuleException
	{
		CtClass clazz = classpool.makeClass(initClassName);
		int rundebuglevel;
		try
		{
			rundebuglevel = Integer.parseInt(resources.configuration().getSetting("runDebugLevel"));
		}
		catch (NumberFormatException e)
		{
			rundebuglevel = 0;
		}
		File repository = resources.get(CONE.REPOSITORY_FILE_KEY);
		boolean useThreaded = Boolean.parseBoolean(resources.configuration().getSetting("FLIRT.threaded"));
		try
		{
			clazz.setModifiers(Modifier.PUBLIC | Modifier.FINAL);

			StringBuilder source = new StringBuilder();
			if (useThreaded)
			{
				logger.debug("Setting interpreter to use threaded interpreter");
				source.append("Composestar.Java.FLIRT.Interpreter.InterpreterMain.setInterpreterMode(true);");
			}
			source.append("Composestar.Java.FLIRT.MessageHandlingFacility.handleApplicationStart(\"");
			source.append(repository.getName().replaceAll("\"", "\\\"")); // filename
			source.append("\", ");
			source.append(rundebuglevel);
			source.append(", ");
			source.append(clazz.getName());
			source.append(".class);");

			CtConstructor initCtor = clazz.getClassInitializer();
			if (initCtor == null)
			{
				initCtor = clazz.makeClassInitializer();
			}
			initCtor.insertBefore(source.toString());

			// add the empty init method which is called by others, actual
			// initialization is done by the class constructor
			CtMethod dummyInit = CtNewMethod.make("public static final void init() {}", clazz);
			clazz.addMethod(dummyInit);
		}
		catch (Exception e)
		{
			throw new ModuleException("Error while trying to create runtime initializer: " + e.getCause() + " "
					+ e.getMessage(), ModuleNames.WEAVER, e);
		}

		try
		{
			clazz.writeFile(outputDir.toString());
		}
		catch (Exception e)
		{
			throw new ModuleException("Error creating initialization class " + initClassName + ": " + e.getMessage(),
					ModuleNames.WEAVER);
		}
		File initClassFile = getOutputFile(outputDir, clazz);
		logger.debug(String.format("Wrote file %s", initClassFile.toString()));
		return initClassFile;
	}
}
