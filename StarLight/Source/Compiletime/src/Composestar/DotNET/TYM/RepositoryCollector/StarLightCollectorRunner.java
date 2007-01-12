package Composestar.DotNET.TYM.RepositoryCollector;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

import org.apache.xmlbeans.XmlException;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.PrimitiveConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.INCREReporter;
import Composestar.Core.INCRE.INCRETimer;
import Composestar.Core.LAMA.CallToOtherMethod;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.TypeMap;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.TYM.TypeCollector.CollectorRunner;
import Composestar.DotNET.LAMA.DotNETAttribute;
import Composestar.DotNET.LAMA.DotNETCallToOtherMethod;
import Composestar.DotNET.LAMA.DotNETFieldInfo;
import Composestar.DotNET.LAMA.DotNETMethodInfo;
import Composestar.DotNET.LAMA.DotNETParameterInfo;
import Composestar.DotNET.LAMA.DotNETType;
import Composestar.DotNET.MASTER.StarLightMaster;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;

import composestar.dotNET.tym.entities.ArrayOfAssemblyConfig;
import composestar.dotNET.tym.entities.ArrayOfAttributeElement;
import composestar.dotNET.tym.entities.ArrayOfCallElement;
import composestar.dotNET.tym.entities.ArrayOfFieldElement;
import composestar.dotNET.tym.entities.ArrayOfFilterActionElement;
import composestar.dotNET.tym.entities.ArrayOfFilterTypeElement;
import composestar.dotNET.tym.entities.ArrayOfMethodElement;
import composestar.dotNET.tym.entities.ArrayOfParameterElement;
import composestar.dotNET.tym.entities.AssemblyConfig;
import composestar.dotNET.tym.entities.AssemblyDocument;
import composestar.dotNET.tym.entities.AssemblyElement;
import composestar.dotNET.tym.entities.AttributeElement;
import composestar.dotNET.tym.entities.CallElement;
import composestar.dotNET.tym.entities.ConfigurationContainer;
import composestar.dotNET.tym.entities.FieldElement;
import composestar.dotNET.tym.entities.FilterActionElement;
import composestar.dotNET.tym.entities.FilterTypeElement;
import composestar.dotNET.tym.entities.MethodBody;
import composestar.dotNET.tym.entities.MethodElement;
import composestar.dotNET.tym.entities.ParameterElement;
import composestar.dotNET.tym.entities.TypeElement;

public class StarLightCollectorRunner implements CollectorRunner
{
	public static final String MODULE_NAME = "COLLECTOR";
	public static final String MODULE_NAME_INCRE = "COLLECTOR";
	
	private boolean processBodies = false;
	private List callsToOtherMethods = new ArrayList();
	
	private INCREReporter reporter = INCRE.instance().getReporter();
	private INCRETimer deserializeTimer = reporter.openProcess(MODULE_NAME, "xml deserialize", INCRETimer.TYPE_NORMAL);
	private long starttime = 0;

