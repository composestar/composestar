package Composestar.Core.FILTH.XMLSpecification;
/*
 * Created on 15-mrt-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author nagyist
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class ArgumentFilter extends XMLFilterImpl{
	protected String _left;
	protected String _right;

	private boolean sleft;
	private boolean sright;

	public ArgumentFilter(){}
	public ArgumentFilter(XMLReader parent){  super(parent); }
	
	public String getLeft(){ return _left; }
	public String getRight(){ return _right; }
	
	public void 
	startElement (String uri, String localName, String qName,
	  Attributes atts)
	throws SAXException{
//	   System.out.println(localName);

	  if ("left".equals(localName)){
		sleft=true;sright=false;
	  }
	  if ("right".equals(localName)){
		sleft=false;sright=true;
	  }

	  super.startElement(uri, localName, qName, atts);
	}


	/**
	 * Filter the Namespace URI for end-element events.
	 */
	public void
	endElement (String uri, String localName, String qName)
	throws SAXException{
	  sleft=false;sright=false;
	  super.endElement(uri, localName, qName);
	}

	public void characters(char buf[], int offset, int len)
	throws SAXException
	{
		String s = new String(buf, offset, len);
//		System.out.println("CHAR "+s+" "+sleft);
		if (sleft && !sright)
		{
			_left=s;
		}
		if (sright && !sleft)
		{
			_right=s;
		}
		
	}
	
}
