//Source file: C:\\local\\staijen\\composestar\\src\\Composestar\\core\\ACTING\\AttributeCollector.java

package Composestar.C.TYM;

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

import Composestar.C.LAMA.CAnnotation;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.Annotation;
import Composestar.Core.LAMA.FieldInfo;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.Resources.CommonResources;

public class AttributeCollector extends DefaultHandler
{

	/**
	 * @roseuid 40AB496E00C2
	 */
	public AttributeCollector()
	{}

	/**
	 * @param resources
	 * @throws Composestar.core.Exception.ModuleException
	 * @roseuid 40AB48FB0299
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		File file = new File(resources.configuration().getProject().getBase(), "attributes.xml");
		try
		{
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLReader parser = saxParser.getXMLReader();
			parser.setContentHandler(this);
			parser.parse(new InputSource(new FileInputStream(file)));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Null pointer hier");
			// throw new ModuleException( e.getMessage() );
		}

	}

	/**
	 * @param uri
	 * @param localName
	 * @param qName
	 * @param attr
	 * @throws org.xml.sax.SAXException
	 * @roseuid 40AB539D030D
	 */
	public void startElement(String uri, String localName, String qName, Attributes attr) throws SAXException
	{
		if ("Attribute".equals(qName) && attr != null)
		{
			Annotation attribute = new CAnnotation();
			String annotype = attr.getValue("type");
			Concern c = (Concern) DataStore.instance().getObjectByID(annotype);
			if (c == null)
			{
				// c=createType(annotype);
			}
			if (c != null && c.getPlatformRepresentation() != null)
			{
				Type annotType = (Type) c.getPlatformRepresentation();

				String target = attr.getValue("target").toLowerCase();
				String location = attr.getValue("location");
				/**
				 * if( "parameter".equals(target)) {
				 * attribute.register(annotType, getTypeLocation(location)); }
				 * else if( "method".equals(target) ) {
				 * attribute.register(annotType, getMethodLocation(location)); }
				 * else if ( "field".equals(target) ) {
				 * attribute.register(annotType, getFieldLocation(location)); }
				 * attribute.setValue(attr.getValue("value"));
				 */
				// attribute.setClassName(attr.getValue("Class"));
				// attribute.setMethodSignature(attr.getValue("Method"));
				// attribute.setTypeId(attr.getValue("TypeId"));
			}
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException
	{}

	/***************************************************************************
	 * Creates a concern of the type of the annotation
	 * 
	 * @param type
	 */
	/***************************************************************************
	 * public PrimitiveConcern createType(String type){ CFile typeOfAnnotation =
	 * new CFile(); DataStore dataStore = DataStore.instance(); PrimitiveConcern
	 * pcFile = new PrimitiveConcern(); typeOfAnnotation.setName(type);
	 * typeOfAnnotation.setFullName("Composestar."+ type);
	 * typeOfAnnotation.setAnnotation(true); pcFile.setName(
	 * typeOfAnnotation.name() );
	 * pcFile.setPlatformRepresentation(typeOfAnnotation);
	 * typeOfAnnotation.setParentConcern(pcFile); dataStore.addObject(
	 * typeOfAnnotation.name(), pcFile ); return pcFile; }
	 **************************************************************************/

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
		String methodName = location.substring(location.lastIndexOf(".") + 1);
		String typeName = location.substring(0, location.lastIndexOf("."));

		Type type = getTypeLocation(typeName);

		if (type != null)
		{
			List methods = type.getMethods();
			Iterator i = methods.iterator();
			for (Object method1 : methods)
			{
				MethodInfo method = (MethodInfo) method1;
				if (method.Name.equals(methodName))
				{
					return method;
				}
			}
		}
		return null;
	}

	public FieldInfo getFieldLocation(String location)
	{
		String fieldName = location.substring(location.lastIndexOf(".") + 1);
		String typeName = location.substring(0, location.lastIndexOf("."));

		Type type = getTypeLocation(typeName);

		if (type != null)
		{
			List fields = type.getFields();
			Iterator i = fields.iterator();
			for (Object field1 : fields)
			{
				FieldInfo field = (FieldInfo) field1;
				if (field.name.equals(fieldName))
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
