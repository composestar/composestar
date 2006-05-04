/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: TypeHarvester.cs,v 1.1 2006/02/16 23:11:02 pascal_durr Exp $
 */

using System.Xml;
using System.Reflection;
using System.Collections;
using System;

public class TypeHarvester
{
	XmlTextWriter Writer = null;
	Hashtable PendingTypes;
	Hashtable ProcessedTypes;
	bool writerEnabled = true;
	
	public int count = 0;

    public TypeHarvester()
    {
		// both hashtables contain key = qualifiedname -> type pointer
		PendingTypes = new Hashtable(); // set initial size?
		ProcessedTypes = new Hashtable(); // set initial size?
	}

	//////////// Pending functionality ///////////////////
	private void addPendingType( HarvesterType htype )
	{
		Type t = htype.theType;

		// check if not done already
		if( ProcessedTypes.Contains( t.FullName ) ) return;
		// check if not in pending
		if( PendingTypes.Contains( t.FullName ) )	return;
		// add to pending
		PendingTypes.Add( t.FullName, htype );
		//PendingTypes.Add( t.FullName, t );
	}

	private void typeProcessed( Type t )
	{
		// check if in pending
		if( PendingTypes.Contains( t.FullName ) )
				PendingTypes.Remove( t.FullName );
		
		if( !ProcessedTypes.Contains( t.FullName) ) 
				ProcessedTypes.Add( t.FullName, "dummy" );
	}

	private void processPendingTypes()
	{
		// currently just displays the pending list..
		IDictionaryEnumerator typeEnumerator = PendingTypes.GetEnumerator();

//    Console.WriteLine( "\t-KEY-\t-VALUE-" );
        while ( typeEnumerator.MoveNext() )
        {
//      Console.WriteLine("\t{0}:\t{1}", typeEnumerator.Key, typeEnumerator.Value);
			//Type singleType = (Type)typeEnumerator.Value;
			HarvesterType singleType = (HarvesterType)typeEnumerator.Value;
			//this.count++;
			getTypeInfo( singleType );
            // Enumerator pukes on MoveNext if hashmap is changed
            typeEnumerator = PendingTypes.GetEnumerator();
		}
	}
	//////////// end Pending ////////////////////////////
	public void start(string outFolder)
    {
		if( Writer != null )
		{
			Console.WriteLine( "WARNING: start() called twice!\n" );
			return;
		}
		Writer = new XmlTextWriter(outFolder + "types.xml", null);
		Writer.WriteStartDocument();
		Writer.Formatting = Formatting.Indented;
		// open root element
		Writer.WriteStartElement( "Types" );
	}

	public void finish()
	{
		if( Writer == null )
		{
			Console.WriteLine( "WARNING: finish() called before start!\n" );
			return;
		}
			// end Types
		Writer.WriteEndElement();
		Writer.Close();
		Writer = null;
	}

  public void getParameterInfo( ParameterInfo p )
  {
	  if(writerEnabled)
	  {	
		  Writer.WriteStartElement( "ParameterInfo" );
		  Writer.WriteStartAttribute( "", "name" , "");
		  Writer.WriteString( p.Name );
		  Writer.WriteEndAttribute();

		  //<!ELEMENT IsIn (#PCDATA)>
		  Writer.WriteStartElement( "IsIn" );
		  Writer.WriteString( p.IsIn.ToString() );
		  Writer.WriteEndElement();
		  //<!ELEMENT IsLcid (#PCDATA)>
		  Writer.WriteStartElement( "IsLcid" );
		  Writer.WriteString( p.IsLcid.ToString() );
		  Writer.WriteEndElement();
		  //<!ELEMENT IsOptional (#PCDATA)>
		  Writer.WriteStartElement( "IsOptional" );
		  Writer.WriteString( p.IsOptional.ToString() );
		  Writer.WriteEndElement();
		  //<!ELEMENT IsOut (#PCDATA)>
		  Writer.WriteStartElement( "IsOut" );
		  Writer.WriteString( p.IsOut.ToString() );
		  Writer.WriteEndElement();
		  //<!ELEMENT IsRetval (#PCDATA)>
		  Writer.WriteStartElement( "IsRetval" );
		  Writer.WriteString( p.IsRetval.ToString() );
		  Writer.WriteEndElement();
		  //<!ELEMENT ParameterType (#PCDATA)>
		  Writer.WriteStartElement( "ParameterType" );
		  if( p.ParameterType != null ) // may be null if Object
			  Writer.WriteString( p.ParameterType.FullName );
		  Writer.WriteEndElement();
		  //<!ELEMENT Position (#PCDATA)>
		  Writer.WriteStartElement( "Position" );
		  Writer.WriteString( p.Position.ToString() );
		  Writer.WriteEndElement();
		  //<!ELEMENT HashCode (#PCDATA)>
		  Writer.WriteStartElement( "HashCode" );
		  Writer.WriteString( p.GetHashCode().ToString() );
		  Writer.WriteEndElement();

		  // end parameter
		  Writer.WriteEndElement();
	  }
  }

