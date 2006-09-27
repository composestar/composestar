package Composestar.DotNET.TYM.RepositoryCollector;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.PrimitiveConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.CompiledImplementation;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.Source;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.SourceFile;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.CallToOtherMethod;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.TypeMap;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.TYM.TypeCollector.CollectorRunner;
import Composestar.DotNET.LAMA.DotNETFieldInfo;
import Composestar.DotNET.LAMA.DotNETMethodInfo;
import Composestar.DotNET.LAMA.DotNETParameterInfo;
import Composestar.DotNET.LAMA.DotNETType;
import Composestar.Repository.RepositoryAccess;
import Composestar.Repository.LanguageModel.CallElement;
import Composestar.Repository.LanguageModel.FieldElement;
import Composestar.Repository.LanguageModel.MethodBody;
import Composestar.Repository.LanguageModel.MethodElement;
import Composestar.Repository.LanguageModel.ParameterElement;
import Composestar.Repository.LanguageModel.TypeElement;
import Composestar.Utils.Debug;

public class StarLightCollectorRunner implements CollectorRunner 
{
	private RepositoryAccess repository;
	private Vector callsToOtherMethods = new Vector();

	public void run(CommonResources resources) throws ModuleException 
	{
		repository = new RepositoryAccess();
		
		// Collect all types from the persistent repository
		collectTypes();
		
		
		
		
		
        int count = 0;
		DataStore dataStore = DataStore.instance();
        HashMap typeMap = TypeMap.instance().map();
        /* TODO : The only types of embedded code are the imported dll's like in the VenusFlytrap
         * Therefore:
         * 1) embedded code that it fully programmed, like the Sound concern of Pacman must be ignored in this part of code;
         * 2) embedded code from dll's do need to pass this part of code (Like VenusFlyTrap)
        */
        // loop through all current concerns, fetch implementation and remove from types map.
        Iterator repIt = dataStore.getIterator();
        while( repIt.hasNext() ) {
        	Object next = repIt.next();
        	if( next instanceof CpsConcern ) {
        		CpsConcern concern = (CpsConcern)next;
        		Debug.out(Debug.MODE_DEBUG,"TYM","Processing concern '"+concern.name+"'");
        		// fetch implementation name
        		Object impl = concern.getImplementation();
        		String className = "";
				if( impl == null ) { 
					continue; 
				}
				else if( impl instanceof Source )
				{ 
					//fixes the problem with the embedded code not being in the type map at all.
					continue; 
					//Source source = (Source)impl;
					//className = source.getClassName();
				}
				else if( impl instanceof SourceFile ) 
				{
					// TO DO: remove this?
					SourceFile source = (SourceFile)impl;
					String sourceFile = source.getSourceFile();
					className = sourceFile.replaceAll( "\\.\\w+", "" );
				}
				else if( impl instanceof CompiledImplementation )
				{
					className = ((CompiledImplementation)impl).getClassName();
				}
				else 
				{
					throw new ModuleException( "CollectorRunner: Can only handle concerns with source file implementations or direct class links.", "TYM" );
				}
        		
        		// transform source name into assembly name blaat.java --> blaat.dll
        		if( !typeMap.containsKey( className ) ) {
        			throw new ModuleException( "Implementation: " + className + " for concern: " + concern.getName() + " not found!", "TYM" );
        			
        		}
        		Debug.out(Debug.MODE_DEBUG,"TYM","Processing type "+className);
        		DotNETType type = (DotNETType)typeMap.get(className);
        		concern.setPlatformRepresentation( type );
        		type.setParentConcern(concern);
        		typeMap.remove( className );
				count++;
        	}
        }
        
        // loop through rest of the concerns and add to the repository in the form of primitive concerns
        Iterator it = typeMap.values().iterator();
        while( it.hasNext() ) {
			DotNETType type = (DotNETType)it.next();
			PrimitiveConcern pc = new PrimitiveConcern();
			pc.setName( type.fullName() );
			pc.setPlatformRepresentation( type );
			type.setParentConcern(pc);
			dataStore.addObject( type.fullName(), pc );
			Debug.out(Debug.MODE_DEBUG,"TYM","Adding primitive concern '"+type.fullName()+"'");
		}
        
        //resolve the MethodInfo reference in the calls within a method:
		resolveCallsToOtherMethods();
	}
	
