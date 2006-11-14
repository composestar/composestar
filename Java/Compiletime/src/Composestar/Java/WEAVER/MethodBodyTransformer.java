package Composestar.Java.WEAVER;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;
import javassist.expr.ExprEditor;
import javassist.expr.Cast;
import javassist.expr.ConstructorCall;
import javassist.expr.FieldAccess;
import javassist.expr.Handler;
import javassist.expr.Instanceof;
import javassist.expr.MethodCall;
import javassist.expr.NewArray;
import javassist.expr.NewExpr;

import java.lang.reflect.Method;
import java.util.Iterator;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.Signature;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Java.LAMA.JavaMethodInfo;
import Composestar.Utils.Debug;

/**
 * A transformer for method bodies.
 * <p>
 * Expressions in the method bodies are replaced with interpreter calls.
 * 
 * @see javassist.expr.ExprEditor
 */
public class MethodBodyTransformer extends ExprEditor
{

	private ClassPool classpool;
	
	public MethodBodyTransformer(ClassPool classpool)
	{
		this.classpool = classpool;
	}

	/**
	 * Edits an expression for explicit type casting.
	 * 
	 * @see javassist.expr.ExprEditor#edit(Cast)
	 */
	public void edit(Cast c) throws CannotCompileException
	{

	}

	/**
	 * Edits a constructor call. The constructor call is either super() or
	 * this() included in a constructor body.
	 * 
	 * @see javassist.expr.ExprEditor#edit(ConstructorCall)
	 */
	public void edit(ConstructorCall c) throws CannotCompileException
	{

	}

	/**
	 * Edits a field-access expression. Field access means both read and write.
	 * 
	 * @see javassist.expr.ExprEditor#edit(FieldAccess)
	 */
	public void edit(FieldAccess f) throws CannotCompileException
	{

	}

	/**
	 * Edits a catch clause.
	 * 
	 * @see javassist.expr.ExprEditor#edit(Handler)
	 */
	public void edit(Handler h) throws CannotCompileException
	{

	}

	/**
	 * Edits an instanceof expression.
	 * 
	 * @see javassist.expr.ExprEditor#edit(Instanceof)
	 */
	public void edit(Instanceof i) throws CannotCompileException
	{

	}

	/**
	 * Edits a method call.
	 * 
	 * @see javassist.expr.ExprEditor#edit(MethodCall)
	 */
	public void edit(MethodCall m) throws CannotCompileException
	{
		HookDictionary hd = HookDictionary.instance();
		try
		{
			String classname = m.getClassName();
				
			if (hd.isMethodInterception(classname))
			{
				String signature = m.getSignature();
				CtClass returnType = Descriptor.getReturnType(signature, classpool);
				if (returnType == CtClass.voidType)
				{
					this.replaceVoidMethodCall(m);
				}
				else
				{
					this.replaceReturnMethodCall(m);
				}
			}
		}
		catch (NotFoundException nfe)
		{
			Debug.out(Debug.MODE_DEBUG, "WEAVER", "Method not found: " + nfe.getMessage()+ " "+m.getSignature());
		}
	}

	/**
	 * Edits an expression for array creation.
	 * 
	 * @see javassist.expr.ExprEditor#edit(NewArray)
	 */
	public void edit(NewArray a) throws CannotCompileException
	{

	}

