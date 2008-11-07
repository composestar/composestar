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
package Composestar.DotNET2.TYM.RepositoryCollector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

import org.apache.xmlbeans.XmlException;

import Composestar.Core.Annotations.ComposestarModule;
import Composestar.Core.CpsRepository2.Concern;
import Composestar.Core.CpsRepository2.CpsConcern;
import Composestar.Core.CpsRepository2.JoinPointContextArgument;
import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.Filters.FilterAction;
import Composestar.Core.CpsRepository2.Filters.FilterAction.FlowBehavior;
import Composestar.Core.CpsRepository2.Meta.FileInformation;
import Composestar.Core.CpsRepository2.Meta.SourceInformation;
import Composestar.Core.CpsRepository2.References.ReferenceManager;
import Composestar.Core.CpsRepository2.References.TypeReference;
import Composestar.Core.CpsRepository2Impl.PrimitiveConcern;
import Composestar.Core.CpsRepository2Impl.Filters.FilterActionImpl;
import Composestar.Core.CpsRepository2Impl.Filters.PrimitiveFilterTypeImpl;
import Composestar.Core.CpsRepository2Impl.References.ReferenceManagerImpl;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.Annotation;
import Composestar.Core.LAMA.CallToOtherMethod;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.UnitRegister;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.DotNET2.LAMA.DotNETAttribute;
import Composestar.DotNET2.LAMA.DotNETCallToOtherMethod;
import Composestar.DotNET2.LAMA.DotNETFieldInfo;
import Composestar.DotNET2.LAMA.DotNETMethodInfo;
import Composestar.DotNET2.LAMA.DotNETParameterInfo;
import Composestar.DotNET2.LAMA.DotNETType;
import Composestar.DotNET2.MASTER.StarLightMaster;
import Composestar.Utils.FileUtils;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Perf.CPSTimer;

import composestar.dotNET2.tym.entities.ArrayOfAttributeElement;
import composestar.dotNET2.tym.entities.ArrayOfCallElement;
import composestar.dotNET2.tym.entities.ArrayOfParameterElement;
import composestar.dotNET2.tym.entities.AssemblyConfig;
import composestar.dotNET2.tym.entities.AssemblyDocument;
import composestar.dotNET2.tym.entities.AssemblyElement;
import composestar.dotNET2.tym.entities.AttributeElement;
import composestar.dotNET2.tym.entities.AttributeValueElement;
import composestar.dotNET2.tym.entities.CallElement;
import composestar.dotNET2.tym.entities.ConcernElement;
import composestar.dotNET2.tym.entities.ConfigurationContainer;
import composestar.dotNET2.tym.entities.FieldElement;
import composestar.dotNET2.tym.entities.FilterActionElement;
import composestar.dotNET2.tym.entities.FilterTypeElement;
import composestar.dotNET2.tym.entities.MethodBody;
import composestar.dotNET2.tym.entities.MethodElement;
import composestar.dotNET2.tym.entities.ParameterElement;
import composestar.dotNET2.tym.entities.TypeElement;

/**
 * Reads the type information from the XML files
 */