  public void getMethodInfo( MethodInfo m, bool declaredHere, string fromDLL )
	{
	  if(writerEnabled)
	  {		
		  Writer.WriteStartElement( "MethodInfo" );
		  Writer.WriteStartAttribute( "", "name" , "");
		  Writer.WriteString( m.Name ); // qualified name?
		  Writer.WriteEndAttribute();

		  //<!ELEMENT CallingConvention (#PCDATA)>
		  Writer.WriteStartElement( "CallingConvention" );
		  Writer.WriteString( ((int)m.CallingConvention).ToString() );
		  Writer.WriteEndElement();
		  //<!ELEMENT IsAbstract (#PCDATA)>
		  Writer.WriteStartElement( "IsAbstract" );
		  Writer.WriteString( m.IsAbstract.ToString() );
		  Writer.WriteEndElement();
		  //<!ELEMENT IsAssembly (#PCDATA)>
		  Writer.WriteStartElement( "IsAssembly" );
		  Writer.WriteString( m.IsAssembly.ToString() );
		  Writer.WriteEndElement();
		  //<!ELEMENT IsConstructor (#PCDATA)>
		  Writer.WriteStartElement( "IsConstructor" );
		  Writer.WriteString( m.IsConstructor.ToString() );
		  Writer.WriteEndElement();
		  //<!ELEMENT IsFamily (#PCDATA)>
		  Writer.WriteStartElement( "IsFamily" );
		  Writer.WriteString( m.IsFamily.ToString() );
		  Writer.WriteEndElement();
		  //<!ELEMENT IsFamilyAndAssembly (#PCDATA)> // inconsistent with type, .NET error not me
		  Writer.WriteStartElement( "IsFamilyAndAssembly" );
		  Writer.WriteString( m.IsFamilyAndAssembly.ToString() );
		  Writer.WriteEndElement();
		  //<!ELEMENT IsFamilyOrAssembly (#PCDATA)>
		  Writer.WriteStartElement( "IsFamilyOrAssembly" );
		  Writer.WriteString( m.IsFamilyOrAssembly.ToString() );
		  Writer.WriteEndElement();
		  //<!ELEMENT IsFinal (#PCDATA)>
		  Writer.WriteStartElement( "IsFinal" );
		  Writer.WriteString( m.IsFinal.ToString() );
		  Writer.WriteEndElement();
		  //<!ELEMENT IsHideBySig (#PCDATA)>
		  Writer.WriteStartElement( "IsHideBySig" );
		  Writer.WriteString( m.IsHideBySig.ToString() );
		  Writer.WriteEndElement();
		  //<!ELEMENT IsPrivate (#PCDATA)>
		  Writer.WriteStartElement( "IsPrivate" );
		  Writer.WriteString( m.IsPrivate.ToString() );
		  Writer.WriteEndElement();
		  //<!ELEMENT IsPublic (#PCDATA)>
		  Writer.WriteStartElement( "IsPublic" );
		  Writer.WriteString( m.IsPublic.ToString() );
		  Writer.WriteEndElement();
		  //<!ELEMENT IsStatic (#PCDATA)>
		  Writer.WriteStartElement( "IsStatic" );
		  Writer.WriteString( m.IsStatic.ToString() );
		  Writer.WriteEndElement();
		  //<!ELEMENT IsVirtual (#PCDATA)>
		  Writer.WriteStartElement( "IsVirtual" );
		  Writer.WriteString( m.IsVirtual.ToString() );
		  Writer.WriteEndElement();
		  //<!ELEMENT IsDeclaredHere (#PCDATA)>
		  Writer.WriteStartElement( "IsDeclaredHere" );
		  Writer.WriteString( declaredHere.ToString() );
		  Writer.WriteEndElement();
		  //<!ELEMENT HashCode (#PCDATA)>
		  Writer.WriteStartElement( "HashCode" );
		  Writer.WriteString( m.GetHashCode().ToString() );
		  Writer.WriteEndElement();
		  //<!ELEMENT ReturnType (#PCDATA)>  // TODO: Store for processing
		  Writer.WriteStartElement( "ReturnType" );
		  if( m.ReturnType != null ) // may be null if Object
		  {
			  Writer.WriteString( m.ReturnType.FullName );
			  HarvesterType htype = new HarvesterType(fromDLL,m.ReturnType);
			  addPendingType( htype );
			  //addPendingType( m.ReturnType );
		  }
		  Writer.WriteEndElement();

		  //process parameters
		  ParameterInfo[] parameters = m.GetParameters();
		  if(parameters.Length != 0)
		  {
			  foreach( ParameterInfo paramInfo in parameters )
			  {
				  getParameterInfo( paramInfo );	
			  }
		  }

		  // end methodinfo
		  Writer.WriteEndElement();
	  }
	  else 
	  {
			// skip writing to xml but still look for return type and parameters
		  if( m.ReturnType != null ) // may be null if Object
		  {
			  HarvesterType htype = new HarvesterType(fromDLL,m.ReturnType);
			  addPendingType( htype );
		  }
		  
		  //process parameters
		  ParameterInfo[] parameters = m.GetParameters();
		  if(parameters.Length != 0)
		  {
			  foreach( ParameterInfo paramInfo in parameters )
			  {
				  getParameterInfo( paramInfo );	
			  }
		  }
	   }
	}

