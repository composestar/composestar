/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: DotNETRepositorySerializer.java,v 1.4 2006/03/14 10:08:35 pascal_durr Exp $
 */

package Composestar.DotNET.CONE;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;

import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.RepositoryImplementation.RepositoryEntity;
import Composestar.Core.RepositoryImplementation.DataStore;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Stack;

import Composestar.Core.CONE.RepositorySerializer;
import Composestar.Core.CONE.CONE;
import Composestar.Core.CpsProgramRepository.PrimitiveConcern;
import Composestar.Core.Exception.ModuleException;

import Composestar.Core.RepositoryImplementation.SerializableRepositoryEntity;
import Composestar.Core.RepositoryImplementation.DataMap;
import Composestar.Utils.Debug;

/**
 * This class creates a dump of the entire datastore. The dump is deserialized in 
 * .net.
 * Serialization is based on the public fields of a class in the repository. 
 * Therefor, certain elements can be kept out of the dump by switching their 
 * accessiblility to private/protected.
 * @author Tom Staijen
 * @version 0.9.0
 */
public class DotNETRepositorySerializer extends CONE implements RepositorySerializer
{
	private Hashtable orderedFieldInfo;
	private PrintWriter out =null;
    
	/**
	 * @param destination
	 * @param ds
	 * @throws Composestar.core.Exception.ModuleException
	 * @roseuid 40EBC2C5001B
	 */
	public DotNETRepositorySerializer() 
	{
		
	}
	
	public void run(CommonResources resources) throws ModuleException
	{
		File destination = null;
		DataStore ds = DataStore.instance();
		
    	String repositoryFilename = Configuration.instance().getPathSettings().getPath("Base") + "repository.xml";
    	
    	destination = new File(repositoryFilename);
    	
		Debug.out(Debug.MODE_DEBUG, "CONE-XML", "v0.2+(optimized)");
		Debug.out(Debug.MODE_DEBUG, "CONE-XML", "Writing repository to file '" + destination.getName() + "'...");

		orderedFieldInfo = new Hashtable();
		try
		{
			out = new PrintWriter(new BufferedWriter(new FileWriter(destination)));
			write(ds);
		}
		catch(IOException e) 
		{
			throw new ModuleException("Unable to open output file: repository.xml","CONE-XML");
		}
	}
    
	/**
	 * @param obj
	 * @param name
	 * @roseuid 40EBC2C500F5
	 */
	private void startElement(String name) 
	{
		//out.println("<"+ name + ">");
		startElement(name, null );     
	}
    
	/**
	 * @param name
	 * @param attributes
	 * @roseuid 40EBC2C50115
	 */
	private void startElement(String name, String attributes) 
	{
		if( attributes != null )
			out.print("<"+ name + ' ' + attributes + '>');
		else
			out.print("<"+ name + '>');     
	}
    
	/**
	 * @param name
	 * @roseuid 40EBC2C50135
	 */
	private void endElement(String name) 
	{
		out.println("</"+ name + ">");     
	}
    
	private void fieldStartElement(Field field)
	{
		startElement(field.getName());
	}

	private void fieldEndElement(Field field)
	{
		endElement(field.getName());
	}

	private void handleStringField(Field field, Object obj) throws java.lang.IllegalAccessException
	{ 
		Object fieldValue = field.get(obj);
		if( fieldValue != null ) 
		{
			fieldStartElement(field);
			out.write(replaceSpecial((String)fieldValue));
			fieldEndElement(field);
		}
	}

	private void handleIntField(Field field, Object obj) throws java.lang.IllegalAccessException
	{ 
		fieldStartElement(field);
		out.write(""+ field.getInt(obj));
		fieldEndElement(field);
	}

	private void handleByteField(Field field, Object obj) throws java.lang.IllegalAccessException
	{
		fieldStartElement(field);
		out.write("" + field.getByte(obj));
		fieldEndElement(field);
	}

	private void handleBoolField(Field field, Object obj) throws java.lang.IllegalAccessException
	{
		fieldStartElement(field);
		out.write("" + field.getBoolean(obj));
		fieldEndElement(field);
	}

	private void handleLongField(Field field, Object obj) throws java.lang.IllegalAccessException
	{
		fieldStartElement(field);
		out.write("" + field.getLong(obj));
		fieldEndElement(field);
	}

	private void handleCharField(Field field, Object obj) throws java.lang.IllegalAccessException
	{
		fieldStartElement(field);
		out.write("" + field.getChar(obj));
		fieldEndElement(field);
	}

	private void handleVectorField(Field field, Object obj, Vector vector) throws java.lang.IllegalAccessException
	{
		if( vector.size() <= 0 ) 
		{
			return;
		}
		fieldStartElement(field);
		startElement("capacityIncrement");
		out.write("0");
		endElement("capacityIncrement");
		startElement("elementCount");
							
		out.write("" + vector.size());
		endElement("elementCount");
		// start dumping the elements of the vector
		startElement("elementData");
		Enumeration en = vector.elements();
		while(  en.hasMoreElements()) 
		{
			Object element = en.nextElement();
			// we only dump Strings and SerializableRepositoryEntity's 
			if( element instanceof String ) 
			{
				// strings are dumped as PCDATA and we need to specify the type as xsd:string
				startElement("anyType", "xsi:type=\"xsd:string\"");
				out.write(replaceSpecial((String)element));
				endElement("anyType");
			}
			else if( element instanceof RepositoryEntity )
			{
				// any other object requires just to have it's fields dumped. again the type is specified
				startElement("anyType", "xsi:type=\"" + getType(element) + "\"");
				writeFields(element);
				endElement("anyType");
			} 
			else 
			{
				startElement("anyType");
				endElement("anyType");
			}
		}
		endElement("elementData");
		fieldEndElement(field);
	}

