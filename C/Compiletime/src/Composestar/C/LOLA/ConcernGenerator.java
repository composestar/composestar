/*
 * Created on 17-sep-2006
 *
 */
package Composestar.C.LOLA;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Exception.ModuleException;
import Composestar.C.LAMA.CMethodInfo;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SimpleSelectorDef.PredicateSelector;
import Composestar.Core.CpsProgramRepository.*;
import Composestar.Utils.Debug;
import Composestar.Core.RepositoryImplementation.DataStore;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.Set;


/**
 * @author johan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ConcernGenerator extends DefaultHandler{
	
	private	HashMap concerns= new HashMap();
	
	 public void run()throws ModuleException{
		try {
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLReader parser  = saxParser.getXMLReader();
			parser.setContentHandler( this );
			parser.parse( new InputSource(Configuration.instance().getPathSettings().getPath("Base")+"CConcern.xml"));
		} 
		catch( Exception e ) {
			e.printStackTrace();
		}
    }
    
    public void startElement(String uri, String localName, String qName, Attributes attr) throws SAXException {
     	if( "module".equals(qName) && attr!=null) {
     		String query = attr.getValue("query");
     		String outputVar= query.substring(0,query.indexOf("|"));
     		query=query.substring(query.indexOf('|')+1,query.length());
     		//System.out.println("test: "+query + outputVar);
     		PredicateSelector ps = new PredicateSelector(outputVar, query);
     		concerns.put(attr.getValue("name"),ps);
     	}
    }
    
    public HashMap getConcerns(){
    	return concerns;
    }
   
    /****************************************
     * After this function HashMap concerns 
     * has key's of the modulenames specified in the CConcern.xml
     * and the Values are PredicateSelectors, where the unitresults are
     * evaluated and therefore known
     * 
     * @throws ModuleException
     */
    public void evaluateConcerns() throws ModuleException{
    	Vector results = new Vector();
    	HashMap resultsMap = new HashMap();
    	Iterator selectorIter = concerns.keySet().iterator();
    	while(selectorIter.hasNext())
      	{
      		String key =(String)selectorIter.next();
    		PredicateSelector selector = (PredicateSelector) concerns.get(key); //selectorIter.next();
    		selector.run();
      		resultsMap.put(key,selector.getSelectedUnits());
    	}
    	concerns.clear();
    	concerns.putAll(resultsMap);
     }
    
    /*************************
     * From the hashmap concerns, real concerns have to be made.
     * The key should be the concern name
     * From the selector its result, all program elements that are the result
     * can be evaluated which functions it contains
     * These functions should be added to the Signature of the concerns
     * TODO: Check if it works that an empty file is created,
     * with only methods in it, I thaugt that this should work,
     * because in the filtermodule only the signature is checked
     * **/
    				
    public void createConcerns(){
    	Iterator resultsEnum=concerns.keySet().iterator();
    	while(resultsEnum.hasNext())
    	{
    		String key =(String)resultsEnum.next();
    		Set element=(Set)concerns.get(key);
    		PrimitiveConcern pconcern = (PrimitiveConcern)DataStore.instance().getObjectByID(key);
    		DataStore.instance().removeObject(pconcern);
    		if(pconcern !=null && element != null){
    			Debug.out(Debug.MODE_INFORMATION,"CLOLA","Concern: " + key + " has methods: ");
    			Signature sig = new Signature();
    			Iterator methods=element.iterator();
    			while(methods.hasNext()){
    				Object method= methods.next();
    				if(method instanceof CMethodInfo)
    				{
	    				CMethodInfo methodInfo =(CMethodInfo)method;
	    				Debug.out(Debug.MODE_INFORMATION,"CLOLA","Method: "+methodInfo.Name);
	    				sig.add(methodInfo,MethodWrapper.ADDED);
	    		    	//By doing addMethod, I'm overwriting the original parent
	    				//that's probably not good, because then the filename is not retrievable annymore.
	    				//cf.addMethod(methodInfo);
	    			}
	    			else Debug.out(Debug.MODE_ERROR,"CLOLA","Result of module " +key+ " is not a set of functions");
	    		}
	    		pconcern.setSignature(sig);
	    		/** overwrite concern in datastore, with added signature**/
	    		DataStore.instance().addObject( pconcern.getName(), pconcern );
	    		}
    	}
       }
    
}
