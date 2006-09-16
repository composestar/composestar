/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

using System;
using System.IO;
using System.Xml;
using System.Reflection;
using System.Collections;

public class TypeHarvester
{
	private XmlTextWriter writer = null;
	private Hashtable pendingTypes;
	private Hashtable processedTypes;
	private Hashtable genericParameters;
	private bool writerEnabled = true;
	private int genericParameterCount = 0;
	
	public int count = 0;

    public TypeHarvester()
    {
		// both hashtables contain key = qualifiedname -> type pointer
		pendingTypes = new Hashtable(); // set initial size?
		processedTypes = new Hashtable(); // set initial size?
		genericParameters = new Hashtable();
	}

	#region Pending functionality

	private void AddPendingType(HarvesterType htype)
	{
		Type t = htype.theType;
		object key = t;

	//	Console.WriteLine("AddPendingType: " + t);

		// check if not done already
		if (processedTypes.Contains(key)) return;
		
		// check if not in pending
		if (pendingTypes.Contains(key)) return;
		
		// add to pending
		pendingTypes.Add(key, htype);
	}

	private void AddProcessedType(Type t)
	{
		object key = t;
		
		// check if in pending
		if (pendingTypes.Contains(key))
			pendingTypes.Remove(key);
		
		// check if in processed
		if (!processedTypes.Contains(key)) 
			processedTypes.Add(key, "dummy");
	}

	private void ProcessPendingTypes()
	{
		// currently just displays the pending list..
		IDictionaryEnumerator typeEnumerator = pendingTypes.GetEnumerator();

//		Console.WriteLine("\t-KEY-\t-VALUE-");
		while (typeEnumerator.MoveNext())
		{
//			Console.WriteLine("\t{0}:\t{1}", typeEnumerator.Key, typeEnumerator.Value);
			//Type singleType = (Type)typeEnumerator.Value;
			HarvesterType singleType = (HarvesterType)typeEnumerator.Value;
			//this.count++;
			ProcessTypes(singleType);
			// Enumerator pukes on MoveNext if hashmap is changed
			typeEnumerator = pendingTypes.GetEnumerator();
		}
	}

	#endregion

	public void Start(string outFolder)
    {
		if (writer != null)
		{
			Console.WriteLine("WARNING: Start() called twice!\n");
			return;
		}
		
		writer = new XmlTextWriter(outFolder + "types.xml", null);
		writer.WriteStartDocument();
		writer.Formatting = Formatting.Indented;
		
		// open root element
		writer.WriteStartElement("Types");
	}

	public void Finish()
	{
		if (writer == null)
		{
			Console.WriteLine("WARNING: Finish() called before Start!\n");
			return;
		}
		
		// close root element
		writer.WriteEndElement();
		writer.Close();
	}

