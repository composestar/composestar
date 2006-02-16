//Source file: C:\\local\\staijen\\composestar\\src\\Composestar\\core\\ACTING\\AttributeCollector.java

package Composestar.DotNET.TYM.TypeCollector;

import java.util.Iterator;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.LAMA.*;
import Composestar.DotNET.LAMA.*;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.Exception.ModuleException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class AttributeCollector extends DefaultHandler {
        
    /**
     * @roseuid 40AB496E00C2
     */
    public AttributeCollector() {
    }
    
    /**
     * @param resources
     * @throws Composestar.core.Exception.ModuleException
     * @roseuid 40AB48FB0299
     */
    public void run(CommonResources resources) throws ModuleException {
		String tempFolder = resources.ProjectConfiguration.getProperty("TempFolder");
		try {
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLReader parser  = saxParser.getXMLReader();
			parser.setContentHandler( this );
			parser.parse( new InputSource( tempFolder + "attributes.xml" ));
		} catch( Exception e ) {
			e.printStackTrace();
			//throw new ModuleException( e.getMessage() );
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
    public void startElement(String uri, String localName, String qName, Attributes attr) throws SAXException {
     	if( "Attribute".equals(qName) && attr != null ) {
     		Annotation attribute = new DotNETAttribute();
			Concern c = (Concern) DataStore.instance().getObjectByID(attr.getValue("type"));
			if( c != null && c.getPlatformRepresentation() != null )
			{
				Type annotType = (Type) c.getPlatformRepresentation();
				
				String target = attr.getValue("target").toLowerCase();
				String location = attr.getValue("location");
				
				if( "type".equals(target))
				{
					attribute.register(annotType, getTypeLocation(location));
				}
				else if( "method".equals(target) )
				{
					attribute.register(annotType, getMethodLocation(location));
				}
				else if ( "field".equals(target) )
				{
				  attribute.register(annotType, getFieldLocation(location));
				}
				
				attribute.setValue(attr.getValue("value"));

				//attribute.setClassName(attr.getValue("Class"));
				//attribute.setMethodSignature(attr.getValue("Method"));
				//attribute.setTypeId(attr.getValue("TypeId"));
			}
     	}
    }
    
    /**
     * @param uri
     * @param localName
     * @param qName
     * @throws org.xml.sax.SAXException
     * @roseuid 40AB53DC0213
     */
    public void endElement(String uri, String localName, String qName) throws SAXException {
    }
    
	public Type getTypeLocation(String location)
	{
		Concern c = (Concern) DataStore.instance().getObjectByID(location);
		if( c != null && c.getPlatformRepresentation() instanceof Type )
		{
			return (Type) c.getPlatformRepresentation();
		}
		return null;
	}
	
	public MethodInfo getMethodLocation(String location)
	{
		String methodName = location.substring(location.lastIndexOf(".")+1);
		String typeName   = location.substring(0,location.lastIndexOf("."));
		
		Type type = getTypeLocation(typeName);
		
		if( type != null )
		{
			List methods = type.getMethods();
            Iterator i = methods.iterator();
			while(i.hasNext())
			{
				MethodInfo method = (MethodInfo) i.next();
				if( method.Name.equals(methodName))
					return method;
			}
		}
		return null;
	}
	
	public FieldInfo getFieldLocation(String location)
	{
	  String fieldName = location.substring(location.lastIndexOf(".")+1);
	  String typeName  = location.substring(0,location.lastIndexOf("."));
	  
		Type type = getTypeLocation(typeName);
		
		if( type != null )
		{
			List fields  = type.getFields();
            Iterator i = fields.iterator();
			while(i.hasNext())
			{
				FieldInfo field = (FieldInfo) i.next();
				if( field.Name.equals(fieldName))
					return field;
			}
		}
		return null;
	}
	
	public ParameterInfo getParameterLocation(String location)
	{
		return null;
	}

}
