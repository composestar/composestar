package Composestar.Java.TYM.SignatureTransformer;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import Composestar.Core.CpsRepository2.Concern;
import Composestar.Core.CpsRepository2.Signatures.MethodRelation;
import Composestar.Core.CpsRepository2.Signatures.Signature;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Java.LAMA.JavaMethodInfo;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Class that modifies a class. It uses javassist to perform the changes.
 * 
 * @see javassist.
 */
public class ClassModifier
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger("SITRA");

	private ClassPool classpool;

	private List<File> pathList;

	/**
	 * Constructor.
	 */
	public ClassModifier()
	{
		classpool = ClassPool.getDefault();
		pathList = new ArrayList<File>();
	}

	/**
	 * Adds a list of methods to a class.
	 * 
	 * @param methods - the methods to be added
	 * @param ct - the class
	 * @throws Exception - when an error occurs while trying to add the methods
	 *             to the class.
	 */
	public void addMethods(Collection<MethodInfo> methods, CtClass ct) throws Exception
	{
		for (MethodInfo m : methods)
		{
			Method theMethod = ((JavaMethodInfo) m).theMethod;
			int modifiers = theMethod.getModifiers();
			CtClass returnClass = null;
			if (!m.returnTypeString.equals(""))
			{
				returnClass = findClass(m.returnTypeString);
			}
			String methodName = m.getName();
			CtClass[] parameters = new CtClass[m.parameters.size()];
			if (m.parameters.size() > 0)
			{
				Class<?>[] params = theMethod.getParameterTypes();
				for (int i = 0; i < params.length; i++)
				{
					String name = params[i].getName();
					CtClass clazz = findClass(name);
					parameters[i] = clazz;
				}
			}
			CtClass[] exceptions = new CtClass[0];
			CtMethod newMethod = CtNewMethod.make(modifiers, returnClass, methodName, parameters, exceptions, null, ct);
			logger.info("method " + newMethod.getName() + " added to dummy class " + ct.getName());
			ct.addMethod(newMethod);
		}

	}

	/**
	 * Deletes a list of methods from a class.
	 * 
	 * @param methods - the methods to be deleted.
	 * @param ct - the class.
	 * @throws Exception - e.g. when method doesn't exist.
	 */
	public void deleteMethods(List<JavaMethodInfo> methods, CtClass ct) throws Exception
	{
		for (JavaMethodInfo m : methods)
		{
			CtClass[] parameters = new CtClass[m.parameters.size()];
			if (m.parameters.size() > 0)
			{
				Class<?>[] params = m.theMethod.getParameterTypes();
				for (int i = 0; i < params.length; i++)
				{
					String name = params[i].getName();
					CtClass clazz = findClass(name);
					parameters[i] = clazz;
				}
			}

			try
			{
				CtMethod method = ct.getDeclaredMethod(m.getName(), parameters);
				ct.removeMethod(method);
			}
			catch (Exception e)
			{
				// swallow not found exception: can occur when method
				// should be removed which is not declared in the class
				// but inherited from superclass
			}
		}
	}

	/**
	 * Returns a CtClass instance if the class is found.
	 * 
	 * @param classname - the fully qualified name of the class.
	 * @throws Exception - when class cannot be found.
	 */
	public CtClass findClass(String classname) throws Exception
	{
		CtClass ct = classpool.get(classname);
		return ct;
	}

	/**
	 * Modifies a class. A classpath needs to be given in order to find the
	 * class.
	 * 
	 * @param c - class wrapped inside a <code>ClassWrapper</code>.
	 * @param classpath - classpath.
	 * @throws Exception - e.g. when class cannot be found.
	 */
	public void modifyClass(ClassWrapper c, File classpath) throws Exception
	{

		if (!pathList.contains(classpath))
		{
			// insert classpath
			classpool.insertClassPath(classpath.toString());
		}

		// load class
		CtClass ct = classpool.get(c.getClazz().getName());

		// make adjustments
		Concern concern = c.getConcern();
		Signature signature = concern.getSignature();

		if (signature != null)
		{
			Collection<MethodInfo> methods = signature.getMethods(MethodRelation.ADDED);
			if (methods.size() > 0)
			{
				addMethods(methods, ct);
			}
		}

		byte[] bytecode = ct.toBytecode();
		c.setByteCode(bytecode);
	}
}
