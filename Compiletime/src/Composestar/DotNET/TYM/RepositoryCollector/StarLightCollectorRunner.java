package Composestar.DotNET.TYM.RepositoryCollector;

import org.xml.sax.SAXNotRecognizedException;
import java.util.*;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.TypeMap;
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
			
			// FIXME: MORE FIELDS
			
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
            
//          FIXME: MORE FIELDS
            
            // collectParameters();
            // collectMethodBody();
            
			type.addMethod(method);
		}
	}
	
}
