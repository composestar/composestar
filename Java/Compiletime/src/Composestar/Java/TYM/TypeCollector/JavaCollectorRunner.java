package Composestar.Java.TYM.TypeCollector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.PrimitiveConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.CompiledImplementation;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.Source;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.SourceFile;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.FieldInfo;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.TypeMap;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.TYM.TypeCollector.CollectorRunner;
import Composestar.Java.LAMA.JavaFieldInfo;
import Composestar.Java.LAMA.JavaMethodInfo;
import Composestar.Java.LAMA.JavaParameterInfo;
import Composestar.Java.LAMA.JavaType;
import Composestar.Java.TYM.TypeHarvester.JavaHarvestRunner;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Module that collects the types retrieved by the <code>HarvestRunner</code>.
 */
public class JavaCollectorRunner implements CollectorRunner
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	protected Map<String,Class> pendingTypes;

	protected Map<String,Class> processedTypes;

	/**
	 * Default Constructor.
	 */
	public JavaCollectorRunner()
	{
		pendingTypes = new HashMap<String,Class>();
		processedTypes = new HashMap<String,Class>();
	}

	/**
	 * Module starting point.
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		try
		{
			// iterate over classes
			Collection<Class> classes = (Collection<Class>)resources.get(JavaHarvestRunner.CLASS_MAP);
			for (Class c : classes)
			{
				try
				{
					processType(c);
				}
				catch (Throwable t)
				{
					logger.debug("Error while processing type: " + c.getName() + " --> " + t.getMessage(), t);
				}
			}
			try
			{
				processPendingTypes();
			}
			catch (Throwable t)
			{
				logger.debug(t.getMessage(), t);
			}
		}
		catch (Exception e)
		{
			throw new ModuleException(e.getMessage(), MODULE_NAME);
		}

		int count = 0;
		DataStore dataStore = DataStore.instance();
		HashMap typeMap = TypeMap.instance().map();
		// loop through all current concerns, fetch implementation and remove
		// from types map.
		Iterator repIt = dataStore.getIterator();
		while (repIt.hasNext())
		{
			Object next = repIt.next();
			if (next instanceof CpsConcern)
			{
				CpsConcern concern = (CpsConcern) next;
				// fetch implementation name
				Object impl = concern.getImplementation();
				String className = "";
				if (impl == null)
				{
					continue;
				}
				else if (impl instanceof Source)
				{
					Source source = (Source) impl;
					className = source.getClassName();
				}
				else if (impl instanceof SourceFile)
				{
					// TO DO: remove this?
					SourceFile source = (SourceFile) impl;
					String sourceFile = source.getSourceFile();
					className = sourceFile.replaceAll("\\.\\w+", "");
				}
				else if (impl instanceof CompiledImplementation)
				{
					className = ((CompiledImplementation) impl).getClassName();
				}
				else
				{
					throw new ModuleException(
							"Can only handle concerns with source file implementations or direct class links.",
							MODULE_NAME);
				}

				if (!concern.getQualifiedName().equals(className))
				{
					// implementation of a different class
					Object otherConcern = dataStore.getObjectByID(className);
					if (otherConcern instanceof CpsConcern)
					{
						logger.info("Implementation of " + concern + " contains type info for "
								+ ((CpsConcern) otherConcern));
						JavaType type = (JavaType) typeMap.get(className);
						concern.setPlatformRepresentation(type);
						type.setParentConcern((CpsConcern) otherConcern);
						typeMap.remove(className);
					}
					continue;
				}

				if (!typeMap.containsKey(className))
				{
					throw new ModuleException("Implementation: " + className + " for concern: " + concern.getName()
							+ " not found!", MODULE_NAME);
				}
				JavaType type = (JavaType) typeMap.get(className);
				concern.setPlatformRepresentation(type);
				type.setParentConcern(concern);
				typeMap.remove(className);
				count++;
			}
		}

		// loop through rest of the concerns and add to the repository in the
		// form of primitive concerns
		for (Object o1 : typeMap.values())
		{
			JavaType type = (JavaType) o1;
			PrimitiveConcern pc = new PrimitiveConcern();
			pc.setName(type.getFullName());
			pc.setPlatformRepresentation(type);
			type.setParentConcern(pc);
			dataStore.addObject(type.getFullName(), pc);
		}
	}

	/**
	 * Processes a type. Adds the interfaces, superclasses, methods and fields.
	 * 
	 * @param c - <code>Class</code> instance.
	 * @throws Throwable
	 */
	private void processType(Class c) throws Throwable
	{
		if (processedTypes.containsKey(c.getName()))
		{
			// this type is already processed
			return;
		}

		typeProcessed(c.getName(), c);

		TypeMap map = TypeMap.instance();
		JavaType jtype = new JavaType(c);
		jtype.setFullName(c.getName());

		map.addType(jtype.getFullName(), jtype);

		// set name
		if (jtype.getFullName().lastIndexOf(".") > 0)
		{
			jtype.setName(jtype.getFullName().substring(jtype.getFullName().lastIndexOf(".") + 1, jtype.getFullName().length()));
		}
		else
		{
			jtype.setName(jtype.getFullName());
		}

		// add superclass
		Class superclass = c.getSuperclass();
		if (superclass != null)
		{
			addPendingType(superclass.getName(), superclass);
		}

		// add interfaces
		Class[] interfaces = c.getInterfaces();
		for (Class anInterface : interfaces)
		{
			jtype.addImplementedInterface(anInterface.getName());
			addPendingType(anInterface.getName(), anInterface);
		}

		// add methods
		Method[] methods = c.getMethods();
		for (Method method : methods)
		{
			jtype.addMethod(processMethodInfo(method));
		}

		methods = c.getDeclaredMethods();
		for (Method method : methods)
		{
			if (Modifier.isPrivate(method.getModifiers()))
			{
				jtype.addMethod(processMethodInfo(method));
			}
		}

		// add fields
		Field[] fields = c.getFields();
		for (Field field : fields)
		{
			jtype.addField(processFieldInfo(field));
		}
	}

	/**
	 * Processes a method. Adds the parameters.
	 * 
	 * @param m - <code>Method</code> instance.
	 * @throws Throwable
	 */
	private MethodInfo processMethodInfo(Method m) throws Throwable
	{
		JavaMethodInfo jmethod = new JavaMethodInfo(m);
		jmethod.setName(m.getName());
		jmethod.setReturnType(m.getReturnType().getName());
		addPendingType(m.getReturnType().getName(), m.getReturnType());

		// add parameters
		Class[] parameters = m.getParameterTypes();
		for (Class parameter : parameters)
		{
			jmethod.addParameter(processParameterInfo(parameter));
		}
		return jmethod;
	}

	/**
	 * Processes a field.
	 * 
	 * @param f - <code>Field</code> instance.
	 * @throws Throwable
	 */
	private FieldInfo processFieldInfo(Field f) throws Throwable
	{

		JavaFieldInfo jfield = new JavaFieldInfo(f);
		jfield.setName(f.getName());
		jfield.setFieldType(f.getType().getName());
		addPendingType(f.getType().getName(), f.getType());
		return jfield;
	}

	/**
	 * Processes a parameter
	 * 
	 * @param p - <code>Class</code> instance.
	 * @throws Throwable
	 */
	private ParameterInfo processParameterInfo(Class p) throws Throwable
	{

		JavaParameterInfo jparameter = new JavaParameterInfo(p);
		jparameter.setName("");
		jparameter.setParameterType(p.getName());
		addPendingType(p.getName(), p);
		return jparameter;
	}

	/**
	 * Helper method.
	 */
	private void addPendingType(String key, Class ptype)
	{

		if (pendingTypes.containsKey(key))
		{
			// type already in list
			return;
		}
		if (processedTypes.containsKey(key))
		{
			// type doesn't need to be in the list
			return;
		}
		pendingTypes.put(key, ptype);
	}

	/**
	 * Processes all pending types.
	 * 
	 * @throws Throwable
	 */
	private void processPendingTypes() throws Throwable
	{

		Iterator pendingIt = pendingTypes.values().iterator();
		while (pendingIt.hasNext())
		{
			Class pendingClass = (Class) pendingIt.next();
			processType(pendingClass);
			typeProcessed(pendingClass.getName(), pendingClass);
			pendingIt = pendingTypes.values().iterator();
		}
	}

	private void typeProcessed(String key, Class type)
	{
		if (pendingTypes.containsKey(key))
		{
			pendingTypes.remove(key);
		}
		if (!processedTypes.containsKey(key))
		{
			processedTypes.put(key, type);
		}
	}

	// Added by INCRE.
	public void copyOperation(Vector dlls)
	{

	}
}
