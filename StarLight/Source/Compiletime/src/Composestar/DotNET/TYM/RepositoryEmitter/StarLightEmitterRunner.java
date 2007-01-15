/*
 * Created on 21-sep-2006
 *
 */
package Composestar.DotNET.TYM.RepositoryEmitter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.zip.GZIPOutputStream;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.MethodWrapper;
import Composestar.Core.CpsProgramRepository.Signature;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.External;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.DeclaredObjectReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ExternalConcernReference;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.INLINE.lowlevel.ModelBuilder;
import Composestar.Core.INLINE.model.Block;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.PathSettings;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.DotNET.LAMA.DotNETCallToOtherMethod;
import Composestar.DotNET.LAMA.DotNETMethodInfo;
import Composestar.DotNET.LAMA.DotNETType;
import Composestar.DotNET.MASTER.StarLightMaster;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;
import Composestar.Utils.StringUtils;

import composestar.dotNET.tym.entities.ArrayOfAssemblyConfig;
import composestar.dotNET.tym.entities.AssemblyConfig;
import composestar.dotNET.tym.entities.InlineInstruction;
import composestar.dotNET.tym.entities.Reference;
import composestar.dotNET.tym.entities.WeaveCall;
import composestar.dotNET.tym.entities.WeaveMethod;
import composestar.dotNET.tym.entities.WeaveSpecification;
import composestar.dotNET.tym.entities.WeaveSpecificationDocument;
import composestar.dotNET.tym.entities.WeaveType;

public class StarLightEmitterRunner implements CTCommonModule
{
	public static final String MODULE_NAME = "EMITTER";
	
	private DataStore dataStore;
	private Map weaveSpecs;
	
	public StarLightEmitterRunner()
	{
		this.dataStore = DataStore.instance();
		this.weaveSpecs = new HashMap();
	}

	public void run(CommonResources resources) throws ModuleException
	{
		// Emit all types to persistent repository
		try
		{
			processConcerns();
			writeWeaveSpecs();
		}
		catch (NullPointerException exc)
		{
			exc.printStackTrace();
			throw new ModuleException("NullPointerException in emitter", MODULE_NAME);
		}
	}
	
	/**
	 * Gets the weavespecification corresponding with a given assembly.
	 * 
	 * @param assemblyName The name of the assembly for which the weavespec needs to be returned.
	 * @return The weavespec for the given dll. If it does not exist, a new one is created.
	 */
	private WeaveSpecification getWeaveSpec(String assemblyName)
	{
		if (weaveSpecs.containsKey(assemblyName))
		{
			return (WeaveSpecification) weaveSpecs.get(assemblyName);
		}
		else
		{
			WeaveSpecification weaveSpec = WeaveSpecification.Factory.newInstance();
			weaveSpec.addNewWeaveTypes();
			weaveSpec.setAssemblyName(assemblyName);
			weaveSpecs.put(assemblyName, weaveSpec);
			return weaveSpec;
		}
	}
	
	private void writeWeaveSpecs() throws ModuleException
	{
		Configuration config = Configuration.instance();
		PathSettings pathSettings = config.getPathSettings();
		
		ArrayOfAssemblyConfig assemblies = StarLightMaster.getConfigContainer().getAssemblies();
		for (int i = 0; i < assemblies.sizeOfAssemblyConfigArray(); i++)
		{
			AssemblyConfig ac = assemblies.getAssemblyConfigArray(i);
			if (weaveSpecs.containsKey(ac.getName()))
			{
				WeaveSpecification weaveSpec = (WeaveSpecification) weaveSpecs.get(ac.getName());
				WeaveSpecificationDocument doc = WeaveSpecificationDocument.Factory.newInstance();
				doc.setWeaveSpecification(weaveSpec);

				File baseDir = new File(pathSettings.getPath("Base"), "Starlight");
				File file = new File(baseDir, ac.getSerializedName() + "_weavespec.xml.gzip");
				
				OutputStream outputStream = null;
				try
				{
					outputStream = new GZIPOutputStream(new FileOutputStream(file));					
					doc.save(outputStream);
				}
				catch (IOException e)
				{
					throw new ModuleException("IOException while writing weavespecfile " + file, MODULE_NAME);
				}
				finally
				{
					FileUtils.close(outputStream);
				}
				
				ac.setWeaveSpecificationFile(file.getAbsolutePath());
			}
		}		
	}

