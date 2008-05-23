package Composestar.DotNET.TYM.TypeCollector;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Annotations.ComposestarModule;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.Annotation;
import Composestar.Core.LAMA.FieldInfo;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Logging.CPSLogger;

@ComposestarModule(ID = AttributeCollector.MODULE_NAME, dependsOn = { ModuleNames.HARVESTER })
public class AttributeCollector extends DefaultHandler implements CTCommonModule
{
	public static final String MODULE_NAME = "AttributeCollector";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	protected DataStore ds;

	public AttributeCollector()
	{}

	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		ds = resources.repository();
		File xmlFile = new File(resources.configuration().getProject().getIntermediate(), "attributes.xml");

		if (xmlFile.exists())
		{
			try
			{
				SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
				SAXParser saxParser = saxParserFactory.newSAXParser();
				XMLReader parser = saxParser.getXMLReader();
				parser.setContentHandler(this);
				parser.parse(new InputSource(new FileInputStream(xmlFile)));
			}
			catch (Exception e)
			{
				throw new ModuleException("Unable to collect attributes: " + e.getMessage(), MODULE_NAME);
			}
		}
		else
		{
			logger.warn("Attribute file not found: " + xmlFile);
		}
		return ModuleReturnValue.Ok;
	}

	public void startElement(String uri, String localName, String qName, Attributes attr) throws SAXException
	{
		if ("Attribute".equalsIgnoreCase(qName) && attr != null)
		{
			Annotation anno = new Annotation();
			Concern c = (Concern) ds.getObjectByID(attr.getValue("type"));
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

				// attr.getValue("value");
			}
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException
	{}

	public Type getTypeLocation(String location)
	{
		Concern c = (Concern) ds.getObjectByID(location);
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
				if (method.getName().equals(methodName))
				{
					return method;
				}
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
				if (field.getName().equals(fieldName))
				{
					return field;
				}
			}
		}
		return null;
	}

	public ParameterInfo getParameterLocation(String location)
	{
		return null;
	}
}
