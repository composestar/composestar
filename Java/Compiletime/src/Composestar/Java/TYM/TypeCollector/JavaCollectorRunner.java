package Composestar.Java.TYM.TypeCollector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import Composestar.Core.CpsRepository2.Concern;
import Composestar.Core.CpsRepository2.QualifiedRepositoryEntity;
import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.CpsRepository2.Meta.FileInformation;
import Composestar.Core.CpsRepository2.Meta.SourceInformation;
import Composestar.Core.CpsRepository2.References.ReferenceManager;
import Composestar.Core.CpsRepository2.References.TypeReference;
import Composestar.Core.CpsRepository2Impl.PrimitiveConcern;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.FieldInfo;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.UnitRegister;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.Java.LAMA.JavaFieldInfo;
import Composestar.Java.LAMA.JavaMethodInfo;
import Composestar.Java.LAMA.JavaParameterInfo;
import Composestar.Java.LAMA.JavaType;
import Composestar.Java.LAMA.JavaTypeResolver;
import Composestar.Java.TYM.TypeHarvester.JavaHarvestRunner;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Module that collects the types retrieved by the <code>HarvestRunner</code>.
 */
// @ComposestarModule(ID = ModuleNames.COLLECTOR, dependsOn = {
// ModuleNames.HARVESTER })
public class JavaCollectorRunner implements CTCommonModule
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.COLLECTOR);

	protected Map<String, Class<?>> pendingTypes;

	protected Map<String, Class<?>> processedTypes;

	private UnitRegister register;

	/**
	 * Default Constructor.
	 */
	public JavaCollectorRunner()
	{
		pendingTypes = new HashMap<String, Class<?>>();
		processedTypes = new HashMap<String, Class<?>>();
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getModuleName()
	 */
	public String getModuleName()
	{
		return ModuleNames.COLLECTOR;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getDependencies()
	 */
	public String[] getDependencies()
	{
		return new String[] { ModuleNames.HARVESTER };
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getImportance()
	 */
	public ModuleImportance getImportance()
	{
		return ModuleImportance.REQUIRED;
	}

	/**
	 * Module starting point.
	 */
	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		try
		{
			register = (UnitRegister) resources.get(UnitRegister.RESOURCE_KEY);
			if (register == null)
			{
				register = new UnitRegister();
				resources.put(UnitRegister.RESOURCE_KEY, register);
			}
			// iterate over classes
			Collection<Class<?>> classes = resources.get(JavaHarvestRunner.CLASS_MAP);
			for (Class<?> c : classes)
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
			throw new ModuleException(e.getMessage(), ModuleNames.COLLECTOR);
		}

		// int count = 0;
		// DataStore dataStore = resources.repository();
		// // loop through all current concerns, fetch implementation and remove
		// // from types map.
		// Iterator<Object> repIt = dataStore.getIterator();
		// while (repIt.hasNext())
		// {
		// Object next = repIt.next();
		// if (next instanceof CpsConcern)
		// {
		// CpsConcern concern = (CpsConcern) next;
		// // fetch implementation name
		// Object impl = concern.getImplementation();
		// String className = "";
		// if (impl == null)
		// {
		// className = concern.getQualifiedName();
		// if (!register.hasType(className))
		// {
		// continue;
		// }
		// }
		// else if (impl instanceof Source)
		// {
		// Source source = (Source) impl;
		// className = source.getClassName();
		// }
		// else if (impl instanceof SourceFile)
		// {
		// // TO DO: remove this?
		// SourceFile source = (SourceFile) impl;
		// String sourceFile = source.getSourceFile();
		// className = sourceFile.replaceAll("\\.\\w+", "");
		// }
		// else if (impl instanceof CompiledImplementation)
		// {
		// className = ((CompiledImplementation) impl).getClassName();
		// }
		// else
		// {
		// throw new ModuleException(
		// "Can only handle concerns with source file implementations or direct class links."
		// ,
		// ModuleNames.COLLECTOR);
		// }
		//
		// if (!concern.getQualifiedName().equals(className))
		// {
		// // implementation of a different class
		// Object otherConcern = dataStore.getObjectByID(className);
		// if (otherConcern instanceof CpsConcern)
		// {
		// logger.info("Implementation of " + concern +
		// " contains type info for " + otherConcern);
		// JavaType type = (JavaType) register.getType(className);
		// concern.setPlatformRepresentation(type);
		// type.setParentConcern((CpsConcern) otherConcern);
		// register.removeType(className);
		// }
		// continue;
		// }
		//
		// if (!register.hasType(className))
		// {
		// throw new ModuleException("Implementation: " + className +
		// " for concern: " + concern.getName()
		// + " not found!", ModuleNames.COLLECTOR);
		// }
		// JavaType type = (JavaType) register.getType(className);
		// concern.setPlatformRepresentation(type);
		// type.setParentConcern(concern);
		// register.removeType(className);
		// count++;
		// }
		// }

		Repository repository = resources.repository();
		ReferenceManager refman = resources.get(ReferenceManager.RESOURCE_KEY);
		// loop through rest of the concerns and add to the repository in the
		// form of primitive concerns
		for (Type type : register.getTypeMap().values())
		{
			TypeReference tref = refman.getTypeReference(type.getFullName());
			tref.setReference(type);
			if (repository.get(type.getFullName()) != null)
			{
				QualifiedRepositoryEntity o = repository.get(type.getFullName());
				if (o instanceof Concern)
				{
					((Concern) o).setTypeReference(tref);
					type.setConcern((Concern) o);
				}
				else
				{
					logger.error(String.format(
							"The repository already contains an entry with the name %s with type: %s", type
									.getFullName(), o.getClass()));
				}
				continue;
			}
			PrimitiveConcern pc = new PrimitiveConcern(type.getFullName().split("\\."));
			Composestar.Core.Config.Source typeSource =
					resources.configuration().getProject().getTypeMapping().getSource(type.getFullName());
			if (typeSource != null)
			{
				SourceInformation srcInfo = new SourceInformation(new FileInformation(typeSource.getFile()));
				pc.setSourceInformation(srcInfo);
			}
			pc.setTypeReference(tref);
			type.setConcern(pc);
			repository.add(pc);
		}

		register.resolveTypes(new JavaTypeResolver());
		return ModuleReturnValue.OK;
	}

	/**
	 * Processes a type. Adds the interfaces, superclasses, methods and fields.
	 * 
	 * @param c - <code>Class</code> instance.
	 * @throws Throwable
	 */
	private void processType(Class<?> c) throws Throwable
	{
		if (processedTypes.containsKey(c.getName()))
		{
			// this type is already processed
			return;
		}

		typeProcessed(c.getName(), c);

		JavaType jtype = new JavaType(c);
		jtype.setFullName(c.getName());
		register.registerLanguageUnit(jtype);

		// set name
		if (jtype.getFullName().lastIndexOf(".") > 0)
		{
			jtype.setName(jtype.getFullName().substring(jtype.getFullName().lastIndexOf(".") + 1,
					jtype.getFullName().length()));
		}
		else
		{
			jtype.setName(jtype.getFullName());
		}

		// add superclass
		Class<?> superclass = c.getSuperclass();
		if (superclass != null)
		{
			addPendingType(superclass.getName(), superclass);
		}

		// add interfaces
		Class<?>[] interfaces = c.getInterfaces();
		for (Class<?> anInterface : interfaces)
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
			if (!Modifier.isPrivate(method.getModifiers()))
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
		register.registerLanguageUnit(jmethod);
		jmethod.setName(m.getName());
		jmethod.setReturnType(m.getReturnType().getName());
		addPendingType(m.getReturnType().getName(), m.getReturnType());

		// add parameters
		Class<?>[] parameters = m.getParameterTypes();
		for (Class<?> parameter : parameters)
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
		register.registerLanguageUnit(jfield);
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
	private ParameterInfo processParameterInfo(Class<?> p) throws Throwable
	{

		JavaParameterInfo jparameter = new JavaParameterInfo(p);
		register.registerLanguageUnit(jparameter);
		jparameter.setName("");
		jparameter.setParameterType(p.getName());
		addPendingType(p.getName(), p);
		return jparameter;
	}

	/**
	 * Helper method.
	 */
	private void addPendingType(String key, Class<?> ptype)
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

		Iterator<Class<?>> pendingIt = pendingTypes.values().iterator();
		while (pendingIt.hasNext())
		{
			Class<?> pendingClass = pendingIt.next();
			processType(pendingClass);
			typeProcessed(pendingClass.getName(), pendingClass);
			pendingIt = pendingTypes.values().iterator();
		}
	}

	private void typeProcessed(String key, Class<?> type)
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
}
