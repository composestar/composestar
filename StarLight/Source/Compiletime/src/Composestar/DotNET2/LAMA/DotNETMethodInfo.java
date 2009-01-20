/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
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
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.UnitResult;

/**
 * Corresponds to the MethodInfo class in the .NET framework. For more
 * information on the methods and their meaning please refer to the microsoft
 * documentation:
 * http://msdn.microsoft.com/library/default.asp?url=/library/en-us
 * /cpref/html/frlr fsystemreflectionmethodinfoclasstopic.asp
 */
public class DotNETMethodInfo extends MethodInfo
{
	private static final long serialVersionUID = -1303615818393508196L;

	private boolean isConstructor;

	private boolean isPrivate;

	private boolean isAssembly;

	private boolean isPublic;

	private boolean isStatic;

	private boolean isFinal;

	private boolean isAbstract;

	private boolean isVirtual;

	private boolean isDeclaredHere;

	private String signature;

	public DotNETMethodInfo()
	{
		super();
	}

	/**
	 * Currently used to support selector name conversion. Copy only stuff that
	 * applies to a method signature. This excludes e.g. the parent.
	 */
	@Override
	public MethodInfo getClone(String n, Type actualParent)
	{
		DotNETMethodInfo mi = new DotNETMethodInfo();
		mi.setName(n);

		// set MethodInfo variables
		mi.parent = actualParent;
		mi.parameters = parameters;
		mi.returnType = returnType;
		mi.returnTypeString = returnTypeString;

		// set DotNETMethodInfo variables
		mi.isConstructor = isConstructor;
		mi.isPrivate = isPrivate;
		mi.isAssembly = isAssembly;
		mi.isPublic = isPublic;
		mi.isDeclaredHere = isDeclaredHere;
		mi.isStatic = isStatic;
		mi.isFinal = isFinal;
		mi.isAbstract = isAbstract;
		mi.isVirtual = isVirtual;

		mi.isDeclaredHere = isDeclaredHere;

		// Create new signature:
		StringBuffer newSignature = new StringBuffer();
		int index1 = signature.indexOf("::");
		if (!actualParent.equals(parent))
		{
			int index0 = signature.indexOf(' ');
			newSignature.append(signature.substring(0, index0));
			String oldType = signature.substring(index0, index1);
			String newType = oldType.replace(parent.getFullName(), actualParent.getFullName());
			newSignature.append(newType);
		}
		else
		{
			newSignature.append(signature.substring(0, index1));
		}
		int index2 = signature.indexOf('(');
		if (!n.equals(name))
		{
			String oldName = signature.substring(index1, index2);
			String newName = oldName.replace(name, n);
			newSignature.append(newName);
		}
		else
		{
			newSignature.append(signature.substring(index1, index2));
		}
		newSignature.append(signature.substring(index2));

		mi.signature = newSignature.toString();
		return mi;
	}

	public boolean isConstructor()
	{
		return isConstructor;
	}

	public void setIsConstructor(boolean isConstructor)
	{
		this.isConstructor = isConstructor;
	}

	@Override
	public boolean isPrivate()
	{
		return isPrivate;
	}

	public void setIsPrivate(boolean isPrivate)
	{
		this.isPrivate = isPrivate;
	}

	public boolean isAssembly()
	{
		return isAssembly;
	}

	public void setIsInternal(boolean isAssembly)
	{
		this.isAssembly = isAssembly;
	}

	@Override
	public boolean isPublic()
	{
		return isPublic;
	}

	public void setIsPublic(boolean isPublic)
	{
		this.isPublic = isPublic;
	}

	@Override
	public boolean isProtected()
	{
		return false; // TODO: should return the isFamily value
	}

	public boolean isStatic()
	{
		return isStatic;
	}

	public void setIsStatic(boolean isStatic)
	{
		this.isStatic = isStatic;
	}

	public boolean isFinal()
	{
		return isFinal;
	}

	public void setIsFinal(boolean isFinal)
	{
		this.isFinal = isFinal;
	}

	public boolean isAbstract()
	{
		return isAbstract;
	}

	public void setIsAbstract(boolean isAbstract)
	{
		this.isAbstract = isAbstract;
	}

	public boolean isVirtual()
	{
		return isVirtual;
	}

	public void setIsVirtual(boolean isVirtual)
	{
		this.isVirtual = isVirtual;
	}

	@Override
	public boolean isDeclaredHere()
	{
		return isDeclaredHere;
	}

	public void setIsDeclaredHere(boolean declaredHere)
	{
		isDeclaredHere = declaredHere;
	}

	public String getSignature()
	{
		return signature;
	}

	public void setSignature(String sig)
	{
		signature = sig;
	}

	/*
	 * public void addParameter(DotNETParameterInfo param) {
	 * Parameters.add(param); // param.setParent(this); } public void
	 * setParent(DotNETType parent) { Parent = parent; }
	 */

	@Override
	public UnitResult getUnitRelation(String argumentName)
	{
		if (argumentName.equals("ParentClass") && parent.getUnitType().equals("Class"))
		{
			return new UnitResult(parent);
		}
		else if (argumentName.equals("ParentInterface") && parent.getUnitType().equals("Interface"))
		{
			return new UnitResult(parent);
		}
		else if (argumentName.equals("ChildParameters"))
		{
			return new UnitResult(new HashSet<ParameterInfo>(parameters));
		}
		else if (argumentName.equals("ReturnClass") && getReturnType().getUnitType().equals("Class"))
		{
			return new UnitResult(getReturnType());
		}
		else if (argumentName.equals("ReturnInterface") && getReturnType().getUnitType().equals("Interface"))
		{
			return new UnitResult(getReturnType());
		}
		else if (argumentName.equals("ReturnAnnotation") && getReturnType().getUnitType().equals("Annotation"))
		{
			return new UnitResult(getReturnType());
		}
		else if (argumentName.equals("Annotations"))
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

	@Override
	public Collection<String> getUnitAttributes()
	{
		Set<String> result = new HashSet<String>();
		if (isPrivate())
		{
			result.add("private");
		}
		if (isAssembly())
		{
			result.add("assembly");
		}
		if (isPublic())
		{
			result.add("public");
		}
		// if (isProtected())
		// result.add("protected");
		if (isStatic())
		{
			result.add("static");
		}
		if (isFinal())
		{
			result.add("final");
		}
		if (isAbstract())
		{
			result.add("abstract");
		}
		if (isVirtual())
		{
			result.add("virtual");
		}
		return result;
	}

	/**
	 * Custom deserialization of this object
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		isConstructor = in.readBoolean();
		isPrivate = in.readBoolean();
		isAssembly = in.readBoolean();
		isPublic = in.readBoolean();
		isStatic = in.readBoolean();
		isFinal = in.readBoolean();
		isAbstract = in.readBoolean();
		isVirtual = in.readBoolean();
		isDeclaredHere = in.readBoolean();
		signature = in.readUTF();
	}

	/**
	 * Custom serialization of this object
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeBoolean(isConstructor);
		out.writeBoolean(isPrivate);
		out.writeBoolean(isAssembly);
		out.writeBoolean(isPublic);
		out.writeBoolean(isStatic);
		out.writeBoolean(isFinal);
		out.writeBoolean(isAbstract);
		out.writeBoolean(isVirtual);
		out.writeBoolean(isDeclaredHere);
		out.writeUTF(signature);
	}
}
