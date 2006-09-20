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

import Composestar.Core.INLINE.model.Block;
import Composestar.Core.RepositoryImplementation.SerializableRepositoryEntity;

/**
 * @author Michiel van Oudheusden
 *
 */
public class CallToOtherMethod implements SerializableRepositoryEntity {

	public String OperationName;
	public String ClassName;       
	public Type Parent;
	public MethodInfo calledMethod;
	public Block outputFilterCode;//doesn't need to be serialized
	
	public CallToOtherMethod()
	{
		
	}
	
		
	/**
	 * @return the calledMethod
	 */
	public MethodInfo getCalledMethod(){
	    return calledMethod;
	}

	/**
	 * @param calledMethod the calledMethod to set
	 */
	public void setCalledMethod(MethodInfo calledMethod){
	    this.calledMethod = calledMethod;
	}

	/**
	 * @return the className
	 */
	public String getClassName(){
	    return ClassName;
	}

	/**
	 * @param className the className to set
	 */
	public void setClassName(String className){
	    ClassName = className;
	}

	/**
	 * @return the operationName
	 */
	public String getOperationName(){
	    return OperationName;
	}

	/**
	 * @param operationName the operationName to set
	 */
	public void setOperationName(String operationName){
	    OperationName = operationName;
	}

	/**
	 * @return the parent
	 */
	public Type getParent(){
	    return Parent;
	}
	
	/**
	 * @param parent the parent to set
	 */
	public void setParent(Type parent){
	    Parent = parent;
	}




	/**
	 * @return the outputFilterCode
	 */
	public Block getOutputFilterCode(){
	    return outputFilterCode;
	}


	/**
	 * @param outputFilterCode the outputFilterCode to set
	 */
	public void setOutputFilterCode(Block outputFilterCode){
	    this.outputFilterCode = outputFilterCode;
	}


	/**
	 * Custom deserialization of this object
	 */
	private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException
	{
		OperationName = in.readUTF();
		Parent = (Type)in.readObject();
		ClassName = in.readUTF();
		calledMethod = (MethodInfo) in.readObject();
	}
	
	/**
	 * Custom serialization of this object
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeUTF(OperationName);
		out.writeObject(Parent);
		out.writeUTF(ClassName);
		out.writeObject( calledMethod );
	}
	
}
