/*
 * DotNETFieldInfo.java - Created on 18-okt-2004 by havingaw
 *
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2004 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 */

package Composestar.DotNET2.LAMA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import Composestar.Core.LAMA.Annotation;
import Composestar.Core.LAMA.FieldInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.UnitResult;

/**
 * @author havingaw Contains the .Net reflection information of a Field See
 *         documentation of .NET System.Reflection.FieldInfo (on MSDN).
 */

public class DotNETFieldInfo extends FieldInfo
{
	private static final long serialVersionUID = 235924601234730641L;

	public int dotNetHashCode; // .NET hashcode

	private boolean flagIsAssembly; // Visible on Assembly level?

	private boolean flagIsPrivate; // Private field?

	private boolean flagIsPublic; // Public field?

	private boolean flagIsStatic; // Static field ('global')?

	// Declared in this Type, or inherited from parent type?
	private boolean flagIsDeclaredHere;

	public DotNETFieldInfo()
	{
		super();
	}

	public int getHashCode()
	{
		return dotNetHashCode;
	}

	public void setHashCode(int hashcode)
	{
		dotNetHashCode = hashcode;
	}

	public boolean isAssembly()
	{
		return flagIsAssembly;
	}

	public void setIsAssembly(boolean isAssembly)
	{
		flagIsAssembly = isAssembly;
	}

	@Override
	public boolean isPrivate()
	{
		return flagIsPrivate;
	}

	public void setIsPrivate(boolean isPrivate)
	{
		flagIsPrivate = isPrivate;
	}

	@Override
	public boolean isPublic()
	{
		return flagIsPublic;
	}

	public void setIsPublic(boolean isPublic)
	{
		flagIsPublic = isPublic;
	}

	@Override
	public boolean isProtected()
	{
		return false; // TODO: should return the isFamily value
	}

	public boolean isStatic()
	{
		return flagIsStatic;
	}

	public void setIsStatic(boolean isStatic)
	{
		flagIsStatic = isStatic;
	}

	@Override
	public boolean isDeclaredHere()
	{
		return flagIsDeclaredHere;
	}

	public void setIsDeclaredHere(boolean isDeclaredHere)
	{
		flagIsDeclaredHere = isDeclaredHere;
	}

	/** Stuff for LOLA* */

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitRelation(java.lang.String)
	 */
	@Override
	public UnitResult getUnitRelation(String argumentName)
	{
		if ("ParentType".equals(argumentName))
		{
			return new UnitResult(parent);
		}
		else if ("Class".equals(argumentName) && "Class".equals(getFieldType().getUnitType()))
		{
			return new UnitResult(getFieldType());
		}
		else if ("Interface".equals(argumentName) && "Interface".equals(getFieldType().getUnitType()))
		{
			return new UnitResult(getFieldType());
		}
		else if ("Annotation".equals(argumentName) && "Annotation".equals(getFieldType().getUnitType()))
		{
			return new UnitResult(getFieldType());
		}
		else if ("Annotations".equals(argumentName))
		{
			Iterator<Annotation> i = getAnnotations().iterator();
			Set<Type> res = new HashSet<Type>();
			while (i.hasNext())
			{
				res.add(i.next().getType());
			}
			return new UnitResult(res);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitAttributes()
	 */
	@Override
	public Collection<String> getUnitAttributes()
	{
		Set<String> result = new HashSet<String>();
		if (isPublic())
		{
			result.add("public");
		}
		if (isPrivate())
		{
			result.add("private");
		}
		// TODO: For some reasons, there is no 'protected' attr for fields?
		if (isStatic())
		{
			result.add("static");
		}
		return result;
	}

	/**
	 * Custom deserialization of this object
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		dotNetHashCode = in.readInt();
		flagIsAssembly = in.readBoolean();

		flagIsPrivate = in.readBoolean();
		flagIsPublic = in.readBoolean();
		flagIsStatic = in.readBoolean();
		flagIsDeclaredHere = in.readBoolean();
	}

	/**
	 * Custom serialization of this object
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeInt(dotNetHashCode);
		out.writeBoolean(flagIsAssembly);

		out.writeBoolean(flagIsPrivate);
		out.writeBoolean(flagIsPublic);
		out.writeBoolean(flagIsStatic);
		out.writeBoolean(flagIsDeclaredHere);
		out.writeObject(parent);
	}
}
