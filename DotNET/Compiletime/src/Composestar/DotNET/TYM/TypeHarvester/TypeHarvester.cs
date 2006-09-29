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
	private XmlTextWriter m_writer;
	private Hashtable m_pendingTypes;
	private Hashtable m_processedTypes;
	private Hashtable m_genericParameters;

	private bool m_write = true;
	private int m_gpc = 0;

	public TypeHarvester(string outFolder)
	{
		string target = Path.Combine(outFolder, "types.xml");
		Debug.Out(Debug.MODE_DEBUG, "TypeHarvester: outFolder=" + outFolder + ", target=" + target);

		m_writer = new XmlTextWriter(target, null);
		
		// these hashtables all map qname -> type
		m_pendingTypes = new Hashtable();
		m_processedTypes = new Hashtable();
		m_genericParameters = new Hashtable();
	}

	#region Pending functionality

	private void AddPendingType(HarvesterType htype)
	{
		Type t = htype.type;
		object key = t;

		//	Console.WriteLine("AddPendingType: " + t);

		// check if not done already
		if (m_processedTypes.Contains(key)) return;

		// check if not in pending
		if (m_pendingTypes.Contains(key)) return;

		// add to pending
		m_pendingTypes.Add(key, htype);
	}

	private void AddProcessedType(Type t)
	{
		object key = t;

		// check if in pending
		if (m_pendingTypes.Contains(key))
			m_pendingTypes.Remove(key);

		// check if in processed
		if (!m_processedTypes.Contains(key))
			m_processedTypes.Add(key, "dummy");
	}

	private void ProcessPendingTypes()
	{
		IDictionaryEnumerator typeEnumerator = m_pendingTypes.GetEnumerator();
		while (typeEnumerator.MoveNext())
		{
			HarvesterType ht = (HarvesterType)typeEnumerator.Value;
			ProcessType(ht);
			
			// Enumerator pukes on MoveNext if hashmap is changed
			typeEnumerator = m_pendingTypes.GetEnumerator();
		}
	}

	#endregion

	public void ProcessDlls(IList dlls)
	{
		m_writer.Formatting = Formatting.Indented;
		m_writer.WriteStartDocument();

		// open root element
		m_writer.WriteStartElement("Types");

		// process dlls
		foreach (string dll in dlls)
		{
			Assembly asm = LoadAssembly(dll);
			if (asm == null) continue;

			ProcessAssembly(dll, asm);
		}

		// close root element
		m_writer.WriteEndElement();
		m_writer.Close();
	}

	private void ProcessAssembly(string dll, Assembly asm)
	{
		if (dll.IndexOf("!") == 0)
		{
			dll = dll.Substring(1);
			m_write = false;
		}
		else
			m_write = true;

		Type[] types = asm.GetTypes();
		foreach (Type t in types)
		{
			HarvesterType ht = new HarvesterType("dll", t);
			ProcessType(ht);
		}

		// take care of the pending types
		ProcessPendingTypes();
	}

	private void ProcessType(HarvesterType ht)
	{
		Type t = ht.type;
		if (m_processedTypes.Contains(t)) return;

		AddProcessedType(t);

		if (m_write)
		{
			m_writer.WriteStartElement("Type");
			m_writer.WriteStartAttribute("", "name", "");
			m_writer.WriteString(t.Name); // qualified name?
			m_writer.WriteEndAttribute();

			// Start of elements of this type
			//<!ELEMENT AssemblyQualifiedName (#PCDATA)>
			m_writer.WriteStartElement("AssemblyQualifiedName");
			m_writer.WriteString(t.AssemblyQualifiedName);
			m_writer.WriteEndElement();
			//<!ELEMENT BaseType (#PCDATA)> TODO: Store for processing..
			m_writer.WriteStartElement("BaseType");
			if (t.BaseType != null) // may be null if Object
			{
				m_writer.WriteString(GetFullName(t.BaseType));
				HarvesterType htype = new HarvesterType(ht.dll, t.BaseType);
				AddPendingType(htype);
				//AddPendingType(t.BaseType);
			}
			m_writer.WriteEndElement();
			//<!ELEMENT FullName (#PCDATA)> TODO: maybe use this for name (change)
			m_writer.WriteStartElement("FullName");
			m_writer.WriteString(GetFullName(t));
			m_writer.WriteEndElement();
			//<!ELEMENT IsAbstract (#PCDATA)>
			m_writer.WriteStartElement("IsAbstract");
			m_writer.WriteString(t.IsAbstract.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsAnsiClass (#PCDATA)>
			m_writer.WriteStartElement("IsAnsiClass");
			m_writer.WriteString(t.IsAnsiClass.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsArray (#PCDATA)>
			m_writer.WriteStartElement("IsArray");
			m_writer.WriteString(t.IsArray.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsAutoClass (#PCDATA)>
			m_writer.WriteStartElement("IsAutoClass");
			m_writer.WriteString(t.IsAutoClass.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsAutoLayout (#PCDATA)>
			m_writer.WriteStartElement("IsAutoLayout");
			m_writer.WriteString(t.IsAutoLayout.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsByRef (#PCDATA)>
			m_writer.WriteStartElement("IsByRef");
			m_writer.WriteString(t.IsByRef.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsClass (#PCDATA)>
			m_writer.WriteStartElement("IsClass");
			m_writer.WriteString(t.IsClass.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsContextful (#PCDATA)>
			m_writer.WriteStartElement("IsContextful");
			m_writer.WriteString(t.IsContextful.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsEnum (#PCDATA)>
			m_writer.WriteStartElement("IsEnum");
			m_writer.WriteString(t.IsEnum.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsImport (#PCDATA)>
			m_writer.WriteStartElement("IsImport");
			m_writer.WriteString(t.IsImport.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsInterface (#PCDATA)>
			m_writer.WriteStartElement("IsInterface");
			m_writer.WriteString(t.IsInterface.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsMarshalByRef (#PCDATA)>
			m_writer.WriteStartElement("IsMarshalByRef");
			m_writer.WriteString(t.IsMarshalByRef.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsNestedFamANDAssem (#PCDATA)>
			m_writer.WriteStartElement("IsNestedFamANDAssem");
			m_writer.WriteString(t.IsNestedFamANDAssem.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsNestedAssembly (#PCDATA)>
			m_writer.WriteStartElement("IsNestedAssembly");
			m_writer.WriteString(t.IsNestedAssembly.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsNestedFamORAssem (#PCDATA)>
			m_writer.WriteStartElement("IsNestedFamORAssem");
			m_writer.WriteString(t.IsNestedFamORAssem.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsNestedPrivate (#PCDATA)>
			m_writer.WriteStartElement("IsNestedPrivate");
			m_writer.WriteString(t.IsNestedPrivate.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsNestedPublic (#PCDATA)>
			m_writer.WriteStartElement("IsNestedPublic");
			m_writer.WriteString(t.IsNestedPublic.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsNotPublic (#PCDATA)>
			m_writer.WriteStartElement("IsNotPublic");
			m_writer.WriteString(t.IsNotPublic.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsPointer (#PCDATA)>
			m_writer.WriteStartElement("IsPointer");
			m_writer.WriteString(t.IsPointer.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsPrimitive (#PCDATA)>
			m_writer.WriteStartElement("IsPrimitive");
			m_writer.WriteString(t.IsPrimitive.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsPublic (#PCDATA)>
			m_writer.WriteStartElement("IsPublic");
			m_writer.WriteString(t.IsPublic.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsSealed (#PCDATA)>
			m_writer.WriteStartElement("IsSealed");
			m_writer.WriteString(t.IsSealed.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsSerializable (#PCDATA)>
			m_writer.WriteStartElement("IsSerializable");
			m_writer.WriteString(t.IsSerializable.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsValueType (#PCDATA)>
			m_writer.WriteStartElement("IsValueType");
			m_writer.WriteString(t.IsValueType.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT Module (#PCDATA)>
			m_writer.WriteStartElement("Module");
			m_writer.WriteString(t.Module.FullyQualifiedName);
			m_writer.WriteEndElement();
			//<!ELEMENT Namespace (#PCDATA)>
			m_writer.WriteStartElement("Namespace");
			m_writer.WriteString(t.Namespace);
			m_writer.WriteEndElement();
			//<!ELEMENT UnderlyingSystemType (#PCDATA)>
			m_writer.WriteStartElement("UnderlyingSystemType");
			if (t.UnderlyingSystemType != null)
			{
				m_writer.WriteString(t.UnderlyingSystemType.FullName); // TODO: handle type
				HarvesterType htype = new HarvesterType(ht.dll, t.UnderlyingSystemType);
				AddPendingType(htype);
				//AddPendingType(t.UnderlyingSystemType);
			}
			m_writer.WriteEndElement();
			//<!ELEMENT HashCode (#PCDATA)>
			m_writer.WriteStartElement("HashCode");
			m_writer.WriteString(t.GetHashCode().ToString());
			m_writer.WriteEndElement();

			// process methods
			MethodInfo[] methods = t.GetMethods(BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic);
			MethodInfo[] methodsDeclaredHere = t.GetMethods(BindingFlags.DeclaredOnly | BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic);
			foreach (MethodInfo m in methods)
			{
				ProcessMethods(m, ((IList)methodsDeclaredHere).Contains(m), ht.dll);
			}

			// process fields (WH20041018)
			FieldInfo[] fields = t.GetFields(BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic);
			FieldInfo[] fieldsDeclaredHere = t.GetFields(BindingFlags.DeclaredOnly | BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic);
			foreach (FieldInfo f in fields)
			{
				ProcessFields(f, ((IList)fieldsDeclaredHere).Contains(f), ht.dll);
			}

			// Add implemented interfaces (WH20041129)
			Type[] interfaces = t.GetInterfaces();
			foreach (Type interf in interfaces)
			{
				//<!ELEMENT ImplementedInterface (#PCDATA)>
				m_writer.WriteStartElement("ImplementedInterface");
				m_writer.WriteString(interf.FullName);
				HarvesterType htype = new HarvesterType(ht.dll, interf);
				AddPendingType(htype);
				//AddPendingType(interf);
				m_writer.WriteEndElement(); // End of implemented interfaces
			}

			// add fromDLL
			m_writer.WriteStartElement("FromDLL");
			m_writer.WriteString("\"" + ht.dll + "\"");
			m_writer.WriteEndElement();

			// end of Type
			m_writer.WriteEndElement();
		}
		else
		{
			// do not write the type to the xml file
			// but still check for underlying types, base types, interfaces, fields and methods
			if (t.BaseType != null) // may be null if Object
			{
				HarvesterType htype = new HarvesterType(ht.dll, t.BaseType);
				AddPendingType(htype);
			}
			if (t.UnderlyingSystemType != null)
			{
				HarvesterType htype = new HarvesterType(ht.dll, t.UnderlyingSystemType);
				AddPendingType(htype);
			}
			// process methods
			MethodInfo[] methods = t.GetMethods(BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic);
			MethodInfo[] methodsDeclaredHere = t.GetMethods(BindingFlags.DeclaredOnly | BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic);
			foreach (MethodInfo m in methods)
			{
				ProcessMethods(m, ((IList)methodsDeclaredHere).Contains(m), ht.dll);
			}

			// process fields (WH20041018)
			FieldInfo[] fields = t.GetFields(BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic);
			FieldInfo[] fieldsDeclaredHere = t.GetFields(BindingFlags.DeclaredOnly | BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic);
			foreach (FieldInfo f in fields)
			{
				ProcessFields(f, ((IList)fieldsDeclaredHere).Contains(f), ht.dll);
			}

			// Add implemented interfaces (WH20041129)
			Type[] interfaces = t.GetInterfaces();
			foreach (Type interf in interfaces)
			{
				HarvesterType htype = new HarvesterType(ht.dll, interf);
				AddPendingType(htype);
			}
		}
	}

	private void ProcessMethods(MethodInfo m, bool declaredHere, string fromDLL)
	{
		if (m_write)
		{
			m_writer.WriteStartElement("MethodInfo");
			m_writer.WriteStartAttribute("", "name", "");
			m_writer.WriteString(m.Name); // qualified name?
			m_writer.WriteEndAttribute();

			//<!ELEMENT CallingConvention (#PCDATA)>
			m_writer.WriteStartElement("CallingConvention");
			m_writer.WriteString(((int)m.CallingConvention).ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsAbstract (#PCDATA)>
			m_writer.WriteStartElement("IsAbstract");
			m_writer.WriteString(m.IsAbstract.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsAssembly (#PCDATA)>
			m_writer.WriteStartElement("IsAssembly");
			m_writer.WriteString(m.IsAssembly.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsConstructor (#PCDATA)>
			m_writer.WriteStartElement("IsConstructor");
			m_writer.WriteString(m.IsConstructor.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsFamily (#PCDATA)>
			m_writer.WriteStartElement("IsFamily");
			m_writer.WriteString(m.IsFamily.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsFamilyAndAssembly (#PCDATA)> // inconsistent with type, .NET error not me
			m_writer.WriteStartElement("IsFamilyAndAssembly");
			m_writer.WriteString(m.IsFamilyAndAssembly.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsFamilyOrAssembly (#PCDATA)>
			m_writer.WriteStartElement("IsFamilyOrAssembly");
			m_writer.WriteString(m.IsFamilyOrAssembly.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsFinal (#PCDATA)>
			m_writer.WriteStartElement("IsFinal");
			m_writer.WriteString(m.IsFinal.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsHideBySig (#PCDATA)>
			m_writer.WriteStartElement("IsHideBySig");
			m_writer.WriteString(m.IsHideBySig.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsPrivate (#PCDATA)>
			m_writer.WriteStartElement("IsPrivate");
			m_writer.WriteString(m.IsPrivate.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsPublic (#PCDATA)>
			m_writer.WriteStartElement("IsPublic");
			m_writer.WriteString(m.IsPublic.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsStatic (#PCDATA)>
			m_writer.WriteStartElement("IsStatic");
			m_writer.WriteString(m.IsStatic.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsVirtual (#PCDATA)>
			m_writer.WriteStartElement("IsVirtual");
			m_writer.WriteString(m.IsVirtual.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsDeclaredHere (#PCDATA)>
			m_writer.WriteStartElement("IsDeclaredHere");
			m_writer.WriteString(declaredHere.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT HashCode (#PCDATA)>
			m_writer.WriteStartElement("HashCode");
			m_writer.WriteString(m.GetHashCode().ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT ReturnType (#PCDATA)>  // TODO: Store for processing
			m_writer.WriteStartElement("ReturnType");

			if (m.ReturnType != null) // may be null if Object
			{
				m_writer.WriteString(GetFullName(m.ReturnType));
				HarvesterType htype = new HarvesterType(fromDLL, m.ReturnType);
				AddPendingType(htype);
				//AddPendingType(m.ReturnType);
			}

			m_writer.WriteEndElement();

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
			m_writer.WriteEndElement();
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

	private void ProcessParameters(ParameterInfo p)
	{
		if (m_write)
		{
			m_writer.WriteStartElement("ParameterInfo");
			m_writer.WriteStartAttribute("", "name", "");
			m_writer.WriteString(p.Name);
			m_writer.WriteEndAttribute();

			//<!ELEMENT IsIn (#PCDATA)>
			m_writer.WriteStartElement("IsIn");
			m_writer.WriteString(p.IsIn.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsLcid (#PCDATA)>
			m_writer.WriteStartElement("IsLcid");
			m_writer.WriteString(p.IsLcid.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsOptional (#PCDATA)>
			m_writer.WriteStartElement("IsOptional");
			m_writer.WriteString(p.IsOptional.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsOut (#PCDATA)>
			m_writer.WriteStartElement("IsOut");
			m_writer.WriteString(p.IsOut.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT IsRetval (#PCDATA)>
			m_writer.WriteStartElement("IsRetval");
			m_writer.WriteString(p.IsRetval.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT ParameterType (#PCDATA)>
			m_writer.WriteStartElement("ParameterType");
			m_writer.WriteString(GetFullName(p.ParameterType));
			m_writer.WriteEndElement();
			//<!ELEMENT Position (#PCDATA)>
			m_writer.WriteStartElement("Position");
			m_writer.WriteString(p.Position.ToString());
			m_writer.WriteEndElement();
			//<!ELEMENT HashCode (#PCDATA)>
			m_writer.WriteStartElement("HashCode");
			m_writer.WriteString(p.GetHashCode().ToString());
			m_writer.WriteEndElement();

			// end parameter
			m_writer.WriteEndElement();
		}
	}

	private void ProcessFields(FieldInfo f, bool declaredHere, string fromDLL)
	{
		if (m_write)
		{
			m_writer.WriteStartElement("FieldInfo");
			m_writer.WriteStartAttribute("", "name", "");
			m_writer.WriteString(f.Name); // qualified name?
			m_writer.WriteEndAttribute();

			//<!ELEMENT FieldType (#PCDATA)>
			m_writer.WriteStartElement("FieldType");
			m_writer.WriteString(GetFullName(f.FieldType));
			HarvesterType htype = new HarvesterType(fromDLL, f.FieldType);
			AddPendingType(htype);
			m_writer.WriteEndElement();

			//<!ELEMENT IsAssembly (#PCDATA)>
			m_writer.WriteStartElement("IsAssembly");
			m_writer.WriteString(f.IsAssembly.ToString());
			m_writer.WriteEndElement();

			//<!ELEMENT IsFamily(#PCDATA)>
			m_writer.WriteStartElement("IsFamily");
			m_writer.WriteString(f.IsFamily.ToString());
			m_writer.WriteEndElement();

			//<!ELEMENT IsFamilyAndAssembly (#PCDATA)>
			m_writer.WriteStartElement("IsFamilyAndAssembly");
			m_writer.WriteString(f.IsFamilyAndAssembly.ToString());
			m_writer.WriteEndElement();

			//<!ELEMENT IsFamilyOrAssembly (#PCDATA)>
			m_writer.WriteStartElement("IsFamilyOrAssembly");
			m_writer.WriteString(f.IsFamilyOrAssembly.ToString());
			m_writer.WriteEndElement();

			//<!ELEMENT IsInitOnly (#PCDATA)>
			m_writer.WriteStartElement("IsInitOnly");
			m_writer.WriteString(f.IsInitOnly.ToString());
			m_writer.WriteEndElement();

			//<!ELEMENT IsLiteral (#PCDATA)>
			m_writer.WriteStartElement("IsLiteral");
			m_writer.WriteString(f.IsLiteral.ToString());
			m_writer.WriteEndElement();

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
			m_writer.WriteStartElement("IsPrivate");
			m_writer.WriteString(f.IsPrivate.ToString());
			m_writer.WriteEndElement();

			//<!ELEMENT IsPublic (#PCDATA)>
			m_writer.WriteStartElement("IsPublic");
			m_writer.WriteString(f.IsPublic.ToString());
			m_writer.WriteEndElement();

			//<!ELEMENT IsStatic (#PCDATA)>
			m_writer.WriteStartElement("IsStatic");
			m_writer.WriteString(f.IsStatic.ToString());
			m_writer.WriteEndElement();

			//<!ELEMENT IsDeclaredHere (#PCDATA)>
			m_writer.WriteStartElement("IsDeclaredHere");
			m_writer.WriteString(declaredHere.ToString());
			m_writer.WriteEndElement();

			//<!ELEMENT HashCode (#PCDATA)>
			m_writer.WriteStartElement("HashCode");
			m_writer.WriteString(f.GetHashCode().ToString());
			m_writer.WriteEndElement();

			// end fieldinfo
			m_writer.WriteEndElement();
		}
		else
		{
			// skip writing to xml but still look for fieldtypes
			if (f.FieldType != null) // may be null if Object
			{
				HarvesterType htype = new HarvesterType(fromDLL, f.FieldType);
				AddPendingType(htype);
			}
		}
	}

	private string GetFullName(Type t)
	{
		if (t.FullName == null)
		{
			if (m_genericParameters.Contains(t))
				return (string)m_genericParameters[t];
			else
			{
				string name = "GenericTypeParam" + (m_gpc++);
				m_genericParameters[t] = name;
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
			return 1;
		}
		else
		{
			for (int i = 0; i < args.Length; i++)
				Debug.Out(Debug.MODE_DEBUG, "arg[" + i + "]=" + args[i]);

			try
			{
				String outFolder = args[0];

				IList dlls = new ArrayList();
				for (int i = 1; i < args.Length; i++)
					dlls.Add(args[i]);

				TypeHarvester th = new TypeHarvester(outFolder);
				th.ProcessDlls(dlls);

				return 0;
			}
			catch (Exception e)
			{
				Debug.Out(Debug.MODE_ERROR, e.Message);
				return 2;
			}
		}
	}

	private static Assembly LoadAssembly(String dll)
	{
		try
		{
			return Assembly.LoadFrom(dll);
		}
		catch (FileNotFoundException)
		{
			Debug.Out(Debug.MODE_WARNING, "File not found " + dll + ". Skipping.");
			return null;
		}
		catch (ArgumentException)
		{
			Debug.Out(Debug.MODE_WARNING, "Cannot harvest type of " + dll + ". Skipping.");
			return null;
		}
	}

	private class HarvesterType
	{
		public string dll;
		public Type type;

		public HarvesterType(string dll, Type type)
		{
			this.dll = dll;
			this.type = type;
		}
	}

	private class Debug
	{
		public readonly static string MODE_ERROR	= "error";
		public readonly static string MODE_CRUCIAL	= "crucial";
		public readonly static string MODE_WARNING	= "warning";
		public readonly static string MODE_INFO		= "information";
		public readonly static string MODE_DEBUG	= "debug";

		private Debug() { }

		public static void Out(string mode, string msg)
		{
			string module = "TYM_TH";
			string filename = "";
			int line = 0;

			Console.WriteLine(module + '~' + mode + '~' + filename + '~' + line + '~' + msg);
		}
	}
}
