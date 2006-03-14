package Composestar.Core.Master.Config.XmlHandlers;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.CustomFilter;

public class CustomFiltersHandler extends DefaultHandler implements ContentHandler
{
	XMLReader parser;
	ProjectConfigurationHandler returnHandler;
	
	public CustomFiltersHandler(XMLReader parser, ProjectConfigurationHandler returnHandler)
	{
		this.parser = parser;
		this.returnHandler = returnHandler;
	} 
	
	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException 
	{
		if("Filter".equals(raw_name))
		{// in <path>
			// look further	
			if(amap.getValue("filtername")!=null && amap.getValue("library")!=null )
			{
				String name = amap.getValue("filterName");
				String path = amap.getValue("library");
				CustomFilter cf = new CustomFilter();
				cf.setFilter(name);
				cf.setLibrary(path);
				Configuration.instance().getFilters().addCustomFilters(cf);
			}
		}			
	}

	public void endElement(String uri, String local_name, String raw_name) throws SAXException 
	{
		if("CustomFilters".equals(raw_name)){
			// end <paths>
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
