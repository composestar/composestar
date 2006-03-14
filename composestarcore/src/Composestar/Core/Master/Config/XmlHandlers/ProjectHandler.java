package Composestar.Core.Master.Config.XmlHandlers;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Project;

public class ProjectHandler extends DefaultHandler implements ContentHandler
{
	XMLReader parser;
	ProjectConfigurationHandler returnHandler;
	Project project;
	
	public ProjectHandler(XMLReader parser, ProjectConfigurationHandler documentHandler)
	{
		this.parser = parser;
		this.returnHandler = documentHandler;
	} 
	
	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException 
	{
		if("Project".equals(raw_name))
		{// in <Project>
			//System.out.println("<project>");
			project = new Project();
			
			for(int i=0;i<amap.getLength();i++){
				String key = amap.getQName(i);
				String val = amap.getValue(key);
				project.addProperty(key,val);
			}
			
			Configuration.instance().getProjects().addProject(project);
		}
		
		if("Sources".equals(raw_name))
		{// in <Sources>	
			//System.out.println("<sources>");	
			//look further
			ProjectSourcesHandler sourceshandler = new ProjectSourcesHandler(project,parser,this);
			parser.setContentHandler( sourceshandler );
		}
		
		if("Dependencies".equals(raw_name))
		{// in <Dependencies>	
			//System.out.println("<dependencies>");	
			//look further
			ProjectDependenciesHandler dependencyhandler = new ProjectDependenciesHandler(project,parser,this);
			parser.setContentHandler( dependencyhandler );
		}
		
		if("TypeSources".equals(raw_name))
		{// in <Type>	
			//System.out.println("<typesources>");	
			//look further
			ProjectTypeSourcesHandler typesourceshandler = new ProjectTypeSourcesHandler(project,parser,this);
			parser.setContentHandler( typesourceshandler );
		}
	}

	public void endElement(String uri, String local_name, String raw_name) throws SAXException 
	{
		if("Project".equals(raw_name))
		{
			// end <Project>
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
