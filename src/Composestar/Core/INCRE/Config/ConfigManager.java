package Composestar.Core.INCRE.Config;

import Composestar.Utils.*;
import Composestar.Core.INCRE.Module;
import Composestar.Core.Master.CommonResources;

import java.util.LinkedHashMap;

import org.xml.sax.XMLReader;

/**
 * Class responsible for parsing configuration files created for the incremental 
 * aspect.
 */
public class ConfigManager 
{
   private XMLReader xmlreader = null;
   private INCREXMLParser xmlparser = null; 
   private CommonResources resources = null;
   
   /**
    * HashMap containing modules. The keys are the names of the modules. The values 
    * are the module objects.
    */
   public LinkedHashMap modules = new LinkedHashMap();
        
   /**
    * @roseuid 420A175602EE
    */
   public ConfigManager(CommonResources resources) 
   {
		this.resources = resources;
		this.xmlparser = new INCREXMLParser(this);
   }
   
   /**
    * @roseuid 420A1A120399
    */
   public void parseXML(String filename) throws java.io.IOException, org.xml.sax.SAXException 
   {
		   xmlreader = org.xml.sax.helpers.XMLReaderFactory.createXMLReader();
		   xmlreader.setContentHandler(this.xmlparser);
		   Debug.out(Debug.MODE_DEBUG, "INCRE","Parsing configuration file...");
		   xmlreader.parse(filename);
   }

	public XMLReader getXMLReader()
	{
		return this.xmlreader;		
	}
   
   /**
    * @roseuid 420A1A5E00BB
    */
   public void addModule(String id,Module m) 
   {
	   this.modules.put(id,m);
   }
   
   /**
    * @roseuid 420A1A64007D
    */
	public Module getModuleByID(String id) 
	{
		return (Module)modules.get(id);
	}

	public LinkedHashMap getModules()
	{
		return this.modules;
	}
	
	public CommonResources getResources()
	{
		return resources;
	}
}