	private void processConcerns() throws ModuleException
	{
		Iterator concernIterator = dataStore.getAllInstancesOf(Concern.class);
		while (concernIterator.hasNext())
		{
			Concern concern = (Concern) concernIterator.next();
			processConcern(concern);
		}
	}
	
	private void processConcern(Concern concern) throws ModuleException
	{
		DotNETType type = (DotNETType) concern.getPlatformRepresentation();
		if (type == null) return;

		if (concern.getDynObject("superImpInfo") != null)
		{
			// get weavespec:
			WeaveSpecification weaveSpec = getWeaveSpec(type.assemblyName());

			// get filtermodules:
			FilterModuleOrder order = (FilterModuleOrder) concern.getDynObject("SingleOrder");

			WeaveType weaveType = weaveSpec.getWeaveTypes().addNewWeaveType();
			weaveType.addNewConditions();
			weaveType.addNewExternals();
			weaveType.addNewInternals();
			weaveType.addNewMethods();
			weaveType.setName(type.fullName());

			Iterator filterModules = order.orderAsList().iterator();
			while (filterModules.hasNext())
			{
				String ref = (String) filterModules.next();
				FilterModule filterModule = (FilterModule) dataStore.getObjectByID(ref);

				// internals:
				Iterator internals = filterModule.getInternalIterator();
				while (internals.hasNext())
				{
					// store internal:
					Internal internal = (Internal) internals.next();
					composestar.dotNET.tym.entities.Internal storedInternal 
							= weaveType.getInternals().addNewInternal();

					// name:
					storedInternal.setName(internal.getName());

					// namespace:
					String namespace = StringUtils.join(internal.getType().getPackage(), ".");
					storedInternal.setNamespace(namespace);

					// typename:
					storedInternal.setType(internal.getType().getName());

					// assembly:
					DotNETType dnt = (DotNETType) internal.getType().getRef().getPlatformRepresentation();
					storedInternal.setAssembly(dnt.assemblyName());
				}

				// externals:
				Iterator externals = filterModule.getExternalIterator();
				while (externals.hasNext())
				{
					// store external
					External external = (External) externals.next();
					composestar.dotNET.tym.entities.External storedExternal 
							= weaveType.getExternals().addNewExternal();

					// name:
					storedExternal.setName(external.getName());

					// reference:
					ExternalConcernReference reference = external.getShortinit();
					DotNETType refType = (DotNETType) reference.getRef().getPlatformRepresentation();
					Reference storedReference = createReference(type, refType.assemblyName(), reference.getPackage(),
							reference.getName(), reference.getInitSelector());
					storedExternal.setReference(storedReference);

					// type:
					storedExternal.setType(external.getType().getQualifiedName());

					// assembly:
					DotNETType dnt = (DotNETType) external.getType().getRef().getPlatformRepresentation();
					storedExternal.setAssembly(dnt.assemblyName());
				}

				// conditions:
				Iterator conditions = filterModule.getConditionIterator();
				while (conditions.hasNext())
				{

					// store condition:
					Condition condition = (Condition) conditions.next();
					composestar.dotNET.tym.entities.Condition storedCondition 
							= weaveType.getConditions().addNewCondition();

					// name:
					storedCondition.setName(condition.getName());

					// reference:
					DotNETType refType;
					Composestar.Core.CpsProgramRepository.CpsConcern.References.Reference condRef = condition
							.getShortref();
					if (condRef instanceof DeclaredObjectReference)
					{
						DeclaredObjectReference dor = (DeclaredObjectReference) condRef;
						if (dor.getName().equals("inner") || dor.getName().equals("self"))
						{
							refType = type;
						}
						else
						{
							refType = (DotNETType) dor.getRef().getType().getRef().getPlatformRepresentation();
						}
					}
					else if (condRef instanceof ConcernReference)
					{
						ConcernReference cor = (ConcernReference) condRef;
						refType = (DotNETType) cor.getRef().getPlatformRepresentation();
					}
					else
					{
						throw new RuntimeException("Unknown reference type");
					}

					Reference reference = createReference(
							type, 
							refType.assemblyName(), 
							condition.getShortref().getPackage(), 
							condition.getShortref().getName(), 
							(String) condition.getDynObject("selector"));

					storedCondition.setReference(reference);
				}
			}

			// emit methods:
			processMethods(concern, weaveType);
		}
	}

