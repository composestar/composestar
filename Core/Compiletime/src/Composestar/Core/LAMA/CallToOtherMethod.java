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
 *
 */
public class CallToOtherMethod implements SerializableRepositoryEntity {

	public String OperationName;
	public String ClassName;       
	public Type Parent;
	
	public CallToOtherMethod()
	{
		
	}
	
	public Type parent() 
	{
		return Parent;
	}
	
	public void setParent(Type parent) 
	{
	    Parent = parent;
	}
	
		
	/**
	 * Custom deserialization of this object
	 */
	private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException
	{
		OperationName = in.readUTF();
		Parent = (Type)in.readObject();
		ClassName = in.readUTF();
	}
	
	/**
	 * Custom serialization of this object
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeUTF(OperationName);
		out.writeObject(Parent);
		out.writeUTF(ClassName);
	}
	
}
