package Composestar.DotNET.TYM.RepositoryCollector;

import org.xml.sax.SAXNotRecognizedException;
import java.util.*;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.*;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.TYM.TypeCollector.CollectorRunner;
import Composestar.DotNET.LAMA.*;
import Composestar.Repository.*;
import Composestar.Repository.LanguageModel.*;

public class StarLightCollectorRunner implements CollectorRunner 
{

	public void run(CommonResources resources) throws ModuleException 
	{
		// Collect all types from the persistent repository
		collectTypes();
	}
	
	private void collectTypes() throws ModuleException
	{
		// Get all types from repository
		ArrayList storedTypes = DataStoreContainer.getInstance().getTypes();
		
		// Process all types, i.e. map them to LAMA
		Iterator typeIterator = storedTypes.iterator();
		while (typeIterator.hasNext())
		{
			TypeElement storedType = (TypeElement)typeIterator.next();
			
			DotNETType type = new DotNETType();
			
			if (storedType.get_FullName() != null) {
	            type.setName( storedType.get_FullName() );
	        } 
			else {
	            throw new ModuleException( "Type must have a name attribute", "TYM" );
	        }
			
			type.setAssemblyQualifedName( storedType.get_AssemblyElement().get_Name() );
			type.setBaseType( storedType.get_BaseType() );
			//type.addImplementedInterface( lastCharData );
			type.setIsAbstract( storedType.get_IsAbstract() );
			//type.setIsAnsiClass( Boolean.valueOf( lastCharData ).booleanValue() );
			//type.setIsArray( Boolean.valueOf( lastCharData ).booleanValue() );
			//type.setIsAutoClass( Boolean.valueOf( lastCharData ).booleanValue() );
			//type.setIsAutoLayout( Boolean.valueOf( lastCharData ).booleanValue() );
			//type.setIsByRef( Boolean.valueOf( lastCharData ).booleanValue() );
			//type.setIsClass( Boolean.valueOf( lastCharData ).booleanValue() );
			//type.setIsContextful( Boolean.valueOf( lastCharData ).booleanValue() );
			type.setIsEnum( storedType.get_IsEnum() );
			//type.setIsImport( Boolean.valueOf( lastCharData ).booleanValue() );
			type.setIsInterface( storedType.get_IsInterface() ); 
			//type.setIsMarshalByRef( Boolean.valueOf( lastCharData ).booleanValue() );
            //  TODO: Missing    Type.setIsNestedFamAndAssem( Boolean.valueOf( LastCharData ).booleanValue() );
//			type.setIsNestedAssembly( Boolean.valueOf( lastCharData ).booleanValue() );
//			type.setIsNestedFamOrAssem( Boolean.valueOf( lastCharData ).booleanValue() );
//			type.setIsNestedPrivate( Boolean.valueOf( lastCharData ).booleanValue() );
//			type.setIsNestedPublic( Boolean.valueOf( lastCharData ).booleanValue() );
//			type.setIsNotPublic( Boolean.valueOf( lastCharData ).booleanValue() );
//			type.setIsPointer( Boolean.valueOf( lastCharData ).booleanValue() );
//			type.setIsPrimitive( Boolean.valueOf( lastCharData ).booleanValue() );			
//		    type.setIsPublic( Boolean.valueOf( lastCharData ).booleanValue() );
		    type.setIsSealed( storedType.get_IsSealed() );
//		    type.setIsSerializable( Boolean.valueOf( lastCharData ).booleanValue() );
		    type.setIsValueType( storedType.get_IsValueType() );

            // TODO: Name
            // TODO: create system in DotNETModule to avoid duplicates
//            DotNETModule mod = new DotNETModule();
//            mod.setFullyQualifiedName( lastCharData );
//            type.setModule( mod );
           
/*			type.setNamespace( lastCharData );
			type.setunderlyingSystemType( lastCharData );
			type.setHashCode( Integer.parseInt( lastCharData ) );
			type.fromDLL = lastCharData.replaceAll("\"","");
*/			
			
			collectMethods(storedType, type);
	
			// Add the DotNETType to the TypeMap
			TypeMap.instance().addType(storedType.get_FullName(), type);
		}
		
	}
	
