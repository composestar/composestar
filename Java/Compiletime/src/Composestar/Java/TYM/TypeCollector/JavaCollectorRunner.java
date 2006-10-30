package Composestar.Java.TYM.TypeCollector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.PrimitiveConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.CompiledImplementation;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.Source;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.SourceFile;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.*;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.TYM.TypeCollector.CollectorRunner;
import Composestar.Java.LAMA.*;
import Composestar.Java.TYM.TypeHarvester.ClassMap;
import Composestar.Utils.Debug;

/**
 * Module that collects the types retrieved by the <code>HarvestRunner</code>.
 */
public class JavaCollectorRunner implements CollectorRunner
{

	HashMap pendingTypes;

	HashMap processedTypes;

	/**
	 * Default Constructor.
	 */
	public JavaCollectorRunner()
	{
		pendingTypes = new HashMap();
		processedTypes = new HashMap();
	}

	/**
	 * Module starting point.
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		try
		{
			// iterate over classes
			ClassMap cm = ClassMap.instance();
			HashMap classes = cm.map();
			Iterator classIt = classes.values().iterator();
			while (classIt.hasNext())
			{
				Class c = (Class) classIt.next();
				try
				{
					processType(c);
				}
				catch (Throwable t)
				{
					Debug.out(Debug.MODE_DEBUG, "COLLECTOR", "Error while processing type: " + c.getName() + " --> "
							+ t.getMessage());
				}
			}
			try
			{
				processPendingTypes();
			}
			catch (Throwable t)
			{
				Debug.out(Debug.MODE_DEBUG, "COLLECTOR", t.getMessage());
			}
		}
		catch (Exception e)
		{
			throw new ModuleException(e.getMessage(), "COLLECTOR");
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
					// fixes the problem with the embedded code not being in the
					// type map at all.
					continue;
					// Source source = (Source)impl;
					// className = source.getClassName();
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
							"CollectorRunner: Can only handle concerns with source file implementations or direct class links.");
				}

				if (!typeMap.containsKey(className))
				{
					throw new ModuleException("Implementation: " + className + " for concern: " + concern.getName()
							+ " not found!");
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
		Iterator it = typeMap.values().iterator();
		while (it.hasNext())
		{
			JavaType type = (JavaType) it.next();
			PrimitiveConcern pc = new PrimitiveConcern();
			pc.setName(type.fullName());
			pc.setPlatformRepresentation(type);
			type.setParentConcern(pc);
			dataStore.addObject(type.fullName(), pc);
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

		map.addType(jtype.fullName(), jtype);

		// set name
		if (jtype.fullName().lastIndexOf(".") > 0)
		{
			jtype.setName(jtype.fullName().substring(jtype.fullName().lastIndexOf(".") + 1, jtype.fullName().length()));
		}
		else
		{
			jtype.setName(jtype.fullName());
		}

		// add superclass
		Class superclass = c.getSuperclass();
		if (superclass != null)
		{
			addPendingType(superclass.getName(), superclass);
		}

		// add interfaces
		Class[] interfaces = c.getInterfaces();
		for (int i = 0; i < interfaces.length; i++)
		{
			jtype.addImplementedInterface(interfaces[i].getName());
			addPendingType(interfaces[i].getName(), interfaces[i]);
		}

		// add methods
		Method[] methods = c.getMethods();
		for (int i = 0; i < methods.length; i++)
		{
			jtype.addMethod(processMethodInfo(methods[i]));
		}

		// add fields
		Field[] fields = c.getFields();
		for (int i = 0; i < fields.length; i++)
		{
			jtype.addField(processFieldInfo(fields[i]));
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
		for (int i = 0; i < parameters.length; i++)
		{
			jmethod.addParameter(processParameterInfo(parameters[i]));
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
