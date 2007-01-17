package Composestar.DotNET.TYPEX;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Composestar.Core.Exception.ModuleException;
import Composestar.DotNET.LAMA.DotNETMethodInfo;
import Composestar.DotNET.LAMA.DotNETParameterInfo;
import Composestar.DotNET.LAMA.DotNETType;

import composestar.dotNET.tym.entities.ArrayOfParameterElement;
import composestar.dotNET.tym.entities.ExpandedAssembly;
import composestar.dotNET.tym.entities.ExpandedType;
import composestar.dotNET.tym.entities.MethodElement;
import composestar.dotNET.tym.entities.ParameterElement;

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
		
		ArrayOfParameterElement pa = me.addNewParameters();
		List<DotNETParameterInfo> paramInfos = mi.getParameters();
		for (DotNETParameterInfo pi : paramInfos)
		{
			ParameterElement pe = pa.addNewParameter();
			pe.setName(pi.name());
			pe.setType(pi.getParameterTypeString());
		}
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
			ExpandedType et = getExpandedSource(source).addNewType();
			et.setEndPos(dnt.getEndPos());
			return et;
		}
		else
		{
			String assemblyName = dnt.assemblyName();
			ExpandedType et = getExpandedAssembly(assemblyName).getTypes().addNewType();
			return et;
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