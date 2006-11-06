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
public class ResourceUsage implements SerializableRepositoryEntity
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4560235679437768784L;

	public String ResourceName;

	public String AccessType;

	public String AccessOccurence;

	public Type Parent;

	public ResourceUsage()
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
	 * 
	 * @param in
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		ResourceName = in.readUTF();
		Parent = (Type) in.readObject();
		AccessType = in.readUTF();
		AccessOccurence = in.readUTF();
	}

	/**
	 * Custom serialization of this object
	 * 
	 * @param out
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeUTF(ResourceName);
		out.writeObject(Parent);
		out.writeUTF(AccessType);
		out.writeUTF(AccessOccurence);
	}
}
