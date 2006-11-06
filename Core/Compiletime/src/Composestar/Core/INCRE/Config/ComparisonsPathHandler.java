package Composestar.Core.INCRE.Config;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

import Composestar.Core.INCRE.*;

import java.util.ArrayList;
import java.util.Iterator;

public class ComparisonsPathHandler extends DefaultHandler
{
	private ConfigManager configmanager;

	private Module module;

	private String fullname = "";

	private TypeHandler returnhandler;

	private ArrayList nodes;

	public ComparisonsPathHandler(ConfigManager cfg, Module module, String fullname, TypeHandler returnhandler)
	{
		this.configmanager = cfg;
		this.module = module;
		this.fullname = fullname;
		this.returnhandler = returnhandler;
		this.nodes = new ArrayList();
	}

	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException
	{
		if (local_name.equalsIgnoreCase("method"))
		{
			String methodname = amap.getValue("name");
			MethodNode method = new MethodNode(methodname);
			this.nodes.add(method);
		}
		else if (local_name.equalsIgnoreCase("field"))
		{
			String fieldname = amap.getValue("name");
			FieldNode field = new FieldNode(fieldname);
			this.nodes.add(field);
		}
	}

	public void endElement(String uri, String local_name, String raw_name)
	{
		// next type between <comparisons> tags
		if (local_name.equalsIgnoreCase("path"))
		{
			Path path = new Path();
			Iterator nodes = this.nodes.iterator();

			while (nodes.hasNext())
			{
				path.addNode((Node) nodes.next());
			}

			this.module.addComparableObject(this.fullname, path);
			this.nodes.clear();

			// look further between <type> tags
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