	/**
	 * @param obj
	 * @roseuid 40EBC2C50163
	 */
	private void writeFields(Object obj) 
	{
		Class objClass = obj.getClass();    	
   	
		Vector fields = getOrderedOutputFields(objClass);
		int n = fields.size();
		if( n == 0 )
		{
			//System.out.println("Class with no fields: " + objClass);
			return;
		}
		Enumeration e = fields.elements();
		while(e.hasMoreElements()) 
		{
			Field field = (Field) e.nextElement();
			if( !Modifier.isStatic(field.getModifiers()) )
			{
				try
				{
					Class type = field.getType();
					if(int.class.equals(type))
					{
						handleIntField(field, obj);
					}
					else if(boolean.class.equals(type))
					{
						handleBoolField(field,obj);
					}
					else if(long.class.equals(type))
					{
						handleLongField(field, obj);
					}
					else if (byte.class.equals(type))
					{
						handleByteField(field, obj);
					}
					else if(char.class.equals(type))
					{
						handleCharField(field, obj);
					}
					else
					{
						handleObjectField(field, obj);
					}
				}
				catch (Exception ex)
				{
					System.err.println("CONE-XML Error: " + ex.getMessage() );
				}
			}     
		}
	}

	private void handleObjectField(Field field, Object obj) throws java.lang.IllegalAccessException
	{
		Object fieldValue = field.get(obj);
		if(fieldValue instanceof String)
		{
			handleStringField(field, obj);
		}
		if(fieldValue instanceof Vector)
		{
			handleVectorField(field,obj, (Vector) fieldValue);
		}
		else if( fieldValue instanceof RepositoryEntity || fieldValue instanceof DataMap || fieldValue instanceof SerializableRepositoryEntity)
		{
			// just a regular object, which requires it's fields dumped
			if( fieldValue != null )
			{
				startElement(field.getName(), "xsi:type=\"" + getType(fieldValue) + "\"");
				// dump the fields of the fieldValue object 
				writeFields(fieldValue);
				fieldEndElement(field);
			}
		}
		else if( fieldValue != null )
		{
			//System.out.println("[CONE/XML] Skipped " + fieldValue.getClass().getName() );
		}

	}
	/**
	 * @param c
	 * @return java.util.Vector
	 * @roseuid 40EBC2C50173
	 */
	private Vector getOrderedOutputFields(Class c) 
	{
		// fields have been ordered before for class c, then fetch the ordered list
		// from the storage map
		if( orderedFieldInfo.containsKey(c))
			return (Vector) orderedFieldInfo.get(c);

		Vector fields = new Vector();
		Stack stack = new Stack();
		Class myClass = c;
		while( !myClass.equals(Object.class) ) 
		{
			stack.push(myClass);
			myClass = myClass.getSuperclass();
		}
    	
    	
		while( !stack.empty() ) 
		{
			myClass = (Class) stack.pop();
			Field[] declaredFields = myClass.getDeclaredFields();
			for( int i = 0; i < declaredFields.length; i++ ) 
			{
				int modifier = declaredFields[i].getModifiers();

				if( Modifier.isPublic(modifier) ) 
				{
					fields.add(declaredFields[i]);
				}
			}
		}
		orderedFieldInfo.put(c, fields);
		return fields;     
	}
    
	/**
	 * @param orig
	 * @return java.lang.String
	 * @roseuid 40EBC2C501A1
	 */
	private String replaceSpecial(String orig) 
	{
		String result = orig.replaceAll( "&", "&amp;" );
		result = result.replaceAll( "<", "&lt;" );
		result = result.replaceAll( ">", "&gt;" );
		return result;
	}
    
	/**
	 * @param o
	 * @return java.lang.String
	 * @roseuid 40EBC2C501D0
	 */
	private String getType(Object o) 
	{
		if( o instanceof String ) 
		{
			return "xsd:string";
		} 
		else 
		{
			String type = o.getClass().getName();
			type = type.substring(type.lastIndexOf(".") + 1);
			return replaceSpecial(type);
		}     
	}
    
	/**
	 * @param obj
	 * @throws Composestar.core.Exception.ModuleException
	 * @roseuid 40EBC2C501FF
	 */
	private void write(DataStore obj) throws ModuleException
	{
			// removing useless crap
			obj.map.excludeUnreferenced(PrimitiveConcern.class);
			
			// little verification:
			// check if all RepositoryEntities have a repositoryKey
			Enumeration keys = obj.map.keys.elements();
			while (keys.hasMoreElements()) 
			{
				String key = (String) keys.nextElement();
				Object value = obj.getObjectByID(key);
	
				if (!(value instanceof RepositoryEntity)) 
				{
					//not that bad, will be skipped in during serialization
					//System.err.println("??? found a " + value.getClass().getName() );
				} 
				else if (key.compareTo(((RepositoryEntity) value).repositoryKey) != 0) 
				{
					// this is bad, the entity has another key then the datastore uses
					// to reference the object
					System.err.println("FATAL ERROR?!!!");
					System.err.println("Key: " + key + " points to " + ((RepositoryEntity) value).repositoryKey);
				}
			}
		
			out.println("<?xml version=\"1.0\"?>");
			startElement("DataStore", "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
			writeFields(obj);
			endElement("DataStore");
			out.flush();
			out.close(); 
	}
}
