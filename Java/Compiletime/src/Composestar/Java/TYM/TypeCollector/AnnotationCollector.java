package Composestar.Java.TYM.TypeCollector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Composestar.Core.Annotations.ComposestarModule;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.UnitRegister;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.Java.LAMA.JavaAnnotation;
import Composestar.Java.TYM.TypeHarvester.JavaHarvestRunner;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Collects the annotations retrieved by the Harvester.
 */
@ComposestarModule(ID = AnnotationCollector.MODULE_NAME, dependsOn = { ModuleNames.HARVESTER })
public class AnnotationCollector implements CTCommonModule
{
	public static final String MODULE_NAME = "AnnotationCollector";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger("AnnotationCollector");

	protected UnitRegister register;

	/**
	 * Module run method.
	 */
	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		register = resources.get(UnitRegister.RESOURCE_KEY);
		try
		{
			Collection<Class<?>> classes = resources.get(JavaHarvestRunner.CLASS_MAP);
			for (Class<?> c : classes)
			{
				try
				{
					fetchTypeAnnotations(c);
					fetchMethodAnnotations(c);
				}
				catch (Throwable t)
				{
					logger
							.info("Error while fetching annotations from type: " + c.getName() + " --> "
									+ t.getMessage());
				}
			}
		}
		catch (Exception e)
		{
			throw new ModuleException(e.getMessage(), MODULE_NAME);
		}
		return ModuleReturnValue.Ok;
	}

	/**
	 * Fetches the method annotations of a class.
	 * 
	 * @param c - <code>Class</code> instance.
	 * @throws Throwable
	 */
	public void fetchMethodAnnotations(Class<?> c) throws Throwable
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
				collectValues(annot, annotation);
			}
		}
	}

	public void fetchTypeAnnotations(Class<?> c)
	{
		// Debug.out(Debug.MODE_CRUCIAL, "AnnotationCollector", "Collecting
		// annotations for type " + c.getName());
		Annotation[] annots = c.getAnnotations();
		for (Annotation annotation : annots)
		{
			logger.debug("Found annotation " + annotation.annotationType().getName() + "(on " + c.getName() + ")");
			JavaAnnotation annot = new JavaAnnotation();
			Type annotType = getTypeLocation(annotation.annotationType().getName());
			if (annotType != null)
			{
				logger.debug("Registering: " + annotType.getName() + " to " + getTypeLocation(c.getName()).getName());
				annot.register(annotType, getTypeLocation(c.getName()));
			}
			collectValues(annot, annotation);
		}
	}

	public void collectValues(JavaAnnotation annot, Annotation annotation)
	{
		final Pattern namepat = Pattern.compile("\\s*([a-zA-Z0-9_]+)\\s*=");

		// annotation.toString():
		// @com.acme.util.Name(first=Alfred, middle=E., last=Neuman)
		String str = annotation.toString();
		int idxs = str.indexOf('(');
		int idxe = str.lastIndexOf(')');
		if (idxs > -1 && idxe > -1 && idxs < idxe - 1)
		{
			str = str.substring(idxs + 1, idxe);
			Matcher m = namepat.matcher(str);
			while (m.find())
			{
				String name = m.group(1);
				if (name != null && name.length() > 0)
				{
					try
					{
						Method method = annotation.annotationType().getDeclaredMethod(name, new Class[0]);
						Object val = method.invoke(annotation);
						if (val != null)
						{
							annot.setValue(name, val.toString());
						}
					}
					catch (Exception e)
					{
						logger.info(String.format("Exception harvesting annotation value for %s.%s", annotation
								.annotationType().getClass().getName(), name));
					}
				}
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
			List<MethodInfo> methods = type.getMethods();
			for (MethodInfo method : methods)
			{
				if (method.name.equals(methodName))
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
		return register.getType(typeName);
	}
}
