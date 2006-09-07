// .NET specific
/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: DotNETRepositoryDeserializer.java,v 1.1 2006/02/16 16:24:28 composer Exp $
 * 
 */
package Composestar.RuntimeDotNET.Utils;

import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.RuntimeCore.Utils.RepositoryDeserializer;
import Composestar.RuntimeCore.Utils.Debug;

import System.*;
import System.IO.*;
import System.Reflection.*;
import System.Xml.*;
import System.Xml.Serialization.*;

import java.util.*;

public class DotNETRepositoryDeserializer extends RepositoryDeserializer
{
	public DotNETRepositoryDeserializer()
	{
	}

	public DataStore deserialize(String file)
	{
		DataStore ds = null;

		try
		{
			// Constructs an instance of the XmlSerializer with the type
			// of object that is being deserialized.
			ds = DataStore.instance();

			if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","Loading assembly 'ComposestarCore.dll'...");
			Assembly asm = null;
			try
			{
				asm = Assembly.LoadFrom( "ComposestarCore.dll" );
			}
			catch( System.IO.FileNotFoundException e) 
			{
				Debug.out(Debug.MODE_ERROR,"FLIRT","Assembly 'ComposestarCore.dll' not found.");
			}
			
			if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","Extracting types from assembly...");
			Type[] types = asm.GetTypes();
			
			ArrayList l = new ArrayList();
			for( int i = 0; i < types.length; i++ ) 
			{
				if( !types[i].get_IsAbstract() )
				{
					l.add(types[i]);
				}
			}

			if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","Loading assembly 'ComposestarCoreDotNET.dll'...");
			try
			{
				asm = Assembly.LoadFrom( "ComposestarCoreDotNET.dll" );
			}
			catch( System.IO.FileNotFoundException e) 
			{
				Debug.out(Debug.MODE_ERROR,"FLIRT","Assembly 'ComposeStarCoreDotNET.dll' not found.");
			}
			
			if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","Extracting types from assembly...");
			types = asm.GetTypes();
			
			for( int i = 0; i < types.length; i++ ) 
			{
				if( !types[i].get_IsAbstract() )
				{
					l.add(types[i]);
				}
			}
			
			//remove dubs QUICK and dirty
			for(int i =0; i < l.size();i++)
			{
				for(int j = i+1; j < l.size();j++)
				{
					if(l.get(i).equals(l.get(j)))
					{
						l.remove(j);
					}
				}
			}

			types = new Type[l.size()];
			for( int i = 0; i < l.size(); i++ )
			{
				types[i] = (Type) l.get(i);
				//if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_DEBUG,"FLIRT","Types["+i+"] = "+types[i]);
			}

			if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","Creating type deserializer... ");
			Type type = ds.GetType();
			XmlSerializer mySerializer = new XmlSerializer(type,types);
			XmlReader reader = new XmlTextReader(file);
			ds = (DataStore)mySerializer.Deserialize(reader);
			if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","Deserialized "+types.length+" type(s).");
			DataStore.setInstance(ds);
			RepositoryFixer.fixRepository(ds);
		}
		catch(InvalidOperationException e)
		{
			Debug.out(Debug.MODE_ERROR,"FLIRT","Could not deserialize data structure.");
			Debug.out(Debug.MODE_ERROR,"FLIRT","\tMessage: "+ e.get_Message());
			String inner = "Inner";
			System.Exception ex = e.get_InnerException();
			while(ex != null)
			{
				Debug.out(Debug.MODE_ERROR,"FLIRT","\t" + inner+ "Message: "+ex.get_Message());
				ex = ex.get_InnerException();
				inner += "Inner";
			}
			Debug.out(Debug.MODE_ERROR,"FLIRT","Exiting...");
			System.exit(-1);
		}
		catch(java.lang.Exception e)
		{
			Debug.out(Debug.MODE_ERROR,"FLIRT","Could not deserialize data structure.");
			Debug.out(Debug.MODE_ERROR,"FLIRT","\tMessage: "+e.getMessage());
			e.printStackTrace();
			Debug.out(Debug.MODE_ERROR,"FLIRT","Exiting...");
			System.exit(-1);
		}

		return ds;
	}
}
