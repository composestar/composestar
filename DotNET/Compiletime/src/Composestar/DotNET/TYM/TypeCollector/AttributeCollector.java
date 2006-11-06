package Composestar.DotNET.TYM.TypeCollector;

import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.Annotation;
import Composestar.Core.LAMA.FieldInfo;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.Project;
import Composestar.Core.Master.Config.Projects;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;

public class AttributeCollector extends DefaultHandler implements CTCommonModule
{
	public static final String MODULE_NAME = "TAC";

	public AttributeCollector()
	{}

	public void run(CommonResources resources) throws ModuleException
	{
		Projects prjs = Configuration.instance().getProjects();
		List projectList = prjs.getProjects();
		Iterator prjIt = projectList.iterator();
		while (prjIt.hasNext())
		{
			Project p = (Project) prjIt.next();
			String projectFolder = p.getBasePath();
			String xmlFile = projectFolder + "attributes.xml";

			if (FileUtils.fileExist(xmlFile))
			{
				try
				{
					SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
					SAXParser saxParser = saxParserFactory.newSAXParser();
					XMLReader parser = saxParser.getXMLReader();
					parser.setContentHandler(this);
					parser.parse(new InputSource(xmlFile));
				}
				catch (Exception e)
				{
					throw new ModuleException("Unable to collect attributes: " + e.getMessage(), MODULE_NAME);
				}
			}
			else Debug.out(Debug.MODE_WARNING, MODULE_NAME, "Attribute file not found: " + xmlFile);
		}
	}

	public void startElement(String uri, String localName, String qName, Attributes attr) throws SAXException
	{
		if ("Attribute".equalsIgnoreCase(qName) && attr != null)
		{
			Annotation anno = new Annotation();
			Concern c = (Concern) DataStore.instance().getObjectByID(attr.getValue("type"));
			if (c != null && c.getPlatformRepresentation() != null)
			{
				Type annoType = (Type) c.getPlatformRepresentation();

				String target = attr.getValue("target").toLowerCase();
				String location = attr.getValue("location");

				if ("type".equals(target))
				{
					anno.register(annoType, getTypeLocation(location));
				}
				else if ("method".equals(target))
				{
					anno.register(annoType, getMethodLocation(location));
				}
				else if ("field".equals(target))
				{
					anno.register(annoType, getFieldLocation(location));
				}

				anno.setValue(attr.getValue("value"));
			}
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException
	{}

	public Type getTypeLocation(String location)
	{
		Concern c = (Concern) DataStore.instance().getObjectByID(location);
		if (c != null && c.getPlatformRepresentation() instanceof Type)
		{
			return (Type) c.getPlatformRepresentation();
		}
		return null;
	}

	public MethodInfo getMethodLocation(String location)
	{
		int dot = location.lastIndexOf(".");
		String methodName = location.substring(dot + 1);
		String typeName = location.substring(0, dot);

		Type type = getTypeLocation(typeName);
		if (type != null)
		{
			List methods = type.getMethods();
			Iterator it = methods.iterator();
			while (it.hasNext())
			{
				MethodInfo method = (MethodInfo) it.next();
				if (method.name().equals(methodName)) return method;
			}
		}
		return null;
	}

	public FieldInfo getFieldLocation(String location)
	{
		int dot = location.lastIndexOf(".");
		String fieldName = location.substring(dot + 1);
		String typeName = location.substring(0, dot);

		Type type = getTypeLocation(typeName);
		if (type != null)
		{
			List fields = type.getFields();
			Iterator it = fields.iterator();
			while (it.hasNext())
			{
				FieldInfo field = (FieldInfo) it.next();
				if (field.name().equals(fieldName)) return field;
			}
		}
		return null;
	}

	public ParameterInfo getParameterLocation(String location)
	{
		return null;
	}
}