@ComposestarModule(ID = ModuleNames.COLLECTOR)
public class StarLightCollectorRunner implements CTCommonModule
{
	private static CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.COLLECTOR);

	private boolean processBodies = false;

	private List<DotNETCallToOtherMethod> callsToOtherMethods = new ArrayList<DotNETCallToOtherMethod>();

	private ConfigurationContainer configContainer;

	private CommonResources resources;

	private UnitRegister register;

	protected Map<String, DotNETType> newAttributeTypes;

	protected Map<String, Type> typeMap;

	protected ReferenceManager refman;

	public ModuleReturnValue run(CommonResources resc) throws ModuleException
	{
		resources = resc;
		refman = resc.get(ReferenceManager.RESOURCE_KEY);
		if (refman == null)
		{
			refman = new ReferenceManagerImpl();
			resources.put(ReferenceManager.RESOURCE_KEY, refman);
		}
		register = (UnitRegister) resources.get(UnitRegister.RESOURCE_KEY);
		if (register == null)
		{
			register = new UnitRegister();
			resources.put(UnitRegister.RESOURCE_KEY, register);
		}
		configContainer = (ConfigurationContainer) resources.get(StarLightMaster.RESOURCE_CONFIGCONTAINER);

		// Collect filtertypes and filteractions
		collectFilterTypesAndActions();

		// Collect the type information from assemblies
		collectAssemblies();

		register.resolveTypes(new Composestar.DotNET2.LAMA.DotNETTypeResolver());

		// Collection cps concern sources
		collectConcernSources();

		// Collect primitive concerns
		collectPrimitiveConcerns();

		// resolve the MethodInfo reference in the calls within a method
		resolveCallsToOtherMethods();

		return ModuleReturnValue.Ok;
	}

	/**
	 * Convert the integer to a flow behavior value
	 * 
	 * @param value
	 * @return
	 */
	private FlowBehavior getFlowBehavior(int value)
	{
		// see Composestar.StarLight.Entities.Configuration.FilterActionElement
		switch (value)
		{
			case 2:
				return FlowBehavior.EXIT;
			case 3:
				return FlowBehavior.RETURN;
			default:
				return FlowBehavior.CONTINUE;
		}
	}

	/**
	 * @param value
	 * @return
	 */
	private FlowBehavior getFlowBehavior(String value)
	{
		if ("exit".equalsIgnoreCase(value))
		{
			return FlowBehavior.EXIT;
		}
		else if ("return".equalsIgnoreCase(value))
		{
			return FlowBehavior.RETURN;
		}
		else
		{
			return FlowBehavior.CONTINUE;
		}
	}

	/**
	 * @param value
	 * @return
	 */
	private JoinPointContextArgument getJPCA(boolean value)
	{
		if (value) return JoinPointContextArgument.FULL;
		else return JoinPointContextArgument.UNUSED;
	}

	private void collectFilterTypesAndActions() throws ModuleException
	{
		long starttime = System.currentTimeMillis();

		// create mapping from strings to filteractions, to use later to resolve
		// the actions in a filtertype
		Map<String, FilterAction> actionMapping = new HashMap<String, FilterAction>();

		// get FilterActions
		List<FilterActionElement> storedActions = configContainer.getFilterActions().getFilterActionList();
		for (FilterActionElement storedAction : storedActions)
		{
			FilterActionImpl filterAction = new FilterActionImpl(storedAction.getName(), storedAction.getFullName());
			filterAction.setFlowBehavior(getFlowBehavior(storedAction.getFlowBehavior()));
			// filterAction.setMessageChangeBehaviour(storedAction.
			// getMessageChangeBehavior());
			filterAction.setResourceOperations(storedAction.getResourceOperations());
			filterAction.setJoinPointContextArgument(getJPCA(storedAction.getCreateJPC()));

			actionMapping.put(filterAction.getName(), filterAction);
			resources.repository().add(filterAction);
		}

		// get FilterTypes:
		List<FilterTypeElement> storedTypes = configContainer.getFilterTypes().getFilterTypeList();
		for (FilterTypeElement storedType : storedTypes)
		{
			PrimitiveFilterTypeImpl filterType = new PrimitiveFilterTypeImpl(storedType.getName());

			// get acceptCallAction:
			FilterAction acceptCallAction = actionMapping.get(storedType.getAcceptCallAction());
			if (acceptCallAction == null)
			{
				throw new ModuleException("AcceptCallAction '" + storedType.getAcceptCallAction()
						+ "' not found for FilterType '" + storedType.getName() + "'.", ModuleNames.COLLECTOR);
			}
			filterType.setAcceptCallAction(acceptCallAction);

			// get rejectCallAction:
			FilterAction rejectCallAction = actionMapping.get(storedType.getRejectCallAction());
			if (rejectCallAction == null)
			{
				throw new ModuleException("RejectCallAction '" + storedType.getRejectCallAction()
						+ "' not found for FilterType '" + storedType.getName() + "'.", ModuleNames.COLLECTOR);
			}
			filterType.setRejectCallAction(rejectCallAction);

			// get acceptReturnAction:
			FilterAction acceptReturnAction = actionMapping.get(storedType.getAcceptReturnAction());
			if (acceptReturnAction == null)
			{
				throw new ModuleException("AcceptReturnAction '" + storedType.getAcceptReturnAction()
						+ "' not found for FilterType '" + storedType.getName() + "'.", ModuleNames.COLLECTOR);
			}
			filterType.setAcceptReturnAction(acceptReturnAction);

			// get rejectReturnAction:
			FilterAction rejectReturnAction = actionMapping.get(storedType.getRejectReturnAction());
			if (rejectReturnAction == null)
			{
				throw new ModuleException("RejectReturnAction '" + storedType.getRejectReturnAction()
						+ "' not found for FilterType '" + storedType.getName() + "'.", ModuleNames.COLLECTOR);
			}
			filterType.setRejectReturnAction(rejectReturnAction);

			resources.repository().add(filterType);
		}

		logger.debug(storedTypes.size() + " filters with " + actionMapping.size()
				+ " filter actions read from database in " + (System.currentTimeMillis() - starttime) + " ms.");
	}

	private void collectAssemblies() throws ModuleException
	{
		CPSTimer timer = CPSTimer.getTimer(ModuleNames.COLLECTOR);
		for (AssemblyConfig ac : configContainer.getAssemblies().getAssemblyConfigList())
		{
			String assemblyName = ac.getName();

			logger.debug("Processing assembly '" + assemblyName + "'...");

			timer.start(assemblyName);
			collectAssembly(ac);
			timer.stop();
		}
	}

	private void collectAssembly(AssemblyConfig assembly) throws ModuleException
	{
		String filename = assembly.getTypeSpecificationFile();

		logger.debug("Loading type information from '" + filename + "'...");

		InputStream is = null;
		try
		{
			CPSTimer timer = CPSTimer.getTimer(ModuleNames.COLLECTOR, "XML deserialize");

			is = new FileInputStream(filename);
			if (filename.endsWith(".gz"))
			{
				is = new GZIPInputStream(is);
			}

			AssemblyDocument doc = AssemblyDocument.Factory.parse(is);

			timer.stop();

			collectTypes(doc.getAssembly());
		}
		catch (XmlException e)
		{
			throw new ModuleException(
					"CollectorRunner: XmlException while parsing " + filename + ": " + e.getMessage(),
					ModuleNames.COLLECTOR);
		}
		catch (IOException e)
		{
			throw new ModuleException("CollectorRunner: IOException while parsing " + filename + ": " + e.getMessage(),
					ModuleNames.COLLECTOR);
		}
		finally
		{
			FileUtils.close(is);
		}
	}

	private void collectConcernSources()
	{
		List<ConcernElement> concerns = configContainer.getConcerns().getConcernList();
		for (ConcernElement ce : concerns)
		{
			File concernFile = new File(ce.getPathName(), ce.getFileName());
			resources.configuration().getProject().addConcern(concernFile);
		}
	}

	private void collectPrimitiveConcerns()
	{
		logger.debug("Processing primitive concerns...");
		long starttime = System.currentTimeMillis();

		newAttributeTypes = new HashMap<String, DotNETType>();
		typeMap = register.getTypeMap();

		for (Type type : typeMap.values())
		{
			// Collect type attributes
			List<Annotation> typeAnnos = type.getAnnotations();
			if (typeAnnos != null)
			{
				for (Annotation annot : typeAnnos)
				{
					resolveAttributeType(annot, type);
				}
			}

			// Collect field attributes
			List<DotNETFieldInfo> fields = type.getFields();
			for (DotNETFieldInfo field : fields)
			{
				List<Annotation> fieldAnnos = field.getAnnotations();
				if (fieldAnnos != null)
				{
					for (Annotation annot : fieldAnnos)
					{
						resolveAttributeType(annot, field);
					}
				}
			}

			// Collect method attributes
			List<DotNETMethodInfo> methods = type.getMethods();
			for (DotNETMethodInfo method : methods)
			{
				List<Annotation> methodAnnos = method.getAnnotations();
				if (methodAnnos != null)
				{
					for (Annotation annot : methodAnnos)
					{
						resolveAttributeType(annot, method);
					}
				}
			}

			// Add type to repository as primitive concern

			TypeReference tref = refman.getTypeReference(type.getFullName());
			tref.setReference(type);

			Concern pc = null;
			RepositoryEntity o = resources.repository().get(type.getFullName(), Concern.class);
			if (o instanceof CpsConcern)
			{
				pc = (Concern) o;
				if (pc.getTypeReference() != null)
				{
					type.setConcern(pc);
					logger.error(String.format("CpsConcern %s is already bound to a platform representation", pc
							.getFullyQualifiedName()));
					continue;
				}
			}
			if (pc == null)
			{
				pc = new PrimitiveConcern(type.getFullName().split("\\."));
				Composestar.Core.Config.Source typeSource = resources.configuration().getProject().getTypeMapping()
						.getSource(type.getFullName());
				if (typeSource != null)
				{
					pc.setSourceInformation(new SourceInformation(new FileInformation(typeSource.getFile())));
				}
				resources.repository().add(pc);
			}
			pc.setTypeReference(tref);
			type.setConcern(pc);

			// logger.debug("Adding primitive concern '" + pc.getName() + "'");
		}

		long elapsed = System.currentTimeMillis() - starttime;
		logger.debug(typeMap.size() + " primitive concerns added in " + elapsed + " ms.");

		for (DotNETType nat : newAttributeTypes.values())
		{
			register.registerLanguageUnit(nat);
		}
	}

	private void resolveAttributeType(Annotation annotation, ProgramElement inTarget)
	{
		DotNETType attributeType;
		if (typeMap.containsKey(annotation.getTypeName()))
		{
			// Attribute type has been resolved by the analyzer
			attributeType = (DotNETType) typeMap.get(annotation.getTypeName());
		}
		else if (newAttributeTypes.containsKey(annotation.getTypeName()))
		{
			// Attribute type has been encountered before, use previously
			// created type
			attributeType = newAttributeTypes.get(annotation.getTypeName());
		}
		else
		{
			// Create a new DotNETType element
			attributeType = new DotNETType();
			attributeType.setFullName(annotation.getTypeName());
			// register.registerLanguageUnit(attributeType);

			// Add this attribute type to the repository as a primitive
			// concern
			Concern pc_attribute = null;
			RepositoryEntity o = resources.repository().get(attributeType.getFullName());
			if (o instanceof CpsConcern)
			{
				pc_attribute = (Concern) o;
				if (pc_attribute.getTypeReference() != null)
				{
					attributeType.setConcern(pc_attribute);
					logger.error(String.format("CpsConcern %s is already bound to a platform representation",
							pc_attribute.getFullyQualifiedName()));
				}
			}
			if (pc_attribute == null)
			{
				pc_attribute = new PrimitiveConcern(attributeType.getFullName().split("\\."));
				resources.repository().add(pc_attribute);
			}
			TypeReference tref = refman.getTypeReference(annotation.getTypeName());
			tref.setReference(attributeType);
			pc_attribute.setTypeReference(tref);
			attributeType.setConcern(pc_attribute);

			// Add this attribute type to the list of added types
			newAttributeTypes.put(attributeType.getFullName(), attributeType);
		}
		// annotation.register(attributeType, inTarget);
		annotation.setType(attributeType);
		annotation.setTarget(inTarget);
		attributeType.addAnnotationInstance(annotation);
	}

	/**
	 * Process all types, i.e. map them to LAMA
	 */
	private void collectTypes(AssemblyElement assembly) throws ModuleException
	{
		List<TypeElement> types = assembly.getTypes().getTypeList();

		logger.debug("Generating language model with " + types.size() + " types...");
		long starttime = System.currentTimeMillis();

		for (TypeElement te : types)
		{
			String fullName = getFullName(te);

			// logger.debug("Processing type '" + fullName + "'...");

			DotNETType dnt = new DotNETType();
			dnt.setName(te.getName());
			dnt.setNamespace(te.getNamespace());
			dnt.setFullName(fullName);
			register.registerLanguageUnit(dnt);
			dnt.setBaseType(te.getBaseType());
			dnt.setAssemblyName(assembly.getName());
			dnt.setFromSource(te.getFromSource());
			dnt.setEndPos(te.getEndPos());

			// Set the implemented interfaces
			List<String> interfaces = te.getInterfaces().getInterfaceList();
			for (String iface : interfaces)
			{
				dnt.addImplementedInterface(iface);
			}

			dnt.setIsClass(te.getIsClass());
			dnt.setIsInterface(te.getIsInterface());
			dnt.setIsEnum(te.getIsEnum());
			dnt.setIsValueType(te.getIsValueType());
			dnt.setIsPrimitive(te.getIsPrimitive());
			dnt.setIsPublic(te.getIsPublic());
			dnt.setIsAbstract(te.getIsAbstract());
			dnt.setIsSealed(te.getIsSealed());

			// Set the attributes for this type
			List<DotNETAttribute> attributes = collectAttributes(te.getAttributes());
			for (DotNETAttribute attribute : attributes)
			{
				dnt.addAnnotation(attribute);
			}

			collectFields(te, dnt);
			collectMethods(te, dnt);
		}

		long elapsed = System.currentTimeMillis() - starttime;
		logger.debug("Language model generated in " + elapsed + " ms.");
	}

	private String getFullName(TypeElement te) throws ModuleException
	{
		String name = te.getName();
		String ns = te.getNamespace();

		if (te.getName() == null)
		{
			throw new ModuleException("Type must have a name attribute", ModuleNames.COLLECTOR);
		}

		// see rev. 2806
		if (ns.endsWith("+"))
		{
			return ns + name;
		}
		else
		{
			if (ns.equals(""))
			{
				return name;
			}
			return ns + "." + name;
		}
	}

	private List<DotNETAttribute> collectAttributes(ArrayOfAttributeElement attributes)
	{
		if (attributes == null)
		{
			return Collections.emptyList();
		}

		List<DotNETAttribute> result = new ArrayList<DotNETAttribute>();
		for (AttributeElement ae : attributes.getAttributeList())
		{
			DotNETAttribute attribute = new DotNETAttribute();
			attribute.setTypeName(ae.getAttributeType());

			// Set value for this attribute
			// if (ae.getValues().sizeOfValueArray() >= 1)
			// {
			//attribute.setStringValue(ae.getValues().getValueArray(0).getValue(
			// ));
			// }

			for (AttributeValueElement val : ae.getValues().getValueList())
			{
				attribute.setValue(val.getName(), val.getValue());
			}

			result.add(attribute);
		}
		return result;
	}

	private void collectFields(TypeElement storedType, DotNETType type) throws ModuleException
	{
		// Process all fields
		List<FieldElement> fields = storedType.getFields().getFieldList();
		for (FieldElement storedField : fields)
		{
			// logger.debug("Processing field '" + storedField.getName() +
			// "'...");

			DotNETFieldInfo field = new DotNETFieldInfo();
			register.registerLanguageUnit(field);
			field.setIsDeclaredHere(true);
			field.setName(storedField.getName());
			field.setFieldType(storedField.getType());
			field.setIsPrivate(storedField.getIsPrivate());
			field.setIsPublic(storedField.getIsPublic());
			field.setIsStatic(storedField.getIsStatic());

			// Set the attributes for this field
			List<DotNETAttribute> attributes = collectAttributes(storedField.getAttributes());
			for (DotNETAttribute attribute : attributes)
			{
				field.addAnnotation(attribute);
			}

			type.addField(field);
		}
	}

	private void collectMethods(TypeElement storedType, DotNETType type) throws ModuleException
	{
		List<MethodElement> methods = storedType.getMethods().getMethodList();
		for (MethodElement storedMethod : methods)
		{
			String name = storedMethod.getName();

			// logger.debug("Processing method '" + name + "'...");

			if (name == null)
			{
				throw new ModuleException("Method must have a name attribute", ModuleNames.COLLECTOR);
			}

			DotNETMethodInfo method = new DotNETMethodInfo();
			register.registerLanguageUnit(method);
			method.setIsDeclaredHere(true);
			method.setName(storedMethod.getName());
			method.setReturnType(storedMethod.getReturnType());
			method.setSignature(storedMethod.getSignature());
			method.setIsConstructor(storedMethod.getIsConstructor());
			method.setIsPrivate(storedMethod.getIsPrivate());
			method.setIsPublic(storedMethod.getIsPublic());
			method.setIsStatic(storedMethod.getIsStatic());
			method.setIsAbstract(storedMethod.getIsAbstract());
			method.setIsVirtual(storedMethod.getIsVirtual());

			collectParameters(storedMethod, method);

			if (processBodies && storedMethod.isSetBody())
			{
				collectMethodBody(storedMethod.getBody(), method);
			}

			// Set the attributes for this method
			List<DotNETAttribute> attributes = collectAttributes(storedMethod.getAttributes());
			for (DotNETAttribute attribute : attributes)
			{
				method.addAnnotation(attribute);
			}

			type.addMethod(method);
		}
	}

	private void collectParameters(MethodElement storedMethod, DotNETMethodInfo method) throws ModuleException
	{
		// Get all parameters for the method 'storedmethod'
		ArrayOfParameterElement storedParameters = storedMethod.getParameters();

		// Process all parameters
		DotNETParameterInfo[] parameters = new DotNETParameterInfo[storedParameters.sizeOfParameterArray()];

		for (int i = 0; i < storedParameters.sizeOfParameterArray(); i++)
		{
			ParameterElement storedParameter = storedParameters.getParameterArray(i);
			String name = storedParameter.getName();

			// logger.debug("Retrieving parameter '" + name + "'");

			if (name == null)
			{
				throw new ModuleException("ParameterInfo must have a name attribute.", ModuleNames.COLLECTOR);
			}

			DotNETParameterInfo parameter = new DotNETParameterInfo();
			register.registerLanguageUnit(parameter);
			parameter.setName(name);
			parameter.setPosition(storedParameter.getOrdinal());
			parameter.setParameterType(storedParameter.getType());

			parameters[parameter.position() - 1] = parameter;
		}

		for (DotNETParameterInfo element : parameters)
		{
			method.addParameter(element);
		}
	}

	private void collectMethodBody(MethodBody storedMethodBody, DotNETMethodInfo method)
	{
		// Get the call elements for this method body
		ArrayOfCallElement storedCalls = storedMethodBody.getCalls();

		for (int i = 0; i < storedCalls.sizeOfCallArray(); i++)
		{
			CallElement storedCall = storedCalls.getCallArray(i);

			DotNETCallToOtherMethod call = new DotNETCallToOtherMethod();
			call.setCallElement(storedCall);

			// TODO: is this mapping correct ?
			call.operationName = storedCall.getMethodReference();

			method.getCallsToOtherMethods().add(call);

			callsToOtherMethods.add(call);
		}
	}

	/**
	 * Resolve the MethodInfo of the called method for all calls to other
	 * methods.
	 */
	private void resolveCallsToOtherMethods()
	{
		if (processBodies)
		{
			logger.debug("Resolving method references for calls withing a method...");
			long starttime = System.currentTimeMillis();

			for (CallToOtherMethod call : callsToOtherMethods)
			{
				call.setCalledMethod(getMethodInfo(call));
			}

			long elapsed = System.currentTimeMillis() - starttime;
			logger.debug("Method references resolved in " + elapsed + " ms.");
		}
	}

	private MethodInfo getMethodInfo(CallToOtherMethod call)
	{
		String operation = call.getOperationName();

		// separate returntype part:
		int pos1 = operation.indexOf(' ');
		// String returnType = operation.substring(0, pos1);

		// separate type:
		int pos2 = operation.indexOf(':');
		String typeName = operation.substring(pos1 + 1, pos2);

		// separate methodname:
		int pos3 = operation.indexOf('(');
		String methodName = operation.substring(pos2 + 2, pos3);

		// separate arguments:
		int pos4 = operation.indexOf(')');
		String arguments = operation.substring(pos3 + 1, pos4);

		StringTokenizer tokenizer = new StringTokenizer(arguments, ",");
		int tokenCount = tokenizer.countTokens();
		String[] argTypes = new String[tokenCount];
		for (int i = 0; i < tokenCount; i++)
		{
			argTypes[i] = tokenizer.nextToken();
		}

		// get method info:
		Type type = register.getType(typeName);
		MethodInfo methodInfo = null;
		if (type != null)
		{
			methodInfo = type.getMethod(methodName, argTypes);
		}

		call.setMethodName(methodName);

		return methodInfo;
	}
}