	private void collectMethods(TypeElement storedType, DotNETType type) throws ModuleException
	{
		// Get all methods for the type 'storedtype'
		ArrayList storedMethods = DataStoreContainer.getInstance().getMethodElements(storedType);		

		// Process all methods
		Iterator methodIterator = storedMethods.iterator();
		while (methodIterator.hasNext())
		{
			MethodElement storedMethod = (MethodElement)methodIterator.next();
			
			DotNETMethodInfo method = new DotNETMethodInfo();
						
            if( storedMethod.get_Name() != null ) {
                method.setName( storedMethod.get_Name() );
            } else {
                throw new ModuleException( "MethodInfo must have a name attribute", "TYM" );
            }
            
            
//            methodInfo.setCallingConvention( Integer.parseInt( lastCharData ) );
//            methodInfo.setIsAbstract( Boolean.valueOf( lastCharData ).booleanValue() );
//            methodInfo.setIsAssembly( Boolean.valueOf( lastCharData ).booleanValue() );
//            methodInfo.setIsConstructor( Boolean.valueOf( lastCharData ).booleanValue() );
//            methodInfo.setIsFamily( Boolean.valueOf( lastCharData ).booleanValue() );
//            methodInfo.setIsFamilyAndAssembly( Boolean.valueOf( lastCharData ).booleanValue() );
//            methodInfo.setIsFamilyOrAssembly( Boolean.valueOf( lastCharData ).booleanValue() );
//            methodInfo.setIsFinal( Boolean.valueOf( lastCharData ).booleanValue() );
//            methodInfo.setIsHideBySig( Boolean.valueOf( lastCharData ).booleanValue() );
//            methodInfo.setIsprivate( Boolean.valueOf( lastCharData ).booleanValue() );
//            methodInfo.setIsPublic( Boolean.valueOf( lastCharData ).booleanValue() );
//            methodInfo.setIsStatic( Boolean.valueOf( lastCharData ).booleanValue() );
//            methodInfo.setIsVirtual( Boolean.valueOf( lastCharData ).booleanValue() );
//            methodInfo.setHashCode( Integer.parseInt( lastCharData ) );
              method.setReturnType( storedMethod.get_ReturnType() );
//          	methodInfo.setIsDeclaredHere( Boolean.valueOf( lastCharData ).booleanValue() );
//            Ignored: MethodAttrributes
          	
            collectParameters(storedMethod, method);
            collectMethodBody(storedMethod.get_MethodBody(), method);
            
			type.addMethod(method);
		}
	}
	
	private void collectParameters(MethodElement storedMethod, DotNETMethodInfo method) throws ModuleException
	{
		// Get all parameters for the method 'storedmethod'
		ArrayList storedParameters = DataStoreContainer.getInstance().getParameterElements(storedMethod);		

		// Process all parameters
		Iterator parameterIterator = storedParameters.iterator();
		while (parameterIterator.hasNext())
		{
			ParameterElement storedParameter = (ParameterElement)parameterIterator.next();
			
			DotNETParameterInfo parameter = new DotNETParameterInfo();
						
            if( storedParameter.get_Name() != null ) {
                parameter.setName( storedParameter.get_Name() );
            } else {
                throw new ModuleException( "ParameterInfo must have a name attribute", "TYM" );
            }
            
            parameter.setPosition( storedParameter.get_Ordinal() );
            parameter.setParameterType( storedParameter.get_ParameterType() );
//            ParamInfo.setIsln( Boolean.valueOf( LastCharData ).booleanValue() );
//            ParamInfo.setIsLcid( Boolean.valueOf( LastCharData ).booleanValue() );
//            ParamInfo.setIsOptional( Boolean.valueOf( LastCharData ).booleanValue() );
//            ParamInfo.setIsOut( Boolean.valueOf( LastCharData ).booleanValue() );
//            ParamInfo.setIsRetVal( Boolean.valueOf( LastCharData ).booleanValue() );
//            ParamInfo.setHashCode( Integer.parseInt( LastCharData ) );
            
            method.addParameter(parameter);
		}
         
	}
	
	private void collectMethodBody(MethodBody storedMethodBody, DotNETMethodInfo method)
	{
		// Get the call elements for this method body
		ArrayList storedCalls = DataStoreContainer.getInstance().getCallElements(storedMethodBody);		

		Iterator callIterator = storedCalls.iterator();
		while (callIterator.hasNext())
		{
			CallElement storedCall = (CallElement)callIterator.next();
			
			CallToOtherMethod call = new CallToOtherMethod();
			
			// TODO: this mapping correct ?
			call.OperationName = storedCall.get_MethodReference();
			
			
			method.getCallsToOtherMethods().add(call);
		}
	}
	
}
