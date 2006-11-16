package Composestar.Core.Master.Config.XmlHandlers;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Project;
import Composestar.Utils.Debug;

public class ProjectHandler extends DefaultHandler implements ContentHandler
{
	XMLReader parser;

	ProjectsHandler returnHandler;

	Project project;

	public ProjectHandler(XMLReader inParser, ProjectsHandler documentHandler)
	{
		parser = inParser;
		returnHandler = documentHandler;
	}

	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException
	{
		if ("Project".equals(raw_name))
		{// in <Project>
			project = new Project();
			for (int i = 0; i < amap.getLength(); i++)
			{
				String key = amap.getQName(i);
				String val = amap.getValue(i);

				if ("name".equals(key))
				{
					project.setName(val);
				}
				else if ("language".equals(key))
				{
					project.setLanguageName(val);
				}
				else if ("basePath".equals(key))
				{
					project.setBasePath(val);
				}
				else
				{
					Debug.out(Debug.MODE_WARNING, "MASTER", "Unknown attribute " + key + " in Project");
					// project.addProperty(key, val);
				}
			}

			Configuration.instance().getProjects().addProject(project);
		}

		if ("Sources".equals(raw_name))
		{// in <Sources>
			// look further
			ProjectSourcesHandler sourceshandler = new ProjectSourcesHandler(project, parser, this);
			parser.setContentHandler(sourceshandler);
		}

		if ("Dependencies".equals(raw_name))
		{// in <Dependencies>
			// look further
			ProjectDependenciesHandler dependencyhandler = new ProjectDependenciesHandler(project, parser, this);
			parser.setContentHandler(dependencyhandler);
		}

	}

	public void endElement(String uri, String local_name, String raw_name) throws SAXException
	{
		if ("Project".equals(raw_name))
		{
			// end <Project>
			parser.setContentHandler(returnHandler);
		}
	}

	public void startDocument()
	{}

	public void endDocument()
	{}
}