	/**
	 * Creates the reference used by the external and condition to retrieve its
	 * instance/value
	 */
	private Reference createReference(Type type, String assembly, Vector pack, String target, String selector)
	{
		Reference storedRef = Reference.Factory.newInstance();

		// namespace:
		storedRef.setNamespace(StringUtils.join(pack, "."));

		// selector:
		storedRef.setSelector(selector);

		// target:
		storedRef.setTarget(target);

		// innercall context:
		if (target.equals("inner"))
		{
			MethodInfo methodInfo = type.getMethod(selector, new String[0]);
			if (methodInfo != null)
			{
				storedRef.setInnerCallContext(ModelBuilder.getMethodId(methodInfo));
			}
			else
			{
				storedRef.setInnerCallContext(-1);
			}
		}
		else
		{
			storedRef.setInnerCallContext(-1);
		}

		// assembly:
		storedRef.setAssembly(assembly);

		return storedRef;
	}

	private void processMethods(Concern concern, WeaveType weaveType) throws ModuleException
	{
		Signature sig = concern.getSignature();		
		List methods = sig.getMethods(MethodWrapper.NORMAL + MethodWrapper.ADDED);
		
		boolean hasFilters;
		List weaveMethods = new ArrayList();

		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Processing concern: " + concern);

		Iterator methodIter = methods.iterator();
		while (methodIter.hasNext())
		{
			hasFilters = false;
			DotNETMethodInfo method = (DotNETMethodInfo) methodIter.next();
			WeaveMethod weaveMethod = WeaveMethod.Factory.newInstance();
			weaveMethod.addNewWeaveCalls();
			weaveMethod.setSignature(method.getSignature());

			// get the block containing the filterinstructions:
			Block filterInstructions = ModelBuilder.getInputFilterCode(method);

			if (filterInstructions != null)
			{
				hasFilters = true;

				// add inputfilter code:
				weaveMethod.setInputFilter(translateInstruction(filterInstructions));
			}

			// emit calls:
			hasFilters = hasFilters || processCalls(method, weaveMethod);

			// add method if it has filters inlined:
			if (hasFilters)
			{
				weaveMethods.add(weaveMethod);
			}
		}

		// add inlined methods to type:
		WeaveMethod[] wma = new WeaveMethod[weaveMethods.size()];
		weaveMethods.toArray(wma);
		
		weaveType.getMethods().setWeaveMethodArray(wma);
	}

	private boolean processCalls(MethodInfo method, WeaveMethod weaveMethod)
	{
		boolean hasFilters = false;
		Iterator calls = method.getCallsToOtherMethods().iterator();

		while (calls.hasNext())
		{
			DotNETCallToOtherMethod call = (DotNETCallToOtherMethod) calls.next();

			// add outputfilter code:
			Block code = ModelBuilder.getOutputFilterCode(call);
			if (code != null)
			{
				WeaveCall weaveCall = weaveMethod.getWeaveCalls().addNewWeaveCall();

				// set methodname:
				weaveCall.setMethodName(call.getMethodName());

				// set filtercode
				weaveCall.setOutputFilter(translateInstruction(code));

				Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Storing call" + weaveCall.toString());

				// set hasfilters to true to indicate that the method has filters
				hasFilters = true;
			}
		}

		// return hasfilters
		return hasFilters;
	}

	private InlineInstruction translateInstruction(Block block)
	{
		return (InlineInstruction) block.accept(new InstructionTranslator());
	}
}
