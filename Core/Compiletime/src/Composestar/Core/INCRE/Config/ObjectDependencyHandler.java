package Composestar.Core.INCRE.Config;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.INCRE.DynamicNode;
import Composestar.Core.INCRE.FieldNode;
import Composestar.Core.INCRE.ObjectDependency;

public class ObjectDependencyHandler extends DefaultHandler
{
	private ConfigManager configmanager;

	private ObjectDependency objdep;

	private DependencyHandler returnhandler;

	public ObjectDependencyHandler(ConfigManager cfg, ObjectDependency objdep, DependencyHandler returnhandler)
	{
		this.configmanager = cfg;
		this.objdep = objdep;
		this.returnhandler = returnhandler;
	}

	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException
	{
		if (local_name.equalsIgnoreCase("node"))
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

	public void endElement(String uri, String local_name, String raw_name)
	{
		if (local_name.equalsIgnoreCase("path"))
		{
			// go to next dependency
			configmanager.getXMLReader().setContentHandler(returnhandler);
		}
	}

	public void startDocument()
	{

	}

	public void endDocument()
	{

	}
}