  public void getFieldInfo( FieldInfo f, bool declaredHere, string fromDLL )
	{
	  if(writerEnabled)
	  {
		  Writer.WriteStartElement( "FieldInfo" );
		  Writer.WriteStartAttribute( "", "name" , "");
		  Writer.WriteString( f.Name ); // qualified name?
		  Writer.WriteEndAttribute();

		  //<!ELEMENT FieldType (#PCDATA)>
		  Writer.WriteStartElement( "FieldType" );
		  if( f.FieldType != null ) // may be null if Object
		  {
			  Writer.WriteString( f.FieldType.FullName );
			  HarvesterType htype = new HarvesterType(fromDLL,f.FieldType);
			  addPendingType( htype );
			  //addPendingType( f.FieldType );
		  }
		  Writer.WriteEndElement();
		
		  //<!ELEMENT IsAssembly (#PCDATA)>
		  Writer.WriteStartElement( "IsAssembly" );
		  Writer.WriteString( f.IsAssembly.ToString() );
		  Writer.WriteEndElement();
		
		  //<!ELEMENT IsFamily(#PCDATA)>
		  Writer.WriteStartElement( "IsFamily" );
		  Writer.WriteString( f.IsFamily.ToString() );
		  Writer.WriteEndElement();
		
		  //<!ELEMENT IsFamilyAndAssembly (#PCDATA)>
		  Writer.WriteStartElement( "IsFamilyAndAssembly" );
		  Writer.WriteString( f.IsFamilyAndAssembly.ToString() );
		  Writer.WriteEndElement();
		
		  //<!ELEMENT IsFamilyOrAssembly (#PCDATA)>
		  Writer.WriteStartElement( "IsFamilyOrAssembly" );
		  Writer.WriteString( f.IsFamilyOrAssembly.ToString() );
		  Writer.WriteEndElement();
		
		  //<!ELEMENT IsInitOnly (#PCDATA)>
		  Writer.WriteStartElement( "IsInitOnly" );
		  Writer.WriteString( f.IsInitOnly.ToString() );
		  Writer.WriteEndElement();
		
		  //<!ELEMENT IsLiteral (#PCDATA)>
		  Writer.WriteStartElement( "IsLiteral" );
		  Writer.WriteString( f.IsLiteral.ToString() );
		  Writer.WriteEndElement();
		
		  /*
		  Uncomment these if you think you need them for some reason
		  //<!ELEMENT IsNotSerialized (#PCDATA)>
		  Writer.WriteStartElement( "IsNotSerialized" );
		  Writer.WriteString( f.IsNotSerialized.ToString() );
		  Writer.WriteEndElement();
		
		  //<!ELEMENT IsPinvokeImpl (#PCDATA)>
		  Writer.WriteStartElement( "IsPinvokeImpl" );
		  Writer.WriteString( f.IsPinvokeImpl.ToString() );
		  Writer.WriteEndElement(); */
		
		  //<!ELEMENT IsPrivate (#PCDATA)>
		  Writer.WriteStartElement( "IsPrivate" );
		  Writer.WriteString( f.IsPrivate.ToString() );
		  Writer.WriteEndElement();

		  //<!ELEMENT IsPublic (#PCDATA)>
		  Writer.WriteStartElement( "IsPublic" );
		  Writer.WriteString( f.IsPublic.ToString() );
		  Writer.WriteEndElement();

		  //<!ELEMENT IsStatic (#PCDATA)>
		  Writer.WriteStartElement( "IsStatic" );
		  Writer.WriteString( f.IsStatic.ToString() );
		  Writer.WriteEndElement();

		  //<!ELEMENT IsDeclaredHere (#PCDATA)>
		  Writer.WriteStartElement( "IsDeclaredHere" );
		  Writer.WriteString( declaredHere.ToString() );
		  Writer.WriteEndElement();

		  //<!ELEMENT HashCode (#PCDATA)>
		  Writer.WriteStartElement( "HashCode" );
		  Writer.WriteString( f.GetHashCode().ToString() );
		  Writer.WriteEndElement();

		  // end fieldinfo
		  Writer.WriteEndElement();
	  }
	  else 
	  {	
		  // skip writing to xml but still look for fieldtypes
		  if( f.FieldType != null ) // may be null if Object
		  {
			  HarvesterType htype = new HarvesterType(fromDLL,f.FieldType);
			  addPendingType( htype );
		 }
	  }
	}

