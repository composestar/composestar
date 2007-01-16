package Composestar.DotNET.TYPEX;

import java.util.HashMap;
import java.util.Map;

import Composestar.Core.Exception.ModuleException;
import Composestar.DotNET.LAMA.DotNETMethodInfo;
import Composestar.DotNET.LAMA.DotNETType;

import composestar.dotNET.tym.entities.ExpandedAssembly;
import composestar.dotNET.tym.entities.ExpandedType;
import composestar.dotNET.tym.entities.MethodElement;

class TypeExpander
{
	private Map<String,ExpandedType> types;
	private Map<String,ExpandedAssembly> assemblies;
	private Map<String,ExpandedSource> sources;
	
	public TypeExpander()
	{
		types = new HashMap<String,ExpandedType>();
		assemblies = new HashMap<String,ExpandedAssembly>();
		sources = new HashMap<String,ExpandedSource>();
	}

	public void run() throws ModuleException
	{
		new SourceExpander().expand(sources.values());
		new AssemblyExpander().expand(assemblies);
	}

	public void addExtraMethod(DotNETType dnt, DotNETMethodInfo mi)
	{
		ExpandedType et = getExpandedType(dnt);
		MethodElement me = et.getExtraMethods().addNewMethod();
		me.setName(mi.getName());
		me.setReturnType(mi.returnTypeName());
	}
	
	private ExpandedType getExpandedType(DotNETType dnt)
	{
		String name = dnt.name();
		ExpandedType et = types.get(name);
		if (et == null)
		{
			types.put(name, et = createExpandedType(dnt));
			et.setName(dnt.fullName());
			et.addNewExtraMethods();
		}

		return et;
	}
	
	private ExpandedType createExpandedType(DotNETType dnt)
	{
		if (dnt.isFromSource())
		{
			String source = dnt.getFromSource();
			return getExpandedSource(source).addNewType();
		}
		else
		{
			String assemblyName = dnt.assemblyName();
			return getExpandedAssembly(assemblyName).getTypes().addNewType();
		}
	}
	
	private ExpandedAssembly getExpandedAssembly(String name)
	{
		ExpandedAssembly ea = assemblies.get(name);
		if (ea == null)
		{
			assemblies.put(name, ea = ExpandedAssembly.Factory.newInstance());
			ea.setName(name);
			ea.addNewTypes();
		}
		
		return ea;
	}

	private ExpandedSource getExpandedSource(String sourceFilename)
	{
		ExpandedSource es = sources.get(sourceFilename);
		if (es == null)
			sources.put(sourceFilename, es = new ExpandedSource(sourceFilename));

		return es;
	}
}