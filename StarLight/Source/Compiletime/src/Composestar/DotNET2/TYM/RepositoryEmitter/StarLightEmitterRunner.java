/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2006-2008 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * $Id$
 */
package Composestar.DotNET2.TYM.RepositoryEmitter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import Composestar.Core.Annotations.ResourceManager;
import Composestar.Core.CpsRepository2.Concern;
import Composestar.Core.CpsRepository2.PropertyNames;
import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.CpsRepository2.FilterModules.Condition;
import Composestar.Core.CpsRepository2.FilterModules.External;
import Composestar.Core.CpsRepository2.FilterModules.FilterModule;
import Composestar.Core.CpsRepository2.FilterModules.FilterModuleVariable;
import Composestar.Core.CpsRepository2.FilterModules.Internal;
import Composestar.Core.CpsRepository2.References.InstanceMethodReference;
import Composestar.Core.CpsRepository2.References.MethodReference;
import Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule;
import Composestar.Core.CpsRepository2.TypeSystem.CpsObject;
import Composestar.Core.CpsRepository2Impl.References.InnerTypeReference;
import Composestar.Core.CpsRepository2Impl.TypeSystem.CpsSelectorMethodInfo;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INLINE.lowlevel.InlinerResources;
import Composestar.Core.INLINE.model.FilterCode;
import Composestar.Core.LAMA.CallToOtherMethod;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.Signatures.MethodRelation;
import Composestar.Core.LAMA.Signatures.Signature;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.SECRET3.SECRETResources;
import Composestar.Core.SECRET3.Model.ConflictRule;
import Composestar.Core.SECRET3.Model.RuleType;
import Composestar.DotNET2.LAMA.DotNETMethodInfo;
import Composestar.DotNET2.LAMA.DotNETType;
import Composestar.DotNET2.MASTER.StarLightMaster;
import Composestar.Utils.FileUtils;
import Composestar.Utils.Logging.CPSLogger;

import composestar.dotNET2.tym.entities.ArrayOfAssemblyConfig;
import composestar.dotNET2.tym.entities.AssemblyConfig;
import composestar.dotNET2.tym.entities.ConfigurationContainer;
import composestar.dotNET2.tym.entities.Reference;
import composestar.dotNET2.tym.entities.WeaveCall;
import composestar.dotNET2.tym.entities.WeaveMethod;
import composestar.dotNET2.tym.entities.WeaveSpecification;
import composestar.dotNET2.tym.entities.WeaveSpecificationDocument;
import composestar.dotNET2.tym.entities.WeaveType;

