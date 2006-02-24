/*
 * Created on 21-jul-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package Composestar.Core.TYM;

import Composestar.Utils.Debug;
import Composestar.Utils.StringConverter;
import Composestar.Core.RepositoryImplementation.DataStore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.HashSet;
import java.util.HashMap;

/**
 * @author Staijen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TypeLocations {
	
	private static TypeLocations instance;

	public static TypeLocations instance()
	{
		if( TypeLocations.instance == null )
			TypeLocations.instance = new TypeLocations(); 	
		return TypeLocations.instance;
	}

	private HashMap types = new HashMap();
	private HashMap sources = new HashMap();
	private HashMap sourceByType = new HashMap();
	private HashSet assemblies = new HashSet();
	
	private TypeLocations()
	{
		DataStore ds = DataStore.instance();
		Properties p = (Properties) ds.getObjectByID("config");
		String typeSourcesString = p.getProperty("TypeSources");
		
		Iterator typeSources = StringConverter.stringToStringList(typeSourcesString, ",");
		while( typeSources.hasNext())
		{
			String typeSource = (String) typeSources.next();
			String[] parts = typeSource.split("@");
			String type = parts[0];
			String source = parts[1];
			setTypeSource(type, source);
		}
	}
	
	public void setTypeSource(String type, String source)
	{
		Type t = getType(type);
		source = (new java.io.File(source)).getAbsolutePath();
		
		Source s = getSource(source);
		t.setSource(s);
		this.sourceByType.put(type,source);
		
		Debug.out(Debug.MODE_DEBUG, "TYM_LOCATION", "Type " + type + " is in " + source);
	}
	
	public void setSourceAssembly(String source, String assembly)
	{
		source = (new java.io.File(source)).getAbsolutePath();
		Source s = getSource(source);
		
		if(assembly == null) 
		{ 
			assembly = "";
		}

		int index = assembly.lastIndexOf(".");
		if(index < 0)
		{
			assembly = "";
		}
		else
		{
			assembly = assembly.substring(0, index);
		}
		Assembly a = new Assembly(assembly);
		s.setAssembly(a);
		this.assemblies.add(a.getName());
		Debug.out(Debug.MODE_DEBUG, "TYM_LOCATION", "Source " + source + " is compiled to  " + assembly);
	}
	
	public Type getType(String name)
	{
		Type t = (Type) this.types.get(name);
		if( t == null )
		{
			t = new Type();
			this.types.put(name, t);
		}
		return t;
	}
	
	public String getSourceByType(String type){
		if(this.sourceByType.containsKey(type))
			return (String)this.sourceByType.get(type);
		else
			return null;
	}
	
	/**
	 * Returns all types declared in a source
	 * @param source Absolute path of a source
	 * @return ArrayList 
	 */
	public ArrayList getTypesBySource(String source){
		ArrayList types = new ArrayList();
		Iterator keys = this.sourceByType.keySet().iterator();
		while(keys.hasNext()){
			String key = (String)keys.next();
			String src = (String)this.sourceByType.get(key);
			if(src.equals(source)){
				types.add(key);
			}
		}
		return types;
	}
	
	private Source getSource(String name)
	{
		Source s = (Source) this.sources.get(name);
		if( s == null )
		{
			s = new Source();
			this.sources.put(name, s);
		}
		return s;
	}
	
	public String getAssemblyByType(String type)
	{
		Type theType = this.getType(type);
		String result = theType.assemblyName();
		Debug.out(Debug.MODE_DEBUG, "TYM_LOCATION", "Type " + type + " is in assembly " + result);
		return result;
	}
	
	public String[] types()
	{
		return (String[]) this.types.keySet().toArray(new String[types.size()]);
	}
	
	public HashSet assemblies()
	{
		return this.assemblies;
	}
	
	private class Type
	{
        private Source source;
		
		public String assemblyName()
		{
			return this.source == null ? "": this.source.assemblyName();
		}
		
		public void setSource(Source s)
		{
			this.source = s;
		}
		
		public Source getSource()
		{
			return this.source;
		}
	}
	
	private class Source
	{
		private Assembly assembly;
	
		public void setAssembly(Assembly a)
		{
			this.assembly = a;
		}
		public Assembly getAssembly()
		{
			return this.assembly;
		}

		public String assemblyName()
		{
			return this.assembly == null ? "": this.assembly.getName();
		}
	}
	
	private class Assembly
	{
		private String name;
		
		public Assembly(String name)
		{
			this.name = name;
		}
		
		public String getName()
		{
			return this.name == null ? "": this.name;
		}
	}
}
