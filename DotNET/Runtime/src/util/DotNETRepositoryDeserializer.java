// .NET specific
/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id$
 * 
 */
package Composestar.RuntimeDotNET.Utils;

import Composestar.Core.RepositoryImplementation.*;
import Composestar.RuntimeCore.Utils.*;

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
		try
		{
			Set typeSet = new HashSet();
			loadTypesFromAssembly("ComposestarCore.dll", typeSet);
			loadTypesFromAssembly("ComposestarCoreDotNET.dll", typeSet);

			Type[] typeArray = new Type[typeSet.size()];
			typeSet.toArray(typeArray);

			Debug.out(Debug.MODE_DEBUG,"FLIRT","Creating type deserializer... ");
			Type type = DataStore.instance().GetType();

			Debug.out(Debug.MODE_DEBUG, "FLIRT", "DataStore type=" + type);
			
			// Constructs an instance of the XmlSerializer with the type
			// of object that is being deserialized.
			XmlSerializer xs = new XmlSerializer(type, typeArray);
			XmlReader reader = new XmlTextReader(file);
			
			DataStore ds = (DataStore)xs.Deserialize(reader);
			
			DataStore.setInstance(ds);
			RepositoryFixer.fixRepository(ds);

			return ds;
		}
		catch (InvalidOperationException e)
		{
			Debug.out(Debug.MODE_ERROR,"FLIRT","Could not deserialize data structure.");
			Debug.out(Debug.MODE_ERROR,"FLIRT","\tMessage: "+ e.get_Message());
			String inner = "Inner";
			System.Exception ex = e.get_InnerException();
			while (ex != null)
			{
				Debug.out(Debug.MODE_ERROR,"FLIRT","\t" + inner+ "Message: "+ex.get_Message());
				ex = ex.get_InnerException();
				inner += "Inner";
			}
			Debug.out(Debug.MODE_ERROR,"FLIRT","Exiting...");
			return null;
		}
		catch (java.lang.Exception e)
		{
			Debug.out(Debug.MODE_ERROR,"FLIRT","Could not deserialize data structure.");
			Debug.out(Debug.MODE_ERROR,"FLIRT","\tMessage: "+e.getMessage());
			e.printStackTrace();
			Debug.out(Debug.MODE_ERROR,"FLIRT","Exiting...");
			return null;
		}
	}

	/**
	 * Loads the types from the assembly with the specified name,
	 * and adds them to the specified list.
	 */
	private void loadTypesFromAssembly(String assembly, Set target)
	{
		Debug.out(Debug.MODE_DEBUG, "FLIRT", "Loading assembly '" + assembly + "'...");
		Assembly asm = Assembly.LoadFrom(assembly);

		Debug.out(Debug.MODE_DEBUG, "FLIRT", "Extracting concrete types from assembly...");
		Type[] types = asm.GetTypes();

		for (int i = 0; i < types.length; i++)
		{
			Type t = types[i];
			if (! t.get_IsAbstract())
				target.add(t);
		}
	}

	/**
	 * For testing purposes.
	 */
	public static void main(String[] args)
	{
		if (args.length == 1)
		{
			Debug.setMode(Debug.MODE_DEBUG);
			String filename = args[0];

			RepositoryDeserializer rd = new DotNETRepositoryDeserializer();
			DataStore ds = rd.deserialize(filename);

			System.out.println("ds = " + ds);
		}
		else
		{
			Debug.out(Debug.MODE_ERROR, "FLIRT", "Usage: bla.exe <repository.xml>");
			System.exit(-1);
		}
	}
}
