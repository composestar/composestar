package Composestar.DotNET.TYPEX;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.MethodWrapper;
import Composestar.Core.CpsProgramRepository.PlatformRepresentation;
import Composestar.Core.CpsProgramRepository.Signature;
import Composestar.DotNET.LAMA.DotNETMethodInfo;
import Composestar.DotNET.LAMA.DotNETParameterInfo;
import Composestar.DotNET.LAMA.DotNETType;

import composestar.dotNET.tym.entities.ArrayOfParameterElement;
import composestar.dotNET.tym.entities.ExpandedAssembly;
import composestar.dotNET.tym.entities.ExpandedType;
import composestar.dotNET.tym.entities.MethodElement;
import composestar.dotNET.tym.entities.ParameterElement;

class ExpandedTypeCollector
{
	private Map<String,ExpandedType> types;
	private Map<String,ExpandedAssembly> assemblies;
	private Map<String,ExpandedSource> sources;
	
	public ExpandedTypeCollector()
	{
		types = new HashMap<String,ExpandedType>();
		assemblies = new HashMap<String,ExpandedAssembly>();
		sources = new HashMap<String,ExpandedSource>();
	}

	public void process(List<Concern> concerns)
	{
		for (Concern concern : concerns)
		{
			PlatformRepresentation pr = concern.getPlatformRepresentation();

			if (pr == null || !(pr instanceof DotNETType))
				continue;

			DotNETType dnt = (DotNETType) pr;
			Signature sig = concern.getSignature();
			
			List<DotNETMethodInfo> methods 
				= sig.getMethods(MethodWrapper.ADDED);
			
			for (DotNETMethodInfo mi : methods)
			{
				addExtraMethod(dnt, mi);
			}
		}
	}

	public Collection<ExpandedAssembly> getExpandedAssemblies()
	{
		return assemblies.values();
	}
	
	public Collection<ExpandedSource> getExpandedSources()
	{
		return sources.values();
	}

	private void addExtraMethod(DotNETType dnt, DotNETMethodInfo mi)
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
	
	private ExpandedAssembly getExpandedAssembly(String assemblyName)
	{
		ExpandedAssembly ea = assemblies.get(assemblyName);
		if (ea == null)
		{
			assemblies.put(assemblyName, ea = ExpandedAssembly.Factory.newInstance());
			ea.setName(assemblyName);
			ea.addNewTypes();
		}
		
		return ea;
	}

	private ExpandedSource getExpandedSource(String sourceFilename)
	{
		ExpandedSource es = sources.get(sourceFilename);
		if (es == null)
		{
			sources.put(sourceFilename, es = new ExpandedSource(sourceFilename));
		}

		return es;
	}
}