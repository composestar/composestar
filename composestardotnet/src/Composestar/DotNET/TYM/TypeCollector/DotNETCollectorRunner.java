//Source file: C:\\local\\staijen\\composestar\\src\\Composestar\\CTAdaption\\TYM\\TypeCollector\\CollectorRunner.java

//Source file: H:\\sfcvs\\composestar\\src\\Composestar\\CTAdaption\\TYM\\TypeCollector\\CollectorRunner.java

package Composestar.DotNET.TYM.TypeCollector;

import Composestar.Core.TYM.TypeCollector.CollectorRunner;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.INCRETimer;
import Composestar.Core.RepositoryImplementation.DataStore;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import Composestar.Core.LAMA.*;
import Composestar.DotNET.LAMA.*;
import Composestar.DotNET.TYM.TypeCollector.DocumentHandler;
import Composestar.Utils.Debug;
import Composestar.Core.CpsProgramRepository.PrimitiveConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.*;
import Composestar.Core.Exception.ModuleException;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
 
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;

public class DotNETCollectorRunner implements CollectorRunner {
    
	private INCRE incre;

    /**
     * @roseuid 405700A80186
     */
    public DotNETCollectorRunner() {
		this.incre = INCRE.instance();
    }
    
    /**
     * The run function of each module is called in the same order as the  modules 
     * where added to the Master.
     * @param resources The resources objects contains the common resources available 
     * e.g the Repository.
     * @throws Composestar.core.Exception.ModuleException If a ModuleException is 
     * thrown the Master will stop its activity immediately.
     * @roseuid 4057006D0252
     */
    public void run(CommonResources resources) throws ModuleException {
    	
    	String tempFolder = Configuration.instance().pathSettings.getPath("Base"); 
    	try{
    		SAXParserFactory saxParserFactory =
            SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            XMLReader  parser  = saxParser.getXMLReader();
            DocumentHandler handler = new DocumentHandler( parser );
            parser.setContentHandler( handler );
            parser.parse( new InputSource( tempFolder + "types.xml" ));
        }catch( Exception e ){
            throw new ModuleException( e.getMessage() );
        }
    	
        int count = 0;
		DataStore dataStore = DataStore.instance();
        HashMap typeMap = TypeMap.instance().map();
        /* TODO : The only types of embedded codeare the imported dll's like in the VenusFlytrap
         * Therefore:
         * 1) embedded code that it fully programd, like the Sound concern of Pacman must be ignored in this part of code;
         * 2) embedded code from dll's do need to pass this part of code (Like VenusFlyTrap)
        */
        // loop through all current concerns, fetch implementation and remove from types map.
        Iterator repIt = dataStore.getIterator();
        while( repIt.hasNext() ) {
        	Object next = repIt.next();
        	if( next instanceof CpsConcern ) {
        		CpsConcern concern = (CpsConcern)next;
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
					throw new ModuleException( "CollectorRunner: Can only handle concerns with source file implementations or direct class links." );
				}
        		
        		// transform source name into assembly name blaat.java --> blaat.dll
        		if( !typeMap.containsKey( className ) ) {
        			throw new ModuleException( "Implementation: " + className + " for concern: " + concern.getName() + " not found!" );
        			
        		}
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
		}
              
        INCRETimer copytypes = incre.getReporter().openProcess("COLLECTOR","Copying skipped types..",INCRETimer.TYPE_INCREMENTAL);
		// add skipped concerns in case collector was incremental
		if(incre.isModuleInc("COLLECTOR"))
		{
			// copy types from dll's skipped by Harvester
			this.copyOperation((Vector)resources.getResource("unmodifiedDLLs"));
		}
		copytypes.stop();
		
	}

    public void copyOperation(Vector dlls)
	{
		TypeMap map = TypeMap.instance();
	
		for(int i=0;i<dlls.size();i++)
		{
			Iterator objects = incre.history.getAllInstancesOf(PrimitiveConcern.class);
			String dllName = (String)dlls.elementAt(i);
			
			while(objects.hasNext()){
				
                PrimitiveConcern pc = (PrimitiveConcern)objects.next();
				DotNETType type = (DotNETType)pc.getPlatformRepresentation();
							
				//System.out.println(dllName + "VERSUS "+type.fromDLL);
				if(type.fromDLL.equals(dllName))
				{
					// make a clone and add to datastore
					PrimitiveConcern pcclone = (PrimitiveConcern)pc.clone();
					type.setParentConcern(pcclone);
					DataStore.instance().addObject( type.fullName(), pcclone );
					
					// IMPORTANT: also add the type to the type map
					type.reset(); // reset hashsets of type
					map.addType( type.fullName() , type );
				}
			}
		}
	}
}
