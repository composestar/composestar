/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2006-2008 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * $Id$
 */
package Composestar.Core.LAMA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

public class Annotation implements Serializable
{
	private static final long serialVersionUID = -3037031466310155062L;

	private String typeName;

	private Type type;

	private Properties values;

	public ProgramElement target;

	public boolean superImposed; // Set to true if this annotation was

	// superimposed by Compose*

	// This info can be used by the weaver (?) to write the new annos
	// back into the assembly.

	public Annotation()
	{
		this(false);
	}

	public Annotation(boolean inIsSuperImposed)
	{
		superImposed = inIsSuperImposed;
		values = new Properties();
	}

	public void register(Type annotationType, ProgramElement inTarget)
	{
		type = annotationType;
		target = inTarget;
		if (type != null)
		{
			setTypeName(type.getFullName());
			type.addAnnotationInstance(this);
		}
		inTarget.addAnnotation(this);
	}

	public void deregister()
	{
		if (type != null)
		{
			type.removeAnnotationInstance(this);
		}
		target.removeAnnotation(this);
	}

	public void setTypeName(String name)
	{
		typeName = name;
	}

	public String getTypeName()
	{
		return typeName;
	}

	public void setType(Type t)
	{
		type = t;
	}

	public Type getType()
	{
		return type;
	}

	public void setValue(String key, String value)
	{
		if (key == null || value == null)
		{
			return;
		}
		values.setProperty(key, value);
	}

	public String getValue(String key)
	{
		return values.getProperty(key);
	}

	public void setValues(Map data)
	{
		Iterator it = data.entrySet().iterator();
		while (it.hasNext())
		{
			Entry entry = (Entry) it.next();
			setValue(entry.getKey().toString(), entry.getValue().toString());
		}
	}

	public boolean isSuperImposed()
	{
		return superImposed;
	}

	public void setIsSuperImposed(boolean isSI)
	{
		superImposed = isSI;
	}

	public ProgramElement getTarget()
	{
		return target;
	}

	public void setTarget(ProgramElement value)
	{
		target = value;
	}

	/**
	 * Custom deserialization of this object
	 * 
	 * @param in
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		// m_type = (Type)in.readObject();
		typeName = in.readUTF();
		if (typeName.length() == 0)
		{
			typeName = null;
		}
		target = (ProgramElement) in.readObject();
		values = (Properties) in.readObject();
		superImposed = in.readBoolean();
	}

	/**
	 * Custom serialization of this object
	 * 
	 * @param out
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		// out.writeObject(m_type);
		out.writeUTF(typeName == null ? "" : typeName);
		out.writeObject(target);
		out.writeObject(values);
		out.writeBoolean(superImposed);
	}
}