    public void getTypeInfo(HarvesterType mytype)
    {
		Type t = mytype.theType;

		/*if(t.FullName == "Composestar.Runtime.util.SyncBuffer")
		{
			Console.WriteLine("Type "+t.FullName+" in "+mytype.fromDLL);
			Console.WriteLine("\tprocessed: "+ProcessedTypes.Contains( t.FullName ));
			Console.WriteLine("\tpending: "+PendingTypes.Contains( t.FullName ));
		}*/
		
		if(ProcessedTypes.Contains( t.FullName )) return;
        // this type is processed
		typeProcessed( t );
		
		if(writerEnabled)
		{
			Writer.WriteStartElement( "Type" );
			Writer.WriteStartAttribute( "", "name" , "");
			Writer.WriteString( t.Name ); // qualified name?
			Writer.WriteEndAttribute();

			// start of elements of this type
			//<!ELEMENT AssemblyQualifiedName (#PCDATA)>
			Writer.WriteStartElement( "AssemblyQualifiedName" );
			Writer.WriteString( t.AssemblyQualifiedName );
			Writer.WriteEndElement();
			//<!ELEMENT BaseType (#PCDATA)> TODO: Store for processing..
			Writer.WriteStartElement( "BaseType" );
			if( t.BaseType != null ) // may be null if Object
			{
				Writer.WriteString( t.BaseType.FullName );
				HarvesterType htype = new HarvesterType(mytype.fromDLL,t.BaseType);
				addPendingType( htype );
				//addPendingType( t.BaseType );
			}
			Writer.WriteEndElement();
			//<!ELEMENT FullName (#PCDATA)> TODO: maybe use this for name (change)
			Writer.WriteStartElement( "FullName" );
			Writer.WriteString( t.FullName );
			Writer.WriteEndElement();
			//<!ELEMENT IsAbstract (#PCDATA)>
			Writer.WriteStartElement( "IsAbstract" );
			Writer.WriteString( t.IsAbstract.ToString() );
			Writer.WriteEndElement();
			//<!ELEMENT IsAnsiClass (#PCDATA)>
			Writer.WriteStartElement( "IsAnsiClass" );
			Writer.WriteString( t.IsAnsiClass.ToString() );
			Writer.WriteEndElement();
			//<!ELEMENT IsArray (#PCDATA)>
			Writer.WriteStartElement( "IsArray" );
			Writer.WriteString( t.IsArray.ToString() );
			Writer.WriteEndElement();
			//<!ELEMENT IsAutoClass (#PCDATA)>
			Writer.WriteStartElement( "IsAutoClass" );
			Writer.WriteString( t.IsAutoClass.ToString() );
			Writer.WriteEndElement();
			//<!ELEMENT IsAutoLayout (#PCDATA)>
			Writer.WriteStartElement( "IsAutoLayout" );
			Writer.WriteString( t.IsAutoLayout.ToString() );
			Writer.WriteEndElement();
			//<!ELEMENT IsByRef (#PCDATA)>
			Writer.WriteStartElement( "IsByRef" );
			Writer.WriteString( t.IsByRef.ToString() );
			Writer.WriteEndElement();
			//<!ELEMENT IsClass (#PCDATA)>
			Writer.WriteStartElement( "IsClass" );
			Writer.WriteString( t.IsClass.ToString() );
			Writer.WriteEndElement();
			//<!ELEMENT IsContextful (#PCDATA)>
			Writer.WriteStartElement( "IsContextful" );
			Writer.WriteString( t.IsContextful.ToString() );
			Writer.WriteEndElement();
			//<!ELEMENT IsEnum (#PCDATA)>
			Writer.WriteStartElement( "IsEnum" );
			Writer.WriteString( t.IsEnum.ToString() );
			Writer.WriteEndElement();
			//<!ELEMENT IsImport (#PCDATA)>
			Writer.WriteStartElement( "IsImport" );
			Writer.WriteString( t.IsImport.ToString() );
			Writer.WriteEndElement();
			//<!ELEMENT IsInterface (#PCDATA)>
			Writer.WriteStartElement( "IsInterface" );
			Writer.WriteString( t.IsInterface.ToString() );
			Writer.WriteEndElement();
			//<!ELEMENT IsMarshalByRef (#PCDATA)>
			Writer.WriteStartElement( "IsMarshalByRef" );
			Writer.WriteString( t.IsMarshalByRef.ToString() );
			Writer.WriteEndElement();
			//<!ELEMENT IsNestedFamANDAssem (#PCDATA)>
			Writer.WriteStartElement( "IsNestedFamANDAssem" );
			Writer.WriteString( t.IsNestedFamANDAssem.ToString() );
			Writer.WriteEndElement();
			//<!ELEMENT IsNestedAssembly (#PCDATA)>
			Writer.WriteStartElement( "IsNestedAssembly" );
			Writer.WriteString( t.IsNestedAssembly.ToString() );
			Writer.WriteEndElement();
			//<!ELEMENT IsNestedFamORAssem (#PCDATA)>
			Writer.WriteStartElement( "IsNestedFamORAssem" );
			Writer.WriteString( t.IsNestedFamORAssem.ToString() );
			Writer.WriteEndElement();
			//<!ELEMENT IsNestedPrivate (#PCDATA)>
			Writer.WriteStartElement( "IsNestedPrivate" );
			Writer.WriteString( t.IsNestedPrivate.ToString() );
			Writer.WriteEndElement();
			//<!ELEMENT IsNestedPublic (#PCDATA)>
			Writer.WriteStartElement( "IsNestedPublic" );
			Writer.WriteString( t.IsNestedPublic.ToString() );
			Writer.WriteEndElement();
			//<!ELEMENT IsNotPublic (#PCDATA)>
			Writer.WriteStartElement( "IsNotPublic" );
			Writer.WriteString( t.IsNotPublic.ToString() );
			Writer.WriteEndElement();
			//<!ELEMENT IsPointer (#PCDATA)>
			Writer.WriteStartElement( "IsPointer" );
			Writer.WriteString( t.IsPointer.ToString() );
			Writer.WriteEndElement();
			//<!ELEMENT IsPrimitive (#PCDATA)>
			Writer.WriteStartElement( "IsPrimitive" );
			Writer.WriteString( t.IsPrimitive.ToString() );
			Writer.WriteEndElement();
			//<!ELEMENT IsPublic (#PCDATA)>
			Writer.WriteStartElement( "IsPublic" );
			Writer.WriteString( t.IsPublic.ToString() );
			Writer.WriteEndElement();
			//<!ELEMENT IsSealed (#PCDATA)>
			Writer.WriteStartElement( "IsSealed" );
			Writer.WriteString( t.IsSealed.ToString() );
			Writer.WriteEndElement();
			//<!ELEMENT IsSerializable (#PCDATA)>
			Writer.WriteStartElement( "IsSerializable" );
			Writer.WriteString( t.IsSerializable.ToString() );
			Writer.WriteEndElement();
			//<!ELEMENT IsValueType (#PCDATA)>
			Writer.WriteStartElement( "IsValueType" );
			Writer.WriteString( t.IsValueType.ToString() );
			Writer.WriteEndElement();
			//<!ELEMENT Module (#PCDATA)>
			Writer.WriteStartElement( "Module" );
			Writer.WriteString( t.Module.FullyQualifiedName );
			Writer.WriteEndElement();
			//<!ELEMENT Namespace (#PCDATA)>
			Writer.WriteStartElement( "Namespace" );
			Writer.WriteString( t.Namespace );
			Writer.WriteEndElement();
			//<!ELEMENT UnderlyingSystemType (#PCDATA)>
			Writer.WriteStartElement( "UnderlyingSystemType" );
			if( t.UnderlyingSystemType != null )
			{
				Writer.WriteString( t.UnderlyingSystemType.FullName ); // TODO: handle type
				HarvesterType htype = new HarvesterType(mytype.fromDLL,t.UnderlyingSystemType);
				addPendingType( htype );
				//addPendingType( t.UnderlyingSystemType );
			}
			Writer.WriteEndElement();
			//<!ELEMENT HashCode (#PCDATA)>
			Writer.WriteStartElement( "HashCode" );
			Writer.WriteString( t.GetHashCode().ToString() );
			Writer.WriteEndElement();

			// process methods
			MethodInfo[] methods = t.GetMethods(BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic);
			MethodInfo[] methodsDeclaredHere = t.GetMethods(BindingFlags.DeclaredOnly | BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic);
			foreach (MethodInfo m in methods)
			{
				getMethodInfo( m, ((IList)methodsDeclaredHere).Contains(m), mytype.fromDLL );
			}
        
			// process fields (WH20041018)
			FieldInfo[] fields = t.GetFields(BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic);
			FieldInfo[] fieldsDeclaredHere = t.GetFields(BindingFlags.DeclaredOnly | BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic);
			foreach (FieldInfo f in fields)
			{
				getFieldInfo( f, ((IList)fieldsDeclaredHere).Contains(f) , mytype.fromDLL );
			}

			// Add implemented interfaces (WH20041129)
			Type[] interfaces = t.GetInterfaces();
			foreach (Type interf in interfaces)
			{
				//<!ELEMENT ImplementedInterface (#PCDATA)>
				Writer.WriteStartElement( "ImplementedInterface" );
				Writer.WriteString( interf.FullName );
				HarvesterType htype = new HarvesterType(mytype.fromDLL,interf);
				addPendingType( htype );
				//addPendingType( interf );
				Writer.WriteEndElement(); // End of implemented interfaces
			}
        
			// add fromDLL
			Writer.WriteStartElement( "FromDLL" );
			Writer.WriteString("\""+mytype.fromDLL+"\"");
			Writer.WriteEndElement();

			// end of Type
			Writer.WriteEndElement();
		}
		else 
		{
			// do not write the type to the xml file
			// but still check for underlying types, base types, interfaces, fields and methods
			if( t.BaseType != null ) // may be null if Object
			{
				HarvesterType htype = new HarvesterType(mytype.fromDLL,t.BaseType);
				addPendingType( htype );
			}
			if( t.UnderlyingSystemType != null )
			{
				HarvesterType htype = new HarvesterType(mytype.fromDLL,t.UnderlyingSystemType);
				addPendingType( htype );
			}
			// process methods
			MethodInfo[] methods = t.GetMethods(BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic);
			MethodInfo[] methodsDeclaredHere = t.GetMethods(BindingFlags.DeclaredOnly | BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic);
			foreach (MethodInfo m in methods)
			{
				getMethodInfo( m, ((IList)methodsDeclaredHere).Contains(m), mytype.fromDLL );
			}
        
			// process fields (WH20041018)
			FieldInfo[] fields = t.GetFields(BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic);
			FieldInfo[] fieldsDeclaredHere = t.GetFields(BindingFlags.DeclaredOnly | BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic);
			foreach (FieldInfo f in fields)
			{
				getFieldInfo( f, ((IList)fieldsDeclaredHere).Contains(f) , mytype.fromDLL );
			}

			// Add implemented interfaces (WH20041129)
			Type[] interfaces = t.GetInterfaces();
			foreach (Type interf in interfaces)
			{
				HarvesterType htype = new HarvesterType(mytype.fromDLL,interf);
				addPendingType( htype );
			}
		}
   }

