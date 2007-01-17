/////////////////////////////////////////////////////////////////////////////////////////
// JSharp tree parser (target language is CSharp)
/////////////////////////////////////////////////////////////////////////////////////////

header {
/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
 
using System.Collections.Generic;
using Composestar.StarLight.CoreServices;
using Composestar.StarLight.Entities.LanguageModel;
}
 
options {
	language  = CSharp;
	namespace = "Composestar.StarLight.TypeAnalyzer.JSharp";
}

class JSharpWalker extends TreeParser;

options {
	importVocab = JSharp;
	defaultErrorHandler = true;
}

////////////////////////////////////////////////////////////////////////////////////////////////

{
	private DefinedType CreateDefinedClass(string name, string baseType, List<string> interfaces, Set<string> mods)
	{
		DefinedType dt = new DefinedType();
		dt.Name = name;
		dt.BaseType = baseType;
		dt.Interfaces = interfaces;
		dt.IsClass = true;
		dt.IsInterface = false;
		dt.IsAbstract = mods.Contains("abstract");
		dt.IsSealed = mods.Contains("final");
		dt.IsPublic = mods.Contains("public");
		return dt;
	}
	
	private DefinedType CreateDefinedInterface(string name, List<string> interfaces, Set<string> mods)
	{
		DefinedType dt = new DefinedType();
		dt.Name = name;
		dt.BaseType = null;
		dt.Interfaces = interfaces;
		dt.IsClass = false;
		dt.IsInterface = true;
		dt.IsAbstract = mods.Contains("abstract");
		dt.IsSealed = mods.Contains("final");
		dt.IsPublic = mods.Contains("public");
		return dt;
	}
	
	private FieldElement CreateFieldElement(string name, string type, Set<string> mods)
	{
		FieldElement fe = new FieldElement();
		fe.Name = name;
		fe.Type = type;
		fe.IsPrivate = mods.Contains("private");
		fe.IsPublic = mods.Contains("public") || mods.Contains("protected");
		fe.IsStatic = mods.Contains("static");
		return fe;
	}
	
	private MethodElement CreateMethodElement(
		string name, string returnType, List<ParameterElement> parameters, Set<string> mods)
	{
		MethodElement me = new MethodElement();
		me.Name = name;
		me.ReturnType = returnType;
		me.Parameters = parameters;
		me.IsConstructor = false;
		me.IsPrivate = mods.Contains("private");
		me.IsPublic = mods.Contains("public") || mods.Contains("protected");
		me.IsStatic = mods.Contains("static");
		me.IsAbstract = mods.Contains("abstract");
		me.IsVirtual = !mods.Contains("final") && !mods.Contains("static");
		return me;
	}
	
	private ParameterElement CreateParameterElement(string name, string type)
	{
		ParameterElement pe = new ParameterElement();
		pe.Name = name;
		pe.Type = type;
		pe.ParameterOption = ParameterOptions.In;
		return pe;
	}
}

/////////////////////////////////////// COMPILATION UNIT ///////////////////////////////////////

compilationUnit returns [CompilationUnit r = null]
	{
		string p = null;
		string i; List<string> ifs = new List<string>(); 
		DefinedType t; List<DefinedType> ts = new List<DefinedType>();
	}
	:	#(COMPILATION_UNIT
			(p=packageDefinition)?
			(i=importDefinition { ifs.Add(i); } )*
			(t=typeDefinition   {  ts.Add(t); } )*
		)
		{ r = new CompilationUnit(p, ifs, ts); }
	;

packageDefinition returns [string r = null]
	:	#(PACKAGE_DEF r=qname)
	;
	
importDefinition returns [string r = null]
	:	#(IMPORT r=qname)
	;

/////////////////////////////////////// TYPE DEFINITIONS ///////////////////////////////////////

typeDefinition returns [DefinedType r = null]
	{
		string n, e;
		Set<string> m;
		List<string> a, i;
	}
	:	#(CLASS_DEF 
			a=attributes m=modifiers n=identifier e=extendsClause i=implementsClause 
			{ r = CreateDefinedClass(n, e, i, m); r.EndPos = ((PosAST)#typeDefinition).getEndPos(); }
			typeBody[r]
		)
	|	#(INTERFACE_DEF 
			a=attributes m=modifiers n=identifier i=interfaceExtends
			{ r = CreateDefinedInterface(n, i, m); r.EndPos = ((PosAST)#typeDefinition).getEndPos(); }
			typeBody[r]
		)
	;
	
extendsClause returns [string r = null]
	:	#(EXTENDS_CLAUSE (r=qname)?)
	;
	
interfaceExtends returns [List<string> r = new List<string>()]
	{ string n; }
	:	#(EXTENDS_CLAUSE
			(n=qname { r.Add(n); } )*
		)
	;

implementsClause returns [List<string> r = new List<string>()]
	{ string n; }
	:	#(IMPLEMENTS_CLAUSE
			(n=qname { r.Add(n); } )*
		)
	;
	
