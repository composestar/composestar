package Composestar.DotNET2.TYM.RepositoryCollector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
import Composestar.Core.INCRE.INCRETimer;
import Composestar.Core.LAMA.Annotation;
import Composestar.Core.LAMA.CallToOtherMethod;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.TypeMap;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.DotNET2.LAMA.DotNETAttribute;
import Composestar.DotNET2.LAMA.DotNETCallToOtherMethod;
import Composestar.DotNET2.LAMA.DotNETFieldInfo;
import Composestar.DotNET2.LAMA.DotNETMethodInfo;
import Composestar.DotNET2.LAMA.DotNETParameterInfo;
import Composestar.DotNET2.LAMA.DotNETType;
import Composestar.DotNET2.MASTER.StarLightMaster;
import Composestar.Utils.FileUtils;
import Composestar.Utils.Logging.CPSLogger;

import composestar.dotNET2.tym.entities.ArrayOfAttributeElement;
import composestar.dotNET2.tym.entities.ArrayOfCallElement;
import composestar.dotNET2.tym.entities.ArrayOfParameterElement;
import composestar.dotNET2.tym.entities.AssemblyConfig;
import composestar.dotNET2.tym.entities.AssemblyDocument;
import composestar.dotNET2.tym.entities.AssemblyElement;
import composestar.dotNET2.tym.entities.AttributeElement;
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

public class StarLightCollectorRunner implements CTCommonModule
{
	public static final String MODULE_NAME = "COLLECTOR";

	private static CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	private boolean processBodies = false;

	private List<DotNETCallToOtherMethod> callsToOtherMethods = new ArrayList<DotNETCallToOtherMethod>();

	private ConfigurationContainer configContainer = StarLightMaster.getConfigContainer();

	private INCRE incre = INCRE.instance();

	private CommonResources resources;

	public void run(CommonResources resc) throws ModuleException
	{
		resources = resc;

		// Collect filtertypes and filteractions
		collectFilterTypesAndActions();

		// Collect the type information from assemblies
		collectAssemblies();

		// Collection cps concern sources
		collectConcernSources();

		// Collect primitive concerns
		collectPrimitiveConcerns();

		// resolve the MethodInfo reference in the calls within a method
		resolveCallsToOtherMethods();
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
			FilterAction filterAction = new FilterAction();
			filterAction.setName(storedAction.getName());
			filterAction.setFullName(storedAction.getFullName());
			filterAction.setFlowBehaviour(storedAction.getFlowBehavior());
			filterAction.setMessageChangeBehaviour(storedAction.getMessageChangeBehavior());

			actionMapping.put(filterAction.getName(), filterAction);
		}

		// get FilterTypes:
		List<FilterTypeElement> storedTypes = configContainer.getFilterTypes().getFilterTypeList();
		for (FilterTypeElement storedType : storedTypes)
		{
			FilterType filterType = new FilterType();
			filterType.setType(storedType.getName());

			// get acceptCallAction:
			FilterAction acceptCallAction = actionMapping.get(storedType.getAcceptCallAction());
			if (acceptCallAction == null)
			{
				throw new ModuleException("AcceptCallAction '" + storedType.getAcceptCallAction()
						+ "' not found for FilterType '" + storedType.getName() + "'.", MODULE_NAME);
			}
			filterType.setAcceptCallAction(acceptCallAction);

			// get rejectCallAction:
			FilterAction rejectCallAction = actionMapping.get(storedType.getRejectCallAction());
			if (rejectCallAction == null)
			{
				throw new ModuleException("RejectCallAction '" + storedType.getRejectCallAction()
						+ "' not found for FilterType '" + storedType.getName() + "'.", MODULE_NAME);
			}
			filterType.setRejectCallAction(rejectCallAction);

			// get acceptReturnAction:
			FilterAction acceptReturnAction = actionMapping.get(storedType.getAcceptReturnAction());
			if (acceptReturnAction == null)
			{
				throw new ModuleException("AcceptReturnAction '" + storedType.getAcceptReturnAction()
						+ "' not found for FilterType '" + storedType.getName() + "'.", MODULE_NAME);
			}
			filterType.setAcceptReturnAction(acceptReturnAction);

			// get rejectReturnAction:
			FilterAction rejectReturnAction = actionMapping.get(storedType.getRejectReturnAction());
			if (rejectReturnAction == null)
			{
				throw new ModuleException("RejectReturnAction '" + storedType.getRejectReturnAction()
						+ "' not found for FilterType '" + storedType.getName() + "'.", MODULE_NAME);
			}
			filterType.setRejectReturnAction(rejectReturnAction);
		}