//@ComposestarModule(ID = ModuleNames.WESPEM, dependsOn = { ComposestarModule.DEPEND_ALL, ModuleNames.SIGN })
public class StarLightEmitterRunner implements CTCommonModule
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.WESPEM);

	private Repository repository;

	private Map<String, WeaveSpecification> weaveSpecs;

	private Map<WeaveSpecification, FilterCodeCompressor> compressors;

	private InstructionTranslator instructionTranslater;

	private FilterCodeCompressor currentCompressor;

	private CommonResources resources;

	private boolean includeConflictRules;

	@ResourceManager
	private InlinerResources inlinerRes;

	public StarLightEmitterRunner()
	{
		weaveSpecs = new HashMap<String, WeaveSpecification>();
		compressors = new HashMap<WeaveSpecification, FilterCodeCompressor>();
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getModuleName()
	 */
	public String getModuleName()
	{
		return ModuleNames.WESPEM;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getDependencies()
	 */
	public String[] getDependencies()
	{
		return new String[] { DEPEND_ALL, ModuleNames.SIGN };
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getImportance()
	 */
	public ModuleImportance getImportance()
	{
		return ModuleImportance.REQUIRED;
	}

	public ModuleReturnValue run(CommonResources resc) throws ModuleException
	{
		resources = resc;
		repository = resc.repository();
		instructionTranslater = new InstructionTranslator(repository);
		// Emit all types to persistent repository
		try
		{
			processConcerns();
			writeWeaveSpecs();
		}
		catch (NullPointerException exc)
		{
			exc.printStackTrace();
			throw new ModuleException("NullPointerException in emitter", ModuleNames.WESPEM);
		}

		return ModuleReturnValue.OK;
	}

	/**
	 * Gets the weavespecification corresponding with a given assembly.
	 * 
	 * @param assemblyName The name of the assembly for which the weavespec
	 *            needs to be returned.
	 * @return The weavespec for the given dll. If it does not exist, a new one
	 *         is created.
	 */
	private WeaveSpecification getWeaveSpec(String assemblyName)
	{
		if (weaveSpecs.containsKey(assemblyName))
		{
			WeaveSpecification weaveSpec = weaveSpecs.get(assemblyName);
			currentCompressor = compressors.get(weaveSpec);

			return weaveSpec;
		}
		else
		{
			WeaveSpecification weaveSpec = WeaveSpecification.Factory.newInstance();
			weaveSpec.addNewWeaveTypes();
			weaveSpec.setAssemblyName(assemblyName);
			weaveSpecs.put(assemblyName, weaveSpec);

			currentCompressor = new FilterCodeCompressor();
			compressors.put(weaveSpec, currentCompressor);

			return weaveSpec;
		}
	}

	private void writeWeaveSpecs() throws ModuleException
	{
		ConfigurationContainer configContainer =
				(ConfigurationContainer) resources.get(StarLightMaster.RESOURCE_CONFIGCONTAINER);
		ArrayOfAssemblyConfig assemblies = configContainer.getAssemblies();
		for (int i = 0; i < assemblies.sizeOfAssemblyConfigArray(); i++)
		{
			AssemblyConfig ac = assemblies.getAssemblyConfigArray(i);
			if (weaveSpecs.containsKey(ac.getName()))
			{
				WeaveSpecification weaveSpec = weaveSpecs.get(ac.getName());
				includeConflictRules = false;
				addGeneralizedFilterCodes(weaveSpec);
				if (includeConflictRules)
				{
					addConflictRules(weaveSpec);
				}
				WeaveSpecificationDocument doc = WeaveSpecificationDocument.Factory.newInstance();
				doc.setWeaveSpecification(weaveSpec);

				File baseDir = new File(resources.configuration().getProject().getIntermediate(), "Starlight");
				File file = new File(baseDir, ac.getId() + "_weavespec.xml.gz");

				logger.debug("Writing '" + file + "'...");

				OutputStream outputStream = null;
				try
				{
					outputStream = new GZIPOutputStream(new FileOutputStream(file));
					doc.save(outputStream);
				}
				catch (IOException e)
				{
					throw new ModuleException("IOException while writing weave spec '" + file + "'", ModuleNames.WESPEM);
				}
				finally
				{
					FileUtils.close(outputStream);
				}

				ac.setWeaveSpecificationFile(file.getAbsolutePath());
			}
		}
	}

	/**
	 * Adds the generalized filtercodes from the compressor to the weavespec.
	 * 
	 * @param weaveSpec
	 */
	private void addGeneralizedFilterCodes(WeaveSpecification weaveSpec)
	{
		FilterCodeCompressor compressor = compressors.get(weaveSpec);

		FilterCode[] filterCodes = compressor.getGeneralizedFilterCodes();

		composestar.dotNET2.tym.entities.FilterCode[] translatedFilterCodes =
				new composestar.dotNET2.tym.entities.FilterCode[filterCodes.length];

		for (int i = 0; i < filterCodes.length; i++)
		{
			translatedFilterCodes[i] = translateFilterCode(filterCodes[i]);
			if (translatedFilterCodes[i].getBookKeeping())
			{
				includeConflictRules = true;
			}
		}

		weaveSpec.addNewGeneralizedFilterCodes();
		weaveSpec.getGeneralizedFilterCodes().setGeneralizedFilterCodeArray(translatedFilterCodes);
	}

	private void addConflictRules(WeaveSpecification weaveSpec) throws ModuleException
	{
		SECRETResources sresc = resources.getResourceManager(SECRETResources.class);
		if (sresc == null)
		{
			return;
		}
		if (weaveSpec.getConflictRules() == null)
		{
			weaveSpec.addNewConflictRules();
		}
		for (ConflictRule cr : sresc.getRules())
		{
			composestar.dotNET2.tym.entities.ConflictRuleElement rule =
					weaveSpec.getConflictRules().addNewConflictRule();
			rule.setPattern(cr.getPattern().toString());
			rule.setResource(cr.getResource().getName());
			rule.setConstraint(cr.getType() == RuleType.Constraint);
			rule.setMessage(cr.getMessage());
		}
	}

	private void processConcerns() throws ModuleException
	{
		for (Concern concern : repository.getAll(Concern.class))
		{
			processConcern(concern);
		}
	}

	public static String getUniqueName(FilterModuleVariable fmvar)
	{
		StringBuffer sb = new StringBuffer(fmvar.getName());
		sb.append('_');
		sb.append(System.identityHashCode(fmvar));
		return sb.toString();
	}

	private void processConcern(Concern concern) throws ModuleException
	{
		if (concern.getTypeReference() == null)
		{
			return;
		}
		DotNETType type = (DotNETType) concern.getTypeReference().getReference();
		if (type == null)
		{
			return;
		}

		if (concern.getSuperimposed() != null)
		{
			// HashSet to prevent the same condition from being stored twice.
			Set<MethodReference> storedConditions = new HashSet<MethodReference>();

			// get weavespec:
			WeaveSpecification weaveSpec = getWeaveSpec(type.assemblyName());

			// get filtermodules:
			List<ImposedFilterModule> order = concern.getSuperimposed().getFilterModuleOrder();

			WeaveType weaveType = weaveSpec.getWeaveTypes().addNewWeaveType();
			weaveType.addNewConditions();
			weaveType.addNewExternals();
			weaveType.addNewInternals();
			weaveType.addNewMethods();
			weaveType.setName(type.getFullName());

			for (ImposedFilterModule ifm : order)
			{
				FilterModule filterModule = ifm.getFilterModule();

				for (FilterModuleVariable fmvar : filterModule.getVariables())
				{
					if (fmvar instanceof Internal)
					{
						composestar.dotNET2.tym.entities.Internal storedInternal =
								weaveType.getInternals().addNewInternal();
						storedInternal.setName(getUniqueName(fmvar));

						Internal intern = (Internal) fmvar;
						DotNETType itype = (DotNETType) intern.getTypeReference().getReference();

						storedInternal.setType(itype.getFullName());
						storedInternal.setAssembly(itype.assemblyName());
					}
					else if (fmvar instanceof External)
					{
						composestar.dotNET2.tym.entities.External storedExternal =
								weaveType.getExternals().addNewExternal();
						storedExternal.setName(getUniqueName(fmvar));

						External extern = (External) fmvar;
						DotNETType itype = (DotNETType) extern.getTypeReference().getReference();

						storedExternal.setType(itype.getFullName());
						storedExternal.setAssembly(itype.assemblyName());

						storedExternal.setReference(createReference(extern.getMethodReference(), type));
					}
					else if (fmvar instanceof Condition)
					{
						composestar.dotNET2.tym.entities.Condition storedCondition =
								weaveType.getConditions().addNewCondition();
						storedCondition.setName(getUniqueName(fmvar));

						storedCondition.setReference(createReference(((Condition) fmvar).getMethodReference(), type));
					}
				}

				if (ifm.getCondition() != null && !storedConditions.contains(ifm.getCondition()))
				{
					composestar.dotNET2.tym.entities.Condition storedCondition =
							weaveType.getConditions().addNewCondition();
					storedCondition.setName(String.format("%s_%d", ifm.getCondition().getReferenceId(), System
							.identityHashCode(ifm.getCondition())));
					storedCondition.setReference(createReference(ifm.getCondition(), null));

					storedConditions.add(ifm.getCondition());
				}
			}

			// emit methods:
			processMethods(concern, weaveType);
		}
	}

	/**
	 * @param mref
	 * @param concernType
	 * @return
	 */
	private Reference createReference(MethodReference mref, Type concernType)
	{
		Reference storedRef = Reference.Factory.newInstance();
		storedRef.setInnerCallContext(-1);

		Type type = mref.getTypeReference().getReference();
		if (mref.getTypeReference() instanceof InnerTypeReference)
		{
			type = concernType;
		}

		MethodInfo mi = mref.getReference();
		if (mi == null)
		{
			// TODO doesn't take into account the possible JPC
			mi = type.getMethod(mref.getReferenceId(), new String[0]);
		}

		if (mref instanceof InstanceMethodReference)
		{
			InstanceMethodReference imref = (InstanceMethodReference) mref;
			CpsObject ctx = imref.getCpsObject();
			if (ctx instanceof FilterModuleVariable)
			{
				storedRef.setTarget(getUniqueName((FilterModuleVariable) ctx));
			}
			else if (ctx != null && ctx.isInnerObject())
			{
				storedRef.setTarget(PropertyNames.INNER);
				storedRef.setInnerCallContext(inlinerRes.getMethodId(mi));
			}
			else if (ctx != null && ctx.isSelfObject())
			{
				storedRef.setTarget(PropertyNames.SELF);
			}
		}
		storedRef.setNamespace(type.namespace());
		storedRef.setType(type.getName());
		storedRef.setAssembly(((DotNETType) type).assemblyName());
		storedRef.setSelector(mi.getName());
		return storedRef;
	}

	private void processMethods(Concern concern, WeaveType weaveType) throws ModuleException
	{
		Signature sig = concern.getTypeReference().getReference().getSignature();
		Collection<MethodInfo> methods = sig.getMethods(EnumSet.of(MethodRelation.NORMAL, MethodRelation.ADDED));

		boolean hasFilters;
		List<WeaveMethod> weaveMethods = new ArrayList<WeaveMethod>();

		logger.debug("Processing concern: " + concern);

		for (MethodInfo method : methods)
		{
			hasFilters = false;
			WeaveMethod weaveMethod = WeaveMethod.Factory.newInstance();
			weaveMethod.addNewWeaveCalls();
			weaveMethod.setSignature(((DotNETMethodInfo) method).getSignature());

			// get the block containing the filterinstructions:
			FilterCode filterCode = inlinerRes.getInputFilterCode(method);

			if (filterCode != null)
			{
				hasFilters = true;

				// add inputfilter code:
				int id = currentCompressor.addFilterCode(filterCode, new CpsSelectorMethodInfo(method));
				weaveMethod.setFilterCodeId(id);
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

		for (CallToOtherMethod call : method.getCallsToOtherMethods())
		{
			// add outputfilter code:
			FilterCode filterCode = inlinerRes.getOutputFilterCode(call);
			if (filterCode != null)
			{
				WeaveCall weaveCall = weaveMethod.getWeaveCalls().addNewWeaveCall();

				// set methodname:
				weaveCall.setMethodName(call.getMethodName());

				// set filtercode
				int id = currentCompressor.addFilterCode(filterCode, new CpsSelectorMethodInfo(call.getCalledMethod()));
				weaveCall.setFilterCodeId(id);

				logger.debug("Storing call" + weaveCall.toString());

				// set hasfilters to true to indicate that the method has
				// filters
				hasFilters = true;
			}
		}

		// return hasfilters
		return hasFilters;
	}

	private composestar.dotNET2.tym.entities.FilterCode translateFilterCode(FilterCode filterCode)
	{
		return (composestar.dotNET2.tym.entities.FilterCode) filterCode.accept(instructionTranslater);
	}
}