	public void run(CommonResources resources) throws ModuleException
	{
		INCRE incre = INCRE.instance();
		DataStore dataStore = DataStore.instance();
		Map typeMap = TypeMap.instance().map();
		ConfigurationContainer configContainer = StarLightMaster.getConfigContainer();

		// Collect all filtertypes and filteractions:
		collectFilterTypesAndActions(configContainer);
		
		// Collect all types from the persistent repository
		ArrayOfAssemblyConfig assemblies = configContainer.getAssemblies();
		for (int i = 0; i < assemblies.sizeOfAssemblyConfigArray(); i++)
		{
			AssemblyConfig ac = assemblies.getAssemblyConfigArray(i);
			String assemblyName = ac.getName();
			
			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Processing assembly '" + assemblyName + "'...");
			
			if (incre.isProcessedByModule(ac, MODULE_NAME))
			{
				INCRETimer copyTimer = reporter.openProcess(MODULE_NAME,assemblyName,INCRETimer.TYPE_INCREMENTAL);
				copyOperation(assemblyName);
				copyTimer.stop();
			}
			else
			{
				INCRETimer runTimer = reporter.openProcess(MODULE_NAME,assemblyName,INCRETimer.TYPE_NORMAL);
				collectOperation(ac);
				runTimer.stop();
			}
		}

		// loop through rest of the concerns and add to the repository in the
		// form of primitive concerns
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Processing primitive concerns...");
		starttime = System.currentTimeMillis();
		
		Set unresolvedAttributeTypes = new HashSet();
		
		Iterator it = typeMap.values().iterator();
		while (it.hasNext())
		{
			DotNETType type = (DotNETType) it.next();
			
			// Collect type attributes
			List typeAnnos = type.getAnnotations();
			if (typeAnnos != null)
			{
				for (int i = 0; i < typeAnnos.size(); i++)
					unresolvedAttributeTypes.add(typeAnnos.get(i));
			}
			
			// Collect field attributes
			Iterator fieldIter = type.getFields().iterator();
			while (fieldIter.hasNext()) 
			{
				DotNETFieldInfo field = (DotNETFieldInfo)fieldIter.next();
				List fieldAnnos = field.getAnnotations();
				if (fieldAnnos != null)
				{
					for (int i = 0; i < fieldAnnos.size(); i++)
						unresolvedAttributeTypes.add(fieldAnnos.get(i));
				}
			}
			
			// Collect method attributes
			Iterator methodIter = type.getMethods().iterator();
			while (methodIter.hasNext())
			{
				DotNETMethodInfo method = (DotNETMethodInfo)methodIter.next();
				List methodAnnos = method.getAnnotations();
				if (methodAnnos != null)
				{
					for (int i = 0; i < methodAnnos.size(); i++)
						unresolvedAttributeTypes.add(methodAnnos.get(i));
				}
			}
			
			// Add type to repository as primitive concern
			PrimitiveConcern pc = new PrimitiveConcern();
			pc.setName(type.fullName());
			pc.setPlatformRepresentation(type);
			type.setParentConcern(pc);
			dataStore.addObject(type.fullName(), pc);
			
		//	Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Adding primitive concern '" + pc.getName() + "'");
		}
		
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, typeMap.size() + " primitive concerns added in "
				+ (System.currentTimeMillis() - starttime) + " ms.");
		
		// Resolve attribute types
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Resolving " + unresolvedAttributeTypes.size() + " attribute types...");
		starttime = System.currentTimeMillis();
		