	private void collectTypes() throws ModuleException
	{
		// Get all types from repository
		List storedTypes = repository.getTypeElements();
		
		// Process all types, i.e. map them to LAMA
		Iterator typeIterator = storedTypes.iterator();
		while (typeIterator.hasNext())
		{
			TypeElement storedType = (TypeElement)typeIterator.next();

			Debug.out(Debug.MODE_DEBUG,"TYM","Processing type '"+storedType.get_FullName()+"'");
			
			DotNETType type = new DotNETType();
			
			type.setName(storedType.get_Name());
			
			if (storedType.get_FullName() != null) {
	            type.setFullName( storedType.get_FullName() );
	        } 
			else {
	            throw new ModuleException( "Type must have a name attribute", "TYM" );
	        }
			
			//type.setAssemblyQualifedName( storedType.get_AssemblyElement().get_Name() );
			type.setBaseType( storedType.get_BaseType() );
			//TODO: type.addImplementedInterface( lastCharData );
			type.setIsAbstract( storedType.get_IsAbstract() );
			//--type.setIsAnsiClass( Boolean.valueOf( lastCharData ).booleanValue() );
			//--type.setIsArray( storedType.get_IsArray() );
			//--type.setIsAutoClass( Boolean.valueOf( lastCharData ).booleanValue() );
			//--type.setIsAutoLayout( Boolean.valueOf( lastCharData ).booleanValue() );
			//--type.setIsByRef( Boolean.valueOf( lastCharData ).booleanValue() );
			type.setIsClass( storedType.get_IsClass() );
			//--type.setIsContextful( Boolean.valueOf( lastCharData ).booleanValue() );
			type.setIsEnum( storedType.get_IsEnum() );
			//--type.setIsImport( Boolean.valueOf( lastCharData ).booleanValue() );
			type.setIsInterface( storedType.get_IsInterface() ); 
			//--type.setIsMarshalByRef( Boolean.valueOf( lastCharData ).booleanValue() );
            //  TODO: Missing    Type.setIsNestedFamAndAssem( Boolean.valueOf( LastCharData ).booleanValue() );
//			--type.setIsNestedAssembly( Boolean.valueOf( lastCharData ).booleanValue() );
//			--type.setIsNestedFamOrAssem( Boolean.valueOf( lastCharData ).booleanValue() );
//			--type.setIsNestedPrivate( Boolean.valueOf( lastCharData ).booleanValue() );
//			--type.setIsNestedPublic( Boolean.valueOf( lastCharData ).booleanValue() );
			type.setIsNotPublic( storedType.get_IsNotPublic() );
//			--type.setIsPointer( Boolean.valueOf( lastCharData ).booleanValue() );
			type.setIsPrimitive( storedType.get_IsPrimitive() );			
		    type.setIsPublic( storedType.get_IsPublic() );
		    type.setIsSealed( storedType.get_IsSealed() );
		    type.setIsSerializable( storedType.get_IsSerializable() );
		    type.setIsValueType( storedType.get_IsValueType() );

            // TODO: Name
            // TODO: create system in DotNETModule to avoid duplicates
//            DotNETModule mod = new DotNETModule();
//            mod.setFullyQualifiedName( lastCharData );
//            type.setModule( mod );
           
			type.setNamespace( storedType.get_Namespace() );
//			--type.setunderlyingSystemType( lastCharData );
//			--type.setHashCode( Integer.parseInt( lastCharData ) );
			type.fromDLL = storedType.get_FromDLL().replaceAll("\"","");
						
			collectFields(storedType, type);
			collectMethods(storedType, type);
	
			// Add the DotNETType to the TypeMap
			TypeMap.instance().addType(storedType.get_FullName(), type);
		}
	}
	
	private void collectFields(TypeElement storedType, DotNETType type) throws ModuleException
	{
		// Get all fields for the type 'storedType'
		List storedFields = repository.getFieldElements(storedType);
	
		// Process all fields
		Iterator fieldIterator = storedFields.iterator();
		while (fieldIterator.hasNext())
		{
			FieldElement storedField = (FieldElement)fieldIterator.next();
			
			DotNETFieldInfo field = new DotNETFieldInfo();
			
			field.setName(storedField.get_Name());
			field.setFieldType(storedField.get_Type());
			field.setIsPrivate(storedField.get_IsPrivate());
			field.setIsPublic(storedField.get_IsPublic());
			field.setIsStatic(storedField.get_IsStatic());
			
			type.addField(field);
		}
	}
	
	private void collectMethods(TypeElement storedType, DotNETType type) throws ModuleException
	{
		// Get all methods for the type 'storedType'
		List storedMethods = repository.getMethodElements(storedType);
			//DataStoreContainer.getInstance().getMethodElements(storedType);		

		// Process all methods
		Iterator methodIterator = storedMethods.iterator();
		while (methodIterator.hasNext())
		{
			MethodElement storedMethod = (MethodElement)methodIterator.next();

			Debug.out(Debug.MODE_DEBUG,"TYM","   Processing method '"+storedMethod.get_Signature()+"'");
			
			DotNETMethodInfo method = new DotNETMethodInfo();
						
            if( storedMethod.get_Name() != null ) {
                method.setName( storedMethod.get_Name() );
            } else {
                throw new ModuleException( "MethodInfo must have a name attribute", "TYM" );
            }
                        
//            --methodInfo.setCallingConvention( Integer.parseInt( lastCharData ) );
            method.setIsAbstract( storedMethod.get_IsAbstract() );
//            --methodInfo.setIsAssembly( Boolean.valueOf( lastCharData ).booleanValue() );
            method.setIsConstructor( storedMethod.get_IsConstructor() );
//            --methodInfo.setIsFamily( Boolean.valueOf( lastCharData ).booleanValue() );
//            --methodInfo.setIsFamilyAndAssembly( Boolean.valueOf( lastCharData ).booleanValue() );
//            --methodInfo.setIsFamilyOrAssembly( Boolean.valueOf( lastCharData ).booleanValue() );
//            --methodInfo.setIsFinal( Boolean.valueOf( lastCharData ).booleanValue() );
//            --methodInfo.setIsHideBySig( Boolean.valueOf( lastCharData ).booleanValue() );
            method.setIsprivate( storedMethod.get_IsPrivate() );
            method.setIsPublic( storedMethod.get_IsPublic() );
            method.setIsStatic( storedMethod.get_IsStatic() );
            method.setIsVirtual( storedMethod.get_IsVirtual() );
//            --methodInfo.setHashCode( Integer.parseInt( lastCharData ) );
            method.setReturnType( storedMethod.get_ReturnType() );
//          --	methodInfo.setIsDeclaredHere( Boolean.valueOf( lastCharData ).booleanValue() );
//           - Ignored: MethodAttrributes
          	
            collectParameters(storedMethod, method);
            
            if (storedMethod.get_HasMethodBody()) collectMethodBody(storedMethod.get_MethodBody(), method);
            
			type.addMethod(method);
		}
	}
	
