/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
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
	private static final long serialVersionUID = -4560235679437768784L;

	public String resourceName;

	public String accessType;

	public String accessOccurence;

	public Type parent;

	public ResourceUsage()
	{}

	public Type parent()
	{
		return parent;
	}

	public void setParent(Type inParent)
	{
		parent = inParent;
	}

	/**
	 * Custom deserialization of this object
	 * 
	 * @param in
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		resourceName = in.readUTF();
		parent = (Type) in.readObject();
		accessType = in.readUTF();
		accessOccurence = in.readUTF();
	}

	/**
	 * Custom serialization of this object
	 * 
	 * @param out
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeUTF(resourceName);
		out.writeObject(parent);
		out.writeUTF(accessType);
		out.writeUTF(accessOccurence);
	}
}
