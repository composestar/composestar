package Composestar.Java.WEAVER;

import java.lang.reflect.Method;
import java.util.Iterator;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtPrimitiveType;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;
import javassist.expr.Cast;
import javassist.expr.ConstructorCall;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.Handler;
import javassist.expr.Instanceof;
import javassist.expr.MethodCall;
import javassist.expr.NewArray;
import javassist.expr.NewExpr;
import Composestar.Core.CpsRepository2.Concern;
import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.Signatures.Signature;
import Composestar.Core.Master.ModuleNames;
import Composestar.Java.LAMA.JavaMethodInfo;
import Composestar.Utils.Logging.CPSLogger;

/**
 * A transformer for method bodies.
 * <p>
 * Expressions in the method bodies are replaced with interpreter calls.
 * 
 * @see javassist.expr.ExprEditor
 */
public class MethodBodyTransformer extends ExprEditor
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.WEAVER);

	private ClassPool classpool;

	private HookDictionary hd;

	private Repository repos;

	public MethodBodyTransformer(ClassPool classpool, HookDictionary hookdict, Repository repository)
	{
		this.classpool = classpool;
		hd = hookdict;
		repos = repository;
	}

	/**
	 * Edits an expression for explicit type casting.
	 * 
	 * @see javassist.expr.ExprEditor#edit(Cast)
	 */
	@Override
	public void edit(Cast c) throws CannotCompileException
	{
		try
		{
			if (hd.isCastInterception(c.getType().getName()))
			{
				c.replace("$_ = $proceed(Composestar.Java.FLIRT.CastingFacility.handleCast($1" + ", " + '"'
						+ c.getType().getName() + '"' + "));");
			}
		}
		catch (NotFoundException nfe)
		{
			logger.debug("Class not found: " + nfe.getMessage());
		}
	}

	/**
	 * Edits a constructor call. The constructor call is either super() or
	 * this() included in a constructor body.
	 * 
	 * @see javassist.expr.ExprEditor#edit(ConstructorCall)
	 */
	@Override
	public void edit(ConstructorCall c) throws CannotCompileException
	{

	}

	/**
	 * Edits a field-access expression. Field access means both read and write.
	 * 
	 * @see javassist.expr.ExprEditor#edit(FieldAccess)
	 */
	@Override
	public void edit(FieldAccess f) throws CannotCompileException
	{

	}

	/**
	 * Edits a catch clause.
	 * 
	 * @see javassist.expr.ExprEditor#edit(Handler)
	 */
	@Override
	public void edit(Handler h) throws CannotCompileException
	{

	}

	/**
	 * Edits an instanceof expression.
	 * 
	 * @see javassist.expr.ExprEditor#edit(Instanceof)
	 */
	@Override
	public void edit(Instanceof i) throws CannotCompileException
	{

	}

	/**
	 * Edits a method call.
	 * 
	 * @see javassist.expr.ExprEditor#edit(MethodCall)
	 */
	@Override
	public void edit(MethodCall m) throws CannotCompileException
	{
		String target = m.getClassName();
		String caller = m.where().getDeclaringClass().getName();

		try
		{
			if (hd.isMethodInterception(target, caller))
			{
				String signature = m.getSignature();
				CtClass returnType = Descriptor.getReturnType(signature, classpool);
				if (returnType == CtClass.voidType)
				{
					replaceVoidMethodCall(m);
				}
				else
				{
					replaceReturnMethodCall(m);
				}
			}
		}
		catch (NotFoundException nfe)
		{
			logger.debug("Method not found: " + nfe.getMessage());
		}
	}

	/**
	 * Edits an expression for array creation.
	 * 
	 * @see javassist.expr.ExprEditor#edit(NewArray)
	 */
	@Override
	public void edit(NewArray a) throws CannotCompileException
	{

	}

	/**
	 * Edits a new expression.
	 * 
	 * @see javassist.expr.ExprEditor#edit(NewExpr)
	 */
	@Override
	public void edit(NewExpr e) throws CannotCompileException
	{
		if (hd.isAfterInstantationInterception(e.getClassName()))
		{
			String.format("", new Object[] { "", "", "" });
			int mod = e.where().getModifiers();
			boolean isStaticCaller = Modifier.isStatic(mod);
			if (isStaticCaller)
			{
				e.replace("{$_ = $proceed($$); Composestar.Java.FLIRT.MessageHandlingFacility.handleInstanceCreation("
						+ '"' + e.where().getName() + '"' + ",$_,$args, null);}");
			}
			else
			{
				e
						.replace("{$_ = $proceed($$); Composestar.Java.FLIRT.MessageHandlingFacility.handleInstanceCreation(this,$_,$args, null);}");
			}
		}
	}

	/**
	 * Replaces a void method call
	 * 
	 * @param m the method call
	 * @throws CannotCompileException
	 */
	public void replaceVoidMethodCall(MethodCall m) throws CannotCompileException, NotFoundException
	{
		int mod = m.where().getModifiers();
		boolean isStaticCaller = Modifier.isStatic(mod);
		boolean isStaticTarget = isStaticTarget(m);

		if (isStaticCaller && isStaticTarget)
		{
			// static caller, static target
			m.replace("Composestar.Java.FLIRT.MessageHandlingFacility.handleVoidMethodCall(" + '"'
					+ m.where().getDeclaringClass().getName() + '"' + "," + '"' + m.getClassName() + '"' + "," + '"'
					+ m.getMethodName() + '"' + ",$args, \"" + getMethodHashKey(m) + "\");");
		}
		else if (isStaticCaller && !isStaticTarget)
		{
			// static caller, non-static target
			m.replace("Composestar.Java.FLIRT.MessageHandlingFacility.handleVoidMethodCall(" + '"'
					+ m.where().getDeclaringClass().getName() + '"' + ",$0," + '"' + m.getMethodName() + '"'
					+ ",$args, \"" + getMethodHashKey(m) + "\");");
		}
		else if (!isStaticCaller && isStaticTarget)
		{
			// non-static caller, static target
			m.replace("Composestar.Java.FLIRT.MessageHandlingFacility.handleVoidMethodCall(this," + '"'
					+ m.getClassName() + '"' + "," + '"' + m.getMethodName() + '"' + ",$args, \"" + getMethodHashKey(m)
					+ "\");");
		}
		else
		{
			// non-static caller, non-static target
			m.replace("Composestar.Java.FLIRT.MessageHandlingFacility.handleVoidMethodCall(this,$0," + '"'
					+ m.getMethodName() + '"' + ",$args, \"" + getMethodHashKey(m) + "\");");
		}
	}

	/**
	 * Replaces a return method call
	 * 
	 * @param m the method call
	 * @throws CannotCompileException
	 */
	public void replaceReturnMethodCall(MethodCall m) throws CannotCompileException, NotFoundException
	{
		int mod = m.where().getModifiers();
		boolean isStaticCaller = Modifier.isStatic(mod);
		boolean isStaticTarget = isStaticTarget(m);

		if (isStaticCaller && isStaticTarget)
		{
			// static caller, static target
			m.replace("$_ = ($r)" + "Composestar.Java.FLIRT.MessageHandlingFacility.handleReturnMethodCall(" + '"'
					+ m.where().getDeclaringClass().getName() + '"' + "," + '"' + m.getClassName() + '"' + "," + '"'
					+ m.getMethodName() + '"' + ",$args, \"" + getMethodHashKey(m) + "\");");
		}
		else if (isStaticCaller && !isStaticTarget)
		{
			// static caller, non-static target
			m.replace("$_ = ($r)" + "Composestar.Java.FLIRT.MessageHandlingFacility.handleReturnMethodCall(" + '"'
					+ m.where().getDeclaringClass().getName() + '"' + ",$0," + '"' + m.getMethodName() + '"'
					+ ",$args, \"" + getMethodHashKey(m) + "\");");
		}
		else if (!isStaticCaller && isStaticTarget)
		{
			// non-static caller, static target
			m.replace("$_ = ($r)" + "Composestar.Java.FLIRT.MessageHandlingFacility.handleReturnMethodCall(this," + '"'
					+ m.getClassName() + '"' + "," + '"' + m.getMethodName() + '"' + ",$args, \"" + getMethodHashKey(m)
					+ "\");");
		}
		else
		{
			// non-static caller, non-static target
			m.replace("$_ = ($r)" + "Composestar.Java.FLIRT.MessageHandlingFacility.handleReturnMethodCall(this,$0,"
					+ '"' + m.getMethodName() + '"' + ",$args, \"" + getMethodHashKey(m) + "\");");
		}
	}

	/**
	 * Helper Method.
	 */
	private boolean isStaticTarget(MethodCall m)
	{
		// first try: bytecode search
		try
		{
			int mod = m.getMethod().getModifiers();
			return Modifier.isStatic(mod);
		}
		catch (NotFoundException nfe)
		{
			// no method declaration found in bytecode.
		}

		// second try: lookup in concern's Signature (it could be added by SIGN)
		Concern c = repos.get(m.getClassName(), Concern.class);
		if (c != null)
		{
			Signature s = c.getTypeReference().getReference().getSignature();
			Iterator<MethodInfo> methods = s.getMethods().iterator();
			if (methods.hasNext())
			{
				JavaMethodInfo method = (JavaMethodInfo) methods.next();
				if (method.getName().equals(m.getMethodName()))
				{
					// equal parameters?
					if (equalParameters(m, method))
					{
						// is static?
						Method theMethod = method.theMethod;
						int mod = theMethod.getModifiers();
						return Modifier.isStatic(mod);
					}
				}
			}
		}
		return false;
	}

	/**
	 * Helper Method.
	 */
	private boolean equalParameters(MethodCall m, MethodInfo method)
	{
		try
		{
			// retrieve parameters from methodcall
			String signature = m.getSignature();
			CtClass[] parameterTypes = Descriptor.getParameterTypes(signature, classpool);
			String[] types = new String[parameterTypes.length];

			for (int i = 0; i < parameterTypes.length; i++)
			{
				types[i] = parameterTypes[i].getName();
			}

			// return false if number of parameters are not the same.
			if (types.length != method.getParameters().size())
			{
				return false;
			}

			// equal parameters?
			Object[] params = method.getParameters().toArray();
			for (int j = 0; j < params.length; j++)
			{
				if (!((ParameterInfo) params[j]).getParameterTypeString().equals(types[j]))
				{
					return false;
				}
			}
		}
		catch (NotFoundException nfe)
		{
			// should not happen!
			logger.debug("NotFoundException: " + nfe.getMessage());
		}
		return true;
	}

	/**
	 * Create a method hash key for this CtMethod, it should have the same
	 * format as the hash key in methodInfo
	 * 
	 * @param meth
	 * @return
	 * @throws NotFoundException
	 * @see {@link MethodInfo#getHashKey()}
	 */
	public String getMethodHashKey(MethodCall mc)
	{
		try
		{
			// this happens in case of methods that were added by a filter (i.e.
			// signature expansion)
			StringBuilder sb = new StringBuilder();
			sb.append(mc.getMethodName()).append('%');
			CtClass cls = Descriptor.getReturnType(mc.getSignature(), classpool);
			if (cls != null)
			{
				if (cls instanceof CtPrimitiveType)
				{
					sb.append(cls.getSimpleName());
				}
				else
				{
					sb.append(cls.getName());
				}
			}
			else
			{
				sb.append("void");
			}
			sb.append('%');
			for (CtClass p : Descriptor.getParameterTypes(mc.getSignature(), classpool))
			{
				sb.append(p.getName()).append('%');
			}
			return sb.toString();

			// meth = mc.getMethod();
			//
			// StringBuilder sb = new StringBuilder();
			// sb.append(meth.getName()).append('%');
			// if (meth.getReturnType() != null)
			// {
			// if (meth.getReturnType() instanceof CtPrimitiveType)
			// {
			// sb.append(meth.getReturnType().getSimpleName());
			// }
			// else
			// {
			// sb.append(meth.getReturnType().getName());
			// }
			// }
			// else
			// {
			// sb.append("void");
			// }
			// sb.append('%');
			// for (CtClass p : meth.getParameterTypes())
			// {
			// sb.append(p.getName()).append('%');
			// }
			// return sb.toString();
		}
		catch (NotFoundException e)
		{
			return "";
		}
	}
}