	private void collectParameters(MethodElement storedMethod, DotNETMethodInfo method) throws ModuleException
	{
		// Get all parameters for the method 'storedmethod'
		List storedParameters = repository.getParameterElements(storedMethod);		

		// Process all parameters
		Iterator parameterIterator = storedParameters.iterator();
		while (parameterIterator.hasNext())
		{
			ParameterElement storedParameter = (ParameterElement)parameterIterator.next();
			
			Debug.out(Debug.MODE_DEBUG,"TYM","      Processing parameter '"+storedParameter.get_Name()+"' ("+storedParameter.get_ParameterType()+")");

			DotNETParameterInfo parameter = new DotNETParameterInfo();
						
            if( storedParameter.get_Name() != null ) {
                parameter.setName( storedParameter.get_Name() );
            } else {
                throw new ModuleException( "ParameterInfo must have a name attribute", "TYM" );
            }
            
            parameter.setPosition( storedParameter.get_Ordinal() );
            parameter.setParameterType( storedParameter.get_ParameterType() );
            parameter.setIsln( storedParameter.get_IsIn() );
            parameter.setIsOptional( storedParameter.get_IsOptional() );
            parameter.setIsOut( storedParameter.get_IsOut() );
            parameter.setIsRetVal( storedParameter.get_IsRetVal() );
//          --ParamInfo.setIsLcid( Boolean.valueOf( LastCharData ).booleanValue() );
//          --ParamInfo.setHashCode( Integer.parseInt( LastCharData ) );
            
            method.addParameter(parameter);
		}
         
	}
	
	private void collectMethodBody(MethodBody storedMethodBody, DotNETMethodInfo method)
	{
		// Get the call elements for this method body
		List storedCalls = repository.getCallElements(storedMethodBody);		

		Iterator callIterator = storedCalls.iterator();
		while (callIterator.hasNext())
		{
			CallElement storedCall = (CallElement)callIterator.next();
			
			CallToOtherMethod call = new CallToOtherMethod();
			
			// TODO: this mapping correct ?
			call.OperationName = storedCall.get_MethodReference();
			
			
			method.getCallsToOtherMethods().add(call);
			
			callsToOtherMethods.add( call );
		}
	}
	
	/**
	 * Resolve the MethodInfo of the called method for all calls to other methods.
	 *
	 */
	private void resolveCallsToOtherMethods(){
	    CallToOtherMethod call;

	    Enumeration calls = callsToOtherMethods.elements();
	    while( calls.hasMoreElements() ){
	        call = (CallToOtherMethod) calls.nextElement();


	        call.setCalledMethod( getMethodInfo( call ) );
	    }
	}

	private MethodInfo getMethodInfo( CallToOtherMethod call ){
	    String operation = call.getOperationName();

	    //separate returntype part:
	    int pos1 = operation.indexOf( ' ' );

	    //separate type:
	    int pos2 = operation.indexOf( ':' );
	    String typeName = operation.substring( pos1+1, pos2 );

	    //separate methodname:
	    int pos3 = operation.indexOf( '(' );
	    String methodName = operation.substring( pos2+2, pos3 );

	    //separate arguments:
	    int pos4 = operation.indexOf( ')' );
	    String arguments = operation.substring( pos3+1, pos4 );

	    StringTokenizer tokenizer = new StringTokenizer( arguments, "," );
	    int tokenCount = tokenizer.countTokens();
	    String[] argTypes = new String[ tokenCount ];
	    for (int i=0; i<tokenCount; i++){
	        argTypes[i] = tokenizer.nextToken();
	    }


	    //get Methodinfo:
	    Type type = TypeMap.instance().getType( typeName );
        MethodInfo methodInfo = null;
        if ( type != null ){
            methodInfo = type.getMethod( methodName, argTypes );
        }

	    return methodInfo;
	}
	
	
}
