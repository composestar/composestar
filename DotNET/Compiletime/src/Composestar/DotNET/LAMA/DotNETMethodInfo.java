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

import Composestar.Core.LAMA.Annotation;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.UnitResult;

/**
 * Corresponds to the MethodInfo class in the .NET framework. For more
 * information on the methods and their meaning please refer to the microsoft
 * documentation:
 * http://msdn.microsoft.com/library/default.asp?url=/library/en-us/cpref/html/frlr
 * fsystemreflectionmethodinfoclasstopic.asp
 */
public class DotNETMethodInfo extends MethodInfo
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1303615818393508196L;

	public int CallingConvention;

	private int HashCode;

	public boolean IsAbstract;

	public boolean IsAssembly;

	public boolean IsConstructor;

	public boolean IsFamily;

	public boolean IsFamilyAndAssembly;

	public boolean IsFamilyOrAssembly;

	public boolean IsFinal;

	public boolean IsHideBySig;

	public boolean IsPrivate;

	public boolean IsPublic;

	public boolean IsStatic;

	public boolean IsVirtual;

	public boolean IsDeclaredHere;

	/**
	 * @roseuid 401B84CF0212
	 */
	public DotNETMethodInfo()
	{
		super();
	}

	// add clone: currently used to support selector name conversion
	// copy only stuff that applies to a method signature, this excludes e.g.
	// the parent
	public MethodInfo getClone(String n, Type actualParent)
	{
		DotNETMethodInfo mi = new DotNETMethodInfo();
		mi.setName(n);
		// set MethodInfo variables
		// mi.Parent = this.Parent;
		mi.Parent = actualParent;
		mi.Parameters = this.Parameters;
		mi.ReturnType = this.ReturnType;
		mi.ReturnTypeString = this.ReturnTypeString;

		// set DotNETMethodInfo variables
		mi.CallingConvention = this.CallingConvention;
		mi.IsAbstract = this.IsAbstract;
		mi.IsAssembly = this.IsAssembly;
		mi.IsConstructor = this.IsConstructor;
		mi.IsDeclaredHere = this.IsDeclaredHere;
		mi.IsFamily = this.IsFamily;
		mi.IsFamilyAndAssembly = this.IsFamilyAndAssembly;
		mi.IsFamilyOrAssembly = this.IsFamilyOrAssembly;
		mi.IsFinal = this.IsFinal;
		mi.IsHideBySig = this.IsHideBySig;
		mi.IsPrivate = this.IsPrivate;
		mi.IsPublic = this.IsPublic;
		mi.IsStatic = this.IsStatic;
		mi.IsVirtual = this.IsVirtual;
		return mi;
	}

	/**
	 * @return int
	 * @roseuid 401B84CF0201
	 */
	public int callingConvention()
	{
		return CallingConvention;
	}

	/**
	 * @param cv
	 * @roseuid 402A018403CD
	 */
	public void setCallingConvention(int cv)
	{
		CallingConvention = cv;
	}

	/**
	 * @return boolean
	 * @roseuid 401B84CF0202
	 */
	public boolean isAbstract()
	{
		return IsAbstract;
	}

	/**
	 * @param isAbstract
	 * @roseuid 402A0195034F
	 */
	public void setIsAbstract(boolean isAbstract)
	{
		IsAbstract = isAbstract;
	}

	/**
	 * @return boolean
	 * @roseuid 401B84CF0203
	 */
	public boolean isAssembly()
	{
		return IsAssembly;
	}

	/**
	 * @param isAssembly
	 * @roseuid 402A01AC00F9
	 */
	public void setIsAssembly(boolean isAssembly)
	{
		IsAssembly = isAssembly;
	}

	/**
	 * @return boolean
	 * @roseuid 401B84CF0204
	 */
	public boolean isConstructor()
	{
		return IsConstructor;
	}

	/**
	 * @param isConstructor
	 * @roseuid 402A01BB026D
	 */
	public void setIsConstructor(boolean isConstructor)
	{
		IsConstructor = isConstructor;
	}

	/**
	 * @return boolean
	 * @roseuid 401B84CF0205
	 */
	public boolean isFamily()
	{
		return IsFamily;
	}

	/**
	 * @param isFamily
	 * @roseuid 402A01CB018A
	 */
	public void setIsFamily(boolean isFamily)
	{
		IsFamily = isFamily;
	}

	/**
	 * @return boolean
	 * @roseuid 401B84CF0206
	 */
	public boolean isFamilyAndAssembly()
	{
		return IsFamilyAndAssembly;
	}

	/**
	 * @param isFamAndAsm
	 * @roseuid 402A01D70000
	 */
	public void setIsFamilyAndAssembly(boolean isFamAndAsm)
	{
		IsFamilyAndAssembly = isFamAndAsm;
	}

	/**
	 * @return boolean
	 * @roseuid 401B84CF0207
	 */
	public boolean isFamilyOrAssembly()
	{
		return IsFamilyOrAssembly;
	}

	/**
	 * @param isFamOrAsm
	 * @roseuid 402A01EF0164
	 */
	public void setIsFamilyOrAssembly(boolean isFamOrAsm)
	{
		IsFamilyOrAssembly = isFamOrAsm;
	}

	/**
	 * @return boolean
	 * @roseuid 401B84CF0208
	 */
	public boolean isFinal()
	{
		return IsFinal;
	}

	/**
	 * @param isFinal
	 * @roseuid 402A01FB02A1
	 */
	public void setIsFinal(boolean isFinal)
	{
		IsFinal = isFinal;
	}

	/**
	 * @return boolean
	 * @roseuid 401B84CF0209
	 */
	public boolean isHideBySig()
	{
		return IsHideBySig;
	}

	/**
	 * @param isHide
	 * @roseuid 402A0213006B
	 */
	public void setIsHideBySig(boolean isHide)
	{
		IsHideBySig = isHide;
	}

	/**
	 * @return boolean
	 * @roseuid 401B84CF020A
	 */
	public boolean isPrivate()
	{
		return IsPrivate;
	}

	/**
	 * @param isPrivate
	 * @roseuid 402A0240034B
	 */
	public void setIsPrivate(boolean isPrivate)
	{
		IsPrivate = isPrivate;
	}

	/**
	 * @return boolean
	 * @roseuid 401B84CF020B
	 */
	public boolean isPublic()
	{
		return IsPublic;
	}

	/**
	 * @param isPublic
	 * @roseuid 402A024C0280
	 */
	public void setIsPublic(boolean isPublic)
	{
		IsPublic = isPublic;
	}

	/**
	 * @return boolean
	 * @roseuid 401B84CF020C
	 */
	public boolean isStatic()
	{
		return IsStatic;
	}

	/**
	 * @param isStatic
	 * @roseuid 402A026E02BB
	 */
	public void setIsStatic(boolean isStatic)
	{
		IsStatic = isStatic;
	}

	/**
	 * @return boolean
	 * @roseuid 401B84CF020D
	 */
	public boolean isVirtual()
	{
		return IsVirtual;
	}

	/**
	 * @param isVirtual
	 * @roseuid 402A027A03E4
	 */
	public void setIsVirtual(boolean isVirtual)
	{
		IsVirtual = isVirtual;
	}

	/**
	 * @return int
	 * @roseuid 401B84CF0210
	 */
	public int getHashCode()
	{
		return HashCode;
	}

	/**
	 * @param code
	 * @roseuid 402A0319000E
	 */
	public void setHashCode(int code)
	{
		HashCode = code;
	}

	/**
	 * @param param
	 * @roseuid 402A032A02F7
	 */
	public void addParameter(DotNETParameterInfo param)
	{
		Parameters.add(param);
		// param.setParent(this);
	}

	/**
	 * @param parent
	 * @roseuid 405068A401B0
	 */
	public void setParent(DotNETType parent)
	{
		Parent = parent;
	}

	/***************************************************************************
	 * Stuff for LOLA
	 * 
	 * @param c
	 **************************************************************************/

	private HashSet toHashSet(Collection c)
	{
		HashSet result = new HashSet();
		Iterator iter = c.iterator();
		while (iter.hasNext())
		{
			result.add(iter.next());
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitRelation(java.lang.String)
	 */
	public UnitResult getUnitRelation(String argumentName)
	{
		if (argumentName.equals("ParentClass") && Parent.getUnitType().equals("Class"))
		{
			return new UnitResult(Parent);
		}
		else if (argumentName.equals("ParentInterface") && Parent.getUnitType().equals("Interface"))
		{
			return new UnitResult(Parent);
		}
		else if (argumentName.equals("ChildParameters"))
		{
			return new UnitResult(toHashSet(Parameters));
		}
		else if (argumentName.equals("ReturnClass") && returnType().getUnitType().equals("Class"))
		{
			return new UnitResult(returnType());
		}
		else if (argumentName.equals("ReturnInterface") && returnType().getUnitType().equals("Interface"))
		{
			return new UnitResult(returnType());
		}
		else if (argumentName.equals("ReturnAnnotation") && returnType().getUnitType().equals("Annotation"))
		{
			return new UnitResult(returnType());
		}
		else if (argumentName.equals("Annotations"))
		{
			Iterator i = getAnnotations().iterator();
			HashSet res = new HashSet();
			while (i.hasNext())
			{
				res.add(((Annotation) i.next()).getType());
			}
			return new UnitResult(res);
		}

		return null;
	}

	public boolean isDeclaredHere()
	{
		return IsDeclaredHere;
	}

	public void setIsDeclaredHere(boolean declaredHere)
	{
		IsDeclaredHere = declaredHere;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitAttributes()
	 */
	public Collection getUnitAttributes()
	{
		HashSet result = new HashSet();
		if (isPublic())
		{
			result.add("public");
		}
		if (isPrivate())
		{
			result.add("private");
		}
		/*
		 * if (isProtected()) result.add("protected");
		 */
		if (isStatic())
		{
			result.add("static");
		}
		if (isFinal())
		{
			result.add("final");
		}
		return result;
	}

	/**
	 * Custom deserialization of this object
	 * 
	 * @param in
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		CallingConvention = in.readInt();
		HashCode = in.readInt();
		IsAbstract = in.readBoolean();
		IsAssembly = in.readBoolean();
		IsConstructor = in.readBoolean();
		IsFamily = in.readBoolean();
		IsFamilyAndAssembly = in.readBoolean();
		IsFamilyOrAssembly = in.readBoolean();
		IsFinal = in.readBoolean();
		IsHideBySig = in.readBoolean();
		IsPrivate = in.readBoolean();
		IsPublic = in.readBoolean();
		IsStatic = in.readBoolean();
		IsVirtual = in.readBoolean();
		IsDeclaredHere = in.readBoolean();
	}

	/**
	 * Custom serialization of this object
	 * 
	 * @param out
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeInt(CallingConvention);
		out.writeInt(HashCode);
		out.writeBoolean(IsAbstract);
		out.writeBoolean(IsAssembly);
		out.writeBoolean(IsConstructor);
		out.writeBoolean(IsFamily);
		out.writeBoolean(IsFamilyAndAssembly);
		out.writeBoolean(IsFamilyOrAssembly);
		out.writeBoolean(IsFinal);
		out.writeBoolean(IsHideBySig);
		out.writeBoolean(IsPrivate);
		out.writeBoolean(IsPublic);
		out.writeBoolean(IsStatic);
		out.writeBoolean(IsVirtual);
		out.writeBoolean(IsDeclaredHere);
	}
}
