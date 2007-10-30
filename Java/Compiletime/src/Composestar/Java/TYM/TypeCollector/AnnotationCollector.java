package Composestar.Java.TYM.TypeCollector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Java.LAMA.JavaAnnotation;
import Composestar.Java.TYM.TypeHarvester.ClassMap;
import Composestar.Utils.Debug;

/**
 * Collects the annotations retrieved by the <code>Harvester</code>.
 */
public class AnnotationCollector implements CTCommonModule
{

	/**
	 * Module run method.
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		try
		{
			ClassMap classmap = ClassMap.instance();
			Map<String,Class> classes = classmap.map();
			for (Class c : classes.values())
			{
				try
				{
					fetchTypeAnnotations(c);
					fetchMethodAnnotations(c);
				}
				catch (Throwable t)
				{
					Debug.out(Debug.MODE_DEBUG, "COLLECTOR", "Error while fetching annotations from type: "
							+ c.getName() + " --> " + t.getMessage());
				}
			}
		}
		catch (Exception e)
		{
			throw new ModuleException(e.getMessage(), "AnnotationCollector");
		}
	}

	/**
	 * Fetches the method annotations of a class.
	 * 
	 * @param c - <code>Class</code> instance.
	 * @throws Throwable
	 */
	public void fetchMethodAnnotations(Class c) throws Throwable
	{
		Method[] methods = c.getMethods();
		for (Method method : methods)
		{
			Annotation[] annotations = method.getAnnotations();
			for (Annotation annotation : annotations)
			{
				JavaAnnotation annot = new JavaAnnotation();
				Type annotType = getTypeLocation(annotation.annotationType().getName());
				if (annotType != null)
				{
					annot.register(annotType, getMethodLocation(getTypeLocation(c.getName()), method.getName()));
				}

				// retrieving value
				String value = "";
				String annotStr = annotation.toString();
				// "@Composestar.Semantics(value=args.read)"
				value = annotStr.substring(annotStr.indexOf("=") + 1, annotStr.indexOf(")"));
				annot.setValue(value);
			}
		}
	}

	public void fetchTypeAnnotations(Class c)
	{
		//Debug.out(Debug.MODE_CRUCIAL, "AnnotationCollector", "Collecting annotations for type " + c.getName());
		Annotation[] annots = c.getAnnotations();
		for (Annotation annotation : annots)
		{
			Debug.out(Debug.MODE_CRUCIAL, "AnnotationCollector", "Found annotation " + annotation.annotationType().getName() + "(on " + c.getName() + ")");
			JavaAnnotation annot = new JavaAnnotation();
			Type annotType = getTypeLocation(annotation.annotationType().getName());
			if (annotType != null)
			{
				Debug.out(Debug.MODE_CRUCIAL, "AnnotationCollector", "Registering: " + annotType.getName() + " to " + getTypeLocation(c.getName()).getName());
				annot.register(annotType, getTypeLocation(c.getName()));
			}
		}
	}
	
	/**
	 * Locates a method. Returns a <code>MethodInfo</code> instance or null if
	 * not found.
	 * 
	 * @param type - the type to search in.
	 * @param methodName - the methodname.
	 * @throws Throwable
	 */
	public MethodInfo getMethodLocation(Type type, String methodName) throws Throwable
	{
		if (type != null)
		{
			List methods = type.getMethods();
			for (Object method1 : methods)
			{
				MethodInfo method = (MethodInfo) method1;
				if (method.Name.equals(methodName))
				{
					return method;
				}
			}
		}
		return null;
	}

	/**
	 * Locates a type. Returns a <code>Type</code> or null if not found.
	 * 
	 * @param typeName - fully qualified name of the type.
	 */
	public Type getTypeLocation(String typeName)
	{
		Concern c = (Concern) DataStore.instance().getObjectByID(typeName);
		if (c != null && c.getPlatformRepresentation() instanceof Type)
		{
			return (Type) c.getPlatformRepresentation();
		}
		return null;
	}
}
