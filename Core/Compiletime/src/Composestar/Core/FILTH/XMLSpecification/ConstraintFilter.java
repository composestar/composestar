/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.FILTH.XMLSpecification;

/**
 * @author nagyist
 */
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import Composestar.Core.FILTH.FILTHService;
import Composestar.Core.FILTH.Core.Action;
import Composestar.Core.FILTH.Core.Graph;
import Composestar.Core.FILTH.Core.Node;
import Composestar.Core.FILTH.Core.Rule;
import Composestar.Core.FILTH.Core.SoftPreRule;

public class ConstraintFilter extends ArgumentFilter
{
	// private ArgumentFilter _af;
	private String value;

	private Graph graph;

	// public OrderFilter(ArgumentFilter af){ _af=af; }

	public ConstraintFilter(Graph g)
	{
		graph = g;
	}

	public ConstraintFilter(XMLReader parent)
	{
		super(parent);
	}

	/**
	 * Filter the Namespace URI for start-element events.
	 */

	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
	{
		// System.out.println(localName);

		if ("constraint".equals(localName))
		{
			/* attrbutes for s */
			if (atts != null)
			{
				//String type = atts.getQName(0);
				value = atts.getValue(0);
			}
			// System.out.println("BEGIN ");
		}
		super.startElement(uri, localName, qName, atts);
	}

	/**
	 * Filter the Namespace URI for end-element events.
	 */
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		Action l, r;
		Node nl, nr;

		if ("constraint".equals(localName))
		{
			// System.out.println(value + "END");

			nl = Action.lookupByName(left, graph);

			if (nl != null)
			{
				l = (Action) nl.getElement();
			}
			else
			{
				/*
				 * l=new Action(_left,new Boolean(true),true);
				 * Action.insert(l,_graph); System.out.println("Action "+l+"
				 * added");
				 */
				l = null;
			}

			nr = Action.lookupByName(right, graph);
			if (nr != null)
			{
				r = (Action) nr.getElement();
			}
			else
			{
				/*
				 * r=new Action(_right,new Boolean(true),true);
				 * Action.insert(r,_graph); System.out.println("Action "+r+"
				 * added");
				 */
				r = null;
			}
			// System.out.println("l:"+l+" r:"+r);
			/* we add a rule only if both arguments are active */
			if ((l != null) && (r != null))
			{
				if ("pre_soft".equals(value))
				{
					Rule rule = new SoftPreRule(l, r);
					rule.insert(graph);

					// FILTHService.print("FILTH::adding rule> "+value+"( "+l+"
					// , "+r+" )\n");
					FILTHService.getLog().print("<li><i>" + value + "( " + l + " , " + r + ")</i></li>\n");
				}
			}

			// itt egy action-t letrehozni; elotte ellenorizni, hogy benne van-e
			// a memoriaban; es felszurni a grafba
			// ezek utan letrehozni a szabaly a constraint alapjan es azt is
			// felvinni a grafba
		}
		super.endElement(uri, localName, qName);
	}

}
