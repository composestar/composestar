/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2008 University of Twente.
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

package Composestar.CwC.TYM.Collector;

import weavec.cmodel.type.ArrayType;
import weavec.cmodel.type.Builtin_va_listType;
import weavec.cmodel.type.CType;
import weavec.cmodel.type.DeclaredType;
import weavec.cmodel.type.DerivedType;
import weavec.cmodel.type.EnumConstantType;
import weavec.cmodel.type.EnumType;
import weavec.cmodel.type.FloatingType;
import weavec.cmodel.type.FunctionType;
import weavec.cmodel.type.IntegerType;
import weavec.cmodel.type.LabelType;
import weavec.cmodel.type.PointerType;
import weavec.cmodel.type.QualifiedType;
import weavec.cmodel.type.StructUnionType;
import weavec.cmodel.type.TypeQualifier;
import weavec.cmodel.type.TypedefType;
import weavec.cmodel.type.UnspecifiedType;
import weavec.cmodel.type.VoidType;

/**
 * converts a CType to a string representation
 * 
 * @author Michiel Hendriks
 */
public class CTypeToStringConverter
{
	public CTypeToStringConverter()
	{}

	public String convert(CType ctype)
	{
		StringBuffer sb = new StringBuffer();
		convertCType(sb, ctype);
		return sb.toString();
	}

	protected void convertCType(StringBuffer sb, CType ctype)
	{
		if (ctype instanceof VoidType)
		{
			convertVoidType(sb, (VoidType) ctype);
		}
		else if (ctype instanceof FloatingType)
		{
			convertFloatingType(sb, (FloatingType) ctype);
		}
		else if (ctype instanceof IntegerType)
		{
			convertIntegerType(sb, (IntegerType) ctype);
		}
		else if (ctype instanceof Builtin_va_listType)
		{
			convertBuiltin_va_listType(sb, (Builtin_va_listType) ctype);
		}
		else if (ctype instanceof EnumType)
		{
			convertEnumType(sb, (EnumType) ctype);
		}
		else if (ctype instanceof StructUnionType)
		{
			convertStructUnionType(sb, (StructUnionType) ctype);
		}
		else if (ctype instanceof DeclaredType)
		{
			convertDeclaredType(sb, (DeclaredType) ctype);
		}
		else if (ctype instanceof ArrayType)
		{
			convertArrayType(sb, (ArrayType) ctype);
		}
		else if (ctype instanceof FunctionType)
		{
			convertFunctionType(sb, (FunctionType) ctype);
		}
		else if (ctype instanceof PointerType)
		{
			convertPointerType(sb, (PointerType) ctype);
		}
		else if (ctype instanceof EnumConstantType)
		{
			convertEnumConstantType(sb, (EnumConstantType) ctype);
		}
		else if (ctype instanceof QualifiedType)
		{
			convertQualifiedType(sb, (QualifiedType) ctype);
		}
		else if (ctype instanceof TypedefType)
		{
			convertTypedefType(sb, (TypedefType) ctype);
		}
		else if (ctype instanceof DerivedType)
		{
			convertDerivedType(sb, (DerivedType) ctype);
		}
		else if (ctype instanceof LabelType)
		{
			convertLabelType(sb, (LabelType) ctype);
		}
		else if (ctype instanceof UnspecifiedType)
		{
			convertUnspecifiedType(sb, (UnspecifiedType) ctype);
		}
	}

	protected void convertVoidType(StringBuffer sb, VoidType type)
	{
		sb.append(type.getBaseName());
	}

	protected void convertFloatingType(StringBuffer sb, FloatingType type)
	{
		switch (type.getKind())
		{
			case FLOAT:
				sb.append("float");
				break;
			case DOUBLE:
				sb.append("double");
				break;
			case LONGDOUBLE:
				sb.append("long double");
				break;
		}
	}

	protected void convertIntegerType(StringBuffer sb, IntegerType type)
	{
		switch (type.getSign())
		{
			case SIGNED:
				// sb.append("signed ");
				break;
			case UNSIGNED:
				sb.append("unsigned ");
				break;
		}
		switch (type.getKind())
		{
			case CHAR:
				sb.append("char");
				break;
			case BOOLEAN:
			case INT:
				sb.append("int");
				break;
			case LONG:
				sb.append("long");
				break;
			case LONGLONG:
				sb.append("long long");
				break;
			case SHORT:
				sb.append("short");
				break;
		}
	}

	protected void convertBuiltin_va_listType(StringBuffer sb, Builtin_va_listType type)
	{
		sb.append("__builtin_va_list");
	}

	protected void convertEnumType(StringBuffer sb, EnumType type)
	{
		sb.append("enum ");
		sb.append(type.getDeclaration().getName());
	}

	protected void convertStructUnionType(StringBuffer sb, StructUnionType type)
	{
		if (type.getDeclaration().isStruct())
		{
			sb.append("struct ");
		}
		else if (type.getDeclaration().isUnion())
		{
			sb.append("union ");
		}
		sb.append(type.getDeclaration().getName());
	}

	protected void convertDeclaredType(StringBuffer sb, DeclaredType type)
	{
		System.err.println("convertDeclaredType not implemented");
	}

	protected void convertArrayType(StringBuffer sb, ArrayType type)
	{
		convertCType(sb, type.getBaseType());
		sb.append("[");
		if (type.getLength() > -1)
		{
			sb.append(type.getLength());
		}
		sb.append("]");
	}

	protected void convertFunctionType(StringBuffer sb, FunctionType type)
	{}

	protected void convertPointerType(StringBuffer sb, PointerType type)
	{
		convertCType(sb, type.getBaseType());
		sb.append("*");
	}

	protected void convertEnumConstantType(StringBuffer sb, EnumConstantType type)
	{
		System.err.println("convertEnumConstantType not implemented");
	}

	protected void convertQualifiedType(StringBuffer sb, QualifiedType type)
	{
		for (TypeQualifier tq : type.getQualifiers())
		{
			sb.append(tq.toString().toLowerCase());
			sb.append(" ");
		}
		convertCType(sb, type.getBaseType());
	}

	protected void convertTypedefType(StringBuffer sb, TypedefType type)
	{
		sb.append(type.getDeclaration().getName());
	}

	protected void convertDerivedType(StringBuffer sb, DerivedType type)
	{
		System.err.println("convertDerivedType not implemented");
	}

	protected void convertLabelType(StringBuffer sb, LabelType type)
	{
		System.err.println("convertLabelType not implemented");
	}

	protected void convertUnspecifiedType(StringBuffer sb, UnspecifiedType type)
	{
		System.err.println("convertUnspecifiedType not implemented");
	}
}
