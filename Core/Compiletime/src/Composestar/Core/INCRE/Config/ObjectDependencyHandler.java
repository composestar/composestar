package Composestar.Core.INCRE.Config;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.INCRE.DynamicNode;
import Composestar.Core.INCRE.FieldNode;
import Composestar.Core.INCRE.ObjectDependency;

public class ObjectDependencyHandler extends DefaultHandler
{
	private XMLReader reader;

	private ObjectDependency objdep;

	private DependencyHandler returnhandler;

	public ObjectDependencyHandler(XMLReader inReader, ObjectDependency inObjdep, DependencyHandler inReturnhandler)
	{
		reader = inReader;
		objdep = inObjdep;
		returnhandler = inReturnhandler;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes amap) throws SAXException
	{
		if (qName.equalsIgnoreCase("node"))
		{
			// process <node>
			String nodetype = amap.getValue("type");
			String objectref = amap.getValue("nodevalue");

			if (nodetype.equals("DYNAMIC"))
			{
				// create 'dynamic' node
				DynamicNode dynamicnode = new DynamicNode(objectref);
				objdep.addNode(dynamicnode);
			}
			else if (nodetype.equals("FIELD"))
			{
				// create 'field' node
				FieldNode fieldnode = new FieldNode(objectref);
				objdep.addNode(fieldnode);
			}
			else
			{
				// throw something
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
	{
		if (qName.equalsIgnoreCase("path"))
		{
			// go to next dependency
			reader.setContentHandler(returnhandler);
		}
	}
}