	/**
	 * Edits a new expression.
	 * 
	 * @see javassist.expr.ExprEditor#edit(NewExpr)
	 */
	public void edit(NewExpr e) throws CannotCompileException
	{
		
		HookDictionary hd = HookDictionary.instance();
		if (hd.isAfterInstantationInterception(e.getClassName()))
		{
			int mod = e.where().getModifiers();
			boolean isStaticCaller = Modifier.isStatic(mod);
			if(isStaticCaller)
			{
				e.replace("{$_ = $proceed($$); Composestar.RuntimeCore.FLIRT.MessageHandlingFacility.handleInstanceCreation(" + '"' + e.where().getName() + '"' + ",$_,$args);}");
			}
			else
			{
				e.replace("{$_ = $proceed($$); Composestar.RuntimeCore.FLIRT.MessageHandlingFacility.handleInstanceCreation(this,$_,$args);}");
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
			m.replace("Composestar.RuntimeCore.FLIRT.MessageHandlingFacility.handleVoidMethodCall(" + '"'
					+ m.where().getDeclaringClass().getName() + '"' + "," + '"'
					+ m.getClassName() + '"' + "," + '"' + m.getMethodName() + '"'
					+ ",$args);");
		}
		else if (isStaticCaller && !isStaticTarget)
		{
			// static caller, non-static target
			m.replace("Composestar.RuntimeCore.FLIRT.MessageHandlingFacility.handleVoidMethodCall(" + '"'
					+ m.where().getDeclaringClass().getName() + '"' + ",$0," + '"' + m.getMethodName() + '"'
					+ ",$args);");
		}
		else if (!isStaticCaller && isStaticTarget)
		{
			// non-static caller, static target
			m.replace("Composestar.RuntimeCore.FLIRT.MessageHandlingFacility.handleVoidMethodCall(this," + '"' 
					+ m.getClassName()+ '"' + "," + '"' + m.getMethodName() + '"'
					+ ",$args);");
		}
		else
		{
			// non-static caller, non-static target
			m.replace("Composestar.RuntimeCore.FLIRT.MessageHandlingFacility.handleVoidMethodCall(this,$0," + '"'
					+ m.getMethodName() + '"' + ",$args);");
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
			m.replace("$_ = ($r)" + "Composestar.RuntimeCore.FLIRT.MessageHandlingFacility.handleReturnMethodCall("
					+ '"' + m.where().getDeclaringClass().getName() + '"' + "," + '"'
					+ m.getClassName() + '"' + "," + '"' + m.getMethodName() + '"'
					+ ",$args);");
		}
		else if (isStaticCaller && !isStaticTarget)
		{
			// static caller, non-static target
			m.replace("$_ = ($r)" + "Composestar.RuntimeCore.FLIRT.MessageHandlingFacility.handleReturnMethodCall("
					+ '"' + m.where().getDeclaringClass().getName() + '"' + ",$0," + '"' + m.getMethodName() + '"'
					+ ",$args);");
		}
		else if (!isStaticCaller && isStaticTarget)
		{
			// non-static caller, static target
			m.replace("$_ = ($r)"
					+ "Composestar.RuntimeCore.FLIRT.MessageHandlingFacility.handleReturnMethodCall(this," + '"'
					+ m.getClassName() + '"' + "," + '"' + m.getMethodName() + '"'
					+ ",$args);");
		}
		else
		{
			// non-static caller, non-static target
			m.replace("$_ = ($r)"
					+ "Composestar.RuntimeCore.FLIRT.MessageHandlingFacility.handleReturnMethodCall(this,$0," + '"'
					+ m.getMethodName() + '"' + ",$args);");
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
		catch(NotFoundException nfe)
		{
			// no method declaration found in bytecode.
		}
		
		// second try: lookup in concern's Signature (it could be added by SIGN)
		DataStore d = DataStore.instance();
		Concern c = (Concern)d.getObjectByID(m.getClassName());
		if(c != null)
		{
			Signature s = c.getSignature();
			Iterator methods = s.getMethods().iterator();
			if(methods.hasNext())
			{
				JavaMethodInfo method = (JavaMethodInfo)methods.next();
				if(method.name().equals(m.getMethodName()))
				{
					// equal parameters?
					if(equalParameters(m,method))
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
		
			for(int i=0; i<parameterTypes.length; i++)
			{
				types[i] = parameterTypes[i].getName();
			}
			
			// return false if number of parameters are not the same.
			if(types.length != method.getParameters().size())
			{
				return false;
			}
			
			// equal parameters?
			Object[] params = method.getParameters().toArray();
			for(int j=0; j<params.length; j++)
			{
				if(!((ParameterInfo)params[j]).getParameterTypeString().equals(types[j]))
				{
					return false;
				}
			}
		}
		catch(NotFoundException nfe)
		{
			// should not happen!
			Debug.out(Debug.MODE_ERROR, "WEAVER", "NotFoundException: " + nfe.getMessage());
		}
		return true;
	}
}