		logger.debug(storedTypes.size() + " filters with " + actionMapping.size()
				+ " filter actions read from database in " + (System.currentTimeMillis() - starttime) + " ms.");
	}

	private void collectAssemblies() throws ModuleException
	{
		for (AssemblyConfig ac : configContainer.getAssemblies().getAssemblyConfigList())
		{
			String assemblyName = ac.getName();

			logger.debug("Processing assembly '" + assemblyName + "'...");

			if (incre.isProcessedByModule(ac, MODULE_NAME))
			{
				INCRETimer copyTimer = incre.getReporter().openProcess(MODULE_NAME, assemblyName,
						INCRETimer.TYPE_INCREMENTAL);

				copyAssemblies(assemblyName);
				copyTimer.stop();
			}
			else
			{
				INCRETimer runTimer = incre.getReporter()
						.openProcess(MODULE_NAME, assemblyName, INCRETimer.TYPE_NORMAL);

				collectAssembly(ac);
				runTimer.stop();
			}
		}
	}

	private void copyAssemblies(String assemblyName) throws ModuleException
	{
		logger.debug("Restoring type information for assembly '" + assemblyName + "'");

		// collect and iterate over all objects from previous compilation runs

		int typecount = 0;
		for (Object obj : incre.history.getDataStore().getObjects())
		{
			// Only restore PrimitiveConcerns and CpsConcerns
			if (obj instanceof PrimitiveConcern || obj instanceof CpsConcern)
			{
				Concern c = (Concern) obj;

				// Get the .NET platform representation for this concern
				DotNETType t = (DotNETType) c.getPlatformRepresentation();

				// Add to datastore if type belongs to the assembly we are
				// restoring
				if (t != null && assemblyName.equals(t.assemblyName()))
				{
					// Register the type with LAMA
					t.setParentConcern(null);
					Composestar.Core.LAMA.UnitRegister.instance().registerLanguageUnit(t);

					// Add the type to the TypeMap
					TypeMap.instance().addType(t.getFullName(), t);
					typecount++;
				}
			}
		}

		logger.debug(typecount + " types restored");
	}

	private void collectAssembly(AssemblyConfig assembly) throws ModuleException
	{
		String filename = assembly.getTypeSpecificationFile();

		logger.debug("Loading type information from '" + filename + "'...");

		InputStream is = null;
		try
		{
			INCRETimer deserializeTimer = incre.getReporter().openProcess(MODULE_NAME, "XML deserialize",
					INCRETimer.TYPE_NORMAL);

			is = new FileInputStream(filename);
			if (filename.endsWith(".gz"))
			{
				is = new GZIPInputStream(is);
			}

			AssemblyDocument doc = AssemblyDocument.Factory.parse(is);

			deserializeTimer.stop();

			collectTypes(doc.getAssembly());
		}
		catch (XmlException e)
		{
			throw new ModuleException(
					"CollectorRunner: XmlException while parsing " + filename + ": " + e.getMessage(), MODULE_NAME);
		}
		catch (IOException e)
		{
			throw new ModuleException("CollectorRunner: IOException while parsing " + filename + ": " + e.getMessage(),
					MODULE_NAME);
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

		Set<Annotation> unresolvedAttributeTypes = new HashSet<Annotation>();

		Map<String, DotNETType> typeMap = TypeMap.instance().map();
		for (DotNETType type : typeMap.values())
		{
			// Collect type attributes
			List<Annotation> typeAnnos = type.getAnnotations();
			if (typeAnnos != null)
			{
				unresolvedAttributeTypes.addAll(typeAnnos);
			}

			// Collect field attributes
			List<DotNETFieldInfo> fields = type.getFields();
			for (DotNETFieldInfo field : fields)
			{
				List<Annotation> fieldAnnos = field.getAnnotations();
				if (fieldAnnos != null)
				{
					unresolvedAttributeTypes.addAll(fieldAnnos);
				}
			}

			// Collect method attributes
			List<DotNETMethodInfo> methods = type.getMethods();
			for (DotNETMethodInfo method : methods)
			{
				List<Annotation> methodAnnos = method.getAnnotations();
				if (methodAnnos != null)
				{
					unresolvedAttributeTypes.addAll(methodAnnos);
				}
			}

			// Add type to repository as primitive concern
			PrimitiveConcern pc = new PrimitiveConcern();
			pc.setName(type.getFullName());
			pc.setPlatformRepresentation(type);
			type.setParentConcern(pc);
			DataStore.instance().addObject(type.getFullName(), pc);

			// logger.debug("Adding primitive concern '" + pc.getName() + "'");
		}

		long elapsed = System.currentTimeMillis() - starttime;
		logger.debug(typeMap.size() + " primitive concerns added in " + elapsed + " ms.");

		// Resolve attribute types
		resolveAttributeTypes(unresolvedAttributeTypes);
	}

	private void resolveAttributeTypes(Set<Annotation> unresolvedAttributeTypes)
	{
		logger.debug("Resolving " + unresolvedAttributeTypes.size() + " attribute types...");
		long starttime = System.currentTimeMillis();

		Map<String, DotNETType> typeMap = TypeMap.instance().map();
		Map<String, DotNETType> newAttributeTypes = new HashMap<String, DotNETType>();

		for (Annotation annotation : unresolvedAttributeTypes)
		{
			if (typeMap.containsKey(annotation.getTypeName()))
			{
				// Attribute type has been resolved by the analyzer
				annotation.setType(typeMap.get(annotation.getTypeName()));
			}
			else if (newAttributeTypes.containsKey(annotation.getTypeName()))
			{
				// Attribute type has been encountered before, use previously
				// created type
				annotation.setType(newAttributeTypes.get(annotation.getTypeName()));
			}
			else
			{
				// Create a new DotNETType element
				DotNETType attributeType = new DotNETType();
				attributeType.setFullName(annotation.getTypeName());

				// Add this attribute type to the repository as a primitive
				// concern
				PrimitiveConcern pc_attribute = new PrimitiveConcern();
				pc_attribute.setName(attributeType.getFullName());
				pc_attribute.setPlatformRepresentation(attributeType);
				attributeType.setParentConcern(pc_attribute);
				DataStore.instance().addObject(attributeType.getFullName(), pc_attribute);

				// Add this attribute type to the list of added types
				newAttributeTypes.put(attributeType.getFullName(), attributeType);
			}
		}

		long elapsed = System.currentTimeMillis() - starttime;
		logger.debug("Attribute types resolved in " + elapsed + " ms.");
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

			// Add the DotNETType to the TypeMap
			TypeMap.instance().addType(dnt.getFullName(), dnt);
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
			throw new ModuleException("Type must have a name attribute", MODULE_NAME);
		}

		// see rev. 2806
		return (ns.endsWith("+") ? (ns + name) : (ns + "." + name));
	}

	private List<DotNETAttribute> collectAttributes(ArrayOfAttributeElement attributes)
	{
		if (attributes == null)
		{
			return Collections.EMPTY_LIST;
		}

		List<DotNETAttribute> result = new ArrayList<DotNETAttribute>();
		for (AttributeElement ae : attributes.getAttributeList())
		{
			DotNETAttribute attribute = new DotNETAttribute();
			attribute.setTypeName(ae.getAttributeType());

			// Set value for this attribute
			if (ae.getValues().sizeOfValueArray() >= 1)
			{
				attribute.setValue(ae.getValues().getValueArray(0).getValue());
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
				throw new ModuleException("Method must have a name attribute", MODULE_NAME);
			}

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
				throw new ModuleException("ParameterInfo must have a name attribute.", MODULE_NAME);
			}

			DotNETParameterInfo parameter = new DotNETParameterInfo();
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