/////////////////////////////////////// CLASSES ///////////////////////////////////////

typeBody[DefinedType d]
	:	#(CLASS_BODY (typeMember[d])*)
	;
	
typeMember[DefinedType d]
	{ MethodElement m; FieldElement f; DefinedType t; }
	:	ctorDef[d]
	|	f=fieldDef			{ d.Fields.Add(f); }
	|	m=methodDef[d]		{ d.Methods.Add(m); }
	|	t=typeDefinition	{ d.DefinedTypes.Add(t); }
	;
	
/////////////////////////////////////// FIELDS ///////////////////////////////////////

fieldDef returns [FieldElement r = null]
	{ Set<string> m; string n, t; }
	:	#(VARIABLE_DEF m=modifiers t=typeName n=identifier /* TODO: init */)
		{ r = CreateFieldElement(n, t, m); }
	;

/////////////////////////////////////// METHODS ///////////////////////////////////////

ctorDef [DefinedType d]
	{
		string n;
		Set<string> m;
		List<ParameterElement> p;
	}
	:	#(CTOR_DEF m=modifiers n=identifier p=formalParams (throwsClause)? methodBody[d])
	;
	
methodDef [DefinedType d] returns [MethodElement r = null]
	{
		string t, n;
		Set<string> m;
		List<string> a;
		List<ParameterElement> p;
	}
	:	#(METHOD_DEF a=attributes m=modifiers t=typeName n=identifier p=formalParams (throwsClause)? (methodBody[d])?)
		{ r = CreateMethodElement(n, t, p, m); }
	;
	
formalParams returns [List<ParameterElement> r = new List<ParameterElement>()]
	{ ParameterElement p; short ordinal = 0; }
	:	#(PARAMETERS (p=formalParam { p.Ordinal = ++ordinal; r.Add(p); })*)
	;
	
formalParam returns [ParameterElement r = null]
	{ Set<string> m; string t, n; }
	:	#(PARAMETER_DEF m=modifiers t=typeName n=identifier)
		{ r = CreateParameterElement(n, t); }
	;
	
throwsClause
	:	#(THROWS .) // TODO
	;
	
/////////////////////////////////////// VARIABLES ///////////////////////////////////////

variableDef
	:	VARIABLE_DEF //#(VARIABLE_DEF attributes typeName variableDecl variableInit)
	;

variableDecl
	{ string n; }
	:	n=identifier
	|	. // TODO: array
	;
	
variableInit
	:	.
	;
/*
variableInit
	:	expression
	|	. // TODO: array
	;
*/
/////////////////////////////////////// METHOD BODY ///////////////////////////////////////

methodBody [DefinedType d]
	:	#(SLIST (codeElement[d])*)
	;
	
expression [DefinedType d]
	:	#(EXPR (codeElement[d])*)
	;

codeElement [DefinedType d]
	{ string t; }
	:	t=typeName
		{ if (!JSharpAnalyzer.IsPrimitive(t)) d.AddReferencedType(t); }
	|	#(. (codeElement[d])*)
	;

/////////////////////////////////////// ATTRIBUTES ///////////////////////////////////////

attributes returns [List<string> r = new List<string>()]
	{ string a; }
	:	#(ATTRIBUTES 
			(a=attribute { r.Add(a); })*
		)
	;
	
attribute returns [string r = "attribute"]
	:	ATTR
	;
	
/////////////////////////////////////// MODIFIERS ///////////////////////////////////////

modifiers returns [Set<string> r = new Set<string>()]
	{ string m; }
	:	#(MODIFIERS 
			(m=modifier { r.Add(m); })*
		)
	;
	
modifier returns [string r = _t.getText()]
	:	"private"
	|   "public"
	|   "protected"
	|   "static"
	|   "transient"
	|   "final"
	|   "abstract"
	|   "native"
	|   "threadsafe"
	|   "synchronized"
	|   "const"
	|   "volatile"
	|	"strictfp"
	;
		
/////////////////////////////////////// NAMES ///////////////////////////////////////

qname returns [string r = null]
	:	s:QNAME { r = s.getText(); }
	;

typeName returns [string r = null]
	:	#(TYPE r=innerTypeName)
	;
	
innerTypeName returns [string r = null]
	:	r=classTypeName
	|	r=primTypeName
	|	#(ARRAY_OF r=innerTypeName) { r += "[]"; }
	;

classTypeName returns [string r = null]
	:	r=qname
	;
	
primTypeName returns [string r = _t.getText()]
	:	"void"
	|	"boolean"
	|	"byte"
	|	"ubyte"
	|	"char"
	|	"short"
	|	"int"
	|	"long"
	|	"float"
	|	"double"
	;

identifier returns [string r = null]
	:	i:IDENT { r = i.getText(); }
	;

////////////////////////////////////////////////////////////////////////////////////////////////
