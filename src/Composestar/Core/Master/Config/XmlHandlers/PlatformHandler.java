package Composestar.Core.Master.Config.XmlHandlers;

import java.util.Iterator;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Language;
import Composestar.Core.Master.Config.Project;

public class PlatformHandler extends DefaultHandler implements ContentHandler
{
	XMLReader parser;
	BuildConfigHandler returnHandler;
	
	public PlatformHandler(XMLReader parser,BuildConfigHandler returnHandler){
		this.parser = parser;
		this.returnHandler = returnHandler;
	}
	
	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException 
	{
		if("Language".equals(raw_name))
		{// in <language>
			// look further	
			if(amap.getValue("name")!=null)
			{
				String languagename = amap.getValue("name");
				Configuration config = Configuration.instance();
				if(config.getProjects().getProjectsByLanguage(languagename)!=null){
					// look further
					Language lang = new Language();
					lang.setName(languagename);
					LanguageHandler langhandler = new LanguageHandler(lang,parser,this);
					parser.setContentHandler( langhandler );
					
					// add language to projects
					List projects = config.getProjects().getProjectsByLanguage(languagename);
					Iterator prjIter = projects.iterator();
					while(prjIter.hasNext()){
						//System.out.println("add lang "+lang.getName());
						Project p = (Project)prjIter.next();
						p.setLanguage(lang);
					}
				}
				else {
					// next language
				}
			}
		}	
		else if("RequiredFiles".equals(raw_name))
		{
			// in <RequiredFiles>
			// look further	
			RequiredFilesHandler fileshandler = new RequiredFilesHandler(parser,this);
			parser.setContentHandler( fileshandler );
		}
	}

	public void endElement(String uri, String local_name, String raw_name) throws SAXException 
	{
		if("Platform".equals(raw_name)){
			// end <platform>
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
