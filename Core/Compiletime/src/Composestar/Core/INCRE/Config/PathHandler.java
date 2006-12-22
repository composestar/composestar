package Composestar.Core.INCRE.Config;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.INCRE.ConfigNode;
import Composestar.Core.INCRE.Dependency;
import Composestar.Core.INCRE.DynamicNode;
import Composestar.Core.INCRE.FieldNode;
import Composestar.Core.INCRE.MethodNode;

public class PathHandler extends DefaultHandler
{
	private XMLReader reader;

	private Dependency dep;

	private DependencyHandler returnhandler;

	public PathHandler(XMLReader inReader, Dependency inDep, DependencyHandler inReturnhandler)
	{
		reader = inReader;
		dep = inDep;
		returnhandler = inReturnhandler;
	}

	public void startElement(String uri, String localName, String qName, Attributes amap) throws SAXException
	{
		if (qName.equalsIgnoreCase("node"))
		{
			// process <node>
			String nodetype = amap.getValue("type");
			String ref = amap.getValue("nodevalue");

			if (nodetype.equals("DYNAMIC"))
			{
				// create 'dynamic' node
				DynamicNode dynamicnode = new DynamicNode(ref);
				dep.addNode(dynamicnode);
			}
			else if (nodetype.equals("FIELD"))
			{
				// create 'field' node
				FieldNode fieldnode = new FieldNode(ref);
				dep.addNode(fieldnode);
			}
			else if (nodetype.equals("METHOD"))
			{
				// create 'method' node
				MethodNode methodnode = new MethodNode(ref);
				dep.addNode(methodnode);
			}
			else if (nodetype.equals("CONFIG"))
			{
				// create 'config' node
				ConfigNode confignode = new ConfigNode(ref);
				dep.addNode(confignode);
			}
			else
			{
				// throw something
			}
		}
	}

	public void endElement(String uri, String localName, String qName)
	{
		if (qName.equalsIgnoreCase("path"))
		{
			if (returnhandler != null)
			{
				reader.setContentHandler(returnhandler);
			}
		}
	}
}
