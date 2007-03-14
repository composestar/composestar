/*
 * Created on 26-apr-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.Core.LAMA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import Composestar.Core.RepositoryImplementation.SerializableRepositoryEntity;

/**
 * @author Michiel van Oudheusden
 */
public class CallToOtherMethod implements SerializableRepositoryEntity
{
	private static final long serialVersionUID = 1718642064472551590L;

	/**
	 * The fully qualified name
	 */
	public String operationName;

	/**
	 * The name of the method
	 */
	public String methodName;

	public MethodInfo calledMethod;

	public String className;

	public Type parent;

	public CallToOtherMethod()
	{

	}

	public Type parent()
	{
		return parent;
	}

	/**
	 * @return the calledMethod
	 */
	public MethodInfo getCalledMethod()
	{
		return calledMethod;
	}

	/**
	 * @param inCalledMethod the calledMethod to set
	 */
	public void setCalledMethod(MethodInfo inCalledMethod)
	{
		this.calledMethod = inCalledMethod;
	}

	/**
	 * @return the className
	 */
	public String getClassName()
	{
		return className;
	}

	/**
	 * @param inClassName the className to set
	 */
	public void setClassName(String inClassName)
	{
		className = inClassName;
	}

	/**
	 * @return the operationName
	 */
	public String getOperationName()
	{
		return operationName;
	}

	/**
	 * @param inOperationName the operationName to set
	 */
	public void setOperationName(String inOperationName)
	{
		operationName = inOperationName;
	}

	/**
	 * @return the parent
	 */
	public Type getParent()
	{
		return parent;
	}

	public void setParent(Type inParent)
	{
		parent = inParent;
	}

	/**
	 * @return the methodName
	 */
	public String getMethodName()
	{
		return methodName;
	}

	/**
	 * @param inMethodName the methodName to set
	 */
	public void setMethodName(String inMethodName)
	{
		this.methodName = inMethodName;
	}

	/**
	 * Custom deserialization of this object
	 * 
	 * @param in
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		operationName = in.readUTF();
		parent = (Type) in.readObject();
		className = in.readUTF();
	}

	/**
	 * Custom serialization of this object
	 * 
	 * @param out
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeUTF(operationName);
		out.writeObject(parent);
		out.writeUTF(className);
	}

}