   public static int Main(string[] args)
   {
       if (args.Length == 0 )
       {
           Console.WriteLine( "Usage: <outputfolder> [<dll names>]" );
       }
       else
       {
           TypeHarvester thing = new TypeHarvester();
           
           // forall dll's check out all types
           String outFolder = null;
		   foreach( string dllName in args )
           {
               if( outFolder == null )
			   {
				   outFolder = dllName;
				   thing.start(outFolder);
				   continue;
			   }

			   string dll = dllName;		
			   if(dll.IndexOf("!")==0)
			   {
				   dll = dll.Substring(1);
				   thing.writerEnabled = false;
			   }
			   else
				   thing.writerEnabled = true;

			   Assembly asm = null;
               try{
                   asm = Assembly.LoadFrom( dll );
               } catch( System.IO.FileNotFoundException ) {
                   Console.Error.WriteLine( "File not found " + dll + ". Skipping." );
                   continue;
               }
			   catch(System.ArgumentException)
			   {
				   Console.Error.WriteLine( "Cannot harvest type of " + dll + ". Skipping." );
				   continue;
			   }
			   Type[] types = null;
			   try
			   {
				   types = asm.GetTypes();
				   foreach( Type singleType in types )
				   {
					   //Console.Error.WriteLine("Type "+singleType.Name+" in "+dllName);
					   HarvesterType htype = new HarvesterType(dll,singleType);
					   //thing.getTypeInfo( singleType );
					   thing.getTypeInfo(htype);
					   thing.count++;

				   }
			   }
			   catch(Exception e)
			   {
				   string inner = "";
				   while(e != null)
				   {
						Console.Error.WriteLine(inner + "Exception:" + e.Message);
					   inner += "Inner";
					   e = e.InnerException;
				   }
				   return 1; // Error!
			   }

			   // take care of the pending types
			   thing.processPendingTypes();
           }
           
           thing.finish();
		   //Console.WriteLine(""+thing.count+ " types found!");
       }
	   return 0;
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

}//class
