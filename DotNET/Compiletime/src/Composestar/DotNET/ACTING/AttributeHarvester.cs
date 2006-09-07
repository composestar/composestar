/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: AttributeHarvester.cs,v 1.1 2006/02/13 11:54:42 pascal Exp $
 */

using System.Xml;
using System.Reflection;
using System.Collections;
using System;

public class AttributeHarvester
{
	XmlTextWriter	Writer = null;
	Hashtable		PendingTypes;
	Hashtable		ProcessedTypes;

    public AttributeHarvester()
    {
		// both hashtables contain key = qualifiedname -> type pointer
		PendingTypes = new Hashtable(); // set initial size?
		ProcessedTypes = new Hashtable(); // set initial size?
	}

	//////////// Pending functionality ///////////////////
	private void addPendingType( Type t )
	{
		// check if not done already
		if( ProcessedTypes.Contains( t.FullName ) ) return;
		// check if not in pending
		if( PendingTypes.Contains( t.FullName ) )	return;
		// add to pending
		PendingTypes.Add( t.FullName, t );
	}

	private void typeProcessed( Type t )
	{
		// check if in pending
		if( PendingTypes.Contains( t.FullName ) )
		{
				PendingTypes.Remove( t.FullName );
		}
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
            getTypeInfo( (Type)typeEnumerator.Value );
            // Enumerator pukes on MoveNext if hashmap is changed
            typeEnumerator = PendingTypes.GetEnumerator();
		}
	}
	//////////// end Pending ////////////////////////////
	public void start()
    {
		if( Writer != null )
		{
			Console.WriteLine( "WARNING: start() called twice!\n" );
			return;
		}
		Writer = new XmlTextWriter("attributes.xml", null);
		Writer.WriteStartDocument();
		Writer.Formatting = Formatting.Indented;
		// open root element
		Writer.WriteStartElement( "Attributes" );
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


  public void getMethodInfo( MethodInfo m , Type t)
	{
		Console.Write(t.Name + "::" + m.ToString() + "\n");
		// process attributes
	    Object[] attributes = m.GetCustomAttributes(true);
		foreach( Attribute attribute in attributes )
		{
			getAttributeInfo( attribute, t, m );
		}
		// end attributes	

		// end methodinfo
	}

	public void getAttributeInfo( Attribute a , Type type, MethodInfo method) 
	{
		// skip boring com.ms attributes
		if( a.ToString().StartsWith("com.ms.") ) return;

		Writer.WriteStartElement("Attribute");
		Writer.WriteStartAttribute("","TypeId","");
		Writer.WriteString(a.ToString());
		Writer.WriteEndAttribute();

		Writer.WriteStartAttribute("","Class","");
		Writer.WriteString(type.Name);
		Writer.WriteEndAttribute();

		if( method != null ) 
		{
			Writer.WriteStartAttribute("","Method","");
			Writer.WriteString(method.ToString());
			Writer.WriteEndAttribute();
		}

		Type t = a.GetType();
		FieldInfo[] fields = t.GetFields();
		foreach( FieldInfo field in fields ) 
		{
			Writer.WriteStartElement("Property");
			Writer.WriteStartAttribute("","Name","");
			Writer.WriteString(""  + field.Name);
			Writer.WriteEndAttribute();
			Writer.WriteStartAttribute("","Value","");
			Writer.WriteString(""  + field.GetValue(a));
			Writer.WriteEndAttribute();
			Writer.WriteEndElement();
		}
		PropertyInfo[] properties = t.GetProperties();
		foreach( PropertyInfo property in properties ) 
		{
			// skip TypeId, same as ToString()
			if( property.Name == "TypeId" ) continue;
			// else:
			Writer.WriteStartElement("Property");
			Writer.WriteStartAttribute("","name","");
			Writer.WriteString(""  + property.Name);
			Writer.WriteEndAttribute();
			Writer.WriteStartAttribute("","value","");
			Writer.WriteString(""  + property.GetValue(a, null));
			Writer.WriteEndAttribute();
			Writer.WriteEndElement();
		}

		Writer.WriteEndElement();

	}

    public void getTypeInfo(Type t)
    {
        Console.Write(t.Name + "\n");
		// this type is processed
        typeProcessed( t );
		
		// process attributes
		Object[] attributes = t.GetCustomAttributes(true);
		foreach( Attribute attribute in attributes )
		{
			getAttributeInfo( attribute , t, null);
		}
		// end attributes	

        // process methods
        MethodInfo[] methods = t.GetMethods();
        foreach (MethodInfo m in methods)
        {
            getMethodInfo( m , t);
        }
          // end of Type

    }//method

   public static void Main(string[] args)
   {
       if (args.Length == 0 )
       {
           Console.WriteLine( "Usage: dll names" );
       }
       else
       {
           AttributeHarvester ah = new AttributeHarvester();
           ah.start();
           // forall dll's check out all types
           foreach( string dllName in args )
           {
               Assembly asm = null;
               try{
                   asm = Assembly.LoadFrom( dllName );
               }catch( System.IO.FileNotFoundException ) {
                   Console.WriteLine( "File not found " + dllName + ". Skipping." );
                   continue;
               }
               Type[] types = asm.GetTypes();
               foreach( Type singleType in types )
                   ah.getTypeInfo( singleType );
           }
           // take care of the pending types
           ah.processPendingTypes();
           ah.finish();
       }
   }

}//class