	public void ProcessTypes(HarvesterType mytype)
	{
		Type t = mytype.theType;

		object key = t; // t.FullName
		if (processedTypes.Contains(key)) return;

		// this type is processed
		AddProcessedType(t);

		if (writerEnabled)
		{
			writer.WriteStartElement("Type");
			writer.WriteStartAttribute("", "name", "");
			writer.WriteString(t.Name); // qualified name?
			writer.WriteEndAttribute();

			// Start of elements of this type
			//<!ELEMENT AssemblyQualifiedName (#PCDATA)>
			writer.WriteStartElement("AssemblyQualifiedName");
			writer.WriteString(t.AssemblyQualifiedName);
			writer.WriteEndElement();
			//<!ELEMENT BaseType (#PCDATA)> TODO: Store for processing..
			writer.WriteStartElement("BaseType");
			if (t.BaseType != null) // may be null if Object
			{
				writer.WriteString(GetFullName(t.BaseType));
				HarvesterType htype = new HarvesterType(mytype.fromDLL, t.BaseType);
				AddPendingType(htype);
				//AddPendingType(t.BaseType);
			}
			writer.WriteEndElement();
			//<!ELEMENT FullName (#PCDATA)> TODO: maybe use this for name (change)
			writer.WriteStartElement("FullName");
			writer.WriteString(GetFullName(t));
			writer.WriteEndElement();
			//<!ELEMENT IsAbstract (#PCDATA)>
			writer.WriteStartElement("IsAbstract");
			writer.WriteString(t.IsAbstract.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsAnsiClass (#PCDATA)>
			writer.WriteStartElement("IsAnsiClass");
			writer.WriteString(t.IsAnsiClass.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsArray (#PCDATA)>
			writer.WriteStartElement("IsArray");
			writer.WriteString(t.IsArray.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsAutoClass (#PCDATA)>
			writer.WriteStartElement("IsAutoClass");
			writer.WriteString(t.IsAutoClass.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsAutoLayout (#PCDATA)>
			writer.WriteStartElement("IsAutoLayout");
			writer.WriteString(t.IsAutoLayout.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsByRef (#PCDATA)>
			writer.WriteStartElement("IsByRef");
			writer.WriteString(t.IsByRef.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsClass (#PCDATA)>
			writer.WriteStartElement("IsClass");
			writer.WriteString(t.IsClass.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsContextful (#PCDATA)>
			writer.WriteStartElement("IsContextful");
			writer.WriteString(t.IsContextful.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsEnum (#PCDATA)>
			writer.WriteStartElement("IsEnum");
			writer.WriteString(t.IsEnum.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsImport (#PCDATA)>
			writer.WriteStartElement("IsImport");
			writer.WriteString(t.IsImport.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsInterface (#PCDATA)>
			writer.WriteStartElement("IsInterface");
			writer.WriteString(t.IsInterface.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsMarshalByRef (#PCDATA)>
			writer.WriteStartElement("IsMarshalByRef");
			writer.WriteString(t.IsMarshalByRef.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsNestedFamANDAssem (#PCDATA)>
			writer.WriteStartElement("IsNestedFamANDAssem");
			writer.WriteString(t.IsNestedFamANDAssem.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsNestedAssembly (#PCDATA)>
			writer.WriteStartElement("IsNestedAssembly");
			writer.WriteString(t.IsNestedAssembly.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsNestedFamORAssem (#PCDATA)>
			writer.WriteStartElement("IsNestedFamORAssem");
			writer.WriteString(t.IsNestedFamORAssem.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsNestedPrivate (#PCDATA)>
			writer.WriteStartElement("IsNestedPrivate");
			writer.WriteString(t.IsNestedPrivate.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsNestedPublic (#PCDATA)>
			writer.WriteStartElement("IsNestedPublic");
			writer.WriteString(t.IsNestedPublic.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsNotPublic (#PCDATA)>
			writer.WriteStartElement("IsNotPublic");
			writer.WriteString(t.IsNotPublic.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsPointer (#PCDATA)>
			writer.WriteStartElement("IsPointer");
			writer.WriteString(t.IsPointer.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsPrimitive (#PCDATA)>
			writer.WriteStartElement("IsPrimitive");
			writer.WriteString(t.IsPrimitive.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsPublic (#PCDATA)>
			writer.WriteStartElement("IsPublic");
			writer.WriteString(t.IsPublic.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsSealed (#PCDATA)>
			writer.WriteStartElement("IsSealed");
			writer.WriteString(t.IsSealed.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsSerializable (#PCDATA)>
			writer.WriteStartElement("IsSerializable");
			writer.WriteString(t.IsSerializable.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsValueType (#PCDATA)>
			writer.WriteStartElement("IsValueType");
			writer.WriteString(t.IsValueType.ToString());
			writer.WriteEndElement();
			//<!ELEMENT Module (#PCDATA)>
			writer.WriteStartElement("Module");
			writer.WriteString(t.Module.FullyQualifiedName);
			writer.WriteEndElement();
			//<!ELEMENT Namespace (#PCDATA)>
			writer.WriteStartElement("Namespace");
			writer.WriteString(t.Namespace);
			writer.WriteEndElement();
			//<!ELEMENT UnderlyingSystemType (#PCDATA)>
			writer.WriteStartElement("UnderlyingSystemType");
			if (t.UnderlyingSystemType != null)
			{
				writer.WriteString(t.UnderlyingSystemType.FullName); // TODO: handle type
				HarvesterType htype = new HarvesterType(mytype.fromDLL, t.UnderlyingSystemType);
				AddPendingType(htype);
				//AddPendingType(t.UnderlyingSystemType);
			}
			writer.WriteEndElement();
			//<!ELEMENT HashCode (#PCDATA)>
			writer.WriteStartElement("HashCode");
			writer.WriteString(t.GetHashCode().ToString());
			writer.WriteEndElement();

			// process methods
			MethodInfo[] methods = t.GetMethods(BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic);
			MethodInfo[] methodsDeclaredHere = t.GetMethods(BindingFlags.DeclaredOnly | BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic);
			foreach (MethodInfo m in methods)
			{
				ProcessMethods(m, ((IList)methodsDeclaredHere).Contains(m), mytype.fromDLL);
			}

			// process fields (WH20041018)
			FieldInfo[] fields = t.GetFields(BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic);
			FieldInfo[] fieldsDeclaredHere = t.GetFields(BindingFlags.DeclaredOnly | BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic);
			foreach (FieldInfo f in fields)
			{
				ProcessFields(f, ((IList)fieldsDeclaredHere).Contains(f), mytype.fromDLL);
			}

			// Add implemented interfaces (WH20041129)
			Type[] interfaces = t.GetInterfaces();
			foreach (Type interf in interfaces)
			{
				//<!ELEMENT ImplementedInterface (#PCDATA)>
				writer.WriteStartElement("ImplementedInterface");
				writer.WriteString(interf.FullName);
				HarvesterType htype = new HarvesterType(mytype.fromDLL, interf);
				AddPendingType(htype);
				//AddPendingType(interf);
				writer.WriteEndElement(); // End of implemented interfaces
			}

			// add fromDLL
			writer.WriteStartElement("FromDLL");
			writer.WriteString("\"" + mytype.fromDLL + "\"");
			writer.WriteEndElement();

			// end of Type
			writer.WriteEndElement();
		}
		else
		{
			// do not write the type to the xml file
			// but still check for underlying types, base types, interfaces, fields and methods
			if (t.BaseType != null) // may be null if Object
			{
				HarvesterType htype = new HarvesterType(mytype.fromDLL, t.BaseType);
				AddPendingType(htype);
			}
			if (t.UnderlyingSystemType != null)
			{
				HarvesterType htype = new HarvesterType(mytype.fromDLL, t.UnderlyingSystemType);
				AddPendingType(htype);
			}
			// process methods
			MethodInfo[] methods = t.GetMethods(BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic);
			MethodInfo[] methodsDeclaredHere = t.GetMethods(BindingFlags.DeclaredOnly | BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic);
			foreach (MethodInfo m in methods)
			{
				ProcessMethods(m, ((IList)methodsDeclaredHere).Contains(m), mytype.fromDLL);
			}

			// process fields (WH20041018)
			FieldInfo[] fields = t.GetFields(BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic);
			FieldInfo[] fieldsDeclaredHere = t.GetFields(BindingFlags.DeclaredOnly | BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic);
			foreach (FieldInfo f in fields)
			{
				ProcessFields(f, ((IList)fieldsDeclaredHere).Contains(f), mytype.fromDLL);
			}

			// Add implemented interfaces (WH20041129)
			Type[] interfaces = t.GetInterfaces();
			foreach (Type interf in interfaces)
			{
				HarvesterType htype = new HarvesterType(mytype.fromDLL, interf);
				AddPendingType(htype);
			}
		}
	}

	public void ProcessMethods(MethodInfo m, bool declaredHere, string fromDLL)
	{
		if (writerEnabled)
		{
			writer.WriteStartElement("MethodInfo");
			writer.WriteStartAttribute("", "name", "");
			writer.WriteString(m.Name); // qualified name?
			writer.WriteEndAttribute();

			//<!ELEMENT CallingConvention (#PCDATA)>
			writer.WriteStartElement("CallingConvention");
			writer.WriteString(((int)m.CallingConvention).ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsAbstract (#PCDATA)>
			writer.WriteStartElement("IsAbstract");
			writer.WriteString(m.IsAbstract.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsAssembly (#PCDATA)>
			writer.WriteStartElement("IsAssembly");
			writer.WriteString(m.IsAssembly.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsConstructor (#PCDATA)>
			writer.WriteStartElement("IsConstructor");
			writer.WriteString(m.IsConstructor.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsFamily (#PCDATA)>
			writer.WriteStartElement("IsFamily");
			writer.WriteString(m.IsFamily.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsFamilyAndAssembly (#PCDATA)> // inconsistent with type, .NET error not me
			writer.WriteStartElement("IsFamilyAndAssembly");
			writer.WriteString(m.IsFamilyAndAssembly.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsFamilyOrAssembly (#PCDATA)>
			writer.WriteStartElement("IsFamilyOrAssembly");
			writer.WriteString(m.IsFamilyOrAssembly.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsFinal (#PCDATA)>
			writer.WriteStartElement("IsFinal");
			writer.WriteString(m.IsFinal.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsHideBySig (#PCDATA)>
			writer.WriteStartElement("IsHideBySig");
			writer.WriteString(m.IsHideBySig.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsPrivate (#PCDATA)>
			writer.WriteStartElement("IsPrivate");
			writer.WriteString(m.IsPrivate.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsPublic (#PCDATA)>
			writer.WriteStartElement("IsPublic");
			writer.WriteString(m.IsPublic.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsStatic (#PCDATA)>
			writer.WriteStartElement("IsStatic");
			writer.WriteString(m.IsStatic.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsVirtual (#PCDATA)>
			writer.WriteStartElement("IsVirtual");
			writer.WriteString(m.IsVirtual.ToString());
			writer.WriteEndElement();
			//<!ELEMENT IsDeclaredHere (#PCDATA)>
			writer.WriteStartElement("IsDeclaredHere");
			writer.WriteString(declaredHere.ToString());
			writer.WriteEndElement();
			//<!ELEMENT HashCode (#PCDATA)>
			writer.WriteStartElement("HashCode");
			writer.WriteString(m.GetHashCode().ToString());
			writer.WriteEndElement();
			//<!ELEMENT ReturnType (#PCDATA)>  // TODO: Store for processing
			writer.WriteStartElement("ReturnType");

			if (m.ReturnType != null) // may be null if Object
			{
				writer.WriteString(GetFullName(m.ReturnType));
				HarvesterType htype = new HarvesterType(fromDLL, m.ReturnType);
				AddPendingType(htype);
				//AddPendingType(m.ReturnType);
			}

			writer.WriteEndElement();

			//process parameters
			ParameterInfo[] parameters = m.GetParameters();
			if (parameters.Length != 0)
			{
				foreach (ParameterInfo paramInfo in parameters)
				{
					ProcessParameters(paramInfo);
				}
			}

			// end methodinfo
			writer.WriteEndElement();
		}
		else
		{
			// skip writing to xml but still look for return type and parameters
			if (m.ReturnType != null) // may be null if Object
			{
				HarvesterType htype = new HarvesterType(fromDLL, m.ReturnType);
				AddPendingType(htype);
			}

			//process parameters
			ParameterInfo[] parameters = m.GetParameters();
			if (parameters.Length != 0)
			{
				foreach (ParameterInfo paramInfo in parameters)
				{
					ProcessParameters(paramInfo);
				}
			}
		}
	}

	public void ProcessParameters(ParameterInfo p)
  {
	  if (writerEnabled)
	  {	
		  writer.WriteStartElement("ParameterInfo");
		  writer.WriteStartAttribute("", "name" , "");
		  writer.WriteString(p.Name);
		  writer.WriteEndAttribute();

		  //<!ELEMENT IsIn (#PCDATA)>
		  writer.WriteStartElement("IsIn");
		  writer.WriteString(p.IsIn.ToString());
		  writer.WriteEndElement();
		  //<!ELEMENT IsLcid (#PCDATA)>
		  writer.WriteStartElement("IsLcid");
		  writer.WriteString(p.IsLcid.ToString());
		  writer.WriteEndElement();
		  //<!ELEMENT IsOptional (#PCDATA)>
		  writer.WriteStartElement("IsOptional");
		  writer.WriteString(p.IsOptional.ToString());
		  writer.WriteEndElement();
		  //<!ELEMENT IsOut (#PCDATA)>
		  writer.WriteStartElement("IsOut");
		  writer.WriteString(p.IsOut.ToString());
		  writer.WriteEndElement();
		  //<!ELEMENT IsRetval (#PCDATA)>
		  writer.WriteStartElement("IsRetval");
		  writer.WriteString(p.IsRetval.ToString());
		  writer.WriteEndElement();
		  //<!ELEMENT ParameterType (#PCDATA)>
		  writer.WriteStartElement("ParameterType");
		  writer.WriteString(GetFullName(p.ParameterType));
		  writer.WriteEndElement();
		  //<!ELEMENT Position (#PCDATA)>
		  writer.WriteStartElement("Position");
		  writer.WriteString(p.Position.ToString());
		  writer.WriteEndElement();
		  //<!ELEMENT HashCode (#PCDATA)>
		  writer.WriteStartElement("HashCode");
		  writer.WriteString(p.GetHashCode().ToString());
		  writer.WriteEndElement();

		  // end parameter
		  writer.WriteEndElement();
	  }
  }

	public void ProcessFields(FieldInfo f, bool declaredHere, string fromDLL)
	{
	  if (writerEnabled)
	  {
		  writer.WriteStartElement("FieldInfo");
		  writer.WriteStartAttribute("", "name" , "");
		  writer.WriteString(f.Name); // qualified name?
		  writer.WriteEndAttribute();

		  //<!ELEMENT FieldType (#PCDATA)>
		  writer.WriteStartElement("FieldType");
		  writer.WriteString(GetFullName(f.FieldType));
		  HarvesterType htype = new HarvesterType(fromDLL,f.FieldType);
		  AddPendingType(htype);
		  writer.WriteEndElement();
		
		  //<!ELEMENT IsAssembly (#PCDATA)>
		  writer.WriteStartElement("IsAssembly");
		  writer.WriteString(f.IsAssembly.ToString());
		  writer.WriteEndElement();
		
		  //<!ELEMENT IsFamily(#PCDATA)>
		  writer.WriteStartElement("IsFamily");
		  writer.WriteString(f.IsFamily.ToString());
		  writer.WriteEndElement();
		
		  //<!ELEMENT IsFamilyAndAssembly (#PCDATA)>
		  writer.WriteStartElement("IsFamilyAndAssembly");
		  writer.WriteString(f.IsFamilyAndAssembly.ToString());
		  writer.WriteEndElement();
		
		  //<!ELEMENT IsFamilyOrAssembly (#PCDATA)>
		  writer.WriteStartElement("IsFamilyOrAssembly");
		  writer.WriteString(f.IsFamilyOrAssembly.ToString());
		  writer.WriteEndElement();
		
		  //<!ELEMENT IsInitOnly (#PCDATA)>
		  writer.WriteStartElement("IsInitOnly");
		  writer.WriteString(f.IsInitOnly.ToString());
		  writer.WriteEndElement();
		
		  //<!ELEMENT IsLiteral (#PCDATA)>
		  writer.WriteStartElement("IsLiteral");
		  writer.WriteString(f.IsLiteral.ToString());
		  writer.WriteEndElement();
		
		  /*
		  Uncomment these if you think you need them for some reason
		  //<!ELEMENT IsNotSerialized (#PCDATA)>
		  writer.WriteStartElement("IsNotSerialized");
		  writer.WriteString(f.IsNotSerialized.ToString());
		  writer.WriteEndElement();
		
		  //<!ELEMENT IsPinvokeImpl (#PCDATA)>
		  writer.WriteStartElement("IsPinvokeImpl");
		  writer.WriteString(f.IsPinvokeImpl.ToString());
		  writer.WriteEndElement(); */
		
		  //<!ELEMENT IsPrivate (#PCDATA)>
		  writer.WriteStartElement("IsPrivate");
		  writer.WriteString(f.IsPrivate.ToString());
		  writer.WriteEndElement();

		  //<!ELEMENT IsPublic (#PCDATA)>
		  writer.WriteStartElement("IsPublic");
		  writer.WriteString(f.IsPublic.ToString());
		  writer.WriteEndElement();

		  //<!ELEMENT IsStatic (#PCDATA)>
		  writer.WriteStartElement("IsStatic");
		  writer.WriteString(f.IsStatic.ToString());
		  writer.WriteEndElement();

		  //<!ELEMENT IsDeclaredHere (#PCDATA)>
		  writer.WriteStartElement("IsDeclaredHere");
		  writer.WriteString(declaredHere.ToString());
		  writer.WriteEndElement();

		  //<!ELEMENT HashCode (#PCDATA)>
		  writer.WriteStartElement("HashCode");
		  writer.WriteString(f.GetHashCode().ToString());
		  writer.WriteEndElement();

		  // end fieldinfo
		  writer.WriteEndElement();
	  }
	  else 
	  {	
		  // skip writing to xml but still look for fieldtypes
		  if(f.FieldType != null) // may be null if Object
		  {
			  HarvesterType htype = new HarvesterType(fromDLL,f.FieldType);
			  AddPendingType(htype);
		 }
	  }
	}

	private string GetFullName(Type t)
	{
		if (t.FullName == null)
		{
			if (genericParameters.Contains(t))
				return (string)genericParameters[t];
			else
			{
				string name = "GenericTypeParam" + (genericParameterCount++);
				genericParameters[t] = name;
				return name;
			}
		}
		else
			return t.FullName;
	}

	public static int Main(string[] args)
	{
		if (args.Length == 0)
		{
			Console.WriteLine("Usage: <outputfolder> [<dll names>]");
		}
		else
		{
			TypeHarvester th = new TypeHarvester();

			// forall dll's check out all types
			String outFolder = null;
			foreach (string arg in args)
			{
				if (outFolder == null)
				{
					outFolder = arg;
					th.Start(outFolder);
					continue;
				}

				string dll = arg;
				if (dll.IndexOf("!") == 0)
				{
					dll = dll.Substring(1);
					th.writerEnabled = false;
				}
				else
					th.writerEnabled = true;

				Assembly asm = LoadAssembly(dll);
				if (asm == null) continue;

				Type[] types = null;
				try {
					types = asm.GetTypes();
					foreach (Type singleType in types)
					{
						HarvesterType htype = new HarvesterType(dll, singleType);
						th.ProcessTypes(htype);
						th.count++;
					}
				}
				catch (Exception e) {
					string inner = "";
					while (e != null)
					{
						Console.Error.WriteLine(inner + "Exception:" + e.Message);
						inner += "Inner";
						e = e.InnerException;
					}
					return 1; // Error!
				}

				// take care of the pending types
				th.ProcessPendingTypes();
			}

			th.Finish();
			//Console.WriteLine(""+thing.count+ " types found!");
		}
		return 0;
	}

	private static Assembly LoadAssembly(String dll)
	{
		try {
			return Assembly.LoadFrom(dll);
		}
		catch (FileNotFoundException) {
			Console.Error.WriteLine("File not found " + dll + ". Skipping.");
			return null;
		}
		catch (ArgumentException) {
			Console.Error.WriteLine("Cannot harvest type of " + dll + ". Skipping.");
			return null;
		}
	}

	public class HarvesterType
	{
		public String fromDLL;
		public Type theType;

		public HarvesterType(String fromDLL, Type t)
		{
			this.fromDLL = fromDLL;
			this.theType = t;
		}
	}
}