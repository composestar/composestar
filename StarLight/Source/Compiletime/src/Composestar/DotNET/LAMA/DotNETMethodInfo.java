/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.DotNET.LAMA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import Composestar.Core.LAMA.Annotation;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.UnitResult;

/**
 * Corresponds to the MethodInfo class in the .NET framework. For more information 
 * on the methods and their meaning please refer to the microsoft documentation:
 * http://msdn.microsoft.com/library/default.asp?url=/library/en-us/cpref/html/frlr
 * fsystemreflectionmethodinfoclasstopic.asp
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
	 * Currently used to support selector name conversion.
	 * Copy only stuff that applies to a method signature.
	 * This excludes e.g. the parent.
	 */
	public MethodInfo getClone(String n, Type actualParent)
	{
		DotNETMethodInfo mi = new DotNETMethodInfo();
		mi.setName(n);

		// set MethodInfo variables
		mi.Parent = actualParent;
		mi.Parameters = this.Parameters;
		mi.ReturnType = this.ReturnType;
		mi.ReturnTypeString = this.ReturnTypeString;

		// set DotNETMethodInfo variables
		mi.isConstructor = this.isConstructor;
		mi.isPrivate = this.isPrivate;
		mi.isAssembly = this.isAssembly;
		mi.isPublic = this.isPublic;
		mi.isDeclaredHere = this.isDeclaredHere;
		mi.isStatic = this.isStatic;
		mi.isFinal = this.isFinal;
		mi.isAbstract = this.isAbstract;
		mi.isVirtual = this.isVirtual;

		mi.isDeclaredHere = this.isDeclaredHere;
		mi.signature = this.signature;
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

	public boolean isPublic()
	{
		return isPublic;
	}

	public void setIsPublic(boolean isPublic)
	{
		this.isPublic = isPublic;
	}
	
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
		this.signature = sig;
	}
/*
	public void addParameter(DotNETParameterInfo param)
	{
		Parameters.add(param);
		// param.setParent(this);
	}

	public void setParent(DotNETType parent)
	{
		Parent = parent;
	}
*/
	// Stuff for LOLA

	private HashSet toHashSet(Collection c)
	{
		HashSet result = new HashSet();
		Iterator iter = c.iterator();
		while (iter.hasNext())
			result.add(iter.next());
		return result;
	}

	public UnitResult getUnitRelation(String argumentName)
	{
		if (argumentName.equals("ParentClass") && Parent.getUnitType().equals("Class"))
			return new UnitResult(Parent);
		else if (argumentName.equals("ParentInterface") && Parent.getUnitType().equals("Interface"))
			return new UnitResult(Parent);      
		else if (argumentName.equals("ChildParameters"))
			return new UnitResult(toHashSet(Parameters));
		else if (argumentName.equals("ReturnClass") && returnType().getUnitType().equals("Class"))
			return new UnitResult(returnType());
		else if (argumentName.equals("ReturnInterface") && returnType().getUnitType().equals("Interface"))
			return new UnitResult(returnType());
		else if (argumentName.equals("ReturnAnnotation") && returnType().getUnitType().equals("Annotation"))
			return new UnitResult(returnType());
		else if (argumentName.equals("Annotations"))
		{
			Iterator i = getAnnotations().iterator();
			HashSet res = new HashSet();
			while (i.hasNext())
				res.add(((Annotation)i.next()).getType());
			return new UnitResult(res);
		}        

		return null;
	}

	public Collection getUnitAttributes()
	{
		Set result = new HashSet();
		if (isPrivate())
			result.add("private");
		if (isAssembly())
			result.add("assembly");
		if (isPublic())
			result.add("public");
	//	if (isProtected())
	//		result.add("protected");
		if (isStatic())
			result.add("static");
		if (isFinal())
			result.add("final");
		if (isAbstract())
			result.add("abstract");
		if (isVirtual())
			result.add("virtual");
		return result;
	}

	/**
	 * Custom deserialization of this object
	 */
	private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException
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
