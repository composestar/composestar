package Composestar.Core.INCRE.Config;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.INCRE.FieldNode;
import Composestar.Core.INCRE.INCREModule;
import Composestar.Core.INCRE.MethodNode;
import Composestar.Core.INCRE.Node;
import Composestar.Core.INCRE.Path;

public class ComparisonsPathHandler extends DefaultHandler
{
	private XMLReader reader;

	private INCREModule module;

	private String fullname = "";

	private TypeHandler returnhandler;

	private List<Node> nodes;

	public ComparisonsPathHandler(XMLReader inReader, INCREModule inModule, String inFullname,
			TypeHandler inReturnhandler)
	{
		reader = inReader;
		module = inModule;
		fullname = inFullname;
		returnhandler = inReturnhandler;
		nodes = new ArrayList<Node>();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes amap) throws SAXException
	{
		if (qName.equalsIgnoreCase("method"))
		{
			String methodname = amap.getValue("name");
			MethodNode method = new MethodNode(methodname);
			nodes.add(method);
		}
		else if (qName.equalsIgnoreCase("field"))
		{
			String fieldname = amap.getValue("name");
			FieldNode field = new FieldNode(fieldname);
			nodes.add(field);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
	{
		// next type between <comparisons> tags
		if (qName.equalsIgnoreCase("path"))
		{
			Path path = new Path();

			for (Node node : nodes)
			{
				path.addNode(node);
			}

			module.addComparableObject(fullname, path);
			nodes.clear();

			// look further between <type> tags
			reader.setContentHandler(returnhandler);
		}
	}
}
