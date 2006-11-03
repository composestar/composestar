package Composestar.DotNET.TYM.RepositoryCollector;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.xmlbeans.XmlException;

import Composestar.Core.CpsProgramRepository.PrimitiveConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.CompiledImplementation;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.Source;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.SourceFile;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.INCRETimer;
import Composestar.Core.LAMA.CallToOtherMethod;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.TypeMap;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.TYM.TypeCollector.CollectorRunner;
import Composestar.DotNET.LAMA.DotNETCallToOtherMethod;
import Composestar.DotNET.LAMA.DotNETFieldInfo;
import Composestar.DotNET.LAMA.DotNETMethodInfo;
import Composestar.DotNET.LAMA.DotNETParameterInfo;
import Composestar.DotNET.LAMA.DotNETType;
import Composestar.DotNET.MASTER.StarLightMaster;
import Composestar.Utils.Debug;

import composestar.dotNET.tym.entities.ArrayOfAssemblyConfig;
import composestar.dotNET.tym.entities.ArrayOfCallElement;
import composestar.dotNET.tym.entities.ArrayOfFieldElement;
import composestar.dotNET.tym.entities.ArrayOfFilterActionElement;
import composestar.dotNET.tym.entities.ArrayOfFilterTypeElement;
import composestar.dotNET.tym.entities.ArrayOfMethodElement;
import composestar.dotNET.tym.entities.ArrayOfParameterElement;
import composestar.dotNET.tym.entities.ArrayOfTypeElement;
import composestar.dotNET.tym.entities.AssemblyDocument;
import composestar.dotNET.tym.entities.AssemblyElement;
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
	private Vector callsToOtherMethods = new Vector();

	// private HashMap fieldMap = new HashMap();
	private HashMap methodMap = new HashMap();

	private HashMap parameterMap = new HashMap();

	private HashMap callsMap = new HashMap();

	private INCRETimer deserializeTimer = INCRE.instance().getReporter().openProcess("COLLECTOR", "xml deserialize",
			INCRETimer.TYPE_NORMAL);

	private INCRETimer storeTimer = INCRE.instance().getReporter().openProcess("COLLECTOR", "storing in repository",
			INCRETimer.TYPE_NORMAL);

	private INCRETimer timer1 = INCRE.instance().getReporter().openProcess("COLLECTOR", "timer1",
			INCRETimer.TYPE_NORMAL);

	private INCRETimer timer2 = INCRE.instance().getReporter().openProcess("COLLECTOR", "timer2",
			INCRETimer.TYPE_NORMAL);

	private INCRETimer timer3 = INCRE.instance().getReporter().openProcess("COLLECTOR", "timer3",
			INCRETimer.TYPE_NORMAL);

	private INCRETimer timer4 = INCRE.instance().getReporter().openProcess("COLLECTOR", "timer4",
			INCRETimer.TYPE_NORMAL);

	public void run(CommonResources resources) throws ModuleException
	{
		long starttime = 0;

		if (Debug.getMode() == Debug.MODE_DEBUG) starttime = System.currentTimeMillis();

		ConfigurationContainer configContainer = StarLightMaster.getConfigContainer();

		Debug.out(Debug.MODE_DEBUG, "COLLECTOR", "Repository initialized in "
				+ (System.currentTimeMillis() - starttime) + " ms.");

		// Construct field hashmap
		// if (Debug.getMode() == Debug.MODE_DEBUG) starttime =
		// System.currentTimeMillis();
		// List allfields = repository.getFieldElements();
		// ListIterator fieldIterator = allfields.listIterator();
		// while (fieldIterator.hasNext())
		// {
		// FieldElement fe = (FieldElement)fieldIterator.next();
		//			
		// List fieldElements = null;
		// if (fieldMap.containsKey(fe.get_ParentTypeId()))
		// {
		// fieldElements = (List)fieldMap.get(fe.get_ParentTypeId());
		// }
		// else {
		// fieldElements = new ArrayList();
		// }
		// fieldElements.add(fe);
		//			
		// fieldMap.put(fe.get_ParentTypeId(), fieldElements);
		// }
		// Debug.out(Debug.MODE_DEBUG, "COLLECTOR", allfields.size() + " fields
		// read from database and cached in " + (System.currentTimeMillis() -
		// starttime) + " ms.");

		// Construct method hashmap
		// if (Debug.getMode() == Debug.MODE_DEBUG) starttime =
		// System.currentTimeMillis();
		// List allmethods = repository.getMethodElements();
		// ListIterator methodIterator = allmethods.listIterator();
		// while (methodIterator.hasNext())
		// {
		// MethodElement me = (MethodElement)methodIterator.next();
		//			
		// List methodElements = null;
		// if (methodMap.containsKey(me.get_ParentTypeId()))
		// {
		// methodElements = (List)methodMap.get(me.get_ParentTypeId());
		// }
		// else {
		// methodElements = new ArrayList();
		// }
		// methodElements.add(me);
		//			
		// methodMap.put(me.get_ParentTypeId(), methodElements);
		// }
		// Debug.out(Debug.MODE_DEBUG, "COLLECTOR", allmethods.size() + "
		// methods read from database and cached in " +
		// (System.currentTimeMillis() - starttime) + " ms.");

		// Construct parameter hashmap
		// if (Debug.getMode() == Debug.MODE_DEBUG) starttime =
		// System.currentTimeMillis();
		// List allparameters = repository.getParameterElements();
		// ListIterator parameterIterator = allparameters.listIterator();
		// while (parameterIterator.hasNext())
		// {
		// ParameterElement pe = (ParameterElement)parameterIterator.next();
		//			
		// List parameterElements = null;
		// if (parameterMap.containsKey(pe.get_ParentMethodId()))
		// {
		// parameterElements = (List)parameterMap.get(pe.get_ParentMethodId());
		// }
		// else {
		// parameterElements = new ArrayList();
		// }
		// parameterElements.add(pe);
		//			
		// parameterMap.put(pe.get_ParentMethodId(), parameterElements);
		// }
		// Debug.out(Debug.MODE_DEBUG, "COLLECTOR", allparameters.size() + "
		// parameters read from database and cached in " +
		// (System.currentTimeMillis() - starttime) + " ms.");

		// Construct call hashmap
		// if (Debug.getMode() == Debug.MODE_DEBUG) starttime =
		// System.currentTimeMillis();
		// List allcalls = repository.getCallElements();
		// ListIterator callIterator = allcalls.listIterator();
		// while (callIterator.hasNext())
		// {
		// CallElement ce = (CallElement)callIterator.next();
		//			
		// List callElements = null;
		// if (callsMap.containsKey(ce.get_ParentMethodBodyId()))
		// {
		// callElements = (List)callsMap.get(ce.get_ParentMethodBodyId());
		// }
		// else {
		// callElements = new ArrayList();
		// }
		// callElements.add(ce);
		//			
		// callsMap.put(ce.get_ParentMethodBodyId(), callElements);
		// }
		// Debug.out(Debug.MODE_DEBUG, "COLLECTOR", allcalls.size() + " calls
		// read from database and cached in " + (System.currentTimeMillis() -
		// starttime) + " ms.");

		// Collect all filtertypes and filteractions:
		collectFilterTypesAndActions(configContainer);

		// Collect all types from the persistent repository
		ArrayOfAssemblyConfig assemblies = configContainer.getAssemblies();
		for (int i = 0; i < assemblies.sizeOfAssemblyConfigArray(); i++)
		{
			AssemblyDocument doc;
			try
			{
				String name = assemblies.getAssemblyConfigArray(i).getSerializedFilename();
				deserializeTimer.start();
				doc = AssemblyDocument.Factory.parse(new File(name));
				deserializeTimer.stop();
			}
			catch (XmlException e)
			{
				throw new ModuleException("CollectorRunner: XmlException while parsing "
						+ assemblies.getAssemblyConfigArray(i).getSerializedFilename(), "TYM");
			}
			catch (IOException e)
			{
				throw new ModuleException("CollectorRunner: IOException while parsing "
						+ assemblies.getAssemblyConfigArray(i).getFilename(), "TYM");
			}
			collectTypes(doc.getAssembly());
		}

		Debug.out(Debug.MODE_DEBUG, "COLLECTOR", "Processing cps concerns...");
		if (Debug.getMode() == Debug.MODE_DEBUG) starttime = System.currentTimeMillis();
		int count = 0;
		DataStore dataStore = DataStore.instance();
		HashMap typeMap = TypeMap.instance().map();
		/*
		 * TODO : The only types of embedded code are the imported dll's like in
		 * the VenusFlytrap Therefore: 1) embedded code that it fully
		 * programmed, like the Sound concern of Pacman must be ignored in this
		 * part of code; 2) embedded code from dll's do need to pass this part
		 * of code (Like VenusFlyTrap)
		 */
		// loop through all current concerns, fetch implementation and remove
		// from types map.
		Iterator repIt = dataStore.getIterator();
		while (repIt.hasNext())
		{
			Object next = repIt.next();
			if (next instanceof CpsConcern)
			{
				CpsConcern concern = (CpsConcern) next;
				Debug.out(Debug.MODE_DEBUG, "TYM", "Processing concern '" + concern.name + "'");
				// fetch implementation name
				Object impl = concern.getImplementation();
				String className = "";
				if (impl == null)
				{
					continue;
				}
				else if (impl instanceof Source)
				{
					// fixes the problem with the embedded code not being in the
					// type map at all.
					continue;
					// Source source = (Source)impl;
					// className = source.getClassName();
				}
				else if (impl instanceof SourceFile)
				{
					// TO DO: remove this?
					SourceFile source = (SourceFile) impl;
					String sourceFile = source.getSourceFile();
					className = sourceFile.replaceAll("\\.\\w+", "");
				}
				else if (impl instanceof CompiledImplementation)
				{
					className = ((CompiledImplementation) impl).getClassName();
				}
				else
				{
					throw new ModuleException(
							"CollectorRunner: Can only handle concerns with source file implementations or direct class links.",
							"TYM");
				}

				// transform source name into assembly name blaat.java -->
				// blaat.dll
				if (!typeMap.containsKey(className))
				{
					throw new ModuleException("Implementation: " + className + " for concern: " + concern.getName()
							+ " not found!", "TYM");

				}
				Debug.out(Debug.MODE_DEBUG, "TYM", "Processing type " + className);
				DotNETType type = (DotNETType) typeMap.get(className);
				concern.setPlatformRepresentation(type);
				type.setParentConcern(concern);
				typeMap.remove(className);
				count++;
			}
		}
		Debug.out(Debug.MODE_DEBUG, "COLLECTOR", count + " cps concerns added in "
				+ (System.currentTimeMillis() - starttime) + " ms.");

		// loop through rest of the concerns and add to the repository in the
		// form of primitive concerns
		Debug.out(Debug.MODE_DEBUG, "COLLECTOR", "Processing primitive concerns...");
		if (Debug.getMode() == Debug.MODE_DEBUG) starttime = System.currentTimeMillis();
		Iterator it = typeMap.values().iterator();
		while (it.hasNext())
		{
			DotNETType type = (DotNETType) it.next();
			PrimitiveConcern pc = new PrimitiveConcern();
			pc.setName(type.fullName());
			pc.setPlatformRepresentation(type);
			type.setParentConcern(pc);
			dataStore.addObject(type.fullName(), pc);
			// Debug.out(Debug.MODE_DEBUG,"TYM","Adding primitive concern
			// '"+type.fullName()+"'");
		}
		Debug.out(Debug.MODE_DEBUG, "COLLECTOR", typeMap.size() + " primitive concerns added in "
				+ (System.currentTimeMillis() - starttime) + " ms.");

		// resolve the MethodInfo reference in the calls within a method:
		Debug.out(Debug.MODE_DEBUG, "COLLECTOR", "Resolving method references for calls withing a method...");
		if (Debug.getMode() == Debug.MODE_DEBUG) starttime = System.currentTimeMillis();
		resolveCallsToOtherMethods();
		Debug.out(Debug.MODE_DEBUG, "COLLECTOR", "Method references resolved in "
				+ (System.currentTimeMillis() - starttime) + " ms.");

	}

	private void collectFilterTypesAndActions(ConfigurationContainer config) throws ModuleException
	{
		long starttime = System.currentTimeMillis();

		// create mapping from strings to filteractions, to use later to resolve
		// the actions in
		// a filtertype
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
						+ "' not found for FilterType '" + storedType.getName() + "'.", "TYM");
			}
			filterType.setAcceptCallAction(acceptCallAction);

			// get rejectCallAction:
			FilterAction rejectCallAction = (FilterAction) actionMapping.get(storedType.getRejectCallAction());
			if (rejectCallAction == null)
			{
				throw new ModuleException("RejectCallAction '" + storedType.getRejectCallAction()
						+ "' not found for FilterType '" + storedType.getName() + "'.", "TYM");
			}
			filterType.setRejectCallAction(rejectCallAction);

			// get acceptReturnAction:
			FilterAction acceptReturnAction = (FilterAction) actionMapping.get(storedType.getAcceptReturnAction());
			if (acceptReturnAction == null)
			{
				throw new ModuleException("AcceptReturnAction '" + storedType.getAcceptReturnAction()
						+ "' not found for FilterType '" + storedType.getName() + "'.", "TYM");
			}
			filterType.setAcceptReturnAction(acceptReturnAction);

			// get rejectReturnAction:
			FilterAction rejectReturnAction = (FilterAction) actionMapping.get(storedType.getRejectReturnAction());
			if (rejectReturnAction == null)
			{
				throw new ModuleException("RejectReturnAction '" + storedType.getRejectReturnAction()
						+ "' not found for FilterType '" + storedType.getName() + "'.", "TYM");
			}
			filterType.setRejectReturnAction(rejectReturnAction);
		}

		Debug.out(Debug.MODE_DEBUG, "COLLECTOR", storedFilters.sizeOfFilterTypeArray() + " filters with "
				+ actionMapping.size() + " filter actions read from database in "
				+ (System.currentTimeMillis() - starttime) + " ms.");
	}

	private void collectTypes(AssemblyElement assembly) throws ModuleException
	{
		long starttime = System.currentTimeMillis();

		ArrayOfTypeElement types = assembly.getTypes();

		// Get all types from repository
		Debug.out(Debug.MODE_DEBUG, "COLLECTOR", types.sizeOfTypeArray() + " types read from database in "
				+ (System.currentTimeMillis() - starttime) + " ms.");

		// Process all types, i.e. map them to LAMA
		Debug.out(Debug.MODE_DEBUG, "COLLECTOR", "Generating language model with " + types.sizeOfTypeArray()
				+ " types...");
		starttime = System.currentTimeMillis();
		for (int i = 0; i < types.sizeOfTypeArray(); i++)
		{
			TypeElement typeElement = types.getTypeArray(i);

			// Debug.out(Debug.MODE_DEBUG,"TYM","Retrieving type
			// '"+storedType.get_FullName()+"'");

			// storeTimer.start();
			DotNETType type = new DotNETType();
			// storeTimer.stop();
			type.setTypeElement(typeElement);
			type.setName(typeElement.getName());

			if (typeElement.getNamespace() != null && typeElement.getName() != null)
			{
				type.setFullName(typeElement.getNamespace() + '.' + typeElement.getName());
			}
			else
			{
				throw new ModuleException("Type must have a name attribute", "TYM");
			}

			
			type.setBaseType(typeElement.getBaseType());
			//type.addImplementedInterface( )
			// TODO: type.addImplementedInterface( lastCharData );
			type.setIsAbstract(typeElement.getIsAbstract());
			// --type.setIsAnsiClass( Boolean.valueOf( lastCharData
			// ).booleanValue() );
			//type.setIsArray( typeElement..get_IsArray() );
			// --type.setIsAutoClass( Boolean.valueOf( lastCharData
			// ).booleanValue() );
			// --type.setIsAutoLayout( Boolean.valueOf( lastCharData
			// ).booleanValue() );
			// --type.setIsByRef( Boolean.valueOf( lastCharData ).booleanValue()
			// );
			type.setIsClass(typeElement.getIsClass());
			// --type.setIsContextful( Boolean.valueOf( lastCharData
			// ).booleanValue() );
			type.setIsEnum(typeElement.getIsEnum());
			// --type.setIsImport( Boolean.valueOf( lastCharData
			// ).booleanValue() );
			type.setIsInterface(typeElement.getIsInterface());
			// --type.setIsMarshalByRef( Boolean.valueOf( lastCharData
			// ).booleanValue() );
			// TODO: Missing Type.setIsNestedFamAndAssem( Boolean.valueOf(
			// LastCharData ).booleanValue() );
			// --type.setIsNestedAssembly( Boolean.valueOf( lastCharData
			// ).booleanValue() );
			// --type.setIsNestedFamOrAssem( Boolean.valueOf( lastCharData
			// ).booleanValue() );
			// --type.setIsNestedPrivate( Boolean.valueOf( lastCharData
			// ).booleanValue() );
			// --type.setIsNestedPublic( Boolean.valueOf( lastCharData
			// ).booleanValue() );
			type.setIsNotPublic(typeElement.getIsNotPublic());
			// --type.setIsPointer( Boolean.valueOf( lastCharData
			// ).booleanValue() );
			type.setIsPrimitive(typeElement.getIsPrimitive());
			type.setIsPublic(typeElement.getIsPublic());
			type.setIsSealed(typeElement.getIsSealed());
			type.setIsSerializable(typeElement.getIsSerializable());
			type.setIsValueType(typeElement.getIsValueType());

			// TODO: Name
			// TODO: create system in DotNETModule to avoid duplicates
			// DotNETModule mod = new DotNETModule();
			// mod.setFullyQualifiedName( lastCharData );
			// type.setModule( mod );

			type.setNamespace(typeElement.getNamespace());
			// --type.setunderlyingSystemType( lastCharData );
			// --type.setHashCode( Integer.parseInt( lastCharData ) );
			type.setFromDLL(assembly.getName());

			collectFields(typeElement, type);
			storeTimer.start();
			collectMethods(typeElement, type);
			storeTimer.stop();

			// Add the DotNETType to the TypeMap
			// storeTimer.start();
			//Debug.out(Debug.MODE_DEBUG, "COLLECTOR", "Adding type: " + type.fullName());
			TypeMap.instance().addType(type.fullName(), type);
			// storeTimer.stop();
		}
		Debug.out(Debug.MODE_DEBUG, "COLLECTOR", "Language model generated in "
				+ (System.currentTimeMillis() - starttime) + " ms.");
	}

	private void collectFields(TypeElement storedType, DotNETType type) throws ModuleException
	{
		// Get all fields for the type 'storedType'
		ArrayOfFieldElement fields = storedType.getFields();

		// Process all fields
		for (int i = 0; i < fields.sizeOfFieldArray(); i++)
		{
			FieldElement storedField = fields.getFieldArray(i);

			// Debug.out(Debug.MODE_DEBUG,"TYM"," Retrieving field
			// '"+storedField.get_Name()+"'");

			DotNETFieldInfo field = new DotNETFieldInfo();

			field.setName(storedField.getName());
			field.setFieldType(storedField.getType());
			field.setIsPrivate(storedField.getIsPrivate());
			field.setIsPublic(storedField.getIsPublic());
			field.setIsStatic(storedField.getIsStatic());

			type.addField(field);
		}
	}

	private void collectMethods(TypeElement storedType, DotNETType type) throws ModuleException
	{

		// Get all methods for the type 'storedType'
		ArrayOfMethodElement methods = storedType.getMethods();

		// Process all methods
		for (int i = 0; i < methods.sizeOfMethodArray(); i++)
		{
			MethodElement storedMethod = methods.getMethodArray(i);

			// Debug.out(Debug.MODE_DEBUG,"TYM"," Retrieving method
			// '"+storedMethod.get_Signature()+"'");

			timer1.start();
			DotNETMethodInfo method = new DotNETMethodInfo();
			method.setMethodElement(storedMethod);
			timer1.stop();
			timer2.start();
			if (storedMethod.getName() != null)
			{
				method.setName(storedMethod.getName());
			}
			else
			{
				throw new ModuleException("MethodInfo must have a name attribute", "TYM");
			}
			timer2.stop();
			timer3.start();
			// --methodInfo.setCallingConvention( Integer.parseInt( lastCharData
			// ) );
			method.setIsAbstract(storedMethod.getIsAbstract());
			// --methodInfo.setIsAssembly( Boolean.valueOf( lastCharData
			// ).booleanValue() );
			method.setIsConstructor(storedMethod.getIsConstructor());
			// --methodInfo.setIsFamily( Boolean.valueOf( lastCharData
			// ).booleanValue() );
			// --methodInfo.setIsFamilyAndAssembly( Boolean.valueOf(
			// lastCharData ).booleanValue() );
			// --methodInfo.setIsFamilyOrAssembly( Boolean.valueOf( lastCharData
			// ).booleanValue() );
			// --methodInfo.setIsFinal( Boolean.valueOf( lastCharData
			// ).booleanValue() );
			// --methodInfo.setIsHideBySig( Boolean.valueOf( lastCharData
			// ).booleanValue() );
			method.setIsPrivate(storedMethod.getIsPrivate());
			method.setIsPublic(storedMethod.getIsPublic());
			method.setIsStatic(storedMethod.getIsStatic());
			method.setIsVirtual(storedMethod.getIsVirtual());
			// --methodInfo.setHashCode( Integer.parseInt( lastCharData ) );
			method.setReturnType(storedMethod.getReturnType());
			// -- methodInfo.setIsDeclaredHere( Boolean.valueOf( lastCharData
			// ).booleanValue() );
			// - Ignored: MethodAttrributes
			timer3.stop();
			timer4.start();

			collectParameters(storedMethod, method);
			// if (storedMethod.isSetBody())
			// collectMethodBody(storedMethod.getBody(), method);

			type.addMethod(method);
			timer4.stop();
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

			// Debug.out(Debug.MODE_DEBUG,"TYM"," Retrieving parameter
			// '"+storedParameter.get_Name()+"'
			// ("+storedParameter.get_ParameterType()+")");

			// storeTimer.start();
			DotNETParameterInfo parameter = new DotNETParameterInfo();
			// storeTimer.stop();

			if (storedParameter.getName() != null)
			{
				parameter.setName(storedParameter.getName());
			}
			else
			{
				throw new ModuleException("ParameterInfo must have a name attribute.", "TYM");
			}

			parameter.setPosition(storedParameter.getOrdinal());
			parameter.setParameterType(storedParameter.getType());

			/*
			 * TODO parameter.setIsln(storedParameter.getIsIn());
			 * parameter.setIsOptional(storedParameter.getIsOptional());
			 * parameter.setIsOut(storedParameter.getIsOut());
			 * parameter.setIsRetVal(storedParameter.getIsRetVal());
			 */

			// --ParamInfo.setIsLcid( Boolean.valueOf( LastCharData
			// ).booleanValue() );
			// --ParamInfo.setHashCode( Integer.parseInt( LastCharData ) );
			parameters[parameter.position() - 1] = parameter;
		}

		for (int i = 0; i < parameters.length; i++)
		{
			// storeTimer.start();
			method.addParameter(parameters[i]);
			// storeTimer.stop();
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

			// TODO: this mapping correct ?
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
		CallToOtherMethod call;

		Enumeration calls = callsToOtherMethods.elements();
		while (calls.hasMoreElements())
		{
			call = (CallToOtherMethod) calls.nextElement();

			call.setCalledMethod(getMethodInfo(call));
		}
	}

	private MethodInfo getMethodInfo(CallToOtherMethod call)
	{
		String operation = call.getOperationName();

		// separate returntype part:
		int pos1 = operation.indexOf(' ');
		String returnType = operation.substring(0, pos1);

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

		// get Methodinfo:
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
