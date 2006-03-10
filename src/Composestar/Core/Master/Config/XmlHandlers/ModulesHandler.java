package Composestar.Core.Master.Config.XmlHandlers;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Module;

public class ModulesHandler extends DefaultHandler implements ContentHandler
{
	XMLReader parser;
	SettingsHandler returnHandler;
	
	public ModulesHandler(XMLReader parser,SettingsHandler returnHandler){
		this.parser = parser;
		this.returnHandler = returnHandler;
	} 
	
	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException 
	{
		if("Module".equals(raw_name))
		{// in <module>
			// look further	
			if(amap.getValue("name")!=null)
			{
				String name = amap.getValue("name");
				Module m = new Module();
				m.setName(name);
				for(int i=0;i<amap.getLength();i++){
					String key = amap.getQName(i);
					String val = amap.getValue(key);
					m.addProperty(key,val);
				}
					
				Configuration.instance().moduleSettings.addModule(name,m);
			}
		}	
	}

	public void endElement(String uri, String local_name, String raw_name) throws SAXException 
	{
		if("Modules".equals(raw_name)){
			// end <modules>
			parser.setContentHandler( returnHandler );
		}
	}

	public void startDocument() 
	{
 
	}

	public void endDocument() 
	{
			
	}
}