		Map newAttributeTypes = new HashMap();
		Iterator unresolvedAttributeTypeIter = unresolvedAttributeTypes.iterator();
		while (unresolvedAttributeTypeIter.hasNext())
		{
			DotNETAttribute attribute = (DotNETAttribute)unresolvedAttributeTypeIter.next();
		
			if (typeMap.containsKey(attribute.getTypeName()))
			{
				// Attribute type has been resolved by the analyzer
				attribute.setType((Type)typeMap.get(attribute.getTypeName()));
			}
			else if (newAttributeTypes.containsKey(attribute.getTypeName()))
			{
				// Attribute type has been encountered before, use previously created type
				attribute.setType((Type)newAttributeTypes.get(attribute.getTypeName()));
			}
			else
			{
				// Create a new DotNETType element
				DotNETType attributeType = new DotNETType();
				attributeType.setFullName(attribute.getTypeName());
				
				// Add this attribute type to the repository as a primitive concern
				PrimitiveConcern pc_attribute = new PrimitiveConcern();
				pc_attribute.setName(attributeType.fullName());
				pc_attribute.setPlatformRepresentation(attributeType);
				attributeType.setParentConcern(pc_attribute);
				dataStore.addObject(attributeType.fullName(), pc_attribute);
				
				// Add this attribute type to the list of added types
				newAttributeTypes.put(attributeType.fullName(), attributeType);
			}
		}
		
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Attribute types resolved in "
				+ (System.currentTimeMillis() - starttime) + " ms.");
		
		// resolve the MethodInfo reference in the calls within a method:	
		if (processBodies)
		{
			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, 
					"Resolving method references for calls withing a method...");
			starttime = System.currentTimeMillis();

			resolveCallsToOtherMethods();
		
			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, 
					"Method references resolved in " + (System.currentTimeMillis() - starttime) + " ms.");
		}
	}
	
	public void copyOperation(String assemblyName) throws ModuleException
	{
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME_INCRE, "Restoring types from '" + assemblyName + "'");
		INCRE incre = INCRE.instance();

		int typecount = 0;

		/* collect and iterate over all objects from previous compilation runs */
		Iterator it = incre.history.getIterator();
		while (it.hasNext())
		{
			Object obj = it.next();

			// Only restore PrimitiveConcerns and CpsConcerns
			if (obj instanceof PrimitiveConcern || obj instanceof CpsConcern)
			{
				Concern c = (Concern)obj;

				// Get the .NET platform representation for this concern
				DotNETType t = (DotNETType)c.getPlatformRepresentation();

				// Add to datastore if type belongs to the assembly we are restoring
				if (t != null && assemblyName.equals(t.assemblyName()))
				{
					// Register the type with LAMA
					t.setParentConcern(null);
					Composestar.Core.LAMA.UnitRegister.instance().registerLanguageUnit(t);

					// Add the type to the TypeMap
					TypeMap.instance().addType(t.fullName(), t);
					typecount++;
				}
			}
		}

		Debug.out(Debug.MODE_DEBUG, MODULE_NAME_INCRE, typecount + " types restored");
	}
    
	public void collectOperation(AssemblyConfig assembly) throws ModuleException
	{
		String serializedFilename = assembly.getTypeSpecificationFile();

		InputStream is = null;
		try
		{
			deserializeTimer.start();
			is = new GZIPInputStream(new FileInputStream(serializedFilename));
			AssemblyDocument doc = AssemblyDocument.Factory.parse(is);
			deserializeTimer.stop();
			
			collectTypes(doc.getAssembly());
		}
		catch (XmlException e)
		{
			throw new ModuleException(
					"CollectorRunner: XmlException while parsing " + serializedFilename +
					": " + e.getMessage(), MODULE_NAME);
		}
		catch (IOException e)
		{
			throw new ModuleException(
					"CollectorRunner: IOException while parsing " + serializedFilename + 
					": " + e.getMessage(), MODULE_NAME);
		}
		finally
		{
			FileUtils.close(is);
		}
	}
	
	private void collectFilterTypesAndActions(ConfigurationContainer config) throws ModuleException
	{
		long starttime = System.currentTimeMillis();

		// create mapping from strings to filteractions, to use later to resolve
		// the actions in a filtertype
		Hashtable actionMapping = new Hashtable();

		// get FilterActions
		ArrayOfFilterActionElement storedActions = config.getFilterActions();
		for (int i = 0; i < storedActions.sizeOfFilterActionArray(); i++)
		{
			FilterActionElement storedAction = storedActions.getFilterActionArray(i);

			FilterAction filterAction = new FilterAction();
			filterAction.setName(storedAction.getName());
			filterAction.setFullName(storedAction.getFullName());
			filterAction.setFlowBehaviour(storedAction.getFlowBehavior());
			filterAction.setMessageChangeBehaviour(storedAction.getMessageChangeBehavior());

			actionMapping.put(filterAction.getName(), filterAction);
		}

		// get FilterTypes:
		ArrayOfFilterTypeElement storedFilters = config.getFilterTypes();
		for (int i = 0; i < storedFilters.sizeOfFilterTypeArray(); i++)
		{
			FilterTypeElement storedType = storedFilters.getFilterTypeArray(i);

			FilterType filterType = new FilterType();
			filterType.setType(storedType.getName());

			// get acceptCallAction:
			FilterAction acceptCallAction = (FilterAction) actionMapping.get(storedType.getAcceptCallAction());
			if (acceptCallAction == null)
			{
				throw new ModuleException("AcceptCallAction '" + storedType.getAcceptCallAction()
						+ "' not found for FilterType '" + storedType.getName() + "'.", MODULE_NAME);
			}
			filterType.setAcceptCallAction(acceptCallAction);

			// get rejectCallAction:
			FilterAction rejectCallAction = (FilterAction) actionMapping.get(storedType.getRejectCallAction());
			if (rejectCallAction == null)
			{
				throw new ModuleException("RejectCallAction '" + storedType.getRejectCallAction()
						+ "' not found for FilterType '" + storedType.getName() + "'.", MODULE_NAME);
			}
			filterType.setRejectCallAction(rejectCallAction);

			// get acceptReturnAction:
			FilterAction acceptReturnAction = (FilterAction) actionMapping.get(storedType.getAcceptReturnAction());
			if (acceptReturnAction == null)
			{
				throw new ModuleException("AcceptReturnAction '" + storedType.getAcceptReturnAction()
						+ "' not found for FilterType '" + storedType.getName() + "'.", MODULE_NAME);
			}
			filterType.setAcceptReturnAction(acceptReturnAction);

			// get rejectReturnAction:
			FilterAction rejectReturnAction = (FilterAction) actionMapping.get(storedType.getRejectReturnAction());
			if (rejectReturnAction == null)
			{
				throw new ModuleException("RejectReturnAction '" + storedType.getRejectReturnAction()
						+ "' not found for FilterType '" + storedType.getName() + "'.", MODULE_NAME);
			}
			filterType.setRejectReturnAction(rejectReturnAction);
		}

		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, storedFilters.sizeOfFilterTypeArray() + " filters with "
				+ actionMapping.size() + " filter actions read from database in "
				+ (System.currentTimeMillis() - starttime) + " ms.");
	}
	
	private String getFullName(TypeElement te) throws ModuleException
	{
		String name = te.getName();
		String ns = te.getNamespace();

		if (te.getName() == null)
			throw new ModuleException("Type must have a name attribute", MODULE_NAME);
		
		// see rev. 2806
		return (ns.endsWith("+") ? (ns + name) : (ns + "." + name));
	}

	private void collectTypes(AssemblyElement assembly) throws ModuleException
	{
		TypeElement[] types = assembly.getTypes().getTypeArray();

		// Process all types, i.e. map them to LAMA
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Generating language model with " + types.length + " types...");
		
		starttime = System.currentTimeMillis();
		for (int i = 0; i < types.length; i++)
		{
			TypeElement te = types[i];
			String fullName = getFullName(te);

		//	Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Processing type '" + fullName + "'...");

			DotNETType dnt = new DotNETType();
			dnt.setName(te.getName());
			dnt.setNamespace(te.getNamespace());
			dnt.setFullName(fullName);
			dnt.setAssemblyName(assembly.getName());
			dnt.setBaseType(te.getBaseType());
			
			// Set the implemented interfaces
			String[] interfaces = te.getInterfaces().getInterfaceArray();
			for (int j = 0; j < interfaces.length; j++)
			{
				dnt.addImplementedInterface(interfaces[j]);
			}
			
			dnt.setIsClass(te.getIsClass());
			dnt.setIsInterface(te.getIsInterface());
			dnt.setIsEnum(te.getIsEnum());
			dnt.setIsValueType(te.getIsValueType());
			dnt.setIsPrimitive(te.getIsPrimitive());
			dnt.setIsPublic(te.getIsPublic());
			dnt.setIsAbstract(te.getIsAbstract());
			dnt.setIsSealed(te.getIsSealed());
		//	dnt.setFromDLL(assembly.getName());
			
			//Set the attributes for this type
			DotNETAttribute[] attributes = collectAttributes(te.getAttributes());
			for (int j = 0; j < attributes.length; j++)
			{
				dnt.addAnnotation(attributes[j]);
			}

			collectFields(te, dnt);
			collectMethods(te, dnt);

			// Add the DotNETType to the TypeMap
			TypeMap.instance().addType(dnt.fullName(), dnt);
		}
		
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Language model generated in "
				+ (System.currentTimeMillis() - starttime) + " ms.");
	}
	
	private DotNETAttribute[] collectAttributes(ArrayOfAttributeElement attributes)
	{
		if (attributes == null) return new DotNETAttribute[0];
		
		DotNETAttribute[] result = new DotNETAttribute[attributes.sizeOfAttributeArray()];
		
		for (int i = 0; i < attributes.sizeOfAttributeArray(); i++)
		{
			AttributeElement ae = attributes.getAttributeArray(i);
			
			DotNETAttribute attribute = new DotNETAttribute();
			attribute.setTypeName(ae.getAttributeType());	
			
			// Set value for this attribute			
			if (ae.getValues().sizeOfValueArray() >= 1) 
				attribute.setValue(ae.getValues().getValueArray(0).getValue());

			result[i] = attribute;
		}
	
		return result;
	}

	private void collectFields(TypeElement storedType, DotNETType type) throws ModuleException
	{
		// Get all fields for the type 'storedType'
		ArrayOfFieldElement fields = storedType.getFields();

		// Process all fields
		for (int i = 0; i < fields.sizeOfFieldArray(); i++)
		{
			FieldElement storedField = fields.getFieldArray(i);

		//	Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Processing field '" + storedField.getName() + "'...");

			DotNETFieldInfo field = new DotNETFieldInfo();
			field.setIsDeclaredHere(true);
			field.setName(storedField.getName());
			field.setFieldType(storedField.getType());
			field.setIsPrivate(storedField.getIsPrivate());
			field.setIsPublic(storedField.getIsPublic());
			field.setIsStatic(storedField.getIsStatic());

			//Set the attributes for this field
			DotNETAttribute[] attributes = collectAttributes(storedField.getAttributes());
			for (int j = 0; j < attributes.length; j++)
				field.addAnnotation(attributes[j]);
			
			type.addField(field);
		}
	}

	private void collectMethods(TypeElement storedType, DotNETType type) throws ModuleException
	{
		ArrayOfMethodElement methods = storedType.getMethods();

		for (int i = 0; i < methods.sizeOfMethodArray(); i++)
		{
			MethodElement storedMethod = methods.getMethodArray(i);
			String name = storedMethod.getName();

		//	Debug.out(Debug.MODE_DEBUG,MODULE_NAME, "Processing method '" + name + "'...");

			if (name == null)
				throw new ModuleException("Method must have a name attribute", MODULE_NAME);

			DotNETMethodInfo method = new DotNETMethodInfo();
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
				collectMethodBody(storedMethod.getBody(), method);

			//Set the attributes for this method
			DotNETAttribute[] attributes = collectAttributes(storedMethod.getAttributes());
			for (int j = 0; j < attributes.length; j++)
				method.addAnnotation(attributes[j]);
			
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

		//	Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Retrieving parameter '" + name + "'");
			
			if (name == null)
				throw new ModuleException("ParameterInfo must have a name attribute.", MODULE_NAME);

			DotNETParameterInfo parameter = new DotNETParameterInfo();
			parameter.setName(name);
			parameter.setPosition(storedParameter.getOrdinal());
			parameter.setParameterType(storedParameter.getType());

			parameters[parameter.position() - 1] = parameter;
		}

		for (int i = 0; i < parameters.length; i++)
		{
			method.addParameter(parameters[i]);
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
			call.OperationName = storedCall.getMethodReference();

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
		Iterator it = callsToOtherMethods.iterator();
		while (it.hasNext())
		{
			CallToOtherMethod call = (CallToOtherMethod) it.next();
			call.setCalledMethod(getMethodInfo(call));
		}
	}

	private MethodInfo getMethodInfo(CallToOtherMethod call)
	{
		String operation = call.getOperationName();

		// separate returntype part:
		int pos1 = operation.indexOf(' ');
	//	String returnType = operation.substring(0, pos1);

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
		Type type = TypeMap.instance().getType(typeName);
		MethodInfo methodInfo = null;
		if (type != null)
		{
			methodInfo = type.getMethod(methodName, argTypes);
		}

		call.setMethodName(methodName);

		return methodInfo;
	}
}
